package wesicknessdect.example.org.wesicknessdetect.activities;

import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.activities.tensorflow.Classifier;
import wesicknessdect.example.org.wesicknessdetect.adapters.PartialResultAdapter;

public class PartialResultActivity extends BaseActivity {

    @BindView(R.id.analysed_img)
    RecyclerView images_analysed_lv;
    String TAG="PartialResultActivity";

    Map<Integer, List<Classifier.Recognition>> recognitions_by_part =new HashMap<>();
    HashMap<Integer, String> images_by_parts =new HashMap<>();
    private Map<Integer,Map<Integer, String>> images_by_part_adapter=new HashMap<>();
    PartialResultAdapter partialResultAdapter;
    int index=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partial_results);
        ButterKnife.bind(this);
        Gson gson=new Gson();
        Type typeOfHashMap = new TypeToken<Map<Integer, List<Classifier.Recognition>>>() { }.getType();
        Type typeOfHashMap2 = new TypeToken<Map<Integer, String>>() { }.getType();

        String recognitions_json = getIntent().getStringExtra("recognitions_by_part");
        String images_json = getIntent().getStringExtra("images_by_part");

        recognitions_by_part=gson.fromJson(recognitions_json,typeOfHashMap);
        images_by_parts=gson.fromJson(images_json,typeOfHashMap2);

        for(Map.Entry<Integer,String> entry:images_by_parts.entrySet()){
            Map<Integer, String> map=new HashMap<>();
            map.put(entry.getKey(),entry.getValue());
            images_by_part_adapter.put(index,map);
            index=+1;
        }
        Log.e(TAG+" map size",images_by_part_adapter.size()+"");
        partialResultAdapter=new PartialResultAdapter(this, recognitions_by_part, images_by_part_adapter);
        images_analysed_lv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        images_analysed_lv.setAdapter(partialResultAdapter);
    }
}
