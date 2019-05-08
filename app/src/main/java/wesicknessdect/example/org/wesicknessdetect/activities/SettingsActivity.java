package wesicknessdect.example.org.wesicknessdetect.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

import com.hotmail.or_dvir.easysettings.pojos.EasySettings;
import com.hotmail.or_dvir.easysettings.pojos.SettingsObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.paperdb.Paper;
import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.futuretasks.RemoteTasks;
import wesicknessdect.example.org.wesicknessdetect.models.Diagnostic;
import wesicknessdect.example.org.wesicknessdetect.models.Picture;
import wesicknessdect.example.org.wesicknessdetect.models.SymptomRect;

public class SettingsActivity extends BaseActivity {
//    @BindView(R.id.toolbar)
//    Toolbar toolbar;

    List<SymptomRect> symptomRects;
    List<Diagnostic> diagnostics;
    List<Picture> pictures;
    ArrayList<SettingsObject> settingsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        LinearLayout container = findViewById(R.id.settingsContainer);
        settingsList = Paper.book().read("SETTINGS",new ArrayList<>());

        EasySettings.inflateSettingsLayout(this, container, settingsList);
//        ButterKnife.bind(this);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        //getSupportActionBar().setLogo(R.drawable.ic_settings);
//        //getSupportActionBar().setDisplayUseLogoEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        LinearLayout container = findViewById(R.id.settingsContainer);
    }


    @Override
    public boolean onNavigateUp() {
        Intent intent = NavUtils.getParentActivityIntent(this);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        NavUtils.navigateUpTo(this, intent);
        return true;
    }

    public void StartSyncingData() {
        // Init Necessary Data
        try {
            SendOfflineDataBefore();
            Thread.sleep(1500);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        try {
            RemoteTasks.getInstance(this).getDiagnostics();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void SendOfflineDataBefore() throws IOException {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }


            @Override
            protected Void doInBackground(Void... voids) {
                Log.e("Pre Task", "Started");
                diagnostics = DB.diagnosticDao().getNotSendedSync();
                pictures = DB.pictureDao().getNotSendedSync();
                symptomRects = DB.symptomRectDao().getNotSendedSync();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (diagnostics != null) {
                    for (Diagnostic d : diagnostics) {
                        Log.e("Diag::Size", diagnostics.size() + "");
                        try {
                            RemoteTasks.getInstance(getApplicationContext()).SendOfflineDiagnostic(d, true);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (pictures != null) {
                    for (Picture p : pictures) {
                        try {
                            RemoteTasks.getInstance(getApplicationContext()).SendDiagnosticPicture(p, true);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (symptomRects != null) {
                    for (SymptomRect s : symptomRects) {
                        Log.e("Diag::Size", symptomRects.size() + "");
                        RemoteTasks.getInstance(getApplicationContext()).sendSymptomRect(s, true);
                    }
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
