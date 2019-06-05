package wesicknessdect.example.org.wesicknessdetect.mvp.view;

public interface IDownloadView {
    void getDownloadBytes(long downloaded,long filesize,int part_id);
}
