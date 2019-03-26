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

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import androidx.annotation.NonNull;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wesicknessdect.example.org.wesicknessdetect.database.AppDatabase;
import wesicknessdect.example.org.wesicknessdetect.events.HideLoadingEvent;
import wesicknessdect.example.org.wesicknessdetect.events.ModelDownloadEvent;
import wesicknessdect.example.org.wesicknessdetect.events.ShowLoadingEvent;
import wesicknessdect.example.org.wesicknessdetect.events.UserAuthenticatedEvent;
import wesicknessdect.example.org.wesicknessdetect.models.Country;
import wesicknessdect.example.org.wesicknessdetect.models.Credential;
import wesicknessdect.example.org.wesicknessdetect.models.Culture;
import wesicknessdect.example.org.wesicknessdetect.models.CulturePart;
import wesicknessdect.example.org.wesicknessdetect.models.Model;
import wesicknessdect.example.org.wesicknessdetect.models.Question;
import wesicknessdect.example.org.wesicknessdetect.models.User;
import wesicknessdect.example.org.wesicknessdetect.retrofit.APIClient;
import wesicknessdect.example.org.wesicknessdetect.retrofit.APIService;
import wesicknessdect.example.org.wesicknessdetect.utils.Constants;

public class RemoteTasks {

    private static RemoteTasks remoteTasks;
    //private static APIService service;
    private static AppDatabase DB;
    private static Context mContext;
    static final ExecutorService executor = Executors.newFixedThreadPool(2);
    String result = "";
    List<Culture> cultures = new ArrayList<>();
    Model model = new Model();
    List<CulturePart> cultureParts = new ArrayList<>();
    List<Question> questions = new ArrayList<>();
    User user = new User();
    boolean fileDownloaded;
    boolean writtenToDisk;
    List<Integer> downloadID = new ArrayList<>();
    int downloadId = 0;
    long currentBytes = 0;
    long totalBytes = 0;

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


    private void getCountries() {
        if (Constants.isOnline(mContext)) {
            Log.e("Country call started:", "Started Ok!!");
            APIService service = APIClient.getClient().create(APIService.class);
            Call<List<Country>> countryCall = service.getCountries();
            countryCall.enqueue(new Callback<List<Country>>() {
                @Override
                public void onResponse(Call<List<Country>> call, Response<List<Country>> response) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (Country c : response.body()) {
                                AppDatabase.getInstance(mContext).countryDao().createCountry(c);
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
        }

    }

    //Send Signup data
    public User doSignUp(User u) throws InterruptedException, ExecutionException {
        if (Constants.isOnline(mContext)) {
            //Dispatch show loading event
            EventBus.getDefault().post(new ShowLoadingEvent("Please wait", "processing...", false));

            Thread.sleep(1000);
            FutureTask<User> future = new FutureTask<>(new Callable<User>() {
                @Override
                public User call() throws Exception {
                    APIService service = APIClient.getClient().create(APIService.class);
                    Call<User> SignupCall = service.doSignup(u);
                    SignupCall.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    user = response.body();
                                    result = response.body().toString();
                                    UserResponseSuccessfullProcess(response);
                                }
                            } else {
                                UserResponseErrorProcess(response);
                            }
                            Log.e("SignupCall Result", result);

                            //Dispatch hide loading event
                            EventBus.getDefault().post(new HideLoadingEvent("Dissmissed"));
                        }

                        @Override
                        public void onFailure(@NonNull Call<User> call, Throwable t) {
                            FailureProcess(t);
                            call.cancel();
                        }
                    });
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
            Thread.sleep(1000);
            FutureTask<String> future = new FutureTask<>(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    APIService service = APIClient.getClient().create(APIService.class);
                    Call<User> loginCall = service.doLogin(c);
                    loginCall.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    UserResponseSuccessfullProcess(response);
                                }
                            } else {
                                UserResponseErrorProcess(response);
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<User> call, Throwable t) {
                            FailureProcess(t);
                            call.cancel();
                        }
                    });
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

    //Get Culture from Server
    public List<Culture> getCulture() {
        if (Constants.isOnline(mContext)) {
            APIService service = APIClient.getClient().create(APIService.class);
            Call<List<Culture>> call = service.getCultures();
            call.enqueue(new Callback<List<Culture>>() {
                @Override
                public void onResponse(Call<List<Culture>> call, Response<List<Culture>> response) {
                    if (response.isSuccessful()) {
                        cultures = response.body();
                    } else {

                    }
                }

                @Override
                public void onFailure(Call<List<Culture>> call, Throwable t) {

                }
            });

        }else {
            //Dispatch show loading event
            EventBus.getDefault().post(new ShowLoadingEvent("Erreur", "Vous n'etes pas connecter a internet", true));
            //return new ArrayList<>();
        }
        return cultures;
    }


    //Get Culture from Server
    @SuppressLint("StaticFieldLeak")
    public List<CulturePart> getCulturePart(int id) throws IOException {
        if (Constants.isOnline(mContext)) {
            APIService service = APIClient.getClient().create(APIService.class);
            Call<List<CulturePart>> call = service.getCulturePart(id);
            Response<List<CulturePart>> response = call.execute();
            if (response.isSuccessful()) {
                cultureParts = response.body();
                Log.e("Cultures Part", cultureParts.size() + "");

                for (CulturePart c : cultureParts) {
                    c.setImage("https://banner2.kisspng.com/20180409/vgq/kisspng-leaf-logo-brand-plant-stem-folha-5acb0798d686f9.0092563815232551928787.jpg");
                    Log.e("Culture Image", c.getImage());
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
            }

        } else {
            //Dispatch show loading event
            EventBus.getDefault().post(new ShowLoadingEvent("Erreur", "Vous n'etes pas connecter a internet", true));
            //return new ArrayList<>();
        }
        return cultureParts;
    }


    //Get the Question from Server
    @SuppressLint("StaticFieldLeak")
    public List<Question> getQuestions() throws IOException {
        if (Constants.isOnline(mContext)) {
            APIService service = APIClient.getClient().create(APIService.class);
            Call<List<Question>> call = service.getQuestion();
            Response<List<Question>> response = call.execute();
            if (response.isSuccessful()) {
                questions = response.body();
                for(Question q:questions){
                    Log.e("Question",q.getQuestion()+"//"+q.getPart_culture_id());
                    new AsyncTask<Void,Void,Void>(){
                        @Override
                        protected Void doInBackground(Void... voids) {
                            DB.questionDao().createQuestion(q);
                            return null;
                        }
                    }.execute();
                }
            } else {
                Log.e("Error Body", response.errorBody().toString());
            }
        } else {
            //Dispatch show loading event
            EventBus.getDefault().post(new ShowLoadingEvent("Erreur", "Vous n'etes pas connecter a internet", true));
            //return new ArrayList<>();
        }
        return questions;
    }

    //Get the model from the server
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
                    DownloadFile(model.getPb(), part_id);
                }
                model.setPb(fmodel.getAbsolutePath());

                if (!flabel.exists()) {
                    DownloadFile(model.getLabel(), part_id);
                }
                model.setLabel(flabel.getAbsolutePath());

            } else {
                Log.e("Error Body", response.errorBody().toString());
            }
            return model;
        } else {
            //Dispatch show loading event
            EventBus.getDefault().post(new ShowLoadingEvent("Erreur", "Vous n'etes pas connecter a internet", true));
            return model;
        }

    }


    //Download the model
    public void DownloadFile(String url, int part_id) {
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
                            //Log.d(url, progress.currentBytes + "/" + progress.totalBytes);
                            currentBytes = progress.currentBytes;
                            totalBytes = progress.totalBytes;
                            EventBus.getDefault().post(new ModelDownloadEvent(progress.currentBytes, progress.totalBytes, part_id));
                        }
                    })
                    .start(new OnDownloadListener() {
                        @Override
                        public void onDownloadComplete() {
                            Log.d(url, "Finished::" + uri.getLastPathSegment());
                            EventBus.getDefault().post(new ModelDownloadEvent(totalBytes, totalBytes, part_id));
                        }

                        @Override
                        public void onError(Error error) {

                        }

                    });
            downloadID.add(downloadId);

//            FastSave.getInstance().saveObjectsList(Constants.DOWNLOAD_IDS, downloadID);
        } else {
            //Dispatch show loading event
            EventBus.getDefault().post(new ShowLoadingEvent("Erreur", "Vous n'etes pas connecter a internet", true));
        }

    }

    //Write Model Streams to Disk
    private boolean writeResponseBodyToDisk(ResponseBody body) {
        try {
            // todo change the file location/name according to your needs
            File modelFile = new File(mContext.getExternalFilesDir(null) + File.separator + "example.pb");

            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096];
                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(modelFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                    Log.d("File Download: ", fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();
                fileDownloaded = true;
                Log.e("File Path", modelFile.getAbsolutePath());

                EventBus.getDefault().post(new HideLoadingEvent("File Download Finished"));
                EventBus.getDefault().post(new ShowLoadingEvent("Done!", "Data downloaded", true));

                return fileDownloaded;
            } catch (IOException e) {
                fileDownloaded = false;
                EventBus.getDefault().post(new ShowLoadingEvent("Error!", "Data not downloaded", true));
                return fileDownloaded;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            fileDownloaded = false;
            EventBus.getDefault().post(new ShowLoadingEvent("Error!", "Data not downloaded", true));
            return fileDownloaded;
        }
    }

    //Do Stuffs if Response is successful
    private void UserResponseSuccessfullProcess(Response<User> response) {

        User user = response.body();
        int profile_id = (int) AppDatabase.getInstance(mContext).profileDao().createProfile(user.getProfile());
        user.setProfile_id(profile_id);
        AppDatabase.getInstance(mContext).userDao().createUser(user);
        FastSave.getInstance().saveString("token", response.body().getToken());
        EventBus.getDefault().post(new UserAuthenticatedEvent(FastSave.getInstance().getString("token", null)));
    }

    //Do Stuffs if response is Error
    private void UserResponseErrorProcess(Response<User> response) {
        try {
            result = "ERROR ::" + response.errorBody().string();
            //Check wether if error msg is in the conatants error msg
            for (Map.Entry<String, String> entry : Constants.api_error_msg.entrySet()) {
                if (result.contains(entry.getKey())) {
                    Log.e("Find Error", entry.getKey());
                    EventBus.getDefault().post(new HideLoadingEvent("Dissmissed"));
                    EventBus.getDefault().post(new ShowLoadingEvent("Error " + entry.getKey(), entry.getValue(), true));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Do Stuffs on Request Failure
    private void FailureProcess(Throwable t) {
        EventBus.getDefault().post(new HideLoadingEvent("Dissmissed"));
        EventBus.getDefault().post(new ShowLoadingEvent("Api Error ", t.getMessage(), true));
        Log.e("Remote Task", t.getMessage());
    }
}
