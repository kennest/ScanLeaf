package wesicknessdect.example.org.wesicknessdetect.fragments;

import android.content.Context;
import android.graphics.Color;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import wesicknessdect.example.org.wesicknessdetect.adapters.DiseaseAdapter;
import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.database.AppDatabase;
import wesicknessdect.example.org.wesicknessdetect.models.Disease;
import wesicknessdect.example.org.wesicknessdetect.ui.SeparatorDecoration;

/**
 * Created by Jordan Adopo on 10/02/2019.
 */

public class MaladiesFragment extends Fragment {

    @BindView(R.id.maladie_rv)
    RecyclerView recyclerView;
    private static AppDatabase DB;
    LayoutAnimationController controller;
    Context mContext=getContext();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DB = AppDatabase.getInstance(getContext());
        controller= AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_animation_fall_down);

        Log.v("MaladiesFragment ", "onCreateView");

        DB.diseaseDao().getAll().observe(this, new Observer<List<Disease>>() {
            @Override
            public void onChanged(List<Disease> diseases) {
//                SeparatorDecoration decoration = new SeparatorDecoration(
//                        mContext,
//                        Color.parseColor("#EAEAEA"),
//                        0.5f);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                recyclerView.setAdapter(new DiseaseAdapter(getActivity(),diseases));
                recyclerView.setLayoutAnimation(controller);
                recyclerView.scheduleLayoutAnimation();
                //recyclerView.addItemDecoration(decoration);
            }
        });

        Log.v("MaladiesFragment ", "onCreateView end");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_disease, null, false);
        ButterKnife.bind(this,view);
        return view;
    }
}
