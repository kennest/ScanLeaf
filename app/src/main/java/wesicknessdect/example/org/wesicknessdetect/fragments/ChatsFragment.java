package wesicknessdect.example.org.wesicknessdetect.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import wesicknessdect.example.org.wesicknessdetect.adapters.ChatAdapter;
import wesicknessdect.example.org.wesicknessdetect.R;
import wesicknessdect.example.org.wesicknessdetect.ui.SeparatorDecoration;

/**
 * Created by Yugansh Tyagi on 3/21/2018.
 */

public class ChatsFragment extends Fragment {

    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        SeparatorDecoration decoration = new SeparatorDecoration(
                getActivity().getApplicationContext(),
                Color.parseColor("#EAEAEA"),
                0.5f);
        recyclerView = view.findViewById(R.id.chat_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new ChatAdapter(getActivity().getApplicationContext()));
        recyclerView.addItemDecoration(decoration);

        return view;
    }
}
