package wesicknessdect.example.org.wesicknessdetect.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.google.android.material.snackbar.Snackbar;

import io.reactivex.Completable;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.activities.BaseActivity;
import wesicknessdect.example.org.wesicknessdetect.database.AppDatabase;
import wesicknessdect.example.org.wesicknessdetect.listener.EndlessRecyclerViewScrollListener;
import wesicknessdect.example.org.wesicknessdetect.models.*;
import wesicknessdect.example.org.wesicknessdetect.utils.Constants;

import java.io.IOException;
import java.util.*;

public class MainAdapter extends PagerAdapter {
    private Activity mContext;
    private List<PageObject> pageObjects;
    List<Culture> cultures = new ArrayList<>();
    List<Disease> diseases = new ArrayList<>();
    List<Post> posts = new ArrayList<>();
    List<Diagnostic> tmp = new ArrayList<>();
    private static AppDatabase DB;
    private EndlessRecyclerViewScrollListener scrollListener;
    AnalysisAdapter analysisAdapter;
    Runnable InitData = null;

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
        View loading = v.findViewById(R.id.loading_data);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.e("ListView Scroll", "Started ->" + totalItemsCount + "//" + tmp.get(0).getRemote_id());
                if (Constants.isOnline(mContext)) {
                    Snackbar snackbar = Snackbar.make(mContext.getWindow().getDecorView(), "Voulez vous en charger plus?", Snackbar.LENGTH_LONG);
                    snackbar.setAction("Oui", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                analysisAdapter.loadNextDataFromApi(getMaxRemoteID(tmp).getRemote_id());
                                GetDiagnosticsFromDB();
                                mContext.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        view.addOnScrollListener(scrollListener);
                                        //Collections.reverse(tmp);
                                        analysisAdapter = new AnalysisAdapter(mContext, tmp);
                                        view.setAdapter(analysisAdapter);
                                        analysisAdapter.notifyDataSetChanged();
                                    }
                                });

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    snackbar.show();
                }
            }
        };
        Handler handler = new Handler();

        InitData = new Runnable() {
            @Override
            public void run() {
                GetDiagnosticsFromDB();
                if (tmp.size() > 0) {
                    //Toast.makeText(mContext, "Full List..." + tmp.size(), Toast.LENGTH_SHORT).show();
                    //handler.removeCallbacks(InitData, null);
                    recyclerView.addOnScrollListener(scrollListener);
                    //Collections.reverse(tmp);
                    analysisAdapter = new AnalysisAdapter(mContext, tmp);
                    recyclerView.setAdapter(analysisAdapter);
                    //analysisAdapter.notifyDataSetChanged();
                    recyclerView.setVisibility(View.VISIBLE);
                    empty.setVisibility(View.GONE);
                    loading.setVisibility(View.GONE);
                    handler.removeCallbacksAndMessages(null);
                    handler.removeCallbacks(InitData);
                    recyclerView.scheduleLayoutAnimation();
                } else {
                    Log.d("Diagnostic RV", "Is Empty");
                    //Toast.makeText(mContext, "Empty List ->" + tmp.size(), Toast.LENGTH_SHORT).show();
                    //empty.setVisibility(View.VISIBLE);
                    ImageButton reset = empty.findViewById(R.id.reload);
                    recyclerView.setVisibility(View.GONE);
                    reset.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new BaseActivity().StartSyncingData(mContext, 0);
                            empty.setVisibility(View.GONE);
                            loading.setVisibility(View.VISIBLE);
                            mContext.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    recyclerView.addOnScrollListener(scrollListener);
                                    analysisAdapter = new AnalysisAdapter(mContext, tmp);
                                    recyclerView.setAdapter(analysisAdapter);
                                    analysisAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    });
                    handler.postDelayed(InitData, 1500);
                }
                //handler.postDelayed(InitData,1000);
            }
        };
        handler.post(InitData);
        return v;
    }

    private void GetDiagnosticsFromDB() {
        DB.diagnosticDao().rxGetAll().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Diagnostic>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @SuppressLint("CheckResult")
                    @Override
                    public void onSuccess(List<Diagnostic> diagnosticList) {
                        List<Diagnostic> diagnostics = diagnosticList;
                        Collections.reverse(new ArrayList<>(diagnostics));
                        for (Diagnostic n : diagnostics) {
                            Completable.fromAction(() -> {
                                List<Picture> pictures = DB.pictureDao().getByDiagnosticUUIdSync(n.getUuid());
                                Log.e("Analysis Rx pictures DB", pictures.size() + "");
                                n.setPictures(pictures);
                            })
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(() -> Log.d("Rx Diagnostic DB", "Completed ->"+n.getRemote_id()),// completed with success,
                                            throwable -> throwable.printStackTrace()// there was an error
                                    );
                        }
                        Log.e("Analysis Frag diag", diagnostics.size() + "");
                        tmp = diagnostics;
                        Log.d("InitHistory size N->", tmp.size() + "");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("DB Error ->", e.getMessage());
                    }
                });

    }


    private Diagnostic getMaxRemoteID(List<Diagnostic> list) {
        Diagnostic maxObject = Collections.max(list, new Comparator<Diagnostic>() {
            @Override
            public int compare(Diagnostic o1, Diagnostic o2) {
                if (o1.getRemote_id() == o2.getRemote_id()) {
                    return 0;
                } else if (o1.getRemote_id() > o2.getRemote_id()) {
                    return -1;
                } else if (o1.getRemote_id() < o2.getRemote_id()) {
                    return 1;
                }
                return 0;
            }
        });
        return maxObject;
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
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(mContext, R.anim.layout_animation_fall_down);
        AsyncTask.SERIAL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                posts = DB.postDao().getAllPost();
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setAdapter(new ChatAdapter(mContext, posts));
                        recyclerView.setLayoutAnimation(controller);
                        recyclerView.scheduleLayoutAnimation();
                    }
                });
            }
        });
        return v;
    }
}
