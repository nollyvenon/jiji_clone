package com.example.finder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import Database.DatabaseOpenHelper;
import adapters.MessagePanelAdapter;
import data.AdPoster;
import data.Chats;
import data.Messages;
import others.BottomAppBarEvent;
import retrofit.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessagePanel extends AppCompatActivity {

    EditText message;
    MaterialButton send;
    MessagePanelAdapter adapter;
    List<Messages> messagesList;
    AdPoster adPoster;
    ProgressDialog progDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_panel);
        TextView pageName = findViewById(R.id.page_name);
        pageName.setText(R.string.chats);

        DatabaseOpenHelper dbo = new DatabaseOpenHelper(this);
        adPoster = dbo.getAdPoster();

        message = findViewById(R.id.message);
        send = findViewById(R.id.send);

        MaterialButton jobFinished = findViewById(R.id.job_finished);
        jobFinished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                String adId = intent.getStringExtra("aid");
                sendFeedback(adId);
            }
        });

        fetchMessages();

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new MyTimerTask(), 2000, 4000);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

    }

    private void sendFeedback(String adId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MessagePanel.this);
        builder.setMessage("Are you through with the project?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        progDialog = ProgressDialog.show(MessagePanel.this, "Loading", "Please wait...", true);
                        progDialog.setCancelable(false);
                        chatFinish(adId);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void chatFinish(String adId) {
        Intent intent = getIntent();

        Call<Messages> call = ApiClient.connect().chatFinish(intent.getStringExtra("uniqueId"));
        call.enqueue(new Callback<Messages>() {
            @Override
            public void onResponse(@NonNull Call<Messages> call, @NonNull Response<Messages> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(MessagePanel.this, "" + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                Messages messages = response.body();
                assert messages != null;
                progDialog.dismiss();
                if (Boolean.parseBoolean(messages.getStatus())) {
                    Intent intent = new Intent(MessagePanel.this, FeedbackForm.class);
                    intent.putExtra("id", adId);
                    intent.putExtra("type", "msg");
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Messages> call, @NonNull Throwable t) {
                //Toast.makeText(MessagePanel.this, t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void goBack(View view) {
        finish();
    }

    private void fetchMessages() {
        Intent intent = getIntent();

        Call<List<Messages>> call = ApiClient.connect().getUniqueMessage(intent.getStringExtra("uniqueId"), adPoster.getAuth());
        call.enqueue(new Callback<List<Messages>>() {
            @Override
            public void onResponse(@NonNull Call<List<Messages>> call, @NonNull Response<List<Messages>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(MessagePanel.this, "" + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                messagesList = response.body();
                assert messagesList != null;

                RecyclerView recyclerView = findViewById(R.id.messages);
                adapter = new MessagePanelAdapter(MessagePanel.this, messagesList);
                GridLayoutManager manager = new GridLayoutManager(MessagePanel.this, 1);
                recyclerView.setLayoutManager(manager);
                recyclerView.setAdapter(adapter);
                manager.scrollToPosition(Objects.requireNonNull(recyclerView.getAdapter()).getItemCount() - 1);
            }

            @Override
            public void onFailure(@NonNull Call<List<Messages>> call, @NonNull Throwable t) {
                //Toast.makeText(MessagePanel.this, t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void sendMessage() {
        Intent intent = getIntent();
        String messageText = message.getText().toString();
        String adId = intent.getStringExtra("aid");
        String findId = intent.getStringExtra("fid");

        if (messageText.isEmpty()) return;

        Call<Messages> call = ApiClient.connect().addMessage(
                messageText, adId + "-" + findId, findId, adId, adPoster.getAuth()
        );

        call.enqueue(new Callback<Messages>() {
            @Override
            public void onResponse(@NonNull Call<Messages> call, @NonNull Response<Messages> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(MessagePanel.this, "" + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                Messages m = response.body();
                assert m != null;
                if (Boolean.parseBoolean(m.getStatus())) {
                    message.setText("");
                    fetchMessages();
                    adapter.setData(messagesList);
                }

            }

            @Override
            public void onFailure(@NonNull Call<Messages> call, @NonNull Throwable t) {
                Toast.makeText(MessagePanel.this, t.toString(), Toast.LENGTH_LONG).show();
            }
        });

    }

    public class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            MessagePanel.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    fetchMessages();
                }
            });
        }
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
