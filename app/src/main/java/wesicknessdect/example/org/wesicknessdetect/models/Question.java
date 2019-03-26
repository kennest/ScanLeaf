package wesicknessdect.example.org.wesicknessdetect.models;

import com.google.gson.annotations.SerializedName;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {@ForeignKey(entity = CulturePart.class,parentColumns = "id",childColumns = "part_culture_id")},
        indices = {@Index("part_culture_id")})
public class Question  {
    @SerializedName(value = "id")
    @PrimaryKey
    private int id;

    @SerializedName(value = "question")
    private String question;

    @SerializedName(value = "partCulture")
    private long part_culture_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public long getPart_culture_id() {
        return part_culture_id;
    }

    public void setPart_culture_id(long part_culture_id) {
        this.part_culture_id = part_culture_id;
    }

}
