package adapters;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.finder.FindDetailFragment;
import com.example.finder.FindProposalFragment;


public class FindDetailPagerAdapter extends FragmentPagerAdapter {
    private int tabNumber;
    private Intent intent;

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
                bundle.putString("id", intent.getStringExtra("id"));
                bundle.putString("category", intent.getStringExtra("category"));
                bundle.putString("title", intent.getStringExtra("title"));
                bundle.putString("price", intent.getStringExtra("price"));
                bundle.putString("timeLeft", intent.getStringExtra("timeLeft"));
                bundle.putString("bidEnd", intent.getStringExtra("bidEnd"));
                bundle.putString("description", intent.getStringExtra("description"));
                bundle.putString("name", intent.getStringExtra("name"));
                bundle.putString("promotion", intent.getStringExtra("promotion"));
                bundle.putString("attachment", intent.getStringExtra("attachment"));
                bundle.putString("chatChoice", intent.getStringExtra("chatChoice"));
                bundle.putString("auth", intent.getStringExtra("auth"));
                findDetailFragment.setArguments(bundle);
                return findDetailFragment;
            case 1:
                FindProposalFragment findProposalFragment = new FindProposalFragment();
                Bundle b = new Bundle();
                b.putString("id", intent.getStringExtra("id"));
                findProposalFragment.setArguments(b);
                return findProposalFragment;
            default:
                return new FindDetailFragment();
        }
    }

    @Override
    public int getCount() {
        return tabNumber;
    }
}
