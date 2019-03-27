package wesicknessdect.example.org.wesicknessdetect.futuretasks;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

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
import wesicknessdect.example.org.wesicknessdetect.activities.tensorflow.Classifier;
import wesicknessdect.example.org.wesicknessdetect.activities.tensorflow.TensorFlowObjectDetectionAPIModel;
import wesicknessdect.example.org.wesicknessdetect.activities.tensorflow.env.Logger;
import wesicknessdect.example.org.wesicknessdetect.events.ImageRecognitionProcessEvent;


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


    //Recognized Symptoms on given bitmap
    public List<Classifier.Recognition> recognizedSymptoms(AssetManager assetManager, Bitmap bitmap, String model,String label,long part_id){
        EventBus.getDefault().post(new ImageRecognitionProcessEvent(part_id,false, new ArrayList<>()));
        List<Classifier.Recognition> recognitions=new ArrayList<>();
                if (MODE == DetectorMode.TF_OD_API) {
                    try {
                        detector = TensorFlowObjectDetectionAPIModel.create(
                                assetManager,
                                model,
                                label,
                                    TF_OD_API_INPUT_SIZE);

                        cropSize = TF_OD_API_INPUT_SIZE;

                        LOGGER.e("Model loaded infos",model+"//"+label);

                        recognitions = detector.recognizeImage(bitmap);
                        Log.e("Recognitions", recognitions.toString());
                        EventBus.getDefault().post(new ImageRecognitionProcessEvent(part_id,true, recognitions));

                    } catch (final IOException e) {
                        EventBus.getDefault().post(new ImageRecognitionProcessEvent(part_id,true, recognitions));
                        LOGGER.e("Exception initializing classifier!", e);
                    }

                }
                return recognitions;
    }
}
