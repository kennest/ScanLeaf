package wesicknessdect.example.org.wesicknessdetect.activities;

import androidx.room.Room;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.paperdb.Paper;
import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.activities.login.LoginActivity;
import wesicknessdect.example.org.wesicknessdetect.database.AppDatabase;
import wesicknessdect.example.org.wesicknessdetect.tasks.SystemTasks;
import wesicknessdect.example.org.wesicknessdetect.models.User;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.hotmail.or_dvir.easysettings.pojos.BasicSettingsObject;
import com.hotmail.or_dvir.easysettings.pojos.CheckBoxSettingsObject;
import com.hotmail.or_dvir.easysettings.pojos.EasySettings;
import com.hotmail.or_dvir.easysettings.pojos.SeekBarSettingsObject;
import com.hotmail.or_dvir.easysettings.pojos.SettingsObject;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.util.ArrayList;
import java.util.List;


public class SplashActivity extends BaseActivity {
    private static int SPLASH_TIME_OUT = 8000;
    TextView tv;
    WebView pulse;
    Animation appear, dubas, fromLeft, fromRight, out;
    private static AppDatabase appDatabase;
    private static final String DATABASE_NAME = "wesickness.db";
    String token = "";
    boolean isAuthenticated = false;

    @BindView(R.id.welcome_txt)
    TextView welcome;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ButterKnife.bind(this);

        //Init Settings
        ArrayList<SettingsObject> mySettingsList = EasySettings.createSettingsArray(
                new BasicSettingsObject.Builder("sync_now", "Synchroniser maintenant")
                        .setSummary("Recupere vos donnees maintenant")
                        .build(),
                //new EditTextSettingsObject.Builder("number","Definissez le Nombre","0","Sauvegarder").build(),
                new CheckBoxSettingsObject.Builder("sync_after", "Synchronisation des donnees", false)
                        .setSummary("Activez la synchronisation auto des donnees")
                        .build());

        Paper.book().write("SETTINGS", mySettingsList);

        EasySettings.initializeSettings(this, mySettingsList);

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_NETWORK_STATE
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                Log.e("PERMISSIONS", "ALL CHECKED");
                SystemTasks.getInstance(SplashActivity.this).ensureLocationSettings();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
        }).check();
        //*****************CHECK IF USER IS AUTHENTICATED****************/
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                List<User> users = DB.userDao().getAll();
                Moshi moshi = new Moshi.Builder().build();
                JsonAdapter<User> jsonAdapter = moshi.adapter(User.class);
                for (User u : users) {
                    String json = jsonAdapter.toJson(u);
                    Log.e("JSON USER: ", json);
                    Log.e("user: " + users.indexOf(u), u.getNom() + "/" + u.getPrenom() + "/" + u.getEmail());
                    if (u.getToken() != null) {
                        isAuthenticated = true;
                        token = u.getToken();
                        welcome.setText("Bienvenue, " + u.getUsername());
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        //*****************CHECK IF USER IS AUTHENTICATED****************/
//        pulse=findViewById(R.id.pulse);
//        pulse.loadUrl("file:///android_asset/boonnnn.gif");
//        pulse.getSettings().setLoadWithOverviewMode(true);
//        pulse.getSettings().setUseWideViewPort(true);
        out = AnimationUtils.loadAnimation(this, R.anim.splashtransitionout);
        fromLeft = AnimationUtils.loadAnimation(this, R.anim.lefttoright);
        fromRight = AnimationUtils.loadAnimation(this, R.anim.rightoleft);
        dubas = AnimationUtils.loadAnimation(this, R.anim.splashtransition);
        appear = AnimationUtils.loadAnimation(this, R.anim.splashtransition);
        appear.setStartOffset(2800);
        fromLeft.setStartOffset(2300);
        fromRight.setStartOffset(2300);
        //duhaut.setStartOffset(1000);
        dubas.setStartOffset(1500);
        ImageView left = findViewById(R.id.left);
        ImageView iv3 = findViewById(R.id.plantation);
        ImageView right = findViewById(R.id.right);
        //pulse.animate().alpha(0.0f).setDuration(1500).setStartDelay(3000);
        left.startAnimation(fromLeft);
        right.startAnimation(fromRight);
        iv3.startAnimation(dubas);

        if (isAuthenticated) {
            welcome.setVisibility(View.VISIBLE);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        welcome.startAnimation(appear);


        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isAuthenticated) {
                    //EventBus.getDefault().post(new ShowPartScreenEvent("from splash screen"));
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
//                    EventBus.getDefault().post(new UserAuthenticatedEvent(token));
                    Intent intent = new Intent(getApplicationContext(), ProcessActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);


    }


}

