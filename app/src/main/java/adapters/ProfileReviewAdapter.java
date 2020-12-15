package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jonnyup.nairarefill.R;

import java.util.List;

import data.Feedback;
import de.hdodenhof.circleimageview.CircleImageView;
import others.Constants;
import others.TimeDifference;

public class ProfileReviewAdapter extends RecyclerView.Adapter<ProfileReviewAdapter.ViewHolder> {

    private Context context;
    private List<Feedback> data;

    public ProfileReviewAdapter(Context context, List<Feedback> data) {
        this.context = context;
        this.data = data;
    }

    public void setData(List<Feedback> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.item_profile_review, parent, false);
        return new ProfileReviewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Glide.with(context).load(Constants.BASE_URL + data.get(position).getProfileImage()).into(holder.profileImage);
        holder.description.setText(data.get(position).getFeedback());
        holder.clientName.setText(data.get(position).getUsername());
        holder.rating.setRating(Float.parseFloat(data.get(position).getRating()));
        holder.jobTitle.setText(data.get(position).getTitle());
        holder.jobTitle.setVisibility(View.GONE);
        holder.date.setText(TimeDifference.getDiffTwo(data.get(position).getCreatedAt()));

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
        TextView jobTitle;
        RatingBar rating;
        TextView date;
        TextView description;
        TextView clientName;
        ImageView expand;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profile_image);
            jobTitle = itemView.findViewById(R.id.job_title);
            description = itemView.findViewById(R.id.description);
            rating = itemView.findViewById(R.id.rating);
            date = itemView.findViewById(R.id.date);
            clientName = itemView.findViewById(R.id.client_name);
            expand = itemView.findViewById(R.id.expand);

        }
    }
}
