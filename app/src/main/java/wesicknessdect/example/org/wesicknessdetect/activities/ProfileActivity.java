package wesicknessdect.example.org.wesicknessdetect.activities;

import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.activities.register.SignupActivity;
import wesicknessdect.example.org.wesicknessdetect.database.AppDatabase;
import wesicknessdect.example.org.wesicknessdetect.models.Country;
import wesicknessdect.example.org.wesicknessdetect.models.DiagnosticPictures;
import wesicknessdect.example.org.wesicknessdetect.models.Profile;
import wesicknessdect.example.org.wesicknessdetect.models.User;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends BaseActivity {

    public List<User> user= null;
    public List<Profile>profil =null;
    String username,pseudo,userEmail,Pays;
    TextView nom,pseudon,email,pays, nbAnalyses,nbDetect;
    Button analyseView, modifyProf;
    ImageView pI;
    Country country;
    String nbAna, nbDete;
    List<String> countryStr = new ArrayList<>();
    Spinner countri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        nom=(TextView) findViewById(R.id.userNom);
        pseudon=(TextView) findViewById(R.id.userFonction);
        email=(TextView) findViewById(R.id.userEmail);
        pI=(ImageView) findViewById(R.id.userImage);
        pays=(TextView) findViewById(R.id.userPays);
        nbAnalyses=(TextView)findViewById(R.id.nbAnalyses);
        nbDetect=(TextView)findViewById(R.id.nbDetect);
        analyseView=(Button) findViewById(R.id.voirAnalyses);
        modifyProf=(Button) findViewById(R.id.modifyProfil);

        nbAna=DB.diagnosticDao().getAllSync().size()+"";
        modifyProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.ediit_profil, null);
                //layout_root should be the name of the "top-level" layout node in the dialog_layout.xml file.
                final EditText nameBox = (EditText) layout.findViewById(R.id.userNewNom);
                final EditText surnameBox = (EditText) layout.findViewById(R.id.userNewPrenoms);
                final EditText pseudoBox = (EditText) layout.findViewById(R.id.userNewPseudo);
                final EditText emailBox = (EditText) layout.findViewById(R.id.userNewEmail);
                countri= layout.findViewById(R.id.userNewPays);
                getCountryFromDBandFillSpinner();
                //Building dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setView(layout);
                builder.setPositiveButton("Confirmer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //save info where you want it
                    }
                });
                builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();


            }
        });
        analyseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getBaseContext(), ProcessActivity.class);
                startActivity(intent);
            }
        });
        nbDete=DB.postDao().getAllPost().size()+"";
        AsyncTask.SERIAL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                profil=DB.profileDao().getProfil();
                user=  DB.userDao().getAll();
                country=DB.countryDao().getById(profil.get(0).getCountry_id());
                Log.d("userDAO", user.toString());
                username=user.get(0).getPrenom()+" "+user.get(0).getNom();
                pseudo=user.get(0).getUsername();
                userEmail=user.get(0).getEmail();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        nom.setText(username);
                        pseudon.setText(pseudo);
                        email.setText(userEmail);
                        pays.setText(country.getName());
                        pI.setImageDrawable(getResources().getDrawable(R.drawable.user_icon));
                        nbAnalyses.setText(nbAna);
                        nbDetect.setText(nbDete);
                    }
                });

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
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ProfileActivity.this, android.R.layout.simple_spinner_item, countryStr);
                // Drop down layout style - list view with radio button
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                countri.setAdapter(dataAdapter);
            }
        });

    }
}
