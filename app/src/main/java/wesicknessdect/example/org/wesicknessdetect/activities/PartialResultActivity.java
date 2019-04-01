package wesicknessdect.example.org.wesicknessdetect.activities;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;

import java.lang.reflect.Type;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import butterknife.BindView;
import butterknife.ButterKnife;
import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.activities.tensorflow.Classifier;
import wesicknessdect.example.org.wesicknessdetect.adapters.PartialResultImageAdapter;
import wesicknessdect.example.org.wesicknessdetect.database.AppDatabase;
import wesicknessdect.example.org.wesicknessdetect.futuretasks.SystemTasks;
import wesicknessdect.example.org.wesicknessdetect.models.Disease;
import wesicknessdect.example.org.wesicknessdetect.models.DiseaseSymptom;
import wesicknessdect.example.org.wesicknessdetect.models.Symptom;

public class PartialResultActivity extends BaseActivity implements CardStackListener {

    @BindView(R.id.analysed_img)
    CardStackView images_analysed_lv;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.btn_save_diagnostic)
    FloatingActionButton save_diagnostic;

    @BindView(R.id.disease_txt)
    TextView disease;
    String TAG = "PartialResultActivity";

    Map<Integer, List<Classifier.Recognition>> recognitions_by_part = new HashMap<>();
    Map<Integer, String> images_by_parts = new HashMap<>();
    HashMap<Long, Integer> disease_score = new HashMap<>();
    Map<Long, Long> disease_symptoms = new HashMap<Long, Long>();
    List<Integer> img_symptoms_id = new ArrayList<>();
    Set<String> symptoms_set = new HashSet<>();
    List<Classifier.Recognition> recognitions = new ArrayList<>();
    private Map<Integer, Map<Integer, String>> images_by_part_adapter = new HashMap<>();
    PartialResultImageAdapter partialResultImageAdapter;
    CardStackLayoutManager manager;
    int index = 0;
    Map.Entry<Long, Integer> maxEntry = null;

    private static AppDatabase DB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partial_results);
        ButterKnife.bind(this);
        DB = AppDatabase.getInstance(this);
        InitCardSwipe();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                InitScoreData();
            }
        });

        //getLocation();
    }

    private void InitScoreData() {

        //Get All recognition in one list
        for (Map.Entry<Integer, List<Classifier.Recognition>> recognition_entry : recognitions_by_part.entrySet()) {
            recognitions.addAll(recognition_entry.getValue().subList(0, 4));
        }

        Log.e("All img Recognitions", recognitions.size() + "");

        //Add distinct label in a list
        for (Classifier.Recognition r : recognitions) {
            symptoms_set.add(r.getTitle().toUpperCase(Locale.ENGLISH));
        }

        Log.e("All Recognitions label", symptoms_set.size() + "");

        //Check Symptoms Table to get the id of the given label
        DB.symptomDao().getAll().observe(PartialResultActivity.this, new Observer<List<Symptom>>() {
            @Override
            public void onChanged(List<Symptom> symptoms) {
                for (Symptom s : symptoms) {
                    //Remove accent from String
                    String item = Normalizer.normalize(s.getName(), Normalizer.Form.NFD);
                    item = item.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

                    for (String n : symptoms_set) {
                        //Log.e("All Symptoms checked:", item.toUpperCase() + "//" + n);
                        if (item.toUpperCase().equals(n)) {
                            img_symptoms_id.add(s.getId());
                        }
                    }
                }
                Log.e("All img symptom id", img_symptoms_id.size() + "");
            }
        });

        DB.diseaseDao().getAll().observe(PartialResultActivity.this, new Observer<List<Disease>>() {
            @Override
            public void onChanged(List<Disease> diseases) {
                for (Disease d : diseases) {
                    disease_score.put((long) d.getId(), 0);
                }
                DB.diseaseSymptomsDao().getAll().observe(PartialResultActivity.this, new Observer<List<DiseaseSymptom>>() {
                    @Override
                    public void onChanged(List<DiseaseSymptom> diseaseSymptoms) {
                        int score = 0;
                        for (DiseaseSymptom ds : diseaseSymptoms) {
                            for (Integer i : img_symptoms_id) {
                                //Log.e("Score index", (long)i+ "//"+ds.getSymptom_id() );
                                Long l = Long.valueOf(i);
                                if (l.equals(ds.getSymptom_id())) {
                                    Log.e("Score index equal", (long) i + "//" + ds.getSymptom_id() + "//" + ds.getDisease_id());
                                    score = score + 1;
                                    disease_score.put(ds.getDisease_id(), score);
                                }
                            }
                        }


                        //Get the max value of the score map
                        Log.e("Score", disease_score.size() + "");
                        for (Map.Entry<Long, Integer> score_entry : disease_score.entrySet()) {
                            Log.e("Score " + score_entry.getKey(), score_entry.getValue() + "");
                            if (maxEntry == null || score_entry.getValue().compareTo(maxEntry.getValue()) > 0) {
                                maxEntry = score_entry;
                                DB.diseaseDao().getAll().observe(PartialResultActivity.this, new Observer<List<Disease>>() {
                                    @Override
                                    public void onChanged(List<Disease> diseases) {
                                        for (Disease d : diseases) {
                                            if (d.getId() == maxEntry.getKey()) {
                                                disease.setText(d.getName().toUpperCase());
                                            }
                                        }
                                    }
                                });
                            }
                        }

                    }
                });
            }
        });




    }

    private void InitCardSwipe() {
        Gson gson = new Gson();
        Type typeOfHashMap = new TypeToken<Map<Integer, List<Classifier.Recognition>>>() {
        }.getType();
        Type typeOfHashMap2 = new TypeToken<Map<Integer, String>>() {
        }.getType();

        String recognitions_json = getIntent().getStringExtra("recognitions_by_part");
        String images_json = getIntent().getStringExtra("images_by_part");

        Log.e(getLocalClassName() + " InitCard:", images_json);

        recognitions_by_part = gson.fromJson(recognitions_json, typeOfHashMap);
        images_by_parts = gson.fromJson(images_json, typeOfHashMap2);

        Log.e(TAG + " map size 0", images_by_parts.size() + "");

        for (Map.Entry<Integer, String> entry : images_by_parts.entrySet()) {
            Map<Integer, String> map = new HashMap<>();
            map.put(entry.getKey(), entry.getValue());
            images_by_part_adapter.put(index, map);
            index = index + 1;
            Log.e(getLocalClassName() + " Index:", index + "");
        }

        Log.e(TAG + " map size", images_by_part_adapter.size() + "");
        manager = new CardStackLayoutManager(this, this);
        manager.setDirections(Direction.HORIZONTAL);
        manager.setCanScrollHorizontal(true);
        manager.setCanScrollVertical(false);
        manager.setSwipeThreshold(0.3f);
        manager.setStackFrom(StackFrom.Top);
        manager.setTranslationInterval(8f);
        partialResultImageAdapter = new PartialResultImageAdapter(this, recognitions_by_part, images_by_part_adapter);
        manager.setVisibleCount(partialResultImageAdapter.getItemCount());
        images_analysed_lv.setLayoutManager(manager);
        images_analysed_lv.setAdapter(partialResultImageAdapter);
    }

    @Override
    public void onCardDragging(Direction direction, float ratio) {

    }

    @Override
    public void onCardSwiped(Direction direction) {
        Log.e("Card Swiped", manager.getTopPosition() + "/****/" + partialResultImageAdapter.getItemCount());
        if (manager.getTopPosition() == partialResultImageAdapter.getItemCount()) {
//            manager = new CardStackLayoutManager(this, this);
//            manager.setDirections(Direction.HORIZONTAL);
//            manager.setCanScrollHorizontal(true);
//            manager.setCanScrollVertical(false);
//            manager.setSwipeThreshold(0.3f);
//            manager.setStackFrom(StackFrom.Top);
//            manager.setTranslationInterval(8f);
//
//            partialResultImageAdapter = new PartialResultImageAdapter(this, recognitions_by_part, images_by_part_adapter);
//            manager.setVisibleCount(partialResultImageAdapter.getItemCount());
//            images_analysed_lv.setLayoutManager(manager);
//            images_analysed_lv.setAdapter(partialResultImageAdapter);
//            partialResultImageAdapter.notifyDataSetChanged();
//
////            RewindAnimationSetting setting = new RewindAnimationSetting.Builder()
////                    .setDirection(Direction.Bottom)
////                    .setDuration(200)
////                    .setInterpolator(new DecelerateInterpolator())
////                    .build();
////
////            manager.setRewindAnimationSetting(setting);
//
//            //images_analysed_lv.rewind();
//            manager.smoothScrollToPosition(images_analysed_lv,null,0);
            //progressBar.setVisibility(View.VISIBLE);
            this.Reload();
        }
    }

    @SuppressLint("CheckResult")
    public void getLocation(){
        Location location= SystemTasks.getInstance(this).getLocation();
        Log.d("Location",location.getLatitude()+"//"+location.getLongitude());
    }

    @Override
    public void onCardRewound() {

        Log.e("Card Swiped", "Rewind");
    }

    @Override
    public void onCardCanceled() {

    }

    @Override
    public void onCardAppeared(View view, int position) {
        Log.e("Card Appeared ", position + "");
        if (position == 0) {
            Log.e("Card Appeared ", position + "");
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCardDisappeared(View view, int position) {
        Log.e("Card Disappeared ", position + "//" + partialResultImageAdapter.getItemCount());
        if (position == (partialResultImageAdapter.getItemCount() - 1)) {
            Log.e("Card Disappeared ", position + "//" + (partialResultImageAdapter.getItemCount() - 1));
            progressBar.setVisibility(View.VISIBLE);
        }
    }
}


