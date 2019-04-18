package wesicknessdect.example.org.wesicknessdetect.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.Toolbar;
import com.fxn.pix.Pix;
import com.google.gson.Gson;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

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
import wesicknessdect.example.org.wesicknessdetect.events.DeletePartPictureEvent;
import wesicknessdect.example.org.wesicknessdetect.events.ImageRecognitionProcessEvent;
import wesicknessdect.example.org.wesicknessdetect.events.ModelDownloadEvent;
import wesicknessdect.example.org.wesicknessdetect.futuretasks.SystemTasks;
import wesicknessdect.example.org.wesicknessdetect.models.CulturePart;
import wesicknessdect.example.org.wesicknessdetect.models.Model;

public class ChooseCulturePartActivity extends BaseActivity {
    List<CulturePart> culturePartList = new ArrayList<>();
    Map<Integer, String> images_by_part = new HashMap<>();
    Map<Integer, List<Classifier.Recognition>> recognitions_by_part =new HashMap<>();
    private static AppDatabase DB;

    private static final ExecutorService executor=Executors.newSingleThreadExecutor();

    @BindView(R.id.culture_parts_lv)
    RecyclerView parts_lv;

    @BindView(R.id.btn_analysis)
    Button analysisBtn;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    CulturePartAdapter culturePartAdapter;
    LayoutAnimationController controller;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_picture);
        ButterKnife.bind(this);
        DB = AppDatabase.getInstance(this);
        controller=AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_fall_down);

        toolbar.setTitle("Choose Culture Part");


        //Diable AnalysisBtn if no image selected
        if(images_by_part.size()==0) {
            disableAnalysisBtn();
        }else{
            enableAnalysisBtn();
        }

        DB.culturePartsDao().getAll().observe(this, new Observer<List<CulturePart>>() {
            @Override
            public void onChanged(List<CulturePart> cultureParts) {
                culturePartList = cultureParts;
                culturePartAdapter = new CulturePartAdapter(ChooseCulturePartActivity.this, culturePartList, new HashMap<>());
                parts_lv.setLayoutManager(new GridLayoutManager(ChooseCulturePartActivity.this, 2));
                parts_lv.setLayoutAnimation(controller);
                parts_lv.setAdapter(culturePartAdapter);
                parts_lv.scheduleLayoutAnimation();
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
                        parts_lv.setLayoutAnimation(controller);
                        culturePartAdapter.notifyDataSetChanged();
                        parts_lv.scheduleLayoutAnimation();
                    }
                });
            }
        });

    }

    private void disableAnalysisBtn(){
        analysisBtn.setActivated(false);
        analysisBtn.setClickable(false);
        analysisBtn.setEnabled(false);
        analysisBtn.setBackgroundColor(getResources().getColor(R.color.gray));
    }

    private void enableAnalysisBtn(){
        analysisBtn.setActivated(true);
        analysisBtn.setClickable(true);
        analysisBtn.setEnabled(true);
        analysisBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("Req code", requestCode + "");
        for (CulturePart c : culturePartList) {
            if (resultCode == Activity.RESULT_OK && requestCode == c.getId()) {
                assert data != null;
                ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
                images_by_part.put((int) c.getId(), returnValue.get(0));
                //Log.e(getLocalClassName()+" images:", images_by_part.size()+ "");
                culturePartAdapter = new CulturePartAdapter(ChooseCulturePartActivity.this, culturePartList, images_by_part);
                parts_lv.setLayoutManager(new GridLayoutManager(ChooseCulturePartActivity.this, 2));
                parts_lv.setLayoutAnimation(controller);
                parts_lv.setAdapter(culturePartAdapter);
                culturePartAdapter.notifyDataSetChanged();
                parts_lv.scheduleLayoutAnimation();
            }
        }
        if(images_by_part.size()==0) {
            disableAnalysisBtn();
        }else{
            enableAnalysisBtn();
        }
    }


    //Launch Analysis for each parts
    @SuppressLint("StaticFieldLeak")
    @OnClick(R.id.btn_analysis)
    public void doAnalysis() {
        analysisBtn.setEnabled(false);
        analysisBtn.setClickable(false);
        analysisBtn.setText("TRAITEMENT...");
        analysisBtn.setBackgroundColor(getResources().getColor(R.color.gray));
        for (Map.Entry<Integer, String> entry : images_by_part.entrySet()) {

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
                                recognitions = SystemTasks.getInstance(ChooseCulturePartActivity.this).recognizedSymptoms(bitmap_cropped, model.getPb(), model.getLabel(), model.getPart_id());
                                Log.d(entry.getKey() + ":Recognitions -> ", recognitions.toString());
                                return null;
                            }
                        }.execute();
                    }else{
                        Log.e("Recognizing Error","Cannot find models and labels");
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
                culturePartAdapter = new CulturePartAdapter(ChooseCulturePartActivity.this, culturePartList, images_by_part);
                parts_lv.setLayoutManager(new GridLayoutManager(ChooseCulturePartActivity.this, 2));
                parts_lv.setAdapter(culturePartAdapter);
                culturePartAdapter.notifyDataSetChanged();
            }
        }
        //finish();
    }

    //Get Culture part recognizing infos for supply progressbar
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getBitmapRecognizeState(ImageRecognitionProcessEvent event) {
        for (CulturePart c : culturePartList) {
            if ((c.getId() == event.part_id)) {
                if (!event.finished) {
                    c.setRecognizing(true);
                }else{
                    c.setRecognizing(false);
                    c.setChecked(true);
                    recognitions_by_part.put((int) event.part_id,event.recognitions);
                }
            }
            culturePartAdapter = new CulturePartAdapter(ChooseCulturePartActivity.this, culturePartList, images_by_part);
            parts_lv.setLayoutManager(new GridLayoutManager(ChooseCulturePartActivity.this, 2));
            parts_lv.setAdapter(culturePartAdapter);
            culturePartAdapter.notifyDataSetChanged();
        }
        if(recognitions_by_part.size()==images_by_part.size()){
            //Log.e(getLocalClassName()+" GoToresult",recognitions_by_part.size()+"//"+images_by_part.size());
            goToPartialResult();
            analysisBtn.setEnabled(true);
            analysisBtn.setClickable(true);
            analysisBtn.setText("ANALYSER");
            analysisBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    //Listen for deletion on picture part
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeletePartPicture(DeletePartPictureEvent event){
        for (Map.Entry<Integer, String> entry : images_by_part.entrySet()) {
            //Log.e("picture delete ", entry.getKey() + "//"+event.part_id);
            if (entry.getKey()==Integer.valueOf((Integer) event.part_id)) {
                //Log.e("picture delete ", entry.getKey() + "//"+event.part_id);
                images_by_part.remove(entry.getKey());
                culturePartAdapter = new CulturePartAdapter(ChooseCulturePartActivity.this, culturePartList, images_by_part);
                parts_lv.setLayoutManager(new GridLayoutManager(ChooseCulturePartActivity.this, 2));
                parts_lv.setAdapter(culturePartAdapter);
                culturePartAdapter.notifyDataSetChanged();
            }
        }
        if(images_by_part.size()==0) {
            disableAnalysisBtn();
        }else{
            enableAnalysisBtn();
        }
    }

    private void goToPartialResult(){
        Intent partial=new Intent(ChooseCulturePartActivity.this,PartialResultActivity.class);
        Gson gson=new Gson();
        String recognitions=gson.toJson(recognitions_by_part);
        String images=gson.toJson(images_by_part);

        //Log.e(getLocalClassName()+" GoToresult:",images);
        partial.putExtra("recognitions_by_part",recognitions);
        partial.putExtra("images_by_part", images);

        startActivity(partial);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
