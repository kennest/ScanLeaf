package wesicknessdect.example.org.wesicknessdetect.models;

import com.google.gson.annotations.SerializedName;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(entity = Culture.class,
        parentColumns = "id",
        childColumns = "culture_id"),
        indices = {@Index("culture_id")})
public class CulturePart {

    @SerializedName(value = "id")
    @PrimaryKey
    private long id;


    @SerializedName(value = "name")
    private String nom;

    @SerializedName(value = "image")
    private String image;

    private long culture_id;

    @Ignore
    private boolean model_downloaded=false;

    @Ignore
    private boolean recognizing=false;

    @Ignore
    private long downloaded=0;

    @Ignore
    private long filesize=0;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public long getCulture_id() {
        return culture_id;
    }

    public void setCulture_id(long culture_id) {
        this.culture_id = culture_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isModel_downloaded() {
        return model_downloaded;
    }

    public void setModel_downloaded(boolean model_downloaded) {
        this.model_downloaded = model_downloaded;
    }

    public long getDownloaded() {
        return downloaded;
    }

    public void setDownloaded(long downloaded) {
        this.downloaded = downloaded;
    }

    public long getFilesize() {
        return filesize;
    }

    public void setFilesize(long filesize) {
        this.filesize = filesize;
    }

    public boolean isRecognizing() {
        return recognizing;
    }

    public void setRecognizing(boolean recognizing) {
        this.recognizing = recognizing;
    }
}
