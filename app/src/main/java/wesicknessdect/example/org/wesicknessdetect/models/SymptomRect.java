package wesicknessdect.example.org.wesicknessdetect.models;

import android.graphics.RectF;

import androidx.room.Entity;

import androidx.room.PrimaryKey;

@Entity
public class SymptomRect extends RectF {
    @PrimaryKey(autoGenerate = true)
    private long id;
    public long symptom_id;
    public long picture_id;
    public boolean sended;

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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isSended() {
        return sended;
    }

    public void setSended(boolean sended) {
        this.sended = sended;
    }
}
