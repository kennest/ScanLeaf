package wesicknessdect.example.org.wesicknessdetect.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.glide.slider.library.Animations.DescriptionAnimation;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.SliderTypes.TextSliderView;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.activities.AnalysisDetailsActivity;
import wesicknessdect.example.org.wesicknessdetect.models.DiagnosticPictures;
import wesicknessdect.example.org.wesicknessdetect.models.Picture;

/**
 * Created by Jordan Adopo on 03/02/2019.
 */

public class AnalysisAdapter extends RecyclerView.Adapter<AnalysisAdapter.StatusHolder> {

    Activity context;
    List<DiagnosticPictures> diagnosticPictures;

    public AnalysisAdapter(Activity context, List<DiagnosticPictures> diagnosticPictures) {
        this.context = context;
        this.diagnosticPictures = diagnosticPictures;
    }

    @NonNull
    @Override
    public StatusHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.analysis_list_item,
                parent,
                false);
        return new StatusHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatusHolder holder, int position) {
        Log.e("XXXX 0 " + position, diagnosticPictures.get(position).pictures.size() + "");
                if(diagnosticPictures.get(position).pictures.size()>0){
                    for (Picture s : diagnosticPictures.get(position).pictures) {
                        Log.e("XXXX N " + position, s.getImage());
                    }
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 8;
                    Bitmap bm=BitmapFactory.decodeFile(String.valueOf(new File(diagnosticPictures.get(position).pictures.get(0).getImage())),options);
                    Glide.with(context)
                            .asBitmap()
                            .load(bm)
                            .apply(new RequestOptions().centerCrop())
                            .apply(new RequestOptions().error(R.drawable.close_box))
                            .apply(new RequestOptions().placeholder(R.drawable.restart))
                            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                            .into(holder.image);

                    //holder.image.setImageBitmap(BitmapFactory.decodeFile(String.valueOf(new File(diagnosticPictures.get(position).pictures.get(0).getImage()))));
                }
                holder.userName.setText(diagnosticPictures.get(position).diagnostic.getDisease());
                holder.itemView.setTag(diagnosticPictures.get(position).diagnostic.getX());
                //holder.analyseTime.setText(diagnosticPictures.get(position).diagnostic.getAdvancedAnalysis()+" Ago");
                holder.analyseTime.setText("1 min Ago");

                //holder.slideview.addOnPageChangeListener(this);

    }

    @Override
    public int getItemCount() {
        return diagnosticPictures.size();
    }

    class StatusHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image)
        CircularImageView image;
        @BindView(R.id.user_name)
        TextView userName;
        @BindView(R.id.analyse_time)
        TextView analyseTime;

        public StatusHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("History item", "CLICKED");
                    Intent i=new Intent(context, AnalysisDetailsActivity.class);
                    i.putExtra("id", (Integer) v.getTag());
                    context.startActivity(i);
                }
            });
        }
    }
}
