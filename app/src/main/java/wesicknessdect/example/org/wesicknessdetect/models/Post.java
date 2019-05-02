package wesicknessdect.example.org.wesicknessdetect.models;

import com.google.gson.annotations.SerializedName;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index("idServeur")})
public class Post {
 @PrimaryKey(autoGenerate = true)
    private long id;

    @SerializedName(value = "diseaseName")
    private String diseaseName;

    @SerializedName(value = "distance")
    private String distance;

    @SerializedName(value = "idServeur")
    private String idServeur;

    @SerializedName(value = "time")
    private String time;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDiseaseName() {
        return diseaseName;
    }

    public void setDiseaseName(String diseaseName) {
        this.diseaseName = diseaseName;
    }


    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getIdServeur() {
        return idServeur;
    }

    public void setIdServeur(String idServeur) {
        this.idServeur = idServeur;
    }



}
