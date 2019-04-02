package wesicknessdect.example.org.wesicknessdetect.models;

import com.google.gson.annotations.SerializedName;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {
        @ForeignKey(
                entity = CulturePart.class,
                parentColumns = "id",
                childColumns = "culture_part_id")
},indices = {@Index("culture_part_id")})
public class Picture {
    @SerializedName(value = "id")
    @PrimaryKey(autoGenerate = true)
    private int id;

    @SerializedName(value = "diagnostic")
    private long diagnostic_id;

    @SerializedName(value = "partCulture")
    private long culture_part_id;

    private boolean sended;

    @SerializedName(value = "image")
    private String image;

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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isSended() {
        return sended;
    }

    public void setSended(boolean sended) {
        this.sended = sended;
    }
}
