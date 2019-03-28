package wesicknessdect.example.org.wesicknessdetect.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.fxn.pix.Pix;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.activities.tensorflow.Classifier;
import wesicknessdect.example.org.wesicknessdetect.activities.tensorflow.TensorFlowObjectDetectionAPIModel;
import wesicknessdect.example.org.wesicknessdetect.activities.tensorflow.env.Logger;
import wesicknessdect.example.org.wesicknessdetect.fragments.AnalyseFragment;
import wesicknessdect.example.org.wesicknessdetect.fragments.CameraFragment;
import wesicknessdect.example.org.wesicknessdetect.fragments.ChatsFragment;
import wesicknessdect.example.org.wesicknessdetect.fragments.MaladiesFragment;

/**
 * Created by Jordan Adopo on 10/02/2019.
 */

public class ProcessActivity extends BaseActivity {

    ViewPager viewPager;
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

    boolean flag = false;

    //TF var
    Classifier detector;
    private static final int TF_OD_API_INPUT_SIZE = 500;
    private static final String TF_OD_API_MODEL_FILE =
            "file:///android_asset/ssd_mobilenet.pb";
    private static final String TF_OD_API_LABELS_FILE = "file:///android_asset/bois_cacao.txt";
    int cropSize = TF_OD_API_INPUT_SIZE;

    private enum DetectorMode {
        TF_OD_API, MULTIBOX, YOLO;
    }

    private Bitmap croppedBitmap = null;
    private static final DetectorMode MODE = DetectorMode.TF_OD_API;
    private static final Logger LOGGER = new Logger();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));

        getWindow().setNavigationBarColor(getResources().getColor(android.R.color.black));

        viewPager = findViewById(R.id.mainViewPager);
        tabLayout = findViewById(R.id.tab_layout);
        appBarLayout = findViewById(R.id.app_bar);
        actionButton = findViewById(R.id.fab);

        viewPager.setAdapter(new MainAdapter(getSupportFragmentManager()));
        viewPager.setCurrentItem(1, true);
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
                    translateUp();
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    actionButton.setVisibility(View.GONE);
                } else if (flag) {
                    translateDown();
                    actionButton.setVisibility(View.VISIBLE);
                }
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
        if (resultCode == Activity.RESULT_OK && requestCode == 200) {
            assert data != null;
            ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            Map<Integer,String> recognition_legend=new HashMap<>();
            if (MODE == DetectorMode.TF_OD_API) {
                try {
                    detector = TensorFlowObjectDetectionAPIModel.create(base64model(), TF_OD_API_LABELS_FILE, TF_OD_API_INPUT_SIZE);

                    cropSize = TF_OD_API_INPUT_SIZE;

                    Bitmap bitmap = BitmapFactory.decodeFile(returnValue.get(0));
                    croppedBitmap = Bitmap.createBitmap(cropSize, cropSize, Bitmap.Config.ARGB_8888);
                    Bitmap bitmap_cropped = Bitmap.createScaledBitmap(bitmap, cropSize, cropSize, false);

                    //detector.recognizeImage(bitmap);

                    List<Classifier.Recognition> recognitions = detector.recognizeImage(bitmap_cropped);
                    Log.e("Recognitions", recognitions.toString());

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    // Get the layout inflater
                    LayoutInflater inflater = getLayoutInflater();

                    View v = inflater.inflate(R.layout.activity_test_recognition, null, false);
                    ImageView image = v.findViewById(R.id.image_result);

                    Canvas canvas = new Canvas(bitmap_cropped);
                    // Draw a solid color to the canvas background
                    //canvas.drawColor(Color.LTGRAY);

                    // Initialize a new Paint instance to draw the Rectangle


                    recognitions=recognitions.subList(0,4);
                    for (Classifier.Recognition r : recognitions) {
                        Paint paint = new Paint();
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setStrokeWidth(4f);
                        Random rnd = new Random();
                        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                        recognition_legend.put(color,r.getTitle());
                        paint.setColor(color);
                        paint.setAntiAlias(true);
                        canvas.drawRect(r.getLocation(), paint);
                    }


                    //Glide.with(this).load(bitmap_cropped).into(image);
                    image.setImageBitmap(bitmap_cropped);
                    // Inflate and set the layout for the dialog
                    // Pass null as the parent view because its going in the dialog layout
                    builder.setView(v)
                            // Add action buttons
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    //EventBus.getDefault().post(new ShowQuizPageEvent("quiz"));
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();


                } catch (final IOException e) {
                    LOGGER.e("Exception initializing classifier!", e);
                    Toast toast =
                            Toast.makeText(
                                    getApplicationContext(), "Classifier could not be initialized", Toast.LENGTH_SHORT);
                    toast.show();
                    finish();
                } catch (InterruptedException|ExecutionException e) {
                    e.printStackTrace();
                }

            }
        }

    }

    //Converti le model en Base64
    public String modelB64() throws InterruptedException, ExecutionException{
        ExecutorService executor = Executors.newFixedThreadPool(2);
        FutureTask<String> future =
                new FutureTask<>(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        InputStream inputStream = null;
                        String modelb64 = "";
                        try {
                            inputStream = getAssets().open("frozen_bois_graph.pb");

                            byte[] buffer = new byte[8192];
                            int bytesRead;
                            ByteArrayOutputStream output = new ByteArrayOutputStream();
                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                output.write(buffer, 0, bytesRead);
                            }
                            byte file[] = output.toByteArray();

                            modelb64 = Base64.encodeToString(file, 0);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.i("modelb64",modelb64);

                        return modelb64;
                    }
                });
        executor.execute(future);
        return future.get();
    }

    //Converti le base64 du model en fichier
    public String base64model() throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        FutureTask<String> future =
                new FutureTask<>(new Callable<String>() {
                    public String call() throws InterruptedException, ExecutionException{
                        File file = new File(getCacheDir().getPath()+"/cacao_graph.pb");
                        int size = (int) file.length();
                        byte[] bytes = Base64.decode(modelB64(),0);
                        try {
                            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                            bos.write(bytes);
                            bos.flush();
                            bos.close();
                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        Log.i("base64model",file.getAbsolutePath());
                        return file.getAbsolutePath();
                    }});
        executor.execute(future);
        return future.get();
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
            }
            if (position == 1) {

                if (analyseFragment == null) {
                    analyseFragment = new AnalyseFragment();
                    return analyseFragment;
                }
                return analyseFragment;
            }
            if (position == 2) {
                if (maladiesFragment == null) {
                    maladiesFragment = new MaladiesFragment();
                    return maladiesFragment;
                }
                return maladiesFragment;
            }
            if (position == 3) {

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
                return "Messages";
            return null;
        }

        @Override
        public int getCount() {
            return 4;
        }
    }

}
