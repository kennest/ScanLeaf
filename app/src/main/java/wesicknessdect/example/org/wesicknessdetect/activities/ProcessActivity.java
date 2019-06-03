package wesicknessdect.example.org.wesicknessdetect.activities;
import android.graphics.Canvas;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.hotmail.or_dvir.easysettings.pojos.EasySettings;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import butterknife.ButterKnife;
import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.adapters.MainAdapter;
import wesicknessdect.example.org.wesicknessdetect.adapters.PageObject;
import wesicknessdect.example.org.wesicknessdetect.fragments.AnalyseFragment;
import wesicknessdect.example.org.wesicknessdetect.fragments.CameraFragment;
import wesicknessdect.example.org.wesicknessdetect.fragments.ChatsFragment;
import wesicknessdect.example.org.wesicknessdetect.fragments.MaladiesFragment;
import wesicknessdect.example.org.wesicknessdetect.models.Profile;
import wesicknessdect.example.org.wesicknessdetect.utils.OfflineService;

/**
 * Created by Jordan Adopo on 10/02/2019.
 */

public class ProcessActivity extends BaseActivity {

    static ViewPager viewPager;
    TabLayout tabLayout;
    Toolbar toolbar;
    AppBarLayout appBarLayout;
//    FloatingActionButton actionButton;

    //Fragment Objects
    CameraFragment cameraFragment;
    ChatsFragment chatsFragment;
    AnalyseFragment analyseFragment;
    MaladiesFragment maladiesFragment;
    static MainAdapter mainAdapter;
    List<PageObject> pageObjects=new ArrayList<>();
    int page=0;

    boolean flag,sync = false;
    private Menu menu;


    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process);
        ButterKnife.bind(this);

        page=getIntent().getIntExtra("page",0);

        Intent offline = new Intent(this, OfflineService.class);
        stopService(offline);
        startService(offline);

        //Try to sync the data
        sync= EasySettings.retrieveSettingsSharedPrefs(getApplicationContext()).getBoolean("sync_after",false);
        if(sync){
            StartSyncingData(getApplication(),0);
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("ScanLeaf");
        //getSupportActionBar().setLogo(getResources().getDrawable(R.drawable.ic_launcher));


        getWindow().setNavigationBarColor(getResources().getColor(android.R.color.black));

        viewPager = findViewById(R.id.mainViewPager);
        tabLayout = findViewById(R.id.tab_layout);
        appBarLayout = findViewById(R.id.app_bar);

        PageObject camera=new PageObject("Camera",R.layout.activity_choix_culture);
        PageObject alertes=new PageObject("Alertes",R.layout.fragment_chat);
        PageObject historique=new PageObject("Historique",R.layout.fragment_analysis);
        PageObject maladies=new PageObject("Maladies",R.layout.fragment_disease);

        pageObjects.add(camera);
        pageObjects.add(historique);
        pageObjects.add(maladies);
        pageObjects.add(alertes);

        mainAdapter = new MainAdapter(this,pageObjects);

        viewPager.setAdapter(mainAdapter);
        if(page==0) {
            viewPager.setCurrentItem(0, true);
        }else{
            viewPager.setCurrentItem(1, true);
        }
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //Code to implement AppBar transition acc. to Viewpager
                /*Log.d("Position", String.valueOf(position));
                Log.d("Offset", String.valueOf(positionOffset));
                Log.d("Pixels", String.valueOf(positionOffsetPixels));
                if(position == 0)
                    appBarLayout.setTranslationY((-positionOffsetPixels/4) - 19.5f);*/
            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                   //mainAdapter.notifyDataSetChanged();
                } else if (flag) {
                    translateDown();

                }

//                if (position == 1) {
//                    toggleView.setVisibility(View.VISIBLE);
//                } else {
//                    toggleView.setVisibility(View.GONE);
//                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout.setupWithViewPager(viewPager);
        setupTabLayout();
    }

    private void setupTabLayout() {
        LinearLayout layout = ((LinearLayout) ((LinearLayout) tabLayout.getChildAt(0)).getChildAt(0));
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) layout.getLayoutParams();
        layoutParams.weight = 0.4f;
        layout.setLayoutParams(layoutParams);
        tabLayout.getTabAt(0).setIcon(getResources().getDrawable(R.drawable.ic_camera));
    }

    private void translateUp() {
        Animation up = new TranslateAnimation(0, -200, 0, -280);
        appBarLayout.setBackgroundColor(getResources().getColor(R.color.black));
        appBarLayout.setAnimation(up);
        Animation down = new TranslateAnimation(0, 200, 0, 280);
        tabLayout.setAnimation(down);
//        appBarLayout.setBackgroundColor(getResources().getColor(R.color.black));
        down.setDuration(1000);
        down.setFillAfter(true);
        down.start();
        up.setDuration(1000);
        up.setFillAfter(true);
        up.start();
        flag = true;
    }

    private void translateDown() {
        Animation up = new TranslateAnimation(-200, 0, 280, 0);
        tabLayout.setAnimation(up);
        Animation down = new TranslateAnimation(200, 0, -280, 0);
        appBarLayout.setAnimation(down);
        down.setDuration(1000);
        down.setFillAfter(true);
        down.start();
        up.setDuration(1000);
        up.setFillAfter(true);
        up.start();
        flag = false;
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                List<Profile> profiles=DB.profileDao().getAllSync();
                if(profiles.size()>0) {
                    if (profiles.get(0).getAvatar() != null) {
                        Log.d("userPicture", profiles.get(0).getAvatar());
                        File f = new File(profiles.get(0).getAvatar());
                        if (f.exists()) {
                            Bitmap bitmap = BitmapFactory.decodeFile(profiles.get(0).getAvatar());
                            bitmap = getRoundBitmap(bitmap);
                            menu.getItem(1).setIcon(new BitmapDrawable(getResources(), bitmap));
                        }
                    }
                }
            }
        });
        return true;
    }


    //Make user avatar round
    public Bitmap getRoundBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_search:

                break;

            case R.id.menu_profil:
                Intent mP = new Intent(this, ProfileActivity.class);
                startActivity(mP);
                break;

//            case R.id.menu_communaute:
//                Intent mC = new Intent(this, CommunityActivity.class);
//                startActivity(mC);
//                break;

            case R.id.menu_settings:
                Intent settings = new Intent(this, SettingsActivity.class);
                startActivity(settings);
                break;

            case R.id.menu_quit:
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setTitle("Attention!!");
                builder.setMessage("Voulez-vous quitter l'application?");
                builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                        finishAndRemoveTask();
                        System.exit(0);
                    }
                });
                builder.setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog d=builder.create();
                d.show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       // Log.e("Req code", requestCode + "");
    }



}
