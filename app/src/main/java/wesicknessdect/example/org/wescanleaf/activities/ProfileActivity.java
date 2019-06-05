package wesicknessdect.example.org.wescanleaf.activities;

import wesicknessdect.example.org.wescanleaf.models.Country;
import wesicknessdect.example.org.wescanleaf.models.Profile;
import wesicknessdect.example.org.wescanleaf.models.User;
import wesicknessdect.example.org.wescanleaf.tasks.RemoteTasks;
import wesicknessdect.example.org.wescanleaf.R;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.lifecycle.Observer;

import com.fxn.pix.Options;
import com.fxn.pix.Pix;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends BaseActivity {

    public List<User> user = null;
    public List<Profile> profil = null;
    String username, pseudo, userEmail, Pays;
    TextView nom, pseudon, email, pays, nbAnalyses, nbDetect;
    Button analyseView, modifyProf, logout;
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
    File avatar_file=null;
    Country c = new Country();
    View layout;
    AlertDialog dialog = null;
    AlertDialog.Builder builder = null;

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
        logout = findViewById(R.id.logout);


        analyseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ProcessActivity.class);
                startActivity(intent);
            }
        });

        AsyncTask.SERIAL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                nbAna = DB.diagnosticDao().getAllSync().size() + "";
                nbDete = DB.postDao().getAllPost().size() + "";
                profil = DB.profileDao().getProfil();
                user = DB.userDao().getAll();
                country = DB.countryDao().getById(profil.get(0).getCountry_id());


                username = user.get(0).getPrenom() + " " + user.get(0).getNom();
                pseudo = user.get(0).getUsername();
                userEmail = user.get(0).getEmail();



                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        nom.setText(username);
                        pseudon.setText(pseudo);
                        email.setText(userEmail);
                        pays.setText(country.getName());
                        if(profil.get(0).getAvatar()!=null) {
                            avatar_file = new File(profil.get(0).getAvatar());
                            if(avatar_file!=null) {
                                if (!avatar_file.exists()) {
                                    Log.e("Avatar Path ->", profil.get(0).getAvatar());
                                    pI.setImageDrawable(getResources().getDrawable(R.drawable.ic_person_add));
                                } else {
                                    Log.e("Avatar Path ->", profil.get(0).getAvatar());
                                    pI.setImageBitmap(BitmapFactory.decodeFile(profil.get(0).getAvatar()));
                                }
                            }
                        }

                        nbAnalyses.setText(nbAna);
                        nbDetect.setText(nbDete);
                        modifyProf.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                //Building dialog
                                builder = new AlertDialog.Builder(ProfileActivity.this);
                                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                                View layout = inflater.inflate(R.layout.ediit_profil, null, true);
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
                                        Intent current=getIntent();
                                        finish();
                                        current.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(current);
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


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Attention");
                builder.setMessage("Voulez-vous deconnecter le compte courant et supprimer les données?");
                // Add the buttons
                builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        deleteCache(getApplicationContext());
                        restartApp();
                    }
                });
                builder.setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dialog.dismiss();
                    }
                });

                // Create the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();

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
        DB.countryDao().getAll().observe(this, new Observer<List<Country>>() {
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
                countri.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        c = DB.countryDao().getByNameSync(countri.getItemAtPosition(position).toString());
                        profil.get(0).setCountry_id(c.getId());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        });
    }


}
