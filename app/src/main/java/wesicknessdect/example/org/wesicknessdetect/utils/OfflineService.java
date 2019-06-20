package wesicknessdect.example.org.wesicknessdetect.utils;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import com.appizona.yehiahd.fastsave.FastSave;
import java.util.List;
import androidx.annotation.Nullable;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import wesicknessdect.example.org.wesicknessdetect.database.AppDatabase;
import wesicknessdect.example.org.wesicknessdetect.models.Diagnostic;
import wesicknessdect.example.org.wesicknessdetect.models.Location;
import wesicknessdect.example.org.wesicknessdetect.models.Picture;
import wesicknessdect.example.org.wesicknessdetect.models.Post;
import wesicknessdect.example.org.wesicknessdetect.models.Profile;
import wesicknessdect.example.org.wesicknessdetect.models.SymptomRect;
import wesicknessdect.example.org.wesicknessdetect.models.User;
import wesicknessdect.example.org.wesicknessdetect.models.UserChoice;
import wesicknessdect.example.org.wesicknessdetect.tasks.RemoteTasks;

public class OfflineService extends Service {
    public static String str_receiver = "scanleaf.offline.service";
    List<SymptomRect> symptomRects;
    List<UserChoice> userChoices;
    List<Diagnostic> diagnostics;
    List<Picture> pictures;
    List<Post> posti;
    User user = new User();
    Profile profile = new Profile();
    AppDatabase DB;
    RemoteTasks remoteTasks=null;

    public OfflineService() {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint({"StaticFieldLeak", "CheckResult"})
    @Override
    public void onCreate() {
        super.onCreate();
        DB = AppDatabase.getInstance(getApplicationContext());
        remoteTasks=RemoteTasks.getInstance(getApplicationContext());

        Completable.fromAction(()->{
            String location = FastSave.getInstance().getString("location", "0.0:0.0");
            String[] split = location.split(":");

            Double lat = Double.valueOf(split[0]);
            Double longi = Double.valueOf(split[1]);

            Location l = new Location();
            l.setLat(lat.toString());
            l.setLongi(longi.toString());

            diagnostics = DB.diagnosticDao().getNotSendedSync();
            pictures = DB.pictureDao().getAllSync();
            symptomRects = DB.symptomRectDao().getAllSync();
            userChoices = DB.userChoiceDao().getNotSended(0);
            profile = DB.profileDao().getNotUpdated();
            posti = DB.postDao().getAllPost();

            try{
                user= DB.userDao().getAll().get(0);
            }catch (IndexOutOfBoundsException e){
                Log.e("Error",e.getMessage());
            }

            String idServeur = "" + 0;

            Log.e("tous_posts", posti.toString());
            if (posti.size() == 0) {
                idServeur = "" + 0;
                l.setIdServeur(idServeur);
                Log.d("envoye", "Lat:" + l.getLat() + ", Long:" + l.getLongi() + ", idServeur:" + l.getIdServeur());
                remoteTasks.sendLocation(l);
            } else {
                Post p = posti.get(posti.size() - 1);
                Log.d("dernier_post_data", " | " + p.getId() + " | " + p.getDiseaseName() + " | " + p.getCity() + " | " + p.getIdServeur() + " | " + p.getTime());
                idServeur = p.getIdServeur();
                l.setIdServeur(idServeur);
                remoteTasks.sendLocation(l);
            }
            SendDataOffline();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(()->{
                    Log.d("Rx Offline->","Succeeded");
                },throwable -> {
                    Log.e("Rx Offline Error->",throwable.getMessage());
                });

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    //Send Offline Data
    private void SendDataOffline() {
        if (diagnostics != null) {
            for (Diagnostic d : diagnostics) {
                remoteTasks.SendOfflineDiagnostic(d, false);
            }
        }

        if (pictures != null) {
            for (Picture p : pictures) {
                if (p.getSended() == 0) {
                    remoteTasks.SendDiagnosticPicture(p, false);
                }
            }
        }

        if (symptomRects != null) {
            for (SymptomRect s : symptomRects) {
                Log.e("Rect::Size", symptomRects.size() + "");
                if (s.getSended() == 0) {
                    remoteTasks.sendSymptomRect(s, false);
                }
            }
        }

        if (userChoices != null) {
            for (UserChoice s : userChoices) {
                Log.e("Choices::Size", userChoices.size() + "");
                remoteTasks.sendUserChoices(s);
            }
        }

        if(profile!=null && user!=null){
            remoteTasks.SendUpdatedUser(user,profile);
        }
    }

}


