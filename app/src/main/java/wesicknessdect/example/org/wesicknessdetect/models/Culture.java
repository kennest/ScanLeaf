package wesicknessdect.example.org.wesicknessdetect.models;

import com.google.gson.annotations.SerializedName;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Culture {
    @PrimaryKey(autoGenerate = true)
    private long id;

    @SerializedName(value = "id")
    private long res_id;

    @SerializedName(value = "name")
    private String name;

    @SerializedName(value = "image")
    private String image;

    private int nbParties;
    private String nomModele;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNbParties() {
        return nbParties;
    }

    public void setNbParties(int nbParties) {
        this.nbParties = nbParties;
    }

    public String getNomModele() {
        return nomModele;
    }

    public void setNomModele(String nomModele) {
        this.nomModele = nomModele;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getRes_id() {
        return res_id;
    }

    public void setRes_id(long res_id) {
        this.res_id = res_id;
    }
}
