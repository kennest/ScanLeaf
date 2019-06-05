package wesicknessdect.example.org.wesicknessdetect.database.dao;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import io.reactivex.Single;
import wesicknessdect.example.org.wesicknessdetect.models.Country;
import wesicknessdect.example.org.wesicknessdetect.models.CulturePart;

@Dao
public interface CulturePartsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void createCulturePart(CulturePart culturePart);

    @Query("SELECT * FROM CulturePart")
    LiveData<List<CulturePart>> getAll();

    @Query("SELECT * FROM CulturePart")
    Single<List<CulturePart>> rxGetAll();

    @Query("SELECT * FROM CulturePart WHERE nom=:name")
    Single<CulturePart> rxGetByName(String name);

    @Query("SELECT * FROM CulturePart WHERE id=:id")
    Single<CulturePart> rxGetById(long id);

    @Query("SELECT * FROM CulturePart")
    List<CulturePart> getAllSync();

    @Query("SELECT * FROM CulturePart WHERE nom=:name")
    LiveData<CulturePart> getByName(String name);

    @Query("SELECT * FROM CulturePart WHERE id=:id")
    LiveData<CulturePart> getById(long id);

    @Query("SELECT * FROM CulturePart WHERE id=:id")
    CulturePart getByIdSync(long id);
}
