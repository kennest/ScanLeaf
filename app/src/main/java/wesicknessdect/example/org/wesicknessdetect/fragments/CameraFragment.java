package wesicknessdect.example.org.wesicknessdetect.fragments;

import android.os.Bundle;
import android.util.Log;

import com.fxn.pix.Options;
import com.fxn.pix.Pix;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

/**
 * Created by Jordan Adopo on 10/02/2019.
 */


public class CameraFragment extends Fragment {


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("Camera","Initialized");
        Pix.start((FragmentActivity) getContext(), Options.init().setRequestCode(200).setCount(30).setFrontfacing(true));
    }


}
