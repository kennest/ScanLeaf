package wesicknessdect.example.org.wesicknessdetect.models;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(indices = {@Index("idPost")})
public class Alert {
 @PrimaryKey(autoGenerate = true)
    private long id;

    @SerializedName(value = "idPost")
    private long idPost;

    @SerializedName(value = "latitude")
    private Double latitude;

    @SerializedName(value = "longitude")
    private Double longitude;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getIdPost() {
        return idPost;
    }

    public void setIdPost(long idPost) {
        this.idPost = idPost;
    }


    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
