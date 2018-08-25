package activity.walkthrough;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.root.skor.R;
import com.viewpagerindicator.CirclePageIndicator;

import activity.userprofile.LoginActivity;
import adaptor.AnimationCustomAdaptor;
import database.SharedDatabase;


public class CustomAnimation extends android.support.v4.app.Fragment {
    private CirclePageIndicator circlePageIndicator;
    private ViewPager viewPager;
    private Context mContext;
    private FragmentManager fragmentManager;
    public String baseUrl;
    public SharedDatabase sharedDatabase;
    public AnimationCustomAdaptor myPagerAdapter;

    public CustomAnimation() {
    }

    @SuppressLint("ValidFragment")
    public CustomAnimation(Context context) {
        mContext = context;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.animation_viewpager, null);
        fragmentManager = getFragmentManager();
        circlePageIndicator = (CirclePageIndicator) rootView.findViewById(R.id.indicator);
        viewPager = (ViewPager) rootView.findViewById(R.id.pagers);
        baseUrl = getResources().getString(R.string.base_url);
        sharedDatabase=new SharedDatabase(getActivity());
        AnimationCustomAdaptor pageAdapter = new AnimationCustomAdaptor(fragmentManager, baseUrl);
        viewPager.setAdapter(pageAdapter);
        circlePageIndicator.setViewPager(viewPager);
        String flag = sharedDatabase.getFlag();
        if (baseUrl.equals("https://cerrapoints.com/")) {
            if (flag.equals("two")) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            } else if (flag.equals("one")) {
                viewPager.setCurrentItem(3);
            } else {
                viewPager.setCurrentItem(0);
            }
        } else {
            if (flag.equals("two")) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            } else if (flag.equals("one")) {
                viewPager.setCurrentItem(4);
            } else {
                viewPager.setCurrentItem(0);
            }
        }


        circlePageIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 0) {
                    circlePageIndicator.setFillColor(Color.parseColor("#155DB3"));
                } else if (position == 1) {
                    circlePageIndicator.setFillColor(Color.parseColor("#42D683"));
                } else if (position == 2) {
                    circlePageIndicator.setFillColor(Color.parseColor("#BB69C8"));
                } else if (position == 3) {
                    circlePageIndicator.setFillColor(Color.parseColor("#FEC207"));
                } else {
                    circlePageIndicator.setFillColor(Color.parseColor("#F47C20"));
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return rootView;
    }


}