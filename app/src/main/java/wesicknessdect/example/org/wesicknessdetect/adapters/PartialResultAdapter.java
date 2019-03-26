package wesicknessdect.example.org.wesicknessdetect.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.activities.tensorflow.Classifier;

public class PartialResultAdapter extends RecyclerView.Adapter<PartialResultAdapter.ImageHolder> {

    Activity context;
    Map<Integer, Classifier.Recognition> recognitionMap=new HashMap<>();
    HashMap<Integer, String> culturePart_image;

    public PartialResultAdapter(Activity context, Map<Integer, Classifier.Recognition> recognitionMap, HashMap<Integer, String> culturePart_image) {
        this.context = context;
        this.recognitionMap = recognitionMap;
        this.culturePart_image = culturePart_image;
    }

    @NonNull
    @Override
    public ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_partial_results,
                parent, false);
        return new ImageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageHolder holder, int position) {
        for (Map.Entry<Integer, String> entry : culturePart_image.entrySet()) {
            Log.e("adapter entry", entry.getKey() + "/" + entry.getValue() + "//" );

        }
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ImageHolder extends RecyclerView.ViewHolder{

        ImageView image;

        public ImageHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
