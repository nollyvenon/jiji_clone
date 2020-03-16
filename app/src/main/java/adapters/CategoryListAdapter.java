package adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finder.CategoryMembers;
import com.example.finder.R;

import java.util.List;

import data.CategoryListData;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.ViewHolder> {

    Context context;
    List<CategoryListData> data;

    public CategoryListAdapter(Context context, List<CategoryListData> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.category_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.categoryImage.setImageResource(data.get(position).getCategoryImage());
        holder.categoryName.setText(data.get(position).getCategoryName());
        holder.categoryCount.setText(new StringBuilder().append(data.get(position).getCategoryCount()).append(" members").toString());
        holder.mParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CategoryMembers.class);
                intent.putExtra("category", data.get(position).getCategoryName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout mParent;
        ImageView categoryImage;
        TextView categoryName;
        TextView categoryCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mParent = itemView.findViewById(R.id.category_list_layout);
            categoryImage = itemView.findViewById(R.id.category_image);
            categoryName = itemView.findViewById(R.id.category_name);
            categoryCount = itemView.findViewById(R.id.category_count);
        }
    }
}
