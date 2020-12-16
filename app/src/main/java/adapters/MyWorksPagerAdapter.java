package adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.johnnyup.nairarefill.MyWorksAdFragment;
import com.johnnyup.nairarefill.MyWorksFindFragment;

public class MyWorksPagerAdapter extends FragmentPagerAdapter {
    int tabNumber;

    public MyWorksPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        tabNumber = behavior;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new MyWorksAdFragment();
            case 1:
                return new MyWorksFindFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabNumber;
    }
}
