package adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import androidx.fragment.app.FragmentPagerAdapter;

import com.example.finder.CategoryFindFragment;
import com.example.finder.CategoryMemberFragment;

public class CategoryMemberPagerAdapter extends FragmentPagerAdapter {
    int tabNumber;

    public CategoryMemberPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        tabNumber = behavior;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new CategoryMemberFragment();
            case 1:
                return new CategoryFindFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabNumber;
    }
}
