package wesicknessdect.example.org.wesicknessdetect.activities;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.appizona.yehiahd.fastsave.FastSave;
import com.bumptech.glide.Glide;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.gmail.samehadar.iosdialog.IOSDialog;

import org.apache.commons.io.FileUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;

import wesicknessdect.example.org.wesicknessdetect.database.AppDatabase;
import wesicknessdect.example.org.wesicknessdetect.events.FailedSignUpEvent;
import wesicknessdect.example.org.wesicknessdetect.events.ShowPartScreenEvent;
import wesicknessdect.example.org.wesicknessdetect.retrofit.APIClient;
import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.events.HideLoadingEvent;
import wesicknessdect.example.org.wesicknessdetect.events.ShowLoadingEvent;
import wesicknessdect.example.org.wesicknessdetect.events.ShowPixScreenEvent;
import wesicknessdect.example.org.wesicknessdetect.events.ShowProcessScreenEvent;
import wesicknessdect.example.org.wesicknessdetect.events.ShowQuizPageEvent;
import wesicknessdect.example.org.wesicknessdetect.events.UserAuthenticatedEvent;
import wesicknessdect.example.org.wesicknessdetect.retrofit.APIService;
import wesicknessdect.example.org.wesicknessdetect.tasks.timers.SyncTimerTask;


public class BaseActivity extends AppCompatActivity {
    AlertDialog myDialog;
    public static AppDatabase DB;
    public static APIService service;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        DB = AppDatabase.getInstance(this);
        service = APIClient.getClient().create(APIService.class);
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    //Show Quiz Layout
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onQuizPageEvent(ShowQuizPageEvent event) {
        Intent i = new Intent(this, QuizActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    //Show the loading Dialog
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoadingEvent(ShowLoadingEvent event) {
        Log.d("Loader", "Shown...");
        if(myDialog!=null) {
            if (myDialog.isShowing() && event.cancelable) {
                myDialog.dismiss();
            }
        }
        myDialog = MyDialog(BaseActivity.this, event.content, event.title, event.type, event.cancelable);
        myDialog.show();
    }

    public AlertDialog MyDialog(Activity context, String msg, String title, int type, boolean cancelable) {
        View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.loader_layout, null);
        TextView alert_title = dialogView.findViewById(R.id.title);
        TextView alert_content = dialogView.findViewById(R.id.content);
        ImageView alert_image = dialogView.findViewById(R.id.image);
        Button alert_btn = dialogView.findViewById(R.id.close);
        alert_title.setText(title);
        alert_content.setText(msg);
        switch (type) {
            case 0:
                alert_image.setColorFilter(ContextCompat.getColor(context, R.color.danger), android.graphics.PorterDuff.Mode.SRC_IN);
                alert_image.setImageDrawable(getDrawable(R.drawable.alert));
                break;
            case 1:
                alert_image.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);
                alert_image.setImageDrawable(getDrawable(R.drawable.ic_check_circle_white_48dp));
                break;
            case 2:
                Glide.with(context)
                        .asGif()
                        .load(Uri.parse("file:///android_asset/syncing.gif"))
                        .into(alert_image);
                alert_btn.setVisibility(View.GONE);
                break;
        }
        alert_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myDialog!=null) {
                    if (myDialog.isShowing()) {
                        myDialog.dismiss();
                    }
                }
            }
        });
        return new AlertDialog.Builder(context)
                .setView(dialogView)
                .setCancelable(cancelable)
                .create();
    }

    //Hide the loading dialog
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHideLoadingEvent(HideLoadingEvent event) {
        Log.e("Dialog dismissed", "True");
        if(myDialog!=null) {
            if (myDialog.isShowing()) {
                myDialog.dismiss();
            }
        }
    }


    //To Do if User is authenticated
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserAuthenticated(UserAuthenticatedEvent event) {
        Log.e("User authenticated", event.token);
        Intent i = new Intent(BaseActivity.this, RestoreDataActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    //launch Pix camera activity
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showPixActivity(ShowPixScreenEvent event) {
        Log.e("Pix activity started", event.part_id + "");
        Pix.start((FragmentActivity) BaseActivity.this, Options.init().setRequestCode(event.part_id).setCount(1).setFrontfacing(true));
        //finish();
    }

    //launch part chooser activity
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showPartChooserActivity(ShowPartScreenEvent event) {
        Log.e("Part activity started", event.message);
        Intent i = new Intent(BaseActivity.this, ChooseCulturePartActivity.class);
        startActivity(i);
        //finish();
    }

    //launch part chooser activity
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showProcessScreen(ShowProcessScreenEvent event) {
        Log.e("Process started", event.message);
        Intent i = new Intent(BaseActivity.this, ProcessActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }


    //Reload the current Activity
    protected void Reload() {
        recreate();
    }

    public void StartSyncingData(Context ctx, int delay) {
        Timer timer = new Timer();
        timer.schedule(new SyncTimerTask(ctx), delay);
        // Init Necessary Data
    }


    public void clearAppData() {
        try {
            String packageName = getPackageName();
            String appDir = getExternalFilesDir(null).getPath() + File.separator;
            Log.d("clearing app data ->", packageName);
            Log.d("clearing app dir ->", appDir);
            Runtime runtime = Runtime.getRuntime();
            runtime.exec("pm clear " + packageName);
            FastSave.getInstance().deleteValue("token");
            File dir = new File(appDir);
            FileUtils.deleteDirectory(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void restartApp() {
        Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
        int mPendingIntentId = 4850;
        PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        finishAffinity();
        System.exit(0);
    }
}
