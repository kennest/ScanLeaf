package wesicknessdect.example.org.wesicknessdetect.database.dao;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import wesicknessdect.example.org.wesicknessdetect.models.Country;
import wesicknessdect.example.org.wesicknessdetect.models.Struggle;

@Dao
public interface StruggleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void createStruggle(Struggle struggle);

    @Query("SELECT * FROM Struggle")
    LiveData<List<Struggle>> getAll();

    @Query("SELECT * FROM Struggle WHERE id=:id")
    LiveData<Struggle> getById(long id);
}
