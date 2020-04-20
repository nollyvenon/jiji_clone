package others;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.finder.AdPostForm;
import com.example.finder.AdPosterRegistration;
import com.example.finder.FindPostForm;
import com.example.finder.FindPosterRegistration;
import com.example.finder.MainActivity;
import com.example.finder.MessageList;
import com.example.finder.PhoneNumberVerification;
import com.example.finder.Profile;
import com.example.finder.Search;

import java.util.ArrayList;
import java.util.List;

import Database.DatabaseOpenHelper;
import data.AdPoster;
import data.FindPoster;
import data.Messages;
import data.VerifiedPhoneNumber;
import retrofit.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BottomAppBarEvent {
    private Context context;
    private String auth;
    private String unreadCount = "0";
    private AdPoster adPoster;

    public BottomAppBarEvent(Context context) {
        this.context = context;

        DatabaseOpenHelper dbo = new DatabaseOpenHelper(context);
        adPoster = dbo.getAdPoster();
        auth = adPoster.getAuth();
    }

    public void getUnreadCount(final TextView v) {
        final Call<List<Messages>> call = ApiClient.connect().getUnreadCount(auth);
        call.enqueue(new Callback<List<Messages>>() {
            @Override
            public void onResponse(@NonNull Call<List<Messages>> call, @NonNull Response<List<Messages>> response) {
                if (!response.isSuccessful()) {
                    //Toast.makeText(context, "" + response.code(), Toast.LENGTH_LONG).show();
                    return;
                }

                List<Messages> m = response.body();
                assert m != null;
                v.setText(m.get(0).getUnreadCount());

            }

            @Override
            public void onFailure(@NonNull Call<List<Messages>> call, @NonNull Throwable t) {
                //Toast.makeText(context, t.toString(), Toast.LENGTH_LONG).show();
            }
        });

    }

    public void postAd() {
        if (isAdRegistered().equals("both")) {
            Intent intent = new Intent(context, AdPostForm.class);
            context.startActivity(intent);
        } else if (isAdRegistered().equals("sms")) {
            Intent intent = new Intent(context, AdPosterRegistration.class);
            intent.putExtra("hide", "hide");
            context.startActivity(intent);
        }
        else {
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
        if(auth != null) {
            Intent intent = new Intent(context, Profile.class);
            context.startActivity(intent);
        } else {
            Intent intent = new Intent(context, PhoneNumberVerification.class);
            intent.putExtra("userType", Constants.FINDS);
            context.startActivity(intent);
        }
    }

    public void goToMessageListActivity() {
        if(auth != null) {
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

        if(user.getAuth() == null) return "none";
        if(user.getUserType().equals(Constants.ADS) || !user.getAds().isEmpty()) return "both";
        if(user.getAds().isEmpty()) return "sms";
        return "none";
    }

    private String isFindRegistered() {
        DatabaseOpenHelper dbo = new DatabaseOpenHelper(context);
        AdPoster user = dbo.getAdPoster();

        if(user.getAuth() == null) return "none";
        if(user.getUserType().equals(Constants.FINDS) || user.getUserType().equals(Constants.ADS)
                || !user.getFinds().isEmpty()) return "both";
        return "none";
    }

    public static void isRegistered(Context context) {
        DatabaseOpenHelper dbo = new DatabaseOpenHelper(context);
        AdPoster adPoster = dbo.getAdPoster();
        if(adPoster.getAuth() == null) {
            Intent intent = new Intent(context, PhoneNumberVerification.class);
            context.startActivity(intent);
        }
    }

}
