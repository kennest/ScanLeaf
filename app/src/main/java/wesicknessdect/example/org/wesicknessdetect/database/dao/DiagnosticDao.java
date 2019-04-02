package wesicknessdect.example.org.wesicknessdetect.database.dao;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import wesicknessdect.example.org.wesicknessdetect.models.Diagnostic;

@Dao
public interface DiagnosticDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long createDiagnostic(Diagnostic diagnostic);

    @Query("SELECT * FROM Diagnostic")
    List<Diagnostic> getAll();

    @Query("SELECT * FROM Diagnostic")
    LiveData<List<Diagnostic>> getDiagnosticWithPictures();
}
