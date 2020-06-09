package adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finder.AdDetail;
import com.example.finder.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import data.Ads;

public class AdsRecyclerViewAdapter extends RecyclerView.Adapter<AdsRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<Ads> data;

    public AdsRecyclerViewAdapter(Context context, List<Ads> data) {
        this.context = context;
        this.data = data;
    }

    public void setData(List<Ads> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.item_ads, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        String des = data.get(position).getDescription();
        des = des.length() > 50 ? des.substring(0, 50) : des;
        holder.description.setText(des + "...");
        holder.price.setText(new StringBuilder().append("N").append(data.get(position).getPrice()).toString());
        holder.title.setText(data.get(position).getTitle());

        double dPrice = Double.parseDouble(data.get(position).getPrice());
        NumberFormat format = new DecimalFormat("#,###");
        String fPrice = format.format(dPrice);
        if(!data.get(position).getPrice().equals("")) {
            holder.price.setText(new StringBuilder().append("N").append(fPrice));
        } else {
            holder.price.setVisibility(View.GONE);
        }

        //holder.views.setText(data.get(position).getViews());

        holder.viewDetail.setOnClickListener(v -> {
            Intent intent = new Intent(context, AdDetail.class);
            intent.putExtra("id", data.get(position).getId());
            intent.putExtra("auth", data.get(position).getAuth());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView description;
        TextView title;
        TextView price;
        TextView views;
        Button viewDetail;
        TextView likes;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.ad_item);
            description = itemView.findViewById(R.id.description);
            title = itemView.findViewById(R.id.title);
            price = itemView.findViewById(R.id.price);
            //views = itemView.findViewById(R.id.views);
            likes = itemView.findViewById(R.id.likes);
            viewDetail = itemView.findViewById(R.id.view_detail);
        }
    }
}
