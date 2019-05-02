package wesicknessdect.example.org.wesicknessdetect.utils;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.appizona.yehiahd.fastsave.FastSave;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import wesicknessdect.example.org.wesicknessdetect.database.AppDatabase;
import wesicknessdect.example.org.wesicknessdetect.futuretasks.RemoteTasks;
import wesicknessdect.example.org.wesicknessdetect.models.Diagnostic;
import wesicknessdect.example.org.wesicknessdetect.models.Location;
import wesicknessdect.example.org.wesicknessdetect.models.Picture;
import wesicknessdect.example.org.wesicknessdetect.models.Post;
import wesicknessdect.example.org.wesicknessdetect.models.SymptomRect;

public class OfflineService extends Service {
    private Timer mTimer = null;
    public AppDatabase DB;
    public static String str_receiver = "scanleaf.offline.service";
    Intent intent;
    List<SymptomRect> symptomRects;
    List<Diagnostic> diagnostics;
    List<Picture> pictures;
    Post p;
    public OfflineService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onCreate() {
        super.onCreate();
        DB = AppDatabase.getInstance(this);
        Toast.makeText(getApplicationContext(), "Offline service Started", Toast.LENGTH_LONG).show();
        mTimer = new Timer();
        mTimer.schedule(new TimerTaskOffline(), 1, 60000);
        intent = new Intent(str_receiver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }


    @SuppressLint("StaticFieldLeak")
    private void SendDataOffline() throws IOException {
        if (diagnostics != null) {
            for (Diagnostic d : diagnostics) {
                Log.e("Diag::Size", diagnostics.size() + "");
                if (d.getSended() == 0) {
                    RemoteTasks.getInstance(getApplicationContext()).SendOfflineDiagnostic(d);
                }
            }
        }

        if(symptomRects!=null){
            for(SymptomRect s:symptomRects){
                Log.e("Diag::Size", symptomRects.size() + "");
                if (s.getSended() == 0) {
                    RemoteTasks.getInstance(getApplicationContext()).sendSymptomRect(s);
                }
            }
        }
    }

    private class TimerTaskOffline extends TimerTask {
        @SuppressLint("StaticFieldLeak")
        @Override
        public void run() {

            String location= FastSave.getInstance().getString("location","0.0:0.0");
            String[] split=location.split(":");

            Double lat= Double.valueOf(split[0]);
            Double longi= Double.valueOf(split[1]);

            Log.d("Mes coordonnées", "Lat: "+lat+", Longi: "+longi);
            Location l=new Location();
            l.setLat(lat.toString());
            l.setLongi(longi.toString());

            //recuperation du dernier id du serveur
            //code de ça ▲

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    Log.e("Pre Task", "Started");
                   p=DB.postDao().getLastPost();
                    Log.d("dernier_post_data"," | "+p.getId()+" | "+p.getDiseaseName()+" | "+p.getDistance()+" | "+p.getIdServeur()+" | "+p.getTime());
                    String idServeur=""+0;
                    Log.d("dernier_post",""+p.getIdServeur());
                    if (p.getIdServeur()!=null) {
                        Post a = p;
                        idServeur = a.getIdServeur();
                    }else{
                        idServeur=""+0;
                    }

                    l.setIdServeur(idServeur);
                    Log.d("envoye", "Lat:"+l.getLat()+", Long:"+l.getLongi()+", idServeur:"+l.getIdServeur());
                    RemoteTasks.getInstance(getApplicationContext()).sendLocation(l);

                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    try {
                        SendDataOffline();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.e("Pre Task", "Finished");
                }
            }.execute();


            //Toast.makeText(getApplicationContext(), "Offline Really Started", Toast.LENGTH_LONG).show();
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }


                @Override
                protected Void doInBackground(Void... voids) {
                    Log.e("Pre Task", "Started");
                    diagnostics = DB.diagnosticDao().getAllSync();
                    pictures = DB.pictureDao().getAllSync();
                    symptomRects = DB.symptomRectDao().getAllSync();
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    try {
                        SendDataOffline();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.e("Pre Task", "Finished");
                }
            }.execute();


        }

    }
}


