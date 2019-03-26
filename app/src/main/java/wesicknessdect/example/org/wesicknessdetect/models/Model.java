package wesicknessdect.example.org.wesicknessdetect.models;

import com.google.gson.annotations.SerializedName;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(entity = CulturePart.class,
        parentColumns = "id",
        childColumns = "part_id"),
        indices = {@Index("part_id")})
public class Model {

    @SerializedName(value = "id")
    @PrimaryKey
    private int id;

    @SerializedName(value = "name")
    private String name;

    @SerializedName(value = "pb")
    private String pb;

    @SerializedName(value = "labels")
    private String label;

    @SerializedName(value = "part")
    private int part_id;


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

    public String getPb() {
        return pb;
    }

    public void setPb(String pb) {
        this.pb = pb;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getPart_id() {
        return part_id;
    }

    public void setPart_id(int part_id) {
        this.part_id = part_id;
    }

}
