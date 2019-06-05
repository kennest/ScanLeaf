package wesicknessdect.example.org.wescanleaf.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

import wesicknessdect.example.org.wescanleaf.models.UserChoice;

@Dao
public abstract class UserChoiceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void create(UserChoice data);

    @Query("SELECT * FROM UserChoice")
    public abstract List<UserChoice>getAllSync();

    @Query("SELECT * FROM UserChoice WHERE sended=:num")
    public abstract List<UserChoice> getNotSended(long num);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void update(UserChoice data);
}
