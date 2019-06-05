package wesicknessdect.example.org.wesicknessdetect.activities;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.gmail.samehadar.iosdialog.IOSDialog;
import org.apache.commons.io.FileUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import java.io.File;
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
    IOSDialog dialog;
    boolean dialogIsCancelable;
    public static AppDatabase DB;
    public static APIService service;
    private static final String DATABASE_NAME = "scanleaf.db";


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
        if (dialog != null) {
            if (dialog.isShowing() && dialogIsCancelable) {
                dialog.dismiss();
            }
        }
        dialogIsCancelable = event.cancelable;
        dialog = LoaderProgress(event.title, event.content, event.cancelable);
        dialog.show();
    }

    //Show the failed SignUp Dialog
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFailEvent(FailedSignUpEvent event) {
        Log.e("Signup Event dismissed", event.msg + ": " + dialogIsCancelable);
        if (dialog.isShowing() && dialogIsCancelable) {
            dialog.dismiss();
        }
        dialogIsCancelable = event.cancelable;
        dialog = LoaderProgress(event.title, event.msg, event.cancelable);
        dialog.show();
    }

    //Hide the loading dialog
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHideLoadingEvent(HideLoadingEvent event) {
        Log.e("Event dismissed", event.msg + ": " + dialogIsCancelable);
        if (dialog.isShowing() && dialogIsCancelable != true) {
            dialog.dismiss();
        }
    }


    //To Do if User is authenticated
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserAuthenticated(UserAuthenticatedEvent event) {
        Log.e("User authenticated", event.token);
        Intent i = new Intent(BaseActivity.this, ProcessActivity.class);
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
        startActivity(i);
        finish();
    }


    //Base loading dialog function
    private IOSDialog LoaderProgress(String title, String content, boolean cancelable) {
        IOSDialog d = new IOSDialog.Builder(BaseActivity.this)
                .setTitle(title)
                .setMessageContent(content)
                .setSpinnerColorRes(R.color.colorPrimary)
                .setCancelable(cancelable)
                .setTitleColorRes(R.color.white)
                .setMessageContentGravity(Gravity.END)
                .build();
        return d;
    }


    //Reload the current Activity
    protected void Reload() {
        if (Build.VERSION.SDK_INT >= 11) {
            recreate();
        } else {
            Intent intent = getIntent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            finish();
            overridePendingTransition(0, 0);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }
    }

    public void StartSyncingData(Context ctx, int delay) {
        Timer timer = new Timer();
        timer.schedule(new SyncTimerTask(ctx), delay);
        // Init Necessary Data
    }


    public void clearAppData() {
        try {
            String packageName = getPackageName();
            String appDir=getExternalFilesDir(null).getPath() + File.separator;
            Log.d("clearing app data ->",packageName);
            Log.d("clearing app dir ->",appDir);
            Runtime runtime = Runtime.getRuntime();
            runtime.exec("pm clear "+packageName);
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
        System.exit(0);
    }
}
