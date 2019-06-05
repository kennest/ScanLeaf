package wesicknessdect.example.org.wescanleaf.database.dao;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import wesicknessdect.example.org.wescanleaf.models.Question;

@Dao
public interface QuestionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void createQuestion(Question question);

    @Query("SELECT * FROM Question")
    LiveData<List<Question>> getAll();

    @Query("SELECT * FROM Question")
    List<Question> getAllSync();

    @Query("SELECT * FROM Question WHERE part_culture_id=:id")
    LiveData<Question> getByPart(long id);

    @Query("SELECT * FROM Question WHERE part_culture_id=:id")
    Question getByPartSync(long id);
}
