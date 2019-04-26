package wesicknessdect.example.org.wesicknessdetect.activities;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.models.CulturePart;
import wesicknessdect.example.org.wesicknessdetect.models.Question;
import wesicknessdect.example.org.wesicknessdetect.models.Symptom;

public class QuizActivity extends BaseActivity {


    private TextView mScoreView;
    ImageView iconCulture;
    private TextView mQuestionView;
    private TextView partCulture,scorepb,scoress;
    Button nexquit;
    String diagnostic;


    LinearLayout ll;
    LinearLayout ll2;
    private String mAnswer;
    private int mScorepb = 0;
    private int mScoress=0;
    private int mQuestionNumber = 0;
    HashMap<Question,List<Symptom>> questionListHashMap=new HashMap<>();
    HashMap<CulturePart,List<HashMap<Question,List<Symptom>>>> culturePartListHashMap=new HashMap<>();
    List<HashMap<Question,List<Symptom>>> hashMaps=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        nexquit=(Button) findViewById(R.id.quit);
        scorepb=(TextView) findViewById(R.id.score1);
        scoress=(TextView) findViewById(R.id.score);
        ll=(LinearLayout) findViewById(R.id.sympt);

        ll.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        ll2=(LinearLayout) findViewById(R.id.partie);
        mQuestionView = (TextView)findViewById(R.id.question);

        DB.culturePartsDao().getAll().observe(this, new Observer<List<CulturePart>>() {
            @Override
            public void onChanged(List<CulturePart> cultureParts) {
                for (CulturePart c:cultureParts){

                    List<Symptom> symptomsList = new ArrayList<>();
                    DB.questionDao().getAll().observe(QuizActivity.this, new Observer<List<Question>>() {
                        @Override
                        public void onChanged(List<Question> questions) {
                            for (Question q:questions){
                                if (q.getPart_culture_id()==c.getId()){
                                    DB.symptomDao().getAll().observe(QuizActivity.this, new Observer<List<Symptom>>() {
                                        @Override
                                        public void onChanged(List<Symptom> symptoms) {
                                            for (Symptom sy:symptoms){
                                                if (sy.getQuestion_id()==q.getId()){
                                                    symptomsList.add(sy);
                                                }
                                            }
                                            questionListHashMap.put(q,symptomsList);
                                        }
                                    });
                                }
                            }
                            hashMaps.add(questionListHashMap);
                        }
                    });
                    culturePartListHashMap.put(c,hashMaps);
                }
                Gson gson = new Gson();
                String json = gson.toJson(culturePartListHashMap);
                Log.d("questionPart", json);
                String json1=gson.toJson(hashMaps);
                Log.d("questionHashMaps", json1);
                String json2=gson.toJson(questionListHashMap);
                Log.d("questionList", json2);
                //mQuestionView.setText(json);

                try {
                    JSONObject jsonObject= new JSONObject(json);
                    int i1;
                    i1 =0;
                    int j=0;

                        init(cultureParts,jsonObject,i1);
//                        JSONArray culturepartarray = (JSONArray) jsonObject.getJSONArray(cultureParts.get(i1).toString());
//                        Log.d("Culture"+ i1,cultureParts.get(i1).toString());
//                        Log.d("Culture"+ i1 +"nom", cultureParts.get(i1).getNom());
//                        iconCulture= (ImageView) findViewById(R.id.partIcon);
//                        partCulture=(TextView) findViewById(R.id.partCulture);
//                        partCulture.setText(cultureParts.get(i1).getNom());
//                        iconCulture.setImageBitmap(BitmapFactory.decodeFile(cultureParts.get(i1).getImage()));
//                        mQuestionView.setText("Veuillez cocher ce que vous remarquez sur "+cultureParts.get(i1).getNom()+" :");
//                        while (j<culturepartarray.length()){
//                            CheckBox cb[];
//                            JSONObject question= (JSONObject) culturepartarray.getJSONObject(j);
//                            CheckBox ch = new CheckBox(QuizActivity.this);
//                            ch.setText((CharSequence) question.get("name"));
//                            ch.setId(j);
//
//                            ll.addView(ch);
//
//                            scoring(ch);
//
//                            j+=1;
//                        }
//                        if (i1 <cultureParts.size()-1){
//                            nexquit.setText("Suivant");
//                            nexquit.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//
//                                    ll.removeAllViews();
//                                    status(i1);
//
//                                }
//                            });
//                        }else {
//                            nexquit.setText("Envoyer");
//                            nexquit.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//
//                                    if(mScoress < mScorepb){
//                                        mQuestionView.setText("Maladie obtenue via questionnaire:\n Pourriture brune ");
//                                    }
//                                    if(mScoress > mScorepb){
//                                        mQuestionView.setText("Maladie obtenue via questionnaire:\n Swollen Shoot ");
//                                    }
//                                    ll.removeAllViews();
//                                    status(i1);
//                                }
//                            });
//                        }


//                    for (CulturePart c:cultureParts) {
//                        JSONObject culturepart = (JSONObject) jsonObject.getJSONObject(String.valueOf(c));
//
//                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        });
    }


    public void init(List<CulturePart> cultureParts, JSONObject jsonObject, int i1){
        int k=0;
        JSONArray culturepartarray = null;
        try {
            culturepartarray = (JSONArray) jsonObject.getJSONArray(cultureParts.get(i1).toString());

        Log.d("Culture"+ i1,cultureParts.get(i1).toString());
        Log.d("Culture"+ i1 +"nom", cultureParts.get(i1).getNom());
        iconCulture= (ImageView) findViewById(R.id.partIcon);
        partCulture=(TextView) findViewById(R.id.partCulture);
        partCulture.setText(cultureParts.get(i1).getNom());
//        mQuestionView.setText("Veuillez cocher ce que vous observez sur les "+cultureParts.get(i1).getNom()+" :");
        iconCulture.setImageBitmap(BitmapFactory.decodeFile(cultureParts.get(i1).getImage()));
        Log.d("CulturePartArrayLength", String.valueOf(culturepartarray.length()));
//        while (j<culturepartarray.length()){
//            CheckBox cb[];
//            JSONObject questionObject= (JSONObject) culturepartarray.getJSONObject(j);
//            Log.d("CultureQuestObject",questionObject.toString());
//            JSONArray questionArray=(JSONArray) questionObject.getJSONArray(cultureParts.get(j).toString());
//            Log.d("CultureQuestArray",questionArray.toString());
//
////            int finalJ2 = 0;
////            DB.questionDao().getAll().observe(QuizActivity.this, new Observer<List<Question>>() {
////                @Override
////                public void onChanged(List<Question> questions) {
////                    try {
////                        JSONArray  questionArray=null;
////                        questionArray=(JSONArray) questionObject.getJSONArray(questions.get(finalJ2).toString());
////                        mQuestionView.setText(questions.get(finalJ2).getQuestion());
//////                        Log.d("CultureQuest",questions.get(finalJ2).getQuestion());
////                        while (k<questionArray.length()) {
//////                            String question = null;
//////                            question = questionArray.getString(3);
//////                            Log.d("CultureQuestArray",question.toString());
//////                            CheckBox ch = new CheckBox(QuizActivity.this);
//////                            ch.setText(question);
//////                            Log.d("CultureSympt", question);
//////                            ch.setId(k);
//////
//////                            ll.addView(ch);
//////
//////                            scoring(ch);
//////                            JSONObject symptobject= (JSONObject) questionArray.getJSONObject(3);
//////                            Log.d("CultureQuestArray",symptobject.toString());
//////                            JSONArray symptArray= (JSONArray) symptobject.getJSONArray()
////                            status(k);
////                        }
////                    } catch (JSONException e) {
////                        e.printStackTrace();
////                    }
////                }
////            });
//
//            j+=1;
//        }
//            for (HashMap hashMap:hashMaps){
                DB.questionDao().getAll().observe(this, new Observer<List<Question>>() {
                    @Override
                    public void onChanged(List<Question> questions) {
                        Log.d("QUESTIONS", questions.toString());
                        Log.d("QUESTIONSCOUNT", String.valueOf(questions.size()));
                        for (Question q:questions){
                            if (q.getPart_culture_id()==cultureParts.get(i1).getId()){
                                mQuestionView.setText(q.getQuestion());
                                DB.symptomDao().getAll().observe(QuizActivity.this, new Observer<List<Symptom>>() {
                                    @Override
                                    public void onChanged(List<Symptom> symptoms) {
                                        List<Integer> integers=new ArrayList<>();
                                        List<CheckBox> checkBoxes=new ArrayList<>();
                                        for (Symptom s:symptoms){
                                            if (s.getQuestion_id()==q.getId()){
                                                CheckBox ch=new CheckBox(QuizActivity.this);
                                                ch.setText(s.getName());
                                                ch.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLightPix));
                                                ch.setPadding(5,5,5,5);

                                                ch.setTextSize(14);
                                                ch.setTextColor(getResources().getColor(R.color.white));
                                                ch.setId(s.getId());
                                                if (integers.isEmpty()){
                                                    integers.add(ch.getId());
                                                    checkBoxes.add(ch);
                                                }else {

                                                    if (!integers.contains(ch.getId())) {
                                                        integers.add(ch.getId());
                                                        checkBoxes.add(ch);
                                                    }
                                                }


                                            }
                                        }
                                        Integer[] a={1, 2, 3, 4, 5, 10, 14, 15, 16, 17};
                                        Log.d("pourriture brune", Arrays.asList(a).toString());

                                        Integer[] b={4, 6, 7, 8, 9, 11, 12, 13, 18, 19, 20, 21, 22, 23, 24};
                                        Log.d("swollen shoot", Arrays.asList(b).toString());
                                        for (Symptom s:symptoms) {
                                            ll.removeAllViews();
                                            for (CheckBox c : checkBoxes) {
                                                ll.removeView(c);
                                                ll.addView(c);
                                                c.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                    @Override
                                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                        if (c.isChecked()) {
                                                            c.setBackgroundColor(getResources().getColor(R.color.white));
                                                            c.setTextColor(getResources().getColor(R.color.colorPrimaryLightPix));
//                                                            for (int element : a) {
//                                                                if (element == c.) {
//                                                                    return true;
//                                                                }
//                                                            }
                                                            if (Arrays.asList(a)
                                                                    .contains(c.getId())) {
                                                                mScorepb += 1;
                                                                scorepb.setText("" + mScorepb);

                                                            }
                                                            if (Arrays.asList(b)
                                                                    .contains(c.getId())) {
                                                                mScoress += 1;
                                                                scoress.setText("" + mScoress);

                                                            }
                                                        } else {
                                                            c.setTextColor(getResources().getColor(R.color.white));
                                                            c.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLightPix));
                                                            if (Arrays.asList(a)
                                                                    .contains(c.getId())) {
                                                                mScorepb -= 1;
                                                                scorepb.setText("" + mScorepb);

                                                            }
                                                            if (Arrays.asList(b)
                                                                    .contains(c.getId())) {
                                                                mScoress -= 1;
                                                                scoress.setText("" + mScoress);

                                                            }
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    }
                });

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (i1 < cultureParts.size()-1){
            nexquit.setText("Suivant");
            nexquit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ll.removeAllViews();

                    init(cultureParts, jsonObject, i1+1);

                }
            });
        }else {
            nexquit.setText("Envoyer");
            nexquit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(mScoress < mScorepb){
                        mQuestionView.setText("Maladie obtenue via questionnaire:\n Pourriture brune ");
                    }
                    if(mScoress > mScorepb){
                        mQuestionView.setText("Maladie obtenue via questionnaire:\n Swollen Shoot ");
                    }
                    if(mScoress == mScorepb){
                        if (mScorepb==0) {
                            mQuestionView.setText("Il semblerait que votre plante ne présente aucune maladie ");
                        }else {
                            mQuestionView.setText("Il semblerait que votre plante soit atteinte des deux maladies: \n-Swollen Shoot \n-Pourriture Brune ");
                        }
                    }
                    nexquit.setVisibility(View.GONE);
                    ll.removeAllViews();
                    ll2.setVisibility(View.INVISIBLE);


                    //init(cultureParts, jsonObject, i1+1, finalJ1);
                }
            });
        }
    }






}
