package wesicknessdect.example.org.wesicknessdetect.fragments;


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

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.adapters.AnalysisAdapter;
import wesicknessdect.example.org.wesicknessdetect.database.AppDatabase;
import wesicknessdect.example.org.wesicknessdetect.events.ToggleViewEvent;
import wesicknessdect.example.org.wesicknessdetect.models.DiagnosticPictures;

/**
 * Created by Yugansh Tyagi on 3/21/2018.
 */

public class AnalyseFragment extends Fragment {

    @BindView(R.id.status_rv)
    RecyclerView recyclerView;

    @BindView(R.id.empty_data)
    View empty;

    @BindView(R.id.calendarView)
    CalendarView calendarView;

    public boolean is_calendar_view_shown =false;

    private static AppDatabase DB;

    AnalysisAdapter analysisAdapter;
    LayoutAnimationController controller;

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
        DB = AppDatabase.getInstance(getContext());
        calendarView=new CalendarView(getActivity());
        controller= AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_animation_fall_down);
       InitView();
    }

    private void InitView(){

        DB.diagnosticDao().getDiagnosticWithPictures().observe(this, new Observer<List<DiagnosticPictures>>() {
            @Override
            public void onChanged(List<DiagnosticPictures> diagnosticPictures) {
                if(diagnosticPictures.size()>0){
//                    SeparatorDecoration decoration = new SeparatorDecoration(
//                            getContext(),
//                            Color.parseColor("#EAEAEA"),
//                            0.5f);
                    if(is_calendar_view_shown){
                        Log.e("Toggle view calendar",is_calendar_view_shown+"");
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(1970, 4, 5);
                        calendar.set(2019, 4, 6);
                        recyclerView.setVisibility(View.GONE);
                        calendarView.setVisibility(View.VISIBLE);
                        calendarView.setDate(calendar.get(Calendar.DATE));
                    }else{
                        Log.e("Toggle view recycler",is_calendar_view_shown+"");
                        calendarView.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        recyclerView.setHasFixedSize(true);
                        Collections.reverse(diagnosticPictures);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        analysisAdapter=new AnalysisAdapter(getActivity(),diagnosticPictures);
                        recyclerView.setAdapter(analysisAdapter);
                        recyclerView.setLayoutAnimation(controller);
                        recyclerView.scheduleLayoutAnimation();
                        //recyclerView.addItemDecoration(decoration);
                    }
                }else{
                    empty.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    //Hide the loading dialog
    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void toggleCalendarView(ToggleViewEvent event){
        Log.e("Toggle view",event.show+"//"+is_calendar_view_shown);
        if(!is_calendar_view_shown) {
            is_calendar_view_shown = event.show;
        }else{
            is_calendar_view_shown=false;
        }
        InitView();
    }


    @Override
    public void onStart() {
        Log.d("EventBus", "Register ");
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("EventBus", "Unregister");
        EventBus.getDefault().unregister(this);
    }
}
