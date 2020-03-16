package com.example.finder;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

import adapters.ProposalAdapter;
import data.Proposal;


public class FindProposalFragment extends Fragment {

    ArrayList<Proposal> proposals = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_find_proposal, container, false);
        RecyclerView recyclerView = linearLayout.findViewById(R.id.proposal_list);
        ProposalAdapter proposalAdapter = new ProposalAdapter(getContext(), getProposal());
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        recyclerView.setAdapter(proposalAdapter);
        return linearLayout;
    }

    private ArrayList<Proposal> getProposal() {
        proposals.add(new Proposal(R.drawable.bg2, "Oluwatobi Awelewa", "2.6", "94",
                "I am certified web developer and designer having a rich experience of web development " +
                        "for over 7+ years. I have a proven track record of successfully delivering number of ",
                "Free delivery"));

        proposals.add(new Proposal(R.drawable.bg2, "Oluwatobi Awelewa", "2.6", "94",
                "I am certified web developer and designer having a rich experience of web development " +
                        "for over 7+ years. I have a proven track record of successfully delivering number of ",
                ""));

        proposals.add(new Proposal(R.drawable.bg2, "Oluwatobi Awelewa", "2.6", "94",
                "I am certified web developer and designer having a rich experience of web development " +
                        "for over 7+ years. I have a proven track record of successfully delivering number of ",
                ""));

        proposals.add(new Proposal(R.drawable.bg2, "Oluwatobi Awelewa", "2.6", "94",
                "I am certified web developer and designer having a rich experience of web development " +
                        "for over 7+ years. I have a proven track record of successfully delivering number of ",
                "Free delivery"));

        proposals.add(new Proposal(R.drawable.bg2, "Oluwatobi Awelewa", "2.6", "94",
                "I am certified web developer and designer having a rich experience of web development " +
                        "for over 7+ years. I have a proven track record of successfully delivering number of ",
                ""));

        return proposals;
    }
}
