package wesicknessdect.example.org.wesicknessdetect.events;

import java.util.List;

import wesicknessdect.example.org.wesicknessdetect.activities.tensorflow.Classifier;

public class ImageRecognitionProcessEvent {
    public final long part_id;
    public final boolean finished;
    public final List<Classifier.Recognition> recognitions;

    public ImageRecognitionProcessEvent(long part_id, boolean finished, List<Classifier.Recognition> recognitions) {
        this.part_id = part_id;
        this.finished = finished;
        this.recognitions = recognitions;
    }
}
