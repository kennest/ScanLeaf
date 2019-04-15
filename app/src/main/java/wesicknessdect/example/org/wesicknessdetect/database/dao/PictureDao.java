package wesicknessdect.example.org.wesicknessdetect.database.dao;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;
import wesicknessdect.example.org.wesicknessdetect.models.CulturePart;
import wesicknessdect.example.org.wesicknessdetect.models.Picture;
import wesicknessdect.example.org.wesicknessdetect.models.PictureSymptomRects;
import wesicknessdect.example.org.wesicknessdetect.models.SymptomRect;

@Dao
public abstract class PictureDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long createPicture(Picture picture);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void updatePicture(Picture picture);

    @Transaction
    public void insertPictureWithSymptomRect(Picture p, List<SymptomRect> symptomRects) {
        final long id = createPicture(p);
        for (SymptomRect s : symptomRects) {
          s.setPicture_id((int) id);
            createSymptomRect(s);
        }
    }

    @Transaction
    @Query("SELECT * FROM Picture WHERE x=:id")
    public abstract LiveData<List<PictureSymptomRects>> getPictureWithSymptomRect(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long createSymptomRect(SymptomRect symptomRect);

    @Delete
    public abstract void deletePicture(Picture picture);

    @Query("SELECT * FROM Picture")
    public abstract LiveData<List<Picture>> getAll();

    @Query("SELECT * FROM Picture")
    public abstract List<Picture> getAllSync();

    @Query("SELECT * FROM Picture WHERE diagnostic_id=:id")
    public abstract LiveData<List<Picture>> getByDiagnosticId(long id);

    @Query("SELECT * FROM Picture WHERE diagnostic_id=:id")
    public abstract List<Picture> getByDiagnosticIdSync(long id);
}
