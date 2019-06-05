package wesicknessdect.example.org.wesicknessdetect.database;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.io.File;

import wesicknessdect.example.org.wesicknessdetect.database.dao.CountryDao;
import wesicknessdect.example.org.wesicknessdetect.database.dao.CultureDao;
import wesicknessdect.example.org.wesicknessdetect.database.dao.CulturePartsDao;
import wesicknessdect.example.org.wesicknessdetect.database.dao.DiagnosticDao;
import wesicknessdect.example.org.wesicknessdetect.database.dao.DiseaseDao;
import wesicknessdect.example.org.wesicknessdetect.database.dao.DiseaseSymptomsDao;
import wesicknessdect.example.org.wesicknessdetect.database.dao.ModelDao;
import wesicknessdect.example.org.wesicknessdetect.database.dao.PostDao;
import wesicknessdect.example.org.wesicknessdetect.database.dao.QuestionDao;
import wesicknessdect.example.org.wesicknessdetect.database.dao.StruggleDao;
import wesicknessdect.example.org.wesicknessdetect.database.dao.SymptomRectDao;
import wesicknessdect.example.org.wesicknessdetect.database.dao.UserChoiceDao;
import wesicknessdect.example.org.wesicknessdetect.models.Profile;
import wesicknessdect.example.org.wesicknessdetect.models.User;
import wesicknessdect.example.org.wesicknessdetect.database.dao.PictureDao;
import wesicknessdect.example.org.wesicknessdetect.database.dao.ProfileDao;
import wesicknessdect.example.org.wesicknessdetect.database.dao.SymptomDao;
import wesicknessdect.example.org.wesicknessdetect.database.dao.UserDao;
import wesicknessdect.example.org.wesicknessdetect.models.Attack;
import wesicknessdect.example.org.wesicknessdetect.models.Country;
import wesicknessdect.example.org.wesicknessdetect.models.Culture;
import wesicknessdect.example.org.wesicknessdetect.models.CultureParcel;
import wesicknessdect.example.org.wesicknessdetect.models.CulturePart;
import wesicknessdect.example.org.wesicknessdetect.models.Diagnostic;
import wesicknessdect.example.org.wesicknessdetect.models.DiagnosticCulture;
import wesicknessdect.example.org.wesicknessdetect.models.Disease;
import wesicknessdect.example.org.wesicknessdetect.models.DiseaseFavorableCondition;
import wesicknessdect.example.org.wesicknessdetect.models.DiseaseSymptom;
import wesicknessdect.example.org.wesicknessdetect.models.FavorableCondition;
import wesicknessdect.example.org.wesicknessdetect.models.Message;
import wesicknessdect.example.org.wesicknessdetect.models.MessagePicture;
import wesicknessdect.example.org.wesicknessdetect.models.Post;
import wesicknessdect.example.org.wesicknessdetect.models.Struggle;
import wesicknessdect.example.org.wesicknessdetect.models.Model;
import wesicknessdect.example.org.wesicknessdetect.models.Parcel;
import wesicknessdect.example.org.wesicknessdetect.models.Picture;
import wesicknessdect.example.org.wesicknessdetect.models.PictureSymptom;
import wesicknessdect.example.org.wesicknessdetect.models.PossibleAnswer;
import wesicknessdect.example.org.wesicknessdetect.models.Question;
import wesicknessdect.example.org.wesicknessdetect.models.Symptom;
import wesicknessdect.example.org.wesicknessdetect.models.SymptomRect;
import wesicknessdect.example.org.wesicknessdetect.models.UserChoice;
import wesicknessdect.example.org.wesicknessdetect.models.UserParcel;

@Database(entities = {
        Profile.class,
        Country.class,
        User.class,
        Culture.class,
        CulturePart.class,
        Disease.class,
        Struggle.class,
        FavorableCondition.class,
        Attack.class,
        CultureParcel.class,
        DiseaseFavorableCondition.class,
        DiseaseSymptom.class,
        DiagnosticCulture.class,
        Diagnostic.class,
        Model.class,
        Post.class,
        Parcel.class,
        Picture.class,
        PictureSymptom.class,
        PossibleAnswer.class,
        Question.class,
        Symptom.class,
        SymptomRect.class,
        UserParcel.class,
        UserChoice.class,
        Message.class,
        MessagePicture.class
}, version = 1,exportSchema = false)
public abstract class AppDatabase extends RoomDatabase{
    private static AppDatabase appDatabase;
    public abstract UserDao userDao();
    public abstract ProfileDao profileDao();
    public abstract CountryDao countryDao();
    public abstract DiagnosticDao diagnosticDao();
    public abstract CulturePartsDao culturePartsDao();
    public abstract ModelDao modelDao();
    public abstract CultureDao cultureDao();
    public abstract SymptomDao symptomDao();
    public abstract SymptomRectDao symptomRectDao();
    public abstract QuestionDao questionDao();
    public abstract DiseaseDao diseaseDao();
    public abstract DiseaseSymptomsDao diseaseSymptomsDao();
    public abstract PostDao postDao();
    public abstract StruggleDao struggleDao();
    public abstract PictureDao pictureDao();
    public abstract UserChoiceDao userChoiceDao();
    private static AppDatabase INSTANCE;
    private static final String DATABASE_NAME = "scanleaf.db";
    private Context context;
    public static AppDatabase getInstance(Context context){
        if(appDatabase == null){
            appDatabase = Room.databaseBuilder(context, AppDatabase.class, context.getExternalFilesDir(null).getPath()+ File.separator+DATABASE_NAME)
                    .allowMainThreadQueries()
                    .build();
        }
        String currentDBPath=context.getDatabasePath(context.getExternalFilesDir(null).getPath()+ File.separator+DATABASE_NAME).getAbsolutePath();
        Log.e("DATABASE PATH ::",currentDBPath);
        return appDatabase;
    }

    public static void destroyInstance() {
        appDatabase = null;
    }
}
