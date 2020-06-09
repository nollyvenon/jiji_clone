package com.example.finder;

import androidx.annotation.NonNull;
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

    String adId, type;
    int rating = 0;
    Button excellence, good, neutral, fair, poor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_form);

        Intent intent = getIntent();
        adId = intent.getStringExtra("id");
        if(intent.getStringExtra("type") != null) {
            type = intent.getStringExtra("type");
        } else {
            type = "";
        }

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
        final Button btn = findViewById(R.id.send_feedback_btn);
        EditText feedback = findViewById(R.id.feedback);
        String feedbackContent = feedback.getText().toString();

        if (rating == 0) {
            Toast.makeText(this, "Please select rating", Toast.LENGTH_LONG).show();
            return;
        }

        view.setClickable(false);
        btn.setText(R.string.submitting);

        DatabaseOpenHelper dbo = new DatabaseOpenHelper(this);
        AdPoster a = dbo.getAdPoster();

        if(a.getAuth() == null) return;

        Call<Feedback> call = ApiClient.connect().addFeedback(
                feedbackContent, rating, adId, type, a.getAuth()
        );
        call.enqueue(new Callback<Feedback>() {
            @Override
            public void onResponse(@NonNull Call<Feedback> call, @NonNull Response<Feedback> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(FeedbackForm.this, "" + response.code(), Toast.LENGTH_LONG).show();

                    view.setClickable(true);
                    btn.setText(R.string.send_feedback);
                    return;
                }

                Feedback ad = response.body();
                assert ad != null;
                if (Boolean.parseBoolean(ad.getStatus())) {
                    Toast.makeText(FeedbackForm.this, "Submitted", Toast.LENGTH_LONG).show();
                    if(!type.equals("")) {
                        Intent intent = new Intent(FeedbackForm.this, MainActivity.class);
                        startActivity(intent);
                        return;
                    }
                    finish();
                } else {
                    Toast.makeText(FeedbackForm.this, "You have added a feedback", Toast.LENGTH_LONG).show();
                }

                view.setClickable(true);
                btn.setText(R.string.send_feedback);
            }

            @Override
            public void onFailure(@NonNull Call<Feedback> call, @NonNull Throwable t) {
                Toast.makeText(FeedbackForm.this, t.toString(), Toast.LENGTH_LONG).show();
                view.setClickable(true);
                btn.setText(R.string.send_feedback);
            }
        });

    }

    public void goBack(View view) {
        if(!type.equals("")) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return;
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(!type.equals("")) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return;
        }
        finish();
    }
}
