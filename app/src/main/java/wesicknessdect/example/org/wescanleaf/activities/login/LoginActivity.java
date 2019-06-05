package wesicknessdect.example.org.wescanleaf.activities.login;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import wesicknessdect.example.org.wescanleaf.activities.BaseActivity;
import wesicknessdect.example.org.wescanleaf.activities.register.SignupActivity;
import wesicknessdect.example.org.wescanleaf.models.Credential;
import wesicknessdect.example.org.wescanleaf.tasks.RemoteTasks;
import wesicknessdect.example.org.wescanleaf.R;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class LoginActivity extends BaseActivity {

    Animation clignoter,slide_up;
    @BindView(R.id.email)
    EditText username;

    @BindView(R.id.password)
    EditText password;

    EditText emailBox;

    Button passwordNew;
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
        passwordNew =(Button) findViewById(R.id.newPassword);
        tv=(TextView) findViewById(R.id.tex);
        tv.startAnimation(clignoter);

        passwordNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewPassword();
            }
        });

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

    public void NewPassword(){
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

        // Get the layout inflater
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.new_password, null, true);
        //layout_root should be the name of the "top-level" layout node in the dialog_layout.xml file.
        emailBox = layout.findViewById(R.id.email);

// 2. Chain together various setter methods to set the dialog characteristics
        builder.setView(layout)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (emailBox.length()==0){
                            emailBox.setError("Ne Laissez pas le champ email vide!!!");
                        }
                        else {
                            try {
                                RemoteTasks.getInstance(LoginActivity.this).ClaimNewPassword(emailBox.getText().toString());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }


                    }
                });

// 3. Get the <code><a href="/reference/android/app/AlertDialog.html">AlertDialog</a></code> from <code><a href="/reference/android/app/AlertDialog.Builder.html#create()">create()</a></code>
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
