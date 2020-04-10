package com.example.finder;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import Database.DatabaseOpenHelper;
import data.AdPoster;
import data.VerifiedPhoneNumber;
import de.hdodenhof.circleimageview.CircleImageView;
import others.Constants;
import retrofit.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileDetailFragment extends Fragment {

    TextView marketArea, businessYear;
    private TextView username, businessDescription, serviceDescription;

    private View view;
    private String auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;

        DatabaseOpenHelper dbo = new DatabaseOpenHelper(getContext());
        AdPoster dba = dbo.getAdPoster();
        auth = dba.getAuth();

        this.initview();
        this.getUser();
    }

    private void initview() {
//        marketArea = view.findViewById(R.id.market_area);
//        businessYear = view.findViewById(R.id.experience);
//        username = view.findViewById(R.id.username);
        businessDescription = view.findViewById(R.id.description);
        serviceDescription = view.findViewById(R.id.service_description);
    }

    private void getUser() {
        DatabaseOpenHelper dbo = new DatabaseOpenHelper(getContext());
        AdPoster a = dbo.getAdPoster();

        Call<AdPoster> call;

        assert getArguments() != null;
        if (getArguments().getString("id") == null) {
            call = ApiClient.connect().getUserByAuth(a.getAuth());
        } else {
            call = ApiClient.connect().getUserById(getArguments().getString("id"));
        }

        call.enqueue(new Callback<AdPoster>() {
            @Override
            public void onResponse(@NonNull Call<AdPoster> call, @NonNull Response<AdPoster> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "" + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                final AdPoster adPoster = response.body();
                assert adPoster != null;

//                marketArea.setText(adPoster.getMarketArea());
//                businessYear.setText(adPoster.getBusinessYear() + " of business experience");
//                username.setText(adPoster.getUsername());
                businessDescription.setText(adPoster.getBusinessDescription());
                serviceDescription.setText(adPoster.getServiceDescription());

            }

            @Override
            public void onFailure(@NonNull Call<AdPoster> call, @NonNull Throwable t) {
                //Toast.makeText(getContext(), t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
