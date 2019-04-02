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
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppDatabase.getInstance(getContext()).cultureDao().getAll().observe(this, new Observer<List<Culture>>() {
            @Override
            public void onChanged(List<Culture> cultures) {
                cultureAdapter=new CultureAdapter(cultures,getActivity());
                culture_lv.setAdapter(cultureAdapter);
            }
        });

    }
}
