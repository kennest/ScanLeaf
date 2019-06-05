package wesicknessdect.example.org.wesicknessdetect.models;

import com.google.gson.annotations.SerializedName;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {@ForeignKey(entity = CulturePart.class,
        parentColumns = "id",
        childColumns = "partieculture_id"),
        @ForeignKey(entity = Disease.class,
        parentColumns = "id",
        childColumns = "maladie_id")},indices = {@Index("maladie_id"),@Index("partieculture_id")})

public class Attack  {
    @SerializedName(value = "id")
    @PrimaryKey
    private int id;
    private long maladie_id;
    private long partieculture_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getMaladie_id() {
        return maladie_id;
    }

    public void setMaladie_id(long maladie_id) {
        this.maladie_id = maladie_id;
    }

    public long getPartieculture_id() {
        return partieculture_id;
    }

    public void setPartieculture_id(long partieculture_id) {
        this.partieculture_id = partieculture_id;
    }
}
