package com.johnnyup.nairarefill;

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
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import Database.DatabaseOpenHelper;
import adapters.ProfileReviewAdapter;
import data.AdPoster;
import data.Feedback;
import retrofit.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileReviewFragment extends Fragment {


    private ProfileReviewAdapter adapter;
    private List<Feedback> fbList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private Boolean isScrolling = true;
    private GridLayoutManager manager;
    private int currentItems, totalItems, scrollOutItems;
    private int adCount = 0;

    private AdPoster a;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        DatabaseOpenHelper dbo = new DatabaseOpenHelper(getContext());
        a = dbo.getAdPoster();

        // Inflate the layout for this fragment
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_profile_review, container, false);
        recyclerView = linearLayout.findViewById(R.id.profile_review);
        adapter = new ProfileReviewAdapter(getContext(), fbList);
        manager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        return linearLayout;
    }

    private void showFeedbacks(final View view) {
        Call<List<Feedback>> call;

        assert getArguments() != null;
        if (getArguments().getString("id") == null) {
            if(a.getAuth() == null) return;
            call = ApiClient.connect().getProfileFeedbackByAuth(a.getAuth(), adCount);
        } else {
            call =  ApiClient.connect().getProfileFeedback(getArguments().getString("id"), adCount);
        }

        call.enqueue(new Callback<List<Feedback>>() {
            @Override
            public void onResponse(@NonNull Call<List<Feedback>> call, @NonNull Response<List<Feedback>> response) {
                if(!response.isSuccessful()) {
                    Toast.makeText(getContext(), response.code(), Toast.LENGTH_LONG).show();
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

                if(fbList.size() == 0) {
                    RelativeLayout rl = view.findViewById(R.id.no_content);
                    rl.setVisibility(View.VISIBLE);
                }

                adapter.setData(fbList);
            }

            @Override
            public void onFailure(@NonNull Call<List<Feedback>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.progress_circular);

        showFeedbacks(view);

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
                                showFeedbacks(view);
                            }
                        }, 5000);
                    }
                }
            }
        });
    }
}
