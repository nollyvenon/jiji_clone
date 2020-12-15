package others;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.jonnyup.nairarefill.AdPostForm;
import com.jonnyup.nairarefill.AdPosterRegistration;
import com.jonnyup.nairarefill.FindPostForm;
import com.jonnyup.nairarefill.MainActivity;
import com.jonnyup.nairarefill.MessageList;
import com.jonnyup.nairarefill.MessagePanel;
import com.jonnyup.nairarefill.PhoneNumberVerification;
import com.jonnyup.nairarefill.Profile;
import com.jonnyup.nairarefill.R;
import com.jonnyup.nairarefill.Search;

import java.util.List;

import Database.DatabaseOpenHelper;
import data.AdPoster;
import data.Messages;
import retrofit.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BottomAppBarEvent {
    private Context context;
    //private String auth;
    private String uniqueId = "0";
    private AdPoster adPoster;

    public BottomAppBarEvent(Context context) {
        this.context = context;

        //DatabaseOpenHelper dbo = new DatabaseOpenHelper(context);
        //adPoster = dbo.getAdPoster();
        //auth = adPoster.getAuth();
    }

    public void getUnreadCount(final TextView v) {
        DatabaseOpenHelper dbo = new DatabaseOpenHelper(context);
        AdPoster user = dbo.getAdPoster();

        if(user.getAuth() == null) {
            v.setText("0");
            return;
        }

        final Call<List<Messages>> call = ApiClient.connect().getUnreadCount(user.getAuth());
        call.enqueue(new Callback<List<Messages>>() {
            @Override
            public void onResponse(@NonNull Call<List<Messages>> call, @NonNull Response<List<Messages>> response) {
                if (!response.isSuccessful()) {
                    //Toast.makeText(context, "" + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                List<Messages> m = response.body();
                assert m != null;
                v.setText(String.valueOf(m.size()));
                if(m.size() == 0) return;
                if(!m.get(0).getUniqueId().equals(uniqueId)) {
                    sendNewMessageNotification(m.get(0).getMessage(), m.get(0).getAdId(), m.get(0).getFindId(), m.get(0).getUniqueId());
                    uniqueId = m.get(0).getUniqueId();
                }

            }

            @Override
            public void onFailure(@NonNull Call<List<Messages>> call, @NonNull Throwable t) {
                //Toast.makeText(context, t.toString(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void sendNewMessageNotification(String newMessage, String aid, String fid, String uniqueId) {

        Intent intent = new Intent(context, MessagePanel.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("aid", aid);
        intent.putExtra("fid", fid);
        intent.putExtra("uniqueId", uniqueId);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default")
                .setSmallIcon(R.drawable.icon_message)
                .setContentTitle("New Message")
                .setContentText(newMessage)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId("YOUR_PACKAGE_NAME");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "YOUR_PACKAGE_NAME",
                    "YOUR_APP_NAME",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
        assert notificationManager != null;
        notificationManager.notify(1, builder.build());

    }

    public void postAd() {
        if (isAdRegistered().equals("both")) {
            Intent intent = new Intent(context, AdPostForm.class);
            context.startActivity(intent);
        } else if (isAdRegistered().equals("sms")) {
            Intent intent = new Intent(context, AdPosterRegistration.class);
            intent.putExtra("hide", "hide");
            context.startActivity(intent);
        } else {
            Intent intent = new Intent(context, PhoneNumberVerification.class);
            intent.putExtra("userType", Constants.ADS);
            context.startActivity(intent);
        }
    }

    public void postFind() {
        if (isFindRegistered().equalsIgnoreCase("both")) {
            Intent intent = new Intent(context, FindPostForm.class);
            context.startActivity(intent);
        } else {
            Intent intent = new Intent(context, PhoneNumberVerification.class);
            intent.putExtra("userType", Constants.FINDS);
            context.startActivity(intent);
        }
    }

    public void goToHomeActivity() {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    public static void goToSearchActivity(Context context) {
        Intent intent = new Intent(context, Search.class);
        context.startActivity(intent);
    }

    public void goToProfileActivity() {
        DatabaseOpenHelper dbo = new DatabaseOpenHelper(context);
        AdPoster user = dbo.getAdPoster();

        if (user.getAuth() != null) {
            Intent intent = new Intent(context, Profile.class);
            context.startActivity(intent);
        } else {
            Intent intent = new Intent(context, PhoneNumberVerification.class);
            intent.putExtra("userType", Constants.FINDS);
            context.startActivity(intent);
        }
    }

    public void goToMessageListActivity() {
        DatabaseOpenHelper dbo = new DatabaseOpenHelper(context);
        AdPoster user = dbo.getAdPoster();
        if (user.getAuth() != null) {
            Intent intent = new Intent(context, MessageList.class);
            context.startActivity(intent);
        } else {
            Intent intent = new Intent(context, PhoneNumberVerification.class);
            intent.putExtra("userType", Constants.FINDS);
            context.startActivity(intent);
        }
    }

    //checks if user is registered,
    //returns true if registered,
    //returns false if not registered
    private String isAdRegistered() {
        DatabaseOpenHelper dbo = new DatabaseOpenHelper(context);
        AdPoster user = dbo.getAdPoster();

        if (user.getAuth() == null) return "none";
        if (user.getUserType().equals(Constants.ADS) || !user.getAds().isEmpty()) return "both";
        if (user.getAds().isEmpty()) return "sms";
        return "none";
    }

    private String isFindRegistered() {
        DatabaseOpenHelper dbo = new DatabaseOpenHelper(context);
        AdPoster user = dbo.getAdPoster();

        if (user.getAuth() == null) return "none";
        if (user.getUserType().equals(Constants.FINDS) || user.getUserType().equals(Constants.ADS)
                || !user.getFinds().isEmpty()) return "both";
        return "none";
    }

    public static void isRegistered(Context context) {
        DatabaseOpenHelper dbo = new DatabaseOpenHelper(context);
        AdPoster adPoster = dbo.getAdPoster();
        if (adPoster.getAuth() == null) {
            Intent intent = new Intent(context, PhoneNumberVerification.class);
            context.startActivity(intent);
        }
    }

}
