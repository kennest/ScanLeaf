package wesicknessdect.example.org.wesicknessdetect.models;

import com.google.gson.annotations.SerializedName;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {
        @ForeignKey(
                entity = UserParcel.class,
                parentColumns = "id",
                childColumns = "user_parcel_id"),
        @ForeignKey(
                entity = Culture.class,
                parentColumns = "id",
                childColumns = "culture_id")
},
        indices = {@Index("user_parcel_id"),@Index("culture_id")})
public class CultureParcel  {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @SerializedName(value = "id")
    private long res_id;

    private long user_parcel_id;
    private long culture_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getUser_parcel_id() {
        return user_parcel_id;
    }

    public void setUser_parcel_id(long user_parcel_id) {
        this.user_parcel_id = user_parcel_id;
    }

    public long getCulture_id() {
        return culture_id;
    }

    public void setCulture_id(long culture_id) {
        this.culture_id = culture_id;
    }

    public long getRes_id() {
        return res_id;
    }

    public void setRes_id(long res_id) {
        this.res_id = res_id;
    }
}
