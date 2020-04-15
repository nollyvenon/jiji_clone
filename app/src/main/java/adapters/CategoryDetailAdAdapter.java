package adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.finder.Profile;
import com.example.finder.R;

import java.util.List;

import data.Ads;
import others.Constants;

public class CategoryDetailAdAdapter extends RecyclerView.Adapter<CategoryDetailAdAdapter.ViewHolder> {

    Context context;
    List<Ads> data;

    public CategoryDetailAdAdapter(Context context, List<Ads> data) {
        this.context = context;
        this.data = data;
    }

    public void setData(List<Ads> data){
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.item_category_member, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.review.setText(data.get(position).getReview());
        if(data.get(position).getImage().isEmpty()) {
            holder.profileImage.setImageResource(R.drawable.bg2);
        } else {
            Glide.with(context.getApplicationContext()).load(Constants.BASE_URL + data.get(position).getImage()).into(holder.profileImage);
        }
        holder.companyName.setText(data.get(position).getBusinessName());
        holder.rating.setRating(Float.parseFloat(data.get(position).getRating()));

        holder.wrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Profile.class);
                intent.putExtra("id", data.get(position).getAdId());
                intent.putExtra("auth", data.get(position).getAuth());
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

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            wrap = itemView.findViewById(R.id.wrap);
            review = itemView.findViewById(R.id.review_count);
            profileImage = itemView.findViewById(R.id.profile_image);
            companyName = itemView.findViewById(R.id.name);
            rating = itemView.findViewById(R.id.rating);
        }
    }
}
