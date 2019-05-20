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

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.adapters.QuizAdapter;
import wesicknessdect.example.org.wesicknessdetect.events.QuizCheckedEvent;
import wesicknessdect.example.org.wesicknessdetect.models.Culture;
import wesicknessdect.example.org.wesicknessdetect.models.CulturePart;
import wesicknessdect.example.org.wesicknessdetect.models.Diagnostic;
import wesicknessdect.example.org.wesicknessdetect.models.Disease;
import wesicknessdect.example.org.wesicknessdetect.models.DiseaseSymptom;
import wesicknessdect.example.org.wesicknessdetect.models.Question;
import wesicknessdect.example.org.wesicknessdetect.models.Symptom;
import wesicknessdect.example.org.wesicknessdetect.tasks.RemoteTasks;

public class QuizActivity extends BaseActivity {
    @BindView(R.id.quit)
    Button nexquit;

    LinearLayout ll;
    QuizAdapter adapter;

    @BindView(R.id.quiz_lv)
    ListView quiz_lv;
    HashMap<Integer, Set<Integer>> choices = new HashMap<>();
    Set<HashMap<Integer, Set<Integer>>> choices_set = new HashSet<>();
    List<DiseaseSymptom> diseases = new ArrayList<>();
    Map.Entry<Long, Integer> maxEntry = null;

    HashMap<Long, Integer> disease_score = new HashMap<>();
    List<CulturePart> cultureParts = new ArrayList<>();

    List<Integer> culture_part_id = new ArrayList<>();
    Diagnostic diagnostic = new Diagnostic();
    int index = 0;
    int score = 0;
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
        Log.e("Quiz ->", disease_score.size() + "->" + diagnostic.getUuid());

        AsyncTask.SERIAL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                cultureParts = DB.culturePartsDao().getAllSync();
                diseases = DB.diseaseSymptomsDao().getAllSync();
                for (CulturePart c : cultureParts) {
                    culture_part_id.add((int) c.getId());
                }
                InitQuiz(culture_part_id.get(index));
                index = index + 1;
            }
        });

        nexquit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Index ->", index + "");
                try {
                    //Si on est au dernier element du tableau des ID des symptomes
                    if (index == (culture_part_id.size())) {
                        nexquit.setText("Envoyer");
                        nexquit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SaveChoiceByPart(choices);
                                Log.e("Index listener 2->", index + "");

                                for(HashMap<Integer, Set<Integer>> hm:choices_set){
                                    for (Map.Entry<Integer, Set<Integer>> entry : hm.entrySet()) {
                                        Log.e("Choices Size ->",entry.getValue().size()+"");
                                        for (DiseaseSymptom d : diseases) {
                                            for (Integer n : entry.getValue()) {
                                                //Log.e("Match disease 0->", d.getDisease_id() + "->"+n);
                                                if (d.getSymptom_id() == n) {
                                                    Log.e("Match disease->", d.getDisease_id() + "->"+n);
                                                    score = score + 1;
                                                }
                                            }
                                            disease_score.put((long) d.getDisease_id(), score);
                                            Log.e("Score ->",d.getDisease_id()+"->"+score);
                                        }
                                    }
                                }

                                for (Map.Entry<Long, Integer> score_entry : disease_score.entrySet()) {
                                    //Log.e("Score " + score_entry.getKey(), score_entry.getValue() + "");
                                    if (maxEntry == null || score_entry.getValue().compareTo(maxEntry.getValue()) > 0) {
                                        maxEntry = score_entry;
                                        DB.diseaseDao().getAll().observe(QuizActivity.this, new Observer<List<Disease>>() {
                                            @Override
                                            public void onChanged(List<Disease> diseases) {
                                                for (Disease d : diseases) {
                                                    if (d.getId() == maxEntry.getKey()) {
                                                        //disease.setText(d.getName().toUpperCase());
                                                        diagnostic.setAdvancedAnalysis(d.getName());
                                                        Log.e("Advanced Analysis->",d.getName());
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        });
                    }

                    SaveChoiceByPart(choices);
                    if (index <= culture_part_id.size()) {
                        InitQuiz(culture_part_id.get(index));
                    }
                    index = index + 1;
                } catch (IndexOutOfBoundsException e) {
                    Log.e("Exception ->", e.getMessage());
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
                if (adapter != null && quiz_lv != null) {
                    adapter.notifyDataSetInvalidated();
                    quiz_lv.invalidate();
                }
                adapter = new QuizAdapter(map, QuizActivity.this, diagnostic.getUuid());
                quiz_lv.setAdapter(adapter);
            }

        });
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void GetQuizCheckedEvent(QuizCheckedEvent event) {
        Log.e("Event ->", event.choices.size() + "//" + event.part_id);
        choices = event.choices;
    }

    private void SaveChoiceByPart(HashMap<Integer, Set<Integer>> choices) {
        choices_set.add(choices);
        Log.e("Save Choices ->", choices_set.size() + "");
    }

}
