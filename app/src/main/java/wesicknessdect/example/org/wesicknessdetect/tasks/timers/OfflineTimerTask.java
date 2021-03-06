package wesicknessdect.example.org.wesicknessdetect.tasks.timers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.appizona.yehiahd.fastsave.FastSave;

import java.io.IOException;
import java.util.List;
import java.util.TimerTask;

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

public class OfflineTimerTask extends TimerTask {
    List<SymptomRect> symptomRects;
    List<UserChoice> userChoices;
    List<Diagnostic> diagnostics;
    List<Picture> pictures;
    List<Post> posti;
    User user = new User();
    Profile profile = new Profile();
    Context ctx;
    AppDatabase DB;
    RemoteTasks remoteTasks=null;

    public OfflineTimerTask(Context ctx) {
        this.ctx = ctx;
        DB = AppDatabase.getInstance(this.ctx);
        remoteTasks=RemoteTasks.getInstance(ctx);
    }

    @Override
    public void run() {
        String location = FastSave.getInstance().getString("location", "0.0:0.0");
        String[] split = location.split(":");

        Double lat = Double.valueOf(split[0]);
        Double longi = Double.valueOf(split[1]);

        Location l = new Location();
        l.setLat(lat.toString());
        l.setLongi(longi.toString());

        //recuperation du dernier id du serveur
        //code de ça ▲

        diagnostics = DB.diagnosticDao().getNotSendedSync();
        pictures = DB.pictureDao().getAllSync();
        symptomRects = DB.symptomRectDao().getAllSync();
        userChoices = DB.userChoiceDao().getNotSended(0);
        profile = DB.profileDao().getNotUpdated();
        try{
            user= DB.userDao().getAll().get(0);
        }catch (IndexOutOfBoundsException e){
            Log.e("Error",e.getMessage());
        }

        String idServeur = "" + 0;
        posti = DB.postDao().getAllPost();
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

    }

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
