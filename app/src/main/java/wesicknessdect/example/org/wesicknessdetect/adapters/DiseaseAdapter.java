package wesicknessdect.example.org.wesicknessdetect.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import wesicknessdect.example.org.wesicknessdetect.activities.DiseaseActivity;
import wesicknessdect.example.org.wesicknessdetect.models.Disease;
import wesicknessdect.example.org.wesicknessdetect.R;

/**
 * Created by Yugansh Tyagi on 3/22/2018.
 */

public class DiseaseAdapter extends RecyclerView.Adapter<DiseaseAdapter.ChatHolder> {

    Activity context;
    List<Disease> diseases;
    private int mExpandedPosition;

    public DiseaseAdapter(Activity context, List<Disease> diseases) {
        this.context = context;
        this.diseases = diseases;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.disease_item,
                parent, false);
        return new ChatHolder(view);
    }

    public void LancerWeb(String url) {
        Intent i = new Intent(context, DiseaseActivity.class);
        Log.e("page URL->", url);
        i.putExtra("page", url);
        context.startActivity(i);
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull ChatHolder c, int position) {
        //Log.v("DiseaseAdapter ", "onBindViewHolder position "+position);
        final boolean isExpanded = position == mExpandedPosition;

        c.maladieImage.setImageResource(R.drawable.swollen);
        c.maladieName.setText(diseases.get(position).getName());
//                if(diseases.get(position).getDescription().length()>55) {
//                    c.maladie_desc.setText(String.format("%s...", diseases.get(position).getDescription().substring(0, 35)));
//                }else{
//                    c.maladie_desc.setText(diseases.get(position).getDescription());
//                }
        c.itemView.setTag(diseases.get(position).getLink());
        Log.d("WebView Url", diseases.get(position).getLink());
        c.imbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LancerWeb(diseases.get(position).getLink());
            }
        });
    }

    @Override
    public int getItemCount() {
        return diseases.size();
    }

    public class ChatHolder extends RecyclerView.ViewHolder {

        ImageView maladieImage;
        TextView maladieName, maladie_desc, maladie_url;
        RelativeLayout rl;
        LinearLayout imbt;

        public ChatHolder(View itemView) {
            super(itemView);
            maladieImage = itemView.findViewById(R.id.maladie_image);
            maladieName = itemView.findViewById(R.id.maladie_name);
//            maladie_url= itemView.findViewById(R.id.maladie_icon);
            imbt = itemView.findViewById(R.id.btnplus);
            rl = itemView.findViewById(R.id.disease);


            Log.v("DiseaseAdapter ", "ChatHolder position ");

        }
    }

}
