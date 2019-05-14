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
                if (index <= culture_part_id.size()) {
                    InitQuiz(culture_part_id.get(index));
                    index = index+1;
                }
            }
        });

//        DB.culturePartsDao().getAll().observe(this, new Observer<List<CulturePart>>() {
//            @Override
//            public void onChanged(List<CulturePart> cultureParts) {
//                for (CulturePart c : cultureParts) {
//
//                    List<Symptom> symptomsList = new ArrayList<>();
//                    DB.questionDao().getAll().observe(QuizActivity.this, new Observer<List<Question>>() {
//                        @Override
//                        public void onChanged(List<Question> questions) {
//                            for (Question q : questions) {
//                                if (q.getPart_culture_id() == c.getId()) {
//                                    DB.symptomDao().getAll().observe(QuizActivity.this, new Observer<List<Symptom>>() {
//                                        @Override
//                                        public void onChanged(List<Symptom> symptoms) {
//                                            for (Symptom sy : symptoms) {
//                                                if (sy.getQuestion_id() == q.getId()) {
//                                                    symptomsList.add(sy);
//                                                }
//                                            }
//                                            q.setSymptomList(symptomsList);
//                                            map.put(c, q);
//                                            list.add(map);
//                                            adapter = new QuizAdapter(list, QuizActivity.this);
//                                        }
//                                    });
//                                }
//                            }
//                        }
//                    });
//                }
//            }
//
//        });
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


    public void init(List<CulturePart> cultureParts, JSONObject jsonObject, int i1) {
        JSONArray culturepartarray = null;
        try {
            culturepartarray = (JSONArray) jsonObject.getJSONArray(cultureParts.get(i1).toString());

            Log.d("Culture" + i1, cultureParts.get(i1).toString());
            Log.d("Culture" + i1 + "nom", cultureParts.get(i1).getNom());

            Log.d("CulturePartArrayLength", String.valueOf(culturepartarray.length()));
            DB.questionDao().getAll().observe(this, new Observer<List<Question>>() {
                @Override
                public void onChanged(List<Question> questions) {
                    Log.d("QUESTIONS", questions.toString());
                    Log.d("QUESTIONSCOUNT", String.valueOf(questions.size()));
                    for (Question q : questions) {
                        if (q.getPart_culture_id() == cultureParts.get(i1).getId()) {
                            //mQuestionView.setText(q.getQuestion());
                            DB.symptomDao().getAll().observe(QuizActivity.this, new Observer<List<Symptom>>() {
                                @Override
                                public void onChanged(List<Symptom> symptoms) {
                                    List<Integer> integers = new ArrayList<>();
                                    List<CheckBox> checkBoxes = new ArrayList<>();
                                    List<Button> inforbulles = new ArrayList<>();
                                    for (Symptom s : symptoms) {
                                        if (s.getQuestion_id() == q.getId()) {
                                            CheckBox ch = new CheckBox(QuizActivity.this);
                                            ch.setText(s.getName());
                                            ch.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLightPix));
                                            ch.setPadding(5, 5, 5, 5);
                                            ch.setTextSize(14);
                                            ch.setTextColor(getResources().getColor(R.color.white));
//                                                infos.setOnClickListener();
                                            ch.setOnLongClickListener(new View.OnLongClickListener() {
                                                @Override
                                                public boolean onLongClick(View v) {
                                                    WebView webView = new WebView(QuizActivity.this);
//                                                        webView.loadUrl(s.getLink());
                                                    webView.loadData("<p style=\"background-color:#00574B; color:white \" align=\"center\">Voici comment se présente <br/><b>" + s.getName() + "</b><br/><br/>(A remplacer ce text par la page web correspondante...) !</p>", "text/html", "utf-8");

                                                    AlertDialog.Builder builder = new AlertDialog.Builder(QuizActivity.this);
                                                    builder.setTitle("Infos sur " + s.getName())
                                                            .setView(webView)
                                                            .setNeutralButton("OK", null)
                                                            .show();
                                                    return false;
                                                }
                                            });
                                            ch.setId(s.getId());

                                            if (integers.isEmpty()) {
                                                integers.add(ch.getId());
                                                checkBoxes.add(ch);
                                                //inforbulles.add(infos);
                                            } else {

                                                if (!integers.contains(ch.getId())) {
                                                    integers.add(ch.getId());
                                                    checkBoxes.add(ch);
                                                    // inforbulles.add(infos);
                                                }
                                            }


                                        }
                                    }

                                    Integer[] a = {1, 2, 3, 4, 5, 10, 14, 15, 16, 17};
                                    Log.d("pourriture brune", Arrays.asList(a).toString());

                                    Integer[] b = {4, 6, 7, 8, 9, 11, 12, 13, 18, 19, 20, 21, 22, 23, 24};
                                    Log.d("swollen shoot", Arrays.asList(b).toString());

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
                                                    if (Arrays.asList(a)
                                                            .contains(c.getId())) {
                                                        mScorepb += 1;
                                                        //scorepb.setText("" + mScorepb);

                                                    }
                                                    if (Arrays.asList(b)
                                                            .contains(c.getId())) {
                                                        mScoress += 1;
                                                        //scoress.setText("" + mScoress);

                                                    }
                                                } else {
                                                    c.setTextColor(getResources().getColor(R.color.white));
                                                    c.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLightPix));
                                                    if (Arrays.asList(a)
                                                            .contains(c.getId())) {
                                                        mScorepb -= 1;
                                                        //scorepb.setText("" + mScorepb);

                                                    }
                                                    if (Arrays.asList(b)
                                                            .contains(c.getId())) {
                                                        mScoress -= 1;
                                                        //scoress.setText("" + mScoress);

                                                    }
                                                }
                                            }
                                        });
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
        if (i1 < cultureParts.size() - 1) {
            nexquit.setText("Suivant");
            nexquit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ll.removeAllViews();

                    init(cultureParts, jsonObject, i1 + 1);

                }
            });
        } else {
            nexquit.setText("Envoyer");
            nexquit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mScoress < mScorepb) {
                        //mQuestionView.setText("Maladie obtenue via questionnaire:\n Pourriture brune ");
                    }

                    if (mScoress > mScorepb) {
                        //mQuestionView.setText("Maladie obtenue via questionnaire:\n Swollen Shoot ");
                    }

                    if (mScoress == mScorepb) {
                        if (mScorepb == 0) {
                            //mQuestionView.setText("Il semblerait que votre plante ne présente aucune maladie ");
                        } else {
                            //mQuestionView.setText("Il semblerait que votre plante soit atteinte des deux maladies: \n-Swollen Shoot \n-Pourriture Brune ");
                        }
                    }
                    nexquit.setVisibility(View.GONE);
                    ll.removeAllViews();
                }
            });
        }
    }


}
