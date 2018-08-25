package adaptor;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import fragment.BeFitAndHealthyFragment;
import fragment.GetRewardzFragment;
import fragment.GetTheLatestInfoFragment;
import fragment.GetTheOrganizedFragment;
import fragment.GreetingFragment;

public class AnimationCustomAdaptor extends FragmentStatePagerAdapter {

    private List<Fragment> fragments;

    public AnimationCustomAdaptor(FragmentManager fm, String baseUrl) {
        super(fm);
           this.fragments = new ArrayList<Fragment>();
            fragments.add(new GetTheLatestInfoFragment());
            fragments.add(new GreetingFragment());
            fragments.add(new GetTheOrganizedFragment());
            fragments.add(new BeFitAndHealthyFragment());
            fragments.add(new GetRewardzFragment());

    }


    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}