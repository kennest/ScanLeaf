package wesicknessdect.example.org.wesicknessdetect.database.dao;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import wesicknessdect.example.org.wesicknessdetect.models.Country;
import wesicknessdect.example.org.wesicknessdetect.models.Culture;

@Dao
public interface CultureDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long createCulture(Culture culture);

    @Query("SELECT * FROM Culture")
    LiveData<List<Culture>> getAll();

    @Query("SELECT * FROM Culture")
    List<Culture> getAllSync();

    @Query("SELECT * FROM Culture WHERE name=:name")
    Culture getByName(String name);
}
