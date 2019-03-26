package wesicknessdect.example.org.wesicknessdetect.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.budiyev.android.circularprogressbar.CircularProgressBar;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.events.ShowPixScreenEvent;
import wesicknessdect.example.org.wesicknessdetect.models.CulturePart;

public class CulturePartAdapter extends RecyclerView.Adapter<CulturePartAdapter.CultureHolder> {

    Activity context;
    List<CulturePart> cultureParts;
    HashMap<Integer, String> culturePart_image;
    Animation downtoup;

    public CulturePartAdapter(Activity context, List<CulturePart> cultureParts, HashMap<Integer, String> culturePart_image) {
        this.context = context;
        this.cultureParts = cultureParts;
        this.culturePart_image = culturePart_image;
    }

    @NonNull
    @Override
    public CultureHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.part_culture_item,
                parent, false);
        downtoup=AnimationUtils.loadAnimation(context, R.anim.toggledowntoup);
        return new CultureHolder(view);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull CultureHolder holder, @NonNull int position) {
        Uri uri = Uri.parse(cultureParts.get(position).getImage());
        String imagePath = context.getExternalFilesDir(null) + File.separator + uri.getLastPathSegment();
        Log.d("image path", imagePath);
        File f = new File(imagePath);

        holder.progressBar.setMaximum(cultureParts.get(position).getFilesize());
        holder.progressBar.setProgress((int) cultureParts.get(position).getDownloaded());
        holder.name.setText(cultureParts.get(position).getNom());
        //RemoteTasks.getInstance(context).DownloadFile(cultureParts.get(position).getImage());
        if (f.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            Bitmap bitmap_cropped = Bitmap.createScaledBitmap(bitmap, 200, 200, false);
            holder.image_part.setImageBitmap(bitmap_cropped);
        }

        if(cultureParts.get(position).isRecognizing()){
            holder.progressBar_recognize.setVisibility(View.VISIBLE);
        }else{
            holder.progressBar_recognize.setVisibility(View.GONE);
        }

        if (cultureParts.get(position).getDownloaded() == cultureParts.get(position).getFilesize()) {
            holder.progressBar.setVisibility(View.GONE);
            holder.imageButton.setVisibility(View.VISIBLE);
            Log.e("Model Downloaded", "OK");
            holder.imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new ShowPixScreenEvent((int) cultureParts.get(position).getId()));
                }
            });
        } else {
            holder.progressBar.setVisibility(View.VISIBLE);
            holder.imageButton.setVisibility(View.INVISIBLE);
        }

        for (Map.Entry<Integer, String> entry : culturePart_image.entrySet()) {
            Log.e("adapter entry", entry.getKey() + "/" + entry.getValue() + "//" + cultureParts.get(position).getId());
            if (entry.getKey() == cultureParts.get(position).getId()) {
                Log.e("image added", entry.getKey() + "");
                Bitmap bitmap = BitmapFactory.decodeFile(entry.getValue());
                Bitmap bitmap_cropped = Bitmap.createScaledBitmap(bitmap, 200, 200, false);
                holder.imageButton.setImageBitmap(bitmap_cropped);
            }
        }
    }

    @Override
    public int getItemCount() {
        return cultureParts.size();
    }

    public class CultureHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_part)
        CircularImageView image_part;
        @BindView(R.id.add_part_picture)
        ImageButton imageButton;
        @BindView(R.id.progress_bar)
        CircularProgressBar progressBar;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.progressBar_recognize)
        ProgressBar progressBar_recognize;

        public CultureHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
