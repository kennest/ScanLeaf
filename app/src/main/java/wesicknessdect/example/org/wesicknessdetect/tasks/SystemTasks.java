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
import android.util.Base64;
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

import androidx.fragment.app.FragmentActivity;

import rx.functions.Action1;
import wesicknessdect.example.org.wesicknessdetect.activities.tensorflow.Classifier;
import wesicknessdect.example.org.wesicknessdetect.activities.tensorflow.ImageClassifier;
import wesicknessdect.example.org.wesicknessdetect.activities.tensorflow.TensorFlowImageClassifier;
import wesicknessdect.example.org.wesicknessdetect.activities.tensorflow.TensorFlowObjectDetectionAPIModel;
import wesicknessdect.example.org.wesicknessdetect.activities.tensorflow.env.Logger;
import wesicknessdect.example.org.wesicknessdetect.events.ImageRecognitionProcessEvent;


public class SystemTasks {
    private static SystemTasks systemTasks;
    private static Activity mContext;
    private Location loc;
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
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
                Log.e("PERMISSIONS","ALL CHECKED");
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
            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
        }).check();
    }


    public String checkImage(Bitmap bitmap){
        String checked="";
        try {
            checker= new ImageClassifier(mContext);
             checked=  checker.classifyFrame(bitmap);
            //Log.e("Checked Result->", checked);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return checked;
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

                    LOGGER.e("Model loaded infos", model + "//" + label + "//" + detector.getStatString());
                    recognitions = detector.recognizeImage(bitmap);
                    Log.e("Recognitions", recognitions.toString());

                        EventBus.getDefault().post(new ImageRecognitionProcessEvent(part_id, true, recognitions.subList(0, 4)));


                } catch (final IOException e) {
                        EventBus.getDefault().post(new ImageRecognitionProcessEvent(part_id, true, recognitions.subList(0, 4)));
                    LOGGER.e("Exception initializing classifier!", e.getMessage());
                }
        }

        return recognitions;
    }
}
