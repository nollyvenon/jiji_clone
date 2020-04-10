package com.example.finder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import Database.DatabaseOpenHelper;
import data.AdPoster;
import data.Feedback;
import retrofit.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedbackForm extends AppCompatActivity {

    String adId;
    int rating = 0;
    Button excellence, good, neutral, fair, poor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_form);

        Intent intent = getIntent();
        adId = intent.getStringExtra("id");

        excellence = findViewById(R.id.excellence);
        good = findViewById(R.id.good);
        neutral = findViewById(R.id.neutral);
        fair = findViewById(R.id.fair);
        poor = findViewById(R.id.poor);

        excellence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickTap();
                v.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                excellence.setTextColor(getResources().getColor(R.color.white));
                rating = 5;
            }
        });

        good.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickTap();
                v.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                good.setTextColor(getResources().getColor(R.color.white));
                rating = 4;
            }
        });

        neutral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickTap();
                v.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                neutral.setTextColor(getResources().getColor(R.color.white));
                rating = 3;
            }
        });

        fair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickTap();
                v.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                fair.setTextColor(getResources().getColor(R.color.white));
                rating = 2;
            }
        });

        poor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickTap();
                v.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                poor.setTextColor(getResources().getColor(R.color.white));
                rating = 1;
            }
        });
    }

    private void clickTap() {
        excellence.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        good.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        neutral.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        fair.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        poor.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

        excellence.setTextColor(getResources().getColor(R.color.green));
        good.setTextColor(getResources().getColor(R.color.colorPrimary));
        neutral.setTextColor(getResources().getColor(R.color.light_black));
        fair.setTextColor(getResources().getColor(R.color.light_red));
        poor.setTextColor(getResources().getColor(R.color.red));
    }

    public void sendFeedback(View view) {
        EditText feedback = findViewById(R.id.feedback);
        String feedbackContent = feedback.getText().toString();

            if (rating == 0) {
                Toast.makeText(this, "Please give a feedback", Toast.LENGTH_LONG).show();
                return;
            }

        Toast.makeText(FeedbackForm.this, "" + adId, Toast.LENGTH_LONG).show();

            DatabaseOpenHelper dbo = new DatabaseOpenHelper(this);
            AdPoster a = dbo.getAdPoster();

            Call<Feedback> call = ApiClient.connect().addFeedback(
                    feedbackContent, rating, adId, a.getAuth()
            );
            call.enqueue(new Callback<Feedback>() {
                @Override
                public void onResponse(Call<Feedback> call, Response<Feedback> response) {
                    if (!response.isSuccessful()) {
                        Toast.makeText(FeedbackForm.this, "" + response.code(), Toast.LENGTH_LONG).show();
                        return;
                    }

                    Feedback ad = response.body();
                    assert ad != null;
                    if (Boolean.parseBoolean(ad.getStatus())) {
                       finish();
                    } else {
                        Toast.makeText(FeedbackForm.this, "You have added a feedback", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Feedback> call, Throwable t) {
                    Toast.makeText(FeedbackForm.this, t.toString(), Toast.LENGTH_LONG).show();
                }
            });

    }

    public void goBack(View view) {
        finish();
    }
}
