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

import com.example.finder.FindDetail;
import com.example.finder.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import data.Finds;

public class FindsRecyclerViewAdapter extends RecyclerView.Adapter<FindsRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<Finds> data;

    public FindsRecyclerViewAdapter(Context context, List<Finds> data) {
        this.context = context;
        this.data = data;
    }

    public void setData(List<Finds> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.item_finds, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        String des = data.get(position).getDescription();
        des = des.length() > 50 ? des.substring(0, 50) : des;
        holder.description.setText(String.format("%s...", des));
        holder.title.setText(data.get(position).getTitle());

        if (!data.get(position).getPrice().equals("")) {
            NumberFormat format = new DecimalFormat("#,###");
            String fPrice = format.format(Double.valueOf(data.get(position).getPrice()));
            holder.price.setText(new StringBuilder().append("N").append(fPrice));
        } else {
            holder.price.setVisibility(View.GONE);
        }

        holder.viewDetail.setOnClickListener(v -> {
            Intent intent = new Intent(context, FindDetail.class);
            intent.putExtra("id", data.get(position).getId());
            intent.putExtra("title", data.get(position).getTitle());
            intent.putExtra("price", data.get(position).getPrice());
            intent.putExtra("description", data.get(position).getDescription());
            intent.putExtra("name", data.get(position).getFinderName());
            intent.putExtra("timeLeft", data.get(position).getCreatedAt());
            intent.putExtra("bidEnd", data.get(position).getBidEnd());
            intent.putExtra("category", data.get(position).getCategory());
            intent.putExtra("promotion", data.get(position).getBenefit());
            intent.putExtra("attachment", data.get(position).getAttachment());
            intent.putExtra("chatChoice", data.get(position).getChatChoice());
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
        TextView price;
        TextView description;
        TextView title;
        Button viewDetail;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.find_item);
            price = itemView.findViewById(R.id.price);
            description = itemView.findViewById(R.id.description);
            title = itemView.findViewById(R.id.title);
            viewDetail = itemView.findViewById(R.id.view_detail);
        }
    }
}
