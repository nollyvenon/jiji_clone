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
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import Database.DatabaseOpenHelper;
import adapters.FindsRecyclerViewAdapter;
import data.AdPoster;
import data.Finds;
import retrofit.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyWorksFindFragment extends Fragment {

    private FindsRecyclerViewAdapter adapter;
    private List<Finds> findsList = new ArrayList<>();
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
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_my_works_find, container, false);
        recyclerView = linearLayout.findViewById(R.id.my_work_finds);
        adapter = new FindsRecyclerViewAdapter(getContext(), findsList);
        manager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        return linearLayout;
    }

    private void showFinds(final View view) {
        assert getArguments() != null;
        String auth = getArguments().getString("auth") == null ? a.getAuth() : getArguments().getString("auth");

        Call<List<Finds>> call = ApiClient.connect().getFindByAuth(auth, adCount);
        call.enqueue(new Callback<List<Finds>>() {
            @Override
            public void onResponse(@NonNull Call<List<Finds>> call, @NonNull Response<List<Finds>> response) {
                if(!response.isSuccessful()) {
                    Toast.makeText(getContext(), response.code(), Toast.LENGTH_LONG).show();
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

                if(findsList.size() == 0) {
                    RelativeLayout rl = view.findViewById(R.id.no_content);
                    rl.setVisibility(View.VISIBLE);
                }

                adapter.setData(findsList);

            }

            @Override
            public void onFailure(@NonNull Call<List<Finds>> call, @NonNull Throwable t) {

            }
        });
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.progress_circular);

        this.showFinds(view);

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
                                showFinds(view);
                            }
                        }, 5000);
                    }
                }
            }
        });
    }
}
