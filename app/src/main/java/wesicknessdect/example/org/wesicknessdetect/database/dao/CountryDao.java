package wesicknessdect.example.org.wesicknessdetect.database.dao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import wesicknessdect.example.org.wesicknessdetect.models.Country;

@Dao
public interface CountryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void createCountry(Country diagnostic);

    @Query("SELECT * FROM Country")
    List<Country> getAll();

    @Query("SELECT * FROM Country WHERE name=:name")
    Country getByName(String name);
}
