package wesicknessdect.example.org.wesicknessdetect.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import wesicknessdect.example.org.wesicknessdetect.MaladiePage;
import wesicknessdect.example.org.wesicknessdetect.R;

/**
 * Created by Yugansh Tyagi on 3/22/2018.
 */

public class DiseaseAdapter extends RecyclerView.Adapter<DiseaseAdapter.ChatHolder> {

    Context context;

    public DiseaseAdapter(Context context) {
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.disease_item,
                parent,false);

        return new ChatHolder(view);
    }

    public void LancerWeb(String url){
        Intent i = new Intent(context, MaladiePage.class);
        i.putExtra("page", url);
        context.startActivity(i);

    }
    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull ChatHolder c, int position) {
        Log.v("DiseaseAdapter ", "onBindViewHolder position "+position);

        switch (position) {
            case 0:
                c.maladieImage.setImageResource(R.drawable.pbrune);
                c.maladieName.setText("Pourriture brune");
                c.maladie_desc.setText("La pourriture brune est une maladie du  ...");
                c.url = "https://scanleaf.000webhostapp.com/Maladies/Pourriture%20brune/pourriture_brune.html";
                c.maladie_url.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.v("DiseaseAdapter ", "onBindViewHolder "+c.url);
                        LancerWeb(c.url);
                    }
                });

                break;

            case 1:

                c.maladieImage.setImageResource(R.drawable.swollen);
                c.maladieName.setText("Swollen shoot");
                c.maladie_desc.setText("Le swollen shoot est une maladie du  ...");
                c.url = "https://scanleaf.000webhostapp.com/Maladies/Swollen%20shoot/swollen_shoot.html";
                c.maladie_url.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.v("DiseaseAdapter ", "onBindViewHolder "+c.url);
                        LancerWeb(c.url);
                    }
                });

                break;

        }

        //Glide.with(context).load(R.drawable.plante).into(((ChatHolder) holder).maladieImage);
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public class ChatHolder extends RecyclerView.ViewHolder{

        ImageView maladieImage;
        TextView maladieName, maladie_desc, maladie_url;
        String url;
        RelativeLayout rl;

        public ChatHolder(View itemView) {
            super(itemView);
            maladieImage = itemView.findViewById(R.id.maladie_image);
            maladieName = itemView.findViewById(R.id.maladie_name);
            maladie_desc = itemView.findViewById(R.id.maladie_desc);
            maladie_url= itemView.findViewById(R.id.maladie_icon);
            rl=itemView.findViewById(R.id.disease);
            url="";

            Log.v("DiseaseAdapter ", "ChatHolder position ");

        }
    }

}
