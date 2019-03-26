package wesicknessdect.example.org.wesicknessdetect.activities;

import android.os.Bundle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import wesicknessdect.example.org.wesicknessdetect.activities.tensorflow.Classifier;

public class PartialResultActivity extends BaseActivity {
    Map<Integer, List<Classifier.Recognition>> recognitionsByPart=new HashMap<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
