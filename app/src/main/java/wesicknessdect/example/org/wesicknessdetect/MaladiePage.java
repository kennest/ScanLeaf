package wesicknessdect.example.org.wesicknessdetect;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;


public class MaladiePage extends AppCompatActivity {

    WebView wv;
    String temp_post_id = getIntent().getExtras().getString("page");
    public MaladiePage(String url){

        wv.findViewById(R.id.pageweb);
        wv.loadUrl(url);
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maladie_page);
        new MaladiePage(temp_post_id);
    }
}
