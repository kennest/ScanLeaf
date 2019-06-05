package wesicknessdect.example.org.wescanleaf.models;

import com.google.gson.annotations.SerializedName;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity
public class UserChoice {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @SerializedName(value = "question")
    private long question;

    @SerializedName(value = "diagnostic_uuid")
    private String diagnostic_uuid;

    @SerializedName(value = "symptoms")
    private String symptoms;

    @SerializedName("uuid")
    private String uuid;

    private int sended=0;

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


    public long getQuestion() {
        return question;
    }

    public void setQuestion(long question) {
        this.question = question;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getSended() {
        return sended;
    }

    public void setSended(int sended) {
        this.sended = sended;
    }
}
