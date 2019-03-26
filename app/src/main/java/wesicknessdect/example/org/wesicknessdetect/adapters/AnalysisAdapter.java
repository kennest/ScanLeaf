package wesicknessdect.example.org.wesicknessdetect.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import wesicknessdect.example.org.wesicknessdetect.R;

/**
 * Created by Jordan Adopo on 03/02/2019.
 */

public class AnalysisAdapter extends RecyclerView.Adapter {

    Context context;

    public AnalysisAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.analysis_item,
                parent,
                false);
        return new StatusHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //Glide.with(context).load(R.drawable.plante_sample).into(((StatusHolder)holder).analyseImage);
        ((StatusHolder) holder).userName.setText("Tige de cacao");
        ((StatusHolder) holder).analyseTime.setText("9 minute ago");
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    class StatusHolder extends RecyclerView.ViewHolder{

        ImageView analyseImage;
        TextView userName,analyseTime;

        public StatusHolder(View itemView) {
            super(itemView);
            analyseImage = itemView.findViewById(R.id.analyse_image);
            userName = itemView.findViewById(R.id.user_name);
            analyseTime = itemView.findViewById(R.id.analyse_time);
        }
    }
}
