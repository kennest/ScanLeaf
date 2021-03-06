package wesicknessdect.example.org.wesicknessdetect.activities.register;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.Observer;

import com.mikhaellopez.circularimageview.CircularImageView;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import wesicknessdect.example.org.wesicknessdetect.models.Profile;
import wesicknessdect.example.org.wesicknessdetect.models.User;
import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.activities.BaseActivity;
import wesicknessdect.example.org.wesicknessdetect.activities.login.LoginActivity;
import wesicknessdect.example.org.wesicknessdetect.database.AppDatabase;
import wesicknessdect.example.org.wesicknessdetect.models.Country;
import wesicknessdect.example.org.wesicknessdetect.mvp.presenters.SignupPresenter;
import wesicknessdect.example.org.wesicknessdetect.mvp.view.ISignupView;
import wesicknessdect.example.org.wesicknessdetect.retrofit.APIClient;
import wesicknessdect.example.org.wesicknessdetect.retrofit.APIService;

public class SignupActivity extends BaseActivity implements ISignupView {

    @BindView(R.id.tex1)
    TextView t;
//    @BindView(R.id.first_name)
//    TextView firstame;
//    @BindView(R.id.last_name)
//    TextView lastname;
    @BindView(R.id.email)
    TextView email;
//    @BindView(R.id.fonction)
//    TextView fonction;
    @BindView(R.id.password)
    TextView password;
    @BindView(R.id.username)
    TextView username;
//    @BindView(R.id.phone)
//    TextView phone;
    Animation titre,scale_up;
    @BindView(R.id.signinsubmit)
    Button signupBtn;
    @BindView(R.id.gotolog)
    TextView loginBtn;

    Button signinBtn;

    List<Country> countries = new ArrayList<>();
    @BindView(R.id.countrySpin)
    Spinner country;
//    @BindView(R.id.sexeSpin)
//    Spinner sex;
    List<String> countryStr = new ArrayList<>();
    Animation smalltobig, btta, btta2;
    CircularImageView civ;
    //List<String> SexeStr = new ArrayList<>();
    APIService service;
    SignupPresenter signupPresenter;

    TextView gotolog;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup2);

        View v = getLayoutInflater().inflate(R.layout.signup2, null, false);
        signupPresenter = new SignupPresenter(this, v, this);
        ButterKnife.bind(this);
        civ=(CircularImageView) findViewById(R.id.imageView);
        smalltobig = AnimationUtils.loadAnimation(this, R.anim.smalltobig);
        btta = AnimationUtils.loadAnimation(this, R.anim.btta);
        btta2 = AnimationUtils.loadAnimation(this, R.anim.btta2);
        //scale_up= AnimationUtils.loadAnimation(this, R.anim.item_animation_fall_down);
        //getWindow().getDecorView().getRootView().findViewById(R.id.layoutInput).startAnimation(scale_up);

//        final Intent i = new Intent(this, LoginActivity.class);
//        final Intent i2 = new Intent(this, LoginActivity.class);

        service = APIClient.getClient().create(APIService.class);
//        SexeStr.add("F");
//        SexeStr.add("M");
//        ArrayAdapter<String> sexAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, SexeStr);
//        sex.setAdapter(sexAdapter);
//
        getCountryFromDBandFillSpinner();
        titre = AnimationUtils.loadAnimation(this, R.anim.clignotement);
        t.startAnimation(titre);
        SpannableString text = new SpannableString("Êtes-vous déjà inscrit? Connectez-vous.");
        text.setSpan(new UnderlineSpan(), 24, 38, 0);
        loginBtn.setText(text);

        //loginBtn.setPaintFlags(loginBtn.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        civ.startAnimation(smalltobig);
        textView=(TextView) findViewById(R.id.textView);
        textView.startAnimation(btta);
        //subtitle_header.startAnimation(btta);
        email.startAnimation(btta2);
        username.startAnimation(btta2);
        password.startAnimation(btta2);
        signinBtn=(Button) findViewById(R.id.signinsubmit);
        signinBtn.startAnimation(btta2);
        signinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btta2.cancel();
                SignupTask();
            }
        });
        gotolog=(TextView) findViewById(R.id.gotolog);
        gotolog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }
//    @SuppressLint("StaticFieldLeak")
//    @OnClick(R.id.gotolog)
//    public void Gotolog() {
//        Intent intent = new Intent(this, LoginActivity.class);
//        startActivity(intent);
//
//    }


//    @SuppressLint("StaticFieldLeak")
//    @OnClick(R.id.signinsubmit)
    public void SignupTask() {
        String cName = country.getSelectedItem().toString();
        //Log.i("Country Selected", cName);

        AppDatabase.getInstance(SignupActivity.this).countryDao().getByName(cName).observe(this, new Observer<Country>() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onChanged(Country country) {
                User u = new User();
                Profile p = new Profile();
//                u.setNom(firstame.getText().toString());
//                u.setPrenom(lastname.getText().toString());
                u.setEmail(email.getText().toString());
                u.setPassword(password.getText().toString());
                u.setUsername(username.getText().toString());

                //p.setGender(sex.getSelectedItem().toString());
                p.setAvatar(null);
//                p.setFonction(fonction.getText().toString());
//                p.setMobile(phone.getText().toString());
                p.setCountry_id(country.getId());
                u.setProfile(p);
                new AsyncTask<Void,Void,Void>(){
                    @Override
                    protected Void doInBackground(Void... voids) {
                        signupPresenter.doSignup(u);
                        return null;
                    }
                }.execute();
            }
        });


    }

    private void getCountryFromDBandFillSpinner() {
                AppDatabase.getInstance(getApplicationContext()).countryDao().getAll().observe(this, new Observer<List<Country>>() {
                    @Override
                    public void onChanged(List<Country> countries) {
                        for (Country c : countries) {
                            countryStr.add(c.getName());
                            //Log.i("Country in DB::", c.getId() + "/" + c.getName());
                        }
                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(SignupActivity.this, android.R.layout.simple_spinner_item, countryStr);
                        // Drop down layout style - list view with radio button
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        country.setAdapter(dataAdapter);
                    }
                });

    }

    @Override
    public void onSignupResuslt(String message) {
        Log.e("Presenter signup res:", message);
    }
}
