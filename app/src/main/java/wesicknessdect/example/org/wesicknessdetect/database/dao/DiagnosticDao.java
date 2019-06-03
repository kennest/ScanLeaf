package wesicknessdect.example.org.wesicknessdetect.database.dao;

import android.util.Log;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import wesicknessdect.example.org.wesicknessdetect.models.Diagnostic;
import wesicknessdect.example.org.wesicknessdetect.models.DiagnosticPictures;
import wesicknessdect.example.org.wesicknessdetect.models.Picture;
import wesicknessdect.example.org.wesicknessdetect.models.Symptom;
import wesicknessdect.example.org.wesicknessdetect.models.SymptomRect;

@Dao
public abstract class DiagnosticDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long createDiagnostic(Diagnostic diagnostic);


    //RX JAVA
    @Query("SELECT * FROM Diagnostic")
    public abstract Single<List<Diagnostic>> rxGetAll();

    @Query("SELECT * FROM Diagnostic WHERE sended=0")
    public abstract Single<List<Diagnostic>> rxGetNotSendedSync();

    @Query("SELECT * FROM Diagnostic WHERE x=:id")
    public abstract Maybe<DiagnosticPictures> rxGetDiagnosticWithPicturesSync(int id);

    @Query("SELECT * FROM Diagnostic WHERE uuid=:uuid")
    public abstract Diagnostic getDiagnosticByUuid(String uuid);

    //RX JAVA

    @Delete
    public abstract void delete(Diagnostic diagnostic);

    @Transaction
    public void insertDiagnosticWithPictureAndRect(Diagnostic d, List<Picture> pictures) {
        Log.e("DAO picture size:",pictures.size()+"");
        final long id = createDiagnostic(d);
        for (Picture p : pictures) {
            p.setDiagnostic_id(id);
            p.setDiagnostic_uuid(d.getUuid());
            final long pic_ic=insertPicture(p);
            Log.e("DAO picture id ->",pic_ic+"");
            for(SymptomRect sr:p.getSymptomRects()){
                sr.setPicture_id((int) pic_ic);
                sr.setPicture_uuid(p.getUuid());
                createSymptomRect(sr);
            }
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long createSymptomRect(SymptomRect symptomRect);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long insertPicture(Picture picture);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void updateDiagnostic(Diagnostic diagnostic);

    @Query("SELECT * FROM Diagnostic")
    public abstract LiveData<List<Diagnostic>> getAll();


    @Query("SELECT * FROM Diagnostic")
    public abstract List<Diagnostic> getAllSync();

    @Query("SELECT * FROM Diagnostic WHERE sended=0")
    public abstract List<Diagnostic> getNotSendedSync();

    @Query("SELECT * FROM Diagnostic WHERE x=:id")
    public abstract DiagnosticPictures getDiagnosticWithPicturesSync(int id);
}
