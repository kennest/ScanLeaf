package wesicknessdect.example.org.wescanleaf.models;

import java.util.List;

import androidx.room.Embedded;
import androidx.room.Relation;

public class PictureSymptomRects {

    @Embedded
    public Picture picture;
    @Relation(parentColumn = "x", entityColumn = "picture_id", entity = SymptomRect.class)
    public List<SymptomRect> symptomRects;

    public Picture getPicture() {
        return picture;
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
    }

    public List<SymptomRect> getSymptomRects() {
        return symptomRects;
    }

    public void setSymptomRects(List<SymptomRect> symptomRects) {
        this.symptomRects = symptomRects;
    }
}
