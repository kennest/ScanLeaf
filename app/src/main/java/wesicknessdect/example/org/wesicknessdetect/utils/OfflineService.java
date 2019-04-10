package wesicknessdect.example.org.wesicknessdetect.utils;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import wesicknessdect.example.org.wesicknessdetect.database.AppDatabase;
import wesicknessdect.example.org.wesicknessdetect.futuretasks.RemoteTasks;
import wesicknessdect.example.org.wesicknessdetect.models.Diagnostic;
import wesicknessdect.example.org.wesicknessdetect.models.Picture;
import wesicknessdect.example.org.wesicknessdetect.models.SymptomRect;
import wesicknessdect.example.org.wesicknessdetect.retrofit.APIClient;
import wesicknessdect.example.org.wesicknessdetect.retrofit.APIService;

public class OfflineService extends Service {
    private Timer mTimer = null;
    long notify_interval = 60000;
    public AppDatabase DB;
    public static APIService service;
    Observer<List<SymptomRect>> obsRect;
    Observer<List<Diagnostic>> obsDiag;
    Observer<List<Picture>> obsPic;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        DB = AppDatabase.getInstance(this);
        service = APIClient.getClient().create(APIService.class);

        Toast.makeText(getApplicationContext(), "Offline service Started", Toast.LENGTH_LONG).show();


        obsDiag = new Observer<List<Diagnostic>>() {
            @Override
            public void onChanged(List<Diagnostic> diagnostics) {
                for (Diagnostic d : diagnostics) {
                    if (d.getSended()==0) {
                        try {
                            RemoteTasks.getInstance(getApplicationContext()).sendDiagnostic(d);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };

        DB.diagnosticDao().getAll().observeForever(obsDiag);

        obsPic = new Observer<List<Picture>>() {
            @Override
            public void onChanged(List<Picture> pictures) {
                for (Picture p : pictures) {
                    Log.e("Offline pic::",p.getX()+"");
                    if(p.getSended()==0){
                        try {
                            RemoteTasks.getInstance(getApplicationContext()).SendDiagnosticPicture(p.getImage(), p.getCulture_part_id(), p.getDiagnostic_id());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        };

        DB.pictureDao().getAll().observeForever(obsPic);


        obsRect = new Observer<List<SymptomRect>>() {
            @Override
            public void onChanged(List<SymptomRect> symptomRects) {
                for (SymptomRect rect : symptomRects) {
                    Log.e("Offline RectF::", rect.picture_id+"");
                    if (rect.sended==0) {
                        RemoteTasks.getInstance(getApplicationContext()).sendSymptomRect(rect);
                    }
                }
            }
        };

        DB.symptomRectDao().getAll().observeForever(obsRect);

        mTimer = new Timer();
        mTimer.schedule(new TimerTaskOffline(), 1, notify_interval);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DB.symptomRectDao().getAll().removeObserver(obsRect);
        DB.diagnosticDao().getAll().removeObserver(obsDiag);
        DB.pictureDao().getAll().removeObserver(obsPic);
    }


    class TimerTaskOffline extends TimerTask {
        @Override
        public void run() {
        }
    }
}


