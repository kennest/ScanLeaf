package wesicknessdect.example.org.wesicknessdetect.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import com.appizona.yehiahd.fastsave.FastSave;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Constants {
    public static final String base_url="http://178.33.130.202:8000";
    public static final HashMap<String,String> api_error_msg=new HashMap<String,String>() {{
        put("avatar", "{\"profil\":{\"avatar\":[\"Upload a valid image. The file you uploaded was either not an image or a corrupted image.\"]}}");
        put("mobile", "{\"profil\":{\"mobile\":[\"Ce numero n'est pas valide. Veuillez le saisir comme suit +33399999999\"]}}");
        put("username", "{\"username\":[\"A user with that username already exists.\"]}");
        put("error_login","{\"non_field_errors\":[\"Incorrect credentials please try again\"]}");
    }};


    //FastSave Key
    public static final String DOWNLOAD_IDS="downloadIDS";
    public static final String CULTURE_PARTS="cultures_parts";


    //Check if internet is available
    public static boolean isOnline(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        Log.e("NETWORK INFO", String.valueOf(Objects.requireNonNull(cm).getActiveNetworkInfo() != null));

        return cm.getActiveNetworkInfo() != null;
    }

    public static List<Integer> RECTCOLORS = new ArrayList<Integer>() {
        {
            add(Color.argb(255, 25, 25, 255));
            add(Color.argb(255, 179, 9, 54));
            add(Color.argb(255, 191, 84, 2));
            add(Color.argb(255, 255, 191, 0));
            add(Color.argb(255, 105, 210, 0));
        }
    };

    @SuppressLint("MissingPermission")
    public static void getLocation(Context context){
        LocationManager lm= (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(location!=null) {
                    Log.d("Location", location.toString());
                    FastSave.getInstance().saveString("location", location.getLatitude() + ":" + location.getLongitude());
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });
    }


}