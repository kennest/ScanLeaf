package wesicknessdect.example.org.wesicknessdetect.models;

import android.graphics.RectF;

import androidx.room.Entity;

@Entity
public class SymptomRect extends RectF {
    public long symptom_id;
    public long picture_id;

    public long getSymptom_id() {
        return symptom_id;
    }

    public void setSymptom_id(long symptom_id) {
        this.symptom_id = symptom_id;
    }

    public long getPicture_id() {
        return picture_id;
    }

    public void setPicture_id(long picture_id) {
        this.picture_id = picture_id;
    }
}
