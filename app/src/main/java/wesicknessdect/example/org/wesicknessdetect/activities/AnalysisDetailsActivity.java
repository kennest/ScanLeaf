package wesicknessdect.example.org.wesicknessdetect.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.viewpager.widget.ViewPager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.adapters.ImagePagerAdapter;
import wesicknessdect.example.org.wesicknessdetect.models.CulturePart;
import wesicknessdect.example.org.wesicknessdetect.models.DiagnosticPictures;
import wesicknessdect.example.org.wesicknessdetect.models.Disease;
import wesicknessdect.example.org.wesicknessdetect.models.Picture;
import wesicknessdect.example.org.wesicknessdetect.models.Struggle;
import wesicknessdect.example.org.wesicknessdetect.models.Symptom;
import wesicknessdect.example.org.wesicknessdetect.models.SymptomRect;

public class AnalysisDetailsActivity extends BaseActivity {
    int diagnostic_id;
    ImagePagerAdapter imagePagerAdapter;
    List<Map<String, Bitmap>> linkedPartImage = new ArrayList<>();
    List<Symptom> symptoms = new ArrayList<>();
    List<SymptomRect> symptomRects = new ArrayList<>();
    DiagnosticPictures diagnosticPictures = new DiagnosticPictures();
    Struggle struggle = new Struggle();
    Disease disease = new Disease();

    @BindView(R.id.pager)
    public ViewPager viewPager;

    @BindView(R.id.toolbar)
    public Toolbar toolbar;

    @BindView(R.id.btnStruggle)
    Button btnStruggle;

    String symtString = "";

    @Override
    public void onStart() {
        super.onStart();
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_details);
        ButterKnife.bind(this);

        diagnostic_id = getIntent().getIntExtra("id", 0);
        Log.e("Details diagnostic", diagnostic_id + "");

        new AsyncTask<Void, Void, Void>() {
            @SuppressLint("WrongThread")
            @Override
            protected Void doInBackground(Void... voids) {
                symptoms = DB.symptomDao().getAllSync();
                diagnosticPictures = DB.diagnosticDao().getDiagnosticWithPicturesSync(diagnostic_id);
                disease = DB.diseaseDao().getByName(diagnosticPictures.diagnostic.getDisease());
                struggle = DB.struggleDao().getByIdSync(disease.getId());
                symptomRects = DB.symptomRectDao().getAllSync();
                return null;
            }
        }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                DB.diagnosticDao().getDiagnosticWithPictures().observe(AnalysisDetailsActivity.this, new Observer<List<DiagnosticPictures>>() {
                    @SuppressLint("StaticFieldLeak")
                    @Override
                    public void onChanged(List<DiagnosticPictures> diagnosticPictures) {
                        for (DiagnosticPictures dp : diagnosticPictures) {
                            if (dp.diagnostic.getX() == diagnostic_id) {
                                toolbar.setTitle(dp.diagnostic.getDisease());

                                for (Picture p : dp.pictures) {
                                    //Log.e("Pic exist:", p.getImage());
                                    if (new File(p.getImage()).exists()) {
                                        Set<String> symptAttrs = new HashSet<>();
                                        Map<String, Bitmap> map = new HashMap<>();
                                        @SuppressLint("UseSparseArrays")
                                        Map<Integer, SymptomRect> rects = new HashMap<>();
                                        Bitmap bm = BitmapFactory.decodeFile(p.getImage());
                                        Gson gson = new Gson();
                                        Bitmap bitmap_cropped = Bitmap.createScaledBitmap(bm, 500, 500, false);
                                        Canvas canvas = new Canvas(bitmap_cropped);

                                        DB.symptomRectDao().getByPictureId(p.getX()).observe(AnalysisDetailsActivity.this, new Observer<List<SymptomRect>>() {
                                            @Override
                                            public void onChanged(List<SymptomRect> symptomRects) {
                                                Log.e("Analysis Rects -> ", symptomRects.size() + "");
                                                for (SymptomRect rect : symptomRects) {
                                                    Random rnd = new Random();
                                                    Paint paint = new Paint();
                                                    Log.e("SympRect -> Picture", rect.picture_id + "//" + p.getX() + "//" + rect.toShortString());
                                                    paint.setStyle(Paint.Style.STROKE);
                                                    paint.setStrokeWidth(4f);
                                                    int color = Color.argb(255, rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255));
                                                    paint.setColor(color);
                                                    paint.setAntiAlias(true);
                                                    canvas.drawRect(rect, paint);
                                                    rects.put(color, rect);
                                                }
                                            }
                                        });

                                        DB.culturePartsDao().getById(p.getCulture_part_id()).observe(AnalysisDetailsActivity.this, new Observer<CulturePart>() {
                                            @SuppressLint("StaticFieldLeak")
                                            @Override
                                            public void onChanged(CulturePart culturePart) {
//                                                Log.e("Part Image DB:", culturePart.getImage() + " FOUND");

                                                for (Map.Entry<Integer, SymptomRect> n : rects.entrySet()) {
                                                    for (Symptom s : symptoms) {
                                                        if (s.getId() == n.getValue().getSymptom_id()) {
                                                            String tmp = s.getName() + ":" + n.getKey();
                                                            Log.e("Symptom details", tmp);
                                                            symptAttrs.add(tmp);
                                                        }
                                                    }
                                                }
                                                symtString = gson.toJson(symptAttrs);
                                                map.put(culturePart.getImage() + "::" + symtString, bitmap_cropped);
                                                linkedPartImage.add(map);
                                                imagePagerAdapter.notifyDataSetChanged();
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    }
                });
                imagePagerAdapter = new ImagePagerAdapter(AnalysisDetailsActivity.this, linkedPartImage);
                viewPager.setAdapter(imagePagerAdapter);

            }
        });
    }

    @OnClick(R.id.btnStruggle)
    public void LancerWeb(){
        if(struggle!=null){
            String url = struggle.getLink();
            Intent i = new Intent(getApplicationContext(), DiseaseActivity.class);
            Log.e("page URL->", url);
            i.putExtra("page", url);
            startActivity(i);
        }
    }
}
