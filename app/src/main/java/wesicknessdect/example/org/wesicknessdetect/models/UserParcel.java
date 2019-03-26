package wesicknessdect.example.org.wesicknessdetect.models;

import com.google.gson.annotations.SerializedName;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {
        @ForeignKey(
                entity = User.class,
                parentColumns = "id",
                childColumns = "user_id"),
        @ForeignKey(
                entity = Parcel.class,
                parentColumns = "id",
                childColumns = "parcel_id")
},indices = {@Index("user_id"),@Index("parcel_id")})
public class UserParcel {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @SerializedName(value = "id")
    private long res_id;
    private long user_id;
    private long parcel_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public long getParcel_id() {
        return parcel_id;
    }

    public void setParcel_id(long parcel_id) {
        this.parcel_id = parcel_id;
    }

    public long getRes_id() {
        return res_id;
    }

    public void setRes_id(long res_id) {
        this.res_id = res_id;
    }
}
