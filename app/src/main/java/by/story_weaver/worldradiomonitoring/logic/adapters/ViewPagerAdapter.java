package by.story_weaver.worldradiomonitoring.logic.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import by.story_weaver.worldradiomonitoring.ui.fragments.CountryFragment;
import by.story_weaver.worldradiomonitoring.ui.fragments.DashboardFragment;
import by.story_weaver.worldradiomonitoring.ui.fragments.StationsFragment;


public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new StationsFragment();
            case 2:
                return new CountryFragment();
            default:
                return new DashboardFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}