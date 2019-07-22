package wesicknessdect.example.org.wesicknessdetect.events;

public class GetFileProgressEvent {
    public final long totalsize;
    public final long downloadsize;
    public final int downloadId;
    public final int partId;

    public GetFileProgressEvent(long totalsize, long downloadsize, int downloadId, int partId) {
        this.totalsize = totalsize;
        this.downloadsize = downloadsize;
        this.downloadId = downloadId;
        this.partId = partId;
    }
}
