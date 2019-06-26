package wesicknessdect.example.org.wesicknessdetect.activities;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.appizona.yehiahd.fastsave.FastSave;
import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.events.ShowProcessScreenEvent;
import wesicknessdect.example.org.wesicknessdetect.tasks.RemoteTasks;


public class RestoreDataActivity extends BaseActivity {
    @BindView(R.id.txtSize)
    TextView sizeTxt;

    @BindView(R.id.btnRestore)
    Button btnRestore;

    @BindView(R.id.btnPass)
    Button btnPass;

    @BindView(R.id.imgPkg)
    ImageView pkgImg;

    @Override
    public void onStart() {
        super.onStart();
        RemoteTasks.getInstance(getApplicationContext()).getDataSize();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restore_data_layout);
        ButterKnife.bind(this);
        Glide.with(getApplicationContext())
                .asBitmap()
                .load(Uri.parse("file:///android_asset/package.png"))
                .into(pkgImg);
        String size= FastSave.getInstance().getString("size","0.0 Mo");
        sizeTxt.setText(size);
    }

    @OnClick(R.id.btnPass)
    void goToProcessActivity(){
        EventBus.getDefault().post(new ShowProcessScreenEvent("From Restore"));
    }

    @OnClick(R.id.btnRestore)
    void doRestoreData(){

    }
}
