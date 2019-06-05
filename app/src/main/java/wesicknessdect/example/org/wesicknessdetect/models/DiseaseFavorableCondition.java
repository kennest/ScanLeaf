package wesicknessdect.example.org.wesicknessdetect.models;

import com.google.gson.annotations.SerializedName;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {@ForeignKey(entity = FavorableCondition.class,
        parentColumns = "id",
        childColumns = "favorable_contition_id"),
        @ForeignKey(entity = Disease.class,
                parentColumns = "id",
                childColumns = "maladie_id"),
},indices = {@Index("favorable_contition_id"),@Index("maladie_id")})
public class DiseaseFavorableCondition  {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @SerializedName(value = "id")
    private long res_id;

    private long favorable_contition_id;
    private long maladie_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getFavorable_contition_id() {
        return favorable_contition_id;
    }

    public void setFavorable_contition_id(long favorable_contition_id) {
        this.favorable_contition_id = favorable_contition_id;
    }

    public long getMaladie_id() {
        return maladie_id;
    }

    public void setMaladie_id(long maladie_id) {
        this.maladie_id = maladie_id;
    }

    public long getRes_id() {
        return res_id;
    }

    public void setRes_id(long res_id) {
        this.res_id = res_id;
    }
}
