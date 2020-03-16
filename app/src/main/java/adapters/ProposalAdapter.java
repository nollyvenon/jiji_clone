package adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finder.MessagePanel;
import com.example.finder.Profile;
import com.example.finder.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;

import data.Proposal;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProposalAdapter extends RecyclerView.Adapter<ProposalAdapter.ViewHolder> {

    private Context context;
    private List<Proposal> data;

    public ProposalAdapter(Context context, List<Proposal> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.proposal_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.profileImage.setImageResource(data.get(position).getProfileImage());
        holder.description.setText(data.get(position).getDescription());
        holder.username.setText(data.get(position).getUsername());
        holder.rating.setRating(data.get(position).getRating());
        holder.reviewCount.setText(data.get(position).getReviewCount());
        holder.benefit.setText(data.get(position).getBenefit());
        if(!data.get(position).getBenefit().equals("")) {
            holder.benefit.setBackgroundColor(context.getResources().getColor(R.color.light_grey));
        } else {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.benefit.getLayoutParams();
            layoutParams.setMargins(0,-30, 0,0);
        }

        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Profile.class);
                context.startActivity(intent);
            }
        });

        holder.chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MessagePanel.class);
                context.startActivity(intent);
            }
        });

        holder.expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.description.getMaxLines() == 2) {
                    holder.description.setMaxLines(Integer.MAX_VALUE);
                    holder.expand.setImageResource(R.drawable.icon_shrink);
                } else {
                    holder.description.setMaxLines(2);
                    holder.expand.setImageResource(R.drawable.icon_expand);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profileImage;
        TextView username;
        RatingBar rating;
        TextView reviewCount;
        TextView description;
        MaterialButton chat;
        TextView benefit;
        ImageView expand;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profile_image);
            username = itemView.findViewById(R.id.name);
            description = itemView.findViewById(R.id.description);
            rating = itemView.findViewById(R.id.rating);
            reviewCount = itemView.findViewById(R.id.review_count);
            chat = itemView.findViewById(R.id.chat);
            benefit = itemView.findViewById(R.id.benefit);
            expand = itemView.findViewById(R.id.expand);
        }
    }
}
