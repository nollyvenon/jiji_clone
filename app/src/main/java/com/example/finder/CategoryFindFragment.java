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

import adapters.FindsRecyclerViewAdapter;
import data.Finds;


/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFindFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_category_find, container, false);
        FindsRecyclerViewAdapter adapter = new FindsRecyclerViewAdapter(getContext(), getFinds());
        RecyclerView findView = linearLayout.findViewById(R.id.find_list);
        findView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        findView.setAdapter(adapter);

        return linearLayout;
    }

    private List<Finds> getFinds() {
        List<Finds> findsList = new ArrayList<>();
        findsList.add(new Finds("25,000",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et \n" +
                        "\"dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut",
                "OnyxDatasystems", "Furniture", "I need a furniture maker", "Free Delivery", "2"));
        findsList.add(new Finds( "25,000",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et \n" +
                        "\"dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut",
                "OnyxDatasystems", "Software Developer", "I need an OpenCart website for my shop",
                "Free Delivery",
                "2"));
        findsList.add(new Finds("25,000",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et \n" +
                        "\"dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut",
                "OnyxDatasystems", "Transport", "Mass transport service needed",
                "Free Delivery", "2"));
        findsList.add(new Finds("25,000",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et \n" +
                        "\"dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut",
                "OnyxDatasystems", "Marketing", "A marketer needed at Ilogbo",
                "Free Delivery", "2"));
        findsList.add(new Finds("25,000",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et \n" +
                        "\"dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut",
                "OnyxDatasystems", "Furniture", "I need a furniture maker", "Free Delivery", "2"));

        return findsList;
    }
}
