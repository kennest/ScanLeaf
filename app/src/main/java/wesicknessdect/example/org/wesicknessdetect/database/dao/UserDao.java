package wesicknessdect.example.org.wesicknessdetect.database.dao;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import wesicknessdect.example.org.wesicknessdetect.models.User;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long createUser(User user);

    @Query("SELECT * FROM User WHERE id = :userId")
    LiveData<User> getUser(long userId);

    @Query("SELECT * FROM User")
    List<User> getAll();


    @Update
    void update(User user);

    @Delete
    void delete(User user);

//    @Query("UPDATE User SET nom=:name, prenom=:surname, username=:pseudo, email=:email WHERE id=:id")
//    User updateUser(String name, String surname, String pseudo, String email, int id);
}
