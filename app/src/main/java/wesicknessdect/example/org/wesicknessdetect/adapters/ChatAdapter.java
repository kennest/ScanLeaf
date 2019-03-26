package wesicknessdect.example.org.wesicknessdetect.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import wesicknessdect.example.org.wesicknessdetect.R;

/**
 * Created by Jordan Adopo on 10/02/2019.
 */

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;

    public ChatAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_layout,
                parent,false);

        return new ChatHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ((ChatHolder) holder).userName.setText("Vieux kouakou");
        ((ChatHolder) holder).unreadCnt.setText("7");
        ((ChatHolder) holder).latestTime.setText("Hier");
        ((ChatHolder) holder).latestMessage.setText("message Text!");
        //Glide.with(context).load(R.drawable.vieuxkouakou).into(((ChatHolder) holder).userImage);
    }

    @Override
    public int getItemCount() {
        return 50;
    }

    private class ChatHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView userImage;
        TextView userName,latestMessage,latestTime,unreadCnt;

        public ChatHolder(View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.user_image);
            latestMessage = itemView.findViewById(R.id.latest_message);
            userName = itemView.findViewById(R.id.my_status);
            latestTime = itemView.findViewById(R.id.latest_time);
            unreadCnt = itemView.findViewById(R.id.unread_messages);
        }

        @Override
        public void onClick(View v) {

        }
    }

}
