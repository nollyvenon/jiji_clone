package com.example.finder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import Database.DatabaseFinderHelper;
import Database.DatabasePhoneHelper;
import adapters.AdsRecyclerViewAdapter;
import adapters.CategoryListAdapter;
import adapters.FindsRecyclerViewAdapter;
import adapters.ViewPagerAdapter;
import data.Ads;
import data.FindPoster;
import data.Finds;
import data.CategoryListData;
import data.VerifiedPhoneNumber;
import others.BottomAppBarEvent;

public class MainActivity extends AppCompatActivity {

    ViewPagerAdapter viewPagerAdapter;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.home_viewpager);
        int[] viewPagerImages = {R.drawable.bg2, R.drawable.bg3, R.drawable.bg4};
        viewPagerAdapter = new ViewPagerAdapter(this, viewPagerImages);
        viewPager.setAdapter(viewPagerAdapter);

        this.showCategories();
        this.showAds();
        this.showFinds();

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new MyTimerTask(), 2000, 4000);

    }

    private void showFinds() {
        List<Finds> findsList = new ArrayList<>();
        findsList.add(new Finds("25,000",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et \n" +
                        "\"dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut",
                "OnyxDatasystems", "Furniture", "I need a furniture maker", "Free Delivery", "2"));
        findsList.add(new Finds( "25,000",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et \n" +
                        "\"dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut",
                "OnyxDatasystems", "Software Developer", "I need an OpenCart website for my shop",
                "Free Delivery",
                "2"));
        findsList.add(new Finds("25,000",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et \n" +
                        "\"dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut",
                "OnyxDatasystems", "Transport", "Mass transport service needed",
                "Free Delivery", "2"));
        findsList.add(new Finds("25,000",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et \n" +
                        "\"dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut",
                "OnyxDatasystems", "Marketing", "A marketer needed at Ilogbo",
                "Free Delivery", "2"));
        findsList.add(new Finds("25,000",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et \n" +
                        "\"dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut",
                "OnyxDatasystems", "Furniture", "I need a furniture maker", "Free Delivery", "2"));

        RecyclerView findView = findViewById(R.id.home_finds);
        FindsRecyclerViewAdapter findsRecyclerViewAdapter = new FindsRecyclerViewAdapter(this, findsList);
        findView.setLayoutManager(new GridLayoutManager(this, 1));
        findView.setAdapter(findsRecyclerViewAdapter);
    }

    private void showAds() {
        List<Ads> adsList = new ArrayList<>();
        adsList.add(new Ads("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et" +
                        "\"dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut",
                "Shopify Developer", "6000", "107","22"));

        adsList.add(new Ads("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et" +
                "\"dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut",
                "Shopify Developer", "6000", "107","22"));

        adsList.add(new Ads("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et" +
                "\"dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut",
                "Shopify Developer", "6000", "107","22"));

        adsList.add(new Ads("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et" +
                "\"dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut",
                "Shopify Developer", "6000", "107","22"));

        adsList.add(new Ads("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et" +
                "\"dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut",
                "Shopify Developer", "6000", "107","22"));

        RecyclerView adview = findViewById(R.id.home_ads);
        AdsRecyclerViewAdapter adsRecyclerViewAdapter = new AdsRecyclerViewAdapter(this, adsList);
        adview.setLayoutManager(new GridLayoutManager(this, 1));
        adview.setAdapter(adsRecyclerViewAdapter);
    }

    private void showCategories() {
        RecyclerView categoryItem = findViewById(R.id.home_category);
        List<CategoryListData> categoryListData = new ArrayList<>();
        categoryListData.add(new CategoryListData(R.drawable.bg2, "Healthcare Community", "6,732,499"));
        categoryListData.add(new CategoryListData(R.drawable.bg2, "Sales Community", "3,304,539"));
        categoryListData.add(new CategoryListData(R.drawable.bg2, "Management & Business Community", "3,305,383"));
        categoryListData.add(new CategoryListData(R.drawable.bg2, "Technology Community", "3,005,720"));
        categoryListData.add(new CategoryListData(R.drawable.bg2, "Finance Community", "2,792,280"));
        CategoryListAdapter categoryListAdapter = new CategoryListAdapter(this, categoryListData);
        categoryItem.setLayoutManager(new GridLayoutManager(this, 1));
        categoryItem.setAdapter(categoryListAdapter);
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
}

