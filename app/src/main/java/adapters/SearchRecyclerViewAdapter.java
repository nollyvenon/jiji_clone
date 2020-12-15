package adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.jonnyup.nairarefill.AdDetail;
import com.jonnyup.nairarefill.FindDetail;
import com.jonnyup.nairarefill.R;
import data.SearchData;

import java.util.List;


public class SearchRecyclerViewAdapter extends RecyclerView.Adapter<SearchRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<SearchData> data;

    public SearchRecyclerViewAdapter(Context context, List<SearchData> data) {
        this.context = context;
        this.data = data;
    }

    public void setData(List<SearchData> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.item_search, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Drawable drawableFind = context.getResources().getDrawable(R.drawable.icon_find);
        Drawable drawableAd = context.getResources().getDrawable(R.drawable.icon_ad);
        holder.title.setText(data.get(position).getTitle());

        if(data.get(position).getType().equals("ads")) {
            int h = drawableAd.getIntrinsicHeight();
            int w = drawableAd.getIntrinsicWidth();
            drawableAd.setBounds( 0, 0, w, h );
            drawableAd.setTint(context.getResources().getColor(R.color.lightest_black));
            holder.title.setCompoundDrawables(drawableAd, null, null, null);
        } else {
            int h = drawableFind.getIntrinsicHeight();
            int w = drawableFind.getIntrinsicWidth();
            drawableFind.setBounds( 0, 0, w, h );
            drawableFind.setTint(context.getResources().getColor(R.color.lightest_black));
            holder.title.setCompoundDrawables(drawableFind, null, null, null);

        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(data.get(position).getType().equals("ads")) {
                    Intent intent = new Intent(context, AdDetail.class);
                    intent.putExtra("id", data.get(position).getId());
                    intent.putExtra("auth", data.get(position).getAuth());
                    context.startActivity(intent);
                } else {
                    //java.sql.Timestamp ts = java.sql.Timestamp.valueOf( data.get(position).getCreatedAt() );
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
                    intent.putExtra("auth", data.get(position).getAuth());
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView title;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.ad_item);
            title = itemView.findViewById(R.id.title);
        }
    }
}
