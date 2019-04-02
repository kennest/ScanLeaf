package wesicknessdect.example.org.wesicknessdetect.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.glide.slider.library.Animations.DescriptionAnimation;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.SliderTypes.TextSliderView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.models.Diagnostic;
import wesicknessdect.example.org.wesicknessdetect.models.DiagnosticPictures;
import wesicknessdect.example.org.wesicknessdetect.models.Picture;

/**
 * Created by Jordan Adopo on 03/02/2019.
 */

public class AnalysisAdapter extends RecyclerView.Adapter<AnalysisAdapter.StatusHolder> {

    Activity context;
    List<DiagnosticPictures> diagnosticPictures;

    public AnalysisAdapter(Activity context,List<DiagnosticPictures> diagnosticPictures) {
        this.context = context;
        this.diagnosticPictures=diagnosticPictures;
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
        //Glide.with(context).load(R.drawable.plante_sample).into(((StatusHolder)holder).analyseImage);
        context.runOnUiThread(new Runnable() {
            @SuppressLint("CheckResult")
            @Override
            public void run() {
                ArrayList<String> listUrl =new ArrayList<>();

                for(Picture p:diagnosticPictures.get(position).pictures){
                    Log.e("images in DB",p.getImage());
                    listUrl.add(p.getImage());
                }

                RequestOptions requestOptions = new RequestOptions();
                requestOptions.centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_camera)
                .error(R.drawable.ic_close_box_white_48dp);

                for (int i = 0; i < listUrl.size(); i++) {
                    TextSliderView sliderView = new TextSliderView(context);
                    // if you want show image only / without description text use DefaultSliderView instead

                    // initialize SliderLayout
                    sliderView
                            .image(listUrl.get(i))
                            .setRequestOption(requestOptions)
                            .setProgressBarVisible(true);

                    //add your extra information
                    sliderView.bundle(new Bundle());
                    holder.slideview.addSlider(sliderView);
                }

                // set Slider Transition Animation
                // mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Default);
                holder.slideview.setPresetTransformer(SliderLayout.Transformer.Accordion);
                holder.slideview.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                holder.slideview.setCustomAnimation(new DescriptionAnimation());
                holder.slideview.setDuration(4000);
                holder.userName.setText(diagnosticPictures.get(position).diagnostic.getDisease());
                holder.analyseTime.setText(diagnosticPictures.get(position).diagnostic.getAdvancedAnalysis()+" Ago");
            }
        });

        //holder.slideview.addOnPageChangeListener(this);
    }

    @Override
    public int getItemCount() {
        return diagnosticPictures.size();
    }

    class StatusHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.slider)
        SliderLayout slideview;
        @BindView(R.id.user_name)
        TextView userName;
        @BindView(R.id.analyse_time)
         TextView analyseTime;

        public StatusHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
