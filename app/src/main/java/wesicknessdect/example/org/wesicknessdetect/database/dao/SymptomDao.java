package wesicknessdect.example.org.wesicknessdetect.database.dao;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import wesicknessdect.example.org.wesicknessdetect.models.Country;
import wesicknessdect.example.org.wesicknessdetect.models.Symptom;

@Dao
public interface SymptomDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void createSymptom(Symptom symptom);

    @Query("SELECT * FROM Symptom")
    LiveData<List<Symptom>> getAll();

    @Query("SELECT * FROM Symptom WHERE question_id=:id")
    LiveData<Symptom> getByQuestion(long id);
}
