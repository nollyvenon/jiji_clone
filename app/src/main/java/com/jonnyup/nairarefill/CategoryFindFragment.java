package com.jonnyup.nairarefill;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import adapters.AdsRecyclerViewAdapter;
import adapters.FindsRecyclerViewAdapter;
import data.Ads;
import data.Finds;
import retrofit.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFindFragment extends Fragment {

    private List<Finds> findsList = new ArrayList<>();
    private FindsRecyclerViewAdapter adapter;
    private List<Ads> adsList = new ArrayList<>();
    private AdsRecyclerViewAdapter adsRecyclerViewAdapter;
    private ProgressBar progressBar;
    private RecyclerView findView;

    private Boolean isScrolling = true;
    private GridLayoutManager manager;
    private int currentItems, totalItems, scrollOutItems;
    private int adCount = 0;
    private String currentView = "ads";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        showFinds();

        // Inflate the layout for this fragment
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_category_find, container, false);
        adapter = new FindsRecyclerViewAdapter(getContext(), findsList);
        findView = linearLayout.findViewById(R.id.find_list);

        adsRecyclerViewAdapter = new AdsRecyclerViewAdapter(getContext(), adsList);

        MaterialButton adsBtn = linearLayout.findViewById(R.id.ads_btn);
        MaterialButton findsBtn = linearLayout.findViewById(R.id.finds_btn);

        adsBtn.setOnClickListener(v -> {
            adCount = 0;
            adsList.clear();
            adsBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            adsBtn.setTextColor(getResources().getColor(R.color.white));
            findsBtn.setBackgroundColor(getResources().getColor(R.color.white));
            findsBtn.setTextColor(getResources().getColor(R.color.colorPrimary));
            showAds();
        });

        findsBtn.setOnClickListener(v -> {
            adCount = 0;
            findsList = new ArrayList<>();
            findsBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            findsBtn.setTextColor(getResources().getColor(R.color.white));
            adsBtn.setBackgroundColor(getResources().getColor(R.color.white));
            adsBtn.setTextColor(getResources().getColor(R.color.colorPrimary));
            showFinds();
        });

        return linearLayout;
    }

    private void showFinds() {

        assert getArguments() != null;
        Call<List<Finds>> call = ApiClient.connect().getFindByCategory(getArguments().getString("id"), adCount);
        call.enqueue(new Callback<List<Finds>>() {
            @Override
            public void onResponse(@NonNull Call<List<Finds>> call, @NonNull Response<List<Finds>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                List<Finds> finds = response.body();
                assert finds != null;
                if (finds.size() == 0) progressBar.setVisibility(View.INVISIBLE);

                for (Finds find : finds) {
                    findsList.add(new Finds(find.getId(), find.getPrice(), find.getDescription(), find.getTitle(), find.getChatChoice(),
                            find.getCategory(), find.getBenefit(), find.getCreatedAt(), find.getAttachment(),
                            find.getBidEnd(), find.getAuth()));
                }

                adapter.setData(findsList);

                manager = new GridLayoutManager(getContext(), 1);
                findView.setLayoutManager(manager);
                findView.setAdapter(adapter);

                currentView = "finds";
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(@NonNull Call<List<Finds>> call, @NonNull Throwable t) {

            }
        });

    }

    private void showAds() {
        progressBar.setVisibility(View.VISIBLE);
        assert getArguments() != null;
        Call<List<Ads>> call = ApiClient.connect().getAdByCategory(getArguments().getString("id"), adCount);
        call.enqueue(new Callback<List<Ads>>() {
            @Override
            public void onResponse(@NonNull Call<List<Ads>> call, @NonNull Response<List<Ads>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                List<Ads> ads = response.body();

                assert ads != null;
                if (ads.size() == 0) progressBar.setVisibility(View.INVISIBLE);

                for (Ads ad : ads) {
                    adsList.add(new Ads(ad.getDescription(), ad.getTitle(), ad.getPrice(), ad.getViews(), ad.getLikes(), ad.getId()));
                }

                adsRecyclerViewAdapter.setData(adsList);

                manager = new GridLayoutManager(getContext(), 1);
                findView.setLayoutManager(manager);
                findView.setAdapter(adsRecyclerViewAdapter);

                currentView = "ads";
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(@NonNull Call<List<Ads>> call, @NonNull Throwable t) {
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.progress_circular);

        findView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = manager.getChildCount();
                totalItems = manager.getItemCount();
                scrollOutItems = manager.findFirstVisibleItemPosition();

                if (isScrolling) {
                    if ((currentItems + scrollOutItems) >= totalItems) {
                        isScrolling = false;
                        new Handler().postDelayed(() -> {
                            adCount = adCount + 10;
                            if(currentView.equals("ads")) {
                                showAds();
                            } else {
                                showFinds();
                            }
                        }, 5000);
                    }
                }
            }
        });
    }
}
