package wesicknessdect.example.org.wescanleaf.models;

import java.util.List;

import androidx.room.Embedded;
import androidx.room.Relation;

public class DiagnosticPictures {
    @Embedded
    public Diagnostic diagnostic;
    @Relation(parentColumn = "x", entityColumn = "diagnostic_id", entity = Picture.class)
    public List<Picture> pictures;

    public Diagnostic getDiagnostic() {
        return diagnostic;
    }

    public void setDiagnostic(Diagnostic diagnostic) {
        this.diagnostic = diagnostic;
    }

    public List<Picture> getPictures() {
        return pictures;
    }

    public void setPictures(List<Picture> pictures) {
        this.pictures = pictures;
    }
}
