package wesicknessdect.example.org.wesicknessdetect.futuretasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.appizona.yehiahd.fastsave.FastSave;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wesicknessdect.example.org.wesicknessdetect.database.AppDatabase;
import wesicknessdect.example.org.wesicknessdetect.events.HideLoadingEvent;
import wesicknessdect.example.org.wesicknessdetect.events.ShowLoadingEvent;
import wesicknessdect.example.org.wesicknessdetect.events.ShowProcessScreenEvent;
import wesicknessdect.example.org.wesicknessdetect.events.UserAuthenticatedEvent;
import wesicknessdect.example.org.wesicknessdetect.models.Country;
import wesicknessdect.example.org.wesicknessdetect.models.Credential;
import wesicknessdect.example.org.wesicknessdetect.models.Culture;
import wesicknessdect.example.org.wesicknessdetect.models.CulturePart;
import wesicknessdect.example.org.wesicknessdetect.models.Diagnostic;
import wesicknessdect.example.org.wesicknessdetect.models.DiagnosticResponse;
import wesicknessdect.example.org.wesicknessdetect.models.Disease;
import wesicknessdect.example.org.wesicknessdetect.models.DiseaseSymptom;
import wesicknessdect.example.org.wesicknessdetect.models.Model;
import wesicknessdect.example.org.wesicknessdetect.models.Picture;
import wesicknessdect.example.org.wesicknessdetect.models.Question;
import wesicknessdect.example.org.wesicknessdetect.models.Struggle;
import wesicknessdect.example.org.wesicknessdetect.models.StruggleResponse;
import wesicknessdect.example.org.wesicknessdetect.models.Symptom;
import wesicknessdect.example.org.wesicknessdetect.models.SymptomRect;
import wesicknessdect.example.org.wesicknessdetect.models.User;
import wesicknessdect.example.org.wesicknessdetect.retrofit.APIClient;
import wesicknessdect.example.org.wesicknessdetect.retrofit.APIService;
import wesicknessdect.example.org.wesicknessdetect.utils.Constants;
import wesicknessdect.example.org.wesicknessdetect.utils.DownloadService;
import wesicknessdect.example.org.wesicknessdetect.utils.EncodeBase64;

public class RemoteTasks {

    private static RemoteTasks remoteTasks;
    //private static APIService service;
    private static AppDatabase DB;
    private static Context mContext;
    static final ExecutorService executor = Executors.newSingleThreadExecutor();
    String result = "";
    List<Culture> cultures = new ArrayList<>();
    Model model = new Model();
    List<CulturePart> cultureParts = new ArrayList<>();
    List<Question> questions = new ArrayList<>();
    List<Disease> diseases = new ArrayList<>();
    List<Struggle> struggles = new ArrayList<>();
    List<Symptom> symptoms = new ArrayList<>();
    List<Country> countries = new ArrayList<>();
    List<Diagnostic> diagnostics = new ArrayList<>();
    List<Picture> pictures = new ArrayList<>();
    Diagnostic diagnostic = new Diagnostic();
    Picture picture = new Picture();
    User user = new User();
    int downloadId = 0;
    long diagnostic_id = 0;
    long profile_id;


    private RemoteTasks(Context context) {
        mContext = context;
    }  //private constructor.

    public static RemoteTasks getInstance(Context context) {
        if (remoteTasks == null) { //if there is no instance available... create new one
            remoteTasks = new RemoteTasks(context);
        }
        mContext = context;
        //service = APIClient.getClient().create(APIService.class);
        DB = AppDatabase.getInstance(mContext);
        return remoteTasks;
    }


    //Get the Countries from Server
    public List<Country> getCountries() {
        if (Constants.isOnline(mContext)) {
            Log.e("Country call started:", "Started Ok!!");
            APIService service = APIClient.getClient().create(APIService.class);
            Call<List<Country>> countryCall = service.getCountries();
            countryCall.enqueue(new Callback<List<Country>>() {
                @Override
                public void onResponse(Call<List<Country>> call, Response<List<Country>> response) {
                    countries = response.body();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (Country c : response.body()) {
                                DB.countryDao().createCountry(c);
                                Log.e("Country:", c.getName());
                            }
                        }
                    }).start();
                    Log.e("Country call Finished:", "Finished Ok!!");
                }

                @Override
                public void onFailure(Call<List<Country>> call, Throwable t) {
                    Log.i("Country getError:", t.getMessage());
                }
            });
        } else {
            //Dispatch show loading event
            EventBus.getDefault().post(new ShowLoadingEvent("Erreur", "Vous n'etes pas connecter a internet", true));
            countries = DB.countryDao().getAll().getValue();
        }
        return countries;
    }

    //Send Signup data
    public User doSignUp(User u) throws InterruptedException, ExecutionException {
        if (Constants.isOnline(mContext)) {
            //Dispatch show loading event
            EventBus.getDefault().post(new ShowLoadingEvent("Please wait", "processing...", false));

            Thread.sleep(1000);
            FutureTask<User> future = new FutureTask<>(new Callable<User>() {
                @SuppressLint("StaticFieldLeak")
                @Override
                public User call() throws Exception {
                    APIService service = APIClient.getClient().create(APIService.class);
                    Call<User> SignupCall = service.doSignup(u);
                    Response<User> response = SignupCall.execute();
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            user = response.body();
                            result = response.body().toString();
                            User user = response.body();
                            new AsyncTask<Void, Void, Void>() {
                                @Override
                                protected Void doInBackground(Void... voids) {
                                    int profile_id = (int) DB.profileDao().createProfile(user.getProfile());
                                    user.setProfile_id(profile_id);
                                    DB.userDao().createUser(user);
                                    FastSave.getInstance().saveString("token", response.body().getToken());
                                    EventBus.getDefault().post(new UserAuthenticatedEvent(FastSave.getInstance().getString("token", null)));
                                    return null;
                                }
                            }.execute();
                        }
                    } else {
                        EventBus.getDefault().post(new HideLoadingEvent("Dissmissed"));
                        UserResponseErrorProcess(response);
                    }
                    return user;
                }
            });
            executor.execute(future);
            return future.get();
        } else {
            //Dispatch show loading event
            EventBus.getDefault().post(new ShowLoadingEvent("Erreur", "Vous n'etes pas connecter a internet", true));
            return new User();
        }


    }

    //Send Login credentials
    public String doLogin(Credential c) throws InterruptedException, ExecutionException {
        if (Constants.isOnline(mContext)) {
            EventBus.getDefault().post(new ShowLoadingEvent("Please wait", "processing...", false));
            FutureTask<String> future = new FutureTask<>(new Callable<String>() {
                @SuppressLint("StaticFieldLeak")
                @Override
                public String call() throws Exception {
                    APIService service = APIClient.getClient().create(APIService.class);
                    Call<User> loginCall = service.doLogin(c);
                    Response<User> response = loginCall.execute();
                    if (response.isSuccessful()) {
                        EventBus.getDefault().post(new HideLoadingEvent("Dissmissed"));
                        if (response.body() != null) {
                            user = response.body();
                            new AsyncTask<Void, Void, Void>() {
                                @Override
                                protected Void doInBackground(Void... voids) {
                                    profile_id = (int) DB.profileDao().createProfile(user.getProfile());
                                    FastSave.getInstance().saveString("token", response.body().getToken());
                                    FastSave.getInstance().saveString("user_id", String.valueOf(response.body().getId()));
                                    EventBus.getDefault().post(new UserAuthenticatedEvent(FastSave.getInstance().getString("token", null)));
                                    return null;
                                }
                            }.execute();

                            new AsyncTask<Void, Void, Void>() {
                                @Override
                                protected Void doInBackground(Void... voids) {
                                    user.setProfile_id(profile_id);
                                    DB.userDao().createUser(user);
                                    return null;
                                }
                            }.execute();

                        } else {
                            Log.e("Error:", response.errorBody().string());
                            EventBus.getDefault().post(new HideLoadingEvent("Dissmissed"));
                        }
                    } else {
                        EventBus.getDefault().post(new HideLoadingEvent("Dissmissed"));
                        UserResponseErrorProcess(response);
                    }
                    return result;
                }
            });
            executor.execute(future);
            return future.get();
        } else {
            //Dispatch show loading event
            EventBus.getDefault().post(new ShowLoadingEvent("Erreur", "Vous n'etes pas connecter a internet", true));
            return null;
        }
    }


    //Send SymptomRect to server
    public SymptomRect sendSymptomRect(JsonObject json) {
        SymptomRect symptomRect = new SymptomRect();
        APIService service = APIClient.getClient().create(APIService.class);
        String token = FastSave.getInstance().getString("token", "");
        Call<SymptomRect> call = service.sendSymptomRect("Token " + token, json);
        try {
            Response<SymptomRect> response = call.execute();
            if (response.isSuccessful()) {
                Log.e("Succeed:", "OK");
                symptomRect = response.body();
                return response.body();
            } else {
                Log.e("Error:", response.errorBody().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return symptomRect;
    }

    //Get Diagnostic from Server
    @SuppressLint("StaticFieldLeak")
    public List<Diagnostic> getDiagnostics() throws IOException {
        if (Constants.isOnline(mContext)) {
            APIService service = APIClient.getClient().create(APIService.class);
            String token = FastSave.getInstance().getString("token", "");
            Call call = service.getDiagnostics("Token " + token);
            Response<DiagnosticResponse> response = call.execute();
            if (response.isSuccessful()) {
                diagnostics = response.body().getResult();

                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        for (Diagnostic d : diagnostics) {
                            long id = DB.diagnosticDao().createDiagnostic(d);
                            try {
                                getDiagnosticPictures(id);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        return null;
                    }
                }.execute();


            } else {
                Log.e("Error:", response.errorBody().string());
            }
        } else {
            EventBus.getDefault().post(new ShowLoadingEvent("Erreur", "Vous n'etes pas connecter a internet", true));
            diagnostics = DB.diagnosticDao().getAll().getValue();
            return diagnostics;
        }
        return diagnostics;
    }

    //Get diagnostic picture from server
    @SuppressLint("StaticFieldLeak")
    public List<Picture> getDiagnosticPictures(long diagnostic_id) throws IOException {
        if (Constants.isOnline(mContext)) {
            APIService service = APIClient.getClient().create(APIService.class);
            String token = FastSave.getInstance().getString("token", "");
            Call call = service.getDiagnosticPictures(diagnostic_id, "Token " + token);
            Response<List<Picture>> response = call.execute();
            if (response.isSuccessful()) {
                List<Picture> list = response.body();
                if (response.body().size() > 0) {
                    for (Picture p : list) {
                        Uri uri = Uri.parse(p.getImage());
                        String destination = mContext.getExternalFilesDir(null).getPath() + File.separator;
                        File f = new File(destination + uri.getLastPathSegment());
                        if (!f.exists()) {
                            DownloadFile(p.getImage());
                        }
                        Log.e("Remote images X 0:", p.getDiagnostic_id() + "//" + p.getId() + "//" + p.getImage());
                        p.setSended(true);
                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... voids) {
                                Uri uri = Uri.parse(p.getImage());
                                String destination = mContext.getExternalFilesDir(null).getPath() + File.separator;
                                p.setImage(destination + uri.getLastPathSegment());
                                p.setDiagnostic_id(diagnostic_id);
                                DB.pictureDao().createPicture(p);
                                Log.e("image DB", "CREATED");
                                return null;
                            }
                        }.execute();
                    }
                }

            } else {
                Log.e("Error:", response.errorBody().string());
            }
        } else {
            EventBus.getDefault().post(new ShowLoadingEvent("Erreur", "Vous n'etes pas connecter a internet", true));
            pictures = DB.pictureDao().getByDiagnosticId(diagnostic_id).getValue();
            return pictures;
        }
        return pictures;
    }

    //Send Diagnostic to Server
    @SuppressLint("StaticFieldLeak")
    public Diagnostic sendDiagnostic(Diagnostic d) throws IOException {
        EventBus.getDefault().post(new ShowLoadingEvent("Please wait", "processing...", false));
        Log.e("Diagnostic pictures", d.getImages_by_parts().size() + "");

        if (Constants.isOnline(mContext)) {
            APIService service = APIClient.getClient().create(APIService.class);
            String token = FastSave.getInstance().getString("token", null);
            Call<Diagnostic> call = service.sendDiagnostic("Token " + token, d);
            Response<Diagnostic> response = call.execute();
            if (response.isSuccessful()) {
                diagnostic = response.body();
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        diagnostic.setSended(true);
                        diagnostic_id = DB.diagnosticDao().createDiagnostic(diagnostic);
                        return null;
                    }
                }.execute();

                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        for (Map.Entry<Integer, String> entry : d.getImages_by_parts().entrySet()) {
                            try {
                                SendDiagnosticPicture(entry.getValue(), entry.getKey(), diagnostic_id);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        return null;
                    }
                }.execute();
                EventBus.getDefault().post(new HideLoadingEvent("Dissmissed"));
                EventBus.getDefault().post(new ShowProcessScreenEvent("From Remote"));
            } else {
                Log.e("Error:", response.errorBody().string());
                EventBus.getDefault().post(new HideLoadingEvent("Dissmissed"));
            }
        } else {
            EventBus.getDefault().post(new HideLoadingEvent("Dissmissed"));
            EventBus.getDefault().post(new ShowLoadingEvent("Erreur", "Vous n'etes pas connecter a internet", true));
            return null;
        }
        return diagnostic;
    }

    //Send Picture of Diagnostic to Server
    @SuppressLint("StaticFieldLeak")
    public boolean SendDiagnosticPicture(String image, long part_id, long diagnotic_id) throws IOException {
        if (Constants.isOnline(mContext)) {
            APIService service = APIClient.getClient().create(APIService.class);
            Picture p = new Picture();
            p.setCulture_part_id(part_id);
            p.setDiagnostic_id(diagnotic_id);
            String base_64 = new EncodeBase64().encode(image);

            Log.e("Diagnostic picture:", image + "//" + part_id + "//" + diagnotic_id + "//" + base_64);
            p.setImage(base_64);

            String token = FastSave.getInstance().getString("token", null);
            Call<Picture> call = service.sendDiagnosticPictures("Token " + token, p);
            Response<Picture> response = call.execute();
            if (response.isSuccessful()) {
                picture = response.body();
//                DownloadFile(picture.getImage());
//                Uri uri = Uri.parse(picture.getImage());
//                String destination = mContext.getExternalFilesDir(null).getPath() + File.separator;
//                p.setImage(destination + uri.getLastPathSegment());
//                Log.e("Diagnostic picture X:", destination + uri.getLastPathSegment());
                p.setImage(image);
                picture.setSended(true);
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        DB.pictureDao().createPicture(p);
                        return null;
                    }
                }.execute();
            } else {
                Log.e("Error:", response.errorBody().string());
            }

        } else {
            EventBus.getDefault().post(new ShowLoadingEvent("Erreur", "Vous n'etes pas connecter a internet", true));
        }
        return true;
    }

    //Get Cultures from Server
    @SuppressLint("StaticFieldLeak")
    public List<Culture> getCultures() throws IOException {
        if (Constants.isOnline(mContext)) {
            APIService service = APIClient.getClient().create(APIService.class);
            Call<List<Culture>> call = service.getCultures();
            Response<List<Culture>> response = call.execute();
            if (response.isSuccessful()) {
                cultures = response.body();
                for (Culture c : cultures) {
                    //Uri uri = Uri.parse("https://banner2.kisspng.com/20180719/kjw/kisspng-cacao-tree-chocolate-polyphenol-cocoa-bean-catechi-wt-5b50795abb1c16.1156862915320006027664.jpg");
                    Uri uri = Uri.parse(c.getImage());
                    DownloadFile(c.getImage());
                    String destination = mContext.getExternalFilesDir(null).getPath() + File.separator;
                    c.setImage(destination + uri.getLastPathSegment());
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            DB.cultureDao().createCulture(c);
                            return null;
                        }
                    }.execute();
                }
            } else {
                Log.e("Error:", response.errorBody().string());
            }

        } else {
            //Dispatch show loading event
            EventBus.getDefault().post(new ShowLoadingEvent("Erreur", "Vous n'etes pas connecter a internet", true));
            //return new ArrayList<>();
        }
        return cultures;
    }

    //Get Struggles from Server
    @SuppressLint("StaticFieldLeak")
    public List<Struggle> getStruggles() throws IOException {
        if (Constants.isOnline(mContext)) {
            APIService service = APIClient.getClient().create(APIService.class);
            Call<StruggleResponse> call = service.getStruggles();
            Response<StruggleResponse> response = call.execute();
            if (response.isSuccessful()) {
                struggles = response.body().getResult();
                for (Struggle s : struggles) {
                    Log.e("Struggles", s.getLink() + "//" + s.getDescription());
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            DB.struggleDao().createStruggle(s);
                            return null;
                        }
                    }.execute();
                }
            } else {
                Log.e("Error Body", response.errorBody().toString());
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        struggles = DB.struggleDao().getAll().getValue();
                        return null;
                    }
                }.execute();
            }
        } else {
            //Dispatch show loading event
            EventBus.getDefault().post(new ShowLoadingEvent("Erreur", "Vous n'etes pas connecter a internet", true));
            //return new ArrayList<>();

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    struggles = DB.struggleDao().getAll().getValue();
                    return null;
                }
            }.execute();
        }
        return struggles;
    }

    //Get Symptoms from Server
    @SuppressLint("StaticFieldLeak")
    public List<Symptom> getSymptoms() throws IOException {
        if (Constants.isOnline(mContext)) {
            APIService service = APIClient.getClient().create(APIService.class);
            Call<List<Symptom>> call = service.getSymptoms();
            Response<List<Symptom>> response = call.execute();
            if (response.isSuccessful()) {
                symptoms = response.body();
                for (Symptom s : symptoms) {
                    Log.e("Symptom", s.getName() + "//" + s.getLink());
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            DB.symptomDao().createSymptom(s);
                            return null;
                        }
                    }.execute();
                }
            } else {
                Log.e("Error Body", response.errorBody().toString());
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        symptoms = DB.symptomDao().getAll().getValue();
                        return null;
                    }
                }.execute();
            }

        } else {
            //Dispatch show loading event
            EventBus.getDefault().post(new ShowLoadingEvent("Erreur", "Vous n'etes pas connecter a internet", true));
            //return new ArrayList<>();
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    symptoms = DB.symptomDao().getAll().getValue();
                    return null;
                }
            }.execute();
        }
        return symptoms;
    }

    //Get Cultures from Server
    @SuppressLint("StaticFieldLeak")
    public List<CulturePart> getCulturePart(int id) throws IOException {
        if (Constants.isOnline(mContext)) {
            APIService service = APIClient.getClient().create(APIService.class);
            Call<List<CulturePart>> call = service.getCulturePart(id);
            Response<List<CulturePart>> response = call.execute();
            if (response.isSuccessful()) {
                cultureParts = response.body();
                Log.e("Cultures Part", cultureParts.size() + "");

                //Download Fake Image Url
//                String url= "https://banner2.kisspng.com/20180409/vgq/kisspng-leaf-logo-brand-plant-stem-folha-5acb0798d686f9.0092563815232551928787.jpg";
//                DownloadFile(url);

                for (CulturePart c : cultureParts) {
                    if (c.getImage() != null) {
                        Log.e("Culture Image", c.getImage());
                        DownloadFile(c.getImage());
                        Uri uri = Uri.parse(c.getImage());
                        String destination = mContext.getExternalFilesDir(null).getPath() + File.separator;
                        c.setImage(destination + uri.getLastPathSegment());
                    }
                    c.setCulture_id(id);
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            DB.culturePartsDao().createCulturePart(c);
                            //get the model
                            Model m = null;
                            try {
                                m = getModel((int) c.getId());
                                m.setPart_id((int) c.getId());
                                //store the model
                                DB.modelDao().createModel(m);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                    }.execute();
                }

            } else {
                Log.e("Error Body", response.errorBody().toString());
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        cultureParts = DB.culturePartsDao().getAll().getValue();
                        return null;
                    }
                }.execute();
            }

        } else {
            //Dispatch show loading event
            EventBus.getDefault().post(new ShowLoadingEvent("Erreur", "Vous n'etes pas connecter a internet", true));
            //return new ArrayList<>();
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    cultureParts = DB.culturePartsDao().getAll().getValue();
                    return null;
                }
            }.execute();
        }
        return cultureParts;
    }

    //Get the Disease from Server
    @SuppressLint("StaticFieldLeak")
    public List<Disease> getDiseases() throws IOException {
        if (Constants.isOnline(mContext)) {
            APIService service = APIClient.getClient().create(APIService.class);
            Call<List<Disease>> call = service.getDiseases();
            Response<List<Disease>> response = call.execute();
            if (response.isSuccessful()) {
                diseases = response.body();
                for (Disease d : diseases) {
                    Log.e("Disease", d.getName() + "//" + d.getStruggle_id());
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            d.setLink(Constants.base_url + d.getLink());
                            DB.diseaseDao().createDisease(d);
                            for (Integer i : d.getSymptoms()) {
                                DiseaseSymptom ds = new DiseaseSymptom();
                                ds.setDisease_id(d.getId());
                                ds.setSymptom_id(i);
                                DB.diseaseSymptomsDao().createDiseaseSymptom(ds);
                            }
                            return null;
                        }
                    }.execute();
                }
            } else {
                Log.e("Error Body", response.errorBody().toString());
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        diseases = DB.diseaseDao().getAll().getValue();
                        return null;
                    }
                }.execute();
            }
        } else {
            //Dispatch show loading event
            EventBus.getDefault().post(new ShowLoadingEvent("Erreur", "Vous n'etes pas connecter a internet", true));
            //return new ArrayList<>();
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    diseases = DB.diseaseDao().getAll().getValue();
                    return null;
                }
            }.execute();
        }
        return diseases;
    }

    //Get the Questions from Server
    @SuppressLint("StaticFieldLeak")
    public List<Question> getQuestions() throws IOException {
        if (Constants.isOnline(mContext)) {
            APIService service = APIClient.getClient().create(APIService.class);
            Call<List<Question>> call = service.getQuestion();
            Response<List<Question>> response = call.execute();
            if (response.isSuccessful()) {
                questions = response.body();
                for (Question q : questions) {
                    Log.e("Question", q.getQuestion() + "//" + q.getPart_culture_id());
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            DB.questionDao().createQuestion(q);
                            return null;
                        }
                    }.execute();
                }
            } else {
                Log.e("Error Body", response.errorBody().toString());
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        questions = DB.questionDao().getAll().getValue();
                        return null;
                    }
                }.execute();
            }
        } else {
            //Dispatch show loading event
            EventBus.getDefault().post(new ShowLoadingEvent("Erreur", "Vous n'etes pas connecter a internet", true));
            //return new ArrayList<>();
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    questions = DB.questionDao().getAll().getValue();
                    return null;
                }
            }.execute();
        }
        return questions;
    }

    //Get the model of the given part id from the server
    @SuppressLint("StaticFieldLeak")
    public Model getModel(int part_id) throws IOException {
        if (Constants.isOnline(mContext)) {
            APIService service = APIClient.getClient().create(APIService.class);
            Call<List<Model>> call = service.getModel(part_id);
            Response<List<Model>> response = call.execute();
            if (response.isSuccessful()) {
                model = response.body().get(0);
                //EventBus.getDefault().post(new ShowLoadingEvent("Please Wait","Data is being download",false));
                Uri model_uri = Uri.parse(model.getPb());
                Uri label_uri = Uri.parse(model.getLabel());

                String destination = Objects.requireNonNull(mContext.getExternalFilesDir(null)).getPath() + File.separator;

                String modelpath = destination + model_uri.getLastPathSegment();
                String label_path = destination + label_uri.getLastPathSegment();
                Log.e("PATHS 0", modelpath);
                Log.e("PATHS 1", label_path);

                File fmodel = new File(modelpath);
                File flabel = new File(label_path);

                if (!fmodel.exists()) {
                    //DownloadFile(model.getPb(), part_id);
                    mContext.startService(DownloadService.getDownloadService(mContext.getApplicationContext(), model.getPb(), part_id));
                }
                model.setPb(fmodel.getAbsolutePath());

                if (!flabel.exists()) {
                    //DownloadFile(model.getLabel(), part_id);
                    mContext.startService(DownloadService.getDownloadService(mContext.getApplicationContext(), model.getLabel(), part_id));
                }
                model.setLabel(flabel.getAbsolutePath());

            } else {
                Log.e("Error Body", response.errorBody().toString());
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        model = DB.modelDao().getByPart(part_id).getValue();
                        return null;
                    }
                }.execute();
            }
            return model;
        } else {
            //Dispatch show loading event
            EventBus.getDefault().post(new ShowLoadingEvent("Erreur", "Vous n'etes pas connecter a internet", true));
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    model = DB.modelDao().getByPart(part_id).getValue();
                    return null;
                }
            }.execute();
            return model;
        }

    }


    //Download the model
    @SuppressLint("StaticFieldLeak")
    public void DownloadFile(String url) {
        if (Constants.isOnline(mContext)) {
            Uri uri = Uri.parse(url);
            String destination = Objects.requireNonNull(mContext.getExternalFilesDir(null)).getPath() + File.separator;
            Log.e("PATHS X", destination + uri.getLastPathSegment());

            downloadId = PRDownloader.download(url, destination, uri.getLastPathSegment())
                    .build()
                    .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                        @Override
                        public void onStartOrResume() {

                        }
                    })
                    .setOnPauseListener(new OnPauseListener() {
                        @Override
                        public void onPause() {

                        }
                    })
                    .setOnCancelListener(new OnCancelListener() {
                        @Override
                        public void onCancel() {

                        }
                    })
                    .setOnProgressListener(new OnProgressListener() {
                        @Override
                        public void onProgress(Progress progress) {
                            Log.d(url, progress.currentBytes + "/" + progress.totalBytes);
                        }
                    })
                    .start(new OnDownloadListener() {
                        @Override
                        public void onDownloadComplete() {
                            Log.d(url, "Finished::" + uri.getLastPathSegment());
                            //EventBus.getDefault().post(new ModelDownloadEvent(totalBytes, totalBytes, 101));
                        }

                        @Override
                        public void onError(Error error) {

                        }
                    });

//            FastSave.getInstance().saveObjectsList(Constants.DOWNLOAD_IDS, downloadID);
        } else {
            //Dispatch show loading event
            EventBus.getDefault().post(new ShowLoadingEvent("Erreur", "Vous n'etes pas connecter a internet", true));
        }

    }


    //Do Stuffs if response is Error
    private void UserResponseErrorProcess(Response<User> response) {
        try {
            result = "ERROR ::" + response.errorBody().string();
            EventBus.getDefault().post(new HideLoadingEvent("Dissmissed"));
            //Check wether if error msg is in the conatants error msg
            for (Map.Entry<String, String> entry : Constants.api_error_msg.entrySet()) {
                if (result.contains(entry.getKey())) {
                    Log.e("Find Error", entry.getKey());
                    EventBus.getDefault().post(new ShowLoadingEvent("Error " + entry.getKey(), entry.getValue(), true));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
