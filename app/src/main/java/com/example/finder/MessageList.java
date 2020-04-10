package com.example.finder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import Database.DatabaseOpenHelper;
import adapters.MessageAdapter;
import data.AdPoster;
import data.Messages;
import others.BottomAppBarEvent;
import retrofit.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageList extends AppCompatActivity {
    MessageAdapter adapter;
    RecyclerView recyclerView;
    List<Messages> messages = new ArrayList<>();
    ProgressBar progressBar;

    Boolean isScrolling = true;
    GridLayoutManager manager;
    int currentItems, totalItems, scrollOutItems;
    int adCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        TextView pageName = findViewById(R.id.page_name);
        pageName.setText("Messages");


        ImageView bottomBarImg = findViewById(R.id.img_msg_bottom_bar);
        TextView bottomBarTitle = findViewById(R.id.title_msg_bottom_bar);
        bottomBarImg.setColorFilter(getResources().getColor(R.color.colorPrimary));
        bottomBarTitle.setTextColor(getResources().getColor(R.color.colorPrimary));

        ImageView search = findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomAppBarEvent.goToSearchActivity(MessageList.this);
            }
        });

        progressBar = findViewById(R.id.progress_circular);
        fetchMessages();

        recyclerView = findViewById(R.id.messages);
        adapter = new MessageAdapter(MessageList.this, messages);
        manager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = manager.getChildCount();
                totalItems = manager.getItemCount();
                scrollOutItems = manager.findFirstVisibleItemPosition();

                if(isScrolling) {
                    if ((currentItems + scrollOutItems) >= totalItems ) {
                        isScrolling = false;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                adCount = adCount + 20;
                                fetchMessages();
                            }
                        }, 5000);
                    }
                }
            }
        });
    }

    public void goBack(View view) {
        finish();
    }

    private void fetchMessages() {
        DatabaseOpenHelper dbo = new DatabaseOpenHelper(this);
        AdPoster adPoster = dbo.getAdPoster();

        Call<List<Messages>> call = ApiClient.connect().getMessages(adPoster.getAuth(), adCount);
        call.enqueue(new Callback<List<Messages>>() {
            @Override
            public void onResponse(@NonNull Call<List<Messages>> call, @NonNull Response<List<Messages>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(MessageList.this, "" + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                List<Messages> m = response.body();
                assert m != null;
                messages.addAll(m);
                if(m.size() == 0) progressBar.setVisibility(View.INVISIBLE);

                adapter.setData(messages);
            }

            @Override
            public void onFailure(@NonNull Call<List<Messages>> call, @NonNull Throwable t) {
                //Toast.makeText(MessageList.this, t.toString(), Toast.LENGTH_LONG).show();
            }
        });
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
