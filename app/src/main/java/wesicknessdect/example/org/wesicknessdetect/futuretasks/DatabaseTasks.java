package wesicknessdect.example.org.wesicknessdetect.futuretasks;

import android.content.Context;

import java.util.List;

import androidx.room.Transaction;
import wesicknessdect.example.org.wesicknessdetect.database.AppDatabase;
import wesicknessdect.example.org.wesicknessdetect.models.Diagnostic;
import wesicknessdect.example.org.wesicknessdetect.models.Picture;

public class DatabaseTasks {
    private static DatabaseTasks databaseTasks;
    //private static APIService service;
    private static AppDatabase DB;
    private static Context mContext;

    private DatabaseTasks(Context context) {
        mContext = context;
    }  //private constructor.

    public static DatabaseTasks getInstance(Context context) {
        if (databaseTasks == null) { //if there is no instance available... create new one
            databaseTasks = new DatabaseTasks(context);
        }
        mContext = context;
        //service = APIClient.getClient().create(APIService.class);
        DB = AppDatabase.getInstance(mContext);
        return databaseTasks;
    }

    //INSERT DIAGNOSTIC AND PICTURES
    @Transaction
    private void DiagWithPic(Diagnostic d, List<Picture> pictures){
        DB.diagnosticDao().createDiagnostic(d);
        for(Picture p:pictures) {
            DB.pictureDao().createPicture(p);
        }
    }
}
