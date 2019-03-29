package wesicknessdect.example.org.wesicknessdetect.database.dao;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import wesicknessdect.example.org.wesicknessdetect.models.Country;
import wesicknessdect.example.org.wesicknessdetect.models.DiseaseSymptom;

@Dao
public interface DiseaseSymptomsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void createDiseaseSymptom(DiseaseSymptom diseaseSymptom);

    @Query("SELECT * FROM DiseaseSymptom")
    LiveData<List<DiseaseSymptom>> getAll();

    @Query("SELECT * FROM DiseaseSymptom WHERE disease_id=:id")
    LiveData<List<DiseaseSymptom>> getByDisease(long id);

    @Query("SELECT * FROM DiseaseSymptom WHERE symptom_id=:id")
    LiveData<List<DiseaseSymptom>> getBySymptom(long id);
}
