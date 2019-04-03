package wesicknessdect.example.org.wesicknessdetect.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import wesicknessdect.example.org.wesicknessdetect.MaladiePage;
import wesicknessdect.example.org.wesicknessdetect.R;

/**
 * Created by Yugansh Tyagi on 3/22/2018.
 */

public class DiseaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;

    public DiseaseAdapter(Context context) {
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.disease_item,
                parent,false);

        return new ChatHolder(view);
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        switch (position) {
            case 0:
                ((ChatHolder) holder).maladieImage.setImageResource(R.drawable.pbrune);
                ((ChatHolder) holder).maladieName.setText("Pourriture brune");
                ((ChatHolder) holder).maladie_desc.setText("La pourriture brune est une maladie du  ...");
                ((ChatHolder) holder).url = "https://scanleaf.000webhostapp.com/Maladies/Pourriture%20brune/pourriture_brune.html";

                break;

            case 1:
                ((ChatHolder) holder).maladieImage.setImageResource(R.drawable.swollen);
                ((ChatHolder) holder).maladieName.setText("Swollen shoot");
                ((ChatHolder) holder).maladie_desc.setText("Le swollen shoot est une maladie du  ...");
                ((ChatHolder) holder).url = "https://scanleaf.000webhostapp.com/Maladies/Swollen%20shoot/swollen_shoot.html";

                break;

        }

        //Glide.with(context).load(R.drawable.plante).into(((ChatHolder) holder).maladieImage);
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    private class ChatHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView maladieImage;
        TextView maladieName, maladie_desc, maladie_url;
        String url;

        public ChatHolder(View itemView) {
            super(itemView);
            maladieImage = itemView.findViewById(R.id.maladie_image);
            maladieName = itemView.findViewById(R.id.maladie_name);
            maladie_desc = itemView.findViewById(R.id.maladie_desc);
            maladie_url= itemView.findViewById(R.id.maladie_icon);

            url="";

        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(context, MaladiePage.class);
            i.putExtra("page", url);
            context.startActivity(i);


        }
    }

}
