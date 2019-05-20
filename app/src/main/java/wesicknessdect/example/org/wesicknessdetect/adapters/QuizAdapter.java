package wesicknessdect.example.org.wesicknessdetect.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import io.paperdb.Paper;
import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.events.QuizCheckedEvent;
import wesicknessdect.example.org.wesicknessdetect.models.CulturePart;
import wesicknessdect.example.org.wesicknessdetect.models.Question;
import wesicknessdect.example.org.wesicknessdetect.models.Symptom;
import wesicknessdect.example.org.wesicknessdetect.utils.AppController;

public class QuizAdapter extends BaseAdapter {
    HashMap<CulturePart, Question> list = new HashMap<>();
    Activity activity;
    String diagnostic_uuid;


    public QuizAdapter(HashMap<CulturePart, Question> list, Activity activity,String diagnostic_uuid) {
        this.list = list;
        this.activity = activity;
        this.diagnostic_uuid = diagnostic_uuid;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list;
    }


    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Gson gson = new Gson();
        convertView = activity.getLayoutInflater().inflate(R.layout.quiz_item, null, false);
        CircularImageView part_icon = convertView.findViewById(R.id.partIcon);
        TextView part_culture = convertView.findViewById(R.id.partCulture);
        TextView question = convertView.findViewById(R.id.question);
        LinearLayout symptom_layout = convertView.findViewById(R.id.sympt);
        symptom_layout.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);


        Set<Integer> symptoms_sets = new HashSet<>();

        for (Map.Entry<CulturePart, Question> entry : list.entrySet()) {

            Log.e("Quiz Adpater Symptom ->", entry.getValue().getSymptomList().size() + "");
            part_icon.setImageBitmap(BitmapFactory.decodeFile(entry.getKey().getImage()));
            part_culture.setText(entry.getKey().getNom());
            question.setText(entry.getValue().getQuestion());

            for (Symptom s : entry.getValue().getSymptomList()) {
                CheckBox ch = new CheckBox(activity);
                ch.setText(s.getName());
                ch.setBackgroundColor(activity.getResources().getColor(R.color.colorPrimaryLightPix));
                ch.setPadding(5, 5, 5, 5);
                ch.setTextSize(14);
                ch.setTag(s.getId());
                ch.setTextColor(activity.getResources().getColor(R.color.white));
//                                                infos.setOnClickListener();
                ch.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        WebView webView = new WebView(activity);
                        webView.loadUrl(s.getLink());
                        webView.loadData("<p style=\"background-color:#00574B; color:white \" align=\"center\">Voici comment se présente <br/><b>" + s.getName() + "</b><br/><br/>(A remplacer ce text par la page web correspondante...) !</p>", "text/html", "utf-8");

                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setTitle("Infos sur " + s.getName())
                                .setView(webView)
                                .setNeutralButton("OK", null)
                                .show();
                        return false;
                    }
                });

                //Listen on checkbox change to set symptoms IDs
                ch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        Set< HashMap<Integer, Set<Integer>>> gson_list=new HashSet<>();
                        HashMap<Integer, Set<Integer>> choices = new HashMap<>();

                        if (isChecked) {
                            symptoms_sets.add((Integer) ch.getTag());
                            ch.setBackgroundColor(activity.getResources().getColor(R.color.white));
                            ch.setTextColor(activity.getResources().getColor(R.color.colorPrimaryLightPix));
                            Log.e("CheckBox Tag ->", buttonView.getTag() + "");
                        } else {
                            symptoms_sets.remove(ch.getTag());
                            ch.setTextColor(activity.getResources().getColor(R.color.white));
                            ch.setBackgroundColor(activity.getResources().getColor(R.color.colorPrimaryLightPix));
                        }

                        //Store Question with its symptoms IDs
                        choices.put(entry.getValue().getId(), symptoms_sets);
                        EventBus.getDefault().post(new QuizCheckedEvent(choices, (int) entry.getKey().getId()));
                        //gson_list.add(choices);
//                        Set< HashMap<Integer, Set<Integer>>> tmp = new HashSet<>();
//                        tmp=AppController.getInstance().getUser_choices();
//                        tmp.add(choices);
//
//
//                        Log.e("Choices tmp size ->", tmp.size() + "");
//                        Log.e("Choices tmp Json ->", tmp.toString());
                    }
                });
                ch.setId(s.getId());
                symptom_layout.addView(ch);
            }
        }
        return convertView;
    }

}
