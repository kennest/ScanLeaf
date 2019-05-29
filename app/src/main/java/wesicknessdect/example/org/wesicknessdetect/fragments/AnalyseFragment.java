package wesicknessdect.example.org.wesicknessdetect.fragments;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.CalendarView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.activities.ProcessActivity;
import wesicknessdect.example.org.wesicknessdetect.activities.QuizActivity;
import wesicknessdect.example.org.wesicknessdetect.adapters.AnalysisAdapter;
import wesicknessdect.example.org.wesicknessdetect.database.AppDatabase;
import wesicknessdect.example.org.wesicknessdetect.events.ToggleViewEvent;
import wesicknessdect.example.org.wesicknessdetect.listener.EndlessRecyclerViewScrollListener;
import wesicknessdect.example.org.wesicknessdetect.models.Diagnostic;
import wesicknessdect.example.org.wesicknessdetect.models.DiagnosticPictures;
import wesicknessdect.example.org.wesicknessdetect.models.Picture;
import wesicknessdect.example.org.wesicknessdetect.models.Post;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Yugansh Tyagi on 3/21/2018.
 */

public class AnalyseFragment extends Fragment {

    @BindView(R.id.status_rv)
    RecyclerView recyclerView;

    @BindView(R.id.empty_data)
    View empty;


    public boolean is_calendar_view_shown = false;

    private static AppDatabase DB;

    AnalysisAdapter analysisAdapter;

    private EndlessRecyclerViewScrollListener scrollListener;
    List<Diagnostic> diagnostics = new ArrayList<>();
    List<Diagnostic> tmp = new ArrayList<>();
    //LayoutAnimationController controller;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_analysis, null, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getLayoutInflater().inflate(R.layout.fragment_analysis, null, false);
        ButterKnife.bind(this, view);
        DB = AppDatabase.getInstance(getContext());
        AsyncTask.SERIAL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                diagnostics = DB.diagnosticDao().getAllSync();
                for (Diagnostic d : diagnostics) {
                    List<Picture> pictures = DB.pictureDao().getByDiagnosticIdSync(d.getX());
                    Log.e("Analysis Frag pic",pictures.size()+"");
                    d.setPictures(pictures);
                    tmp.add(d);
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        InitView();
                    }
                });
            }
        });
        //controller= AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_animation_fall_down);
    }

    private void InitView() {

        if (tmp.size() > 0) {

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

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
            analysisAdapter = new AnalysisAdapter(getActivity(), tmp);
            recyclerView.setAdapter(analysisAdapter);
            //recyclerView.setLayoutAnimation(controller);
            recyclerView.scheduleLayoutAnimation();
            //recyclerView.addItemDecoration(decoration);
        } else {
            empty.setVisibility(View.VISIBLE);
        }
    }

//Hide the loading dialog
}
