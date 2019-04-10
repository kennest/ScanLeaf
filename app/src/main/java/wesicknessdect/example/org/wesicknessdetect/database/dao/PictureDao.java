package wesicknessdect.example.org.wesicknessdetect.database.dao;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import wesicknessdect.example.org.wesicknessdetect.models.CulturePart;
import wesicknessdect.example.org.wesicknessdetect.models.Picture;

@Dao
public interface PictureDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void createPicture(Picture picture);

    @Query("SELECT * FROM Picture")
    LiveData<List<Picture>> getAll();

    @Query("SELECT * FROM Picture WHERE diagnostic_id=:id")
    LiveData<List<Picture>> getByDiagnosticId(long id);
}
