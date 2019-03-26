package wesicknessdect.example.org.wesicknessdetect.models;

import android.text.TextUtils;
import android.util.Patterns;

import com.google.gson.annotations.SerializedName;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(entity = Profile.class,
        parentColumns = "id",
        childColumns = "profile_id"),
        indices = {@Index("profile_id")})
public class User  {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @SerializedName(value = "id")
    private long res_id;

    @SerializedName(value = "first_name")
    private String nom;

    @SerializedName(value = "last_name")
    private String prenom;

    @SerializedName(value = "username")
    private String username;

    @SerializedName(value = "email")
    private String email;

    @SerializedName(value = "password")
    private String password;

    @Ignore
    @SerializedName(value = "profil")
    private Profile profile;

    @SerializedName(value = "token")
    private String token;

    private long profile_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getProfile_id() {
        return profile_id;
    }

    public void setProfile_id(long profile_id) {
        this.profile_id = profile_id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isValidData(){
        return !TextUtils.isEmpty(getNom()) || !TextUtils.isEmpty(getUsername()) || Patterns.EMAIL_ADDRESS.matcher(getEmail()).matches();
    }

    public long getRes_id() {
        return res_id;
    }

    public void setRes_id(long res_id) {
        this.res_id = res_id;
    }
}
