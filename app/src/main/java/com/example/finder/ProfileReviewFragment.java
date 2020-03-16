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

import adapters.ProfileReviewAdapter;
import data.Review;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileReviewFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_profile_review, container, false);
        RecyclerView recyclerView = linearLayout.findViewById(R.id.profile_review);
        ProfileReviewAdapter adapter = new ProfileReviewAdapter(getContext(), getReviews());
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        recyclerView.setAdapter(adapter);

        return linearLayout;
    }

    private ArrayList<Review> getReviews() {
        ArrayList<Review> reviews = new ArrayList<>();
        reviews.add(new Review("designing a google blogger page", "300", "4.3", "Ajibosun Arie",
                R.drawable.bg2, "6 days ago",
                "A very kind guy he did his best to deliver a perfect work and offered his help to any other"));

        reviews.add(new Review("designing a google blogger page", "300", "4.5", "Ajibosun Arie",
                R.drawable.bg2, "6 days ago",
                "A very kind guy he did his best to deliver a perfect work and offered his help to any other"));

        reviews.add(new Review("designing a google blogger page", "300", "3.3", "Ajibosun Arie",
                R.drawable.bg2, "6 days ago",
                "A very kind guy he did his best to deliver a perfect work and offered his help to any other"));

        reviews.add(new Review("designing a google blogger page", "300", "5", "Ajibosun Arie",
                R.drawable.bg2, "6 days ago",
                "A very kind guy he did his best to deliver a perfect work and offered his help to any other"));

        reviews.add(new Review("designing a google blogger page", "300", "3.5", "Ajibosun Arie",
                R.drawable.bg2, "6 days ago",
                "A very kind guy he did his best to deliver a perfect work and offered his help to any other"));
        return reviews;
    }
}
