package wesicknessdect.example.org.wescanleaf.models;

import com.google.gson.annotations.SerializedName;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Struggle {
    @SerializedName(value = "id")
    @PrimaryKey
    private long id;

    @SerializedName(value = "description")
    private String description;

    @SerializedName(value = "link")
    private String link;



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
}
