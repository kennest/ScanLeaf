package wesicknessdect.example.org.wesicknessdetect.database.dao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import wesicknessdect.example.org.wesicknessdetect.models.Country;
import wesicknessdect.example.org.wesicknessdetect.models.Culture;

@Dao
public interface CultureDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void createCulture(Culture culture);

    @Query("SELECT * FROM Culture")
    List<Culture> getAll();

    @Query("SELECT * FROM Culture WHERE name=:name")
    Culture getByName(String name);
}
