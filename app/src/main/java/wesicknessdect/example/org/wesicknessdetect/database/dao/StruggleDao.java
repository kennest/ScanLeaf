package wesicknessdect.example.org.wesicknessdetect.database.dao;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import wesicknessdect.example.org.wesicknessdetect.models.Struggle;

@Dao
public abstract class StruggleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void createStruggle(Struggle struggle);

    @Query("SELECT * FROM Struggle")
    public abstract LiveData<List<Struggle>> getAll();

    @Query("SELECT * FROM Struggle WHERE id=:id")
    public abstract Struggle getByIdSync(long id);
}
