package wesicknessdect.example.org.wesicknessdetect.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.widget.Toolbar;

import com.appizona.yehiahd.fastsave.FastSave;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.models.Post;

public class NotificationActivity extends BaseActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    Post post=new Post();
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        ButterKnife.bind(this);
        Bundle bundleExtra= getIntent().getBundleExtra("bundle");
        String postData=bundleExtra.getString("post");
        Gson gson=new Gson();
        post=gson.fromJson(postData,Post.class);

        Log.d("Notification Data ->",postData);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        toolbar.setTitle(post.getDiseaseName());
        toolbar.setSubtitle("detectée à "+post.getDistance()+"km de vous:");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.
        String[] me_str= FastSave.getInstance().getString("location","0,0:0,0").split(":");

        LatLng sydney = new LatLng(-34, 151);
        LatLng me = new LatLng(Double.parseDouble(me_str[0]), Double.parseDouble(me_str[1]));

        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.addMarker(new MarkerOptions().position(me).title("Je suis Ici!"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(me));

    }
}
