package com.example.finder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;

import adapters.FindDetailPagerAdapter;
import others.BottomAppBarEvent;

public class FindDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_detail);

        Intent intent = getIntent();
        String category = intent.getStringExtra("title");
        TextView pageName = findViewById(R.id.page_name);
        pageName.setText(category);

        ImageView search = findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomAppBarEvent.goToSearchActivity(FindDetail.this);
            }
        });

        TabLayout tabLayout = findViewById(R.id.find_detail_tab_layout);

        final ViewPager viewPager = findViewById(R.id.find_detail_pager);
        FindDetailPagerAdapter findDetailPagerAdapter = new FindDetailPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), intent);
        viewPager.setAdapter(findDetailPagerAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener( new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    public void goToHomeActivity(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(FindDetail.this);
        bottomAppBarEvent.goToHomeActivity();
    }

    public void postAd(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(FindDetail.this);
        bottomAppBarEvent.postAd();
    }

    public void postFind(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(FindDetail.this);
        bottomAppBarEvent.postFind();
    }

    public void goToProfileActivity(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(FindDetail.this);
        bottomAppBarEvent.goToProfileActivity();
    }

    public void goToMessageListActivity(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(FindDetail.this);
        bottomAppBarEvent.goToMessageListActivity();
    }
}
