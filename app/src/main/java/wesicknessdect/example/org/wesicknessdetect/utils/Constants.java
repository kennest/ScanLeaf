package wesicknessdect.example.org.wesicknessdetect.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.HashMap;
import java.util.Objects;

public class Constants {
    public static final String base_url="http://";
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


}