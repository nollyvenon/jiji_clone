package com.example.finder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import adapters.MessageAdapter;
import data.Messages;
import others.BottomAppBarEvent;

public class MessageList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        TextView pageName = findViewById(R.id.page_name);
        pageName.setText("Messages");

        List<Messages> messages = new ArrayList<>();
        messages.add(new Messages("Awelewa Oluwatobi", "2 days ago", "1",
                "If you are connected but behind a firewall, check that Firefox has permission to access the Web."));
        messages.add(new Messages("Segun Oyekunle", "2 days ago", "2",
                "If you are connected but behind a firewall, check that Firefox has permission to access the Web."));
        messages.add(new Messages("Ahmed Tijani", "2 days ago", "3",
                "If you are connected but behind a firewall, check that Firefox has permission to access the Web."));
        messages.add(new Messages("Balogun Emmanuela", "2 days ago", "4",
                "If you are connected but behind a firewall, check that Firefox has permission to access the Web."));


        RecyclerView recyclerView = findViewById(R.id.messages);
        MessageAdapter adapter = new MessageAdapter(this, messages);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.setAdapter(adapter);

    }

    public void goToHomeActivity(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(MessageList.this);
        bottomAppBarEvent.goToHomeActivity();
    }

    public void postAd(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(MessageList.this);
        bottomAppBarEvent.postAd();
    }

    public void postFind(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(MessageList.this);
        bottomAppBarEvent.postFind();
    }

    public void goToProfileActivity(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(MessageList.this);
        bottomAppBarEvent.goToProfileActivity();
    }

    public void goToMessageListActivity(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(MessageList.this);
        bottomAppBarEvent.goToMessageListActivity();
    }
}
