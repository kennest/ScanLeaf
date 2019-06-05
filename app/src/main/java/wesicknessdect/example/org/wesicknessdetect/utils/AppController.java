package wesicknessdect.example.org.wesicknessdetect.utils;

import android.annotation.SuppressLint;
import android.app.Application;
import android.net.Uri;
import android.os.AsyncTask;

import com.appizona.yehiahd.fastsave.FastSave;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.paperdb.Paper;
import wesicknessdect.example.org.wesicknessdetect.activities.tensorflow.Classifier;
import wesicknessdect.example.org.wesicknessdetect.database.AppDatabase;
import wesicknessdect.example.org.wesicknessdetect.models.Picture;
import wesicknessdect.example.org.wesicknessdetect.models.SymptomRect;
import wesicknessdect.example.org.wesicknessdetect.tasks.RemoteTasks;

public class AppController extends Application {
    private static final String DATABASE_NAME = "scanleaf.db";
    public static AppDatabase appDatabase;
    private static AppController mInstance;
    private static RemoteTasks remoteTasks;
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

        remoteTasks=RemoteTasks.getInstance(getApplicationContext());

        //RemoteTasks.getInstance(getApplicationContext()).DownloadFile("https://banner2.kisspng.com/20180409/vgq/kisspng-leaf-logo-brand-plant-stem-folha-5acb0798d686f9.0092563815232551928787.jpg");

        //Delete the Database
        //getApplicationContext().deleteDatabase(getApplicationContext().getExternalFilesDir(null).getPath()+ File.separator+DATABASE_NAME);

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
    public void InitDBFromServer(){

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                remoteTasks.DownloadFile("https://banner2.kisspng.com/20180719/kjw/kisspng-cacao-tree-chocolate-polyphenol-cocoa-bean-catechi-wt-5b50795abb1c16.1156862915320006027664.jpg");

                Uri model_uri = Uri.parse("http://178.33.130.202:8000/media/models/check_cacao.lite");
                Uri label_uri = Uri.parse("http://178.33.130.202:8000/media/models/check_cacao.txt");
                String destination = Objects.requireNonNull(getBaseContext().getExternalFilesDir(null)).getPath() + File.separator;

                String modelpath = destination + model_uri.getLastPathSegment();
                String label_path = destination + label_uri.getLastPathSegment();

                FastSave.getInstance().saveString("check_model",modelpath);
                FastSave.getInstance().saveString("check_label",label_path);


                File fmodel = new File(modelpath);
                File flabel = new File(label_path);

                if (!fmodel.exists()) {
                    remoteTasks.DownloadFile("http://178.33.130.202:8000/media/models/check_cacao.lite");
                }

                if (!flabel.exists()) {
                    remoteTasks.DownloadFile("http://178.33.130.202:8000/media/models/check_cacao.txt");
                }



                //Init all needed data
                try {
                    remoteTasks.getCultures();
                    remoteTasks.getCountries();
                    remoteTasks.getCulturePart(1);
                    remoteTasks.getQuestions();
                    remoteTasks.getSymptoms();
                    remoteTasks.getStruggles();
                    remoteTasks.getDiseases();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }
}
