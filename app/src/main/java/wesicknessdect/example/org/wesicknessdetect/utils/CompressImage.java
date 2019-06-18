package wesicknessdect.example.org.wesicknessdetect.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import java.io.File;
import java.io.IOException;
import id.zelory.compressor.Compressor;


public class CompressImage {
    Context context;
    File actualFile = null;

    public CompressImage(Context ctx) {
        this.context = ctx;
    }

    @SuppressLint("CheckResult")
    public File CompressImgFile(File imgfile) {
        actualFile = imgfile;
        try {
            actualFile = new Compressor(context).compressToFile(imgfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return actualFile;
    }
}
