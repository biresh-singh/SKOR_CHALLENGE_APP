package adaptor;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import fragment.EVoucherFragment;
import fragment.VoucherListFragment;

/**
 * Created by jessica on 12/27/17.
 */

public class WalletPageAdapter extends FragmentStatePagerAdapter{

    private static final int NUM_PAGES = 2;

    public WalletPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment voucherListFragment = new VoucherListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type",position);
        voucherListFragment.setArguments(bundle);
        return voucherListFragment;
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }
}
