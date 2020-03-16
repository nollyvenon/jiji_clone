package com.example.finder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import adapters.MessagePanelAdapter;
import data.Chats;
import others.BottomAppBarEvent;

public class MessagePanel extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_panel);
        TextView pageName = findViewById(R.id.page_name);
        pageName.setText("Chats");

        List<Chats> chats = new ArrayList<>();
        chats.add(new Chats("Awelewa Oluwatobi", "2 days ago", "1",
                "If you are connected but behind a firewall, check that Firefox has permission to access the Web."));
        chats.add(new Chats("Segun Oyekunle", "2 days ago", "2",
                "If you are connected but behind a firewall, check that Firefox has permission to access the Web."));
        chats.add(new Chats("Ahmed Tijani", "2 days ago", "1",
                "If you are connected but behind a firewall, check that Firefox has permission to access the Web."));
        chats.add(new Chats("Balogun Emmanuela", "2 days ago", "4",
                "If you are connected but behind a firewall, check that Firefox has permission to access the Web."));


        RecyclerView recyclerView = findViewById(R.id.messages);
        MessagePanelAdapter adapter = new MessagePanelAdapter(this, chats);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.setAdapter(adapter);
    }

    public void goToHomeActivity(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(MessagePanel.this);
        bottomAppBarEvent.goToHomeActivity();
    }

    public void postAd(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(MessagePanel.this);
        bottomAppBarEvent.postAd();
    }

    public void postFind(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(MessagePanel.this);
        bottomAppBarEvent.postFind();
    }

    public void goToProfileActivity(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(MessagePanel.this);
        bottomAppBarEvent.goToProfileActivity();
    }

    public void goToMessageListActivity(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(MessagePanel.this);
        bottomAppBarEvent.goToMessageListActivity();
    }
}
