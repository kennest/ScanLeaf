package wesicknessdect.example.org.wesicknessdetect.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.viewpager.widget.PagerAdapter;
import wesicknessdect.example.org.wesicknessdetect.R;

public class ImagePagerAdapter extends PagerAdapter {
    Activity mContext;
    LayoutInflater mLayoutInflater;
    List<Bitmap> bitmaps;
    List<HashMap<String, Bitmap>> linkedPartImage;

    public ImagePagerAdapter(Activity context, List<Bitmap> bitmaps, List<HashMap<String, Bitmap>> linkedPartImage) {
        mContext = context;
        this.bitmaps = bitmaps;
        this.linkedPartImage = linkedPartImage;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        Log.e("SIZE:",linkedPartImage.size()+"");
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
        CircularImageView part_image = itemView.findViewById(R.id.part_image);

        for (Map.Entry<String, Bitmap> n : linkedPartImage.get(position).entrySet()) {
            Log.e("Adapter Part img:",position+"//"+n.getKey());
            Bitmap bm=BitmapFactory.decodeFile(n.getKey());
            part_image.setImageBitmap(bm);
            //image.setImageBitmap(BitmapFactory.decodeFile(n.getKey()));
            image.setImageBitmap(n.getValue());
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
