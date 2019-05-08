package wesicknessdect.example.org.wesicknessdetect.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import java.io.IOException;
import java.util.List;

import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.database.AppDatabase;
import wesicknessdect.example.org.wesicknessdetect.futuretasks.RemoteTasks;
import wesicknessdect.example.org.wesicknessdetect.models.Diagnostic;
import wesicknessdect.example.org.wesicknessdetect.models.Picture;
import wesicknessdect.example.org.wesicknessdetect.models.SymptomRect;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    private AppDatabase DB;
    List<SymptomRect> symptomRects;
    List<Diagnostic> diagnostics;
    List<Picture> pictures;
    public SettingsFragment(AppDatabase DB) {
        this.DB = DB;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.pref_vizualizer, rootKey);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("number")) {
            Preference preference = findPreference(key);
            if (preference instanceof EditTextPreference){
                EditTextPreference editTextPreference =  (EditTextPreference)preference;
                if (editTextPreference.getText().trim().length() > 0){
                    editTextPreference.setSummary("Nombre  " + editTextPreference.getText());
                }else{
                    editTextPreference.setSummary("Pas de nombre");
                }
            }
        } else if (key.equals("synchronisation")) {
           StartSyncingData();
            Preference preference = findPreference(key);
            if (preference instanceof SwitchPreference){
                SwitchPreference switchPreference =  (SwitchPreference) preference;
               if(switchPreference.isChecked()){
                   StartSyncingData();
               }
            }
        }
    }

    public void StartSyncingData(){
        // Init Necessary Data
        try {
            SendOfflineDataBefore();
            Thread.sleep(1500);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        try {
            RemoteTasks.getInstance(getContext()).getDiagnostics();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void SendOfflineDataBefore() throws IOException{

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
                            RemoteTasks.getInstance(getContext()).SendOfflineDiagnostic(d,true);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if(pictures!=null){
                    for(Picture p:pictures){
                        try {
                            RemoteTasks.getInstance(getContext()).SendDiagnosticPicture(p,true);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if(symptomRects!=null){
                    for(SymptomRect s:symptomRects){
                        Log.e("Diag::Size", symptomRects.size() + "");
                        RemoteTasks.getInstance(getContext()).sendSymptomRect(s,true);
                    }
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
