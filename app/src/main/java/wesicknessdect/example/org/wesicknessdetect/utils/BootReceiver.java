package wesicknessdect.example.org.wesicknessdetect.utils;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class BootReceiver extends BroadcastReceiver {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context,"JobService Started!",Toast.LENGTH_LONG).show();
        Log.d("JobService Started","Succeed");
        JobSchedulerUtil.scheduleJob(context);
    }
}
