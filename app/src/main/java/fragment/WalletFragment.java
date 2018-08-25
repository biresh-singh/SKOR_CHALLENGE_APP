package fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.root.skor.R;

import activity.userprofile.MainActivity;
import adaptor.WalletPageAdapter;
import utils.Loader;

public class WalletFragment extends Fragment {
    LinearLayout toolbarLinearLayout;
    FrameLayout inProgressFrameLayout, completedFrameLayout;
    ViewPager viewPager;
    TextView inProgressTextView, completedTextView;

    private PagerAdapter mPagerAdapter;
    private int type;

    public WalletFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wallet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbarLinearLayout = (LinearLayout) view.findViewById(R.id.fragment_wallet_toolbarMenuPanelLinearLayout);
        inProgressFrameLayout = (FrameLayout) view.findViewById(R.id.fragment_wallet_inProgressFrameLayout);
        completedFrameLayout = (FrameLayout) view.findViewById(R.id.fragment_wallet_completedFrameLayout);
        viewPager = (ViewPager) view.findViewById(R.id.fragment_wallet_viewPager);
        inProgressTextView = (TextView) view.findViewById(R.id.fragment_wallet_inProgressTextView);
        completedTextView = (TextView) view.findViewById(R.id.fragment_wallet_completedTextView);

        type = viewPager.getCurrentItem();
        if(type == 0){
            changeView(inProgressFrameLayout);
        }else{
            changeView(completedFrameLayout);
        }

        toolbarLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        View.OnClickListener fragmentHeaderTapped = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(view.getId() == R.id.fragment_wallet_inProgressFrameLayout ? 0 : 1);
                changeView(view);
            }
        };

        inProgressFrameLayout.setOnClickListener(fragmentHeaderTapped);
        completedFrameLayout.setOnClickListener(fragmentHeaderTapped);
        mPagerAdapter = new WalletPageAdapter(getChildFragmentManager());
        viewPager.setAdapter(mPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0){
                    changeView(inProgressFrameLayout);
                }else{
                    changeView(completedFrameLayout);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void changeView(View view) {
        resetViewColor();
        if(view.getId() == R.id.fragment_wallet_completedFrameLayout) {
            completedTextView.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.button_bg_white));
            completedTextView.setTextColor(ContextCompat.getColor(getContext(),R.color.red_orange));
        }else{
            inProgressTextView.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.button_bg_white));
            inProgressTextView.setTextColor(ContextCompat.getColor(getContext(),R.color.red_orange));
        }
        //CHANGE FRAGMENT
    }

    private void resetViewColor(){
        inProgressTextView.setBackgroundColor(ActivityCompat.getColor(getActivity(),R.color.red_orange));
        inProgressTextView.setTextColor(ActivityCompat.getColor(getActivity(),R.color.white));
        completedTextView.setBackgroundColor(ActivityCompat.getColor(getActivity(),R.color.red_orange));
        completedTextView.setTextColor(ActivityCompat.getColor(getActivity(),R.color.white));
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
