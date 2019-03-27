package wesicknessdect.example.org.wesicknessdetect.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.RewindAnimationSetting;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.activities.tensorflow.Classifier;
import wesicknessdect.example.org.wesicknessdetect.adapters.PartialResultImageAdapter;

public class PartialResultActivity extends BaseActivity implements CardStackListener{

    @BindView(R.id.analysed_img)
    CardStackView images_analysed_lv;
    String TAG="PartialResultActivity";

    Map<Integer, List<Classifier.Recognition>> recognitions_by_part =new HashMap<>();
    Map<Integer, String> images_by_parts =new HashMap<>();
    private Map<Integer,Map<Integer, String>> images_by_part_adapter=new HashMap<>();
    PartialResultImageAdapter partialResultImageAdapter;
    CardStackLayoutManager manager;
    int index=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partial_results);
        ButterKnife.bind(this);
        InitCardSwipe();
    }

    private void InitView(){
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
        partialResultImageAdapter =new PartialResultImageAdapter(this, recognitions_by_part, images_by_part_adapter);
        images_analysed_lv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        images_analysed_lv.setAdapter(partialResultImageAdapter);
    }

    private void InitCardSwipe(){
        Gson gson=new Gson();
        Type typeOfHashMap = new TypeToken<Map<Integer, List<Classifier.Recognition>>>() { }.getType();
        Type typeOfHashMap2 = new TypeToken<Map<Integer, String>>() { }.getType();

        String recognitions_json = getIntent().getStringExtra("recognitions_by_part");
        String images_json = getIntent().getStringExtra("images_by_part");

        Log.e(getLocalClassName()+" InitCard:",images_json);

        recognitions_by_part=gson.fromJson(recognitions_json,typeOfHashMap);
        images_by_parts=gson.fromJson(images_json,typeOfHashMap2);

        Log.e(TAG+" map size 0",images_by_parts.size()+"");

        for(Map.Entry<Integer,String> entry:images_by_parts.entrySet()){
            Map<Integer, String> map=new HashMap<>();
            map.put(entry.getKey(),entry.getValue());
            images_by_part_adapter.put(index,map);
            index=index+1;
            Log.e(getLocalClassName()+" Index:",index+"");
        }

        Log.e(TAG+" map size",images_by_part_adapter.size()+"");
        manager = new CardStackLayoutManager(this,this);
        manager.setDirections(Direction.HORIZONTAL);
        manager.setCanScrollHorizontal(true);
        manager.setCanScrollVertical(false);
        manager.setSwipeThreshold(0.3f);
        partialResultImageAdapter =new PartialResultImageAdapter(this, recognitions_by_part, images_by_part_adapter);
        images_analysed_lv.setLayoutManager(manager);
        images_analysed_lv.setAdapter(partialResultImageAdapter);
    }

    @Override
    public void onCardDragging(Direction direction, float ratio) {

    }

    @Override
    public void onCardSwiped(Direction direction) {
        Log.e("Card Swiped",manager.getTopPosition()+"/****/"+partialResultImageAdapter.getItemCount());
        if (manager.getTopPosition()==partialResultImageAdapter.getItemCount()) {

            manager = new CardStackLayoutManager(this,this);
            manager.setDirections(Direction.HORIZONTAL);
            manager.setCanScrollHorizontal(true);
            manager.setCanScrollVertical(false);
            manager.setSwipeThreshold(0.3f);
            partialResultImageAdapter =new PartialResultImageAdapter(this, recognitions_by_part, images_by_part_adapter);
            images_analysed_lv.setLayoutManager(manager);
            images_analysed_lv.setAdapter(partialResultImageAdapter);

            RewindAnimationSetting setting = new RewindAnimationSetting.Builder()
                    .setDirection(Direction.Bottom)
                    .setDuration(100)
                    .setInterpolator(new DecelerateInterpolator())
                    .build();

            manager.setRewindAnimationSetting(setting);

           images_analysed_lv.rewind();
        }
    }

    @Override
    public void onCardRewound() {
        Log.e("Card Swiped","Rewind");
    }

    @Override
    public void onCardCanceled() {

    }

    @Override
    public void onCardAppeared(View view, int position) {

    }

    @Override
    public void onCardDisappeared(View view, int position) {

    }
}


