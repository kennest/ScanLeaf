package wesicknessdect.example.org.wesicknessdetect.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StruggleResponse {
    @SerializedName(value = "results")
    List<Struggle> result;

    public List<Struggle> getResult() {
        return result;
    }

    public void setResult(List<Struggle> result) {
        this.result = result;
    }
}
