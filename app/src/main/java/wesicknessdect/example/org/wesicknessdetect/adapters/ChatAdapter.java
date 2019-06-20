package wesicknessdect.example.org.wesicknessdetect.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.List;

import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.activities.NotificationActivity;
import wesicknessdect.example.org.wesicknessdetect.models.Post;

/**
 * Created by Jordan Adopo on 10/02/2019.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatHolder> {

    private List<Post> mData = null;
    private Context context;


    public ChatAdapter(Context context, List<Post> list) {
        this.mData = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_item_layout,
                viewGroup, false);

        return new ChatHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatHolder holder, int position) {
        Post post = mData.get(position);
        holder.cardView.setTag(mData.get(position));
        holder.bind(post);
        //Glide.with(context).load(R.drawable.vieuxkouakou).into(((ChatHolder) holder).userImage);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ChatHolder extends RecyclerView.ViewHolder {

        ImageView userImage;
        TextView userName, latestMessage, latestTime, unreadCnt;
        CardView cardView;

        public ChatHolder(final View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.user_image);
            latestMessage = itemView.findViewById(R.id.latest_message);
            userName = itemView.findViewById(R.id.my_status);
            latestTime = itemView.findViewById(R.id.latest_time);
            cardView=itemView.findViewById(R.id.card);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    Gson gson = new Gson();
                    bundle.putString("post", gson.toJson(v.getTag()));
                    Log.d("Chat Apapter post->",gson.toJson(v.getTag()));
                    Intent notificationIntent = new Intent(context, NotificationActivity.class);
                    notificationIntent.putExtra("bundle", bundle);
                    context.startActivity(notificationIntent);
                }
            });
        }

        public void bind(final Post post) {
            if (post.getDiseaseName().charAt(0)=='S'){
                userImage.setImageDrawable(itemView.getResources().getDrawable(R.drawable.swollen_shoot));
            }else{
                userImage.setImageDrawable(itemView.getResources().getDrawable(R.drawable.pourriture_cacao));
            }

            String f = post.getDiseaseName();
            userName.setText(f);

            latestTime.setText(post.getTime());

//            String distance = post.getDistance();
//            Character z = '0';
//            if (distance.charAt(0) == z) {
//                distance = "Détecté près";
//            } else {
//                distance = "Détecté à " + post.getDistance() + " km";
//            }
            String d ="Détecté dans la zone de "+ post.getCity() + ".";
            latestMessage.setText(d);
        }


    }

}
