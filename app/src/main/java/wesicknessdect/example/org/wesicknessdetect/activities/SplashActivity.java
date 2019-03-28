package wesicknessdect.example.org.wesicknessdetect.activities;

import androidx.room.Room;
import butterknife.BindView;
import butterknife.ButterKnife;
import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.activities.login.LoginActivity;
import wesicknessdect.example.org.wesicknessdetect.database.AppDatabase;
import wesicknessdect.example.org.wesicknessdetect.events.ShowPartScreenEvent;
import wesicknessdect.example.org.wesicknessdetect.events.UserAuthenticatedEvent;
import wesicknessdect.example.org.wesicknessdetect.models.User;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.greenrobot.eventbus.EventBus;

import java.util.List;


public class SplashActivity extends BaseActivity {
    private static int SPLASH_TIME_OUT=6000;
    TextView tv;
    Animation appear,dubas,fromLeft,fromRight, duhaut;
    private AppDatabase appDatabase;
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

        duhaut = AnimationUtils.loadAnimation(this, R.anim.uptodown);
        dubas =AnimationUtils.loadAnimation(this, R.anim.downtoup);
        appear = AnimationUtils.loadAnimation(this, R.anim.splashtransition);
        appear.setStartOffset(800);
        duhaut.setStartOffset(1000);
        dubas.setStartOffset(1000);
        ImageView iv2 = findViewById(R.id.chapeau);
        ImageView iv3 = findViewById(R.id.plantation);
        ImageView imageView= findViewById(R.id.vK);
        imageView.startAnimation(appear);
        iv2.startAnimation(duhaut);
        iv3.startAnimation(dubas);
        if(isAuthenticated) {
            welcome.setVisibility(View.VISIBLE);
            try {
                Thread.sleep(1000);
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
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);

        }


    }

