package com.example.flowershop.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flowershop.R;
import com.example.flowershop.adapters.MessageListAdapter;
import com.example.flowershop.models.Message;
import com.example.flowershop.models.User;

import java.util.ArrayList;;
import java.util.List;

public class ChatFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_chat, container, false);
        TextView date = root.findViewById(R.id.text_gchat_date_me);
        EditText message = root.findViewById(R.id.edit_gchat_message);
        Button sendBtn = root.findViewById(R.id.button_gchat_send);
        SharedPreferences pref = getActivity().getSharedPreferences("CurrentUser", MODE_PRIVATE);
        int userID = pref.getInt("id", 0);
        String username = pref.getString("username", "");
        List<Message> list = new ArrayList<>();
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageS = message.getText().toString();
                list.add(new Message(messageS, new User(userID, username), System.currentTimeMillis()));
                message.setText("");
            }
        });
        RecyclerView recyclerView = root.findViewById(R.id.recycler_gchat);
        MessageListAdapter adapter = new MessageListAdapter(getActivity(), list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return root;
    }
}
