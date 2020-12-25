package com.johnnyup.nairarefill;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

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
import java.util.ArrayList;
import java.util.List;
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
    private List<Proposal> proposals1 = new ArrayList<>();
    LinearLayout root;
    LinearLayout form;
    CardView awardedPremiumView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        DatabaseOpenHelper dbo = new DatabaseOpenHelper(getContext());
        AdPoster adPoster = dbo.getAdPoster();
        auth = adPoster.getAuth() == null ? "" : adPoster.getAuth();

        // Inflate the layout for this fragment
        root = (LinearLayout) inflater.inflate(R.layout.fragment_find_detail, container, false);
        TextView titleView = root.findViewById(R.id.title);
        TextView timeLeftView = root.findViewById(R.id.time_left);
        TextView priceView = root.findViewById(R.id.price);
        TextView descriptionView = root.findViewById(R.id.description);
        ImageView delete = root.findViewById(R.id.delete);
         awardedPremiumView = root.findViewById(R.id.award_premium_wrapper);
        form = root.findViewById(R.id.proposal_form);

        assert getArguments() != null;
        String timeLeft = TimeDifference.getDiff(getArguments().getString("bidEnd"));

        titleView.setText(getArguments().getString("title"));
        timeLeftView.setText(timeLeft);

        NumberFormat format = new DecimalFormat("#,###");
        double priceOne, priceTwo;
        String fPrice;

        String argPrice = getArguments().getString("price");

        if (!argPrice.equals("")) {
            if (argPrice.contains("-")) {
                String[] prices = argPrice.split("-");
                priceOne = Double.parseDouble(prices[0]);
                priceTwo = Double.parseDouble(prices[1]);

                String fPriceOne = format.format(priceOne);
                String fPriceTwo = format.format(priceTwo);

                fPrice = new StringBuilder().append("N").append(fPriceOne).append(" - ").append("N").append(fPriceTwo).toString();
            } else {
                priceOne = Double.parseDouble(argPrice);
                String fPriceOne = format.format(priceOne);
                fPrice = new StringBuilder().append("N").append(fPriceOne).toString();
            }
            priceView.setText(fPrice);
        } else {
            priceView.setText("N0.00");
        }

        String attachmentStr = getArguments().getString("attachment");
        if (getArguments().getString("attachment") != null && !attachmentStr.equals("")) {
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

        if (adPoster.getAuth() != null) {
            if (Objects.equals(getArguments().getString("auth"), adPoster.getAuth())) {
                delete.setVisibility(View.VISIBLE);
            }

            if (!timeLeft.equalsIgnoreCase("Bid is closed")) {
                if (!Objects.equals(getArguments().getString("auth"), adPoster.getAuth()) && !timeLeft.isEmpty()) {
                    form.setVisibility(View.VISIBLE);
                }
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
        fetchAwarded(getArguments().getString("id"));
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

        awardedPremiumView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Paystack.class);
                intent.putExtra("award", "1");
                intent.putExtra("auth", auth);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                startActivity(intent);
                getActivity().finish();
                return;
            }
        });

        ImageView delete = view.findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFind();
            }
        });
    }

    private void fetchAwardedCount() {
        Call<Proposal> call = ApiClient.connect().fetchAwardedCount(auth);
        call.enqueue(new Callback<Proposal>() {
            @Override
            public void onResponse(@NonNull Call<Proposal> call, @NonNull Response<Proposal> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "" + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                Proposal proposal = response.body();
                assert proposal != null;
                if (!proposal.getStatus()) {
                    if (proposal.getAwardedCount() > 2) {
                        form.setVisibility(View.GONE);
                        awardedPremiumView.setVisibility(View.VISIBLE);
                        return;
                    }
                }
                form.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(@NonNull Call<Proposal> call, @NonNull Throwable t) {
                //Toast.makeText(getContext(), t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void fetchAwarded(String findId) {
        Call<List<Proposal>> call = ApiClient.connect().fetchAwarded(findId);
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

                if (proposals1 == null || proposals1.size() < 1) {
                    fetchAwardedCount();
                    return;
                }
                TextView awarded = root.findViewById(R.id.awarded_name);
                awarded.setText("Job awarded to " + proposals1.get(0).getBusinessName());
                awarded.setVisibility(View.VISIBLE);
                form.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(@NonNull Call<List<Proposal>> call, @NonNull Throwable t) {
                //Toast.makeText(getContext(), t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addProposal(String benefit, final String description, final View v) {

        if (auth.equals("")) return;

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
                    fetchAwarded(getArguments().getString("id"));
                } else {
                    Toast.makeText(getContext(), "You are not qualified to bid for this work", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Proposal> call, @NonNull Throwable t) {
            }
        });
    }

    private void deleteFind() {
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
