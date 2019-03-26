package wesicknessdect.example.org.wesicknessdetect.models;

import com.google.gson.annotations.SerializedName;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {
        @ForeignKey(
                entity = Diagnostic.class,
                parentColumns = "id",
        childColumns = "diagnostic_id"),
        @ForeignKey(
                entity = CulturePart.class,
                parentColumns = "id",
                childColumns = "culture_part_id")
},indices = {@Index("diagnostic_id"),@Index("culture_part_id")})
public class Picture {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @SerializedName(value = "id")
    private long res_id;
    private long diagnostic_id;
    private long culture_part_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getDiagnostic_id() {
        return diagnostic_id;
    }

    public void setDiagnostic_id(long diagnostic_id) {
        this.diagnostic_id = diagnostic_id;
    }

    public long getCulture_part_id() {
        return culture_part_id;
    }

    public void setCulture_part_id(long culture_part_id) {
        this.culture_part_id = culture_part_id;
    }

    public long getRes_id() {
        return res_id;
    }

    public void setRes_id(long res_id) {
        this.res_id = res_id;
    }
}
