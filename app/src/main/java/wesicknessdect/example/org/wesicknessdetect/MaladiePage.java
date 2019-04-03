package wesicknessdect.example.org.wesicknessdetect;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;


public class MaladiePage extends AppCompatActivity {

    WebView wv;
    String temp_post_id ="";
    public void show(String url){

        wv  = (WebView) findViewById(R.id.pageweb);
        wv.loadUrl(url);
        Log.v("url webview ", url);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.setWebViewClient(new WebViewClient());
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maladie_page);
        Log.v(" MaladiePage ", " onCreate ");
        Bundle b = null;
        b= getIntent().getExtras();
        if (b != null && b.containsKey("page")){
            temp_post_id = b.getString("page");
            show(temp_post_id);
        }
        else {
            Toast.makeText(getApplicationContext(), "Impossible de charger l'url", Toast.LENGTH_LONG).show();
            Log.v(" MaladiePage ", " onCreate vide ");
        }
    }
}
