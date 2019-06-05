package wesicknessdect.example.org.wescanleaf.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import wesicknessdect.example.org.wescanleaf.R;
import wesicknessdect.example.org.wescanleaf.models.Post;

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
                viewGroup,false);

        return new ChatHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatHolder holder, int position) {

        Post post=mData.get(position);
        holder.bind(post);


        //Glide.with(context).load(R.drawable.vieuxkouakou).into(((ChatHolder) holder).userImage);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ChatHolder extends RecyclerView.ViewHolder {

        ImageView userImage;
        TextView userName,latestMessage,latestTime,unreadCnt;

        public ChatHolder(final View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.user_image);
            latestMessage = itemView.findViewById(R.id.latest_message);
            userName = itemView.findViewById(R.id.my_status);
            latestTime = itemView.findViewById(R.id.latest_time);
            //unreadCnt = itemView.findViewById(R.id.unread_messages);

        }

        public void bind(final Post post){
            userImage.setImageDrawable(itemView.getResources().getDrawable(R.drawable.swollen));
            String f=post.getDiseaseName();
            userName.setText(f);


            latestTime.setText(post.getTime());

//            long di= Long.parseLong(post.getDistance());
//            int dis;
//            dis= (int) (di/1000);
            String distance=post.getDistance();
            Character z='0';
            if (distance.charAt(0)==z)
            {
                distance="Détecté près";
            }else {
                distance="Détecté à "+post.getDistance()+" km";
            }
            String d=distance+" de vous";
            latestMessage.setText(d);
        }


    }

}
