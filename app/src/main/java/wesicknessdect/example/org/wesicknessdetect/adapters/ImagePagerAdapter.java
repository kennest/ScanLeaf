package wesicknessdect.example.org.wesicknessdetect.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.List;
import androidx.viewpager.widget.PagerAdapter;
import wesicknessdect.example.org.wesicknessdetect.R;

public class ImagePagerAdapter extends PagerAdapter {
    Context mContext;
    LayoutInflater mLayoutInflater;
    List<Bitmap> bitmaps;

    public ImagePagerAdapter(Context context, List<Bitmap> bitmaps) {
        mContext = context;
        this.bitmaps=bitmaps;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return bitmaps.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.analysis_detail_image, container, false);
        ImageView image=itemView.findViewById(R.id.image);
        image.setImageBitmap(bitmaps.get(position));
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
