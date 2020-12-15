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
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jonnyup.nairarefill.CategoryMembers;
import com.jonnyup.nairarefill.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import data.CategoryListData;
import others.Constants;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.ViewHolder> {

    private Context context;
    private List<CategoryListData> data;
    private String type;

    public CategoryListAdapter(Context context, List<CategoryListData> data, @Nullable String type) {
        this.context = context;
        this.data = data;
        this.type = type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        if(this.type.equals("home")) {
            view = inflater.inflate(R.layout.item_home_category, parent, false);
        } else {
            view = inflater.inflate(R.layout.item_category, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
//        if(data.get(position).getImage().isEmpty()) {
//            holder.categoryImage.setImageResource(R.drawable.icon_cat);
//        } else {
            Glide.with(context)
                    .load(Constants.BASE_URL + "public/images/category/" +  data.get(position).getImage())
                    .into(holder.categoryImage);
        //}

        holder.categoryName.setText(data.get(position).getName());

        double count = Double.parseDouble(data.get(position).getCount());
        NumberFormat format = new DecimalFormat("#,###");
        String fCount = format.format(count);
        holder.categoryCount.setText(new StringBuilder().append(fCount).append(" members"));

        holder.mParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CategoryMembers.class);
                intent.putExtra("category", data.get(position).getName());
                intent.putExtra("id", data.get(position).getId());
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
