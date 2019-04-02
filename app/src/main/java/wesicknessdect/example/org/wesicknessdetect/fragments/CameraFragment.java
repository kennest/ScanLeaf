package wesicknessdect.example.org.wesicknessdetect.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fxn.pix.Options;
import com.fxn.pix.Pix;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.activities.ChooseCultureActivity;
import wesicknessdect.example.org.wesicknessdetect.activities.ChooseCulturePartActivity;
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

        AppDatabase.getInstance(getContext()).cultureDao().getAll().observe(this, new Observer<List<Culture>>() {
            @Override
            public void onChanged(List<Culture> cultures) {
                cultureAdapter=new CultureAdapter(cultures,getActivity());
                culture_lv.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                culture_lv.setAdapter(cultureAdapter);
            }
        });
    }


}
