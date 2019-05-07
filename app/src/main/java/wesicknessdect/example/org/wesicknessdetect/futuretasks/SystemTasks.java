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

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import rx.functions.Action1;
import wesicknessdect.example.org.wesicknessdetect.activities.SplashActivity;
import wesicknessdect.example.org.wesicknessdetect.activities.tensorflow.Classifier;
import wesicknessdect.example.org.wesicknessdetect.activities.tensorflow.TensorFlowObjectDetectionAPIModel;
import wesicknessdect.example.org.wesicknessdetect.activities.tensorflow.env.Logger;
import wesicknessdect.example.org.wesicknessdetect.events.ImageRecognitionProcessEvent;
import wesicknessdect.example.org.wesicknessdetect.models.SymptomRect;

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

//        new RxGps(mContext).locationBalancedPowerAcuracy()
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(location -> {
//                    //you've got the location
//                    Log.d("Location", location.getLatitude() + "//" + location.getLongitude());
//                    FastSave.getInstance().saveString("location",location.getLatitude()+":"+location.getLongitude());
//                }, throwable -> {
//                    if (throwable instanceof RxGps.PermissionException) {
//                        //the user does not allow the permission
//                        Log.e("Location", "No permissions");
//                    } else if (throwable instanceof RxGps.PlayServicesNotAvailableException) {
//                        //the user do not have play services
//                        Log.e("Location", "No play services");
//                    }
//                });
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
                LOGGER.e("Exception initializing classifier!", e);
            }

        }
        return recognitions;
    }
}
