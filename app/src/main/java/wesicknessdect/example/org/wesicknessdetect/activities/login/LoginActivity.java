package wesicknessdect.example.org.wesicknessdetect.activities.login;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.activities.BaseActivity;
import wesicknessdect.example.org.wesicknessdetect.activities.register.SigninActivity;
import wesicknessdect.example.org.wesicknessdetect.events.ShowSignupScreenEvent;
import wesicknessdect.example.org.wesicknessdetect.futuretasks.RemoteTasks;
import wesicknessdect.example.org.wesicknessdetect.models.Credential;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.ExecutionException;

public class LoginActivity extends BaseActivity {

    Animation clignoter;
    @BindView(R.id.email)
    EditText username;

    @BindView(R.id.password)
    EditText password;

    TextView tv;
    Button loginBtn;
    Button signupPageBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        clignoter= AnimationUtils.loadAnimation(this, R.anim.clignotement);

        signupPageBtn =(Button) findViewById(R.id.gotosign);
        tv=(TextView) findViewById(R.id.tex);
        tv.startAnimation(clignoter);

        signupPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent i2=new Intent(LoginActivity.this, SigninActivity.class);
                startActivity(i2);
            }
        });
    }

    @OnClick(R.id.loginsubmit)
    public void Logintask() {
        Credential c = new Credential();
        c.setEmail(username.getText().toString());
        c.setPassword(password.getText().toString());
        try {
            RemoteTasks.getInstance(LoginActivity.this).doLogin(c);
        } catch (InterruptedException|ExecutionException e) {
            e.printStackTrace();
        }
    }
}
