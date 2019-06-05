package wesicknessdect.example.org.wescanleaf;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import rjsv.floatingmenu.floatingmenubutton.FloatingMenuButton;
import wesicknessdect.example.org.wescanleaf.activities.BaseActivity;
import wesicknessdect.example.org.wescanleaf.activities.FinalResultActivity;
import wesicknessdect.example.org.wescanleaf.adapters.MyAdapter;
import wesicknessdect.example.org.wescanleaf.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.github.aakira.expandablelayout.ExpandableLayout;

import java.util.ArrayList;

/**
 * Created by Jordan Adopo on 10/02/2019.
 */

public class MainActivity extends BaseActivity {
    RecyclerView recyclerView;
    RecyclerView recyclerView2;
    MyAdapter myAdapter;
    MyAdapter myAdapter2;
    Button bilan;
    Button continuer;
    Button bilan2;
    FloatingMenuButton floatingButton;
    ArrayList<String> partieCacao;
    ArrayList<String> partieCafe;
    ArrayList<String> partieHevea;
    ArrayList<String> partieBanane;
    ArrayList<String> partieTomate;
    String a;
    String[] culture = new String[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        partieCacao.add("Tige");
//        partieCacao.add("Cabosse");
//        partieCacao.add("Feuille");
//        partieCacao.add("Racine");
//
//        partieCafe.add("Tige");
//        partieCafe.add("Grain");
//        partieCafe.add("Feuilles");
//
//        partieHevea.add("Feuille");
//        partieHevea.add("Tronc");
//        partieHevea.add("Racine");


        floatingButton = findViewById(R.id.my_floating_button);
        floatingButton.setRadius(200);
        rjsv.floatingmenu.floatingmenubutton.subbutton.FloatingSubButton del= findViewById(R.id.delete);
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout ll= findViewById(R.id.l1);
                ll.setVisibility(View.GONE);
            }
        });

        AlertDialog.Builder debut=new AlertDialog.Builder(MainActivity.this);
        debut.setTitle("Ensuite:")
                .setMessage("Prenez une photo de la tige")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Pix.start(MainActivity.this, Options.init().setRequestCode(100).setCount(20).setFrontfacing(true));
                    }
                })
                .setNegativeButton("Retour", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        AlertDialog.Builder milieu=new AlertDialog.Builder(MainActivity.this);
        milieu.setTitle("Ensuite:")
                .setMessage("Prenez une photo des cabosses")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Pix.start(MainActivity.this, Options.init().setRequestCode(200).setCount(20).setFrontfacing(true));
                    }
                })
                .setNegativeButton("Retour", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        // Set the dialog title
        String[] listItems = getResources().getStringArray(R.array.culture);

        builder.setTitle("Choix de la culture à analyser")
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected


                .setSingleChoiceItems(listItems,-1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        culture[0] =listItems[i];
                        a=listItems[i];

                    }
                })
                // Set the action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    private String a;

                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        // User clicked OK, so save the mSelectedItems results somewhere
                        // or return them to the component that opened the dialog
                        TextView culturename= findViewById(R.id.culturename);
                        a=culturename.getText().toString();
                        culturename.setText("Analyse de votre culture "+culture[0]);
                        AlertDialog tigeDialog=debut.create();
                        tigeDialog.show();

                    }
                })
                .setNegativeButton("Retour", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        AlertDialog mDialog = builder.create();
       mDialog.show();



//        if(culture[0]=="Cacao"){
//            Toast.makeText(MainActivity.this,"Prenez une photo de la tige",Toast.LENGTH_LONG).show();
//            Pix.start(MainActivity.this, Options.init().setRequestCode(100).setCount(20).setFrontfacing(true));
//        }
//        else {
////            Intent i= new Intent(MainActivity.this,MainActivity.class);
////            startActivity(i);
//          Pix.start(MainActivity.this, Options.init().setRequestCode(100).setCount(20).setFrontfacing(true));
//        }

//        Pix.start(MainActivity.this, Options.init().setRequestCode(100).setCount(20).setFrontfacing(true));
//        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        getSupportActionBar().setCustomView(R.layout.custom_actionbar);


        final ExpandableLayout expandableLayout
                = findViewById(R.id.expandableLayout);
        final ExpandableLayout expandableLayout2
                = findViewById(R.id.expandableLayout2);

        bilan = findViewById(R.id.Bil);

        bilan2 = findViewById(R.id.Bil2);
        Button resultat = findViewById(R.id.result);

        continuer = findViewById(R.id.suivre);
        continuer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog oups=milieu.create();
                oups.show();
            }
        });





        bilan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expandableLayout.isExpanded()){
                    expandableLayout.collapse();
                }
                else {
                    expandableLayout.expand();
                }
            }
        });
        bilan2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expandableLayout2.isExpanded()){
                    expandableLayout2.collapse();
                }
                else {
                    expandableLayout2.expand();
                }
            }
        });
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        myAdapter = new MyAdapter(this);
        recyclerView.setAdapter(myAdapter);
        recyclerView2 = findViewById(R.id.recyclerView2);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        myAdapter2 = new MyAdapter(this);
        recyclerView2.setAdapter(myAdapter2);
        findViewById(R.id.fab).setOnClickListener((View view) ->
                Pix.start(MainActivity.this, Options.init().setRequestCode(100).setCount(20).setFrontfacing(true)));

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        LinearLayout ll= findViewById(R.id.l1);
        ll.setVisibility(View.VISIBLE);
        ExpandableLayout expandableLayout
                = findViewById(R.id.expandableLayout);
        expandableLayout.collapse();
        Button bilan = findViewById(R.id.Bil);
        bilan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expandableLayout.isExpanded()){
                    expandableLayout.collapse();
                    bilan.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    bilan.setTextColor(0xEDEDEDED);

                }
                else {
                    expandableLayout.expand();
                    bilan.setBackgroundColor(0xEDEDEDED);
                    bilan.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                }
            }
        });

        ExpandableLayout expandableLayout2
                = findViewById(R.id.expandableLayout2);
        expandableLayout2.collapse();
        Button bilan2 = findViewById(R.id.Bil2);
        Button res= findViewById(R.id.result);
        res.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(MainActivity.this, FinalResultActivity.class);
                startActivity(i);
            }
        });
        bilan2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expandableLayout2.isExpanded()){
                    expandableLayout2.collapse();
                    bilan2.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    bilan2.setTextColor(0xEDEDEDED);

                }
                else {
                    expandableLayout2.expand();
                    bilan2.setBackgroundColor(0xEDEDEDED);
                    bilan2.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                }
            }
        });
        //Log.e("val", "requestCode ->  " + requestCode+"  resultCode "+resultCode);
        switch (requestCode) {
            case (100): {
                if (resultCode == Activity.RESULT_OK) {
                    ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
                    myAdapter.addImage(returnValue);
                    /*for (String s : returnValue) {
                        Log.e("val", " ->  " + s);
                    }*/
                }
            }
            case (200): {
                if (resultCode == Activity.RESULT_OK) {
                    ArrayList<String> returnValue2 = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
                    myAdapter2.addImage(returnValue2);
//                    continuer.setVisibility(View.GONE);
//                    floatingButton.setVisibility(View.GONE);
                    /*for (String s : returnValue) {
                        Log.e("val", " ->  " + s);
                    }*/
                }
            }
            break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Pix.start(MainActivity.this, Options.init().setRequestCode(100).setCount(20));
                } else {
                    Toast.makeText(MainActivity.this, "Approve permissions to open Pix ImagePicker", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
}
