package wesicknessdect.example.org.wesicknessdetect.activities.register;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.activities.BaseActivity;
import wesicknessdect.example.org.wesicknessdetect.activities.login.LoginActivity;
import wesicknessdect.example.org.wesicknessdetect.database.AppDatabase;
import wesicknessdect.example.org.wesicknessdetect.models.Country;
import wesicknessdect.example.org.wesicknessdetect.models.Profile;
import wesicknessdect.example.org.wesicknessdetect.models.User;
import wesicknessdect.example.org.wesicknessdetect.mvp.presenters.SignupPresenter;
import wesicknessdect.example.org.wesicknessdetect.mvp.view.ISignupView;
import wesicknessdect.example.org.wesicknessdetect.retrofit.APIClient;
import wesicknessdect.example.org.wesicknessdetect.retrofit.APIService;

public class SigninActivity extends BaseActivity implements ISignupView {

    @BindView(R.id.tex1)
    TextView t;
    @BindView(R.id.first_name)
    TextView firstame;
    @BindView(R.id.last_name)
    TextView lastname;
    @BindView(R.id.email)
    TextView email;
    @BindView(R.id.fonction)
    TextView fonction;
    @BindView(R.id.password)
    TextView password;
    @BindView(R.id.username)
    TextView username;
    @BindView(R.id.phone)
    TextView phone;
    Animation titre;
    @BindView(R.id.signinsubmit)
    Button signupBtn;
    @BindView(R.id.gotolog)
    Button loginBtn;
    List<Country> countries = new ArrayList<>();
    @BindView(R.id.countrySpin)
    Spinner country;
    @BindView(R.id.sexeSpin)
    Spinner sex;
    List<String> countryStr = new ArrayList<>();
    List<String> SexeStr = new ArrayList<>();
    APIService service;
    SignupPresenter signupPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        View v = getLayoutInflater().inflate(R.layout.activity_signin, null, false);
        signupPresenter = new SignupPresenter(this, v, this);
        ButterKnife.bind(this);

        final Intent i = new Intent(this, LoginActivity.class);
        final Intent i2 = new Intent(this, LoginActivity.class);

        service = APIClient.getClient().create(APIService.class);
        SexeStr.add("F");
        SexeStr.add("M");
        ArrayAdapter<String> sexAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, SexeStr);
        sex.setAdapter(sexAdapter);
//
        getCountryFromDBandFillSpinner();
        titre = AnimationUtils.loadAnimation(this, R.anim.clignotement);
        t.startAnimation(titre);
    }

    @OnClick(R.id.signinsubmit)
    public void SignupTask() {
        String cName = country.getSelectedItem().toString();
        Log.i("Country Selected", cName);
        new Thread(new Runnable() {
            @Override
            public void run() {

                int country_id = AppDatabase.getInstance(SigninActivity.this).countryDao().getByName(cName).getId();

                User u = new User();
                Profile p = new Profile();
                u.setNom(firstame.getText().toString());
                u.setPrenom(lastname.getText().toString());
                u.setEmail(email.getText().toString());
                u.setPassword(password.getText().toString());
                u.setUsername(username.getText().toString());

                p.setGender(sex.getSelectedItem().toString());
                p.setAvatar(null);
                p.setFonction(fonction.getText().toString());
                p.setMobile(phone.getText().toString());
                p.setCountry(country_id);
                u.setProfile(p);
                signupPresenter.doSignup(u);
            }
        }).start();
    }

    private void getCountryFromDBandFillSpinner() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                countries = AppDatabase.getInstance(getApplicationContext()).countryDao().getAll();
                for (Country c : countries) {
                    countryStr.add(c.getName());
                    Log.i("Country in DB::", c.getId() + "/" + c.getName());
                }
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(SigninActivity.this, android.R.layout.simple_spinner_item, countryStr);
                // Drop down layout style - list view with radio button
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                country.setAdapter(dataAdapter);
            }
        }).start();
    }

    @Override
    public void onSignupResuslt(String message) {
        Log.e("Presenter signup res:", message);
    }
}
