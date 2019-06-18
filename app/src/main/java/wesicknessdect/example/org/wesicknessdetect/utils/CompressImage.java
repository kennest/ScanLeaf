package wesicknessdect.example.org.wesicknessdetect.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import java.io.File;
import java.io.IOException;
import id.zelory.compressor.Compressor;


public class CompressImage {
    Activity activity;
    File actualFile = null;

    public CompressImage(Activity activity) {
        this.activity = activity;
    }

    @SuppressLint("CheckResult")
    public File CompressImgFile(File imgfile) {
        actualFile = imgfile;
        try {
            actualFile = new Compressor(activity).compressToFile(imgfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return actualFile;
    }
}
