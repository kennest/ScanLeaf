package wesicknessdect.example.org.wesicknessdetect.models;

import com.google.gson.annotations.SerializedName;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {
        @ForeignKey(
                entity = Diagnostic.class,
                parentColumns = "x",
                childColumns = "diagnostic_id"),
        @ForeignKey(
                entity = Symptom.class,
                parentColumns = "id",
                childColumns = "symptom_id")
},indices = {@Index("diagnostic_id"),@Index("symptom_id")})
public class UserChoice {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @SerializedName(value = "id")
    private long res_id;
    private String value;
    private long diagnostic_id;
    private long symptom_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public long getDiagnostic_id() {
        return diagnostic_id;
    }

    public void setDiagnostic_id(long diagnostic_id) {
        this.diagnostic_id = diagnostic_id;
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
