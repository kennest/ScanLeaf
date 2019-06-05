package wesicknessdect.example.org.wescanleaf.tasks;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import wesicknessdect.example.org.wescanleaf.models.Culture;
import wesicknessdect.example.org.wescanleaf.models.CulturePart;
import wesicknessdect.example.org.wescanleaf.models.Diagnostic;
import wesicknessdect.example.org.wescanleaf.models.Disease;
import wesicknessdect.example.org.wescanleaf.models.Model;
import wesicknessdect.example.org.wescanleaf.models.Picture;
import wesicknessdect.example.org.wescanleaf.models.Struggle;
import wesicknessdect.example.org.wescanleaf.models.Symptom;
import wesicknessdect.example.org.wescanleaf.models.SymptomRect;

public class PaperDBTasks {
    private static PaperDBTasks paperDBTasks;
    //private static APIService service;
    private static Context mContext;
    private  static String DIAGNOSTIC="diagnostics";
    private  static String PICTURES="pictures";
    private  static String DISEASES="diseases";
    private  static String SYMPTOMS="symptoms";
    private  static String SYMPTOM_RECTS="symptom_rects";
    private  static String STRUGGLES="struggles";
    private  static String CULTURES="cultures";
    private  static String CULTURE_PARTS="culture_parts";
    private  static String MODELS="models";
    private  static String QUESTIONS="questions";

    private PaperDBTasks(Context context) {
        mContext = context;
    }  //private constructor.

    public static PaperDBTasks getInstance(Context context) {
        if (paperDBTasks == null) { //if there is no instance available... create new one
            paperDBTasks = new PaperDBTasks(context);
        }
        mContext = context;
        return paperDBTasks;
    }

    //INSERT ONE DIAGNOSTIC
    public void StoreDiagnostic(Diagnostic d){
        List<Diagnostic> diagnostics= Paper.book().read(DIAGNOSTIC);
        diagnostics.add(d);
        Paper.book().write(DIAGNOSTIC,diagnostics);
    }

    //INSERT MANY DIAGNOSTICS
    public void StoreManyDiagnostics(List<Diagnostic> diagnostics){
        List<Diagnostic> diagnosticsDB= Paper.book().read(DIAGNOSTIC);
        diagnosticsDB.addAll(diagnostics);
        Paper.book().write(DIAGNOSTIC,diagnosticsDB);
    }

    //INSERT DIAGNOSTIC AND PICTURES
    public void StoreDiagWithPictires(Diagnostic d, List<Picture> pictures){
        List<Diagnostic> diagnostics= Paper.book().read(DIAGNOSTIC,new ArrayList<>());
        diagnostics.add(d);
        Paper.book().write(DIAGNOSTIC,diagnostics);

        List<Picture> picturesDB=Paper.book().read(PICTURES,new ArrayList<>());
        for(Picture p:pictures){
            p.setDiagnostic_id(p.getX());
            picturesDB.add(p);
        }

        Paper.book().write(PICTURES,picturesDB);
    }

    //INSERT ONE PICTURE
    public void StorePicture(Picture p){
        List<Picture> picturesDB=Paper.book().read(PICTURES,new ArrayList<>());
        picturesDB.add(p);
        Paper.book().write(PICTURES,picturesDB);
    }

    //INSERT MANY PICTURES
    public void StoreManyPictures(List<Picture> pictures){
        List<Picture> picturesDB=Paper.book().read(PICTURES,new ArrayList<>());
        picturesDB.addAll(pictures);
        Paper.book().write(PICTURES,picturesDB);
    }

    //INSERT ONE DISEASE
    public void StoreDisease(Disease d){
        List<Disease> diseases=Paper.book().read(DISEASES,new ArrayList<>());
        diseases.add(d);
        Paper.book().write(DISEASES,diseases);
    }

    //INSERT MANY DISEASES
    public void StoreManyDiseases(List<Disease> diseases){
        List<Disease> diseasesDB=Paper.book().read(DISEASES,new ArrayList<>());
        diseasesDB.addAll(diseases);
        Paper.book().write(DISEASES,diseasesDB);
    }

    //INSERT ONE SYMPTOM
    public void StoreSymptoms(Symptom s){
        List<Symptom> symptomsDB=Paper.book().read(SYMPTOMS,new ArrayList<>());
        symptomsDB.add(s);
        Paper.book().write(SYMPTOMS,symptomsDB);
    }

    //INSERT MANY SYMPTOMS
    public void StoreManySymptoms(List<Symptom> symptoms){
        List<Symptom> symptomsDB=Paper.book().read(SYMPTOMS,new ArrayList<>());
        symptomsDB.addAll(symptoms);
        Paper.book().write(DISEASES,symptomsDB);
    }


    //INSERT ONE SYMPTOM_RECT
    public void StoreSymptomRects(SymptomRect s){
        List<SymptomRect> symptomRectsDB=Paper.book().read(SYMPTOM_RECTS,new ArrayList<>());
        symptomRectsDB.add(s);
        Paper.book().write(SYMPTOMS,symptomRectsDB);
    }

    //INSERT MANY SYMPTOM_RECTS
    public void StoreManySymptomRects(List<SymptomRect> symptomRects){
        List<SymptomRect> symptomRectsDB=Paper.book().read(SYMPTOM_RECTS,new ArrayList<>());
        symptomRectsDB.addAll(symptomRects);
        Paper.book().write(DISEASES,symptomRectsDB);
    }

    //INSERT ONE STRUGGLES
    public void StoreStruggles(Struggle s){
        List<Struggle> strugglesDB=Paper.book().read(STRUGGLES,new ArrayList<>());
        strugglesDB.add(s);
        Paper.book().write(STRUGGLES,strugglesDB);
    }

    //INSERT MANY STRUGGLES
    public void StoreManyStruggles(List<Struggle> struggles){
        List<Struggle> strugglesDB=Paper.book().read(STRUGGLES,new ArrayList<>());
        strugglesDB.addAll(struggles);
        Paper.book().write(DISEASES,strugglesDB);
    }

    //INSERT ONE CULTURE
    public void StoreCulture(Culture c){
        List<Culture> culturesDB=Paper.book().read(CULTURES,new ArrayList<>());
        culturesDB.add(c);
        Paper.book().write(STRUGGLES,culturesDB);
    }

    //INSERT MANY CULTURES
    public void StoreManyCultures(List<Culture> cultures){
        List<Culture> culturesDB=Paper.book().read(CULTURES,new ArrayList<>());
        culturesDB.addAll(cultures);
        Paper.book().write(CULTURES,culturesDB);
    }

    //INSERT ONE CULTURE_PART
    public void StoreCulturePart(CulturePart c){
        List<CulturePart> culturePartsDB=Paper.book().read(CULTURE_PARTS,new ArrayList<>());
        culturePartsDB.add(c);
        Paper.book().write(CULTURE_PARTS,culturePartsDB);
    }

    //INSERT MANY CULTURE_PARTS
    public void StoreManyCultureParts(List<CulturePart> cultureParts){
        List<CulturePart> culturePartsDB=Paper.book().read(CULTURE_PARTS,new ArrayList<>());
        culturePartsDB.addAll(cultureParts);
        Paper.book().write(CULTURE_PARTS,cultureParts);
    }

    //INSERT ONE MODEL
    public void StoreModel(Model m){
        List<Model> modelsDB=Paper.book().read(MODELS,new ArrayList<>());
        modelsDB.add(m);
        Paper.book().write(MODELS,modelsDB);
    }

    //INSERT MANY MODELS
    public void StoreManyModels(List<Model> models){
        List<Model> modelsDB=Paper.book().read(MODELS,new ArrayList<>());
        modelsDB.addAll(models);
        Paper.book().write(MODELS,modelsDB);
    }
}
