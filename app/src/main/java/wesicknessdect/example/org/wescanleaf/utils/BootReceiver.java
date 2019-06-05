package wesicknessdect.example.org.wescanleaf.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent offline = new Intent(context, OfflineService.class);
            context.stopService(offline);
            context.startService(offline);
        }
    }

}
