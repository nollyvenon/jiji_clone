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

import adapters.FindsRecyclerViewAdapter;
import data.Finds;
import others.BottomAppBarEvent;
import retrofit.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllFind extends AppCompatActivity {

    List<Finds> findsList = new ArrayList<>();
    FindsRecyclerViewAdapter findsRecyclerViewAdapter;
    ProgressBar progressBar;

    Boolean isScrolling = true;
    GridLayoutManager manager;
    int currentItems, totalItems, scrollOutItems;
    int adCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_find);

        ImageView search = findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomAppBarEvent.goToSearchActivity(AllFind.this);
            }
        });

        TextView pageName = findViewById(R.id.page_name);
        pageName.setText(R.string.finds);

        RecyclerView findView = findViewById(R.id.find_list);
        findsRecyclerViewAdapter = new FindsRecyclerViewAdapter(AllFind.this, findsList);
        manager = new GridLayoutManager(this, 1);
        findView.setLayoutManager(manager);
        findView.setAdapter(findsRecyclerViewAdapter);

        progressBar = findViewById(R.id.progress_circular);
        showFinds();

        findView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                                showFinds();
                            }
                        }, 5000);
                    }
                }
            }
        });
    }

    private void showFinds() {
        progressBar.setVisibility(View.VISIBLE);
        Call<List<Finds>> call = ApiClient.connect().getFinds(adCount);
        call.enqueue(new Callback<List<Finds>>() {
            @Override
            public void onResponse(@NonNull Call<List<Finds>> call, @NonNull Response<List<Finds>> response) {
                if(!response.isSuccessful()) {
                    Toast.makeText(AllFind.this, response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                List<Finds> finds = response.body();
                assert finds != null;
                if(finds.size() == 0) progressBar.setVisibility(View.INVISIBLE);

                for(Finds find : finds) {
                    findsList.add(new Finds(find.getId(), find.getPrice(), find.getDescription(), find.getTitle(), find.getChatChoice(),
                            find.getCategory(), find.getBenefit(), find.getCreatedAt(), find.getAttachment(),
                            find.getBidEnd(), find.getAuth()));
                }

                findsRecyclerViewAdapter.setData(findsList);
            }

            @Override
            public void onFailure(@NonNull Call<List<Finds>> call, @NonNull Throwable t) {

            }
        });

    }

    public void goBack(View view) {
        finish();
    }

    public void goToHomeActivity(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(AllFind.this);
        bottomAppBarEvent.goToHomeActivity();
    }

    public void postAd(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(AllFind.this);
        bottomAppBarEvent.postAd();
    }

    public void postFind(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(AllFind.this);
        bottomAppBarEvent.postFind();
    }

    public void goToProfileActivity(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(AllFind.this);
        bottomAppBarEvent.goToProfileActivity();
    }

    public void goToMessageListActivity(View view) {
        BottomAppBarEvent bottomAppBarEvent = new BottomAppBarEvent(AllFind.this);
        bottomAppBarEvent.goToMessageListActivity();
    }

}
