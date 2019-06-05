package wesicknessdect.example.org.wescanleaf.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import wesicknessdect.example.org.wescanleaf.R;
import wesicknessdect.example.org.wescanleaf.events.ShowPartScreenEvent;
import wesicknessdect.example.org.wescanleaf.models.Culture;

public class CultureAdapter extends RecyclerView.Adapter<CultureAdapter.CultureHolder> {
    List<Culture> cultures;
    private Context context;

    public CultureAdapter(List<Culture> cultures, Context context) {
        this.cultures = cultures;
        this.context = context;
    }

    @NonNull
    @Override
    public CultureHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.culture_item,
                parent, false);
        return new CultureHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CultureHolder holder, int position) {
                holder.name.setText(cultures.get(position).getName());
                Uri uri = Uri.parse(cultures.get(position).getImage());
                String imagePath = context.getExternalFilesDir(null) + File.separator + uri.getLastPathSegment();
                //Log.d("image path", imagePath);
                File f = new File(imagePath);
                if (f.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                    Bitmap bitmap_cropped = Bitmap.createScaledBitmap(bitmap, 200, 200, false);
                    holder.imageButton.setImageBitmap(bitmap_cropped);
                    holder.imageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EventBus.getDefault().post(new ShowPartScreenEvent("From Fragment"));
                        }
                    });
                }
    }

    @Override
    public int getItemCount() {
        return cultures.size();
    }

    public class CultureHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image)
        ImageButton imageButton;

        @BindView(R.id.name)
        TextView name;

        public CultureHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
