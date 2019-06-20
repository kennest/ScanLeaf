package wesicknessdect.example.org.wesicknessdetect.tasks;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.appizona.yehiahd.fastsave.FastSave;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.jetradarmobile.rxlocationsettings.RxLocationSettings;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import io.reactivex.Flowable;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import androidx.fragment.app.FragmentActivity;

import io.reactivex.Observable;
import io.reactivex.Single;
import rx.functions.Action1;
import wesicknessdect.example.org.wesicknessdetect.activities.tensorflow.Classifier;
import wesicknessdect.example.org.wesicknessdetect.activities.tensorflow.Classifier.Recognition;
import wesicknessdect.example.org.wesicknessdetect.activities.tensorflow.ImageClassifier;
import wesicknessdect.example.org.wesicknessdetect.activities.tensorflow.TensorFlowObjectDetectionAPIModel;
import wesicknessdect.example.org.wesicknessdetect.activities.tensorflow.env.Logger;
import wesicknessdect.example.org.wesicknessdetect.events.ImageRecognitionProcessEvent;
import wesicknessdect.example.org.wesicknessdetect.utils.Constants;


public class SystemTasks {
    private static SystemTasks systemTasks;
    private static Activity mContext;
    private ImageClassifier checker;

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


    @SuppressLint({"CheckResult", "MissingPermission"})
    public void getLocation() {
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                Log.d("Location", location.getLatitude() + "//" + location.getLongitude());
                FastSave.getInstance().saveString("location", location.getLatitude() + ":" + location.getLongitude());
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }

    public void ensureLocationSettings() {
        Dexter.withActivity(mContext)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_NETWORK_STATE
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                Log.e("PERMISSIONS", "ALL CHECKED");
                LocationSettingsRequest locationSettingsRequest = new LocationSettingsRequest.Builder()
                        .addLocationRequest(LocationRequest.create().setPriority(LocationRequest.PRIORITY_LOW_POWER))
                        .build();
                RxLocationSettings.with((FragmentActivity) mContext).ensure(locationSettingsRequest).subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean enabled) {
                        // Toast.makeText(BootActivity.this, enabled ? "Enabled" : "Failed",
                        // Toast.LENGTH_LONG).show();
                        if (enabled) {
                            Constants.getLocation(mContext);
                        }
                    }
                });
            }



            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
        }).check();
    }


    public String checkImage(Bitmap bitmap) {
        String checked = "";
        try {
            checker = new ImageClassifier(mContext);
            checked = checker.classifyFrame(bitmap);
            //Log.e("Checked Result->", checked);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return checked;
    }

    //Recognized Symptoms on given bitmap
    public ImageRecognitionProcessEvent recognizedSymptoms(Bitmap bitmap, String model, String label, long part_id) {
        //EventBus.getDefault().post(new ImageRecognitionProcessEvent(part_id, false, new ArrayList<>()));
        List<Recognition> recognitions = new ArrayList<>();
        ImageRecognitionProcessEvent event = new ImageRecognitionProcessEvent(part_id, false, recognitions);
        if (MODE == DetectorMode.TF_OD_API) {
            try {
                detector = TensorFlowObjectDetectionAPIModel.create(
                        model,
                        label,
                        TF_OD_API_INPUT_SIZE);
                cropSize = TF_OD_API_INPUT_SIZE;

                LOGGER.e("Rx Model loaded infos", model + "//" + label + "//" + detector.getStatString());
                recognitions = detector.recognizeImage(bitmap);
                event = new ImageRecognitionProcessEvent(part_id, true, recognitions);
                Log.e("Rx Recognitions ->", recognitions.toString());
                detector.close();
                return event;

                //EventBus.getDefault().post(new ImageRecognitionProcessEvent(part_id, true, recognitions.subList(0, 4)));
            } catch (final IOException e) {
                LOGGER.e("Rx Exception initializing classifier!", e.getMessage());
                detector.close();
                return event;
                //EventBus.getDefault().post(new ImageRecognitionProcessEvent(part_id, true, recognitions));
            }
        }
        return event;
    }
}
