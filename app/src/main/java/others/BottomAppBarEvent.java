package others;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

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

import Database.DatabaseOpenHelper;
import data.AdPoster;
import data.FindPoster;
import data.VerifiedPhoneNumber;

public class BottomAppBarEvent {
    Context context;
    String auth;

    public BottomAppBarEvent(Context context) {
        this.context = context;

        DatabaseOpenHelper dbo = new DatabaseOpenHelper(context);
        AdPoster adPoster = dbo.getAdPoster();
        auth = adPoster.getAuth();
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
