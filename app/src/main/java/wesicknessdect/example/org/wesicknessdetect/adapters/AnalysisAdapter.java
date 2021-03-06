package wesicknessdect.example.org.wesicknessdetect.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.icu.util.DateInterval;
import android.media.Image;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Magnifier;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import javax.microedition.khronos.opengles.GL10;

import butterknife.BindView;
import butterknife.ButterKnife;
import wesicknessdect.example.org.wesicknessdetect.activities.AnalysisDetailsActivity;
import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.models.Diagnostic;
import wesicknessdect.example.org.wesicknessdetect.models.Picture;
import wesicknessdect.example.org.wesicknessdetect.tasks.RemoteTasks;

/**
 * Created by Jordan Adopo on 03/02/2019.
 */

public class AnalysisAdapter extends RecyclerView.Adapter<AnalysisAdapter.StatusHolder> {

    Context context;
    List<Diagnostic> diagnostics;

    Magnifier magnifier = null;
    public AnalysisAdapter(Context context, List<Diagnostic> diagnostics) {
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
                Log.d("Analysis Picture Size->" , diagnostics.get(0).getPictures().size()+"");

                for(Picture n:diagnostics.get(position).getPictures()){
                    Log.d("First Picture ->",n.getImage());
                }
                if (diagnostics.get(position).getPictures().size() > 0) {
                    Handler handler = new Handler();

                    Runnable loadImage = new Runnable() {
                        @SuppressLint("ClickableViewAccessibility")
                        @Override
                        public void run() {
                            try{
                                holder.image.setBackground(BitmapDrawable.createFromPath(String.valueOf(new File(diagnostics.get(position).getPictures().get(0).getImage()))));
//                                holder.image.setOnTouchListener(new View.OnTouchListener() {
//                                    @Override
//                                    public boolean onTouch(View v, MotionEvent event) {
//                                        switch (event.getActionMasked()) {
//                                            case MotionEvent.ACTION_DOWN:
//                                                // Fall through.
//                                            case MotionEvent.ACTION_MOVE: {
//                                                final int[] viewPosition = new int[2];
//                                                v.getLocationOnScreen(viewPosition);
//
//                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                                                    magnifier = new Magnifier(holder.image);
//                                                    magnifier.show(holder.image.getWidth() / 2, holder.image.getHeight() / 2);
//                                                    magnifier.show(event.getRawX() - viewPosition[0],
//                                                            event.getRawY() - viewPosition[1]);
//                                                }
//
//                                                break;
//                                            }
//                                            case MotionEvent.ACTION_CANCEL:
//                                                // Fall through.
//                                            case MotionEvent.ACTION_UP: {
//                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                                                    magnifier.dismiss();
//                                                }
//                                            }
//                                        }
//                                        return true;
//                                    }
//                                });
                            }catch (IndexOutOfBoundsException e){
                                Log.e("Error->",e.getMessage());
                                //Log.e("Image Error->",diagnostics.get(0).getPictures().get(0).getImage());
                            }
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
                //holder.userName.setTypeface(R.font.);
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
                if (elapsedDays <= 0) {
                    holder.analyseTime.setText("Aujourd'hui à " + time_creation);
                } else {
                    holder.analyseTime.setText("Il y a " + elapsedDays + " jours à " + time_creation);
                }
                //holder.slideview.addOnPageChangeListener(this);
                holder.image.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition_animation));
                holder.container.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_scale_animation));
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
        ImageView image;

        CardView card;


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

            card= itemView.findViewById(R.id.card);
            ButterKnife.bind(this, itemView);



            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setTag(itemView.getTag());
                    Log.d("History item", "CLICKED");
                    Intent i = new Intent(context, AnalysisDetailsActivity.class);
                    i.putExtra("uuid", v.getTag().toString());
                    context.startActivity(i);
                }
            });

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
