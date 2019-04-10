package wesicknessdect.example.org.wesicknessdetect.models;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {
        @ForeignKey(
                entity = User.class, parentColumns = "id", childColumns = "user_id"
        ),
},indices = {@Index("user_id")})
public class Diagnostic  {
    @SerializedName(value = "id")
    @PrimaryKey(autoGenerate = true)
    private int x;

    private int net_id;

    private double longitude;
    private double latitude;
    private String localisation;
    private String date;
    private String hour;
    private String advancedAnalysis;
    private String disease;
    private boolean finish;
    private Integer sended;
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

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
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

    public Integer getSended() {
        return sended;
    }

    public void setSended(Integer sended) {
        this.sended = sended;
    }

    public Map<Integer, String> getImages_by_parts() {
        return images_by_parts;
    }

    public void setImages_by_parts(Map<Integer, String> images_by_parts) {
        this.images_by_parts = images_by_parts;
    }

    public int getNet_id() {
        return net_id;
    }

    public void setNet_id(int net_id) {
        this.net_id = net_id;
    }
}
