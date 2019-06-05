package wesicknessdect.example.org.wescanleaf.utils;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import wesicknessdect.example.org.wescanleaf.database.AppDatabase;
import wesicknessdect.example.org.wescanleaf.models.Diagnostic;
import wesicknessdect.example.org.wescanleaf.models.Picture;
import wesicknessdect.example.org.wescanleaf.models.SymptomRect;
import wesicknessdect.example.org.wescanleaf.tasks.RemoteTasks;

public class SyncReceiver extends BroadcastReceiver {
    List<SymptomRect> symptomRects;
    List<Diagnostic> diagnostics;
    List<Picture> pictures;
    public static AppDatabase DB;
    @SuppressLint("StaticFieldLeak")
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e("SYNC RECEIVER ->","RECEIVED");

        DB=AppDatabase.getInstance(context);

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
                            RemoteTasks.getInstance(context).SendOfflineDiagnostic(d, true);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (pictures != null) {
                    for (Picture p : pictures) {
                        try {
                            RemoteTasks.getInstance(context).SendDiagnosticPicture(p, true);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (symptomRects != null) {
                    for (SymptomRect s : symptomRects) {
                        Log.e("Diag::Size", symptomRects.size() + "");
                        RemoteTasks.getInstance(context).sendSymptomRect(s, true);
                    }
                }
            }
        }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

        try {
            RemoteTasks.getInstance(context).getDiagnostics(0);
        }   catch (IOException e) {
            e.printStackTrace();
        }
    }

}
