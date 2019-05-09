package wesicknessdect.example.org.wesicknessdetect.utils;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.appizona.yehiahd.fastsave.FastSave;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.Nullable;

import wesicknessdect.example.org.wesicknessdetect.database.AppDatabase;
import wesicknessdect.example.org.wesicknessdetect.tasks.RemoteTasks;
import wesicknessdect.example.org.wesicknessdetect.models.Diagnostic;
import wesicknessdect.example.org.wesicknessdetect.models.Location;
import wesicknessdect.example.org.wesicknessdetect.models.Picture;
import wesicknessdect.example.org.wesicknessdetect.models.Post;
import wesicknessdect.example.org.wesicknessdetect.models.SymptomRect;
import wesicknessdect.example.org.wesicknessdetect.tasks.timers.OfflineTimerTask;

public class OfflineService extends Service {
    private Timer mTimer = null;
    public AppDatabase DB;
    public static String str_receiver = "scanleaf.offline.service";
    Intent intent;
    List<SymptomRect> symptomRects;
    List<Diagnostic> diagnostics;
    List<Picture> pictures;
    List<Post> posti;

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
        //Toast.makeText(getApplicationContext(), "Offline service Started", Toast.LENGTH_LONG).show();
        mTimer = new Timer();
        mTimer.schedule(new OfflineTimerTask(this), 0, 60000);
        intent = new Intent(str_receiver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

}


