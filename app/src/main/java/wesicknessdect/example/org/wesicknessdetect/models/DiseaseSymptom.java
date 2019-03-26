package wesicknessdect.example.org.wesicknessdetect.models;

import com.google.gson.annotations.SerializedName;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {
        @ForeignKey(
                entity = Disease.class,
                parentColumns = "id",
                childColumns = "disease_id"),
        @ForeignKey(
                entity = Symptom.class,
                parentColumns = "id",
                childColumns = "symptom_id")
},indices = {@Index("disease_id"),@Index("symptom_id")})
public class DiseaseSymptom {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @SerializedName(value = "id")
    private long res_id;

    private long disease_id;
    private long symptom_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getDisease_id() {
        return disease_id;
    }

    public void setDisease_id(long disease_id) {
        this.disease_id = disease_id;
    }

    public long getSymptom_id() {
        return symptom_id;
    }

    public void setSymptom_id(long symptom_id) {
        this.symptom_id = symptom_id;
    }

    public long getRes_id() {
        return res_id;
    }

    public void setRes_id(long res_id) {
        this.res_id = res_id;
    }
}
