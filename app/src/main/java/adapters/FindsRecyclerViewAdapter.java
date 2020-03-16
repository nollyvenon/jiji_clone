package adapters;

        import android.content.Context;
        import android.content.Intent;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.RatingBar;
        import android.widget.TextView;
        import android.widget.Toast;

        import androidx.annotation.NonNull;
        import androidx.cardview.widget.CardView;
        import androidx.recyclerview.widget.RecyclerView;

        import com.example.finder.FindDetail;
        import com.example.finder.R;

        import java.util.List;

        import data.Finds;

public class FindsRecyclerViewAdapter extends RecyclerView.Adapter<FindsRecyclerViewAdapter.ViewHolder> {

    Context context;
    List<Finds> data;

    public FindsRecyclerViewAdapter(Context context, List<Finds> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.finds_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.price.setText("N"+data.get(position).getPrice());
        holder.description.setText(data.get(position).getDescription());
        holder.title.setText(data.get(position).getTitle());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FindDetail.class);
                intent.putExtra("title", data.get(position).getTitle());
                intent.putExtra("price", data.get(position).getPrice());
                intent.putExtra("description", data.get(position).getDescription());
                intent.putExtra("name", data.get(position).getFinderName());
                intent.putExtra("timeLeft", data.get(position).getTimeLeft());
                intent.putExtra("category", data.get(position).getCategory());
                intent.putExtra("promotion", data.get(position).getPromotion());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView price;
        TextView description;
        TextView title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.find_item);
            price = itemView.findViewById(R.id.price);
            description = itemView.findViewById(R.id.description);
            title = itemView.findViewById(R.id.title);
        }
    }
}
