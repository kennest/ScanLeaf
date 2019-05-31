package wesicknessdect.example.org.wesicknessdetect.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.adapters.ImagePagerAdapter;
import wesicknessdect.example.org.wesicknessdetect.models.CulturePart;
import wesicknessdect.example.org.wesicknessdetect.models.Diagnostic;
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
    List<Picture> pictures = new ArrayList<>();
    Diagnostic diagnostic = new Diagnostic();
    Struggle struggle = new Struggle();
    Disease disease = new Disease();
    CulturePart cp = new CulturePart();

    @BindView(R.id.pager)
    public ViewPager viewPager;

    @BindView(R.id.toolbar)
    public Toolbar toolbar;

    @BindView(R.id.btnStruggle)
    Button btnStruggle;

    @BindView(R.id.time)
    TextView time;

    String symtString = "";

    @Override
    public void onStart() {
        super.onStart();
    }


    @SuppressLint({"StaticFieldLeak", "CheckResult"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_details);
        ButterKnife.bind(this);

        diagnostic_id = getIntent().getIntExtra("id", 0);
        Log.e("Rx Details diagnostic", diagnostic_id + "");
        Completable.fromAction(() -> {
            symptoms = DB.symptomDao().getAllSync();
            diagnostic = DB.diagnosticDao().getDiagnosticById(diagnostic_id);
            pictures = DB.pictureDao().getByDiagnosticIdSync(diagnostic.getRemote_id());
            disease = DB.diseaseDao().getByName(diagnostic.getDisease());
            struggle = DB.struggleDao().getByIdSync(disease.getId());
            symptomRects = DB.symptomRectDao().getAllSync();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (diagnostic.getAdvancedAnalysis().equals("")) {
                                        toolbar.setTitle(diagnostic.getDisease());
                                    } else {
                                        if (!diagnostic.getAdvancedAnalysis().equals(diagnostic.getDisease())) {
                                            toolbar.setTitle(diagnostic.getDisease() + " et " + diagnostic.getAdvancedAnalysis());
                                        } else {
                                            toolbar.setTitle(diagnostic.getDisease());
                                        }
                                    }

                                    Date now = new Date();
                                    @SuppressLint("SimpleDateFormat")
                                    String now_str = new SimpleDateFormat("yyyy-MM-dd").format(now);
                                    List<String> creation_str = new ArrayList<>();
                                    Date date_creation = null;
                                    Date str_time = null;
                                    long elapsedDays = 0;
                                    long ago = 0;
                                    String time_creation = "";

                                    if (diagnostic.getCreation_date().contains("T")) {
                                        creation_str = Arrays.asList(diagnostic.getCreation_date().split("T"));
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
                                        creation_str = Arrays.asList(diagnostic.getCreation_date().split(" "));
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

                                    if (elapsedDays < 0) {
                                        time.setText("Aujourd'hui à " + time_creation);
                                    } else {
                                        time.setText("Il y a " + elapsedDays + " jours à " + time_creation);
                                    }

                                    for (Picture p : pictures) {
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

                                            for (SymptomRect rect : symptomRects) {
                                                if (rect.picture_id == p.getX()) {
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

                                            for (Map.Entry<Integer, SymptomRect> n : rects.entrySet()) {
                                                for (Symptom s : symptoms) {
                                                    if (s.getId() == n.getValue().getSymptom_id()) {
                                                        String tmp = s.getName() + ":" + n.getKey();
                                                        Log.e("Symptom details", tmp);
                                                        symptAttrs.add(tmp);
                                                    }
                                                }
                                            }

                                            cp = DB.culturePartsDao().getByIdSync(p.getCulture_part_id());
                                            symtString = gson.toJson(symptAttrs);
                                            map.put(cp.getImage() + "::" + symtString + "::" + cp.getNom(), bitmap_cropped);
                                            linkedPartImage.add(map);
                                        }
                                    }
                                    imagePagerAdapter = new ImagePagerAdapter(AnalysisDetailsActivity.this, linkedPartImage);
                                    viewPager.setAdapter(imagePagerAdapter);
                                }
                            });

                        },
                        throwable -> throwable.printStackTrace());

//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                symptoms = DB.symptomDao().getAllSync();
//                diagnosticPictures = DB.diagnosticDao().getDiagnosticWithPicturesSync(diagnostic_id);
//                disease = DB.diseaseDao().getByName(diagnosticPictures.diagnostic.getDisease());
//                struggle = DB.struggleDao().getByIdSync(disease.getId());
//                symptomRects = DB.symptomRectDao().getAllSync();
//
//                if(diagnosticPictures.diagnostic.getAdvancedAnalysis().equals("")) {
//                    toolbar.setTitle(diagnosticPictures.diagnostic.getDisease());
//                }else{
//                    if(!diagnosticPictures.diagnostic.getAdvancedAnalysis().equals(diagnosticPictures.diagnostic.getDisease())) {
//                        toolbar.setTitle(diagnosticPictures.diagnostic.getDisease() + " et " + diagnosticPictures.diagnostic.getAdvancedAnalysis());
//                    }else{
//                        toolbar.setTitle(diagnosticPictures.diagnostic.getDisease());
//                    }
//                }
//
//                Date now = new Date();
//                @SuppressLint("SimpleDateFormat")
//                String now_str = new SimpleDateFormat("yyyy-MM-dd").format(now);
//                List<String> creation_str = new ArrayList<>();
//                Date date_creation = null;
//                Date str_time = null;
//                long elapsedDays = 0;
//                long ago = 0;
//                String time_creation="";
//
//                if (diagnosticPictures.diagnostic.getCreation_date().contains("T")) {
//                    creation_str = Arrays.asList(diagnosticPictures.diagnostic.getCreation_date().split("T"));
//                    time_creation = creation_str.get(1).substring(0, 5);
//                    try {
//                        date_creation = new SimpleDateFormat("yyyy-MM-dd").parse(creation_str.get(0));
//                        str_time = new SimpleDateFormat("HH:mm").parse(time_creation);
//                        Log.d("Date Elapsed->", creation_str.get(0) + "//" + now_str);
//                        ago = now.getTime() - date_creation.getTime();
//                        //ago = TimeUnit.MILLISECONDS.toMillis(ago);
//                        long secondsInMilli = 1000;
//                        long minutesInMilli = secondsInMilli * 60;
//                        long hoursInMilli = minutesInMilli * 60;
//                        long daysInMilli = hoursInMilli * 24;
//                        elapsedDays = ago / daysInMilli;
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    creation_str = Arrays.asList(diagnosticPictures.diagnostic.getCreation_date().split(" "));
//                    time_creation = creation_str.get(1).substring(0, 5);
//                    try {
//                        date_creation = new SimpleDateFormat("yyyy-MM-dd").parse(creation_str.get(0));
//                        str_time = new SimpleDateFormat("HH:mm").parse(time_creation);
//                        Log.d("Date Elapsed->", creation_str.get(0) + "//" + now_str);
//                        ago = now.getTime() - date_creation.getTime();
//                        //ago = TimeUnit.MILLISECONDS.toMillis(ago);
//                        long secondsInMilli = 1000;
//                        long minutesInMilli = secondsInMilli * 60;
//                        long hoursInMilli = minutesInMilli * 60;
//                        long daysInMilli = hoursInMilli * 24;
//                        elapsedDays = ago / daysInMilli;
//
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                if(elapsedDays<0){
//                   time.setText("Aujourd'hui à " + time_creation);
//                }else{
//                   time.setText("Il y a " + elapsedDays + " jours à " + time_creation);
//                }
//
//
//                for (Picture p : diagnosticPictures.pictures) {
//                    //Log.e("Pic exist:", p.getImage());
//                    if (new File(p.getImage()).exists()) {
//                        Set<String> symptAttrs = new HashSet<>();
//                        Map<String, Bitmap> map = new HashMap<>();
//                        @SuppressLint("UseSparseArrays")
//                        Map<Integer, SymptomRect> rects = new HashMap<>();
//                        Bitmap bm = BitmapFactory.decodeFile(p.getImage());
//                        Gson gson = new Gson();
//                        Bitmap bitmap_cropped = Bitmap.createScaledBitmap(bm, 500, 500, false);
//                        Canvas canvas = new Canvas(bitmap_cropped);
//
//                        for (SymptomRect rect : symptomRects) {
//                            if (rect.picture_id == p.getX()) {
//                                Random rnd = new Random();
//                                Paint paint = new Paint();
//                                Log.e("SympRect -> Picture", rect.picture_id + "//" + p.getX() + "//" + rect.toShortString());
//                                paint.setStyle(Paint.Style.STROKE);
//                                paint.setStrokeWidth(4f);
//                                int color = Color.argb(255, rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255));
//                                paint.setColor(color);
//                                paint.setAntiAlias(true);
//                                canvas.drawRect(rect, paint);
//                                rects.put(color, rect);
//                            }
//                        }
//
//                        for (Map.Entry<Integer, SymptomRect> n : rects.entrySet()) {
//                            for (Symptom s : symptoms) {
//                                if (s.getId() == n.getValue().getSymptom_id()) {
//                                    String tmp = s.getName() + ":" + n.getKey();
//                                    Log.e("Symptom details", tmp);
//                                    symptAttrs.add(tmp);
//                                }
//                            }
//                        }
//
//                        cp = DB.culturePartsDao().getByIdSync(p.getCulture_part_id());
//                        symtString = gson.toJson(symptAttrs);
//                        map.put(cp.getImage() + "::" + symtString + "::" + cp.getNom(), bitmap_cropped);
//                        linkedPartImage.add(map);
//                    }
//                }
//                imagePagerAdapter = new ImagePagerAdapter(AnalysisDetailsActivity.this, linkedPartImage);
//                viewPager.setAdapter(imagePagerAdapter);
//            }
//        });
    }

    @OnClick(R.id.btnStruggle)
    public void LancerWeb() {
        if (struggle != null) {
            String url = struggle.getLink();
            Intent i = new Intent(getApplicationContext(), DiseaseActivity.class);
            Log.e("page URL->", url);
            i.putExtra("page", url);
            startActivity(i);
        }
    }
}
