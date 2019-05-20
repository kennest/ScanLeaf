package wesicknessdect.example.org.wesicknessdetect.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.core.app.NavUtils;

import com.hotmail.or_dvir.easysettings.events.BasicSettingsClickEvent;
import com.hotmail.or_dvir.easysettings.events.CheckBoxSettingsClickEvent;
import com.hotmail.or_dvir.easysettings.events.SeekBarSettingsValueChangedEvent;
import com.hotmail.or_dvir.easysettings.pojos.EasySettings;
import com.hotmail.or_dvir.easysettings.pojos.SettingsObject;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.paperdb.Paper;
import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.models.Diagnostic;
import wesicknessdect.example.org.wesicknessdetect.models.Picture;
import wesicknessdect.example.org.wesicknessdetect.models.SymptomRect;
import wesicknessdect.example.org.wesicknessdetect.tasks.timers.SyncTimerTask;
import wesicknessdect.example.org.wesicknessdetect.utils.SyncReceiver;

public class SettingsActivity extends BaseActivity {
//    @BindView(R.id.toolbar)
//    Toolbar toolbar;

    List<SymptomRect> symptomRects;
    List<Diagnostic> diagnostics;
    List<Picture> pictures;
    ArrayList<SettingsObject> settingsList;
    SyncReceiver receiver = new SyncReceiver ();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        LinearLayout container = findViewById(R.id.settingsContainer);
        settingsList = Paper.book().read("SETTINGS",new ArrayList<>());

        EasySettings.inflateSettingsLayout(this, container, settingsList);
//        ButterKnife.bind(this);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        //getSupportActionBar().setLogo(R.drawable.ic_settings);
//        //getSupportActionBar().setDisplayUseLogoEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        LinearLayout container = findViewById(R.id.settingsContainer);
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
