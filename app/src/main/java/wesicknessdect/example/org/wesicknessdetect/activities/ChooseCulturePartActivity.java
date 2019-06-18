package wesicknessdect.example.org.wesicknessdetect.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import io.reactivex.subscribers.DisposableSubscriber;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.io.File;
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
import io.reactivex.Completable;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.activities.tensorflow.Classifier;
import wesicknessdect.example.org.wesicknessdetect.adapters.CulturePartAdapter;
import wesicknessdect.example.org.wesicknessdetect.database.AppDatabase;
import wesicknessdect.example.org.wesicknessdetect.events.DeletePartPictureEvent;
import wesicknessdect.example.org.wesicknessdetect.events.ImageRecognitionProcessEvent;
import wesicknessdect.example.org.wesicknessdetect.events.ModelDownloadEvent;
import wesicknessdect.example.org.wesicknessdetect.tasks.SystemTasks;
import wesicknessdect.example.org.wesicknessdetect.models.CulturePart;
import wesicknessdect.example.org.wesicknessdetect.models.Model;

public class ChooseCulturePartActivity extends BaseActivity {
    List<CulturePart> cultureParts = new ArrayList<>();
    Map<Integer, String> images_by_part = new HashMap<>();
    Map<Integer, Bitmap> BAD_IMAGES = new HashMap<>();
    Map<Integer, List<Classifier.Recognition>> recognitions_by_part = new HashMap<>();
    private static AppDatabase DB;

    @BindView(R.id.culture_parts_lv)
    RecyclerView parts_lv;

    @BindView(R.id.btn_analysis)
    Button analysisBtn;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    CulturePartAdapter culturePartAdapter;
    LayoutAnimationController controller;

    File compressedImg = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_picture);
        ButterKnife.bind(this);
        DB = AppDatabase.getInstance(this);
        controller = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_fall_down);

        toolbar.setTitle("Prendre une photo pour chaque partie");


        //Diable AnalysisBtn if no image selected
        if (images_by_part.size() == 0) {
            disableAnalysisBtn();
        } else {
            enableAnalysisBtn();
        }

        DB.culturePartsDao().rxGetAll()
                .subscribeOn(Schedulers.trampoline())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<CulturePart>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<CulturePart> culturePartList) {
                        cultureParts = culturePartList;
                        culturePartAdapter = new CulturePartAdapter(ChooseCulturePartActivity.this, culturePartList, new HashMap<>());
                        parts_lv.setLayoutManager(new GridLayoutManager(ChooseCulturePartActivity.this, 2));
                        parts_lv.setLayoutAnimation(controller);
                        parts_lv.setAdapter(culturePartAdapter);
                        parts_lv.scheduleLayoutAnimation();
                        for (CulturePart c : culturePartList) {
                            DB.modelDao().rxGetByPart(c.getId())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new DisposableSingleObserver<Model>() {

                                        @Override
                                        public void onSuccess(Model m) {
                                            if (new File(m.getPb()).exists()) {
                                                c.setModel_downloaded(true);
                                                c.setDownloaded(1000);
                                                c.setFilesize(1000);
                                            }
                                            culturePartAdapter = new CulturePartAdapter(ChooseCulturePartActivity.this, cultureParts, new HashMap<>());
                                            parts_lv.setLayoutManager(new GridLayoutManager(ChooseCulturePartActivity.this, 2));
                                            parts_lv.setAdapter(culturePartAdapter);
                                            parts_lv.setLayoutAnimation(controller);
                                            culturePartAdapter.notifyDataSetChanged();
                                            parts_lv.scheduleLayoutAnimation();
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            Log.e("Rx Model load Err ->" + c.getId(), e.getMessage());
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

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

    @SuppressLint("CheckResult")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.e("Req code", requestCode + "");
        for (CulturePart c : cultureParts) {
            if (resultCode == Activity.RESULT_OK && requestCode == c.getId()) {
                assert data != null;
                ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
                images_by_part.put((int) c.getId(), returnValue.get(0));
                Log.d("Picture choose->",returnValue.get(0));
                //Log.e(getLocalClassName()+" images:", images_by_part.size()+ "");
                culturePartAdapter = new CulturePartAdapter(ChooseCulturePartActivity.this, cultureParts, images_by_part);
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
    @SuppressLint({"StaticFieldLeak", "CheckResult"})
    @OnClick(R.id.btn_analysis)
    public void doAnalysis() {
        analysisBtn.setEnabled(false);
        analysisBtn.setClickable(false);
        analysisBtn.setText("TRAITEMENT...");
        analysisBtn.setBackgroundColor(getResources().getColor(R.color.gray));
        Completable.fromAction(() -> {
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
        })
                .subscribeOn(Schedulers.trampoline())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                            if (BAD_IMAGES.size() > 0) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ChooseCulturePartActivity.this);
                                View v = getLayoutInflater().inflate(R.layout.bad_images_layout, null, false);
                                LinearLayout layout = v.findViewById(R.id.bad_images_layout);
                                builder.setTitle("Attention!!!");
                                if (BAD_IMAGES.size() == 1) {
                                    builder.setMessage(String.format("partie de cacao non detectée sur %d image,voulez vous quand même l'analyser?", BAD_IMAGES.size()));
                                } else {
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
                                                DB.modelDao().rxGetByPart((long) entry.getKey())
                                                        .subscribeOn(Schedulers.trampoline())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribe(new SingleObserver<Model>() {
                                                            @Override
                                                            public void onSubscribe(Disposable d) {

                                                            }

                                                            @Override
                                                            public void onSuccess(Model model) {
                                                                Bitmap bitmap = BitmapFactory.decodeFile(entry.getValue());
                                                                Bitmap bitmap_cropped = Bitmap.createScaledBitmap(bitmap, 500, 500, false);
                                                                doRecognizingImage(model.getPb(), model.getLabel(), entry.getKey(), bitmap_cropped);
                                                            }

                                                            @Override
                                                            public void onError(Throwable e) {

                                                            }
                                                        });
                                            }
                                        }
                                    });
                                }
                                builder.setView(v);
                                AlertDialog dialog = builder.create();
                                dialog.show();

                            } else {
                                Log.d("Rx PIX SIZE ->", images_by_part.size() + "");
                                for (Map.Entry<Integer, String> entry : images_by_part.entrySet()) {
                                    DB.modelDao().rxGetByPart((long) entry.getKey())
                                            .subscribeOn(Schedulers.trampoline())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new SingleObserver<Model>() {

                                                @Override
                                                public void onSubscribe(Disposable d) {

                                                }

                                                @Override
                                                public void onSuccess(Model model) {
                                                    Log.d("Rx Model Treat ->", model.getId() + "");
                                                    Bitmap bitmap = BitmapFactory.decodeFile(entry.getValue());
                                                    Bitmap bitmap_cropped = Bitmap.createScaledBitmap(bitmap, 500, 500, false);
                                                    doRecognizingImage(model.getPb(), model.getLabel(), entry.getKey(), bitmap_cropped);
                                                }

                                                @Override
                                                public void onError(Throwable e) {
                                                    Log.e("Rx Model load Err", e.getMessage());
                                                }
                                            });
                                }
                            }
                        },
                        throwable -> Log.e("Recognize Error", throwable.getMessage()));

    }

    @SuppressLint("StaticFieldLeak")
    private void doRecognizingImage(String modelpath, String labelpath, int part_id, Bitmap bitmap) {
        File modelfilepath = new File(modelpath);
        File labelfilepath = new File(labelpath);

        if (modelfilepath.exists() && labelfilepath.exists()) {
            for (CulturePart c : cultureParts) {
                if ((c.getId() == part_id)) {
                    c.setRecognizing(true);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            culturePartAdapter = new CulturePartAdapter(ChooseCulturePartActivity.this, cultureParts, images_by_part);
                            parts_lv.setLayoutManager(new GridLayoutManager(ChooseCulturePartActivity.this, 2));
                            parts_lv.setAdapter(culturePartAdapter);
                            culturePartAdapter.notifyDataSetChanged();
                            Log.d("Rx Recognizing ->", part_id + "");
                        }
                    });
                }
            }
            SystemTasks.getInstance(ChooseCulturePartActivity.this)
                    .recognizedSymptoms(bitmap, modelpath, labelpath, part_id)
                    .subscribeOn(Schedulers.trampoline())
                    //.observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<ImageRecognitionProcessEvent>() {

                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(ImageRecognitionProcessEvent event) {
                            getBitmapRecognizeState(event);
                        }

                        @Override
                        public void onError(Throwable t) {

                        }
                    });
        } else {
            Log.e("Recognizing Error", "Cannot find models and labels");
        }
    }

    //Get Culture part downloadd infos for supply progressbar
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void getModelDownloadBytes(ModelDownloadEvent event) {
        //Log.e("Download started", event.part_id + "/" + event.downloaded + "/" + event.filesize);
        for (CulturePart c : cultureParts) {
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
                culturePartAdapter = new CulturePartAdapter(ChooseCulturePartActivity.this, cultureParts, images_by_part);
                parts_lv.setLayoutManager(new GridLayoutManager(ChooseCulturePartActivity.this, 2));
                parts_lv.setAdapter(culturePartAdapter);
                culturePartAdapter.notifyDataSetChanged();
            }
        }
        //finish();
    }

    public void getBitmapRecognizeState(ImageRecognitionProcessEvent event) {
        Log.d("Rx Recognition state ->", event.recognitions.toString());
        for (CulturePart c : cultureParts) {
            if ((c.getId() == event.part_id)) {
                if (!event.finished) {
                    c.setRecognizing(true);
                } else {
                    c.setRecognizing(false);
                    c.setChecked(true);
                    recognitions_by_part.put((int) event.part_id, event.recognitions);
                }
            }
            culturePartAdapter = new CulturePartAdapter(ChooseCulturePartActivity.this, cultureParts, images_by_part);
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
        HashMap<Integer, String> tmp_image_part = new HashMap<>(images_by_part);
        for (Map.Entry<Integer, String> entry : tmp_image_part.entrySet()) {
            //Log.e("picture delete ", entry.getKey() + "//"+event.part_id);
            if (entry.getKey() == Integer.valueOf((Integer) event.part_id)) {
                //Log.e("picture delete ", entry.getKey() + "//"+event.part_id);
                images_by_part.remove(entry.getKey());
                culturePartAdapter = new CulturePartAdapter(ChooseCulturePartActivity.this, cultureParts, images_by_part);
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


    @Override
    protected void onResume() {
        super.onResume();
    }
}
