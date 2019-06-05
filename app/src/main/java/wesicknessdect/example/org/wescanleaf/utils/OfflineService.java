package wesicknessdect.example.org.wescanleaf.utils;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.Timer;

import androidx.annotation.Nullable;

import wesicknessdect.example.org.wescanleaf.tasks.timers.OfflineTimerTask;

public class OfflineService extends Service {
    private Timer mTimer = null;
    public static String str_receiver = "scanleaf.offline.service";
    Intent intent;

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


