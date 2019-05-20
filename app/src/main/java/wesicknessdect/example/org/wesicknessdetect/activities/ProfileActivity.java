package wesicknessdect.example.org.wesicknessdetect.activities;

import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.database.AppDatabase;
import wesicknessdect.example.org.wesicknessdetect.models.Country;
import wesicknessdect.example.org.wesicknessdetect.models.Profile;
import wesicknessdect.example.org.wesicknessdetect.models.User;
import wesicknessdect.example.org.wesicknessdetect.tasks.RemoteTasks;
import wesicknessdect.example.org.wesicknessdetect.utils.EncodeBase64;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.lifecycle.Observer;

import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.ImageQuality;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends BaseActivity {

    public List<User> user = null;
    public List<Profile> profil = null;
    String username, pseudo, userEmail, Pays;
    TextView nom, pseudon, email, pays, nbAnalyses, nbDetect;
    Button analyseView, modifyProf;
    ImageView pI;
    Country country;
    String nbAna, nbDete;
    List<String> countryStr = new ArrayList<>();
    Spinner countri;
    private int RequestCode = 100;
    String path;

    ImageView imageBox;
    EditText nameBox;
    EditText surnameBox;
    EditText pseudoBox;
    EditText emailBox;
    String password;
    int id;
    String chemin;
    Country c = new Country();
    View layout;
    AlertDialog dialog=null;
    AlertDialog.Builder builder=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profil);
        nom = (TextView) findViewById(R.id.userNom);
        pseudon = (TextView) findViewById(R.id.userFonction);
        email = (TextView) findViewById(R.id.userEmail);
        pI = (ImageView) findViewById(R.id.userImage);
        pays = (TextView) findViewById(R.id.userPays);
        nbAnalyses = (TextView) findViewById(R.id.nbAnalyses);
        nbDetect = (TextView) findViewById(R.id.nbDetect);
        analyseView = (Button) findViewById(R.id.voirAnalyses);
        modifyProf = (Button) findViewById(R.id.modifyProfil);

        nbAna = DB.diagnosticDao().getAllSync().size() + "";

        analyseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ProcessActivity.class);
                startActivity(intent);
            }
        });

        nbDete = DB.postDao().getAllPost().size() + "";

        AsyncTask.SERIAL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                profil = DB.profileDao().getProfil();
                user = DB.userDao().getAll();
                country = DB.countryDao().getById(profil.get(0).getCountry_id());
                Log.d("userDAO", user.toString());
                username = user.get(0).getPrenom() + " " + user.get(0).getNom();
                pseudo = user.get(0).getUsername();
                userEmail = user.get(0).getEmail();

                if (profil.get(0).getAvatar() == null) {
                    chemin = "rien";
                } else {
                    chemin = profil.get(0).getAvatar();
                }


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        nom.setText(username);
                        pseudon.setText(pseudo);
                        email.setText(userEmail);
                        pays.setText(country.getName());
                        if (chemin.equals("rien") || chemin.equals("")) {
                            pI.setImageDrawable(getResources().getDrawable(R.drawable.ic_person_add));
                        } else {
                            pI.setImageBitmap(BitmapFactory.decodeFile(chemin));
                        }
                        nbAnalyses.setText(nbAna);
                        nbDetect.setText(nbDete);
                        modifyProf.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                //Building dialog
                                builder = new AlertDialog.Builder(ProfileActivity.this);
                                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                                View layout = inflater.inflate(R.layout.ediit_profil, null,true);
                                //layout_root should be the name of the "top-level" layout node in the dialog_layout.xml file.
                                nameBox = layout.findViewById(R.id.userNewNom);
                                countri = layout.findViewById(R.id.userNewPays);
                                surnameBox = layout.findViewById(R.id.userNewPrenoms);
                                pseudoBox = layout.findViewById(R.id.userNewPseudo);
                                emailBox = layout.findViewById(R.id.userNewEmail);
                                imageBox = layout.findViewById(R.id.userNewImage);
                                builder.setView(layout);

                                nameBox.setText(user.get(0).getNom());
                                surnameBox.setText(user.get(0).getPrenom());
                                pseudoBox.setText(pseudo);
                                id = user.get(0).getId();
                                emailBox.setText(userEmail);

                                imageBox.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Pix.start(ProfileActivity.this, Options.init()
                                                .setRequestCode(RequestCode)                                                 //Request code for activity results
                                                .setCount(1));
                                    }
                                });

                                getCountryFromDBandFillSpinner();

                                builder.setPositiveButton("Confirmer", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface d, int which) {

                                        AsyncTask.SERIAL_EXECUTOR.execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                c = DB.countryDao().getByNameSync(countri.getSelectedItem().toString());
                                                user.get(0).setNom(nameBox.getText().toString());
                                                user.get(0).setPrenom(surnameBox.getText().toString());
                                                user.get(0).setEmail(email.getText().toString());
                                                user.get(0).setPassword(password);
                                                user.get(0).setUsername(pseudoBox.getText().toString());
                                                if (path != null) {
                                                    if (path.equals("")) {
                                                        path = "rien";
                                                    }
                                                    profil.get(0).setAvatar(path);
                                                }
                                                profil.get(0).setCountry_id(c.getId());
                                                user.get(0).setProfile(profil.get(0));

                                                try {
                                                    RemoteTasks.getInstance(ProfileActivity.this).SendUpdatedUser(path, user.get(0), profil.get(0));
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                    }
                                });

                                builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface d, int which) {
                                        d.dismiss();
                                    }
                                });
                                dialog = builder.create();
                                dialog.show();
                            }
                        });
                    }
                });

            }
        });




    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == RequestCode) {
            ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            path = returnValue.get(0);
            imageBox.setImageBitmap(BitmapFactory.decodeFile(path));
            pI.setImageBitmap(BitmapFactory.decodeFile(path));
        }
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

    private void InitDialogView() {


    }

}
