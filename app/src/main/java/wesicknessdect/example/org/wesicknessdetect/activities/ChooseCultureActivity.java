package wesicknessdect.example.org.wesicknessdetect.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.adapters.CultureAdapter;
import wesicknessdect.example.org.wesicknessdetect.database.AppDatabase;
import wesicknessdect.example.org.wesicknessdetect.models.Culture;

public class ChooseCultureActivity extends Fragment {
    @BindView(R.id.culture_lv)
    RecyclerView culture_lv;

    CultureAdapter cultureAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v=inflater.inflate(R.layout.activity_choix_culture, null, false);
        ButterKnife.bind(v);
//        HashMap<Question,List<Symptom>> questionListHashMap=new HashMap<>();
//        HashMap<CulturePart,List<HashMap<Question,List<Symptom>>>> culturePartListHashMap=new HashMap<>();
//        List<HashMap<Question,List<Symptom>>> hashMaps=new ArrayList<>();
//        DB.culturePartsDao().getAll().observe(this, new Observer<List<CulturePart>>() {
//            @Override
//            public void onChanged(List<CulturePart> cultureParts) {
//                for (CulturePart c:cultureParts){
//
//                    List<Symptom> symptomsList = new ArrayList<>();
//                    DB.questionDao().getAll().observe(ChooseCultureActivity.this, new Observer<List<Question>>() {
//                        @Override
//                        public void onChanged(List<Question> questions) {
//                            for (Question q:questions){
//                                if (q.getPart_culture_id()==c.getId()){
//                                    DB.symptomDao().getAll().observe(ChooseCultureActivity.this, new Observer<List<Symptom>>() {
//                                        @Override
//                                        public void onChanged(List<Symptom> symptoms) {
//                                            for (Symptom sy:symptoms){
//                                                if (sy.getQuestion_id()==q.getId()){
//                                                    symptomsList.add(sy);
//                                                }
//                                            }
//                                            questionListHashMap.put(q,symptomsList);
//                                        }
//                                    });
//                                }
//                            }
//                            hashMaps.add(questionListHashMap);
//                        }
//                    });
//                    culturePartListHashMap.put(c,hashMaps);
//                }
//
//            }
//        });
//        String all=culturePartListHashMap.toString();
//        Log.d("questionnaire", all);
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppDatabase.getInstance(getContext()).cultureDao().getAll().observe(this, new Observer<List<Culture>>() {
            @Override
            public void onChanged(List<Culture> cultures) {
                //Log.e("Cultures DB",cultures.size()+"");
                cultureAdapter=new CultureAdapter(cultures,getActivity());
                culture_lv.setAdapter(cultureAdapter);
            }
        });

    }
}
