package com.johnnyup.nairarefill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import adapters.AdsRecyclerViewAdapter;
import data.Ads;
import others.BottomAppBarEvent;
import retrofit.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllAds extends AppCompatActivity {

    List<Ads> adsList = new ArrayList<>();
    AdsRecyclerViewAdapter adsRecyclerViewAdapter;
    ProgressBar progressBar;

    Boolean isScrolling = true;
    GridLayoutManager manager;
    int currentItems, totalItems, scrollOutItems;
    int adCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_ads);

        ImageView search = findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomAppBarEvent.goToSearchActivity(AllAds.this);
            }
        });

        TextView pageName = findViewById(R.id.page_name);
        pageName.setText(R.string.ads);

        progressBar = findViewById(R.id.progress_circular);
        showAds();

        final RecyclerView adview = findViewById(R.id.ad_list);
        adsRecyclerViewAdapter = new AdsRecyclerViewAdapter(AllAds.this, adsList);
        manager = new GridLayoutManager(AllAds.this, 1);
        adview.setLayoutManager(manager);
        adview.setAdapter(adsRecyclerViewAdapter);

        adview.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                                adCount = adCount + 10;
                                showAds();
                            }
                        }, 5000);
                    }
                }
            }
        });
    }

    private void showAds() {
        progressBar.setVisibility(View.VISIBLE);
        Call<List<Ads>> call = ApiClient.connect().getAds(adCount);
        call.enqueue(new Callback<List<Ads>>() {
            @Override
            public void onResponse(@NonNull Call<List<Ads>> call, @NonNull Response<List<Ads>> response) {
                if(!response.isSuccessful()) {
                    Toast.makeText(AllAds.this, response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                List<Ads> ads = response.body();

                assert ads != null;
                if(ads.size() == 0) progressBar.setVisibility(View.INVISIBLE);

                for(Ads ad : ads) {
                    adsList.add(new Ads(ad.getDescription(), ad.getTitle(), ad.getPrice(), ad.getViews(), ad.getLikes(), ad.getId()));
                }

                adsRecyclerViewAdapter.setData(adsList);
            }

            @Override
            public void onFailure(@NonNull Call<List<Ads>> call, @NonNull Throwable t) {
            }
        });
    }

    public void goBack(View view) {
        finish();
    }

    public void goToHomeActivity(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(AllAds.this);
        bottomAppBarEvent.goToHomeActivity();
    }

    public void postAd(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(AllAds.this);
        bottomAppBarEvent.postAd();
    }

    public void postFind(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(AllAds.this);
        bottomAppBarEvent.postFind();
    }

    public void goToProfileActivity(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(AllAds.this);
        bottomAppBarEvent.goToProfileActivity();
    }

    public void goToMessageListActivity(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(AllAds.this);
        bottomAppBarEvent.goToMessageListActivity();
    }

}
