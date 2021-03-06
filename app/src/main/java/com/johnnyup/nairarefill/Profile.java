package com.johnnyup.nairarefill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

import Database.DatabaseOpenHelper;
import adapters.ProfilePagerAdapter;
import data.AdPoster;
import data.Finds;
import de.hdodenhof.circleimageview.CircleImageView;
import others.BottomAppBarEvent;
import others.Constants;
import retrofit.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Profile extends AppCompatActivity {

    CircleImageView profileImage;
    TextView location, businessName, views;
    RatingBar rating;

    Intent intent;
    String auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        findViewById(R.id.bottom_appbar).setVisibility(View.GONE);

        checkFinds();

        TabLayout tabLayout = findViewById(R.id.profile_tab_layout);

        ImageView bottomBarImg = findViewById(R.id.img_profile_bottom_bar);
        TextView bottomBarTitle = findViewById(R.id.title_profile_bottom_bar);
        bottomBarImg.setColorFilter(getResources().getColor(R.color.colorPrimary));
        bottomBarTitle.setTextColor(getResources().getColor(R.color.colorPrimary));

        DatabaseOpenHelper dbo = new DatabaseOpenHelper(this);
        AdPoster dba = dbo.getAdPoster();
        auth = dba.getAuth();

        ImageView search = findViewById(R.id.search);
        search.setOnClickListener(v -> BottomAppBarEvent.goToSearchActivity(Profile.this));

        initview();
        intent = getIntent();
        getUser();

        profileImage.setImageResource(R.drawable.avatar);
        final ViewPager viewPager = findViewById(R.id.profile_pager);

        ProfilePagerAdapter pagerAdapter = new ProfilePagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), intent);
        viewPager.setAdapter(pagerAdapter);

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
        if (intent.getStringExtra("id") != null) {
            //TabLayout findTab = findViewById(R.id.profile_tab_layout);
//            ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(2).setVisibility(View.GONE);
//            ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(3).setVisibility(View.GONE);
            findViewById(R.id.bottom_appbar).setVisibility(View.GONE);
        }
    }

    private void initview() {
        profileImage = findViewById(R.id.profile_image);
        location = findViewById(R.id.location);
        businessName = findViewById(R.id.company_name);
        rating = findViewById(R.id.rating);
        views = findViewById(R.id.views);
    }

    private void getUser() {
        DatabaseOpenHelper dbo = new DatabaseOpenHelper(this);
        AdPoster a = dbo.getAdPoster();

        Call<AdPoster> call;

        if (intent.getStringExtra("id") == null) {
            call = ApiClient.connect().getUserByAuth(a.getAuth());
        } else {
            call = ApiClient.connect().getUserById(intent.getStringExtra("id"), a.getAuth());
        }

        call.enqueue(new Callback<AdPoster>() {
            @Override
            public void onResponse(@NonNull Call<AdPoster> call, @NonNull Response<AdPoster> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(Profile.this, "" + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                final AdPoster adPoster = response.body();
                assert adPoster != null;

                Glide.with(Profile.this).load(Constants.BASE_URL + adPoster.getProfileImage()).into(profileImage);

                location.setText(adPoster.getLocation());
                businessName.setText(adPoster.getUsername());
                rating.setRating(Float.parseFloat(adPoster.getRating()));
                views.setText(adPoster.getProfileView());

                if(adPoster.getUserType().equalsIgnoreCase(Constants.FINDS)) {
                    rating.setVisibility(View.GONE);
                    views.setVisibility(View.GONE);
                    location.setVisibility(View.GONE);
                    businessName.setText(adPoster.getUsername());

                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(190,50,10,10);
                    businessName.setLayoutParams(params);

                }

                if(a.getAuth() != null && adPoster.getAuth() != null && auth.equals(adPoster.getAuth())) {

                    TextView edit = findViewById(R.id.edit);
                    edit.setVisibility(View.VISIBLE);
                    edit.setOnClickListener(v -> editProfile(adPoster.getAuth()));
                }

                assert auth != null;
                if(a.getAuth() != null && adPoster.getAuth() != null && !auth.equals(adPoster.getAuth())) {
                    Button startChat = findViewById(R.id.start_chat);
                    startChat.setVisibility(View.VISIBLE);
                    startChat.setOnClickListener(v -> goToMessagePanel(adPoster.getAuth()));
                }

            }

            @Override
            public void onFailure(@NonNull Call<AdPoster> call, @NonNull Throwable t) {
               // Toast.makeText(Profile.this, t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void checkFinds() {
        DatabaseOpenHelper dbo = new DatabaseOpenHelper(this);
        AdPoster a = dbo.getAdPoster();

        if(a.getAuth() == null) {
            findViewById(R.id.start_chat).setVisibility(View.GONE);
            return;
        };

        auth = a.getAuth() == null ? "" : a.getAuth();
        Call<List<Finds>> call = ApiClient.connect().getFindByAuth(auth, 0);
        call.enqueue(new Callback<List<Finds>>() {
            @Override
            public void onResponse(@NonNull Call<List<Finds>> call, @NonNull Response<List<Finds>> response) {
                if(!response.isSuccessful()) {
                    //Toast.makeText(getContext(), response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                List<Finds> finds = response.body();
                assert finds != null;
                if(finds == null || finds.size() < 1) {
                    findViewById(R.id.start_chat).setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(@NonNull Call<List<Finds>> call, @NonNull Throwable t) {
                findViewById(R.id.start_chat).setVisibility(View.GONE);
            }
        });
    }

    public void goToMessagePanel(String auth) {
        Intent intent = new Intent(this, MessagePanel.class);
        intent.putExtra("auth", auth);
        startActivity(intent);
    }

    public void editProfile(String auth) {
        DatabaseOpenHelper dbo = new DatabaseOpenHelper(this);
        AdPoster adPoster = dbo.getAdPoster();
        String userType = adPoster.getUserType();

        if(userType.equalsIgnoreCase(Constants.FINDS)) {
            Intent i =  new Intent(this, FindPosterRegistration.class);
            i.putExtra("auth", auth);
            i.putExtra("userType", Constants.FINDS);
            i.putExtra("hide", "hide");
            startActivity(i);
        } else {
            Intent i =  new Intent(this, AdPosterRegistration.class);
            i.putExtra("auth", auth);
            i.putExtra("userType", Constants.ADS);
            i.putExtra("hide", "hide");
            startActivity(i);
        }
    }

    public void goBack(View view) {
        finish();
    }

    public void goToHomeActivity(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(Profile.this);
        bottomAppBarEvent.goToHomeActivity();
    }

    public void postAd(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(Profile.this);
        bottomAppBarEvent.postAd();
    }

    public void postFind(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(Profile.this);
        bottomAppBarEvent.postFind();
    }

    public void goToProfileActivity(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(Profile.this);
        bottomAppBarEvent.goToProfileActivity();
    }

    public void goToMessageListActivity(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(Profile.this);
        bottomAppBarEvent.goToMessageListActivity();
    }
}
