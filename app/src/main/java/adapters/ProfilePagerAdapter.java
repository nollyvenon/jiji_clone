package adapters;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.jonnyup.nairarefill.MyWorksAdFragment;
import com.jonnyup.nairarefill.MyWorksFindFragment;
import com.jonnyup.nairarefill.ProfileDetailFragment;
import com.jonnyup.nairarefill.ProfileReviewFragment;

public class ProfilePagerAdapter extends FragmentPagerAdapter {

    private int tabNumber;
    Intent intent;

    public ProfilePagerAdapter(@NonNull FragmentManager fm, int behavior, Intent intent) {
        super(fm, behavior);
        tabNumber = behavior;
        this.intent = intent;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Bundle b = new Bundle();
        switch (position) {
            case 0:
                ProfileDetailFragment profileDetailFragment = new ProfileDetailFragment();
                b.putString("id", intent.getStringExtra("id"));
                profileDetailFragment.setArguments(b);
                return profileDetailFragment;
            case 1:
                ProfileReviewFragment profileReviewFragment = new ProfileReviewFragment();
                b = new Bundle();
                b.putString("id", intent.getStringExtra("id"));
                profileReviewFragment.setArguments(b);
                return  profileReviewFragment;
            case 2:
                MyWorksAdFragment myWorksAdFragment = new MyWorksAdFragment();
                b.putString("id", intent.getStringExtra("id"));
                b.putString("auth", intent.getStringExtra("auth"));
                myWorksAdFragment.setArguments(b);
                return myWorksAdFragment;
            case 3:
                MyWorksFindFragment myWorksFindFragment = new MyWorksFindFragment();
                b.putString("id", intent.getStringExtra("id"));
                b.putString("auth", intent.getStringExtra("auth"));
                myWorksFindFragment.setArguments(b);
                return myWorksFindFragment;
            default:
                return new ProfileDetailFragment();
        }
    }

    @Override
    public int getCount() {
        return tabNumber;
    }
}
