package adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.finder.ProfileDetailFragment;
import com.example.finder.ProfileReviewFragment;

public class ProfilePagerAdapter extends FragmentPagerAdapter {

    private int tabNumber;

    public ProfilePagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        tabNumber = behavior;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ProfileDetailFragment();
            case 1:
                return new ProfileReviewFragment();
            default:
                return new ProfileDetailFragment();
        }
    }

    @Override
    public int getCount() {
        return tabNumber;
    }
}
