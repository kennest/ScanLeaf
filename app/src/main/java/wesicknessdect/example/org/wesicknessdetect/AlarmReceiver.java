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
import android.os.SystemClock;
import androidx.core.app.NotificationCompat;
import java.util.List;

import wesicknessdect.example.org.wesicknessdetect.activities.NotificationActivity;
import wesicknessdect.example.org.wesicknessdetect.database.AppDatabase;
import wesicknessdect.example.org.wesicknessdetect.models.Post;

import static android.app.NotificationManager.IMPORTANCE_DEFAULT;

public class AlarmReceiver extends BroadcastReceiver{
    private static final String CHANNEL_ID = "com.singhajit.notificationDemo.channelId";
    public static AppDatabase DB;
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent notificationIntent = new Intent(context, NotificationActivity.class);
        DB=AppDatabase.getInstance(context);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(NotificationActivity.class);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

//        Notification.Builder builderGroup = new Notification.Builder(context)
//                .setContentTitle("WeScanLeaf Notification")
//                .setContentTitle("WeScanLeaf Détection")
//                .setContentText("Certaines maladies ont été détectées...")
//                .setGroupSummary(true)
//                .setGroup("disease")
//                .setContentIntent(pendingIntent);

        Notification.Builder builder = new Notification.Builder(context)
                                        .setGroup("disease");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID);
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "NotificationDemo",
                    IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }


        long[] vibrate = { 0, 100, 200, 300 };

        List<Post> posts=DB.postDao().getAllPost();
        int nb=posts.size();
        if (nb>=1){
        for (Post post:posts){

            String distance=post.getDistance();
            Character z='0';
            if (distance.charAt(0)==z)
            {
                distance=post.getDiseaseName()+" détectée près";
            }else {
                distance=post.getDiseaseName()+" détectée à "+post.getDistance()+" km";
            }
            String d=distance+" de vous";
            Notification notification = builder.setContentTitle("WeScanLeaf Notification")
                    .setContentText(d)
                    .setTicker("Alerte, nouvelle maladie!")
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setGroup("disease")
                    .setOnlyAlertOnce(true)
                    .setColor(context.getResources().getColor(R.color.colorPrimaryPix))
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_launcher))
                    .setContentIntent(pendingIntent).build();
            SystemClock.sleep(800);
            notification.vibrate = vibrate;
            notificationManager.notify((int) post.getId(), notification);
        }}
        String y="";
        if (nb==0){
            y="Aucune maladie n'a été détectée dans les environs...";
        }else if (nb==1){
            y=nb+" maladie a été détectée dans les environs...";
        }else if (nb==2){
            y=nb+" maladies ont été détectées dans les environs...";
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

        SystemClock.sleep(2000);
        notificationManager.notify(50, summaryNotification);
        }

    }

