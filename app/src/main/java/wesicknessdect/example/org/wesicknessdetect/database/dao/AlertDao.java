package wesicknessdect.example.org.wesicknessdetect.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import wesicknessdect.example.org.wesicknessdetect.models.Alert;
import wesicknessdect.example.org.wesicknessdetect.models.Post;

@Dao
public interface AlertDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long createAlert(Alert alert);

    @Query("SELECT * FROM Alert")
    LiveData<List<Alert>> getAll();

    @Query("SELECT * FROM Alert")
    List<Alert> getAllAlert();

    @Query("SELECT * FROM Alert WHERE idPost=:id")
    List<Alert> getAllAlertByPost(long id);

    @Query("SELECT * FROM Alert ORDER BY id DESC LIMIT 1")
    Alert getLastAlert();

    @Query("UPDATE Alert SET longitude = :longitude, latitude = :latitude WHERE id =:id")
    void updateAlert(Double longitude, Double latitude, long id);




}
