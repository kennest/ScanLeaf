package wesicknessdect.example.org.wesicknessdetect.database.dao;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import io.reactivex.Single;
import wesicknessdect.example.org.wesicknessdetect.models.Profile;

@Dao
public interface ProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long createProfile(Profile profile);

    @Query("SELECT * FROM Profile")
    LiveData<List<Profile>> getAll();

    @Query("SELECT * FROM Profile")
    List<Profile> getAllSync();

    @Query("SELECT * FROM Profile")
    Single<List<Profile>> rxGetAll();

    @Query("SELECT * FROM Profile WHERE updated=0")
    Profile getNotUpdated();

    @Query("SELECT * FROM Profile")
    List<Profile> getProfil();

    @Update
    void update(Profile profile);

    @Delete
    void delete(Profile profile);

}
