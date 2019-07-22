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
import wesicknessdect.example.org.wesicknessdetect.models.User;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long createUser(User user);

    @Query("SELECT * FROM User")
    List<User> getAll();

    @Query("SELECT * FROM User")
    Single<List<User>> rxGetAll();


    @Update
    void update(User user);

    @Delete
    void delete(User user);

}
