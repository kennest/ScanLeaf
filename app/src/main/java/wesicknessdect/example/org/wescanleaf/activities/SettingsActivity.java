package wesicknessdect.example.org.wescanleaf.activities;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import androidx.core.app.NavUtils;
import com.hotmail.or_dvir.easysettings.events.BasicSettingsClickEvent;
import com.hotmail.or_dvir.easysettings.events.CheckBoxSettingsClickEvent;
import com.hotmail.or_dvir.easysettings.pojos.EasySettings;
import com.hotmail.or_dvir.easysettings.pojos.SettingsObject;
import org.greenrobot.eventbus.Subscribe;
import java.util.ArrayList;
import io.paperdb.Paper;
import wesicknessdect.example.org.wescanleaf.R;

public class SettingsActivity extends BaseActivity {
//    @BindView(R.id.toolbar)
//    Toolbar toolbar;
    ArrayList<SettingsObject> settingsList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        LinearLayout container = findViewById(R.id.settingsContainer);
        settingsList = Paper.book().read("SETTINGS",new ArrayList<>());
        EasySettings.inflateSettingsLayout(this, container, settingsList);
    }

    @Subscribe
    public void onCheckBoxSettingsClicked(CheckBoxSettingsClickEvent event)
    {
        boolean prefValue = EasySettings.retrieveSettingsSharedPrefs(this)
                .getBoolean(event.getClickedSettingsObj().getKey(),
                        event.getClickedSettingsObj().getDefaultValue());
        if(prefValue){
            StartSyncingData(getApplicationContext(),1000);
        }
        Log.e("CheckBox Prefs ->",event.getClickedSettingsObj().getValue()+"");
    }

    @Subscribe
    public void onBasicSettingClicked(BasicSettingsClickEvent event){
        if(event.getClickedSettingsObj().getKey().equals("sync_now")){
            Log.e("Basic Prefs ->",event.getClickedSettingsObj().getValue()+"");
            StartSyncingData(getApplicationContext(),0);
        }
    }

    @Override
    public boolean onNavigateUp() {
        Intent intent = NavUtils.getParentActivityIntent(this);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        NavUtils.navigateUpTo(this, intent);
        return true;
    }



}
