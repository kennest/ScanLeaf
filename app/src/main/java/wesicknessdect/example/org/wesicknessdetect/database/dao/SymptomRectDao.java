package wesicknessdect.example.org.wesicknessdetect.database.dao;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import wesicknessdect.example.org.wesicknessdetect.models.Symptom;
import wesicknessdect.example.org.wesicknessdetect.models.SymptomRect;

@Dao
public interface SymptomRectDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void createSymptomRect(SymptomRect symptomRect);

    @Query("SELECT * FROM SymptomRect")
    LiveData<List<SymptomRect>> getAll();

    @Query("SELECT * FROM SymptomRect WHERE picture_id=:id")
    LiveData<SymptomRect> getByPicture(long id);

}
