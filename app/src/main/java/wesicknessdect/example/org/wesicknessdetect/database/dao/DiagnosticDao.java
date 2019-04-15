package wesicknessdect.example.org.wesicknessdetect.database.dao;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;
import wesicknessdect.example.org.wesicknessdetect.models.Diagnostic;
import wesicknessdect.example.org.wesicknessdetect.models.DiagnosticPictures;
import wesicknessdect.example.org.wesicknessdetect.models.Picture;

@Dao
public abstract class DiagnosticDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long createDiagnostic(Diagnostic diagnostic);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertDiagnostics(Diagnostic... diagnostics);


    @Transaction
    public void insertDiagnosticWithPicture(Diagnostic d, List<Picture> pictures) {
        final long id = createDiagnostic(d);
        for (Picture p : pictures) {
            p.setDiagnostic_id(id);
            insertPicture(p);
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long insertPicture(Picture picture);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void updateDiagnostic(Diagnostic diagnostic);

    @Delete
    public abstract void deleteDiagnostic(Diagnostic diagnostic);

    @Query("SELECT * FROM Diagnostic")
    public abstract LiveData<List<Diagnostic>> getAll();

    @Query("SELECT * FROM Diagnostic")
    public abstract List<Diagnostic> getAllSync();

    @Transaction
    @Query("SELECT * FROM Diagnostic")
    public abstract LiveData<List<DiagnosticPictures>> getDiagnosticWithPictures();

    @Transaction
    @Query("SELECT * FROM Diagnostic")
    public abstract List<DiagnosticPictures> getDiagnosticWithPicturesSync();
}
