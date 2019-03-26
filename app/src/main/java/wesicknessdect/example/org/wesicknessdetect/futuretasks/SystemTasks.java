package wesicknessdect.example.org.wesicknessdetect.futuretasks;

import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import androidx.appcompat.app.AlertDialog;
import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.activities.ChooseCulturePartActivity;
import wesicknessdect.example.org.wesicknessdetect.activities.tensorflow.Classifier;
import wesicknessdect.example.org.wesicknessdetect.activities.tensorflow.TensorFlowObjectDetectionAPIModel;
import wesicknessdect.example.org.wesicknessdetect.activities.tensorflow.env.Logger;
import wesicknessdect.example.org.wesicknessdetect.events.ImageRecognitionProcessEvent;
import wesicknessdect.example.org.wesicknessdetect.retrofit.APIService;

public class SystemTasks {
    private static SystemTasks systemTasks;

    private SystemTasks() {
    }  //private constructor.

    public static SystemTasks getInstance() {
        if (systemTasks == null) { //if there is no instance available... create new one
            systemTasks = new SystemTasks();
        }
        return systemTasks;
    }

    private static APIService service;
    static final ExecutorService executor = Executors.newSingleThreadExecutor();
    String result = "";
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



    //Converti le base64 du model en fichier
    public String base64model(String modelpath, AssetManager assetManager) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        FutureTask<String> future =
                new FutureTask<>(new Callable<String>() {
                    public String call() throws InterruptedException, ExecutionException {
                        File file = new File(modelpath);
                        int size = (int) file.length();
                        byte[] bytes = Base64.decode(modelB64(assetManager), 0);
                        try {
                            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                            bos.write(bytes);
                            bos.flush();
                            bos.close();
                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        Log.i("base64model", file.getAbsolutePath());
                        return file.getAbsolutePath();
                    }
                });
        executor.execute(future);
        return future.get();
    }

    //Converti le model en Base64
    public String modelB64(AssetManager assetManager) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        FutureTask<String> future =
                new FutureTask<>(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        InputStream inputStream = null;
                        String modelb64 = "";
                        try {
                            inputStream = assetManager.open("ssd_mobilenet.pb");

                            byte[] buffer = new byte[8192];
                            int bytesRead;
                            ByteArrayOutputStream output = new ByteArrayOutputStream();
                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                output.write(buffer, 0, bytesRead);
                            }
                            byte file[] = output.toByteArray();

                            modelb64 = Base64.encodeToString(file, 0);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.i("modelb64", modelb64);

                        return modelb64;
                    }
                });
        executor.execute(future);
        return future.get();
    }

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


    //Recognized Symptoms on given bitmap
    public List<Classifier.Recognition> recognizedSymptoms(AssetManager assetManager, Bitmap bitmap, String model,String label,long part_id){
        EventBus.getDefault().post(new ImageRecognitionProcessEvent(part_id,false));
        List<Classifier.Recognition> recognitions=new ArrayList<>();
                if (MODE == DetectorMode.TF_OD_API) {
                    try {
                        detector = TensorFlowObjectDetectionAPIModel.create(
                                assetManager,
                                model,
                                label,
                                    TF_OD_API_INPUT_SIZE);

                        cropSize = TF_OD_API_INPUT_SIZE;
                        //detector.recognizeImage(bitmap);

                        LOGGER.e("Model loaded infos",model+"//"+label);

                        recognitions = detector.recognizeImage(bitmap);
                        Log.e("Recognitions", recognitions.toString());
                        EventBus.getDefault().post(new ImageRecognitionProcessEvent(part_id,true));

                    } catch (final IOException e) {
                        LOGGER.e("Exception initializing classifier!", e);
                    }

                }
                return recognitions;
    }
}
