package adapters;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finder.MessagePanel;
import com.example.finder.R;

import java.util.List;

import data.Chats;
import data.Messages;

public class MessagePanelAdapter extends RecyclerView.Adapter<MessagePanelAdapter.ViewHolder> {

    Context context;
    List<Chats> data;

    public MessagePanelAdapter(Context context, List<Chats> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.message_chat_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.sender.setText(data.get(position).getSender());
        holder.date.setText(data.get(position).getDate());
        holder.content.setText(data.get(position).getContent());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        if(data.get(position).getMessageID().equals("1")) {
            params.setMargins(0, 5, 60, 5);
            holder.wrap.setLayoutParams(params);
        } else {
            params.setMargins(60, 5, 0, 5);
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            wrap = itemView.findViewById(R.id.wrap);
            sender = itemView.findViewById(R.id.sender);
            date = itemView.findViewById(R.id.date);
            content = itemView.findViewById(R.id.content);
        }
    }
}
