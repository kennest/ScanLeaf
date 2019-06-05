package wesicknessdect.example.org.wesicknessdetect.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.adapters.CultureAdapter;
import wesicknessdect.example.org.wesicknessdetect.database.AppDatabase;
import wesicknessdect.example.org.wesicknessdetect.models.Culture;

/**
 * Created by Jordan Adopo on 10/02/2019.
 */


public class CameraFragment extends Fragment {
    @BindView(R.id.culture_lv)
     RecyclerView culture_lv;

    private CultureAdapter cultureAdapter;
    LayoutAnimationController controller;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v=inflater.inflate(R.layout.activity_choix_culture, container, false);
        ButterKnife.bind(this,v);
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("Camera","Initialized");

        controller= AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_animation_fall_down);

        AppDatabase.getInstance(getContext()).cultureDao().getAll().observe(this, new Observer<List<Culture>>() {
            @Override
            public void onChanged(List<Culture> cultures) {
                Log.e("Cultures DB",cultures.size()+"");
                cultureAdapter=new CultureAdapter(cultures,getActivity());
                culture_lv.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                culture_lv.setAdapter(cultureAdapter);
                culture_lv.setLayoutAnimation(controller);
                culture_lv.scheduleLayoutAnimation();
            }
        });
    }


}
