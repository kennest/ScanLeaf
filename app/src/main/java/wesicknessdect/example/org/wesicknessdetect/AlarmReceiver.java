package wesicknessdect.example.org.wesicknessdetect;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;

import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;

import java.util.List;

import wesicknessdect.example.org.wesicknessdetect.activities.NotificationActivity;
import wesicknessdect.example.org.wesicknessdetect.database.AppDatabase;
import wesicknessdect.example.org.wesicknessdetect.models.Post;
import wesicknessdect.example.org.wesicknessdetect.R;

import static android.app.NotificationManager.IMPORTANCE_DEFAULT;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "com.singhajit.notificationDemo.channelId";
    public static AppDatabase DB;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) || Intent.ACTION_PACKAGE_ADDED.equals(intent.getAction()) || Intent.ACTION_PACKAGE_CHANGED.equals(intent.getAction())) {
            DB = AppDatabase.getInstance(context);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            Notification.Builder builder = new Notification.Builder(context)
                    .setGroup("disease");
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            List<Post> posts = DB.postDao().getAllPost();


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder.setChannelId(CHANNEL_ID);
            }


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID,
                        "NotificationDemo",
                        IMPORTANCE_DEFAULT
                );
                notificationManager.createNotificationChannel(channel);
            }


            long[] vibrate = {0, 100, 200, 300};


            if (posts.size() > 0) {
                for (Post post : posts) {

                    String distance = post.getDistance();
                    Character z = '0';
                    if (distance.charAt(0) == z) {
                        distance = post.getDiseaseName() + " détectée près";
                    } else {
                        distance = post.getDiseaseName() + " détectée à " + post.getDistance() + " km";
                    }

                    String d = distance + " de vous";

                    Bundle bundle = new Bundle();
                    Gson gson = new Gson();
                    bundle.putString("post", gson.toJson(post));

                    Intent notificationIntent = new Intent(context, NotificationActivity.class);
                    notificationIntent.putExtra("bundle", bundle);
                    stackBuilder.addParentStack(NotificationActivity.class);
                    stackBuilder.addNextIntent(notificationIntent);
                    PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    Notification notification = builder.setContentTitle("ScanLeaf Notification")
                            .setContentText(d)
                            .setTicker("Alerte, nouvelle maladie!")
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setGroup("disease")
                            .setOnlyAlertOnce(true)
                            .setColor(context.getResources().getColor(R.color.colorPrimaryPix))
                            .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
                            .setContentIntent(pendingIntent).build();
                    SystemClock.sleep(100);
                    notification.vibrate = vibrate;
                    notificationManager.notify((int) post.getId(), notification);
                }
            }

            String y = "";
            if (posts.size() == 0) {
                y = "Aucune maladie n'a été détectée dans les environs...";
            } else if (posts.size() == 1) {
                y = posts.size() + " maladie a été détectée dans les environs...";
            } else if (posts.size() == 2) {
                y = posts.size() + " maladies ont été détectées dans les environs...";
            }

            Notification summaryNotification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setStyle(new NotificationCompat.InboxStyle()
                            .setBigContentTitle(y)
                            .setSummaryText("Détection de maladie"))
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setGroup("disease")
                    .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
                    .setGroupSummary(true)
                    .build();

            SystemClock.sleep(0);
            notificationManager.notify(50, summaryNotification);
        }

    }

}

