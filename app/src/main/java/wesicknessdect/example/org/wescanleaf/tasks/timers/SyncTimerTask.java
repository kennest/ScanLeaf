package wesicknessdect.example.org.wescanleaf.tasks.timers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import java.util.TimerTask;

import wesicknessdect.example.org.wescanleaf.utils.SyncReceiver;

public class SyncTimerTask extends TimerTask{
    private Context ctx;
    private SyncReceiver receiver = new SyncReceiver ();
    public SyncTimerTask(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public void run() {
        AlarmManager alarms = (AlarmManager)ctx.getSystemService(Context.ALARM_SERVICE);

        IntentFilter filter = new IntentFilter("ALARM_ACTION");
        ctx.registerReceiver(receiver, filter);

        Intent intent = new Intent("ALARM_ACTION");
        intent.putExtra("param", "My scheduled action");
        PendingIntent operation = PendingIntent.getBroadcast(ctx, 0, intent, 0);
        // invoke broadcast after one minute of my app launch
        alarms.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), operation) ;
    }
}

