package adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finder.Profile;
import com.example.finder.R;

import java.util.List;

import data.Ads;

public class CategoryDetailAdAdapter extends RecyclerView.Adapter<CategoryDetailAdAdapter.ViewHolder> {

    Context context;
    List<Ads> data;

    public CategoryDetailAdAdapter(Context context, List<Ads> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.category_member_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.review.setText(data.get(position).getReview());
        holder.profileImage.setImageResource(data.get(position).getProfileImage());
        holder.companyName.setText(data.get(position).getCompanyName());
        holder.rating.setRating(data.get(position).getRating());

        holder.wrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Profile.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        CardView wrap;
        TextView review;
        TextView companyName;
        ImageView profileImage;
        RatingBar rating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            wrap = itemView.findViewById(R.id.wrap);
            review = itemView.findViewById(R.id.review_count);
            profileImage = itemView.findViewById(R.id.profile_image);
            companyName = itemView.findViewById(R.id.name);
            rating = itemView.findViewById(R.id.rating);
        }
    }
}
