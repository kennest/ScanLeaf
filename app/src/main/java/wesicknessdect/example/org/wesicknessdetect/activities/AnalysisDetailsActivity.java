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
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import androidx.lifecycle.Observer;
import androidx.viewpager.widget.ViewPager;

import org.reactivestreams.Subscription;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Completable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;
import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.adapters.ImagePagerAdapter;
import wesicknessdect.example.org.wesicknessdetect.models.CulturePart;
import wesicknessdect.example.org.wesicknessdetect.models.Diagnostic;
import wesicknessdect.example.org.wesicknessdetect.models.Disease;
import wesicknessdect.example.org.wesicknessdetect.models.Picture;
import wesicknessdect.example.org.wesicknessdetect.models.Struggle;
import wesicknessdect.example.org.wesicknessdetect.models.Symptom;
import wesicknessdect.example.org.wesicknessdetect.models.SymptomRect;
import wesicknessdect.example.org.wesicknessdetect.utils.Constants;

public class AnalysisDetailsActivity extends BaseActivity {
    String diagnostic_uuid;
    ImagePagerAdapter imagePagerAdapter;
    List<Map<String, Bitmap>> linkedPartImage = new ArrayList<>();
    List<Picture> pictures = new ArrayList<>();
    List<SymptomRect> symptomRects = new ArrayList<>();
    List<Symptom> symptoms = new ArrayList<>();
    Diagnostic diagnostic = new Diagnostic();
    Struggle struggle = new Struggle();
    Disease disease = new Disease();
    CulturePart cp = new CulturePart();
    int i = 0;

    @BindView(R.id.pager)
    public ViewPager viewPager;

    @BindView(R.id.toolbar)
    public Toolbar toolbar;

    @BindView(R.id.btnStruggle)
    FloatingActionButton btnStruggle;

    @BindView(R.id.time)
    TextView time;

    String symtString = "";
    Handler handler = new Handler();

    @Override
    public void onStart() {
        super.onStart();

    }


    @SuppressLint({"CheckResult"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_details);
        ButterKnife.bind(this);
//        diagnostic_uuid = getIntent().getStringExtra("uuid");
        //Log.e("Rx Details diagnostic", diagnostic_uuid + "");

        AsyncTask.SERIAL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                diagnostic_uuid = getIntent().getStringExtra("uuid");
                diagnostic = DB.diagnosticDao().getDiagnosticByUuid(diagnostic_uuid);
                pictures = DB.pictureDao().getByDiagnosticUUIdSync(diagnostic_uuid);
                disease = DB.diseaseDao().getByName(diagnostic.getDisease());
                struggle = DB.struggleDao().getByIdSync(disease.getId());
                symptoms = DB.symptomDao().getAllSync();
                symptomRects = DB.symptomRectDao().getAllSync();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (diagnostic.getAdvancedAnalysis().equals("") || diagnostic.getAdvancedAnalysis()==null) {
                            toolbar.setTitle(diagnostic.getDisease());
                            toolbar.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                        } else {
                            if (!diagnostic.getAdvancedAnalysis().equals(diagnostic.getDisease())) {
                                toolbar.setTitle(diagnostic.getDisease() + " et " + diagnostic.getAdvancedAnalysis());
                                toolbar.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                            }else{
                                toolbar.setTitle(diagnostic.getDisease());
                                toolbar.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
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

                        if (elapsedDays <= 0) {
                            time.setText("Aujourd'hui à " + time_creation);
                        } else {
                            time.setText("Il y a " + elapsedDays + " jours à " + time_creation);
                        }


                        for (Picture p : pictures) {
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
                                    if (p.getUuid().equals(rect.getPicture_uuid())) {
                                        Paint paint = new Paint();
                                        Log.d("SympRect -> Picture", rect.picture_id + "//" + p.getX() + "//" + rect.toShortString());
                                        paint.setStyle(Paint.Style.STROKE);
                                        paint.setStrokeWidth(4f);
                                        try {
                                            int color = Constants.RECTCOLORS.get(i);
                                            i += 1;
                                            paint.setColor(color);
                                            paint.setAntiAlias(true);
                                            canvas.drawRect(rect, paint);
                                            rects.put(color, rect);
                                            Log.d("Draw Rect","Passed");
                                        } catch (IndexOutOfBoundsException e) {
                                            i=0;
                                            Log.e("Colors size->", e.getMessage());
                                        }
                                    }
                                }

                                for (Map.Entry<Integer, SymptomRect> n : rects.entrySet()) {
                                    for (Symptom s : symptoms) {
                                        if (s.getId() == n.getValue().getSymptom_id()) {
                                            String tmp = s.getName() + ":" + n.getKey();
                                            Log.d("Symptom details", tmp);
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
            }
        });


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
