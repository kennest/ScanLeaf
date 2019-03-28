package wesicknessdect.example.org.wesicknessdetect.models;

import com.google.gson.annotations.SerializedName;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Profile {
    @SerializedName(value = "id")
    @PrimaryKey
    private int id;



    @SerializedName(value = "avatar")
    private String avatar;

    @SerializedName(value = "gender")
    private String gender;

    @SerializedName(value = "country")
    private int country;

    @SerializedName(value = "fonction")
    private String fonction;

    @SerializedName(value = "birth")
    private String birth;

    @SerializedName(value = "birthPlace")
    private String birthPlace;

    @SerializedName(value = "mobile")
    private String mobile;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getCountry() {
        return country;
    }

    public void setCountry(int country) {
        this.country = country;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getFonction() {
        return fonction;
    }

    public void setFonction(String fonction) {
        this.fonction = fonction;
    }

}
