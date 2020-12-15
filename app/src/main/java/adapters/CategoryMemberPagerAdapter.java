package adapters;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import androidx.fragment.app.FragmentPagerAdapter;

import com.jonnyup.nairarefill.CategoryFindFragment;
import com.jonnyup.nairarefill.CategoryMemberFragment;

public class CategoryMemberPagerAdapter extends FragmentPagerAdapter {
    private int tabNumber;
    private Intent intent;
    private Bundle b = new Bundle();

    public CategoryMemberPagerAdapter(@NonNull FragmentManager fm, int behavior, Intent intent) {
        super(fm, behavior);
        tabNumber = behavior;
        this.intent = intent;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                CategoryMemberFragment categoryMemberFragment = new CategoryMemberFragment();
                b.putString("id", intent.getStringExtra("id"));
                categoryMemberFragment.setArguments(b);
                return categoryMemberFragment;
            case 1:
                CategoryFindFragment categoryFindFragment = new CategoryFindFragment();
                b.putString("id", intent.getStringExtra("id"));
                categoryFindFragment.setArguments(b);
                return categoryFindFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabNumber;
    }
}
