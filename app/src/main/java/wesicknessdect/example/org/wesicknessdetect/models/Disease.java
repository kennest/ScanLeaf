package wesicknessdect.example.org.wesicknessdetect.models;

import com.google.gson.annotations.SerializedName;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(entity = Method.class,
        parentColumns = "id",
        childColumns = "method_id"),
        indices = {@Index("method_id")})
public class Disease {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @SerializedName(value = "id")
    private long res_id;

    private String nomMal;
    private long method_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomMal() {
        return nomMal;
    }

    public void setNomMal(String nomMal) {
        this.nomMal = nomMal;
    }

    public long getMethod_id() {
        return method_id;
    }

    public void setMethod_id(long method_id) {
        this.method_id = method_id;
    }

    public long getRes_id() {
        return res_id;
    }

    public void setRes_id(long res_id) {
        this.res_id = res_id;
    }
}
