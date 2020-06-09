package com.example.finder;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Objects;

import Database.DatabaseOpenHelper;
import data.AdPoster;
import data.Finds;
import data.Proposal;
import others.Constants;
import others.TimeDifference;
import retrofit.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FindDetailFragment extends Fragment {

    private String auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        DatabaseOpenHelper dbo = new DatabaseOpenHelper(getContext());
        AdPoster adPoster = dbo.getAdPoster();
        auth = adPoster.getAuth() == null ? "" : adPoster.getAuth();

        // Inflate the layout for this fragment
        LinearLayout root = (LinearLayout) inflater.inflate(R.layout.fragment_find_detail, container, false);
        TextView titleView = root.findViewById(R.id.title);
        TextView timeLeftView = root.findViewById(R.id.time_left);
        TextView priceView = root.findViewById(R.id.price);
        TextView descriptionView = root.findViewById(R.id.description);
        ImageView delete = root.findViewById(R.id.delete);
        LinearLayout form = root.findViewById(R.id.proposal_form);

        assert getArguments() != null;
        String timeLeft = TimeDifference.getDiff(getArguments().getString("bidEnd"));

        titleView.setText(getArguments().getString("title"));
        timeLeftView.setText(timeLeft);

        NumberFormat format = new DecimalFormat("#,###");
        if(!Objects.equals(getArguments().getString("price"), "")) {
            String fPrice = format.format(Double.valueOf(Objects.requireNonNull(getArguments().getString("price"))));
            priceView.setText(new StringBuilder().append("N").append(fPrice));
        } else {
            priceView.setVisibility(View.GONE);
        }

        String attachmentStr = getArguments().getString("attachment");
        assert attachmentStr != null;
        if(getArguments().getString("attachment") != null && !attachmentStr.equals("")) {
            TextView attachment = root.findViewById(R.id.attachment);
            CardView attachmentWrapper = root.findViewById(R.id.attachment_wrapper);
            attachmentWrapper.setVisibility(View.VISIBLE);
            attachment.setVisibility(View.VISIBLE);
            attachment.setOnClickListener(v -> {
                String googleDocsUrl = "http://docs.google.com/viewer?url=" + Constants.BASE_URL + attachmentStr;
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(googleDocsUrl), "text/html");
                startActivity(intent);
            });
        }

        descriptionView.setText(getArguments().getString("description"));

        if(adPoster.getAuth() != null) {
            if (Objects.equals(getArguments().getString("auth"), adPoster.getAuth())) {
                delete.setVisibility(View.VISIBLE);
            }

            if (!Objects.equals(getArguments().getString("auth"), adPoster.getAuth()) &&
                    adPoster.getAuth() != null && !timeLeft.equals("Bid is closed")) {
                form.setVisibility(View.VISIBLE);
            }
        }

        return root;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final EditText description = view.findViewById(R.id.bid_description);
        final EditText benefit = view.findViewById(R.id.bid_benefit);
        MaterialButton submit = view.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ben = benefit.getText().toString();
                String des = description.getText().toString();
                addProposal(ben, des, view);
            }
        });

        view.findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);

                // change the type of data you need to share,
                // for image use "image/*"
                intent.setType("text/plain");
                assert getArguments() != null;
                intent.putExtra(Intent.EXTRA_TEXT, Constants.BASE_URL + "deeplink/finds/" + getArguments().getString("id"));
                startActivity(Intent.createChooser(intent, "Share"));
            }
        });

        ImageView delete = view.findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletefind();
            }
        });
    }

    private void addProposal(String benefit, final String description, final View v) {

        if(auth.equals("")) return;

        if (description.isEmpty()) {
            Toast.makeText(getContext(), "Description field is compulsory", Toast.LENGTH_LONG).show();
            return;
        }

        if (benefit.isEmpty()) {
            benefit = "";
        }

        assert getArguments() != null;
        Call<Proposal> call = ApiClient.connect().addProposal(
                description, getArguments().getString("id"), benefit,
                getArguments().getString("chatChoice"), getArguments().getString("category"), auth
        );

        call.enqueue(new Callback<Proposal>() {
            @Override
            public void onResponse(@NonNull Call<Proposal> call, @NonNull Response<Proposal> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "" + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                Proposal proposal = response.body();
                assert proposal != null;
                if (proposal.getStatus()) {
                    Toast.makeText(getContext(), "Bid sent!", Toast.LENGTH_LONG).show();

                    final EditText description = v.findViewById(R.id.bid_description);
                    final EditText benefit = v.findViewById(R.id.bid_benefit);
                    description.setText("");
                    benefit.setText("");
                } else {
                    Toast.makeText(getContext(), "You are not qualified to bid", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Proposal> call, @NonNull Throwable t) {
            }
        });
    }

    private void deletefind() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        builder.setMessage("Delete this Find?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        assert getArguments() != null;
                        Call<Finds> call = ApiClient.connect().deleteFind(getArguments().getString("id"));
                        call.enqueue(new Callback<Finds>() {
                            @Override
                            public void onResponse(@NonNull Call<Finds> call, @NonNull Response<Finds> response) {
                                if (!response.isSuccessful()) {
                                    Toast.makeText(getContext(), "" + response.code(), Toast.LENGTH_LONG).show();
                                    return;
                                }

                                final Finds ads = response.body();
                                assert ads != null;

                                if (Boolean.parseBoolean(ads.getStatus())) {
                                    Intent intent = new Intent(getContext(), MainActivity.class);
                                    startActivity(intent);
                                }

                            }

                            @Override
                            public void onFailure(@NonNull Call<Finds> call, @NonNull Throwable t) {
                            }
                        });
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
