package wesicknessdect.example.org.wesicknessdetect.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import wesicknessdect.example.org.wesicknessdetect.models.Profile;

@Dao
public interface ProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long createProfile(Profile profile);
}
