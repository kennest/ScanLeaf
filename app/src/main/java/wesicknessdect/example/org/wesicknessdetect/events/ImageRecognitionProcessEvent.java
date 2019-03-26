package wesicknessdect.example.org.wesicknessdetect.events;

public class ImageRecognitionProcessEvent {
    public final long part_id;
    public final boolean finished;

    public ImageRecognitionProcessEvent(long part_id, boolean finished) {
        this.part_id = part_id;
        this.finished = finished;
    }
}
