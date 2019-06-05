package wesicknessdect.example.org.wesicknessdetect.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import androidx.viewpager.widget.PagerAdapter;
import wesicknessdect.example.org.wesicknessdetect.R;

public class ImagePagerAdapter extends PagerAdapter {
    Activity mContext;
    LayoutInflater mLayoutInflater;
    List<Map<String, Bitmap>> linkedPartImage;


    public ImagePagerAdapter(Activity context, List<Map<String, Bitmap>> linkedPartImage) {
        mContext = context;
        this.linkedPartImage = linkedPartImage;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        //Log.e("SIZE:", linkedPartImage.size() + "");
        //Log.e("SIZE N:",linkedSymptColor.size()+"");
        return linkedPartImage.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.analysis_detail_image, container, false);
        ImageView image = itemView.findViewById(R.id.image);
        TextView partname=itemView.findViewById(R.id.partname);
        CircularImageView part_image = itemView.findViewById(R.id.part_image);
        LinearLayout symptoms_txt = itemView.findViewById(R.id.symptoms_txt);
        symptoms_txt.setOrientation(LinearLayout.VERTICAL);
        Gson gson = new Gson();

                String[] str2;
                String[] str;
                ArrayList tags = new ArrayList();

                if (linkedPartImage.size() > 0) {
                    com.google.android.material.chip.ChipGroup chipGroup = new com.google.android.material.chip.ChipGroup(mContext);
                    for (Map.Entry<String, Bitmap> n : linkedPartImage.get(position).entrySet()) {
                        //Log.e("Adapter Part img:", position + "//" + n.getKey());
                        str=n.getKey().split("::");
                        Log.e("Splitted 0:  ",str[1]);
                        Bitmap bm = BitmapFactory.decodeFile(str[0]);
                        part_image.setImageBitmap(bm);
                        //image.setImageBitmap(BitmapFactory.decodeFile(n.getKey()));
                        image.setImageBitmap(n.getValue());
                        partname.setText(str[2]);
                        //Split to get symptoms infos
                        Type typeOfHashMap = new TypeToken<List<String>>() {}.getType();
                        symptoms_txt.removeAllViews();
                        Set<String> symptAttrs=new HashSet<>(gson.fromJson(str[1],typeOfHashMap));
                        Log.e("Attrs Size -> "+position+" -> ",symptAttrs.size()+"");
                        for(String s:symptAttrs){
                            str2=s.split(":");
                            com.google.android.material.chip.Chip cv=new com.google.android.material.chip.Chip(mContext);
                            cv.setText(String.format("%s", str2[0]));
                            cv.setTextColor(Color.WHITE);
                            cv.setChipIcon(mContext.getDrawable(R.drawable.barley_off));
//                            Log.e("color",str2[1].substring(1));
                            cv.setChipBackgroundColor(ColorStateList.valueOf(Integer.parseInt(str2[1])));
                            chipGroup.addView(cv);
                        }
                        symptoms_txt.removeAllViews();
                        symptoms_txt.addView(chipGroup);
                    }
//
                }

        container.addView(itemView);
        //image.setImageBitmap(bitmaps.get(position));

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}
