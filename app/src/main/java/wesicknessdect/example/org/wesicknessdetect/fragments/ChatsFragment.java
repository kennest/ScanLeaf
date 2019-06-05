package wesicknessdect.example.org.wesicknessdetect.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import wesicknessdect.example.org.wesicknessdetect.adapters.ChatAdapter;
import wesicknessdect.example.org.wesicknessdetect.R;

import static wesicknessdect.example.org.wesicknessdetect.activities.BaseActivity.DB;

/**
 * Created by Yugansh Tyagi on 3/21/2018.
 */

public class ChatsFragment extends Fragment {

    RecyclerView recyclerView;
    private LinearLayout m1LinearLayout;
    String maladie;
    String distance;
    String checkBoxText;
    private Context context=getContext();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        //m1LinearLayout=(LinearLayout) view.findViewById(R.id.ll);
//        loadAlert();
//        SeparatorDecoration decoration = new SeparatorDecoration(context,
//                Color.parseColor("#EAEAEA"),
//                0.5f);
        recyclerView = view.findViewById(R.id.chat_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new ChatAdapter(context, DB.postDao().getAllPost()));
//        recyclerView.addItemDecoration(decoration);


        return view;
    }
//    private void addLayout(String maladie, String distance) {
//        LinearLayout mLinearLayout=new LinearLayout(getContext());
//        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
//        mLinearLayout.setLayoutParams(param);
//
//        //LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
//        View myView = getLayoutInflater().inflate(R.layout.chat_item_layout, null);
//        ImageView iv=myView.findViewById(R.id.user_image);
//        //iv.setImageResource(R.drawable.swollen);
//        TextView tv=myView.findViewById(R.id.my_status);
//        String f=maladie+" détecté près de vous...";
//        tv.setText(f);
//
//        TextView tV=myView.findViewById(R.id.latest_time);
//        long millis = new Date().getTime();
//        millis=(millis/1000);
//        String t=millis+"s";
//        tV.setText(t);
//
//        TextView dist=getView().findViewById(R.id.latest_message);
//        long di= Long.parseLong(distance);
//        int dis;
//        dis= (int) (di/1000);
//        String d=dis+" km";
//        dist.setText(d);
//
//        m1LinearLayout.addView(myView);
//
//    }
//
//    private void loadAlert(){
//        DB.postDao().getAll().observe(getActivity(), new Observer<List<Post>>() {
//            @Override
//            public void onChanged(List<Post> posts) {
//                Log.d("postNumber", posts.size()+"");
//                for (Post p:posts){
//                    addLayout(p.getDiseaseName(),p.getDistance());
//                }
//            }
//        });
//    }
}
