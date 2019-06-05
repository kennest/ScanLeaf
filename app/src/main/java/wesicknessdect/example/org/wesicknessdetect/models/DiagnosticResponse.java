package wesicknessdect.example.org.wesicknessdetect.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DiagnosticResponse {
    @SerializedName(value = "results")
    List<Diagnostic> result;

    public List<Diagnostic> getResult() {
        return result;
    }

    public void setResult(List<Diagnostic> result) {
        this.result = result;
    }
}
