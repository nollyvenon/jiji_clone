package others;

import android.content.Context;
import android.content.Intent;

import com.example.finder.AdPostForm;
import com.example.finder.AdPosterRegistration;
import com.example.finder.FindPostForm;
import com.example.finder.FindPosterRegistration;
import com.example.finder.MainActivity;
import com.example.finder.MessageList;
import com.example.finder.PhoneNumberVerification;
import com.example.finder.Profile;

import java.util.ArrayList;

import Database.DatabaseFinderHelper;
import Database.DatabaseOpenHelper;
import Database.DatabasePhoneHelper;
import data.AdPoster;
import data.FindPoster;
import data.VerifiedPhoneNumber;

public class BottomAppBarEvent {
    Context context;

    public BottomAppBarEvent(Context context) {
        this.context = context;
    }

    public void postAd() {
        DatabasePhoneHelper dbp = new DatabasePhoneHelper(context);
        if (isAdRegistered().equalsIgnoreCase("both")) {
            Intent intent = new Intent(context, AdPostForm.class);
            context.startActivity(intent);
        } else if (isAdRegistered().equalsIgnoreCase("sms")) {
            Intent intent = new Intent(context, AdPosterRegistration.class);
            context.startActivity(intent);
        } else {
            Intent intent = new Intent(context, PhoneNumberVerification.class);
            intent.putExtra("user_type", Constants.ADS);
            context.startActivity(intent);
        }
    }

    public void postFind() {
        DatabasePhoneHelper dbp = new DatabasePhoneHelper(context);
        if (isFindRegistered().equalsIgnoreCase("both")) {
            Intent intent = new Intent(context, FindPostForm.class);
            context.startActivity(intent);
        } else if (isFindRegistered().equalsIgnoreCase("sms")) {
            Intent intent = new Intent(context, FindPosterRegistration.class);
            context.startActivity(intent);
        }  else {
            Intent intent = new Intent(context, PhoneNumberVerification.class);
            intent.putExtra("user_type", Constants.FINDS);
            context.startActivity(intent);
        }
    }

    public void goToHomeActivity() {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    public void goToProfileActivity() {
        Intent intent = new Intent(context, Profile.class);
        context.startActivity(intent);
    }

    public void goToMessageListActivity() {
        Intent intent = new Intent(context, MessageList.class);
        context.startActivity(intent);
    }

    //checks if user is registered,
    //returns true if registered,
    //returns false if not registered
    private String isAdRegistered() {
        DatabasePhoneHelper dbp = new DatabasePhoneHelper(context);
        ArrayList<VerifiedPhoneNumber> verifiedPhones = dbp.getVerifiedPhone();
        dbp.close();

        DatabaseOpenHelper dbo = new DatabaseOpenHelper(context);
        ArrayList<AdPoster> adPosters = dbo.getAdPoster();
        dbo.close();

        if(verifiedPhones.size() > 0 && adPosters.size() > 0) return "both";
        if(verifiedPhones.size() > 0) {
            VerifiedPhoneNumber verifiedPhoneNumber = new VerifiedPhoneNumber();
            verifiedPhoneNumber.setUserType(Constants.ADS);
            verifiedPhoneNumber.setPhoneNumber(verifiedPhones.get(0).getPhoneNumber());
            dbp.updateVerifiedPhone(verifiedPhoneNumber);
            dbo.close();
            return "sms";
        }
        return "none";
    }

    private String isFindRegistered() {
        DatabasePhoneHelper dbp = new DatabasePhoneHelper(context);
        ArrayList<VerifiedPhoneNumber> verifiedPhones = dbp.getVerifiedPhone();
        dbp.close();

        DatabaseFinderHelper dbo = new DatabaseFinderHelper(context);
        ArrayList<FindPoster> findPosters = dbo.fetchFindPoster();
        dbo.close();

        if(verifiedPhones.size() > 0 && findPosters.size() > 0) return "both";
        if(verifiedPhones.size() > 0) {
            VerifiedPhoneNumber verifiedPhoneNumber = new VerifiedPhoneNumber();
            verifiedPhoneNumber.setUserType(Constants.FINDS);
            verifiedPhoneNumber.setPhoneNumber(verifiedPhones.get(0).getPhoneNumber());
            dbp.updateVerifiedPhone(verifiedPhoneNumber);
            dbo.close();
            return "sms";
        }
        return "none";
    }

}
