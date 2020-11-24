package com.aws.takitour.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.aws.takitour.R;
import com.aws.takitour.fragments.ExploreFragment;
import com.aws.takitour.fragments.MapFragment;
import com.aws.takitour.fragments.NotificationFragment;
import com.aws.takitour.fragments.ProfileFragment;
import com.aws.takitour.fragments.ToursFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new ExploreFragment();
            case 1:
                return new ToursFragment();
            case 2:
                return new NotificationFragment();
            case 3:
                return new ProfileFragment();
            default:
                return new ExploreFragment();
        }
    }

    @Override
    public int getCount() {
        return 4;
    }
}
