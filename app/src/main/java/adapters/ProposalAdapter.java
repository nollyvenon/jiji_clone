package adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.finder.AllFind;
import com.example.finder.MessagePanel;
import com.example.finder.Profile;
import com.example.finder.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;

import Database.DatabaseOpenHelper;
import data.AdPoster;
import data.Proposal;
import de.hdodenhof.circleimageview.CircleImageView;
import others.Constants;
import retrofit.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProposalAdapter extends RecyclerView.Adapter<ProposalAdapter.ViewHolder> {

    private Context context;
    private List<Proposal> data;
    private ProgressDialog progDialog;

    public ProposalAdapter(Context context, List<Proposal> data) {
        this.context = context;
        this.data = data;
    }

    public void setData(List<Proposal> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.item_proposal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        DatabaseOpenHelper dbo = new DatabaseOpenHelper(context);
        AdPoster adPoster = dbo.getAdPoster();

        Glide.with(context).load(Constants.BASE_URL + data.get(position).getProfileImage()).into(holder.profileImage);
        holder.description.setText(data.get(position).getDescription());
        holder.businessName.setText(data.get(position).getUsername());
        holder.rating.setRating(Float.parseFloat(data.get(position).getRating()));
        holder.reviewCount.setText("(" + data.get(position).getReviewCount() + ")");
        holder.benefit.setText(data.get(position).getBenefit());
        if (!data.get(position).getBenefit().equals("")) {
            holder.benefit.setBackgroundColor(context.getResources().getColor(R.color.light_grey));
        } else {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.benefit.getLayoutParams();
            layoutParams.setMargins(0, -30, 0, 0);
        }

        holder.businessName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Profile.class);
                intent.putExtra("id", data.get(position).getAdId());
                context.startActivity(intent);
            }
        });

        holder.expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.description.getMaxLines() == 2) {
                    holder.description.setMaxLines(Integer.MAX_VALUE);
                    holder.expand.setImageResource(R.drawable.icon_shrink);
                } else {
                    holder.description.setMaxLines(2);
                    holder.expand.setImageResource(R.drawable.icon_expand);
                }
            }
        });

        if (adPoster.getAuth() == null) return;
        if (data.get(position).getfFinderId().equals(data.get(position).getFinderId())) {
            if (data.get(position).getAwardedId().equals(data.get(position).getAdId())) {
                holder.chat.setVisibility(View.VISIBLE);
                holder.cancelAward.setVisibility(View.VISIBLE);
                holder.chat.setText(R.string.start_chat);
                holder.chat.setOnClickListener(v -> {
                    Intent intent = new Intent(context, MessagePanel.class);
                    intent.putExtra("aid", data.get(position).getAdId());
                    intent.putExtra("fid", data.get(position).getFinderId());
                    intent.putExtra("uniqueId", data.get(position).getAdId() + "-" + data.get(position).getFinderId());
                    context.startActivity(intent);
                });

                holder.cancelAward.setOnClickListener(v -> {
                    progDialog = ProgressDialog.show(context, "Loading", "Please wait...", true);
                    progDialog.setCancelable(false);
                    cancelJob(data.get(position).getFindId(), data.get(position).getFinderId(), "0");
                });
            }

            if (!data.get(position).getAwardedId().equals("0")) return;

            holder.chat.setVisibility(View.VISIBLE);
            holder.chat.setOnClickListener(v -> {
                progDialog = ProgressDialog.show(context, "Loading", "Please wait...", true);
                progDialog.setCancelable(false);
                awardJob(data.get(position).getFindId(), data.get(position).getFinderId(), data.get(position).getAdId(), data.get(position).getBusinessName());
            });
        }
    }

    private void awardJob(String findId, String finderId, String adId, String businessName) {
        Call<Proposal> call = ApiClient.connect().awardJob(findId, adId);
        call.enqueue(new Callback<Proposal>() {
            @Override
            public void onResponse(@NonNull Call<Proposal> call, @NonNull Response<Proposal> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(context, "" + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                Proposal proposal = response.body();
                assert proposal != null;

                if(proposal.getStatus()) {
                    progDialog.dismiss();
                    Toast.makeText(context, "Job awarded, please wait for "+businessName+" to accept", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(context, MessagePanel.class);
                    intent.putExtra("aid", adId);
                    intent.putExtra("fid", finderId);
                    intent.putExtra("uniqueId", adId + "-" + finderId);
                    context.startActivity(intent);
                }

            }

            @Override
            public void onFailure(@NonNull Call<Proposal> call, @NonNull Throwable t) {
                //Toast.makeText(getContext(), t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void cancelJob(String findId, String finderId, String adId) {
        Call<Proposal> call = ApiClient.connect().awardJob(findId, adId);
        call.enqueue(new Callback<Proposal>() {
            @Override
            public void onResponse(@NonNull Call<Proposal> call, @NonNull Response<Proposal> response) {
                if (!response.isSuccessful()) {
                    //Toast.makeText(context, "" + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                Proposal proposal = response.body();
                assert proposal != null;

                if(proposal.getStatus()) {
                    progDialog.dismiss();
                    Toast.makeText(context, "Job awarded cancelled successfully", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(context, AllFind.class);
                    context.startActivity(intent);
                }

            }

            @Override
            public void onFailure(@NonNull Call<Proposal> call, @NonNull Throwable t) {
                //Toast.makeText(getContext(), t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profileImage;
        TextView businessName;
        RatingBar rating;
        TextView reviewCount;
        TextView description;
        MaterialButton chat;
        TextView benefit;
        ImageView expand;
        Button cancelAward;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profile_image);
            businessName = itemView.findViewById(R.id.name);
            description = itemView.findViewById(R.id.description);
            rating = itemView.findViewById(R.id.rating);
            reviewCount = itemView.findViewById(R.id.review_count);
            chat = itemView.findViewById(R.id.chat);
            benefit = itemView.findViewById(R.id.benefit);
            expand = itemView.findViewById(R.id.expand);
            cancelAward = itemView.findViewById(R.id.cancel_award);
        }
    }
}
