package wesicknessdect.example.org.wesicknessdetect.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import wesicknessdect.example.org.wesicknessdetect.database.dao.CountryDao;
import wesicknessdect.example.org.wesicknessdetect.database.dao.CultureDao;
import wesicknessdect.example.org.wesicknessdetect.database.dao.CulturePartsDao;
import wesicknessdect.example.org.wesicknessdetect.database.dao.ModelDao;
import wesicknessdect.example.org.wesicknessdetect.database.dao.ProfileDao;
import wesicknessdect.example.org.wesicknessdetect.database.dao.QuestionDao;
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
import wesicknessdect.example.org.wesicknessdetect.models.Struggle;
import wesicknessdect.example.org.wesicknessdetect.models.Model;
import wesicknessdect.example.org.wesicknessdetect.models.Parcel;
import wesicknessdect.example.org.wesicknessdetect.models.Picture;
import wesicknessdect.example.org.wesicknessdetect.models.PictureSymptom;
import wesicknessdect.example.org.wesicknessdetect.models.PossibleAnswer;
import wesicknessdect.example.org.wesicknessdetect.models.Profile;
import wesicknessdect.example.org.wesicknessdetect.models.Question;
import wesicknessdect.example.org.wesicknessdetect.models.Symptom;
import wesicknessdect.example.org.wesicknessdetect.models.User;
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
        Parcel.class,
        Picture.class,
        PictureSymptom.class,
        PossibleAnswer.class,
        Question.class,
        Symptom.class,
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
    public abstract CulturePartsDao culturePartsDao();
    public abstract ModelDao modelDao();
    public abstract CultureDao cultureDao();
    public abstract SymptomDao symptomDao();
    public abstract QuestionDao questionDao();
    private static AppDatabase INSTANCE;
    private static final String DATABASE_NAME = "wesickness.db";
    private Context context;
    public static AppDatabase getInstance(Context context){
        if(appDatabase == null){
            appDatabase = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .build();
        }
        return appDatabase;
    }

    public static void destroyInstance() {
        appDatabase = null;
    }
}
