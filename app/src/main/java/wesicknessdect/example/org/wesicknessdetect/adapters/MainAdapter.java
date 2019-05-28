package wesicknessdect.example.org.wesicknessdetect.adapters;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.database.AppDatabase;
import wesicknessdect.example.org.wesicknessdetect.listener.EndlessRecyclerViewScrollListener;
import wesicknessdect.example.org.wesicknessdetect.models.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static wesicknessdect.example.org.wesicknessdetect.activities.BaseActivity.DB;

public class MainAdapter extends PagerAdapter {
    private Activity mContext;
    private List<PageObject> pageObjects;
    List<Culture> cultures = new ArrayList<>();
    List<Diagnostic> diagnostics = new ArrayList<>();
    List<Disease> diseases = new ArrayList<>();
    List<Post> posts = new ArrayList<>();
    List<Diagnostic> tmp = new ArrayList<>();
    private static AppDatabase DB;
    private EndlessRecyclerViewScrollListener scrollListener;

    public MainAdapter(Activity mContext, List<PageObject> pageObjects) {
        this.mContext = mContext;
        this.pageObjects = pageObjects;
        DB = AppDatabase.getInstance(mContext);
    }

    @Override
    public Object instantiateItem(@NonNull ViewGroup collection, int position) {
        PageObject pageObject = pageObjects.get(position);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(pageObject.getmLayoutResId(), collection, false);
        if (pageObject.getmTitleResId().equals("Camera")) {
            layout = (ViewGroup) InitCameraView(layout);
        } else if (pageObject.getmTitleResId().equals("Historique")) {
            layout = (ViewGroup) InitHistoryView(layout);
        } else if (pageObject.getmTitleResId().equals("Maladies")) {
            layout = (ViewGroup) InitMaladieView(layout);
        } else {
            layout = (ViewGroup) InitAlerteView(layout);
        }
        collection.addView(layout);
        return layout;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (pageObjects.get(position).getmTitleResId().equals("Camera")) {
            return "";
        } else {
            return pageObjects.get(position).getmTitleResId();
        }
    }

    @Override
    public int getCount() {
        return pageObjects.size();
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }


    private View InitCameraView(View v) {
        RecyclerView culture_lv = v.findViewById(R.id.culture_lv);
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(mContext, R.anim.layout_animation_fall_down);
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cultures = DB.cultureDao().getAllSync();
                CultureAdapter cultureAdapter = new CultureAdapter(cultures, mContext);
                culture_lv.setLayoutManager(new GridLayoutManager(mContext, 1));
                culture_lv.setAdapter(cultureAdapter);
                culture_lv.setLayoutAnimation(controller);
                culture_lv.scheduleLayoutAnimation();
            }
        });
        return v;
    }

    private View InitHistoryView(View v) {
        RecyclerView recyclerView = v.findViewById(R.id.status_rv);
        View empty = v.findViewById(R.id.empty_data);

        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                diagnostics = DB.diagnosticDao().getAllSync();
                Log.e("Analysis Frag diag", diagnostics.size() + "");
                for (Diagnostic d : diagnostics) {
                    List<Picture> pictures = DB.pictureDao().getByDiagnosticIdSync(d.getX());
                    Log.e("Analysis Frag pic", pictures.size() + "");
                    d.setPictures(pictures);
                    tmp.add(d);
                }

                if (tmp.size() > 0) {
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
                    scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
                        @Override
                        public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                            Log.e("ListView Scroll", "Started...");
                            // Triggered only when new data needs to be appended to the list
                            // Add whatever code is needed to append new items to the bottom of the list
                            //loadNextDataFromApi(page);
                        }
                    };
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.addOnScrollListener(scrollListener);
                    Collections.reverse(tmp);
                    AnalysisAdapter analysisAdapter = new AnalysisAdapter(mContext, tmp);
                    recyclerView.setAdapter(analysisAdapter);
                    //recyclerView.setLayoutAnimation(controller);
                    recyclerView.scheduleLayoutAnimation();
                    //recyclerView.addItemDecoration(decoration);
                } else {
                    empty.setVisibility(View.VISIBLE);
                }
            }
        });
        return v;
    }

    private View InitMaladieView(View v) {
        RecyclerView recyclerView = v.findViewById(R.id.maladie_rv);
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(mContext, R.anim.layout_animation_fall_down);
        AsyncTask.SERIAL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                diseases = DB.diseaseDao().getAllSync();
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                        recyclerView.setAdapter(new DiseaseAdapter(mContext, diseases));
                        recyclerView.setLayoutAnimation(controller);
                        recyclerView.scheduleLayoutAnimation();
                    }
                });
            }
        });
        return v;
    }

    private View InitAlerteView(View v) {
        RecyclerView recyclerView = v.findViewById(R.id.chat_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        AsyncTask.SERIAL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                posts = DB.postDao().getAllPost();
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setAdapter(new ChatAdapter(mContext, posts));
                    }
                });
            }
        });
        return v;
    }
}
