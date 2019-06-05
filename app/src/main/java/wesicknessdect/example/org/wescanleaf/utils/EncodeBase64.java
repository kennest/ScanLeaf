package wesicknessdect.example.org.wescanleaf.utils;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class EncodeBase64 {

    @SuppressLint("StaticFieldLeak")
    public String encode(String filepath) {
                FileInputStream fileInputStream;
                String val = "";
                try {
                    File f = new File(filepath);
                    byte[] bytesArray = new byte[(int) f.length()];
                    fileInputStream = new FileInputStream(f);
                    fileInputStream.read(bytesArray);
                    ByteArrayOutputStream bao = new ByteArrayOutputStream();
                    val = Base64.encodeToString(bytesArray, Base64.NO_WRAP);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return val;
    }

}

