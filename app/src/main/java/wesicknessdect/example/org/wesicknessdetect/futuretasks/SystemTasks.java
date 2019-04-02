package wesicknessdect.example.org.wesicknessdetect.futuretasks;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;

import com.appizona.yehiahd.fastsave.FastSave;
import com.github.florent37.rxgps.RxGps;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.material.snackbar.Snackbar;
import com.jetradarmobile.rxlocationsettings.RxLocationSettings;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import rx.functions.Action1;
import wesicknessdect.example.org.wesicknessdetect.activities.tensorflow.Classifier;
import wesicknessdect.example.org.wesicknessdetect.activities.tensorflow.TensorFlowObjectDetectionAPIModel;
import wesicknessdect.example.org.wesicknessdetect.activities.tensorflow.env.Logger;
import wesicknessdect.example.org.wesicknessdetect.events.ImageRecognitionProcessEvent;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;


public class SystemTasks {
    private static SystemTasks systemTasks;
    private static Activity mContext;
    private Location loc;

    private SystemTasks(Activity context) {
        mContext = context;
    }  //private constructor.

    public static SystemTasks getInstance(Activity context) {
        if (systemTasks == null) { //if there is no instance available... create new one
            systemTasks = new SystemTasks(context);
        }
        mContext = context;
        return systemTasks;
    }

    //TF var
    Classifier detector;
    private static final int TF_OD_API_INPUT_SIZE = 500;
    int cropSize = TF_OD_API_INPUT_SIZE;

    private enum DetectorMode {
        TF_OD_API, MULTIBOX, YOLO;
    }

    private Bitmap croppedBitmap = null;
    private static final DetectorMode MODE = DetectorMode.TF_OD_API;
    private static final Logger LOGGER = new Logger();


    //Converti le fichier en base64
    public String imageToB64(String filepath) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        FutureTask<String> future =
                new FutureTask<String>(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        InputStream inputStream = null;
                        String fileB64 = "";
                        File f = new File(filepath);
                        try {
                            inputStream = new FileInputStream(f.getAbsolutePath());

                            byte[] buffer = new byte[8192];
                            int bytesRead;
                            ByteArrayOutputStream output = new ByteArrayOutputStream();
                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                output.write(buffer, 0, bytesRead);
                            }
                            byte file[] = output.toByteArray();

                            fileB64 = Base64.encodeToString(file, 0);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.i("fileb64", fileB64);
                        return fileB64;
                    }
                });
        executor.execute(future);
        return future.get();
    }

    @SuppressLint("CheckResult")
    public void getLocation() {
        new RxGps(mContext).locationLowPower()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(location -> {
                    //you've got the location
                    Log.d("Location", location.getLatitude() + "//" + location.getLongitude());
                    FastSave.getInstance().saveString("location",location.getLatitude()+":"+location.getLongitude());
                }, throwable -> {
                    if (throwable instanceof RxGps.PermissionException) {
                        //the user does not allow the permission
                    } else if (throwable instanceof RxGps.PlayServicesNotAvailableException) {
                        //the user do not have play services
                        Log.e("Location", "No play services");
                    }
                });
    }
    public void ensureLocationSettings() {
        LocationSettingsRequest locationSettingsRequest = new LocationSettingsRequest.Builder()
                .addLocationRequest(LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY))
                .build();
        RxLocationSettings.with((FragmentActivity) mContext).ensure(locationSettingsRequest).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean enabled) {
                // Toast.makeText(BootActivity.this, enabled ? "Enabled" : "Failed",
                // Toast.LENGTH_LONG).show();
                if (enabled) {
                    getLocation();
                }
            }
        });
    }


    //Recognized Symptoms on given bitmap
    public List<Classifier.Recognition> recognizedSymptoms(Bitmap bitmap, String model, String label, long part_id) {
        EventBus.getDefault().post(new ImageRecognitionProcessEvent(part_id, false, new ArrayList<>()));
        List<Classifier.Recognition> recognitions = new ArrayList<>();
        if (MODE == DetectorMode.TF_OD_API) {
            try {
                detector = TensorFlowObjectDetectionAPIModel.create(
                        model,
                        label,
                        TF_OD_API_INPUT_SIZE);

                cropSize = TF_OD_API_INPUT_SIZE;

                LOGGER.e("Model loaded infos", model + "//" + label+"//"+detector.getStatString());

                recognitions = detector.recognizeImage(bitmap);
                Log.e("Recognitions", recognitions.toString());
                EventBus.getDefault().post(new ImageRecognitionProcessEvent(part_id, true, recognitions));

            } catch (final IOException e) {
                EventBus.getDefault().post(new ImageRecognitionProcessEvent(part_id, true, recognitions));
                LOGGER.e("Exception initializing classifier!", e);
            }

        }
        return recognitions;
    }
}
