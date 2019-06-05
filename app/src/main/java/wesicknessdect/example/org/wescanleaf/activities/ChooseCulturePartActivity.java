package wesicknessdect.example.org.wescanleaf.activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toolbar;

import com.appizona.yehiahd.fastsave.FastSave;
import com.fxn.pix.Pix;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import wesicknessdect.example.org.wescanleaf.activities.tensorflow.Classifier;
import wesicknessdect.example.org.wescanleaf.R;
import wesicknessdect.example.org.wescanleaf.adapters.CulturePartAdapter;
import wesicknessdect.example.org.wescanleaf.database.AppDatabase;
import wesicknessdect.example.org.wescanleaf.events.DeletePartPictureEvent;
import wesicknessdect.example.org.wescanleaf.events.ImageRecognitionProcessEvent;
import wesicknessdect.example.org.wescanleaf.events.ModelDownloadEvent;
import wesicknessdect.example.org.wescanleaf.tasks.SystemTasks;
import wesicknessdect.example.org.wescanleaf.models.CulturePart;
import wesicknessdect.example.org.wescanleaf.models.Model;

public class ChooseCulturePartActivity extends BaseActivity {
    List<CulturePart> culturePartList = new ArrayList<>();
    Map<Integer, String> images_by_part = new HashMap<>();
    Map<Integer, Bitmap> BAD_IMAGES = new HashMap<>();
    Map<Integer, List<Classifier.Recognition>> recognitions_by_part = new HashMap<>();
    private static AppDatabase DB;
    List<Classifier.Recognition> check_recognitions = new ArrayList<>();

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
        controller = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_fall_down);

        toolbar.setTitle("Choose Culture Part");


        //Diable AnalysisBtn if no image selected
        if (images_by_part.size() == 0) {
            disableAnalysisBtn();
        } else {
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
                        //Log.e("Number of models", models.size() + "");
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

    private void disableAnalysisBtn() {
        analysisBtn.setActivated(false);
        analysisBtn.setClickable(false);
        analysisBtn.setEnabled(false);
        analysisBtn.setBackgroundColor(getResources().getColor(R.color.gray));
    }

    private void enableAnalysisBtn() {
        analysisBtn.setActivated(true);
        analysisBtn.setClickable(true);
        analysisBtn.setEnabled(true);
        analysisBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.e("Req code", requestCode + "");
        for (CulturePart c : culturePartList) {
            if (resultCode == RESULT_OK && requestCode == c.getId()) {
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
        if (images_by_part.size() == 0) {
            disableAnalysisBtn();
        } else {
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
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                for (Map.Entry<Integer, String> entry : images_by_part.entrySet()) {
                    String check_label = FastSave.getInstance().getString("check_label", "");
                    String check_model = FastSave.getInstance().getString("check_model", "");

                    //Log.e("Checks filepath ->",check_label+"->"+check_model);
                    File fcheckmodel = new File(check_model);
                    File fchecklabel = new File(check_label);

                    if (fchecklabel.exists() && fcheckmodel.exists()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(entry.getValue());
                        Bitmap bitmap_cropped = Bitmap.createScaledBitmap(bitmap, 224, 224, false);

                        String result = SystemTasks.getInstance(ChooseCulturePartActivity.this).checkImage(bitmap_cropped);
                        Log.e("Checks Result 0->", result);
                        Gson gson = new Gson();
                        Map<String, Float> result_map = new HashMap<>();
                        Type typeOfHashMap = new TypeToken<Map<String, Float>>() {
                        }.getType();

                        String checked = "";
                        result_map = gson.fromJson(result, typeOfHashMap);
                        Float max = Collections.max(result_map.values());
                        for (Map.Entry<String, Float> n : result_map.entrySet()) {
                            if (n.getValue() == max) {
                                checked = n.getKey();
                            }
                        }
                        if (checked.equals("bad")) {
                            Log.e("Checkd Error ->", "BAD IMAGE ");
                            BAD_IMAGES.put(entry.getKey(), bitmap_cropped);
                        }

                    }
                }

                if (BAD_IMAGES.size() > 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ChooseCulturePartActivity.this);
                            View v = getLayoutInflater().inflate(R.layout.bad_images_layout, null, false);
                            LinearLayout layout = v.findViewById(R.id.bad_images_layout);
                            builder.setTitle("Attention!!!");
                            if(BAD_IMAGES.size()==1) {
                                builder.setMessage(String.format("partie de cacao non detectée sur %d image,voulez vous quand même l'analyser?", BAD_IMAGES.size()));
                            }else {
                                builder.setMessage(String.format("partie de cacao non detectée sur %d images,voulez vous quand même l'analyser?", BAD_IMAGES.size()));
                            }
                            for (Map.Entry<Integer, Bitmap> bad : BAD_IMAGES.entrySet()) {

                                View item = getLayoutInflater().inflate(R.layout.bad_image_item, null, false);
                                ImageView image = item.findViewById(R.id.image);
                                ImageButton delete = item.findViewById(R.id.delete);
                                image.setImageBitmap(bad.getValue());
                                layout.addView(item);

                                delete.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        layout.removeView(item);
                                        BAD_IMAGES.remove(bad.getKey());
                                        EventBus.getDefault().post(new DeletePartPictureEvent(bad.getKey()));
                                    }
                                });

                                builder.setPositiveButton("Continuer", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        //doRecognizingImage(model.getPb(),model.getLabel(),entry.getKey(),bitmap_cropped);
                                        for (Map.Entry<Integer, String> entry : images_by_part.entrySet()) {
                                            DB.modelDao().getByPart((long) entry.getKey()).observe(ChooseCulturePartActivity.this, new Observer<Model>() {
                                                @Override
                                                public void onChanged(Model model) {
                                                    Bitmap bitmap = BitmapFactory.decodeFile(entry.getValue());
                                                    Bitmap bitmap_cropped = Bitmap.createScaledBitmap(bitmap, 500, 500, false);
                                                    doRecognizingImage(model.getPb(), model.getLabel(), entry.getKey(), bitmap_cropped);
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                            builder.setView(v);
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    });

                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (Map.Entry<Integer, String> entry : images_by_part.entrySet()) {
                                DB.modelDao().getByPart((long) entry.getKey()).observe(ChooseCulturePartActivity.this, new Observer<Model>() {
                                    @Override
                                    public void onChanged(Model model) {
                                        Bitmap bitmap = BitmapFactory.decodeFile(entry.getValue());
                                        Bitmap bitmap_cropped = Bitmap.createScaledBitmap(bitmap, 500, 500, false);
                                        doRecognizingImage(model.getPb(), model.getLabel(), entry.getKey(), bitmap_cropped);
                                    }
                                });
                            }
                        }
                    });

                }
                return null;
            }
        }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);


    }

    @SuppressLint("StaticFieldLeak")
    private void doRecognizingImage(String modelpath, String labelpath, int part_id, Bitmap bitmap) {
        File modelfilepath = new File(modelpath);
        File labelfilepath = new File(labelpath);
        if (modelfilepath.exists() && labelfilepath.exists()) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    SystemTasks.getInstance(ChooseCulturePartActivity.this).recognizedSymptoms(bitmap, modelpath, labelpath, part_id);
                    return null;
                }
            }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        } else {
            Log.e("Recognizing Error", "Cannot find models and labels");
        }
    }

    //Get Culture part downloadd infos for supply progressbar
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void getModelDownloadBytes(ModelDownloadEvent event) {
        //Log.e("Download started", event.part_id + "/" + event.downloaded + "/" + event.filesize);
        for (CulturePart c : culturePartList) {
            if ((c.getId() == event.part_id)) {
                if (event.downloaded == event.filesize) {
                    c.setModel_downloaded(true);
                    c.setDownloaded(event.downloaded);
                    c.setFilesize(event.filesize);
                    //Log.e("Download Finished", event.part_id + "/" + event.downloaded + "/" + event.filesize);
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
                } else {
                    c.setRecognizing(false);
                    c.setChecked(true);
                    recognitions_by_part.put((int) event.part_id, event.recognitions);
                }
            }
            culturePartAdapter = new CulturePartAdapter(ChooseCulturePartActivity.this, culturePartList, images_by_part);
            parts_lv.setLayoutManager(new GridLayoutManager(ChooseCulturePartActivity.this, 2));
            parts_lv.setAdapter(culturePartAdapter);
            culturePartAdapter.notifyDataSetChanged();
        }
        if (recognitions_by_part.size() == images_by_part.size()) {
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
    public void onDeletePartPicture(DeletePartPictureEvent event) {
        HashMap<Integer, String> tmp_image_part=new HashMap<>(images_by_part);
        for (Map.Entry<Integer, String> entry : tmp_image_part.entrySet()) {
            //Log.e("picture delete ", entry.getKey() + "//"+event.part_id);
            if (entry.getKey() == Integer.valueOf((Integer) event.part_id)) {
                //Log.e("picture delete ", entry.getKey() + "//"+event.part_id);
                images_by_part.remove(entry.getKey());
                culturePartAdapter = new CulturePartAdapter(ChooseCulturePartActivity.this, culturePartList, images_by_part);
                parts_lv.setLayoutManager(new GridLayoutManager(ChooseCulturePartActivity.this, 2));
                parts_lv.setAdapter(culturePartAdapter);
                culturePartAdapter.notifyDataSetChanged();
            }
        }
        if (images_by_part.size() == 0) {
            disableAnalysisBtn();
        } else {
            enableAnalysisBtn();
        }
    }

    private void goToPartialResult() {
        Intent partial = new Intent(ChooseCulturePartActivity.this, PartialResultActivity.class);
        Gson gson = new Gson();
        String recognitions = gson.toJson(recognitions_by_part);
        String images = gson.toJson(images_by_part);

        //Log.e(getLocalClassName()+" GoToresult:",images);
        partial.putExtra("recognitions_by_part", recognitions);
        partial.putExtra("images_by_part", images);

        startActivity(partial);
    }

    private File createFileFromInputStream(InputStream inputStream, String my_file_name) {

        try {
            File f = new File(my_file_name);
            OutputStream outputStream = new FileOutputStream(f);
            byte buffer[] = new byte[1024];
            int length = 0;

            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();

            return f;
        } catch (IOException e) {
            //Logging exception
        }

        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
