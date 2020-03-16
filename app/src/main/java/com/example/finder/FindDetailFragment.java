package com.example.finder;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


public class FindDetailFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        LinearLayout root = (LinearLayout) inflater.inflate(R.layout.fragment_find_detail, container, false);
        TextView titleView = root.findViewById(R.id.title);
        TextView timeLeftView = root.findViewById(R.id.time_left);
        TextView priceView = root.findViewById(R.id.price);
        TextView descriptionView = root.findViewById(R.id.description);


        String timeLeft = getArguments().getString("timeLeft");
        timeLeft = "Bidding ends in " + timeLeft;

        titleView.setText(getArguments().getString("title"));
        timeLeftView.setText(timeLeft);
        priceView.setText("N"+getArguments().getString("price"));
        descriptionView.setText(getArguments().getString("description"));
        return root;
    }
}
