package wesicknessdect.example.org.wesicknessdetect.database.dao;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import io.reactivex.Maybe;
import io.reactivex.Single;
import wesicknessdect.example.org.wesicknessdetect.models.Model;

@Dao
public interface ModelDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void createModel(Model model);

    @Query("SELECT * FROM Model")
    LiveData<List<Model>> getAll();

    @Query("SELECT * FROM Model")
    Single<List<Model>> rxGetAll();

    @Query("SELECT * FROM Model WHERE part_id=:id")
    Single<Model> rxGetByPart(long id);

    @Query("SELECT * FROM Model WHERE part_id=:id")
    LiveData<Model> getByPart(long id);

    @Query("SELECT * FROM Model WHERE part_id=:id")
    Model getByPartSync(long id);
}
