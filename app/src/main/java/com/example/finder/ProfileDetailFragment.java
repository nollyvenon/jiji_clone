package com.example.finder;

import android.content.Intent;
import android.os.Bundle;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import Database.DatabasePhoneHelper;
import data.VerifiedPhoneNumber;
import others.Constants;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileDetailFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        CoordinatorLayout parent = (CoordinatorLayout) inflater.inflate(R.layout.fragment_profile_detail, container, false);
        TextView editButton = parent.findViewById(R.id.edit);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfile(v);
            }
        });

        TextView startChatButton = parent.findViewById(R.id.start_chat);
        startChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMessagePanel(v);
            }
        });
        return parent;
    }

    public void goToMessagePanel(View view) {
        Intent intent = new Intent(getContext(), MessagePanel.class);
        getContext().startActivity(intent);
    }

    public void editProfile(View view) {
        DatabasePhoneHelper dbo = new DatabasePhoneHelper(getContext());
        ArrayList<VerifiedPhoneNumber> list = dbo.getVerifiedPhone();
        String userType = list.get(0).getUserType();

        if(userType.equalsIgnoreCase(Constants.FINDS)) {
            Intent i =  new Intent(getContext(), FindPosterRegistration.class);
            i.putExtra("userType", Constants.FINDS);
            startActivity(i);
        } else {
            Intent i =  new Intent(getContext(), AdPosterRegistration.class);
            i.putExtra("userType", Constants.ADS);
            startActivity(i);
        }
    }


}
