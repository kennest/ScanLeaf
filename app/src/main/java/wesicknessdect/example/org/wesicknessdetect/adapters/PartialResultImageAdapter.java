package wesicknessdect.example.org.wesicknessdetect.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.activities.tensorflow.Classifier;
import wesicknessdect.example.org.wesicknessdetect.database.AppDatabase;
import wesicknessdect.example.org.wesicknessdetect.models.CulturePart;
import wesicknessdect.example.org.wesicknessdetect.models.Picture;
import wesicknessdect.example.org.wesicknessdetect.models.Question;
import wesicknessdect.example.org.wesicknessdetect.models.Symptom;
import wesicknessdect.example.org.wesicknessdetect.models.SymptomRect;
import wesicknessdect.example.org.wesicknessdetect.utils.AppController;

public class PartialResultImageAdapter extends RecyclerView.Adapter<PartialResultImageAdapter.ImageHolder> {

    private Activity context;
    private Map<Integer, List<Classifier.Recognition>> recognitions_by_part;
    private Map<Integer, Map<Integer, String>> images_by_part;
    private CulturePart culturePart = new CulturePart();
    private List<Symptom> symptomsList = new ArrayList<>();
    private Question question =new Question();

    List<Picture> pictures = new ArrayList<>();

    public PartialResultImageAdapter(Activity context, Map<Integer, List<Classifier.Recognition>> recognitions_by_part, Map<Integer, Map<Integer, String>> images_by_part) {
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


    @SuppressLint("StaticFieldLeak")
    @Override
    public void onBindViewHolder(@NonNull ImageHolder holder, int position) {

        for (Map.Entry<Integer, Map<Integer, String>> entry : images_by_part.entrySet()) {
            if (entry.getKey().equals(position)) {
                for (Map.Entry<Integer, String> n : entry.getValue().entrySet()) {
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            question=AppDatabase.getInstance(context).questionDao().getByPartSync(n.getKey());
                            symptomsList = AppDatabase.getInstance(context).symptomDao().getByQuestion(question.getId());
                            return null;
                        }
                    }.execute();
                }
            }
        }

        //Log.e("recognitions imgs 0", "/" + images_by_part.get(position) + "//" + position);
        context.runOnUiThread(new Runnable() {
            @SuppressLint({"DefaultLocale", "StaticFieldLeak"})
            @Override
            public void run() {
                @SuppressLint("UseSparseArrays")
                Map<Integer, String> recognition_legend = new HashMap<>();
                //holder.symptoms_txt=new LinearLayout(context);
                Set<String> symptoms = new HashSet<>();
                List<SymptomRect> symptomsRects = new ArrayList<>();
                Picture p = new Picture();
                for (Map.Entry<Integer, Map<Integer, String>> entry : images_by_part.entrySet()) {
                    //Log.e("recognitions imgs", entry.getKey() + " ** " + images_by_part.get(position) + " ** " + entry.getValue() + " ** " + position);

                    if (entry.getKey().equals(position)) {
                    for (Map.Entry<Integer, String> n : entry.getValue().entrySet()) {
                        p.setCulture_part_id(n.getKey());
                        p.setImage(n.getValue());
                        p.setSended(0);
                        String uuid= UUID.randomUUID().toString();
                        p.setUuid(uuid);
                        //Retrieve the culture part image from DB
                        // Log.e("part_id", n.getKey() + " ** " + position);

                        AppDatabase.getInstance(context).culturePartsDao().getById(n.getKey()).observeForever(new Observer<CulturePart>() {
                            @Override
                            public void onChanged(CulturePart c) {
                                culturePart = c;
                                holder.part_image.setImageBitmap(BitmapFactory.decodeFile(culturePart.getImage()));
                                holder.part_name.setText(culturePart.getNom());
                            }
                        });

                        if (culturePart != null) {
                            holder.part_image.setImageBitmap(BitmapFactory.decodeFile(culturePart.getImage()));
                            holder.part_name.setText(culturePart.getNom());
                        }

                        for (Map.Entry<Integer, List<Classifier.Recognition>> recognitionEntry : recognitions_by_part.entrySet()) {
                            if (n.getKey().equals(recognitionEntry.getKey())) {
                                Bitmap bitmap = BitmapFactory.decodeFile(n.getValue());
                                Bitmap bitmap_cropped = Bitmap.createScaledBitmap(bitmap, 500, 500, false);
                                //List<Classifier.Recognition> recognitions = recognitionEntry.getValue();
                                Canvas canvas = new Canvas(bitmap_cropped);

                                //recognitions = recognitions.subList(0, 4);
                                for (Classifier.Recognition r : recognitionEntry.getValue().subList(0, 3)) {
                                    symptoms.add(r.getTitle() + "---" + (Math.round(r.getConfidence() * 100)) + "%");

                                    for (Symptom s : symptomsList) {
                                        if (s.getName().equals(r.getTitle())) {
                                            SymptomRect sr = new SymptomRect();
                                            sr.set(r.getLocation());
                                            sr.setSymptom_id(s.getId());
                                            sr.setLabel(r.getTitle());
                                            sr.setSended(0);
                                            String ruuid= UUID.randomUUID().toString();
                                            sr.setUuid(ruuid);
                                            symptomsRects.add(sr);
                                        }
                                    }

                                    Paint paint = new Paint();
                                    paint.setStyle(Paint.Style.STROKE);
                                    paint.setStrokeWidth(4f);
                                    Random rnd = new Random();
                                    int color = Color.argb(255, rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255));

                                    //For each recognitions add a layout with the corresponding color of the canvas
                                    LinearLayout line = new LinearLayout(context);
                                    line.setOrientation(LinearLayout.HORIZONTAL);
                                    TextView txt = new TextView(context);
                                    txt.setPadding(5, 5, 5, 0);
                                    txt.setText(String.format("%s  ->  %d%%", r.getTitle(), Math.round(r.getConfidence() * 100)));
                                    txt.setTextColor(color);
                                    txt.setTypeface(txt.getTypeface(), Typeface.NORMAL);
                                    txt.setTextSize(15);
                                    line.addView(txt);
                                    holder.symptoms_txt.addView(line);
                                    recognition_legend.put(color, r.getTitle());
                                    paint.setColor(color);
                                    paint.setAntiAlias(true);
                                    canvas.drawRect(r.getLocation(), paint);
                                }
                                p.setSymptomRects(symptomsRects);
                                Log.e("Rect Partial 0 ->", symptomsRects.size() + "");
                                //AppController.getInstance().setSymptomsRects(symptomsRects);
                                holder.image.setImageBitmap(bitmap_cropped);
                            }
                        }
                    }

                    }
                }
                pictures.add(p);
                AppController.getInstance().setPictures(pictures);
            }
        });

    }


    @Override
    public int getItemCount() {
        Log.e("images size", images_by_part.size() + "");
        return images_by_part.size();
    }


    public class ImageHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv)
        ImageView image;
        @BindView(R.id.symptoms_txt)
        LinearLayout symptoms_txt;
        @BindView(R.id.part_image)
        CircularImageView part_image;
        @BindView(R.id.part_name)
        TextView part_name;

        public ImageHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
