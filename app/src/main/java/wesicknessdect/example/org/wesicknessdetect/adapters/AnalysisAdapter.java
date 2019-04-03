package wesicknessdect.example.org.wesicknessdetect.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.glide.slider.library.Animations.DescriptionAnimation;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.SliderTypes.TextSliderView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import wesicknessdect.example.org.wesicknessdetect.R;
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
                R.layout.analysis_item,
                parent,
                false);
        return new StatusHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatusHolder holder, int position) {
        Log.e("XXXX 0 " + position, diagnosticPictures.get(position).pictures.size() + "");

        //Glide.with(context).load(R.drawable.plante_sample).into(((StatusHolder)holder).analyseImage);

        holder.slideview.removeAllSliders();
        for (Picture s : diagnosticPictures.get(position).pictures) {
            Log.e("XXXX N " + position, s.getImage());
            TextSliderView sliderView = new TextSliderView(context);
            // if you want show image only / without description text use DefaultSliderView instead

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.centerCrop();
            // initialize SliderLayout
            sliderView
                    .image(s.getImage())
                    .setRequestOption(requestOptions)
                    .setProgressBarVisible(true);

            //add your extra information
            //sliderView.bundle(new Bundle());
            holder.slideview.addSlider(sliderView);
        }

        // set Slider Transition Animation
        // mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Default);
        holder.slideview.setPresetTransformer(SliderLayout.Transformer.Accordion);
        holder.slideview.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        holder.slideview.setCustomAnimation(new DescriptionAnimation());
        holder.slideview.setDuration(4000);
        holder.userName.setText(diagnosticPictures.get(position).diagnostic.getDisease());
        //holder.analyseTime.setText(diagnosticPictures.get(position).diagnostic.getAdvancedAnalysis()+" Ago");
        holder.analyseTime.setText("1 min Ago");

        //holder.slideview.addOnPageChangeListener(this);
    }

    @Override
    public int getItemCount() {
        return diagnosticPictures.size();
    }

    class StatusHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.slider)
        SliderLayout slideview;
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
                }
            });
        }
    }
}
