package wesicknessdect.example.org.wesicknessdetect.database.dao;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import wesicknessdect.example.org.wesicknessdetect.models.Country;
import wesicknessdect.example.org.wesicknessdetect.models.Disease;

@Dao
public interface DiseaseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void createDisease(Disease disease);

    @Query("SELECT * FROM Disease")
    LiveData<List<Disease>> getAll();

//    Then your String name value should look like:
//    name = "%fido%";
    @Query("SELECT * FROM Disease WHERE name LIKE :name")
    LiveData<Disease> getByName(String name);
}
