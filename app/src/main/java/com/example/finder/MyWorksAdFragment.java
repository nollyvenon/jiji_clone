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
import java.util.List;

import adapters.AdsRecyclerViewAdapter;
import data.Ads;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyWorksAdFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_my_works_ad, container, false);
        RecyclerView recyclerView = linearLayout.findViewById(R.id.my_work_ads);
        AdsRecyclerViewAdapter adapter = new AdsRecyclerViewAdapter(getContext(), showAds());
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        recyclerView.setAdapter(adapter);

        return linearLayout;
    }

    private List<Ads> showAds() {
        List<Ads> adsList = new ArrayList<>();

        adsList.add(new Ads("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et" +
                "\"dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut",
                "Shopify Developer", "6000", "107", "22"));

        adsList.add(new Ads("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et" +
                "\"dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut",
                "Shopify Developer", "6000", "107", "22"));

        adsList.add(new Ads("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et" +
                "\"dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut",
                "Shopify Developer", "6000", "107", "22"));

        adsList.add(new Ads("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et" +
                "\"dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut",
                "Shopify Developer", "6000", "107", "22"));

        adsList.add(new Ads("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et" +
                "\"dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut",
                "Shopify Developer", "6000", "107", "22"));

        return adsList;
    }

}