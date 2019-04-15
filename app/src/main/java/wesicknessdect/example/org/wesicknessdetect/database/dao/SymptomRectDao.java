package wesicknessdect.example.org.wesicknessdetect.database.dao;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import wesicknessdect.example.org.wesicknessdetect.models.Symptom;
import wesicknessdect.example.org.wesicknessdetect.models.SymptomRect;

@Dao
public interface SymptomRectDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long createSymptomRect(SymptomRect symptomRect);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateSymptomRect(SymptomRect symptomRect);

    @Delete
    void deleteSymptomRect(SymptomRect symptomRect);

    @Query("SELECT * FROM SymptomRect")
    LiveData<List<SymptomRect>> getAll();

    @Query("SELECT * FROM SymptomRect")
    List<SymptomRect> getAllSync();

    @Query("SELECT * FROM SymptomRect WHERE picture_id=:id")
    LiveData<List<SymptomRect>> getByPictureId(long id);

    @Query("SELECT * FROM SymptomRect WHERE picture_id=:id")
    List<SymptomRect> getByPictureIdSync(long id);

    @Query("SELECT * FROM SymptomRect WHERE symptom_id=:id")
    LiveData<List<SymptomRect>> getBySymptomId(long id);

}
