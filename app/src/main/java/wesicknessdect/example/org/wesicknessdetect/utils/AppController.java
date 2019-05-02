package wesicknessdect.example.org.wesicknessdetect.utils;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import com.appizona.yehiahd.fastsave.FastSave;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.paperdb.Paper;
import wesicknessdect.example.org.wesicknessdetect.activities.tensorflow.Classifier;
import wesicknessdect.example.org.wesicknessdetect.database.AppDatabase;
import wesicknessdect.example.org.wesicknessdetect.futuretasks.RemoteTasks;
import wesicknessdect.example.org.wesicknessdetect.models.Culture;
import wesicknessdect.example.org.wesicknessdetect.models.CulturePart;
import wesicknessdect.example.org.wesicknessdetect.models.Picture;
import wesicknessdect.example.org.wesicknessdetect.models.SymptomRect;

public class AppController extends Application {
    private static final String DATABASE_NAME = "scanleaf.db";
    public static AppDatabase appDatabase;
    private static AppController mInstance;
    @SuppressLint("UseSparseArrays")
    public Map<Integer, List<Classifier.Recognition>> recognitions_by_part = new HashMap<>();
    public List<SymptomRect> symptomsRects = new ArrayList<>();
    public List<Picture> pictures = new ArrayList<>();

    public static synchronized AppController getInstance() {
        if (mInstance == null) { //if there is no instance available... create new one
            mInstance = new AppController();
        }
        //service = APIClient.getClient().create(APIService.class);
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //Init FastSave
        FastSave.init(getApplicationContext());

        //Init PaperDb
        Paper.init(getApplicationContext());

        //Init PRDownloader
        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setDatabaseEnabled(true)
                .build();
        PRDownloader.initialize(getApplicationContext(), config);


        //RemoteTasks.getInstance(getApplicationContext()).DownloadFile("https://banner2.kisspng.com/20180409/vgq/kisspng-leaf-logo-brand-plant-stem-folha-5acb0798d686f9.0092563815232551928787.jpg");

        //Delete the Database
        getApplicationContext().deleteDatabase(getApplicationContext().getExternalFilesDir(null).getPath()+ File.separator+DATABASE_NAME);

        //Create the database
        //appDatabase = AppDatabase.getInstance(getApplicationContext());

        //Create data
        InitDBFromServer();
    }

    public List<SymptomRect> getSymptomsRects() {
        return symptomsRects;
    }

    public void setSymptomsRects(List<SymptomRect> symptomsRects) {
        this.symptomsRects = symptomsRects;
    }

    public Map<Integer, List<Classifier.Recognition>> getRecognitions_by_part() {
        return recognitions_by_part;
    }

    public void setRecognitions_by_part(Map<Integer, List<Classifier.Recognition>> recognitions_by_part) {
        this.recognitions_by_part = recognitions_by_part;
    }

    public List<Picture> getPictures() {
        return pictures;
    }

    public void setPictures(List<Picture> pictures) {
        this.pictures = pictures;
    }

    @SuppressLint("StaticFieldLeak")
    private void InitDBFromServer(){

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                RemoteTasks.getInstance(getApplicationContext()).DownloadFile("https://banner2.kisspng.com/20180719/kjw/kisspng-cacao-tree-chocolate-polyphenol-cocoa-bean-catechi-wt-5b50795abb1c16.1156862915320006027664.jpg");

                //Init all needed data
                try {
                    RemoteTasks.getInstance(getApplicationContext()).getCultures();
                    RemoteTasks.getInstance(getApplicationContext()).getCountries();
                    RemoteTasks.getInstance(getApplicationContext()).getCulturePart(1);
                    RemoteTasks.getInstance(getApplicationContext()).getQuestions();
                    RemoteTasks.getInstance(getApplicationContext()).getSymptoms();
                    RemoteTasks.getInstance(getApplicationContext()).getStruggles();
                    RemoteTasks.getInstance(getApplicationContext()).getDiseases();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }
}
