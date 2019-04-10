package wesicknessdect.example.org.wesicknessdetect.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.adapters.ImagePagerAdapter;
import wesicknessdect.example.org.wesicknessdetect.models.CulturePart;
import wesicknessdect.example.org.wesicknessdetect.models.DiagnosticPictures;
import wesicknessdect.example.org.wesicknessdetect.models.Picture;
import wesicknessdect.example.org.wesicknessdetect.models.SymptomRect;

public class AnalysisDetailsActivity extends BaseActivity {
    int diagnostic_id;
    ImagePagerAdapter imagePagerAdapter;
    List<Bitmap> bitmaps = new ArrayList<>();

    @BindView(R.id.pager)
    public ViewPager viewPager;


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_details);
        ButterKnife.bind(this);

        diagnostic_id = getIntent().getIntExtra("id", 0);

        DB.diagnosticDao().getDiagnosticWithPictures().observe(AnalysisDetailsActivity.this, new Observer<List<DiagnosticPictures>>() {
            @Override
            public void onChanged(List<DiagnosticPictures> diagnosticPictures) {
                for (DiagnosticPictures dp : diagnosticPictures) {
                    if (dp.diagnostic.getX() == diagnostic_id) {
                        for (Picture p : dp.pictures) {
                            Bitmap bm = BitmapFactory.decodeFile(p.getImage());
                            Bitmap bitmap_cropped = Bitmap.createScaledBitmap(bm, 500, 500, false);
                            Canvas canvas = new Canvas(bitmap_cropped);

                            DB.culturePartsDao().getById(p.getCulture_part_id()).observe(AnalysisDetailsActivity.this, new Observer<CulturePart>() {
                                @Override
                                public void onChanged(CulturePart culturePart) {
                                    if(new File(culturePart.getImage()).exists()) {
                                        Bitmap part_image = BitmapFactory.decodeFile(culturePart.getImage());
                                    }
                                }
                            });

                            DB.symptomRectDao().getByPictureId(p.getX()).observe(AnalysisDetailsActivity.this, new Observer<List<SymptomRect>>() {
                                @Override
                                public void onChanged(List<SymptomRect> symptomRects) {
                                    for (SymptomRect rect : symptomRects) {
                                        Random rnd = new Random();
                                        Paint paint = new Paint();
                                        Log.e("SympRect -> Picture", rect.picture_id + "//" + p.getX());
                                        paint.setStyle(Paint.Style.STROKE);
                                        paint.setStrokeWidth(4f);
                                        int color = Color.argb(255, rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255));
                                        paint.setColor(color);
                                        paint.setAntiAlias(true);
                                        canvas.drawRect(rect, paint);
                                    }
                                }
                            });
                            bitmaps.add(bitmap_cropped);
                            imagePagerAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
        imagePagerAdapter = new ImagePagerAdapter(getApplicationContext(), bitmaps);
        viewPager.setAdapter(imagePagerAdapter);

    }
}
