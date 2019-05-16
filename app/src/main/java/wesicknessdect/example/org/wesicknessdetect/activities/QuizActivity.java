package wesicknessdect.example.org.wesicknessdetect.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.adapters.QuizAdapter;
import wesicknessdect.example.org.wesicknessdetect.models.Culture;
import wesicknessdect.example.org.wesicknessdetect.models.CulturePart;
import wesicknessdect.example.org.wesicknessdetect.models.Diagnostic;
import wesicknessdect.example.org.wesicknessdetect.models.Question;
import wesicknessdect.example.org.wesicknessdetect.models.Symptom;

public class QuizActivity extends BaseActivity {
    @BindView(R.id.quit)
    Button nexquit;

    LinearLayout ll;
    QuizAdapter adapter;

    @BindView(R.id.quiz_lv)
    ListView quiz_lv;
    private int mScorepb = 0;
    private int mScoress = 0;

    List<HashMap<CulturePart, Question>> list = new ArrayList<>();
    HashMap<Long, Integer> disease_score = new HashMap<>();
    List<CulturePart> cultureParts = new ArrayList<>();

    List<Integer> culture_part_id = new ArrayList<>();
    Diagnostic diagnostic=new Diagnostic();
    int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        ButterKnife.bind(this);
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<Long, Integer>>() {
        }.getType();

        Type diagnostictype = new TypeToken<Diagnostic>() {
        }.getType();

        String data = getIntent().getStringExtra("disease_score_gson");
        String diagnostic_gson = getIntent().getStringExtra("diagnostic_gson");
        disease_score = gson.fromJson(data, type);
        diagnostic = gson.fromJson(diagnostic_gson, diagnostictype);
        Log.e("Quiz ->", disease_score.size() + "->"+diagnostic.getUuid());

        AsyncTask.SERIAL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                cultureParts = DB.culturePartsDao().getAllSync();
                for (CulturePart c : cultureParts) {
                    culture_part_id.add((int) c.getId());
                }
                InitQuiz(culture_part_id.get(index));
                index = index+1;
            }
        });

        nexquit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Index ->", index + "");
                if (index <= (culture_part_id.size()-1)) {
                    InitQuiz(culture_part_id.get(index));
                    index = index+1;
                }
            }
        });
    }


    public void InitQuiz(int part_id) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                HashMap<CulturePart, Question> map = new HashMap<>();
                Log.e("Quiz Init partID->", part_id + "");
                CulturePart cp = DB.culturePartsDao().getByIdSync(part_id);

                Question question = DB.questionDao().getByPartSync(cp.getId());

                Log.e("Quiz Init QuestionID->", question.getId() + "");
                List<Symptom> symptoms = new ArrayList<>();
                symptoms = DB.symptomDao().getByQuestion(question.getId());

                List<Symptom> symptomsList = new ArrayList<>();

                for (Symptom s : symptoms) {
                    Log.e("Symptom/Question->", s.getQuestion_id() + "//" + question.getId());
                    if (s.getQuestion_id() == question.getId()) {
                        symptomsList.add(s);
                    }
                }

                question.setSymptomList(symptomsList);
                map.put(cp, question);

                //list.add(map);
                if (adapter != null && quiz_lv!=null) {
                    adapter.notifyDataSetInvalidated();
                    quiz_lv.invalidate();
                }
                adapter = new QuizAdapter(map, QuizActivity.this);
                quiz_lv.setAdapter(adapter);
            }

        });
    }

}
