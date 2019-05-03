package wesicknessdect.example.org.wesicknessdetect.models;

import android.graphics.RectF;

import com.google.gson.annotations.SerializedName;

import androidx.room.Entity;

import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(indices = @Index("uuid"))
public class SymptomRect extends RectF {

    @SerializedName(value = "id_mobile")
    @PrimaryKey(autoGenerate = true)
    public int x;

    @SerializedName(value = "id")
    private int remote_id;

    @SerializedName(value = "uuid")
    private String uuid;

    @SerializedName(value = "picture_uuid")
    private String picture_uuid;

    @SerializedName(value = "symptom")
    public int symptom_id;

    @SerializedName(value = "picture")
    public int picture_id;

    public String label;

    public int sended;

    public int getSymptom_id() {
        return symptom_id;
    }

    public void setSymptom_id(int symptom_id) {
        this.symptom_id = symptom_id;
    }

    public int getPicture_id() {
        return picture_id;
    }

    public void setPicture_id(int picture_id) {
        this.picture_id = picture_id;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getSended() {
        return sended;
    }

    public void setSended(int sended) {
        this.sended = sended;
    }

    public int getRemote_id() {
        return remote_id;
    }

    public void setRemote_id(int remote_id) {
        this.remote_id = remote_id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPicture_uuid() {
        return picture_uuid;
    }

    public void setPicture_uuid(String picture_uuid) {
        this.picture_uuid = picture_uuid;
    }
}
