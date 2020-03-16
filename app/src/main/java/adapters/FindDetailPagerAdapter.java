package adapters;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.finder.FindDetailFragment;
import com.example.finder.FindProposalFragment;


public class FindDetailPagerAdapter extends FragmentPagerAdapter {
    int tabNumber;
    Intent intent;

    public FindDetailPagerAdapter(@NonNull FragmentManager fm, int behavior, Intent intent) {
        super(fm, behavior);
        tabNumber = behavior;
        this.intent = intent;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                FindDetailFragment findDetailFragment = new FindDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putString("category", intent.getStringExtra("category"));
                bundle.putString("title", intent.getStringExtra("title"));
                bundle.putString("price", intent.getStringExtra("price"));
                bundle.putString("timeLeft", intent.getStringExtra("timeLeft"));
                bundle.putString("description", intent.getStringExtra("description"));
                bundle.putString("title", intent.getStringExtra("title"));
                findDetailFragment.setArguments(bundle);
                return findDetailFragment;
            case 1:
                return new FindProposalFragment();
            default:
                return new FindDetailFragment();
        }
    }

    @Override
    public int getCount() {
        return tabNumber;
    }
}
