package wesicknessdect.example.org.wesicknessdetect.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.DateInterval;
import android.media.Image;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.glide.slider.library.Animations.DescriptionAnimation;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.SliderTypes.TextSliderView;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.activities.AnalysisDetailsActivity;
import wesicknessdect.example.org.wesicknessdetect.models.Diagnostic;
import wesicknessdect.example.org.wesicknessdetect.models.DiagnosticPictures;
import wesicknessdect.example.org.wesicknessdetect.models.Picture;
import wesicknessdect.example.org.wesicknessdetect.tasks.RemoteTasks;

/**
 * Created by Jordan Adopo on 03/02/2019.
 */

public class AnalysisAdapter extends RecyclerView.Adapter<AnalysisAdapter.StatusHolder> {

    Activity context;
    List<Diagnostic> diagnostics;

    public AnalysisAdapter(Activity context, List<Diagnostic> diagnostics) {
        this.context = context;
        this.diagnostics = diagnostics;
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

    @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
    @Override
    public void onBindViewHolder(@NonNull StatusHolder holder, int position) {
        if (diagnostics.get(position) != null) {
            if (diagnostics.get(position).getPictures() != null) {
                //Log.e("XXXX 0 " + position, diagnostics.get(position).getPictures().size() + "");
                if (diagnostics.get(position).getPictures().size() > 0) {
                    Handler handler = new Handler();

                    Runnable loadImage = new Runnable() {
                        @Override
                        public void run() {
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inSampleSize = 8;
                            Bitmap bm = BitmapFactory.decodeFile(String.valueOf(new File(diagnostics.get(position).getPictures().get(0).getImage())), options);
                            Glide.with(context)
                                    .asBitmap()
                                    .load(bm)
                                    .apply(new RequestOptions().override(100, 100))
                                    .apply(new RequestOptions().centerCrop())
                                    .override(100,100)
                                    .apply(new RequestOptions().error(R.drawable.information))
                                    .apply(new RequestOptions().placeholder(R.drawable.restart))
                                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                                    .into(holder.image);
                        }
                    };

                    File image = new File(diagnostics.get(position).getPictures().get(0).getImage());

                    if (!image.exists()) {
                        handler.postDelayed(loadImage, 500);
                    } else {
                        handler.removeCallbacks(loadImage);
                    }
                    handler.post(loadImage);

                    holder.counter.setText(Integer.toString(diagnostics.get(position).getPictures().size()));

                    //holder.image.setImageBitmap(BitmapFactory.decodeFile(String.valueOf(new File(diagnosticPictures.get(position).pictures.get(0).getImage()))));
                }
                holder.userName.setText(diagnostics.get(position).getDisease());

                //Calcul du temps passe  entre la creation et la date actuelle
                Date now = new Date();
                @SuppressLint("SimpleDateFormat")
                String now_str = new SimpleDateFormat("yyyy-MM-dd").format(now);
                List<String> creation_str = new ArrayList<>();
                Date date_creation = null;
                Date str_time = null;
                long elapsedDays = 0;
                long ago = 0;
                String time_creation = "";

                if (diagnostics.get(position).getCreation_date().contains("T")) {
                    creation_str = Arrays.asList(diagnostics.get(position).getCreation_date().split("T"));
                    time_creation = creation_str.get(1).substring(0, 5);
                    try {
                        date_creation = new SimpleDateFormat("yyyy-MM-dd").parse(creation_str.get(0));
                        str_time = new SimpleDateFormat("HH:mm").parse(time_creation);
                        Log.d("Date Elapsed->", creation_str.get(0) + "//" + now_str);
                        ago = now.getTime() - date_creation.getTime();
                        //ago = TimeUnit.MILLISECONDS.toMillis(ago);
                        long secondsInMilli = 1000;
                        long minutesInMilli = secondsInMilli * 60;
                        long hoursInMilli = minutesInMilli * 60;
                        long daysInMilli = hoursInMilli * 24;
                        elapsedDays = ago / daysInMilli;
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    creation_str = Arrays.asList(diagnostics.get(position).getCreation_date().split(" "));
                    time_creation = creation_str.get(1).substring(0, 5);
                    try {
                        date_creation = new SimpleDateFormat("yyyy-MM-dd").parse(creation_str.get(0));
                        str_time = new SimpleDateFormat("HH:mm").parse(time_creation);
                        Log.d("Date Elapsed->", creation_str.get(0) + "//" + now_str);
                        ago = now.getTime() - date_creation.getTime();
                        //ago = TimeUnit.MILLISECONDS.toMillis(ago);
                        long secondsInMilli = 1000;
                        long minutesInMilli = secondsInMilli * 60;
                        long hoursInMilli = minutesInMilli * 60;
                        long daysInMilli = hoursInMilli * 24;
                        elapsedDays = ago / daysInMilli;

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                Log.d("Date Creation->", creation_str.toString());

                //holder.now.setText(time_creation);
                holder.itemView.setTag(diagnostics.get(position).getUuid());
                //holder.analyseTime.setText(diagnosticPictures.get(position).diagnostic.getAdvancedAnalysis()+" Ago");
                if (elapsedDays < 0) {
                    holder.analyseTime.setText("Aujourd'hui à " + time_creation);
                } else {
                    holder.analyseTime.setText("Il y a " + elapsedDays + " jours à " + time_creation);
                }
                //holder.slideview.addOnPageChangeListener(this);
                //holder.image.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition_animation));
                //holder.container.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_scale_animation));
            }
        }

    }


    public void loadNextDataFromApi(int lastID) throws IOException {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
        RemoteTasks.getInstance(context).getDiagnostics(lastID);
    }

    @Override
    public int getItemCount() {
        return diagnostics.size();
    }

    class StatusHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image)
        CircularImageView image;

        @BindView(R.id.user_name)
        TextView userName;

        @BindView(R.id.container)
        RelativeLayout container;

        @BindView(R.id.analyse_time)
        TextView analyseTime;

        @BindView(R.id.counter)
        TextView counter;

        public StatusHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("History item", "CLICKED");
                    Intent i = new Intent(context, AnalysisDetailsActivity.class);
                    i.putExtra("uuid", v.getTag().toString());
                    context.startActivity(i);
                }
            });
        }
    }
}
