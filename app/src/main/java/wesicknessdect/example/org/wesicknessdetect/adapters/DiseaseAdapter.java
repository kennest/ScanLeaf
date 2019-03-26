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
 * Created by Yugansh Tyagi on 3/22/2018.
 */

public class DiseaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;

    public DiseaseAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.disease_item,
                parent,false);

        return new ChatHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ((ChatHolder) holder).maladieName.setText("Swollen shoot");
        ((ChatHolder) holder).maladie_desc.setText("Le swollen shoot est une maladie qui agit ...");
        //Glide.with(context).load(R.drawable.plante).into(((ChatHolder) holder).maladieImage);
    }

    @Override
    public int getItemCount() {
        return 20;
    }

    private class ChatHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView maladieImage;
        TextView maladieName, maladie_desc;

        public ChatHolder(View itemView) {
            super(itemView);
            maladieImage = itemView.findViewById(R.id.maladie_image);
            maladieName = itemView.findViewById(R.id.maladie_name);
            maladie_desc = itemView.findViewById(R.id.maladie_desc);
        }

        @Override
        public void onClick(View v) {

        }
    }

}
