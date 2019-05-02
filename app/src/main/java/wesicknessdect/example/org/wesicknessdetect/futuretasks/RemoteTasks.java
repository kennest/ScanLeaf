package wesicknessdect.example.org.wesicknessdetect.futuretasks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.Nullable;

import com.appizona.yehiahd.fastsave.FastSave;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Response;
import wesicknessdect.example.org.wesicknessdetect.database.AppDatabase;
import wesicknessdect.example.org.wesicknessdetect.events.FailedSignUpEvent;
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

    List<Model> models = new ArrayList<>();
    Diagnostic diagnostic = new Diagnostic();
    Picture picture = new Picture();
    SymptomRect symptomRect = new SymptomRect();
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
    @SuppressLint("StaticFieldLeak")
    public List<Country> getCountries() throws IOException {
        if (Constants.isOnline(mContext)) {
            Log.e("Country call started:", "Started Ok!!");
            APIService service = APIClient.getClient().create(APIService.class);
            Call<List<Country>> countryCall = service.getCountries();
            Response<List<Country>> response = countryCall.execute();
            if (response.isSuccessful()) {
                countries = response.body();
                //Store country to paperDB
                Paper.book().write("countries", countries);

                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        for (Country c : response.body()) {
                            DB.countryDao().createCountry(c);
                            // Log.e("Country:", c.getName());
                        }
                        return null;
                    }
                }.execute();
            } else {
                Log.i("Country getError:", response.errorBody().string());
            }
        } else {
            //Dispatch show loading event
            //EventBus.getDefault().post(new ShowLoadingEvent("Erreur", "Vous n'etes pas connecter a internet", true));
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    DB.countryDao().getAllSync();
                    return null;
                }
            }.execute();

        }
        return countries;
    }

    //Send Signup data
    public User doSignUp(User u) throws InterruptedException, ExecutionException {
        if (Constants.isOnline(mContext)) {
            //Dispatch show loading event
            EventBus.getDefault().post(new ShowLoadingEvent("Veuillez patienter SVP", "Traitement...", true));

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
                        EventBus.getDefault().post(new FailedSignUpEvent("Vos entrées sont invalides", "Inscription échouée", true));
                        //UserResponseErrorProcess(response);
                    }
                    return user;
                }
            });
            executor.execute(future);
            return future.get();
        } else {
            //Dispatch show loading event
            EventBus.getDefault().post(new ShowLoadingEvent("Erreur", "Vous n'êtes pas connecté a internet", true));
            return new User();
        }


    }

    //Send Login credentials
    public String doLogin(Credential c) throws InterruptedException, ExecutionException {
        if (Constants.isOnline(mContext)) {
            EventBus.getDefault().post(new ShowLoadingEvent("Veuillez patienter SVP", "Traitement...", true));
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
                            EventBus.getDefault().post(new FailedSignUpEvent("Pas de données correspondantes", "Erreur de réponse", true));
                        }
                    } else {
                        EventBus.getDefault().post(new FailedSignUpEvent("Vos entrées sont invalides", "Connexion échouée", true));
                        //UserResponseErrorProcess(response);
                    }
                    return result;
                }
            });
            executor.execute(future);
            return future.get();
        } else {
            //Dispatch show loading event
            EventBus.getDefault().post(new ShowLoadingEvent("Pas d'internet", "Vous n'êtes pas connecté a internet", true));
            return null;
        }
    }


    //Send SymptomRect to server
    @SuppressLint("StaticFieldLeak")
    public SymptomRect sendSymptomRect(SymptomRect r, @Nullable boolean sync) {

        if (Constants.isOnline(mContext)) {
            JsonObject json = new JsonObject();
            json.addProperty("x_min", r.left);
            json.addProperty("y_min", r.bottom);
            json.addProperty("x_max", r.right);
            json.addProperty("y_max", r.top);
            json.addProperty("picture", r.picture_id);
            json.addProperty("symptom", r.symptom_id);
            json.addProperty("id_mobile", r.x);

            APIService service = APIClient.getClient().create(APIService.class);
            String token = FastSave.getInstance().getString("token", "");
            Call<JsonElement> call = service.sendSymptomRect("Token " + token, json);
            try {
                Response<JsonElement> response = call.execute();
                if (response.isSuccessful()) {
                    Log.e("SymptomRect ID:", r.getX() + "");
                    if (response.body().getAsJsonObject().has("statut")) {
                        if (response.body().getAsJsonObject().get("statut").getAsInt() == 1) {
                            new AsyncTask<Void, Void, Void>() {
                                @Override
                                protected Void doInBackground(Void... voids) {
                                    if (!sync) {
                                        r.setSended(1);
                                        DB.symptomRectDao().updateSymptomRect(r);
                                    } else {
                                        DB.symptomRectDao().delete(r);
                                    }
                                    return null;
                                }
                            }.execute();
                        }
                    }

                    return null;
                } else {
                    Log.e("Error:", response.errorBody().string());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return symptomRect;
        }
        return symptomRect;
    }

    //Get SymptomRect from server
    @SuppressLint("StaticFieldLeak")
    public List<SymptomRect> getSymptomsRect(int picture_id) throws IOException {
        List<SymptomRect> symptomRects = new ArrayList<>();
        if (Constants.isOnline(mContext)) {
            APIService service = APIClient.getClient().create(APIService.class);
            String token = FastSave.getInstance().getString("token", "");
            Call call = service.getSymptomRect("Token " + token, picture_id);
            Response<List<JsonElement>> response = call.execute();
            if (response.isSuccessful()) {
                List<JsonElement> symptomRectsDB = response.body();
                //Log.e("RectF Remote:", r.getPicture_id()+"//"+r.left+"//"+r.right);
                assert symptomRectsDB != null;
                for (JsonElement r : symptomRectsDB) {
                    SymptomRect s = new SymptomRect();
                    //s.setX(r.getAsJsonObject().get("id").getAsInt());
                    //s.setPicture_id(r.getAsJsonObject().get("picture").getAsInt());
                    s.setSymptom_id(r.getAsJsonObject().get("symptom").getAsInt());
                    s.left = Float.parseFloat(r.getAsJsonObject().get("x_min").getAsString());
                    s.bottom = Float.parseFloat(r.getAsJsonObject().get("y_min").getAsString());
                    s.top = Float.parseFloat(r.getAsJsonObject().get("y_max").getAsString());
                    s.right = Float.parseFloat(r.getAsJsonObject().get("x_max").getAsString());
                    s.setSended(1);
                    symptomRects.add(s);
                }
                Log.e("Sync RectF length -> ", symptomRects.size() + " -> "+picture_id);
                return symptomRects;
            } else {
                Log.e("Error:", response.errorBody().string());
                return null;
            }
        }
        return symptomRects;
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
                            List<Picture> pictures = new ArrayList<>();
                            d.setSended(1);
                            try {
                                pictures = getDiagnosticPictures(d.getRemote_id());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            DB.diagnosticDao().insertDiagnosticWithPictureAndRect(d, pictures);
                        }
                        return null;
                    }
                }.execute();

            } else {
                Log.e("Error:", response.errorBody().string());
            }
        } else {
            //EventBus.getDefault().post(new ShowLoadingEvent("Erreur", "Vous n'etes pas connecter a internet", true));
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    diagnostics = DB.diagnosticDao().getAllSync();
                    return null;
                }
            }.execute();
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
                    for (Picture p : response.body()) {
                        List<SymptomRect> list1 = new ArrayList<>();
                        list1 = getSymptomsRect((int) p.getRemote_id());
                        p.setSymptomRects(list1);
                        Uri uri = Uri.parse(p.getImage());
                        String destination = mContext.getExternalFilesDir(null).getPath() + File.separator;
                        File f = new File(destination + uri.getLastPathSegment());
                        if (!f.exists()) {
                            DownloadFile(p.getImage());
                            Log.e("Remote image Exist:", p.getDiagnostic_id() + "//" + p.getX() + "//" + p.getImage());
                        }
                        p.setImage(destination + uri.getLastPathSegment());
                        p.setSended(1);
                    }
                    return list;
                }
            } else {
                Log.e("Error:", response.errorBody().string());
            }
        } else {
            //EventBus.getDefault().post(new ShowLoadingEvent("Erreur", "Vous n'etes pas connecter a internet", true));

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    pictures = DB.pictureDao().getByDiagnosticIdSync(diagnostic_id);
                    return null;
                }
            }.execute();

            return pictures;
        }
        return pictures;
    }

    //Send Diagnostic to Server
    @SuppressLint("StaticFieldLeak")
    public Diagnostic sendDiagnostic(Diagnostic d, @Nullable boolean sync) {
//        EventBus.getDefault().post(new ShowLoadingEvent("Please wait", "processing...", false));
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                d.setSended(0);
                for (Picture p : d.getPictures()) {
                    for (SymptomRect sr : p.getSymptomRects()) {
                        Symptom s = DB.symptomDao().getByNameSync(sr.label.toUpperCase());
                        sr.setSymptom_id(s.getId());
                    }
                }
                DB.diagnosticDao().insertDiagnosticWithPictureAndRect(d, d.getPictures());
                return null;
            }
        }.execute();
        EventBus.getDefault().post(new ShowProcessScreenEvent("From Remote"));
        return d;
    }


    @SuppressLint("StaticFieldLeak")
    public void SendOfflineDiagnostic(Diagnostic d, @Nullable boolean sync) throws IOException {
        if (Constants.isOnline(mContext)) {
            APIService service = APIClient.getClient().create(APIService.class);
            String token = FastSave.getInstance().getString("token", null);
            Call<JsonElement> call = service.sendDiagnostic("Token " + token, d);
            Response<JsonElement> response = call.execute();
            if (response.isSuccessful()) {
                if (response.body().getAsJsonObject().has("statut")) {
                    if (response.body().getAsJsonObject().get("statut").getAsInt() == (1)) {
                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... voids) {
                                //Log.e("RM diag ID", response.body() + "");
                                if (!sync) {
                                    d.setSended(1);
                                    DB.diagnosticDao().updateDiagnostic(d);
                                } else {
                                    DB.diagnosticDao().delete(d);
                                }
//                                pictures = DB.pictureDao().getByDiagnosticIdSync(d.getX());
//                                for (Picture p : pictures) {
//                                    if (p.getSended() == 0) {
//                                        try {
//                                            List<SymptomRect> list=new ArrayList<>();
//                                            p.setDiagnostic_id(d.getX());
//                                            SendDiagnosticPicture(p, sync);
//                                            list = DB.symptomRectDao().getByPictureIdSync(p.getX());
//                                            Log.e("Offline Rect lenght->",list.size()+"");
//                                            for (SymptomRect s : list) {
//                                                if (s.getSended() == 0) {
//                                                    sendSymptomRect(s, sync);
//                                                }
//                                            }
//                                        } catch (IOException e) {
//                                            e.printStackTrace();
//                                        }
//
//                                    }
//                                }
                                return null;
                            }
                        }.execute();
                    }
                }
            } else {
                Log.e("Error:", response.errorBody().string());
            }
        } else {
            Log.e("Network Error:", "No Internet Connection");
        }
    }

    //Send Picture of Diagnostic to Server
    @SuppressLint("StaticFieldLeak")
    public boolean SendDiagnosticPicture(Picture p, @Nullable boolean sync) throws IOException {
        String image = p.getImage();
        if (Constants.isOnline(mContext)) {
            JsonObject json = new JsonObject();
            APIService service = APIClient.getClient().create(APIService.class);
            String base_64 = new EncodeBase64().encode(p.getImage());

            //Log.e("Picture ID:", p.getX() + "");
            json.addProperty("diagnostic", p.getDiagnostic_id());
            json.addProperty("image", base_64);
            json.addProperty("id_mobile", p.getX());
            json.addProperty("partCulture", p.getCulture_part_id());

            String token = FastSave.getInstance().getString("token", null);
            Call<JsonElement> call = service.sendDiagnosticPictures("Token " + token, json);
            Response<JsonElement> response = call.execute();
            if (response.isSuccessful()) {
                if (response.body().getAsJsonObject().has("statut")) {
                    if (response.body().getAsJsonObject().get("statut").getAsInt() == 1) {
                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... voids) {
                                if (!sync) {
                                    p.setSended(1);
                                    DB.pictureDao().updatePicture(p);
                                } else {
                                    DB.pictureDao().deletePicture(p);
                                }
                                return null;
                            }
                        }.execute();
                    }
                }
            } else {
                Log.e("Error:", response.errorBody().string());
            }

        } else {

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    p.setImage(image);
                    p.setSended(0);
                    DB.pictureDao().createPicture(p);
                    return null;
                }
            }.execute();
            //EventBus.getDefault().post(new ShowLoadingEvent("Erreur", "Vous n'etes pas connecter a internet", true));
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
                    //Store culture to paperDB
                    Paper.book().write("cultures", cultures);
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
                    //Log.e("Struggles", s.getLink() + "//" + s.getDescription());
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            DB.struggleDao().createStruggle(s);
                            return null;
                        }
                    }.execute();
                }
                //Store culture pars to paperDB
                Paper.book().write("struggles", struggles);
            } else {
                Log.e("Error:", response.errorBody().toString());
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
            // EventBus.getDefault().post(new ShowLoadingEvent("Erreur", "Vous n'etes pas connecter a internet", true));
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

                //Store symptoms to paperDB
                Paper.book().write("symptoms", symptoms);

                for (Symptom s : symptoms) {
                    // Log.e("Symptom", s.getName() + "//" + s.getLink());
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            s.setName(s.getName().toUpperCase());
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
            //EventBus.getDefault().post(new ShowLoadingEvent("Erreur", "Vous n'etes pas connecter a internet", true));
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

                //Store culture pars to paperDB
                Paper.book().write("culture_parts", cultureParts);

                //Log.e("Cultures Part", cultureParts.size() + "");

                //Download Fake Image Url
//                String url= "https://banner2.kisspng.com/20180409/vgq/kisspng-leaf-logo-brand-plant-stem-folha-5acb0798d686f9.0092563815232551928787.jpg";
//                DownloadFile(url);

                for (CulturePart c : cultureParts) {
                    if (c.getImage() != null) {
                        // Log.e("Culture Image", c.getImage());
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
                                models.add(m);
                                //store the model
                                DB.modelDao().createModel(m);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                    }.execute();
                }


                //Store culture pars to paperDB
                Paper.book().write("culture_parts", cultureParts);

                //Store culture pars to paperDB
                Paper.book().write("models", models);

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
                    d.setLink(Constants.base_url + d.getLink());
                    // Log.e("Disease", d.getName() + "//" + d.getStruggle_id());
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
                //Store diseases to paperDB
                Paper.book().write("diseases", diseases);
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

                //Store questions to paperDB
                Paper.book().write("questions", questions);

                for (Question q : questions) {
                    // Log.e("Question", q.getQuestion() + "//" + q.getPart_culture_id());
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
                //Log.e("PATHS 0", modelpath);
                //Log.e("PATHS 1", label_path);

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
    public void OfflineSynchronSending() {

    }

}
