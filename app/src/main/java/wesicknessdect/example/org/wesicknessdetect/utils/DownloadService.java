package wesicknessdect.example.org.wesicknessdetect.utils;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.system.ErrnoException;
import android.util.Log;

import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import wesicknessdect.example.org.wesicknessdetect.events.ModelDownloadEvent;
import wesicknessdect.example.org.wesicknessdetect.events.ShowLoadingEvent;

public class DownloadService extends IntentService {
    private static final String DOWNLOAD_PATH = "downloadpath";
    private static final String PART_ID = "part_id";
    int downloadId = 0;
    long currentBytes = 0;
    long totalBytes = 0;

    public DownloadService() {
        super("DownloadSongService");
    }

    public static Intent getDownloadService(final @NonNull Context callingClassContext, final @NonNull String downloadPath, final @Nullable int part_id) {
        return new Intent(callingClassContext, DownloadService.class)
                .putExtra(DOWNLOAD_PATH, downloadPath)
                .putExtra(PART_ID, part_id);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String downloadPath = intent.getStringExtra(DOWNLOAD_PATH);
        int part_id = intent.getIntExtra(PART_ID,0);
        try {
            startDownload(getApplicationContext(), downloadPath, part_id);
        }catch (ErrnoException e){
                EventBus.getDefault().post(new ShowLoadingEvent("No Space Left", "Please Free Memory", true,0));
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        return super.onStartCommand(intent, flags, startId);
    }

    public void startDownload(Context context,String url,@Nullable int part_id) throws ErrnoException {
        if (Constants.isOnline(context)) {
            Uri uri = Uri.parse(url);
            String destination = Objects.requireNonNull(context.getExternalFilesDir(null)).getPath() + File.separator;
            Log.e("PATHS X", destination + uri.getLastPathSegment());

            downloadId = PRDownloader.download(url, destination, uri.getLastPathSegment())
                    .build()
                    .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                        @Override
                        public void onStartOrResume() {

                        }
                    })
                    .setOnPauseListener(new OnPauseListener() {
                        @Override
                        public void onPause() {

                        }
                    })
                    .setOnCancelListener(new OnCancelListener() {
                        @Override
                        public void onCancel() {

                        }
                    })
                    .setOnProgressListener(new OnProgressListener() {
                        @Override
                        public void onProgress(Progress progress) {
                            Log.d(url, progress.currentBytes + "/" + progress.totalBytes);
                            currentBytes = progress.currentBytes;
                            totalBytes = progress.totalBytes;
                            EventBus.getDefault().post(new ModelDownloadEvent(progress.currentBytes, progress.totalBytes, part_id));
                        }
                    })
                    .start(new OnDownloadListener() {
                        @Override
                        public void onDownloadComplete() {
                            Log.d(url, "Finished::" + uri.getLastPathSegment());
                            EventBus.getDefault().post(new ModelDownloadEvent(totalBytes, totalBytes, part_id));
                        }

                        @Override
                        public void onError(Error error) {

                        }

                    });

//            FastSave.getInstance().saveObjectsList(Constants.DOWNLOAD_IDS, downloadID);
        } else {
            //Dispatch show loading event
            EventBus.getDefault().post(new ShowLoadingEvent("Erreur", "Vous n'etes pas connecter a internet", true,0));
        }
    }
}
