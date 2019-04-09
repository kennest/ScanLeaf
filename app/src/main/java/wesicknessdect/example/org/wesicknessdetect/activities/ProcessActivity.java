package wesicknessdect.example.org.wesicknessdetect.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.events.ToggleViewEvent;
import wesicknessdect.example.org.wesicknessdetect.fragments.AnalyseFragment;
import wesicknessdect.example.org.wesicknessdetect.fragments.CameraFragment;
import wesicknessdect.example.org.wesicknessdetect.fragments.ChatsFragment;
import wesicknessdect.example.org.wesicknessdetect.fragments.MaladiesFragment;
import wesicknessdect.example.org.wesicknessdetect.futuretasks.RemoteTasks;

/**
 * Created by Jordan Adopo on 10/02/2019.
 */

public class ProcessActivity extends BaseActivity {

    static ViewPager viewPager;
    TabLayout tabLayout;
    Toolbar toolbar;
    AppBarLayout appBarLayout;
    FloatingActionButton actionButton;
    Activity myActivity = this;


    //Fragment Objects
    CameraFragment cameraFragment;
    ChatsFragment chatsFragment;
    AnalyseFragment analyseFragment;
    MaladiesFragment maladiesFragment;
    static MainAdapter mainAdapter;

    @BindView(R.id.toggleView)
    FloatingActionButton toggleView;

    boolean flag = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process);
        ButterKnife.bind(this);

        //Init Necessary Data
        try {
            RemoteTasks.getInstance(this).getDiagnostics();
        } catch (IOException e) {
            e.printStackTrace();
        }


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));

        getWindow().setNavigationBarColor(getResources().getColor(android.R.color.black));

        viewPager = findViewById(R.id.mainViewPager);
        tabLayout = findViewById(R.id.tab_layout);
        appBarLayout = findViewById(R.id.app_bar);
        actionButton = findViewById(R.id.fab);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0, true);
            }


        });

        mainAdapter = new MainAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mainAdapter);
        viewPager.setCurrentItem(0, true);
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
                if (position == 0) {
//                    translateUp();
//                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
//                    actionButton.setVisibility(View.GONE);
                } else if (flag) {
                    translateDown();
                    actionButton.setVisibility(View.VISIBLE);
                }
                if (position == 1) {
                    toggleView.setVisibility(View.VISIBLE);
                } else {
                    toggleView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout.setupWithViewPager(viewPager);
        setupTabLayout();

        toggleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new ToggleViewEvent(true));
            }
        });
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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
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

            case R.id.menu_communaute:
                Intent mC = new Intent(this, CommunityActivity.class);
                startActivity(mC);
                break;

            case R.id.menu_settings:
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("Req code", requestCode + "");
    }

    private class MainAdapter extends FragmentStatePagerAdapter {

        public MainAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                if (cameraFragment == null) {
                    cameraFragment = new CameraFragment();
                    return cameraFragment;
                }
                return cameraFragment;
            } else if (position == 1) {

                if (analyseFragment == null) {
                    analyseFragment = new AnalyseFragment();
                    return analyseFragment;
                }
                return analyseFragment;
            } else if (position == 2) {
                if (maladiesFragment == null) {
                    maladiesFragment = new MaladiesFragment();
                    return maladiesFragment;
                }
                return maladiesFragment;
            } else if (position == 3) {
                if (chatsFragment == null) {
                    chatsFragment = new ChatsFragment();
                    return chatsFragment;
                }
                return chatsFragment;
            }
            return null;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0)
                return "";
            if (position == 1)
                return "Historique";
            if (position == 2)
                return "Maladies";
            if (position == 3)
                return "Communites";
            return null;
        }

        @Override
        public int getCount() {
            return 4;
        }
    }

}
