package com.johnnyup.nairarefill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import data.Finds;
import retrofit.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeepLink extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deep_link);

        ProgressDialog progDialog = ProgressDialog.show(this, "Loading", "Please wait...", true);
        progDialog.setCancelable(false);

        Intent intent = getIntent();
        Uri data = intent.getData();

        assert data != null;
        String url = data.toString();

        String[] urlAry = url.split("/");
        String page = urlAry[urlAry.length - 2];
        String pId = urlAry[urlAry.length - 1];

        if(page.equals("ads")) {
            Intent intent1 = new Intent(this, AdDetail.class);
            intent1.putExtra("id", pId);
            intent1.putExtra("back", "back");
            startActivity(intent1);
        } else if(page.equals("finds")) {
            getFind(Integer.parseInt(pId));
        }

    }

    private void getFind(int id) {
        Call<Finds> call = ApiClient.connect().getFind(id);
        call.enqueue(new Callback<Finds>() {
            @Override
            public void onResponse(@NonNull Call<Finds> call, @NonNull Response<Finds> response) {
                if(!response.isSuccessful()) {
                    Intent intent = new Intent(DeepLink.this, MainActivity.class);
                    startActivity(intent);
                }

                Finds finds = response.body();
                assert finds != null;

                Intent intent = new Intent(DeepLink.this, FindDetail.class);
                intent.putExtra("id", finds.getId());
                intent.putExtra("title", finds.getTitle());
                intent.putExtra("price", finds.getPrice());
                intent.putExtra("description", finds.getDescription());
                intent.putExtra("name", finds.getFinderName()); //
                intent.putExtra("timeLeft", finds.getCreatedAt());
                intent.putExtra("bidEnd", finds.getBidEnd());
                intent.putExtra("category", finds.getCategory());
                intent.putExtra("promotion", finds.getBenefit());
                intent.putExtra("attachment", finds.getAttachment());
                intent.putExtra("chatChoice", finds.getChatChoice());
                intent.putExtra("auth", finds.getAuth());
                intent.putExtra("back", "back");
                startActivity(intent);

            }

            @Override
            public void onFailure(@NonNull Call<Finds> call, @NonNull Throwable t) {

            }
        });

    }
}
