package wesicknessdect.example.org.wesicknessdetect.tasks;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import id.zelory.compressor.Compressor;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import wesicknessdect.example.org.wesicknessdetect.database.AppDatabase;
import wesicknessdect.example.org.wesicknessdetect.events.FailedSignUpEvent;
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
import wesicknessdect.example.org.wesicknessdetect.models.Location;
import wesicknessdect.example.org.wesicknessdetect.models.Model;
import wesicknessdect.example.org.wesicknessdetect.models.Picture;
import wesicknessdect.example.org.wesicknessdetect.models.Post;
import wesicknessdect.example.org.wesicknessdetect.models.Profile;
import wesicknessdect.example.org.wesicknessdetect.models.Question;
import wesicknessdect.example.org.wesicknessdetect.models.Struggle;
import wesicknessdect.example.org.wesicknessdetect.models.StruggleResponse;
import wesicknessdect.example.org.wesicknessdetect.models.Symptom;
import wesicknessdect.example.org.wesicknessdetect.models.SymptomRect;
import wesicknessdect.example.org.wesicknessdetect.models.User;
import wesicknessdect.example.org.wesicknessdetect.models.UserChoice;
import wesicknessdect.example.org.wesicknessdetect.retrofit.APIClient;
import wesicknessdect.example.org.wesicknessdetect.utils.AppController;
import wesicknessdect.example.org.wesicknessdetect.utils.CompressImage;
import wesicknessdect.example.org.wesicknessdetect.utils.Constants;
import wesicknessdect.example.org.wesicknessdetect.utils.DownloadService;
import wesicknessdect.example.org.wesicknessdetect.utils.EncodeBase64;
import wesicknessdect.example.org.wesicknessdetect.AlarmReceiver;
import wesicknessdect.example.org.wesicknessdetect.events.HideLoadingEvent;
import wesicknessdect.example.org.wesicknessdetect.retrofit.APIService;

public class RemoteTasks {

    private static RemoteTasks remoteTasks;
    //private static APIService service;
    private static AppDatabase DB;
    private static Context mContext;
    static final ExecutorService executor = Executors.newSingleThreadExecutor();
    String result = "";
    List<Culture> cultures = new ArrayList<>();
    List<CulturePart> cultureParts = new ArrayList<>();
    List<Question> questions = new ArrayList<>();
    List<Disease> diseases = new ArrayList<>();
    List<Struggle> struggles = new ArrayList<>();
    List<Symptom> symptoms = new ArrayList<>();
    List<Country> countries = new ArrayList<>();
    List<Diagnostic> diagnostics = new ArrayList<>();
    List<Profile> profiles = new ArrayList<>();
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


    //Send Signup data
    @SuppressLint("CheckResult")
    public void doSignUp(User u) {
        if (Constants.isOnline(mContext)) {
            //Dispatch show loading event
            EventBus.getDefault().post(new ShowLoadingEvent("Veuillez patienter SVP", "Traitement...", true, 2));
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            APIService service = APIClient.getClient().create(APIService.class);
            service.rxDoSignup(u)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io())
                    .subscribeWith(new DisposableSingleObserver<User>() {
                        @Override
                        public void onSuccess(User u) {
                            Completable.fromAction(() -> {
                                int profile_id = (int) DB.profileDao().createProfile(u.getProfile());
                                u.setProfile_id(profile_id);
                                DB.userDao().createUser(u);
                                FastSave.getInstance().saveString("token", u.getToken());
                                EventBus.getDefault().post(new UserAuthenticatedEvent(FastSave.getInstance().getString("token", null)));
                            })
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .unsubscribeOn(Schedulers.io())
                                    .subscribe(() -> {
                                                EventBus.getDefault().post(new HideLoadingEvent());
                                                Log.d("Rx Signup", "Succeed");
                                            },
                                            throwable -> Log.e("Rx Signup Error ->", throwable.getMessage()));
                        }

                        @Override
                        public void onError(Throwable e) {
                            EventBus.getDefault().post(new ShowLoadingEvent("Vos entrées sont invalides", "Inscription échouée", true,0));
                        }
                    });

        } else {
            //Dispatch show loading event
            EventBus.getDefault().post(new ShowLoadingEvent("Erreur", "Vous n'êtes pas connecté a internet", true, 0));
        }


    }

    //Send Login credentials
    @SuppressLint("CheckResult")
    public void doLogin(Credential c) {
        if (Constants.isOnline(mContext)) {
            EventBus.getDefault().post(new ShowLoadingEvent("Veuillez patienter SVP", "Connexion en cours...", false, 2));
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            APIService service = APIClient.getClient().create(APIService.class);
            service.rxDoLogin(c)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io())
                    .subscribeWith(new DisposableSingleObserver<User>() {
                        @Override
                        public void onSuccess(User u) {
                            Profile p = u.getProfile();
                            Uri uri = Uri.parse(p.getAvatar());
                            String destination = mContext.getExternalFilesDir(null).getPath() + File.separator;
                            File f = new File(destination + uri.getLastPathSegment());
                            if (!f.exists()) {
                                DownloadFile(Constants.base_url + p.getAvatar());
                            }
                            p.setAvatar(destination + uri.getLastPathSegment());

                            Completable.fromAction(() -> {
                                AppController.getInstance().InitDBFromServer();

                                //Do other Stuffs
                                profiles = DB.profileDao().getAllSync();
                                if (profiles.size() > 0) {
                                    profile_id = profiles.get(0).getId();
                                } else {
                                    profile_id = (int) DB.profileDao().createProfile(p);
                                }

                                FastSave.getInstance().saveString("token", u.getToken());
                                FastSave.getInstance().saveString("user_id", String.valueOf(u.getId()));
                                EventBus.getDefault().post(new UserAuthenticatedEvent(FastSave.getInstance().getString("token", null)));

                                u.setProfile_id(profile_id);
                                DB.userDao().createUser(u);

                                AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                                Intent notificationIntent = new Intent(mContext, AlarmReceiver.class);
                                PendingIntent broadcast = PendingIntent.getBroadcast(mContext, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                Calendar cal = Calendar.getInstance();
                                cal.add(Calendar.SECOND, 1);
                                alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);
                            })
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .unsubscribeOn(Schedulers.io())
                                    .subscribe(() -> {
                                                EventBus.getDefault().post(new HideLoadingEvent());
                                                Log.d("Rx Login", "Succeed");
                                            },
                                            throwable -> Log.e("Rx Login Error ->", throwable.getMessage()));
                        }

                        @Override
                        public void onError(Throwable e) {
                            EventBus.getDefault().post(new ShowLoadingEvent("Erreur", "Verifier les infos de connexion...", true,0));
                            Log.e("Error:", e.getMessage());
                        }
                    });


        } else {
            //Dispatch show loading event
            EventBus.getDefault().post(new ShowLoadingEvent("Pas d'internet", "Vous n'êtes pas connecté a internet", true, 0));
        }
    }


    //Send User choices of Quiz
    @SuppressLint({"StaticFieldLeak", "CheckResult"})
    public void sendUserChoices(UserChoice choice) {
        if (Constants.isOnline(mContext)) {
            APIService service = APIClient.getClient().create(APIService.class);
            String token = FastSave.getInstance().getString("token", "");
            service.rxSendUserChoices("Token " + token, choice)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io())
                    .subscribeWith(new DisposableSingleObserver<JsonElement>() {
                        @Override
                        public void onSuccess(JsonElement jsonElement) {
                            if (jsonElement.getAsJsonObject().has("statut")) {
                                if (jsonElement.getAsJsonObject().get("statut").getAsInt() == 1) {
                                    Completable.fromAction(() -> {
                                        choice.setSended(1);
                                        DB.userChoiceDao().update(choice);
                                    })
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .unsubscribeOn(Schedulers.io())
                                            .subscribe(() -> {
                                                        Log.d("Rx Send User Choice", "Completed ->" + choice.getUuid());
                                                    },
                                                    throwable -> Log.e("Rx Send Choice Error ->", throwable.getMessage()));
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("Rx Send User Choices:", e.getMessage());
                        }
                    });
        }
    }

    //Send SymptomRect to server
    @SuppressLint({"StaticFieldLeak", "CheckResult"})
    public void sendSymptomRect(SymptomRect r, @Nullable boolean sync) {

        if (Constants.isOnline(mContext)) {
            JsonObject json = new JsonObject();
            json.addProperty("x_min", r.left);
            json.addProperty("y_min", r.bottom);
            json.addProperty("x_max", r.right);
            json.addProperty("y_max", r.top);
            json.addProperty("picture_uuid", r.getPicture_uuid());
            json.addProperty("uuid", r.getUuid());
            json.addProperty("symptom", r.symptom_id);
            json.addProperty("id_mobile", r.x);

            APIService service = APIClient.getClient().create(APIService.class);
            String token = FastSave.getInstance().getString("token", "");
            service.rxSendSymptomRect("Token " + token, json).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io()).subscribeWith(new DisposableSingleObserver<JsonElement>() {
                @Override
                public void onSuccess(JsonElement jsonElement) {
                    if (jsonElement.getAsJsonObject().get("statut").getAsInt() == 1) {
                        Completable.fromAction(() -> {
                            if (!sync) {
                                r.setSended(1);
                                DB.symptomRectDao().updateSymptomRect(r);
                            } else {
                                DB.symptomRectDao().delete(r);
                            }
                        })
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .unsubscribeOn(Schedulers.io())
                                .subscribe(() -> {
                                            Log.d("Send Rect", "Completed ->" + r.getUuid());
                                        },
                                        throwable -> Log.e("Send Rect Error ->", throwable.getMessage()));
                    }
                }

                @Override
                public void onError(Throwable e) {
                    Log.e("Rx Send Rect Error:", e.getMessage());
                }
            });

        }
    }

    //Get the Countries from Server
    @SuppressLint({"StaticFieldLeak", "CheckResult"})
    public void getCountries() {
        if (Constants.isOnline(mContext)) {
            APIService service = APIClient.getClient().create(APIService.class);
            service.rxGetCountry().subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableSingleObserver<List<Country>>() {
                @Override
                public void onSuccess(List<Country> countryList) {
                    countries = countryList;
                    for (Country c : countries) {
                        Completable.fromAction(() -> DB.countryDao().createCountry(c))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(() -> Log.d("Rx Country", "Completed ->" + c.getId()),// completed with success,
                                        throwable -> throwable.printStackTrace()// there was an error
                                );
                    }
                }

                @Override
                public void onError(Throwable e) {
                    Log.e("Error Body", e.getMessage());
                }
            });

        }
    }

    //Get SymptomRect from server
    @SuppressLint({"StaticFieldLeak", "CheckResult"})
    public void getSymptomsRect(Picture p) {
        if (Constants.isOnline(mContext)) {
            APIService service = APIClient.getClient().create(APIService.class);
            String token = FastSave.getInstance().getString("token", "");
            service.rxGetSymptomRect("Token " + token, (int) p.getRemote_id())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableSingleObserver<List<JsonElement>>() {
                @Override
                public void onSuccess(List<JsonElement> symptomRectList) {
                    List<SymptomRect> rectList = new ArrayList<>();
                    Completable.fromAction(() -> {
                        for (JsonElement r : symptomRectList) {
                            SymptomRect s = new SymptomRect();
                            s.setSymptom_id(r.getAsJsonObject().get("symptom").getAsInt());
                            s.left = Float.parseFloat(r.getAsJsonObject().get("x_min").getAsString());
                            s.bottom = Float.parseFloat(r.getAsJsonObject().get("y_min").getAsString());
                            s.top = Float.parseFloat(r.getAsJsonObject().get("y_max").getAsString());
                            s.right = Float.parseFloat(r.getAsJsonObject().get("x_max").getAsString());
                            s.setSended(1);
                            s.setUuid(r.getAsJsonObject().get("uuid").getAsString());
                            s.setPicture_id((int) p.getX());
                            s.setPicture_uuid(p.getUuid());
                            rectList.add(s);
                        }
                    })
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(() -> {
                                        for (SymptomRect sr : rectList) {
                                            Completable.fromAction(() -> DB.symptomRectDao().create(sr))
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(() -> Log.d("Rx Rect Inserted", "Completed ->" + sr.getPicture_id()),// completed with success,
                                                            throwable -> throwable.printStackTrace()// there was an error
                                                    );
                                        }

                                    },// completed with success,
                                    throwable -> throwable.printStackTrace()// there was an error
                            );
                }

                @Override
                public void onError(Throwable e) {
                    Log.e("Error Rx Rects", e.getMessage());
                }
            });
        }
    }

    //Get Diagnostic from Server
    @SuppressLint({"StaticFieldLeak", "CheckResult"})
    public void getDiagnostics(int lastId) {
        if (Constants.isOnline(mContext)) {
            APIService service = APIClient.getClient().create(APIService.class);
            String token = FastSave.getInstance().getString("token", "");

            service.rxGetDiagnostics("Token " + token, lastId).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableSingleObserver<DiagnosticResponse>() {
                @Override
                public void onSuccess(DiagnosticResponse diagnosticResponse) {
                    diagnostics = diagnosticResponse.getResult();
                    for (Diagnostic d : diagnostics) {
                        Completable.fromAction(() -> {
                            d.setSended(1);
                            getDiagnosticPictures(d);
                        })
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(() -> {
                                            Log.d("Rx Diagnostic", "Completed ->" + d.getRemote_id());
                                            Completable.fromAction(() -> DB.diagnosticDao().createDiagnostic(d))
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(() -> Log.d("Rx Diagnostic Inserted", "Completed ->" + d.getRemote_id()),// completed with success,
                                                            throwable -> throwable.printStackTrace()// there was an error
                                                    );
                                        },// completed with success,
                                        throwable -> throwable.printStackTrace()// there was an error
                                );
                    }
                }


                @Override
                public void onError(Throwable e) {
                    Log.e("Error Rx Diagnostics", e.getMessage());
                }
            });
        }
    }

    //Get diagnostic picture from server
    @SuppressLint({"StaticFieldLeak", "CheckResult"})
    public void getDiagnosticPictures(Diagnostic d) {
        if (Constants.isOnline(mContext)) {
            APIService service = APIClient.getClient().create(APIService.class);
            String token = FastSave.getInstance().getString("token", "");
            service.rxGetDiagnosticPictures(d.getRemote_id(), "Token " + token)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableSingleObserver<List<Picture>>() {
                @Override
                public void onSuccess(List<Picture> pictureList) {
                    for (Picture p : pictureList) {
                        Completable.fromAction(() -> {
                            Uri uri = Uri.parse(p.getImage());
                            String destination = mContext.getExternalFilesDir(null).getPath() + File.separator;
                            File f = new File(destination + uri.getLastPathSegment());
                            if (!f.exists()) {
                                DownloadFile(p.getImage());
                            }
                            Log.d("Rx Remote image Exist:", p.getDiagnostic_id() + "//" + p.getX() + "//" + p.getImage());
                            p.setImage(destination + uri.getLastPathSegment());
                            p.setSended(1);
                            p.setDiagnostic_id(d.getRemote_id());
                            p.setDiagnostic_uuid(d.getUuid());

                        })
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(() -> {
                                            Log.d("Rx Diagnostic Picture", p.getRemote_id() + "");
                                            //GET PIXELS OF THE PICTURES
                                            Completable.fromAction(() -> {
                                                long id = DB.pictureDao().createPicture(p);
                                                p.setX((int) id);
                                                getSymptomsRect(p);
                                            })
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(() -> Log.d("Rx Picture Inserted", "Completed ->" + p.getRemote_id()),// completed with success,
                                                            throwable -> throwable.printStackTrace()// there was an error
                                                    );
                                        },// completed with success,
                                        throwable -> throwable.printStackTrace()// there was an error
                                );
                    }

                }

                @Override
                public void onError(Throwable e) {
                    Log.e("Error Rx Pictures", e.getMessage());
                }
            });
        }
    }

    //Send Diagnostic to Server
    @SuppressLint({"StaticFieldLeak", "CheckResult"})
    public void sendDiagnostic(Diagnostic d, @Nullable boolean sync) {
//
    }


    @SuppressLint({"StaticFieldLeak", "CheckResult"})
    public void SendOfflineDiagnostic(Diagnostic d, @Nullable boolean sync) {
        if (Constants.isOnline(mContext)) {
            APIService service = APIClient.getClient().create(APIService.class);
            String token = FastSave.getInstance().getString("token", null);
            service.rxSendDiagnostic("Token " + token, d)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io()).subscribeWith(new SingleObserver<JsonElement>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onSuccess(JsonElement jsonElement) {
                    if (jsonElement.getAsJsonObject().has("statut")) {
                        if (jsonElement.getAsJsonObject().get("statut").getAsInt() == (1)) {

                            Completable.fromAction(() -> {
                                if (!sync) {
                                    d.setSended(1);
                                    DB.diagnosticDao().updateDiagnostic(d);
                                } else {
                                    DB.diagnosticDao().delete(d);
                                }
                            })
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .unsubscribeOn(Schedulers.io())
                                    .subscribe(() -> {
                                                Log.d("Rx Send Diag Offline", "Completed ->" + d.getUuid());
                                                EventBus.getDefault().post(new ShowProcessScreenEvent("From Remote"));
                                            },
                                            throwable -> Log.e("Send Diag Error ->", throwable.getMessage()));
                        }
                    }
                }

                @Override
                public void onError(Throwable e) {
                    Log.e("Rx Diag Send Error ->", e.getMessage());
                }
            });
        }
    }

    //Send Picture of Diagnostic to Server
    @SuppressLint({"StaticFieldLeak", "CheckResult"})
    public void SendDiagnosticPicture(Picture p, @Nullable boolean sync) {
        if (Constants.isOnline(mContext)) {
            JsonObject json = new JsonObject();
            APIService service = APIClient.getClient().create(APIService.class);
            Completable.fromAction(() -> {
                File compressedImg = new CompressImage(mContext).CompressImgFile(new File(p.getImage()));
                String base_64 = new EncodeBase64().encode(compressedImg.getPath());
                //Log.e("Picture ID:", p.getX() + "");
                json.addProperty("diagnostic", p.getDiagnostic_id());
                json.addProperty("image", base_64);
                json.addProperty("diagnostic_uuid", p.getDiagnostic_uuid());
                json.addProperty("uuid", p.getUuid());
                //json.addProperty("id_mobile", p.getX());
                json.addProperty("partCulture", p.getCulture_part_id());
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> {
                        String token = FastSave.getInstance().getString("token", null);
                        service.rxSendDiagnosticPictures("Token " + token, json)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .unsubscribeOn(Schedulers.io())
                                .subscribeWith(new DisposableSingleObserver<JsonElement>() {
                                    @Override
                                    public void onSuccess(JsonElement jsonElement) {
                                        if (jsonElement.getAsJsonObject().get("statut").getAsInt() == 1) {

                                            Completable.fromAction(() -> {
                                                if (!sync) {
                                                    p.setSended(1);
                                                    DB.pictureDao().updatePicture(p);
                                                } else {
                                                    DB.pictureDao().deletePicture(p);
                                                }
                                            })
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .unsubscribeOn(Schedulers.io())
                                                    .subscribe(() -> {
                                                                Log.d("Rx Send Picture Offline", "Completed ->" + p.getUuid());
                                                            },
                                                            throwable -> Log.e("Send Picture Error ->", throwable.getMessage()));
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Log.e("Rx Send Picture Error:", e.getMessage());
                                    }
                                });
                    }, throwable -> {
                    });
        }

    }

    //Update the user informations
    @SuppressLint("StaticFieldLeak")
    public void SendUpdatedUser(User u, Profile p) {
        if (Constants.isOnline(mContext)) {
            String base_64 = "";
            JsonObject json = new JsonObject();
            JsonObject profile = new JsonObject();

            APIService service = APIClient.getClient().create(APIService.class);
            if (p.getAvatar() != null) {
                if (p.getAvatar().equals("")) {
                    base_64 = "rien";
                } else {
                    base_64 = new EncodeBase64().encode(p.getAvatar());
                    profile.addProperty("avatar", base_64);
                }
            }

            if (u.getPassword() != null) {
                json.addProperty("password", u.getPassword());
            } else {
                json.addProperty("password", "");
            }
            json.addProperty("first_name", u.getNom());
            json.addProperty("last_name", u.getPrenom());
            profile.addProperty("country", p.getCountry_id());

            json.addProperty("email", u.getEmail());
            json.add("profil", profile);
            json.addProperty("username", u.getUsername());

            //json.addProperty("id_mobile", p.getX());

            Log.e("Update User Json ->", json.toString());

            String token = FastSave.getInstance().getString("token", null);
            service.rxUpdateProfile("Token " + token, json)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<JsonElement>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @SuppressLint("CheckResult")
                        @Override
                        public void onSuccess(JsonElement jsonElement) {
                            Log.d("updated_user_response:", jsonElement.toString());
                            if (jsonElement.getAsJsonObject().has("statut")) {
                                if (jsonElement.getAsJsonObject().get("statut").getAsInt() == 1) {
                                    Completable.fromAction(() -> {
                                        p.setAvatar(p.getAvatar());
                                        p.setUpdated(1);
                                        DB.userDao().update(u);
                                        DB.profileDao().update(p);
                                    })
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(() -> {
                                                Log.d("Rx User Updated->", "Succeed ->" + p.getId());
                                            }, throwable -> {
                                                Log.d("Rx User Updated Error->", throwable.getMessage());
                                            });
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("Rx Error:", e.getMessage());
                        }
                    });

        }
    }

    //Claim new password from server if forgotten
    @SuppressLint("StaticFieldLeak")
    public void ClaimNewPassword(String Email) throws IOException {
        if (Constants.isOnline(mContext)) {
            JsonObject json = new JsonObject();
            APIService service = APIClient.getClient().create(APIService.class);
            json.addProperty("email", Email);

            Log.e("Update User Json ->", json.toString());

            //String token = FastSave.getInstance().getString("token", null);
            Call<JsonElement> call = service.getNewPassword(json);
            Response<JsonElement> response = call.execute();
            if (response.isSuccessful()) {
                Log.d("user_password_response:", response.body().toString());
                if (response.body().getAsJsonObject().has("statut")) {
                    if (response.body().getAsJsonObject().get("statut").getAsInt() == 1) {
                        // 1. Instantiate an <code><a href="/reference/android/app/AlertDialog.Builder.html">AlertDialog.Builder</a></code> with its constructor
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

// 2. Chain together various setter methods to set the dialog characteristics
                        builder.setMessage("Votre requête a été prise en compte! \n Veuillez vérifier votre adresse email (" + Email + ") pour le nouveau mot de passe généré...")
                                .setTitle("Nouveau mot de passe")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });

// 3. Get the <code><a href="/reference/android/app/AlertDialog.html">AlertDialog</a></code> from <code><a href="/reference/android/app/AlertDialog.Builder.html#create()">create()</a></code>
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
            } else {
                Log.e("Error:", response.errorBody().string());
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

// 2. Chain together various setter methods to set the dialog characteristics
                builder.setMessage("Votre requête n'a pas été prise en compte! \n Votre adresse email (" + Email + ") n'existe pas dans la base de donnée...")
                        .setTitle("Erreur!!!")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

// 3. Get the <code><a href="/reference/android/app/AlertDialog.html">AlertDialog</a></code> from <code><a href="/reference/android/app/AlertDialog.Builder.html#create()">create()</a></code>
                AlertDialog dialog = builder.create();
                dialog.show();
            }

        } else {
            // 1. Instantiate an <code><a href="/reference/android/app/AlertDialog.Builder.html">AlertDialog.Builder</a></code> with its constructor
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

// 2. Chain together various setter methods to set the dialog characteristics
            builder.setMessage("Votre requête n'a pas été prise en compte! \n Votre adresse email (" + Email + ") n'existe pas dans la base de donnée...")
                    .setTitle("Erreur!!!")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

// 3. Get the <code><a href="/reference/android/app/AlertDialog.html">AlertDialog</a                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            ></code> from <code><a href="/reference/android/app/AlertDialog.Builder.html#create()">create()</a></code>
            AlertDialog dialog = builder.create();
            dialog.show();
            //EventBus.getDefault().post(new ShowLoadingEvent("Erreur", "Vous n'etes pas connecter a internet", true));
        }
    }

    //Get Cultures from Server
    @SuppressLint({"StaticFieldLeak", "CheckResult"})
    public void getCultures() {
        if (Constants.isOnline(mContext)) {
            APIService service = APIClient.getClient().create(APIService.class);

            service.rxGetCultures().subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableSingleObserver<List<Culture>>() {
                @Override
                public void onSuccess(List<Culture> cultureList) {
                    cultures = cultureList;
                    for (Culture c : cultures) {
                        Completable.fromAction(() -> {
                            Uri uri = Uri.parse(c.getImage());
                            DownloadFile(c.getImage());
                            String destination = mContext.getExternalFilesDir(null).getPath() + File.separator;
                            c.setImage(destination + uri.getLastPathSegment());
                            DB.cultureDao().createCulture(c);
                        })
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(() -> Log.d("Rx Culture", "Completed ->" + c.getId()),// completed with success,
                                        throwable -> throwable.printStackTrace()// there was an error
                                );
                    }
                }

                @Override
                public void onError(Throwable e) {
                    Log.e("Error Body", e.getMessage());
                }
            });

        }
    }

    //Get Struggles from Server
    @SuppressLint({"StaticFieldLeak", "CheckResult"})
    public void getStruggles() {
        if (Constants.isOnline(mContext)) {
            APIService service = APIClient.getClient().create(APIService.class);
            service.rxGetStruggles()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableSingleObserver<StruggleResponse>() {
                @Override
                public void onSuccess(StruggleResponse struggleResponse) {
                    struggles = struggleResponse.getResult();
                    for (Struggle s : struggles) {
                        Completable.fromAction(() -> {
                            s.setLink(Constants.base_url + s.getLink());
                            DB.struggleDao().createStruggle(s);
                        })
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .unsubscribeOn(Schedulers.io())
                                .subscribe(() -> Log.d("Rx Struggle", "Completed ->" + s.getId()),// completed with success,
                                        throwable -> throwable.printStackTrace()// there was an error
                                );
                    }
                }

                @Override
                public void onError(Throwable e) {
                    Log.e("Error Body", e.getMessage());
                }
            });
        }
    }

    //Get Symptoms from Server
    @SuppressLint({"StaticFieldLeak", "CheckResult"})
    public void getSymptoms() {
        if (Constants.isOnline(mContext)) {
            APIService service = APIClient.getClient().create(APIService.class);
            service.rxGetSymptoms().subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableSingleObserver<List<Symptom>>() {
                @Override
                public void onSuccess(List<Symptom> symptomList) {
                    symptoms = symptomList;
                    for (Symptom s : symptoms) {
                        Completable.fromAction(() -> DB.symptomDao().createSymptom(s))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(() -> Log.d("Rx Symptom", "Completed ->" + s.getId()),// completed with success,
                                        throwable -> throwable.printStackTrace()// there was an error
                                );
                    }
                }

                @Override
                public void onError(Throwable e) {
                    Log.e("Error Body", e.getMessage());
                }
            });

        }
    }

    //Get Cultures from Server
    @SuppressLint({"StaticFieldLeak", "CheckResult"})
    public void getCulturePart(int id) {
        if (Constants.isOnline(mContext)) {
            APIService service = APIClient.getClient().create(APIService.class);

            service.rxGetCulturePart(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io())
                    .subscribeWith(new DisposableSingleObserver<List<CulturePart>>() {
                        @Override
                        public void onSuccess(List<CulturePart> culturePartList) {
                            cultureParts = culturePartList;
                            for (CulturePart c : culturePartList) {
                                Completable.fromAction(() -> {
                                    if (c.getImage() != null) {
                                        File f = new File(c.getImage());
                                        if (!f.exists()) {
                                            DownloadFile(c.getImage());
                                        }
                                        Uri uri = Uri.parse(c.getImage());
                                        String destination = mContext.getExternalFilesDir(null).getPath() + File.separator;
                                        c.setImage(destination + uri.getLastPathSegment());
                                    }
                                    c.setCulture_id(id);
                                    DB.culturePartsDao().createCulturePart(c);
                                    //get the model
                                    getModel(c);
                                })
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(() -> Log.d("Rx CulturePart", "Completed ->" + c.getId()),// completed with success,
                                                throwable -> throwable.printStackTrace()// there was an error
                                        );
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("Error Body", e.getMessage());
                        }
                    });

        }
    }

    //Get the Disease from Server
    @SuppressLint({"StaticFieldLeak", "CheckResult"})
    public List<Disease> getDiseases() {
        if (Constants.isOnline(mContext)) {
            APIService service = APIClient.getClient().create(APIService.class);
            service.rxGetDiseases()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io())
                    .subscribeWith(new DisposableSingleObserver<List<Disease>>() {
                        @Override
                        public void onSuccess(List<Disease> diseaseList) {
                            diseases = diseaseList;
                            for (Disease d : diseases) {
                                Completable.fromAction(() -> {
                                    d.setLink(Constants.base_url + d.getLink());
                                    DB.diseaseDao().createDisease(d);
                                })
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(() -> Log.d("Rx Disease", "Completed ->" + d.getId()),// completed with success,
                                                throwable -> throwable.printStackTrace()// there was an error
                                        );
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("Error Body", e.getMessage());
                        }
                    });
        }
        return diseases;
    }

    //Get the Questions from Server
    @SuppressLint({"StaticFieldLeak", "CheckResult"})
    public void getQuestions() {
        if (Constants.isOnline(mContext)) {
            APIService service = APIClient.getClient().create(APIService.class);
            service.rxGetQuestion().subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<List<Question>>() {
                        @Override
                        public void onSuccess(List<Question> questionList) {
                            // Received all notes
                            questions = questionList;
                            for (Question q : questions) {
                                Completable.fromAction(() -> DB.questionDao().createQuestion(q))
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(() -> Log.d("Rx Question", "Completed ->" + q.getId()),// completed with success,
                                                throwable -> throwable.printStackTrace()// there was an error
                                        );
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            // Network error
                            Log.e("Error", e.getMessage());
                        }
                    });
//            }
        }
    }

    //Get the model of the given part id from the server
    @SuppressLint({"StaticFieldLeak", "CheckResult"})
    public void getModel(CulturePart c) {
        if (Constants.isOnline(mContext)) {
            APIService service = APIClient.getClient().create(APIService.class);
            service.rxGetModel((int) c.getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<List<Model>>() {
                        @Override
                        public void onSuccess(List<Model> modelList) {
                            Uri model_uri = Uri.parse(modelList.get(0).getPb());
                            Uri label_uri = Uri.parse(modelList.get(0).getLabel());

                            String destination = Objects.requireNonNull(mContext.getExternalFilesDir(null)).getPath() + File.separator;

                            String modelpath = destination + model_uri.getLastPathSegment();
                            String label_path = destination + label_uri.getLastPathSegment();

                            File fmodel = new File(modelpath);
                            File flabel = new File(label_path);

                            if (!fmodel.exists()) {
                                //DownloadFile(model.getPb(), part_id);
                                mContext.startService(DownloadService.getDownloadService(mContext.getApplicationContext(), modelList.get(0).getPb(), (int) c.getId()));
                            }
                            modelList.get(0).setPb(fmodel.getAbsolutePath());

                            if (!flabel.exists()) {
                                //DownloadFile(model.getLabel(), part_id);
                                mContext.startService(DownloadService.getDownloadService(mContext.getApplicationContext(), modelList.get(0).getLabel(), (int) c.getId()));
                            }
                            modelList.get(0).setLabel(flabel.getAbsolutePath());
                            modelList.get(0).setPart_id((int) c.getId());

                            Completable.fromAction(() -> {
                                DB.modelDao().createModel(modelList.get(0));
                            })
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(() -> {
                                                Log.d("Rx Model", "Completed ->" + modelList.get(0).getId());
                                            },// completed with success,
                                            throwable -> throwable.printStackTrace()// there was an error
                                    );
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("Error Body", e.getMessage());
                        }
                    });
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
            EventBus.getDefault().post(new ShowLoadingEvent("Erreur", "Vous n'êtes pas connecté(e) à internet...", true, 0));
        }

    }

    //Send My Location to server
    @SuppressLint({"StaticFieldLeak", "CheckResult"})
    public void sendLocation(Location l) {
        if (Constants.isOnline(mContext)) {
            APIService service = APIClient.getClient().create(APIService.class);
            String token = FastSave.getInstance().getString("token", "");
            //String params ="{\"lat\":\""+l.getLat()+"\",\"longi\":\""+l.getLongi()+"\",\"idServeur\":\""+l.getLongi()+"\"}";
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("lat", l.getLat())
                    .addFormDataPart("longi", l.getLongi())
                    .addFormDataPart("idServeur", l.getIdServeur())
                    .build();
            service.rxSendMyLocation("Token " + token, requestBody)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io())
                    .subscribeWith(new DisposableSingleObserver<List<JsonElement>>() {
                        @Override
                        public void onSuccess(List<JsonElement> jsonElements) {
                            Log.d("post_reponse :", jsonElements.toString());
                            if (jsonElements.size() != 0) {
                                AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                                Intent notificationIntent = new Intent(mContext, AlarmReceiver.class);
                                PendingIntent broadcast = PendingIntent.getBroadcast(mContext, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                Completable.fromAction(() -> {
                                    for (JsonElement json : jsonElements) {
                                        Post p = new Post();
                                        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
                                        long millis = new Date().getTime();
                                        String t = dateFormat.format(millis);
                                        p.setDiseaseName(json.getAsJsonObject().get("maladie").getAsString());
                                        p.setDistance(json.getAsJsonObject().get("distance").getAsString());
                                        p.setLatitude(json.getAsJsonObject().get("latitude").getAsDouble());
                                        p.setLongitude(json.getAsJsonObject().get("longitude").getAsDouble());
                                        p.setIdServeur(json.getAsJsonObject().get("id").getAsString());
                                        p.setTime(t);
                                        DB.postDao().createPost(p);
                                        Calendar cal = Calendar.getInstance();
                                        cal.add(Calendar.SECOND, 5);
                                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);
                                    }
                                })
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(() -> {
                                                    Log.d("Rx Posts Get", "Completed");
                                                }
                                                , throwable -> Log.e("Rx Save Posts Error->", throwable.getMessage()));

                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("Rx Get Posts Error->", e.getMessage());
                        }
                    });

        }

    }

}
