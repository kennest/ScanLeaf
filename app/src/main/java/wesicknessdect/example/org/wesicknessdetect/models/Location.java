package wesicknessdect.example.org.wesicknessdetect.models;

import com.google.gson.annotations.SerializedName;

public class Location {
    @SerializedName(value = "lat")
    String lat;

    @SerializedName(value = "longi")
    String longi;

    @SerializedName(value = "idServeur")
    String idServeur;

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLongi() {
        return longi;
    }

    public void setLongi(String longi) {
        this.longi = longi;
    }

    public String getIdServeur() {
        return idServeur;
    }

    public void setIdServeur(String idServeur) {
        this.idServeur = idServeur;
    }
}
