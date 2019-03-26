package wesicknessdect.example.org.wesicknessdetect.utils;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.AsyncTask;

import com.appizona.yehiahd.fastsave.FastSave;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import wesicknessdect.example.org.wesicknessdetect.database.AppDatabase;
import wesicknessdect.example.org.wesicknessdetect.futuretasks.RemoteTasks;
import wesicknessdect.example.org.wesicknessdetect.models.Culture;
import wesicknessdect.example.org.wesicknessdetect.models.CulturePart;
import wesicknessdect.example.org.wesicknessdetect.models.Model;

public class AppController extends Application {
    private static final String DATABASE_NAME = "wesickness.db";
    public AppDatabase appDatabase;
    private static AppController mInstance;
    List<CulturePart> culturePartList=new ArrayList<>();

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //Init FastSave
        FastSave.init(getApplicationContext());

        //Init PRDownloader
        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setDatabaseEnabled(true)
                .build();
        PRDownloader.initialize(getApplicationContext(), config);

        RemoteTasks.getInstance(getApplicationContext()).DownloadFile("https://banner2.kisspng.com/20180409/vgq/kisspng-leaf-logo-brand-plant-stem-folha-5acb0798d686f9.0092563815232551928787.jpg",101);

        //Delete the Database
        getApplicationContext().deleteDatabase(DATABASE_NAME);

        //Create the database
        appDatabase = AppDatabase.getInstance(getApplicationContext());

        //Create data
        CreateData();
    }

    @SuppressLint("StaticFieldLeak")
    private void CreateData(){

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Culture c=new Culture();
                c.setName("CACAO");
                c.setImage("");
                c.setNbParties(5);
                c.setNomModele("");
                appDatabase.cultureDao().createCulture(c);

                //Init all needed data
                try {
                    RemoteTasks.getInstance(getApplicationContext()).getCulturePart(1);
                    RemoteTasks.getInstance(getApplicationContext()).getQuestions();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }
}
