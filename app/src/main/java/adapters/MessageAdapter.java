package adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finder.MessagePanel;
import com.example.finder.R;

import java.util.List;

import data.Messages;
import others.TimeDifference;
import retrofit.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private Context context;
    private List<Messages> data;

    public MessageAdapter(Context context, List<Messages> data) {
        this.context = context;
        this.data = data;
    }

    public void setData(List<Messages> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.item_message_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        if(data.get(position).getBusinessName().equals("")) {
            holder.sender.setText(data.get(position).getUsername());
        } else {
            holder.sender.setText(data.get(position).getBusinessName());
        }
        holder.date.setText(TimeDifference.getDiffTwo(data.get(position).getCreatedAt()));
        holder.content.setText(data.get(position).getMessage());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeMessage(data.get(position).getUniqueId(), holder);
            }
        });

        holder.wrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MessagePanel.class);
                intent.putExtra("aid", data.get(position).getAdId());
                intent.putExtra("fid", data.get(position).getFindId());
                intent.putExtra("uniqueId", data.get(position).getUniqueId());
                context.startActivity(intent);
            }
        });
    }

    private void removeMessage(final String uniqueId, final ViewHolder holder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Delete this Message?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Call<Messages> call = ApiClient.connect().removeMessage(uniqueId);
                        call.enqueue(new Callback<Messages>() {
                            @Override
                            public void onResponse(@NonNull Call<Messages> call, @NonNull Response<Messages> response) {
                                if (!response.isSuccessful()) {
                                    Toast.makeText(context, "" + response.code(), Toast.LENGTH_LONG).show();
                                    return;
                                }

                                final Messages ads = response.body();
                                assert ads != null;

                                if (Boolean.parseBoolean(ads.getStatus())) {
                                    holder.wrap.setVisibility(View.GONE);
                                }

                            }

                            @Override
                            public void onFailure(@NonNull Call<Messages> call, @NonNull Throwable t) {
                                Toast.makeText(context, t.toString(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog =  builder.create();
        alertDialog.show();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private CardView wrap;
        private TextView sender;
        private TextView date;
        private TextView delete;
        private TextView content;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            wrap = itemView.findViewById(R.id.wrap);
            sender = itemView.findViewById(R.id.sender);
            date = itemView.findViewById(R.id.date);
            delete = itemView.findViewById(R.id.delete);
            content = itemView.findViewById(R.id.content);
        }
    }
}
