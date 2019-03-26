package wesicknessdect.example.org.wesicknessdetect.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toolbar;

import com.fxn.pix.Pix;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.activities.tensorflow.Classifier;
import wesicknessdect.example.org.wesicknessdetect.adapters.CulturePartAdapter;
import wesicknessdect.example.org.wesicknessdetect.database.AppDatabase;
import wesicknessdect.example.org.wesicknessdetect.events.ImageRecognitionProcessEvent;
import wesicknessdect.example.org.wesicknessdetect.events.ModelDownloadEvent;
import wesicknessdect.example.org.wesicknessdetect.futuretasks.SystemTasks;
import wesicknessdect.example.org.wesicknessdetect.models.CulturePart;
import wesicknessdect.example.org.wesicknessdetect.models.Model;

public class ChooseCulturePartActivity extends BaseActivity {
    List<CulturePart> culturePartList = new ArrayList<>();
    List<String> pictures_path = new ArrayList<>();
    HashMap<Integer, String> culturePart_image = new HashMap<>();
    private static AppDatabase DB;
    List<Model> models_List = new ArrayList<>();
    boolean modelFinded = false;
    Model modele = new Model();


    @BindView(R.id.culture_parts_lv)
    RecyclerView parts_lv;

    @BindView(R.id.btn_analysis)
    Button analysisBtn;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    CulturePartAdapter culturePartAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_picture);

        DB = AppDatabase.getInstance(this);

        ButterKnife.bind(this);
        toolbar.setTitle("Choose Culture Part");

        DB.culturePartsDao().getAll().observe(this, new Observer<List<CulturePart>>() {
            @Override
            public void onChanged(List<CulturePart> cultureParts) {
                culturePartList = cultureParts;
                culturePartAdapter = new CulturePartAdapter(ChooseCulturePartActivity.this, culturePartList, new HashMap<>());
                parts_lv.setLayoutManager(new GridLayoutManager(ChooseCulturePartActivity.this, 2));
                parts_lv.setAdapter(culturePartAdapter);
            }
        });

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DB.modelDao().getAll().observe(ChooseCulturePartActivity.this, new Observer<List<Model>>() {
                    @Override
                    public void onChanged(List<Model> models) {
                        Log.e("Number of models", models.size() + "");
//                        models_List = models;
                        for (Model m : models) {
                            for (CulturePart c : culturePartList) {
                                if (m.getPart_id() == c.getId()) {
                                    File modelfile = new File(m.getPb());
                                    if (modelfile.exists()) {
                                        c.setModel_downloaded(true);
                                        c.setDownloaded(1000);
                                        c.setFilesize(1000);
                                    }
                                }
                            }
                        }
                        culturePartAdapter = new CulturePartAdapter(ChooseCulturePartActivity.this, culturePartList, new HashMap<>());
                        parts_lv.setLayoutManager(new GridLayoutManager(ChooseCulturePartActivity.this, 2));
                        parts_lv.setAdapter(culturePartAdapter);
                        culturePartAdapter.notifyDataSetChanged();
                    }
                });

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("Req code", requestCode + "");
        for (CulturePart c : culturePartList) {
            if (resultCode == Activity.RESULT_OK && requestCode == c.getId()) {
                assert data != null;
                ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
                culturePart_image.put((int) c.getId(), returnValue.get(0));
                culturePartAdapter = new CulturePartAdapter(ChooseCulturePartActivity.this, culturePartList, culturePart_image);
                parts_lv.setLayoutManager(new GridLayoutManager(ChooseCulturePartActivity.this, 2));
                parts_lv.setAdapter(culturePartAdapter);
                culturePartAdapter.notifyDataSetChanged();
            }
        }
    }


    //Launch Analysis for each parts
    @SuppressLint("StaticFieldLeak")
    @OnClick(R.id.btn_analysis)
    public void doAnalysis() {

        for (Map.Entry<Integer, String> entry : culturePart_image.entrySet()) {
            DB.modelDao().getByPart((long) entry.getKey()).observe(this, new Observer<Model>() {
                @Override
                public void onChanged(Model model) {
                    // modele = model;
                    Log.i("model in DB::", model.getLabel() + "//" + model.getPb() + "//" + entry.getKey());
                    File modelfile=new File(model.getPb());
                    File labelpath=new File(model.getLabel());
                    if(modelfile.exists() && labelpath.exists()){
                        Bitmap bitmap = BitmapFactory.decodeFile(entry.getValue());
                        Bitmap bitmap_cropped = Bitmap.createScaledBitmap(bitmap, 500, 500, false);
                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... voids) {
                                List<Classifier.Recognition> recognitions = new ArrayList<>();
                                recognitions = SystemTasks.getInstance().recognizedSymptoms(getAssets(), bitmap_cropped, model.getPb(), model.getLabel(), model.getPart_id());
                                Log.d(entry.getKey() + ":Recognitions -> ", recognitions.toString());
                                return null;
                            }
                        }.execute();
                    }else{
                        Log.e("Recognize Error","Cannot find models and labels");
                    }
                }
            });


        }

    }

    //Get Culture part downloadd infos for supply progressbar
    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void getModelDownloadBytes(ModelDownloadEvent event) {
        Log.e("Download started", event.part_id + "/" + event.downloaded + "/" + event.filesize);
        for (CulturePart c : culturePartList) {
            if ((c.getId() == event.part_id)) {
                if (event.downloaded == event.filesize) {
                    c.setModel_downloaded(true);
                    c.setDownloaded(event.downloaded);
                    c.setFilesize(event.filesize);
                    Log.e("Download Finished", event.part_id + "/" + event.downloaded + "/" + event.filesize);
                } else {
                    c.setDownloaded(event.downloaded);
                    c.setFilesize(event.filesize);
                    c.setModel_downloaded(false);
                }
                culturePartAdapter = new CulturePartAdapter(ChooseCulturePartActivity.this, culturePartList, culturePart_image);
                parts_lv.setLayoutManager(new GridLayoutManager(ChooseCulturePartActivity.this, 2));
                parts_lv.setAdapter(culturePartAdapter);
                culturePartAdapter.notifyDataSetChanged();
            }
        }
        //finish();
    }

    //Get Culture part downloadd infos for supply progressbar
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getBitmapRecognizeState(ImageRecognitionProcessEvent event) {
        for (CulturePart c : culturePartList) {
            if ((c.getId() == event.part_id)) {
                if (!event.finished) {
                    c.setRecognizing(true);
                }else{
                    c.setRecognizing(false);
                }
            }
            culturePartAdapter = new CulturePartAdapter(ChooseCulturePartActivity.this, culturePartList, culturePart_image);
            parts_lv.setLayoutManager(new GridLayoutManager(ChooseCulturePartActivity.this, 2));
            parts_lv.setAdapter(culturePartAdapter);
            culturePartAdapter.notifyDataSetChanged();
        }
    }
}
