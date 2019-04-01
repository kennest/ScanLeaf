package wesicknessdect.example.org.wesicknessdetect.futuretasks;

import android.Manifest;
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

import com.google.android.material.snackbar.Snackbar;

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
import wesicknessdect.example.org.wesicknessdetect.activities.tensorflow.Classifier;
import wesicknessdect.example.org.wesicknessdetect.activities.tensorflow.TensorFlowObjectDetectionAPIModel;
import wesicknessdect.example.org.wesicknessdetect.activities.tensorflow.env.Logger;
import wesicknessdect.example.org.wesicknessdetect.events.ImageRecognitionProcessEvent;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.content.Context.LOCATION_SERVICE;
import static androidx.core.app.ActivityCompat.requestPermissions;


public class SystemTasks {
    private static SystemTasks systemTasks;
    private static Activity mContext;
    private LocationManager locationManager;
    private LocationListener listener;
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

    public Location getLocation() {

        locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                loc = new Location(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                //
            }

            @Override
            public void onProviderEnabled(String s) {
                //
            }

            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(i);
            }
        };
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext,
                ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                request_permission();
            }
            return loc;
        } else {
            // permission has been granted
            locationManager.requestLocationUpdates("gps", 5000, 0, listener);
            return loc;
        }
        //return loc;
    }

    private void request_permission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(mContext,
                ACCESS_COARSE_LOCATION)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mContext.requestPermissions(new String[]{ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 10);
            }

        } else {
            // permission has not been granted yet. Request it directly.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mContext.requestPermissions(new String[]{ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 10);
            }
        }
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
