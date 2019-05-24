package wesicknessdect.example.org.wesicknessdetect.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import androidx.lifecycle.Observer;

import butterknife.BindView;
import butterknife.ButterKnife;
import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.activities.tensorflow.Classifier;
import wesicknessdect.example.org.wesicknessdetect.adapters.QuizAdapter;
import wesicknessdect.example.org.wesicknessdetect.events.QuizCheckedEvent;
import wesicknessdect.example.org.wesicknessdetect.models.CulturePart;
import wesicknessdect.example.org.wesicknessdetect.models.Diagnostic;
import wesicknessdect.example.org.wesicknessdetect.models.Disease;
import wesicknessdect.example.org.wesicknessdetect.models.DiseaseSymptom;
import wesicknessdect.example.org.wesicknessdetect.models.Question;
import wesicknessdect.example.org.wesicknessdetect.models.Symptom;
import wesicknessdect.example.org.wesicknessdetect.models.UserChoice;
import wesicknessdect.example.org.wesicknessdetect.tasks.RemoteTasks;
import wesicknessdect.example.org.wesicknessdetect.utils.AppController;

public class QuizActivity extends BaseActivity {
    @BindView(R.id.quit)
    Button nexquit;

    @BindView(R.id.global_result)
    LinearLayout global_result;

    QuizAdapter adapter;

    @BindView(R.id.quiz_lv)
    ListView quiz_lv;

    HashMap<Integer, Set<Integer>> choices = new HashMap<>();
    Set<HashMap<Integer, Set<Integer>>> choices_set = new HashSet<>();
    List<DiseaseSymptom> diseaseSymptoms = new ArrayList<>();
    List<Disease> diseases = new ArrayList<>();
    List<UserChoice> userChoices = new ArrayList<>();

    HashMap<Long, Integer> disease_score = new HashMap<>();
    List<CulturePart> cultureParts = new ArrayList<>();
    Map<Integer, List<Classifier.Recognition>> recognitions_by_part = new HashMap<>();

    List<Integer> culture_part_id = new ArrayList<>();
    Diagnostic diagnostic = new Diagnostic();
    int index = 0;

    //int score = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        ButterKnife.bind(this);

        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<Long, Integer>>() {
        }.getType();
        Type typeOfHashMap = new TypeToken<Map<Integer, List<Classifier.Recognition>>>() {
        }.getType();

        Type diagnostictype = new TypeToken<Diagnostic>() {
        }.getType();

        String data = getIntent().getStringExtra("disease_score_gson");
        String diagnostic_gson = getIntent().getStringExtra("diagnostic_gson");
        String recognition_by_part_gson = getIntent().getStringExtra("recognition_by_part_gson");
        disease_score = gson.fromJson(data, type);
        diagnostic = gson.fromJson(diagnostic_gson, diagnostictype);
        recognitions_by_part = gson.fromJson(recognition_by_part_gson, typeOfHashMap);
        Log.e("Quiz ->", disease_score.size() + "->" + diagnostic.getUuid());

        AsyncTask.SERIAL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                cultureParts = DB.culturePartsDao().getAllSync();
                diseaseSymptoms = DB.diseaseSymptomsDao().getAllSync();
                diseases = DB.diseaseDao().getAllSync();
                for (CulturePart c : cultureParts) {
                    culture_part_id.add((int) c.getId());
                }
                for (Disease d : diseases) {
                    disease_score.put((long) d.getId(), 0);
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
                    if (index == (culture_part_id.size() - 1)) {
                        nexquit.setText("Terminer");
                        nexquit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SaveChoiceByPart(choices);
                                Log.e("Index listener 2->", index + "");

                                for (HashMap<Integer, Set<Integer>> hm : choices_set) {
                                    for (Map.Entry<Integer, Set<Integer>> entry : hm.entrySet()) {
                                        Log.e("Choices Size ->", entry.getValue().size() + "");
                                        int score = 0;
                                        Set<Integer> tmp = entry.getValue();
                                        for (Integer n : tmp) {
                                            for (DiseaseSymptom d : diseaseSymptoms) {
                                                //Log.e("Match disease 0->", d.getDisease_id() + "->"+n);
                                                if (d.getSymptom_id() == Long.valueOf(n)) {
                                                    Log.e("Match disease->", d.getDisease_id() + "->" + n + "/" + d.getSymptom_id());
                                                    score = score + 1;
                                                    disease_score.put(d.getDisease_id(), score);
                                                    //Log.e("Score ->", d.getDisease_id() + "->" + score);
                                                }
                                            }
                                        }
                                    }
                                }
                                int max = Collections.max(disease_score.values());
                                List<String> detected = new ArrayList<>();
                                String final_detected = "";
                                for (Map.Entry<Long, Integer> score_entry : disease_score.entrySet()) {
                                    Log.e("Score N->" + score_entry.getKey(), score_entry.getValue() + "->" + max);
                                    if (score_entry.getValue() == max) {
                                        for (Disease d : diseases) {
                                            if (d.getId() == score_entry.getKey()) {
                                                detected.add(d.getName());
                                                if(final_detected==""){
                                                    final_detected=d.getName();
                                                }else {
                                                    final_detected = final_detected + " et " + d.getName();
                                                }
                                                //disease.setText(d.getName().toUpperCase())
                                            }
                                        }
                                    }
                                }
                                diagnostic.setAdvancedAnalysis(final_detected);
                                TextView result = global_result.findViewById(R.id.result);
                                result.setText(final_detected);
                                global_result.setPadding(100, 450, 100, 20);
                                global_result.setVisibility(View.VISIBLE);
                                quiz_lv.setVisibility(View.GONE);
                                if (index == culture_part_id.size()) {
                                    nexquit.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            sendDiagnosticAndChoices(diagnostic, userChoices);
                                        }
                                    });
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


    //Save Diagnostic and  UserChoices to DB for sended all later
    private void sendDiagnosticAndChoices(Diagnostic d, List<UserChoice> choices) {
        d.setPictures(AppController.getInstance().getPictures());
        RemoteTasks.getInstance(this).sendDiagnostic(d, false);
        finish();
        //Thread.sleep(1000);

        AsyncTask.SERIAL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                for (UserChoice uc : choices) {
                    uc.setSended(0);
                    DB.userChoiceDao().create(uc);
                }
            }
        });

//            for(Map.Entry<Integer,List<Classifier.Recognition>> entry:recognitions_by_part.entrySet()){
//                entry.setValue(entry.getValue().subList(0,4));
//            }
        AppController.getInstance().setRecognitions_by_part(recognitions_by_part);
    }


    //Save the choices and create the UserChoice object to send it to server later
    private void SaveChoiceByPart(HashMap<Integer, Set<Integer>> choices) {
        UserChoice choice = new UserChoice();
        for (Map.Entry<Integer, Set<Integer>> n : choices.entrySet()) {
            choice.setQuestion(n.getKey());
            choice.setUuid(UUID.randomUUID().toString());
            String symptoms = "";
            for (Integer i : n.getValue()) {
                if (symptoms == "") {
                    symptoms = String.valueOf(i);
                } else {
                    symptoms = symptoms + ":" + i;
                }
            }
            choice.setSymptoms(symptoms);
            choice.setDiagnostic_uuid(diagnostic.getUuid());
        }
        userChoices.add(choice);
        choices_set.add(choices);
        Log.e("Save Choices ->", choices_set.size() + "");
    }

}
