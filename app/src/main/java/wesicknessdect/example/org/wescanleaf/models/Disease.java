package wesicknessdect.example.org.wescanleaf.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(entity = Struggle.class,
        parentColumns = "id",
        childColumns = "struggle_id"),
        indices = {@Index("struggle_id")})
public class Disease {
    @SerializedName(value = "id")
    @PrimaryKey
    private int id;

    @SerializedName(value = "name")
    private String name;
    @SerializedName(value = "description")
    private String description;
    @SerializedName(value = "link")
    private String link;
    @SerializedName(value = "struggle")
    private long struggle_id;

    @Ignore
    @SerializedName(value = "symptom")
    private List<Integer> symptoms;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public long getStruggle_id() {
        return struggle_id;
    }

    public void setStruggle_id(long struggle_id) {
        this.struggle_id = struggle_id;
    }

    public List<Integer> getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(List<Integer> symptoms) {
        this.symptoms = symptoms;
    }
}
