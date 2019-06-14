package wesicknessdect.example.org.wesicknessdetect.activities.login;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import wesicknessdect.example.org.wesicknessdetect.activities.BaseActivity;
import wesicknessdect.example.org.wesicknessdetect.activities.ProcessActivity;
import wesicknessdect.example.org.wesicknessdetect.activities.register.SignupActivity;
import wesicknessdect.example.org.wesicknessdetect.events.UserAuthenticatedEvent;
import wesicknessdect.example.org.wesicknessdetect.models.Credential;
import wesicknessdect.example.org.wesicknessdetect.tasks.RemoteTasks;
import wesicknessdect.example.org.wesicknessdetect.R;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class LoginActivity extends BaseActivity {

    Animation clignoter,slide_up;
    @BindView(R.id.email)
    EditText username;

    @BindView(R.id.password)
    EditText password;

    EditText emailBox;

    CircularImageView civ;
    TextView textView, subtitle_header;
    TextView passwordNew;
    TextView tv;

    Animation smalltobig, btta, btta2;
    Button loginBtn;
    TextView signupPageBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login2);
        ButterKnife.bind(this);
        Typeface tf=Typeface.createFromAsset(getAssets(),"finger_paint.ttf");
        Typeface tf1=Typeface.createFromAsset(getAssets(),"changa.ttf");
        Typeface tf2=Typeface.createFromAsset(getAssets(),"neo_latina.ttf");

        textView=(TextView) findViewById(R.id.textView);
        textView.setTypeface(tf);
        //subtitle_header=(TextView) findViewById(R.id.subtitle_header);
        smalltobig = AnimationUtils.loadAnimation(this, R.anim.smalltobig);
        btta = AnimationUtils.loadAnimation(this, R.anim.btta);
        btta2 = AnimationUtils.loadAnimation(this, R.anim.btta2);
        clignoter= AnimationUtils.loadAnimation(this, R.anim.clignotement);
        slide_up= AnimationUtils.loadAnimation(this, R.anim.item_animation_fall_down);

        //getWindow().getDecorView().getRootView().findViewById(R.id.layoutInput).startAnimation(slide_up);

        civ=(CircularImageView) findViewById(R.id.imageView);
        signupPageBtn =(TextView) findViewById(R.id.gotosign);
        passwordNew =(TextView) findViewById(R.id.newPassword);
        tv=(TextView) findViewById(R.id.tex);

        tv.setTypeface(tf);
        tv.startAnimation(clignoter);

        civ.startAnimation(smalltobig);

        textView.startAnimation(btta);
        //subtitle_header.startAnimation(btta);

        username.startAnimation(btta2);
        password.startAnimation(btta2);

        passwordNew.startAnimation(btta2);
        SpannableString text = new SpannableString("Vous n'avez pas de compte? Inscrivez-vous.");
        text.setSpan(new UnderlineSpan(), 27, 41, 0);
        passwordNew.setPaintFlags(passwordNew.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        signupPageBtn.setText(text);
        signupPageBtn.startAnimation(btta2);

        loginBtn=(Button) findViewById(R.id.loginsubmit);
        loginBtn.startAnimation(btta2);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btta2.cancel();
                Logintask();
            }
        });

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


    //To start login task
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

    //To get a new Password
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


    //To Do if User is authenticated
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void UserAuthenticated(UserAuthenticatedEvent event) {
        Log.e("User authenticated", event.token);
        finish();
    }
}
