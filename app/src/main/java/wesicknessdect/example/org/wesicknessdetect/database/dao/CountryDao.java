package wesicknessdect.example.org.wesicknessdetect.database.dao;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import wesicknessdect.example.org.wesicknessdetect.models.Country;

@Dao
public interface CountryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long createCountry(Country diagnostic);

    @Query("SELECT * FROM Country")
    LiveData<List<Country>> getAll();

    @Query("SELECT * FROM Country")
    List<Country> getAllSync();

    @Query("SELECT * FROM Country WHERE name=:name")
    LiveData<Country> getByName(String name);

    @Query("SELECT * FROM Country WHERE id=:id")
    Country getById(int id);
}
