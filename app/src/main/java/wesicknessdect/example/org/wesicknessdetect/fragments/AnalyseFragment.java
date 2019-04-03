package wesicknessdect.example.org.wesicknessdetect.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.adapters.AnalysisAdapter;
import wesicknessdect.example.org.wesicknessdetect.database.AppDatabase;
import wesicknessdect.example.org.wesicknessdetect.models.DiagnosticPictures;
import wesicknessdect.example.org.wesicknessdetect.ui.SeparatorDecoration;

/**
 * Created by Yugansh Tyagi on 3/21/2018.
 */

public class AnalyseFragment extends Fragment {

    @BindView(R.id.status_rv)
    RecyclerView recyclerView;

    @BindView(R.id.empty_data)
    View empty;

    private static AppDatabase DB;

    AnalysisAdapter analysisAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_analysis, null, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DB = AppDatabase.getInstance(getContext());
        DB.diagnosticDao().getDiagnosticWithPictures().observe(this, new Observer<List<DiagnosticPictures>>() {
            @Override
            public void onChanged(List<DiagnosticPictures> diagnosticPictures) {
                if(diagnosticPictures.size()>0){
//                    SeparatorDecoration decoration = new SeparatorDecoration(
//                            getContext(),
//                            Color.parseColor("#EAEAEA"),
//                            0.5f);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    analysisAdapter=new AnalysisAdapter(getActivity(),diagnosticPictures);
                    recyclerView.setAdapter(analysisAdapter);
                    //recyclerView.addItemDecoration(decoration);
                }else{
                    empty.setVisibility(View.VISIBLE);
                }

            }
        });
    }
}
