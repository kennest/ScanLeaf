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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.budiyev.android.circularprogressbar.CircularProgressBar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.events.DeletePartPictureEvent;
import wesicknessdect.example.org.wesicknessdetect.events.ShowPixScreenEvent;
import wesicknessdect.example.org.wesicknessdetect.models.CulturePart;

public class CulturePartAdapter extends RecyclerView.Adapter<CulturePartAdapter.CultureHolder> {

    private Activity context;
    private List<CulturePart> cultureParts;
    private Map<Integer, String> culturePart_image;
    private BottomSheetDialog dialog;

    public CulturePartAdapter(Activity context, List<CulturePart> cultureParts, Map<Integer, String> culturePart_image) {
        this.context = context;
        this.cultureParts = cultureParts;
        this.culturePart_image = culturePart_image;
    }

    @NonNull
    @Override
    public CultureHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.part_culture_item,
                parent, false);
        return new CultureHolder(view);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull CultureHolder holder, @NonNull int position) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Uri uri = Uri.parse(cultureParts.get(position).getImage());
                String imagePath = context.getExternalFilesDir(null) + File.separator + uri.getLastPathSegment();
                //Log.d("image path", imagePath);
                File f = new File(imagePath);

                holder.progressBar.setMaximum(cultureParts.get(position).getFilesize());
                holder.progressBar.setProgress((int) cultureParts.get(position).getDownloaded());
                holder.name.setText(cultureParts.get(position).getNom());

                if (f.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                    Bitmap bitmap_cropped = Bitmap.createScaledBitmap(bitmap, 200, 200, false);
                    holder.image_part.setImageBitmap(bitmap_cropped);
                }

                //Si la partie es en cours de traitement
                if (cultureParts.get(position).isRecognizing()) {
                    Log.d("Rx Recognizing ->",cultureParts.get(position).getId()+"");
                    holder.progressBar_recognize.setVisibility(View.VISIBLE);
                    holder.imageButton.setEnabled(false);
                    holder.imageButton.setClickable(false);
                    holder.checked.setVisibility(View.GONE);
                } else {
                    holder.progressBar_recognize.setVisibility(View.GONE);
                    holder.imageButton.setEnabled(true);
                    holder.imageButton.setClickable(true);
                }

                //Si la partie est deja traitee
                if (cultureParts.get(position).isChecked()) {
                    holder.checked.setVisibility(View.VISIBLE);
                    holder.progressBar_recognize.setVisibility(View.GONE);
                } else {
                    holder.checked.setVisibility(View.GONE);
                }

                //Si le telechargement du modele est fini
                if (cultureParts.get(position).getDownloaded() == cultureParts.get(position).getFilesize()) {
                    cultureParts.get(position).setModel_downloaded(true);
                    holder.progressBar.setVisibility(View.GONE);
                    holder.imageButton.setVisibility(View.VISIBLE);
                    //Log.e("Model Downloaded", "OK");
                    holder.imageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            View view = context.getLayoutInflater().inflate(R.layout.part_sheet, null);
                            dialog = new BottomSheetDialog(context);
                            dialog.setContentView(view);
                            TextView title_bsd = (TextView) view.findViewById(R.id.title_bsd);
                            title_bsd.setText(cultureParts.get(position).getNom());
                            ImageView partie_bsd =(ImageView) view.findViewById(R.id.partie_bsd);
                            if (cultureParts.get(position).getNom().contains("TIGE")){
                                partie_bsd.setImageResource(R.drawable.trong);
                            }else if (cultureParts.get(position).getNom().contains("FEUILLE")){
                                partie_bsd.setImageResource(R.drawable.feuille);
                            }else if (cultureParts.get(position).getNom().contains("FRUIT")){
                                partie_bsd.setImageResource(R.drawable.cabosse);
                            }else if (cultureParts.get(position).getNom().contains("COLLET")){
                                partie_bsd.setImageResource(R.drawable.racine);
                            }else if (cultureParts.get(position).getNom().contains("REJET")){
                                partie_bsd.setImageResource(R.drawable.rejet);
                            }
                            TextView close_bsd = (TextView) view.findViewById(R.id.close_bsd);
                            close_bsd.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    EventBus.getDefault().post(new ShowPixScreenEvent((int) cultureParts.get(position).getId()));
                                    dialog.dismiss();
                                }
                            });
                            dialog.show();


                        }
                    });
                } else {
                    holder.progressBar.setVisibility(View.VISIBLE);
                    holder.imageButton.setVisibility(View.INVISIBLE);
                    cultureParts.get(position).setModel_downloaded(false);
                }

                for (Map.Entry<Integer, String> entry : culturePart_image.entrySet()) {
                    //Log.e("adapter entry", entry.getKey() + "/" + entry.getValue() + "//" + cultureParts.get(position).getId());
                    if (entry.getKey() == cultureParts.get(position).getId()) {
                       // Log.e("image added", entry.getKey() + "");
                        Bitmap bitmap = BitmapFactory.decodeFile(entry.getValue());
                        Bitmap bitmap_cropped = Bitmap.createScaledBitmap(bitmap, 200, 200, false);
                        holder.imageButton.setImageBitmap(bitmap_cropped);
                        holder.delPicture.setVisibility(View.VISIBLE);
                        Long l = cultureParts.get(position).getId();
                        int id = l.intValue();
                        holder.delPicture.setTag(id);
                        holder.delPicture.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cultureParts.get(position).setChecked(false);
                                cultureParts.get(position).setRecognizing(false);
                                EventBus.getDefault().post(new DeletePartPictureEvent((Integer) v.getTag()));
                            }
                        });
                    }
                }
            }
        });
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
        @BindView(R.id.del_picture)
        ImageButton delPicture;
        @BindView(R.id.progress_bar)
        CircularProgressBar progressBar;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.progressBar_recognize)
        ProgressBar progressBar_recognize;
        @BindView(R.id.checked)
        ImageView checked;

        public CultureHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
