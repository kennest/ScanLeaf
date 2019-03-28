package wesicknessdect.example.org.wesicknessdetect.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.activities.tensorflow.Classifier;

public class PartialResultImageAdapter extends RecyclerView.Adapter<PartialResultImageAdapter.ImageHolder> {

    private Activity context;
    private Map<Integer, List<Classifier.Recognition>> recognitions_by_part;
    private Map<Integer,Map<Integer, String>> images_by_part;

    public PartialResultImageAdapter(Activity context, Map<Integer, List<Classifier.Recognition>> recognitions_by_part, Map<Integer,Map<Integer, String>> images_by_part) {
        this.context = context;
        this.recognitions_by_part = recognitions_by_part;
        this.images_by_part = images_by_part;
    }

    @NonNull
    @Override
    public ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image,
                parent, false);
        return new ImageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageHolder holder, int position) {

        //Log.e("recognitions imgs 0", "/" + images_by_part.get(position) + "//" + position);
        context.runOnUiThread(new Runnable() {
            @SuppressLint("DefaultLocale")
            @Override
            public void run() {
                @SuppressLint("UseSparseArrays")
                Map<Integer, String> recognition_legend = new HashMap<>();
                //holder.symptoms_txt=new LinearLayout(context);
                Set<String> symptoms=new HashSet<>();
                    for (Map.Entry<Integer, Map<Integer, String>> entry : images_by_part.entrySet()) {
                        Log.e("recognitions imgs", entry.getKey()+" ** " + images_by_part.get(position) + " ** "+entry.getValue()+" ** " + position);
                        if(entry.getKey().equals(position)){
                        for(Map.Entry<Integer,String> n:entry.getValue().entrySet()){
                                for (Map.Entry<Integer, List<Classifier.Recognition>> recognitionEntry : recognitions_by_part.entrySet()) {
                                    if (n.getKey().equals(recognitionEntry.getKey())) {
                                        Bitmap bitmap = BitmapFactory.decodeFile(n.getValue());
                                        Bitmap bitmap_cropped = Bitmap.createScaledBitmap(bitmap, 500, 500, false);
                                        List<Classifier.Recognition> recognitions = recognitionEntry.getValue();
                                        Canvas canvas = new Canvas(bitmap_cropped);
                                        recognitions = recognitions.subList(0, 2);
                                        for (Classifier.Recognition r : recognitions) {

                                            symptoms.add(r.getTitle()+"---"+(Math.round(r.getConfidence()*100))+"%");
                                            Paint paint = new Paint();
                                            paint.setStyle(Paint.Style.STROKE);
                                            paint.setStrokeWidth(4f);
                                            Random rnd = new Random();
                                            int color = Color.argb(255, rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255));

                                            //For each recognitions add a layout with the corresponding color of the canvas
                                            LinearLayout line=new LinearLayout(context);
                                            line.setOrientation(LinearLayout.HORIZONTAL);
                                            TextView txt=new TextView(context);
                                            txt.setText(String.format("%s  ---  %d%%", r.getTitle(), Math.round(r.getConfidence() * 100)));
                                            txt.setTextColor(color);
                                            txt.setTypeface(txt.getTypeface(), Typeface.BOLD);
                                            txt.setTextSize(20);
                                            line.addView(txt);
                                            holder.symptoms_txt.addView(line);

                                            recognition_legend.put(color, r.getTitle());
                                            paint.setColor(color);
                                            paint.setAntiAlias(true);
                                            canvas.drawRect(r.getLocation(), paint);
                                        }
                                        holder.image.setImageBitmap(bitmap_cropped);
                                    }
                                }
                            }
                        }
                    }

                }
        });

    }

    @Override
    public int getItemCount() {
        Log.e("images size",images_by_part.size()+"");
        return images_by_part.size();
    }


    public class ImageHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv)
        ImageView image;
        @BindView(R.id.symptoms_txt)
        LinearLayout symptoms_txt;

        public ImageHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
