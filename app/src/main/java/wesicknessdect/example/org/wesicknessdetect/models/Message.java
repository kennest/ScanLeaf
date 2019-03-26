package wesicknessdect.example.org.wesicknessdetect.models;

import com.google.gson.annotations.SerializedName;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {@ForeignKey(entity = Diagnostic.class, parentColumns = "id", childColumns = "diagnostic_id")},
        indices = {@Index("diagnostic_id")})
public class Message {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @SerializedName(value = "id")
    private long res_id;

    private String content;
    private long diagnostic_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getDiagnostic_id() {
        return diagnostic_id;
    }

    public void setDiagnostic_id(long diagnostic_id) {
        this.diagnostic_id = diagnostic_id;
    }

    public long getRes_id() {
        return res_id;
    }

    public void setRes_id(long res_id) {
        this.res_id = res_id;
    }
}
