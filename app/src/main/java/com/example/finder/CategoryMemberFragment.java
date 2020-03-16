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

import adapters.CategoryDetailAdAdapter;
import data.Ads;


/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryMemberFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_category_member, container, false);
        RecyclerView recyclerView = linearLayout.findViewById(R.id.member_list);
        CategoryDetailAdAdapter adapter = new CategoryDetailAdAdapter(getContext(), getAds());
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        recyclerView.setAdapter(adapter);

        return linearLayout;
    }

    private List<Ads> getAds() {
        List<Ads> adsList = new ArrayList<>();
        adsList.add(new Ads("Swell Technologies", "2.3", "(115)", R.drawable.bg2));
        adsList.add(new Ads("Onyx Data Systems", "5.0", "(405)", R.drawable.bg2));
        adsList.add(new Ads("Nissim", "3.6", "(23)", R.drawable.bg2));
        adsList.add(new Ads("Akent Furnitures", "1.6", "(7)", R.drawable.bg2));
        adsList.add(new Ads("Chidioke Electronics", "3.6", "(57)", R.drawable.bg2));

        return adsList;
    }
}
