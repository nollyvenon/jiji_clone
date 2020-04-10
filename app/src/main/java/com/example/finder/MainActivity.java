package com.example.finder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import adapters.AdsRecyclerViewAdapter;
import adapters.CategoryListAdapter;
import adapters.FindsRecyclerViewAdapter;
import adapters.ViewPagerAdapter;
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

    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;
    public String current = "ads";
    TextView title, viewMore;
    RecyclerView postView;

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

        this.showSlide();

        this.showCategories();
        //this.showFinds();

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new MyTimerTask(), 2000, 4000);

        timer.scheduleAtFixedRate(new RecentAdFind(), 2000, 10000);

        EditText search = findViewById(R.id.search);
        search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    Intent intent = new Intent(MainActivity.this, Search.class);
                    startActivity(intent);
                }
            }
        });

    }

    private void showSlide() {

        Call<List<Slides>> call = ApiClient.connect().getSlides();
        call.enqueue(new Callback<List<Slides>>() {
            @Override
            public void onResponse(@NonNull Call<List<Slides>> call, @NonNull Response<List<Slides>> response) {
                if(!response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, response.code(), Toast.LENGTH_LONG).show();
                    return;
                }


                List<Slides> slides = new ArrayList<>();
                List<Slides> sl = response.body();

                assert sl != null;

                for(Slides s : sl) {
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

    private void showFinds() {

        Call<List<Finds>> call = ApiClient.connect().getRecentFinds();
        call.enqueue(new Callback<List<Finds>>() {
            @Override
            public void onResponse(@NonNull Call<List<Finds>> call, @NonNull Response<List<Finds>> response) {
                if(!response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                List<Finds> findsList = new ArrayList<>();
                List<Finds> finds = response.body();

                assert finds != null;
                for(Finds find : finds) {
                    findsList.add(new Finds(find.getId(), find.getPrice(), find.getDescription(), find.getTitle(),
                            find.getCategory(), find.getBenefit(), find.getCreatedAt(), find.getBidEnd(), find.getAuth()));
                }

                title.setText(R.string.finds);
                FindsRecyclerViewAdapter findsRecyclerViewAdapter = new FindsRecyclerViewAdapter(MainActivity.this, findsList);
                postView.setLayoutManager(new GridLayoutManager(MainActivity.this, 1));
                postView.setAdapter(findsRecyclerViewAdapter);
                current = "ads";
                viewMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewAllFind(v);
                    }
                });
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
                if(!response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                List<Ads> adsList = new ArrayList<>();
                List<Ads> ads = response.body();

                assert ads != null;
                if(ads.size() == 0) progressBar.setVisibility(View.INVISIBLE);

                for(Ads ad : ads) {
                    adsList.add(new Ads(ad.getDescription(), ad.getTitle(), ad.getPrice(), ad.getViews(), ad.getLikes(), ad.getId()));
                }

                title.setText(R.string.ads);
                AdsRecyclerViewAdapter adsRecyclerViewAdapter = new AdsRecyclerViewAdapter(MainActivity.this, adsList);
                postView.setLayoutManager(new GridLayoutManager(MainActivity.this, 1));
                postView.setAdapter(adsRecyclerViewAdapter);
                current = "finds";
                viewMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewAllAd(v);
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call<List<Ads>> call, @NonNull Throwable t) {
            }
        });
    }

    private void showCategories() {
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
                for(CategoryListData category : categories) {
                    categoryListData.add(new CategoryListData(category.getId(), category.getImage(), category.getName(), category.getCount()));
                }
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

    public class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(viewPager == null) return;
                    if(viewPager.getCurrentItem() == 0) {
                        viewPager.setCurrentItem(1);
                    } else if(viewPager.getCurrentItem() == 1) {
                        viewPager.setCurrentItem(2);
                    } else {
                        viewPager.setCurrentItem(0);
                    }
                }
            });
        }
    }

    public class RecentAdFind extends TimerTask {

        @Override
        public void run() {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    postView.animate().translationY(0f).alpha(0.9f).setDuration(700);
                    if(current.equals("ads")) {
                        showAds();
                        postView.animate().translationY(-30).alpha(0.9f).setDuration(12000);
                    } else {
                        showFinds();
                    }
                }
            });
        }
    }
}

