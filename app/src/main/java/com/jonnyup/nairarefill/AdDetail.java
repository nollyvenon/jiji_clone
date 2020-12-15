package com.jonnyup.nairarefill;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AbsListView;
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
    TextView location, title, businessName, price, views, description, promotions, likes, attachment;
    RatingBar rating;

    Intent intent;
    String auth = null;
    private boolean fromBrowser = false;

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
        checkFinds();

        if(intent.getStringExtra("back") != null) {
            fromBrowser = true;
        }

        findViewById(R.id.share).setOnClickListener(v -> {
            Intent intent1 = new Intent();
            intent1.setAction(Intent.ACTION_SEND);

            // change the type of data you need to share,
            // for image use "image/*"
            intent1.setType("text/plain");
            intent1.putExtra(Intent.EXTRA_TEXT, Constants.BASE_URL + "deeplink/ads/" + intent.getStringExtra("id"));
            startActivity(Intent.createChooser(intent1, "Share"));
        });
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
        attachment = findViewById(R.id.attachment);
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
                businessName.setText(ads.getUsername());
                description.setText(ads.getDescription());
                promotions.setText(ads.getBenefit());

                NumberFormat format = new DecimalFormat("#,###");
                double priceOne, priceTwo;
                String fPrice;

                if (!ads.getPrice().equals("")) {
                    if (ads.getPrice().contains("-")) {
                        String[] prices = ads.getPrice().split("-");
                        priceOne = Double.parseDouble(prices[0]);
                        priceTwo = Double.parseDouble(prices[1]);

                        String fPriceOne = format.format(priceOne);
                        String fPriceTwo = format.format(priceTwo);

                        fPrice = new StringBuilder().append("N").append(fPriceOne).append(" - ").append("N").append(fPriceTwo).toString();
                    } else {
                        priceOne = Double.parseDouble(ads.getPrice());
                        String fPriceOne = format.format(priceOne);
                        fPrice = new StringBuilder().append("N").append(fPriceOne).toString();
                    }
                    price.setText(fPrice);
                } else {
                    price.setText("N0.00");
                }

                views.setText(ads.getViews());
                title.setText(ads.getTitle());
                rating.setRating(ads.getRating() != null ? Float.parseFloat(ads.getRating()) : 0);
                likes.setText(ads.getLikes());

                businessName.setOnClickListener(v -> {
                    Intent intent = new Intent(AdDetail.this, Profile.class);
                    intent.putExtra("id", ads.getAdId());
                    intent.putExtra("auth", ads.getAuth());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                    startActivity(intent);
                    AdDetail.this.finish();
                });

                if(ads.getAttachment() != null && !ads.getAttachment().equals("")) {
                    //attachment.setText(ads.getAttachment());
                    attachment.setVisibility(View.VISIBLE);
                    attachment.setOnClickListener(v -> {
                        String googleDocsUrl = "http://docs.google.com/viewer?url=" + Constants.BASE_URL + ads.getAttachment();
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse(googleDocsUrl), "text/html");
                        startActivity(intent);
                    });
                }

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
                    del.setOnClickListener(v -> {
                        BottomAppBarEvent.isRegistered(AdDetail.this);
                        deleteAd(v);
                    });
                }

                if (!auth.equals(ads.getAuth()) && !auth.equals("")) {
                    MaterialButton startChat = findViewById(R.id.start_chat);
                    TextView startChat2 = findViewById(R.id.contact_dealer);
                    startChat.setVisibility(View.VISIBLE);
                    startChat2.setVisibility(View.VISIBLE);
                    findViewById(R.id.feedback).setVisibility(View.VISIBLE);
                    startChat.setOnClickListener(v -> {
                        BottomAppBarEvent.isRegistered(AdDetail.this);
                        Intent intent = new Intent(AdDetail.this, MessagePanel.class);
                        intent.putExtra("aid", ads.getAdId());
                        intent.putExtra("fid", ads.getFindId());
                        intent.putExtra("uniqueId", ads.getAdId() + "-" + ads.getFindId());
                        startActivity(intent);
                    });

                    startChat2.setOnClickListener(v -> {
                        BottomAppBarEvent.isRegistered(AdDetail.this);
                        Intent intent = new Intent(AdDetail.this, MessagePanel.class);
                        intent.putExtra("aid", ads.getAdId());
                        intent.putExtra("fid", ads.getFindId());
                        intent.putExtra("uniqueId", ads.getAdId() + "-" + ads.getFindId());
                        startActivity(intent);
                    });
                }

            }

            @Override
            public void onFailure(@NonNull Call<Ads> call, @NonNull Throwable t) {
            }
        });
    }

    private void showFeedbacks() {
        if(intent.getStringExtra("id") == null) return;
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
                        new Handler().postDelayed(() -> {
                            adCount = adCount + 10;
                            showFeedbacks();
                        }, 5000);
                    }
                }
            }
        });
    }

    public void goToFeedbackForm(View view) {
        if(auth.equals("")) return;
        Intent intent = new Intent(this, FeedbackForm.class);
        intent.putExtra("id", this.intent.getStringExtra("id"));
        startActivity(intent);
    }

    public void likeAd(View view) {
        BottomAppBarEvent.isRegistered(this);
        DatabaseOpenHelper dbo = new DatabaseOpenHelper(this);
        AdPoster a = dbo.getAdPoster();

        if(a.getAuth() == null) return;

        auth = a.getAuth() == null ? "" : a.getAuth();
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
                .setPositiveButton("Yes", (dialog, id) -> {
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
                                AdDetail.this.finish();
                            }

                        }

                        @Override
                        public void onFailure(@NonNull Call<Ads> call, @NonNull Throwable t) {
                        }
                    });
                })
                .setNegativeButton("No", (dialog, id) -> dialog.cancel());
        AlertDialog alertDialog =  builder.create();
        alertDialog.show();
    }

    private void checkFinds() {
        DatabaseOpenHelper dbo = new DatabaseOpenHelper(this);
        AdPoster a = dbo.getAdPoster();

        if(a.getAuth() == null) {
            findViewById(R.id.feedback).setVisibility(View.GONE);
            findViewById(R.id.contact_dealer).setVisibility(View.GONE);
            findViewById(R.id.start_chat).setVisibility(View.GONE);
            return;
        };

        Call<List<Finds>> call = ApiClient.connect().getFindByAuth(a.getAuth(), 0);
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
                    findViewById(R.id.feedback).setVisibility(View.GONE);
                    findViewById(R.id.contact_dealer).setVisibility(View.GONE);
                    findViewById(R.id.start_chat).setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(@NonNull Call<List<Finds>> call, @NonNull Throwable t) {
                findViewById(R.id.feedback).setVisibility(View.GONE);
                findViewById(R.id.contact_dealer).setVisibility(View.GONE);
                findViewById(R.id.start_chat).setVisibility(View.GONE);
            }
        });
    }

    public void goBack(View view) {
        if(fromBrowser) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return;
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        if(fromBrowser) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return;
        }
        finish();
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
