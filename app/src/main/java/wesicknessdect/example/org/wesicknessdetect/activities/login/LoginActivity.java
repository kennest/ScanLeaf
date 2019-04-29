package wesicknessdect.example.org.wesicknessdetect.activities.login;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.paperdb.Paper;
import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.activities.BaseActivity;
import wesicknessdect.example.org.wesicknessdetect.activities.register.SignupActivity;
import wesicknessdect.example.org.wesicknessdetect.futuretasks.RemoteTasks;
import wesicknessdect.example.org.wesicknessdetect.models.Credential;
import wesicknessdect.example.org.wesicknessdetect.models.Disease;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class LoginActivity extends BaseActivity {

    Animation clignoter,slide_up;
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
        slide_up= AnimationUtils.loadAnimation(this, R.anim.item_animation_fall_down);

        getWindow().getDecorView().getRootView().findViewById(R.id.layoutInput).startAnimation(slide_up);

        signupPageBtn =(Button) findViewById(R.id.gotosign);
        tv=(TextView) findViewById(R.id.tex);
        tv.startAnimation(clignoter);

        signupPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent i2=new Intent(LoginActivity.this, SignupActivity.class);
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
