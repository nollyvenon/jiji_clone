package com.example.finder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import Database.DatabaseOpenHelper;
import adapters.AdsRecyclerViewAdapter;
import adapters.CategoryListAdapter;
import adapters.FindsRecyclerViewAdapter;
import adapters.ViewPagerAdapter;
import data.AdPoster;
import data.Ads;
import data.Finds;
import data.CategoryListData;
import data.Slides;
import others.BottomAppBarEvent;
import retrofit.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "msg_notif";
    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;
    public String current = "ads";
    private BottomAppBarEvent bottomAppBarEvent;
    private TextView title, viewMore;
    private RecyclerView postView;
    private int totalSlide = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView bottomBarImg = findViewById(R.id.img_home_bottom_bar);
        TextView bottomBarTitle = findViewById(R.id.title_home_bottom_bar);
        bottomBarImg.setColorFilter(getResources().getColor(R.color.colorPrimary));
        bottomBarTitle.setTextColor(getResources().getColor(R.color.colorPrimary));
        title = findViewById(R.id.title);
        postView = findViewById(R.id.home_ads);

        viewMore = findViewById(R.id.view_more);

        final TextView msgCount = findViewById(R.id.unread_count);
        bottomAppBarEvent = new BottomAppBarEvent(MainActivity.this);

        this.showSlide();

        this.showCategories();

        Timer timer = new Timer();

        final Handler handler = new Handler();
        final Runnable slidee = () -> {
            if (viewPager == null) return;
            if (viewPager.getCurrentItem() == totalSlide - 1) {
                viewPager.setCurrentItem(0);
                return;
            }
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
        };

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(slidee);
            }
        }, 2000, 4000);

        timer.scheduleAtFixedRate(new RecentAdFind(), 2000, 10000);

        bottomAppBarEvent.getUnreadCount(msgCount);

        final Runnable unreadMessageCount = () -> bottomAppBarEvent.getUnreadCount(msgCount);


        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(unreadMessageCount);
            }
        }, 20000, 20000);

        EditText search = findViewById(R.id.search);
        search.setFocusable(false);
        search.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Search.class);
            startActivity(intent);
        });

        //getUpgradeButton();
    }

    private void showSlide() {
        Call<List<Slides>> call = ApiClient.connect().getSlides();
        call.enqueue(new Callback<List<Slides>>() {
            @Override
            public void onResponse(@NonNull Call<List<Slides>> call, @NonNull Response<List<Slides>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                List<Slides> slides = new ArrayList<>();
                List<Slides> sl = response.body();

                assert sl != null;

                totalSlide = sl.size();
                for (Slides s : sl) {
                    slides.add(new Slides(s.getImage()));
                }

                viewPager = findViewById(R.id.home_viewpager);
                viewPagerAdapter = new ViewPagerAdapter(MainActivity.this, slides);
                viewPager.setAdapter(viewPagerAdapter);
            }

            @Override
            public void onFailure(@NonNull Call<List<Slides>> call, @NonNull Throwable t) {

            }
        });

    }

//    public void upgradeToPremium(View view) {
//        DatabaseOpenHelper dbo = new DatabaseOpenHelper(MainActivity.this);
//        AdPoster a = dbo.getAdPoster();
//        Intent intent = new Intent(MainActivity.this, Paystack.class);
//        intent.putExtra("auth", a.getAuth());
//        startActivity(intent);
//    }

//    private void getUpgradeButton() {
//        DatabaseOpenHelper dbo = new DatabaseOpenHelper(MainActivity.this);
//        AdPoster a = dbo.getAdPoster();
//        MaterialButton btn = findViewById(R.id.upgrade_to_premium_btn);
//
//        Call<AdPoster> call = ApiClient.connect().getUserByAuth(a.getAuth());
//        call.enqueue(new Callback<AdPoster>() {
//            @Override
//            public void onResponse(@NonNull Call<AdPoster> call, @NonNull Response<AdPoster> response) {
//                if (!response.isSuccessful()) {
//                    Toast.makeText(MainActivity.this, "" + response.code(), Toast.LENGTH_LONG).show();
//                    return;
//                }
//
//                AdPoster adPoster = response.body();
//                assert adPoster != null;
//                if (adPoster.getIsPremium().equals("0") && !adPoster.getBusinessName().equals("")) {
//                    btn.setVisibility(View.VISIBLE);
//                } else {
//                    btn.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<AdPoster> call, @NonNull Throwable t) {
//                //Toast.makeText(MainActivity.this, t.toString(), Toast.LENGTH_LONG).show();
//            }
//        });
//    }

    private void showFinds() {
        final ProgressBar progressBar = findViewById(R.id.progress_circular);
        Call<List<Finds>> call = ApiClient.connect().getRecentFinds();
        call.enqueue(new Callback<List<Finds>>() {
            @Override
            public void onResponse(@NonNull Call<List<Finds>> call, @NonNull Response<List<Finds>> response) {
                if (!response.isSuccessful()) {
                    //Toast.makeText(MainActivity.this, response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                List<Finds> findsList = new ArrayList<>();
                List<Finds> finds = response.body();

                assert finds != null;
                for (Finds find : finds) {
                    findsList.add(new Finds(find.getId(), find.getPrice(), find.getDescription(), find.getTitle(), find.getChatChoice(),
                            find.getCategory(), find.getBenefit(), find.getCreatedAt(), find.getAttachment(),
                            find.getBidEnd(), find.getAuth()));
                }
                progressBar.setVisibility(View.INVISIBLE);

                title.setText(R.string.finds);
                FindsRecyclerViewAdapter findsRecyclerViewAdapter = new FindsRecyclerViewAdapter(MainActivity.this, findsList);
                postView.setLayoutManager(new GridLayoutManager(MainActivity.this, 1));
                postView.setAdapter(findsRecyclerViewAdapter);
                current = "ads";
                viewMore.setOnClickListener(v -> viewAllFind(v));
            }

            @Override
            public void onFailure(@NonNull Call<List<Finds>> call, @NonNull Throwable t) {

            }
        });

    }

    private void showAds() {
        final ProgressBar progressBar = findViewById(R.id.progress_circular);

        Call<List<Ads>> call = ApiClient.connect().getRecentAds();
        call.enqueue(new Callback<List<Ads>>() {
            @Override
            public void onResponse(@NonNull Call<List<Ads>> call, @NonNull Response<List<Ads>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                List<Ads> adsList = new ArrayList<>();
                List<Ads> ads = response.body();

                assert ads != null;

                for (Ads ad : ads) {
                    adsList.add(new Ads(ad.getDescription(), ad.getTitle(), ad.getPrice(), ad.getViews(), ad.getLikes(), ad.getId()));
                }

                progressBar.setVisibility(View.INVISIBLE);

                title.setText(R.string.ads);
                AdsRecyclerViewAdapter adsRecyclerViewAdapter = new AdsRecyclerViewAdapter(MainActivity.this, adsList);
                postView.setLayoutManager(new GridLayoutManager(MainActivity.this, 1));
                postView.setAdapter(adsRecyclerViewAdapter);
                current = "finds";
                viewMore.setOnClickListener(v -> viewAllAd(v));
            }

            @Override
            public void onFailure(@NonNull Call<List<Ads>> call, @NonNull Throwable t) {
            }
        });
    }

    private void showCategories() {
        final ProgressBar progressBar = findViewById(R.id.progress_circular);
        Call<List<CategoryListData>> call = ApiClient.connect().getCategories();
        call.enqueue(new Callback<List<CategoryListData>>() {
            @Override
            public void onResponse(@NonNull Call<List<CategoryListData>> call, @NonNull Response<List<CategoryListData>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "" + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                RecyclerView categoryItem = findViewById(R.id.home_category);
                List<CategoryListData> categories = response.body();

                List<CategoryListData> categoryListData = new ArrayList<>();
                assert categories != null;
                for (CategoryListData category : categories) {
                    categoryListData.add(new CategoryListData(category.getId(), category.getImage(), category.getName(), category.getCount()));
                }

                progressBar.setVisibility(View.INVISIBLE);

                CategoryListAdapter categoryListAdapter = new CategoryListAdapter(MainActivity.this, categoryListData, "home");
                categoryItem.setLayoutManager(new GridLayoutManager(MainActivity.this, 3));
                categoryItem.setAdapter(categoryListAdapter);
            }

            @Override
            public void onFailure(@NonNull Call<List<CategoryListData>> call, @NonNull Throwable t) {
            }
        });
    }

    public void viewAllFind(View view) {
        Intent intent = new Intent(this, AllFind.class);
        startActivity(intent);
    }

    public void viewAllAd(View view) {
        Intent intent = new Intent(this, AllAds.class);
        startActivity(intent);
    }

    public void goToHomeActivity(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(MainActivity.this);
        bottomAppBarEvent.goToHomeActivity();
    }

    public void viewCategoryPage(View view) {
        Intent intent = new Intent(MainActivity.this, CategoryList.class);
        startActivity(intent);
    }

    public void postAd(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(MainActivity.this);
        bottomAppBarEvent.postAd();
    }

    public void postFind(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(MainActivity.this);
        bottomAppBarEvent.postFind();
    }

    public void goToProfileActivity(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(MainActivity.this);
        bottomAppBarEvent.goToProfileActivity();
    }

    public void goToMessageListActivity(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(MainActivity.this);
        bottomAppBarEvent.goToMessageListActivity();
    }

    public class RecentAdFind extends TimerTask {

        @Override
        public void run() {
            MainActivity.this.runOnUiThread(() -> {
                if (current.equals("ads")) {
                    showAds();
                    postView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_into_view));
                } else {
                    showFinds();
                    postView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_into_view));
                }
            });
        }
    }
}

