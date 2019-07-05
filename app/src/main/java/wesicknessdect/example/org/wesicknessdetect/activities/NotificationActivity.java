package wesicknessdect.example.org.wesicknessdetect.activities;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.widget.Toolbar;

import com.appizona.yehiahd.fastsave.FastSave;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.models.Alert;
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

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        toolbar.setTitle(post.getDiseaseName());
        toolbar.setSubtitle("Détecté dans la zone de "+ post.getCity() + ".");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().isZoomControlsEnabled();
        mMap.setMaxZoomPreference(10.0f);
        //mMap.setMinZoomPreference(10.0f);

        // Get my current location from prefs
        String[] me_str= FastSave.getInstance().getString("location","0,0:0,0").split(":");


        List<LatLng> alerts_markers=new ArrayList<>();
        List<Alert> Alerts= DB.alertDao().getAllAlert();
        if (Alerts.size()!=0) {
            for (Alert alert : Alerts) {
                if (alert.getId() == post.getId()) {
                    LatLng ll = new LatLng(alert.getLatitude(), alert.getLongitude());
                    alerts_markers.add(ll);
                }
            }
        }

        LatLng me = new LatLng(Double.parseDouble(me_str[0]), Double.parseDouble(me_str[1]));

            if (alerts_markers.size() != 0) {
                int i=0;
                for (LatLng latLng : alerts_markers) {
                    i++;
                    mMap.addMarker(new MarkerOptions().position(latLng).title("détection "+i));
                }
                i=0;
                addCircleToMap(me);
            }


        mMap.setOnCircleClickListener(new GoogleMap.OnCircleClickListener() {

            @Override
            public void onCircleClick(Circle circle) {
                // Flip the r, g and b components of the circle's
                // stroke color.
                int strokeColor = circle.getStrokeColor() ^ 0x00ffffff;
                circle.setStrokeColor(strokeColor);
            }
        });



        //LatLng post_loc = new LatLng(Double.parseDouble(me_str[0])-1, Double.parseDouble(me_str[1])-1);
        //LatLng SYDNEY = new LatLng(-33.88,151.21);

        //Log.d("Post Data ->",me.toString()+"//"+post_loc.toString());

        //mMap.addMarker(new MarkerOptions().position(post_loc).title(post.getDiseaseName()));
        mMap.addMarker(new MarkerOptions().position(me).title("Je suis Ici!"));
        //mMap.addMarker(new MarkerOptions().position(SYDNEY).title("SYDNEY"));
       // mMap.moveCamera(CameraUpdateFactory.newLatLng(me));
        //LatLng bounds=new LatLng((me.latitude+post_loc.latitude)/2,(me.longitude+post_loc.longitude)/2);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(me,9f));


    }

    private void addCircleToMap(LatLng lalo) {

        // circle settings
        int radiusM =15000; // your radius in meters
//        double latitude = // your center latitude
//        double longitude = // your center longitude
                LatLng latLng = lalo;

        // draw circle
        int d = 500; // diameter
        Bitmap bm = Bitmap.createBitmap(d, d, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint();
        p.setColor(getResources().getColor(R.color.danger));
        c.drawCircle(d/2, d/2, d/2, p);


        // generate BitmapDescriptor from circle Bitmap
        BitmapDescriptor bmD = BitmapDescriptorFactory.fromBitmap(bm);


// mapView is the GoogleMap
        mMap.addGroundOverlay(new GroundOverlayOptions().
                image(bmD).
                position(latLng,radiusM*2,radiusM*2).
                transparency(0.4f)
        );
    }
}
