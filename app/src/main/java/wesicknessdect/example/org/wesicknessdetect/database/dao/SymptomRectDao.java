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
public abstract class SymptomRectDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long createSymptomRect(SymptomRect symptomRect);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void updateSymptomRect(SymptomRect symptomRect);

    @Delete
    public abstract void deleteSymptomRect(SymptomRect symptomRect);

    @Query("SELECT * FROM SymptomRect")
    public abstract LiveData<List<SymptomRect>> getAll();

    @Query("SELECT * FROM SymptomRect")
    public abstract List<SymptomRect> getAllSync();

    @Query("SELECT * FROM SymptomRect WHERE picture_id=:id")
    public abstract LiveData<List<SymptomRect>> getByPictureId(long id);

    @Query("SELECT * FROM SymptomRect WHERE picture_id=:id")
    public abstract List<SymptomRect> getByPictureIdSync(long id);

    @Query("SELECT * FROM SymptomRect WHERE symptom_id=:id")
    public abstract LiveData<List<SymptomRect>> getBySymptomId(long id);

}
