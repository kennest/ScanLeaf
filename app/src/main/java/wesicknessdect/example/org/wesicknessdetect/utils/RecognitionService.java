package wesicknessdect.example.org.wesicknessdetect.utils;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import wesicknessdect.example.org.wesicknessdetect.activities.tensorflow.Classifier;
import wesicknessdect.example.org.wesicknessdetect.activities.tensorflow.TensorFlowObjectDetectionAPIModel;
import wesicknessdect.example.org.wesicknessdetect.activities.tensorflow.env.Logger;
import wesicknessdect.example.org.wesicknessdetect.events.ImageRecognitionProcessEvent;

public class RecognitionService extends IntentService {
    private static final String BITMAP = "bitmap";
    private static final String MODEL = "model";
    private static final String LABEL = "label";
    private static final String PART_ID = "part_id";
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

    public RecognitionService() {
        super("DownloadSongService");
    }

    public static Intent getRecognitionervice(final @NonNull Context callingClassContext,final Bitmap bitmap,final String model,final String label, long part_id) {
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream);
        byte[] byteArray = bStream.toByteArray();
        return new Intent(callingClassContext, RecognitionService.class)
                .putExtra(BITMAP, byteArray)
                .putExtra(MODEL, model)
                .putExtra(LABEL, label)
                .putExtra(PART_ID, part_id);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        byte[] byteArray = intent.getByteArrayExtra(BITMAP);
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        int part_id = intent.getIntExtra(PART_ID,0);
        String model=intent.getStringExtra(MODEL);
        String label=intent.getStringExtra(LABEL);
        recognizedSymptoms(bitmap,model,label,part_id);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        return super.onStartCommand(intent, flags, startId);
    }

    public List<Classifier.Recognition> recognizedSymptoms( Bitmap bitmap, String model, String label, long part_id){
        EventBus.getDefault().post(new ImageRecognitionProcessEvent(part_id,false, new ArrayList<>()));
        List<Classifier.Recognition> recognitions=new ArrayList<>();
        if (MODE == DetectorMode.TF_OD_API) {
            try {
                detector = TensorFlowObjectDetectionAPIModel.create(
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
