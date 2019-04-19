package wesicknessdect.example.org.wesicknessdetect.models;

import com.google.gson.annotations.SerializedName;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(foreignKeys = {
        @ForeignKey(entity = CulturePart.class,
        parentColumns = "id",
        childColumns = "culture_part_id",onUpdate = CASCADE,onDelete = CASCADE),
        @ForeignKey(entity = Diagnostic.class,
                parentColumns = "x",
                childColumns = "diagnostic_id",onUpdate = CASCADE)},
        indices = {@Index({"diagnostic_id","culture_part_id"})})
public class Picture {

    @SerializedName(value = "id_mobile")
    @PrimaryKey(autoGenerate = true)
    private int x;

    @SerializedName(value = "id")
    private long remote_id;

    @SerializedName(value = "diagnostic")
    private long diagnostic_id;

    @SerializedName(value = "partCulture")
    private long culture_part_id;

    public int sended;

    @SerializedName(value = "image")
    private String image;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
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

    public int getSended() {
        return sended;
    }

    public void setSended(int sended) {
        this.sended = sended;
    }

    public long getRemote_id() {
        return remote_id;
    }

    public void setRemote_id(long remote_id) {
        this.remote_id = remote_id;
    }
}
