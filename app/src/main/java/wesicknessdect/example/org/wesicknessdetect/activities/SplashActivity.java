package wesicknessdect.example.org.wesicknessdetect.activities;

import androidx.room.Room;
import butterknife.BindView;
import butterknife.ButterKnife;
import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.activities.login.LoginActivity;
import wesicknessdect.example.org.wesicknessdetect.database.AppDatabase;
import wesicknessdect.example.org.wesicknessdetect.events.UserAuthenticatedEvent;
import wesicknessdect.example.org.wesicknessdetect.models.User;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.greenrobot.eventbus.EventBus;

import java.util.List;


public class SplashActivity extends BaseActivity {
    private static int SPLASH_TIME_OUT=8000;
    TextView tv;
    WebView pulse;
    Animation appear,dubas,fromLeft,fromRight, out;
    private static AppDatabase appDatabase;
    private static final String DATABASE_NAME = "wesickness.db";
    String token="";
    boolean isAuthenticated=false;

    @BindView(R.id.welcome_txt)
    TextView welcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        //*****************CHECK IF USER IS AUTHENTICATED****************/
        new Thread(new Runnable() {
            @Override
            public void run() {
                appDatabase = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, DATABASE_NAME)
                        .build();

                List<User> users = appDatabase.userDao().getAll();
                Moshi moshi = new Moshi.Builder().build();
                JsonAdapter<User> jsonAdapter = moshi.adapter(User.class);
                for(User u :users){
                    String json = jsonAdapter.toJson(u);
                    Log.e("JSON USER: ",json);
                    Log.e("user: "+users.indexOf(u), u.getNom()+"/"+u.getPrenom()+"/"+u.getEmail());
                    if(u.getToken()!=null){
                        isAuthenticated=true;
                        token=u.getToken();
                        welcome.setText("Welcome, "+u.getUsername());
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        //*****************CHECK IF USER IS AUTHENTICATED****************/
        pulse=findViewById(R.id.pulse);
        pulse.loadUrl("file:///android_asset/boonnnn.gif");
        pulse.getSettings().setLoadWithOverviewMode(true);
        pulse.getSettings().setUseWideViewPort(true);
        out=AnimationUtils.loadAnimation(this, R.anim.splashtransitionout);
        fromLeft = AnimationUtils.loadAnimation(this, R.anim.lefttoright);
        fromRight =AnimationUtils.loadAnimation(this, R.anim.rightoleft);
        dubas =AnimationUtils.loadAnimation(this, R.anim.splashtransition);
        appear = AnimationUtils.loadAnimation(this, R.anim.splashtransition);
        appear.setStartOffset(2800);
        fromLeft.setStartOffset(2300);
        fromRight.setStartOffset(2300);
        //duhaut.setStartOffset(1000);
        dubas.setStartOffset(1500);
        ImageView left = findViewById(R.id.left);
        ImageView iv3 = findViewById(R.id.plantation);
        ImageView right= findViewById(R.id.right);
        pulse.animate().alpha(0.0f).setDuration(1500).setStartDelay(3000);
        left.startAnimation(fromLeft);
        right.startAnimation(fromRight);
        iv3.startAnimation(dubas);

        if(isAuthenticated) {
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
                if(!isAuthenticated) {
                    //EventBus.getDefault().post(new ShowPartScreenEvent("from splash screen"));
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                   finish();
                }else{
                    EventBus.getDefault().post(new UserAuthenticatedEvent(token));
//                    Intent intent = new Intent(getApplicationContext(), QuizActivity.class);
//                    startActivity(intent);
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);

        }


    }

