package wesicknessdect.example.org.wesicknessdetect.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

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
import wesicknessdect.example.org.wesicknessdetect.models.DiagnosticPictures;
import wesicknessdect.example.org.wesicknessdetect.models.Picture;
import wesicknessdect.example.org.wesicknessdetect.models.SymptomRect;

public class AnalysisDetailsActivity extends BaseActivity {
    int diagnostic_id = 0;
    List<SymptomRect> rects = new ArrayList<>();
    ImagePagerAdapter imagePagerAdapter;
    List<Bitmap> bitmaps=new ArrayList<>();
    @BindView(R.id.pager)
    public ViewPager viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_details);
        ButterKnife.bind(this);

        diagnostic_id = getIntent().getIntExtra("id", 0);

        DB.diagnosticDao().getDiagnosticWithPictures().observe(this, new Observer<List<DiagnosticPictures>>() {
            @Override
            public void onChanged(List<DiagnosticPictures> diagnosticPictures) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (DiagnosticPictures dp : diagnosticPictures) {
                            if (dp.diagnostic.getId() == diagnostic_id) {
                                for (Picture p : dp.pictures) {
                                    Bitmap bm=BitmapFactory.decodeFile(p.getImage());
                                    Bitmap bitmap_cropped = Bitmap.createScaledBitmap(bm, 500, 500, false);
                                    Canvas canvas = new Canvas(bitmap_cropped);
                                    Paint paint = new Paint();
                                    paint.setStyle(Paint.Style.STROKE);
                                    paint.setStrokeWidth(4f);

                                    DB.symptomRectDao().getAll().observe(AnalysisDetailsActivity.this, new Observer<List<SymptomRect>>() {
                                        @Override
                                        public void onChanged(List<SymptomRect> symptomRects) {
                                            for (SymptomRect rect : symptomRects) {
                                                if (rect.picture_id == p.getId()) {
                                                    rects.add(rect);
                                                    Random rnd = new Random();
                                                    int color = Color.argb(255, rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255));
                                                    paint.setColor(color);
                                                    paint.setAntiAlias(true);
                                                    canvas.drawRect(rect, paint);
                                                }
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

            }
        });
        imagePagerAdapter=new ImagePagerAdapter(getApplicationContext(),bitmaps);
        viewPager.setAdapter(imagePagerAdapter);
    }
}
