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

import com.example.finder.R;

import java.util.ArrayList;

import data.Review;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileReviewAdapter extends RecyclerView.Adapter<ProfileReviewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Review> data;

    public ProfileReviewAdapter(Context context, ArrayList<Review> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.profile_review_item, parent, false);
        return new ProfileReviewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.profileImage.setImageResource(data.get(position).getClientImage());
        holder.description.setText(data.get(position).getDescription());
        holder.clientName.setText(data.get(position).getClientName());
        holder.rating.setRating(data.get(position).getRating());
        holder.jobTitle.setText(data.get(position).getJobTitle());
        holder.price.setText(data.get(position).getPrice());
        holder.date.setText(data.get(position).getDate());

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
        TextView price;
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
            price = itemView.findViewById(R.id.price);
            date = itemView.findViewById(R.id.date);
            clientName = itemView.findViewById(R.id.client_name);
            expand = itemView.findViewById(R.id.expand);

        }
    }
}
