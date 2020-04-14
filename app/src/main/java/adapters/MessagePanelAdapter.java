package adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finder.R;

import java.util.Arrays;
import java.util.List;

import Database.DatabaseOpenHelper;
import data.AdPoster;
import data.Messages;
import others.TimeDifference;

public class MessagePanelAdapter extends RecyclerView.Adapter<MessagePanelAdapter.ViewHolder> {

    Context context;
    List<Messages> data;

    public MessagePanelAdapter(Context context, List<Messages> data) {
        this.context = context;
        this.data = data;
    }

    public void setData(List<Messages> data){
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.item_message_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        DatabaseOpenHelper dbo = new DatabaseOpenHelper(context);
        AdPoster adPoster = dbo.getAdPoster();

        holder.date.setText(TimeDifference.getDiffTwo(data.get(position).getCreatedAt()));
        holder.content.setText(data.get(position).getMessage());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        if(!data.get(position).getId().equals(data.get(position).getSender())) {
            holder.wrap.setBackgroundColor(context.getResources().getColor(R.color.light_purple));
            holder.sender.setText(R.string.you);
            params.setMargins(0, 5, 80, 5);
            holder.wrap.setLayoutParams(params);
        } else {
            holder.sender.setText(data.get(position).getBusinessName());
            params.setMargins(80, 5, 0, 5);
            holder.wrap.setLayoutParams(params);
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout wrap;
        private TextView sender;
        private TextView date;
        private TextView content;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            wrap = itemView.findViewById(R.id.wrap);
            sender = itemView.findViewById(R.id.sender);
            date = itemView.findViewById(R.id.date);
            content = itemView.findViewById(R.id.content);
        }
    }
}
