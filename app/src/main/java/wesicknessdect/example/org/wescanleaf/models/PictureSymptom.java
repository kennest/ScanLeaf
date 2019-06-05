package wesicknessdect.example.org.wescanleaf.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {
        @ForeignKey(
                entity = Picture.class,
                parentColumns = "x",
                childColumns = "picture_id"),
        @ForeignKey(
                entity = Symptom.class,
                parentColumns = "id",
                childColumns = "symptom_id")
},indices = {@Index("picture_id"),@Index("symptom_id")})
public class PictureSymptom  {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private long picture_id;
    private long symptom_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getPicture_id() {
        return picture_id;
    }

    public void setPicture_id(long picture_id) {
        this.picture_id = picture_id;
    }

    public long getSymptom_id() {
        return symptom_id;
    }

    public void setSymptom_id(long symptom_id) {
        this.symptom_id = symptom_id;
    }

}
