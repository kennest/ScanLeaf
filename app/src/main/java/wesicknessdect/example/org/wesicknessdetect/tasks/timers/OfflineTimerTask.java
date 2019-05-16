package wesicknessdect.example.org.wesicknessdetect.tasks.timers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
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
import wesicknessdect.example.org.wesicknessdetect.models.SymptomRect;
import wesicknessdect.example.org.wesicknessdetect.tasks.RemoteTasks;

public class OfflineTimerTask extends TimerTask {
    List<SymptomRect> symptomRects;
    List<Diagnostic> diagnostics;
    List<Picture> pictures;
    List<Post> posti;
    Context ctx;

    public OfflineTimerTask(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public void run() {
        String location = FastSave.getInstance().getString("location", "0.0:0.0");
        String[] split = location.split(":");

        Double lat = Double.valueOf(split[0]);
        Double longi = Double.valueOf(split[1]);

        Log.e("mes_coordonnées", "Lat: " + lat + ", Longi: " + longi);
        Location l = new Location();
        l.setLat(lat.toString());
        l.setLongi(longi.toString());

        //recuperation du dernier id du serveur
        //code de ça ▲

                diagnostics = AppDatabase.getInstance(ctx).diagnosticDao().getAllSync();
                pictures = AppDatabase.getInstance(ctx).pictureDao().getAllSync();
                symptomRects = AppDatabase.getInstance(ctx).symptomRectDao().getAllSync();
                Log.e("Pre Task", "Started");
                String idServeur = "" + 0;
                posti = AppDatabase.getInstance(ctx).postDao().getAllPost();
                Log.e("tous_posts", posti.toString());
                if (posti.size() == 0) {
                    idServeur = "" + 0;
                    l.setIdServeur(idServeur);
                    Log.d("envoye", "Lat:" + l.getLat() + ", Long:" + l.getLongi() + ", idServeur:" + l.getIdServeur());
                    RemoteTasks.getInstance(ctx).sendLocation(l);
                } else {
                    Post p = posti.get(posti.size() - 1);
                    Log.d("dernier_post_data", " | " + p.getId() + " | " + p.getDiseaseName() + " | " + p.getDistance() + " | " + p.getIdServeur() + " | " + p.getTime());
                    idServeur = p.getIdServeur();
                    l.setIdServeur(idServeur);
                    Log.d("envoye", "Lat:" + l.getLat() + ", Long:" + l.getLongi() + ", idServeur:" + l.getIdServeur());
                    RemoteTasks.getInstance(ctx).sendLocation(l);
                }

                try {
                    SendDataOffline();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.e("Pre Task", "Finished");
    }

    @SuppressLint("StaticFieldLeak")
    private void SendDataOffline() throws IOException {
        if (diagnostics != null) {
            for (Diagnostic d : diagnostics) {
                Log.e("Diag::Size", diagnostics.size() + "");
                if (d.getSended() == 0) {
                    RemoteTasks.getInstance(ctx).SendOfflineDiagnostic(d, false);
                }
            }
        }

        if (pictures != null) {
            for (Picture p : pictures) {
                if (p.getSended() == 0) {
                    RemoteTasks.getInstance(ctx).SendDiagnosticPicture(p, false);
                }
            }
        }

        if (symptomRects != null) {
            for (SymptomRect s : symptomRects) {
                Log.e("Diag::Size", symptomRects.size() + "");
                if (s.getSended() == 0) {
                    RemoteTasks.getInstance(ctx).sendSymptomRect(s, false);
                }
            }
        }
    }
}
