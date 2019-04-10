package wesicknessdect.example.org.wesicknessdetect.models;

import com.google.gson.annotations.SerializedName;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
@Entity(foreignKeys = {
        @ForeignKey(
                entity = Message.class,
                parentColumns = "id",
                childColumns = "message_id"),
        @ForeignKey(
                entity = Picture.class,
                parentColumns = "x",
                childColumns = "picture_id")
},indices = {@Index("message_id"),@Index("picture_id")})
public class MessagePicture {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @SerializedName(value = "id")
    private long res_id;
    private long message_id;
    private long picture_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getMessage_id() {
        return message_id;
    }

    public void setMessage_id(long message_id) {
        this.message_id = message_id;
    }

    public long getPicture_id() {
        return picture_id;
    }

    public void setPicture_id(long picture_id) {
        this.picture_id = picture_id;
    }

    public long getRes_id() {
        return res_id;
    }

    public void setRes_id(long res_id) {
        this.res_id = res_id;
    }
}
