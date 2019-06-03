package wesicknessdect.example.org.wesicknessdetect.utils;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import wesicknessdect.example.org.wesicknessdetect.database.AppDatabase;
import wesicknessdect.example.org.wesicknessdetect.tasks.RemoteTasks;
import wesicknessdect.example.org.wesicknessdetect.models.Diagnostic;
import wesicknessdect.example.org.wesicknessdetect.models.Picture;
import wesicknessdect.example.org.wesicknessdetect.models.SymptomRect;

public class SyncReceiver extends BroadcastReceiver {
    List<SymptomRect> symptomRects;
    List<Diagnostic> diagnostics;
    List<Picture> pictures;
    public static AppDatabase DB;

    @SuppressLint({"StaticFieldLeak", "CheckResult"})
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e("SYNC RECEIVER ->", "RECEIVED");
        DB = AppDatabase.getInstance(context);
        Completable.fromAction(() -> {
            diagnostics = DB.diagnosticDao().getNotSendedSync();
            pictures = DB.pictureDao().getNotSendedSync();
            symptomRects = DB.symptomRectDao().getNotSendedSync();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                            if (diagnostics != null) {
                                for (Diagnostic d : diagnostics) {
                                    Log.e("Diag::Size", diagnostics.size() + "");
                                        RemoteTasks.getInstance(context).SendOfflineDiagnostic(d, true);
                                }
                            }

                            if (pictures != null) {
                                for (Picture p : pictures) {
                                        RemoteTasks.getInstance(context).SendDiagnosticPicture(p, true);
                                }
                            }

                            if (symptomRects != null) {
                                for (SymptomRect s : symptomRects) {
                                    Log.e("Diag::Size", symptomRects.size() + "");
                                    RemoteTasks.getInstance(context).sendSymptomRect(s, true);
                                }
                            }
                            RemoteTasks.getInstance(context).getDiagnostics(0);
                        },// completed with success,
                        throwable -> throwable.printStackTrace()// there was an error
                );
    }

}
