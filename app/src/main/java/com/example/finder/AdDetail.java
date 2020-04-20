package com.example.finder;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import Database.DatabaseOpenHelper;
import adapters.ProfileReviewAdapter;
import data.AdPoster;
import data.Ads;
import data.Feedback;
import data.Finds;
import others.BottomAppBarEvent;
import others.Constants;
import retrofit.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdDetail extends AppCompatActivity {

    private List<Feedback> fbList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    ImageView profileImage;
    TextView location, title, businessName, price, views, description, promotions, likes;
    RatingBar rating;

    Intent intent;
    String auth;

    private ProfileReviewAdapter adapter;
    private Boolean isScrolling = true;
    private GridLayoutManager manager;
    private int currentItems, totalItems, scrollOutItems;
    private int adCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_detail);

        ImageView search = findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomAppBarEvent.goToSearchActivity(AdDetail.this);
            }
        });

        recyclerView = findViewById(R.id.profile_review);
        adapter = new ProfileReviewAdapter(AdDetail.this, fbList);
        manager = new GridLayoutManager(AdDetail.this, 1);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        progressBar = findViewById(R.id.progress_circular);

        initview();
        intent = getIntent();
        getUser();

        showFeedbacks();
        onScrollFeedback();
    }

    private void initview() {
        profileImage = findViewById(R.id.profile_image);
        location = findViewById(R.id.location);
        businessName = findViewById(R.id.company_name);
        title = findViewById(R.id.title);
        price = findViewById(R.id.price);
        views = findViewById(R.id.views);
        description = findViewById(R.id.description);
        promotions = findViewById(R.id.promotion);
        likes = findViewById(R.id.likes);
        rating = findViewById(R.id.rating);
    }

    public void goBack(View view) {
        finish();
    }

    private void getUser() {
        DatabaseOpenHelper dbo = new DatabaseOpenHelper(this);
        AdPoster a = dbo.getAdPoster();
        auth = a.getAuth() == null ? "" : a.getAuth();

        Call<Ads> call = ApiClient.connect().getAdDetail(intent.getStringExtra("id"), auth);
        call.enqueue(new Callback<Ads>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onResponse(@NonNull Call<Ads> call, @NonNull Response<Ads> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(AdDetail.this, "" + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                final Ads ads = response.body();
                assert ads != null;

                Glide.with(AdDetail.this).load(Constants.BASE_URL + ads.getImage()).into(profileImage);
                location.setText(ads.getLocation());
                businessName.setText(ads.getBusinessName());
                description.setText(ads.getDescription());
                promotions.setText(ads.getBenefit());

                double dPrice = Double.parseDouble(ads.getPrice());
                NumberFormat format = new DecimalFormat("#,###");
                String fPrice = format.format(dPrice);
                price.setText(new StringBuilder().append("N").append(fPrice));

                views.setText(ads.getViews());
                title.setText(ads.getTitle());
                rating.setRating(Float.parseFloat(ads.getRating()));
                likes.setText(ads.getLikes());

                businessName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AdDetail.this, Profile.class);
                        intent.putExtra("id", ads.getAdId());
                        intent.putExtra("auth", ads.getAuth());
                        startActivity(intent);
                    }
                });

                if (ads.getLiked() != null && !ads.getLiked().isEmpty()) {
                    likes.getCompoundDrawables()[0].setTint(getApplicationContext().getResources().getColor(R.color.duskYellow));
                    likes.setTextColor(getApplicationContext().getResources().getColor(R.color.duskYellow));
                } else {
                    likes.getCompoundDrawables()[0].setTint(getApplicationContext().getResources().getColor(R.color.light_grey));
                    likes.setTextColor(getApplicationContext().getResources().getColor(R.color.light_grey));
                }

                if (auth.equals(ads.getAuth()) && !auth.equals("")) {
                    ImageView del = findViewById(R.id.delete);
                    del.setVisibility(View.VISIBLE);
                    del.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            BottomAppBarEvent.isRegistered(AdDetail.this);
                            deleteAd(v);
                        }
                    });
                }

                if (!auth.equals(ads.getAuth()) && !auth.equals("")) {
                    MaterialButton startChat = findViewById(R.id.start_chat);
                    TextView startChat2 = findViewById(R.id.contact_dealer);
                    startChat.setVisibility(View.VISIBLE);
                    startChat2.setVisibility(View.VISIBLE);
                    startChat.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            BottomAppBarEvent.isRegistered(AdDetail.this);
                            Intent intent = new Intent(AdDetail.this, MessagePanel.class);
                            intent.putExtra("aid", ads.getAdId());
                            intent.putExtra("fid", ads.getFindId());
                            intent.putExtra("uniqueId", ads.getAdId() + "-" + ads.getFindId());
                            startActivity(intent);
                        }
                    });

                    startChat2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            BottomAppBarEvent.isRegistered(AdDetail.this);
                            Intent intent = new Intent(AdDetail.this, MessagePanel.class);
                            intent.putExtra("aid", ads.getAdId());
                            intent.putExtra("fid", ads.getFindId());
                            intent.putExtra("uniqueId", ads.getAdId() + "-" + ads.getFindId());
                            startActivity(intent);
                        }
                    });
                }

            }

            @Override
            public void onFailure(@NonNull Call<Ads> call, @NonNull Throwable t) {
            }
        });
    }

    private void showFeedbacks() {
        Call<List<Feedback>> call =  ApiClient.connect().getAdFeedback(intent.getStringExtra("id"), adCount);
        call.enqueue(new Callback<List<Feedback>>() {
            @Override
            public void onResponse(@NonNull Call<List<Feedback>> call, @NonNull Response<List<Feedback>> response) {
                if(!response.isSuccessful()) {
                    Toast.makeText(AdDetail.this, response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                List<Feedback> fbs = response.body();
                assert fbList != null;
                if(fbList.size() == 0) progressBar.setVisibility(View.INVISIBLE);

                assert fbs != null;
                for(Feedback fb : fbs) {
                    fbList.add(new Feedback(fb.getUsername(), fb.getRating(), fb.getFeedback(), fb.getCreatedAt(), fb.getUserId(),
                            fb.getAdId(), fb.getStatus(), fb.getAuth(), fb.getTitle(), fb.getProfileImage(), fb.getFinderId()));
                }

                adapter.setData(fbList);
            }

            @Override
            public void onFailure(@NonNull Call<List<Feedback>> call, @NonNull Throwable t) {
            }
        });
    }

    private void onScrollFeedback() {
        progressBar = findViewById(R.id.progress_circular);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                                showFeedbacks();
                            }
                        }, 5000);
                    }
                }
            }
        });
    }

    public void goToFeedbackForm(View view) {
        Intent intent = new Intent(this, FeedbackForm.class);
        intent.putExtra("id", this.intent.getStringExtra("id"));
        startActivity(intent);
    }

    public void likeAd(View view) {
        BottomAppBarEvent.isRegistered(this);
        DatabaseOpenHelper dbo = new DatabaseOpenHelper(this);
        AdPoster a = dbo.getAdPoster();
        auth = a.getAuth();

        Call<Ads> call = ApiClient.connect().likeAd(intent.getStringExtra("id"), auth);
        call.enqueue(new Callback<Ads>() {
            @SuppressLint("NewApi")
            @Override
            public void onResponse(@NonNull Call<Ads> call, @NonNull Response<Ads> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(AdDetail.this, "" + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                final Ads ads = response.body();
                assert ads != null;

                if (Boolean.parseBoolean(ads.getStatus())) {
                    likes.getCompoundDrawables()[0].setTint(getApplicationContext().getResources().getColor(R.color.duskYellow));
                    likes.setTextColor(getApplicationContext().getResources().getColor(R.color.duskYellow));
                } else {
                    likes.getCompoundDrawables()[0].setTint(getApplicationContext().getResources().getColor(R.color.light_grey));
                    likes.setTextColor(getApplicationContext().getResources().getColor(R.color.light_grey));
                }

                likes.setText(ads.getLikes());

            }

            @Override
            public void onFailure(@NonNull Call<Ads> call, @NonNull Throwable t) {
            }
        });
    }

    public void deleteAd(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AdDetail.this);
        builder.setMessage("Delete this Ad?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Call<Ads> call = ApiClient.connect().deleteAd(intent.getStringExtra("id"));
                        call.enqueue(new Callback<Ads>() {
                            @Override
                            public void onResponse(@NonNull Call<Ads> call, @NonNull Response<Ads> response) {
                                if (!response.isSuccessful()) {
                                    Toast.makeText(AdDetail.this, "" + response.code(), Toast.LENGTH_LONG).show();
                                    return;
                                }

                                final Ads ads = response.body();
                                assert ads != null;

                                if (Boolean.parseBoolean(ads.getStatus())) {
                                    Intent intent = new Intent(AdDetail.this, MainActivity.class);
                                    startActivity(intent);
                                }

                            }

                            @Override
                            public void onFailure(@NonNull Call<Ads> call, @NonNull Throwable t) {
                            }
                        });
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog =  builder.create();
        alertDialog.show();
    }


    public void goToHomeActivity(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(AdDetail.this);
        bottomAppBarEvent.goToHomeActivity();
    }

    public void postAd(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(AdDetail.this);
        bottomAppBarEvent.postAd();
    }

    public void postFind(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(AdDetail.this);
        bottomAppBarEvent.postFind();
    }

    public void goToProfileActivity(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(AdDetail.this);
        bottomAppBarEvent.goToProfileActivity();
    }

    public void goToMessageListActivity(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(AdDetail.this);
        bottomAppBarEvent.goToMessageListActivity();
    }
}
