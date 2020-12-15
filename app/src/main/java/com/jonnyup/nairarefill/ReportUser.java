package com.jonnyup.nairarefill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

public class ReportUser extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_user);
    }

    public void sendReport(View view) {
        final Button btn = findViewById(R.id.report_user_btn);
        Intent intent = getIntent();
        String reportedId = intent.getStringExtra("reportedId");
        EditText report = findViewById(R.id.report);
        String reportContent = report.getText().toString();

        if (reportContent.isEmpty()) {
            Toast.makeText(this, "Report field is compulsory", Toast.LENGTH_LONG).show();
            return;
        }

        view.setClickable(false);
        btn.setText(R.string.submitting);

        DatabaseOpenHelper dbo = new DatabaseOpenHelper(this);
        AdPoster a = dbo.getAdPoster();

        if(a.getAuth() == null) {
            finish();
            return;
        }

        Call<Feedback> call = ApiClient.connect().addReport(
                reportContent, reportedId, a.getAuth()
        );
        call.enqueue(new Callback<Feedback>() {
            @Override
            public void onResponse(@NonNull Call<Feedback> call, @NonNull Response<Feedback> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(ReportUser.this, "" + response.code(), Toast.LENGTH_LONG).show();

                    view.setClickable(true);
                    btn.setText(R.string.send_report);
                    return;
                }

                Feedback ad = response.body();
                assert ad != null;
                if (Boolean.parseBoolean(ad.getStatus())) {
                    Toast.makeText(ReportUser.this, "Submitted", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(ReportUser.this, "Error in submission", Toast.LENGTH_LONG).show();
                }


                view.setClickable(true);
                btn.setText(R.string.send_report);
            }

            @Override
            public void onFailure(@NonNull Call<Feedback> call, @NonNull Throwable t) {
                Toast.makeText(ReportUser.this, t.toString(), Toast.LENGTH_LONG).show();

                view.setClickable(true);
                btn.setText(R.string.send_report);
            }
        });

    }

    public void goBack(View view) {
        finish();
    }
}
