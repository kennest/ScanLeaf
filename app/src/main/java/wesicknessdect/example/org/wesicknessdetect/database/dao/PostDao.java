package wesicknessdect.example.org.wesicknessdetect.database.dao;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import wesicknessdect.example.org.wesicknessdetect.models.Culture;
import wesicknessdect.example.org.wesicknessdetect.models.Post;

@Dao
public interface PostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long createPost(Post post);

    @Query("SELECT * FROM Post")
    LiveData<List<Post>> getAll();

    @Query("SELECT * FROM Post")
    List<Post> getAllPost();

    @Query("SELECT id,MAX(idServeur)FROM Post")
    Post getLastPost();

    @Query("UPDATE Post SET diseaseName = :maladie, distance = :distance WHERE idServeur =:idServeur")
    void updatePost(String maladie, String distance, String idServeur);




}
