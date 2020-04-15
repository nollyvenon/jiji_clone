package com.example.finder;

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

import java.util.ArrayList;
import java.util.List;

import adapters.ProposalAdapter;
import data.Proposal;
import retrofit.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FindProposalFragment extends Fragment {

    private ArrayList<Proposal> proposals = new ArrayList<>();
    private List<Proposal> proposals1 = new ArrayList<>();
    private ProposalAdapter proposalAdapter;
    private RecyclerView recyclerView;

    private Boolean isScrolling = true;
    private GridLayoutManager manager;
    private int currentItems, totalItems, scrollOutItems;
    private int adCount = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        assert getArguments() != null;
        getProposal(getArguments().getString("id"));

        // Inflate the layout for this fragment
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_find_proposal, container, false);
        recyclerView = linearLayout.findViewById(R.id.proposal_list);
        proposalAdapter = new ProposalAdapter(getContext(), proposals);
        manager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(proposalAdapter);
        return linearLayout;
    }

    private void getProposal(final String findId) {
        Call<List<Proposal>> call = ApiClient.connect().getProposal(findId, adCount);
        call.enqueue(new Callback<List<Proposal>>() {
            @Override
            public void onResponse(@NonNull Call<List<Proposal>> call, @NonNull Response<List<Proposal>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "" + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                proposals1.clear();
                proposals1 = response.body();
                assert proposals1 != null;

                for (Proposal p : proposals1) {
                    proposals.add(new Proposal(p.getProfileImage(), p.getBusinessName(), p.getRating(), p.getReviewCount(),
                                    p.getDescription(), p.getBenefit(), p.getId(), p.getFindId(), p.getAdId(), p.getAuth(), p.getFinderId()));
                }

                proposalAdapter.setData(proposals);

            }

            @Override
            public void onFailure(@NonNull Call<List<Proposal>> call, @NonNull Throwable t) {
                //Toast.makeText(getContext(), t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ProgressBar progressBar = view.findViewById(R.id.progress_circular);

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
                                assert getArguments() != null;
                                getProposal(getArguments().getString("id"));
                                if(proposals1.size() == 0) progressBar.setVisibility(View.INVISIBLE);
                            }
                        }, 5000);
                    }
                }
            }
        });
    }
}
