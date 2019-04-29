package wesicknessdect.example.org.wesicknessdetect.activities;

import androidx.appcompat.app.AppCompatActivity;
import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.activities.BaseActivity;
import wesicknessdect.example.org.wesicknessdetect.utils.Constants;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;


public class DiseaseActivity extends BaseActivity {

    WebView wv;
    String temp_post_id ="";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maladie_page);
        //Log.v(" DiseaseActivity ", " onCreate ");
        Bundle b = null;
        b= getIntent().getExtras();
        if(Constants.isOnline(getApplicationContext())){
            if (b != null && b.containsKey("page")){
                temp_post_id = b.getString("page");
                show(temp_post_id);
            }
            else {
                Toast.makeText(getApplicationContext(), "Impossible de charger l'url", Toast.LENGTH_LONG).show();
                //Log.v(" DiseaseActivity ", " onCreate vide ");
            }
        }else{
            Toast.makeText(getApplicationContext(), "pas de connexion", Toast.LENGTH_LONG).show();
            finish();
        }

    }

    @SuppressLint("SetJavaScriptEnabled")
    public void show(String url){
        wv  = (WebView) findViewById(R.id.pageweb);
        wv.loadUrl(url);
        Log.v("url webview ", url);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.setWebViewClient(new WebViewClient());
    }
}
