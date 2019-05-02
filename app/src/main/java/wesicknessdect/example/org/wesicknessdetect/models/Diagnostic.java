package wesicknessdect.example.org.wesicknessdetect.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(foreignKeys = {
        @ForeignKey(
                entity = User.class, parentColumns = "id", childColumns = "user_id",onUpdate = CASCADE,onDelete = CASCADE
        ),
},indices = {@Index({"user_id"})})
public class Diagnostic  {
    @SerializedName(value = "id_mobile")
    @PrimaryKey(autoGenerate = true)
    private int x;

    @SerializedName(value = "id")
    private int remote_id;

    private String localisation;
    private String advancedAnalysis;
    private String disease;
    private boolean finish;
    private int sended;
    private float probability=95f;
    @SerializedName(value = "user")
    private long user_id;
    @SerializedName(value = "culture")
    private long culture_id;
    @SerializedName(value = "country")
    private long country_id;
    private int is_share=0;

    @Ignore
    private Map<Integer, String> images_by_parts;

    @Ignore
    private List<Picture> pictures;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }


    public float getProbability() {
        return probability;
    }

    public void setProbability(float probability) {
        this.probability = probability;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public int getIs_share() {
        return is_share;
    }

    public void setIs_share(int is_share) {
        this.is_share = is_share;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public String getLocalisation() {
        return localisation;
    }

    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }

    public long getCountry_id() {
        return country_id;
    }

    public void setCountry_id(long country_id) {
        this.country_id = country_id;
    }

    public long getCulture_id() {
        return culture_id;
    }

    public void setCulture_id(long culture_id) {
        this.culture_id = culture_id;
    }

    public String getAdvancedAnalysis() {
        return advancedAnalysis;
    }

    public void setAdvancedAnalysis(String advancedAnalysis) {
        this.advancedAnalysis = advancedAnalysis;
    }

    public boolean isFinish() {
        return finish;
    }

    public void setFinish(boolean finish) {
        this.finish = finish;
    }

    public int getSended() {
        return sended;
    }

    public void setSended(int sended) {
        this.sended = sended;
    }

    public Map<Integer, String> getImages_by_parts() {
        return images_by_parts;
    }

    public void setImages_by_parts(Map<Integer, String> images_by_parts) {
        this.images_by_parts = images_by_parts;
    }

    public int getRemote_id() {
        return remote_id;
    }

    public void setRemote_id(int remote_id) {
        this.remote_id = remote_id;
    }

    public List<Picture> getPictures() {
        return pictures;
    }

    public void setPictures(List<Picture> pictures) {
        this.pictures = pictures;
    }
}
