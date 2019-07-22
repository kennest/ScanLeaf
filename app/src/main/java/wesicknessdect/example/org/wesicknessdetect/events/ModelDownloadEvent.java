package wesicknessdect.example.org.wesicknessdetect.events;

public class ModelDownloadEvent {
   public final long downloaded;
   public final long filesize;
   public final int part_id;
   public final int downloadId;

    public ModelDownloadEvent(long downloaded, long filesize, int part_id, int downloadId) {
        this.downloaded = downloaded;
        this.filesize = filesize;
        this.part_id = part_id;
        this.downloadId = downloadId;
    }
}
