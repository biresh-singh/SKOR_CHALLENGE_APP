package fragment.challenge;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.root.skor.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by biresh.singh on 15-06-2018.
 */
@SuppressLint("ValidFragment")
public class ChallengeFragment  extends Fragment {
    private Context mContext;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String manageScreen = "";

    public ChallengeFragment(Context context, Object obj) {
        this.mContext = context;
        this.manageScreen = (String) obj;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_challenge, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {

        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setCustomFont();

        if (!TextUtils.isEmpty(manageScreen)) {
            if (manageScreen.equalsIgnoreCase("myChallenge")) {
                viewPager.setCurrentItem(0);
            } else if (manageScreen.equalsIgnoreCase("individual")) {
                viewPager.setCurrentItem(1);
            } else {
                viewPager.setCurrentItem(0);
            }
        } else {
            viewPager.setCurrentItem(0);
        }
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        adapter.addFragment(new MyChallengeFragment(mContext), getActivity().getResources().getString(R.string.myChallenge));
        adapter.addFragment(new IndividualFragment(mContext), getActivity().getResources().getString(R.string.individual));
        adapter.addFragment(new TeamFragment(mContext), getActivity().getResources().getString(R.string.team));
        viewPager.setAdapter(adapter);
    }



    public void setCustomFont() {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                TextView tabTextView = new TextView(getActivity());
                tab.setCustomView(tabTextView);
                tabTextView.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                tabTextView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                tabTextView.setText(tab.getText());
                //tabTextView.setTextSize(R.dimen._16sdp);
                if (i == 0) {
                    tabTextView.setTextColor(getResources().getColor(R.color.tabblack));
                    tabTextView.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "appfont/Roboto-Bold.ttf"));

                }
                else
                {
                    tabTextView.setTextColor(getResources().getColor(R.color.tabgrey));
                    tabTextView.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "appfont/Roboto-Regular.ttf"));
                }
            }
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                TextView text = (TextView) tab.getCustomView();
                text.setTextColor(getResources().getColor(R.color.tabblack));
                text.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "appfont/Roboto-Bold.ttf"));

                PagerAdapter pagerAdapter = (PagerAdapter) viewPager
                        .getAdapter();
                for (int i = 0; i < pagerAdapter.getCount(); i++) {

                    Fragment viewPagerFragment = (Fragment) viewPager
                            .getAdapter().instantiateItem(viewPager, i);
                    if (viewPagerFragment != null
                            && viewPagerFragment.isAdded()) {

                        if (viewPagerFragment instanceof MyChallengeFragment) {
                            MyChallengeFragment myChallengeFragment = (MyChallengeFragment) viewPagerFragment;
                            if (myChallengeFragment != null) {
                                myChallengeFragment.updateUI();
                            }
                        } else if (viewPagerFragment instanceof IndividualFragment) {
                            IndividualFragment  individualFragment = (IndividualFragment) viewPagerFragment;
                            if (individualFragment != null) {
                                individualFragment.updateUI();
                            }
                        }
                        else if (viewPagerFragment instanceof TeamFragment) {
                            TeamFragment  teamFragment = (TeamFragment) viewPagerFragment;
                            if (teamFragment != null) {
                                teamFragment.updateUI();
                            }
                        }
                    }
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TextView text = (TextView) tab.getCustomView();
                text.setTextColor(getResources().getColor(R.color.tabgrey));
                text.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "appfont/Roboto-Regular.ttf"));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
