package wesicknessdect.example.org.wesicknessdetect.activities;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.appizona.yehiahd.fastsave.FastSave;
import com.bumptech.glide.Glide;
import com.downloader.PRDownloader;
import com.downloader.Status;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Completable;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.events.DataSizeEvent;
import wesicknessdect.example.org.wesicknessdetect.events.GetFileProgressEvent;
import wesicknessdect.example.org.wesicknessdetect.events.ModelDownloadEvent;
import wesicknessdect.example.org.wesicknessdetect.events.ShowProcessScreenEvent;
import wesicknessdect.example.org.wesicknessdetect.models.Model;
import wesicknessdect.example.org.wesicknessdetect.models.Picture;
import wesicknessdect.example.org.wesicknessdetect.models.Profile;
import wesicknessdect.example.org.wesicknessdetect.models.User;
import wesicknessdect.example.org.wesicknessdetect.tasks.RemoteTasks;
import wesicknessdect.example.org.wesicknessdetect.utils.DownloadService;


public class RestoreDataActivity extends BaseActivity {
    @BindView(R.id.txtSize)
    TextView sizeTxt;

    @BindView(R.id.btnRestore)
    Button btnRestore;

    @BindView(R.id.btnPass)
    Button btnPass;

    @BindView(R.id.imgPkg)
    ImageView pkgImg;

    @BindView(R.id.progress)
    ProgressBar progressBar;

    RemoteTasks remoteTasks;
    DownloadManager dm;

    long totalBytes = 0;
    long progress = 0;

    Set<Integer> downloadIds = new HashSet<>();

    @Override
    public void onStart() {
        super.onStart();
        remoteTasks = RemoteTasks.getInstance(getApplicationContext());
        remoteTasks.getDiagnostics(0);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restore_data_layout);
        ButterKnife.bind(this);

        dm=(DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        Glide.with(getApplicationContext())
                .asBitmap()
                .load(Uri.parse("file:///android_asset/package.png"))
                .into(pkgImg);

        String size = FastSave.getInstance().getString("size", "0.0 Mo");
        sizeTxt.setText(size);
        progressBar.setMax((int) totalBytes);
        progressBar.setProgress((int) progress);
    }

    @OnClick(R.id.btnPass)
    void goToProcessActivity() {
        EventBus.getDefault().post(new ShowProcessScreenEvent("From Restore"));
    }

    @SuppressLint("CheckResult")
    @OnClick(R.id.btnRestore)
    void doRestoreData() {
        progressBar.setVisibility(View.VISIBLE);

        DB.profileDao()
                .rxGetAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Profile>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<Profile> profiles) {
                        for (Profile u : profiles) {
                            Uri uri = Uri.parse(u.getAvatar());
                            String destination = getExternalFilesDir(null).getPath() + File.separator;
                            File f = new File(destination + uri.getLastPathSegment());
                            startService(DownloadService.getDownloadService(getApplicationContext(), u.getAvatar(), 40000));
                            Completable.fromAction(()->{
                                u.setAvatar(destination + uri.getLastPathSegment());
                                DB.profileDao().update(u);
                            })
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(()->{},throwable -> {});
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });

        DB.pictureDao()
                .rxGetAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Picture>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<Picture> pictureList) {
                        for (Picture p : pictureList) {
                            Uri uri = Uri.parse(p.getImage());
                            String destination = getExternalFilesDir(null).getPath() + File.separator;
                            File f = new File(destination + uri.getLastPathSegment());
                            if(!f.exists()){
                                //startService(DownloadService.getDownloadService(getApplicationContext(), p.getImage(), 40000));
                                downloadFile(p.getImage());
                            }
                            Completable.fromAction(()->{
                                p.setImage(destination + uri.getLastPathSegment());
                                DB.pictureDao().updatePicture(p);
                            })
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(()->{},throwable -> {});
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });

        DB.modelDao().rxGetAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<Model>>() {
                    @Override
                    public void onSuccess(List<Model> modelList) {
                        for (Model m : modelList) {
                            Uri model_uri = Uri.parse(modelList.get(0).getPb());
                            Uri label_uri = Uri.parse(modelList.get(0).getLabel());

                            String destination = Objects.requireNonNull(getExternalFilesDir(null)).getPath() + File.separator;

                            String modelpath = destination + model_uri.getLastPathSegment();
                            String label_path = destination + label_uri.getLastPathSegment();

                            File fmodel = new File(modelpath);
                            File flabel = new File(label_path);

                            if(!flabel.exists()){
                                //startService(DownloadService.getDownloadService(getApplicationContext(), m.getLabel(), 40000));
                                downloadFile(m.getLabel());
                            }

                            if(!fmodel.exists()){
                                //startService(DownloadService.getDownloadService(getApplicationContext(), m.getPb(), 40000));
                                downloadFile(m.getPb());
                            }

                            Completable.fromAction(()->{
                                m.setPb(fmodel.getAbsolutePath());
                                m.setLabel(flabel.getAbsolutePath());
                                DB.modelDao().updateModel(m);
                            })
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(()->{},throwable -> {});

                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });

        btnPass.setVisibility(View.GONE);
    }

    //Hide the loading dialog
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onDataSizeEvent(DataSizeEvent event) {
        sizeTxt.setText(event.size);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onDownloadProgress(ModelDownloadEvent event) {
        Log.d("Restore Data 0 ->", event.downloadId + "");
        Log.d("Restore Data 0 ->", event.part_id + "");
        Log.d("Restore Data 0 ->", downloadIds.size() + "");
        downloadIds.add(event.downloadId);
        if (event.part_id == 40000) {
            for (Integer n : downloadIds) {
                if (n != event.downloadId) {
                    downloadIds.add(event.downloadId);
                    totalBytes = totalBytes + event.filesize;
                    Log.d("Restore Data 0->", event.downloadId + "");
                }
            }
            progress = progress + event.downloaded;
            progressBar.setMax((int) totalBytes);
            progressBar.setProgress((int) progress);
            Log.d("Restore TotalSize ->", totalBytes + "");
        }
    }

    public void downloadFile(String url) {
        DownloadManager.Request request1 = new DownloadManager.Request(Uri.parse(url));
        Uri uri = Uri.parse(url);
        String destination = Objects.requireNonNull(getExternalFilesDir(null)).getPath() + File.separator;
        request1.setVisibleInDownloadsUi(false);
        request1.setDestinationInExternalFilesDir(getApplicationContext(),destination,uri.getLastPathSegment());
        request1.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);

        long id = dm.enqueue(request1);

        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(id);
        Cursor c = dm.query(query);
        if (c.moveToFirst()) {
            int totalSize = c.getInt(c
                    .getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
            int downloadedSize = c.getInt(c
                    .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
            Log.d("Total size", totalSize + "");
            Log.d("Download size", downloadedSize + "");
            //final double dl_progress = (downloadedSize / totalSize) * 100;
            c.close();
        }

        if (DownloadManager.STATUS_SUCCESSFUL == 8) {

        }
    }


}
