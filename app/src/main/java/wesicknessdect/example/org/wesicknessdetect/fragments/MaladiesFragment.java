package wesicknessdect.example.org.wesicknessdetect.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import wesicknessdect.example.org.wesicknessdetect.MaladiePage;
import wesicknessdect.example.org.wesicknessdetect.adapters.DiseaseAdapter;
import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.ui.SeparatorDecoration;

/**
 * Created by Jordan Adopo on 10/02/2019.
 */

public class MaladiesFragment extends Fragment {

    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_disease, null, false);

        SeparatorDecoration decoration = new SeparatorDecoration(
                getContext(),
                Color.parseColor("#EAEAEA"),
                0.5f);

        Log.v("MaladiesFragment ", "onCreateView");

        recyclerView = view.findViewById(R.id.maladie_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new DiseaseAdapter(getContext()));
        recyclerView.addItemDecoration(decoration);

        Log.v("MaladiesFragment ", "onCreateView end");


        return view;
    }
}
