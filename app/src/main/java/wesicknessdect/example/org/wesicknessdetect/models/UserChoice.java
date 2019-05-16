package wesicknessdect.example.org.wesicknessdetect.models;

import com.google.gson.annotations.SerializedName;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {
        @ForeignKey(
                entity = Symptom.class,
                parentColumns = "id",
                childColumns = "symptom_id")
},indices = {@Index("symptom_id")})
public class UserChoice {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @SerializedName(value = "id")
    private long remote_id;

    @SerializedName(value = "diagnostic_uuid")
    private String diagnostic_uuid;

    private long symptom_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDiagnostic_uuid() {
        return diagnostic_uuid;
    }

    public void setDiagnostic_uuid(String diagnostic_uuid) {
        this.diagnostic_uuid = diagnostic_uuid;
    }

    public long getSymptom_id() {
        return symptom_id;
    }

    public void setSymptom_id(long symptom_id) {
        this.symptom_id = symptom_id;
    }

    public long getRemote_id() {
        return remote_id;
    }

    public void setRemote_id(long remote_id) {
        this.remote_id = remote_id;
    }
}
