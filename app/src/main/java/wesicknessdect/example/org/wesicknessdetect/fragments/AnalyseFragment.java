package wesicknessdect.example.org.wesicknessdetect.fragments;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import wesicknessdect.example.org.wesicknessdetect.listener.EndlessRecyclerViewScrollListener;
import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.adapters.AnalysisAdapter;
import wesicknessdect.example.org.wesicknessdetect.database.AppDatabase;
import wesicknessdect.example.org.wesicknessdetect.models.Diagnostic;
import wesicknessdect.example.org.wesicknessdetect.models.Picture;

/**
 * Created by Yugansh Tyagi on 3/21/2018.
 */

public class AnalyseFragment extends Fragment {

    @BindView(R.id.status_rv)
    RecyclerView recyclerView;

    @BindView(R.id.empty_data)
    View empty;

    private GridLayoutManager layoutManager;


    public boolean is_calendar_view_shown = false;

    private static AppDatabase DB;

    private AnalysisAdapter analysisAdapter;

    private Context context=getContext();

    private EndlessRecyclerViewScrollListener scrollListener;
    List<Diagnostic> diagnostics = new ArrayList<>();
    List<Diagnostic> tmp = new ArrayList<>();
    //LayoutAnimationController controller;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_analysis, null, false);
        ButterKnife.bind(this, view);
        DB = AppDatabase.getInstance(getContext());
        diagnostics = DB.diagnosticDao().getAllSync();
        for (Diagnostic d : diagnostics) {
            List<Picture> pictures = DB.pictureDao().getByDiagnosticIdSync(d.getX());
            Log.e("Analysis Frag pic",pictures.size()+"");
            d.setPictures(pictures);
            tmp.add(d);
        }
        if (tmp.size() > 0) {

            //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

//            scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
//                @Override
//                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
//                    Log.e("ListView Scroll", "Started...");
//                    // Triggered only when new data needs to be appended to the list
//                    // Add whatever code is needed to append new items to the bottom of the list
//                    //loadNextDataFromApi(page);
//                }
//            };
            recyclerView.setVisibility(View.VISIBLE);
            Collections.reverse(tmp);
            layoutManager= new GridLayoutManager(context,2);
            recyclerView.setLayoutManager(layoutManager);
            analysisAdapter = new AnalysisAdapter(context, tmp);
            recyclerView.setAdapter(analysisAdapter);
            recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
            recyclerView.addOnScrollListener(scrollListener);
            //recyclerView.setLayoutAnimation(controller);
            recyclerView.scheduleLayoutAnimation();
            //recyclerView.addItemDecoration(decoration);
        } else {
            empty.setVisibility(View.VISIBLE);
        }
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getLayoutInflater().inflate(R.layout.fragment_analysis, null, false);
        ButterKnife.bind(this, view);
        DB = AppDatabase.getInstance(getContext());
                diagnostics = DB.diagnosticDao().getAllSync();
                for (Diagnostic d : diagnostics) {
                    List<Picture> pictures = DB.pictureDao().getByDiagnosticIdSync(d.getX());
                    Log.e("Analysis Frag pic",pictures.size()+"");
                    d.setPictures(pictures);
                    tmp.add(d);
                }
        if (tmp.size() > 0) {

            //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

//            scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
//                @Override
//                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
//                    Log.e("ListView Scroll", "Started...");
//                    // Triggered only when new data needs to be appended to the list
//                    // Add whatever code is needed to append new items to the bottom of the list
//                    //loadNextDataFromApi(page);
//                }
//            };
            recyclerView.setVisibility(View.VISIBLE);
            Collections.reverse(tmp);
            layoutManager= new GridLayoutManager(context,2);
            recyclerView.setLayoutManager(layoutManager);

            //recyclerView.addItemDecoration(new Gr);
            analysisAdapter = new AnalysisAdapter(context, tmp);
            recyclerView.setAdapter(analysisAdapter);

            recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
            recyclerView.addOnScrollListener(scrollListener);
            Log.d("analysis_columns :",layoutManager.getSpanCount()+" columns");
            //recyclerView.setLayoutAnimation(controller);
            recyclerView.scheduleLayoutAnimation();
            //recyclerView.addItemDecoration(decoration);
        } else {
            empty.setVisibility(View.VISIBLE);
        }
        //controller= AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_animation_fall_down);
    }
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}



//Hide the loading dialog

