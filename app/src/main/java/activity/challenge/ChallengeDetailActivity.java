package activity.challenge;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.root.skor.R;

import constants.Constants;
import fragment.challenge.ProceedChallengeFragment;
import fragment.challenge.TakeChallengeFragment;
import listener.NavigationDeleget;
import model.Challenge;
import model.Rewards;
import utils.FontManager;

/**
 * Created by biresh.singh on 16-06-2018.
 */


public class ChallengeDetailActivity extends FragmentActivity implements NavigationDeleget, View.OnClickListener {
    private Context mContext;
    private Fragment mFragment = null;
    private FragmentManager mFragmentManager = null;
    private FragmentTransaction fragmentTransaction = null;
    private NavigationDeleget mNavigationDeleget;
    private String challengetype;
    private ImageButton btnBack;
    Challenge clng;
    Rewards rewards;
    private ImageView imgCenter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_detail);
        if(getIntent()!=null)
        {
            Bundle bundle=getIntent().getExtras();
            if(bundle!=null)
            {

                if(bundle.containsKey(Constants.CHALLENGETYPE))
                {
                    challengetype=bundle.getString(Constants.CHALLENGETYPE);
                }
                if(bundle.containsKey(Constants.GETCHALLENGEDATA))
                {
                    clng=bundle.getParcelable(Constants.GETCHALLENGEDATA);

                }
                if(bundle.containsKey(Constants.GETCHALLENGEREWARDSDATA))
                {
                    rewards=bundle.getParcelable(Constants.GETCHALLENGEREWARDSDATA);

                }
            }
        }
        initView();
    }

    private void initView() {
        mContext = this;
        mNavigationDeleget = (NavigationDeleget) mContext;
        mFragmentManager = getSupportFragmentManager();

        btnBack = (ImageButton) findViewById(R.id.btnBack);
      /*  imgCenter = (ImageView) findViewById(R.id.imgCenter);*/

        /*imgCenter.bringToFront();

        if(challengetype.equals(Constants.TREASUREHUNT))
        {
            imgCenter.setImageDrawable(getResources().getDrawable(R.drawable.treasurehunt));
        }
        else if(challengetype.equals(Constants.YOGACLASS))
        {
            imgCenter.setImageDrawable(getResources().getDrawable(R.drawable.yogaclass));
        }
        else if(challengetype.equals(Constants.FUNNIESTARTICLE))
        {
            imgCenter.setImageDrawable(getResources().getDrawable(R.drawable.yogaclass));
        }
        else if(challengetype.equals(Constants.EMPLOYEEFUN))
        {
            imgCenter.setImageDrawable(getResources().getDrawable(R.drawable.employee_award_icon));
        }
        else if(challengetype.equals(Constants.GYMCHECKIN))
        {
            imgCenter.setImageDrawable(getResources().getDrawable(R.drawable.dumbell_icon));
        }
        else if(challengetype.equals(Constants.PHOTOHUNT))
        {
            imgCenter.setImageDrawable(getResources().getDrawable(R.drawable.dumbell_icon));
        }
        else if(challengetype.equals(Constants.HEALTHYLIVINGARTICLES))
        {
            imgCenter.setImageDrawable(getResources().getDrawable(R.drawable.dumbell_icon));
        }
        else if(challengetype.equals(Constants.WALKINGACTIVITY))
        {
            imgCenter.setImageDrawable(getResources().getDrawable(R.drawable.run_along_icon));
        }*/

        TextView tvChallengeIcon= (TextView) findViewById(R.id.challengeIcon);
        Typeface iconFont = FontManager.getTypeface(mContext, FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(tvChallengeIcon, iconFont);

        tvChallengeIcon.setText(mContext.getResources().getString(R.string.fa_icon_areachart));
        tvChallengeIcon.bringToFront();
        btnBack.setOnClickListener(this);

        displayView(Constants.TakeChallengeFragment,null);

        MixpanelAPI mixpanel=MixpanelAPI.getInstance(this,getResources().getString(R.string.mixpanel_token));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment currentFragment = fragmentManager.findFragmentById(R.id.challengeContainer);
                if (currentFragment instanceof ProceedChallengeFragment) {
                    displayView(Constants.TakeChallengeFragment, null);
                }
                else
                {
                    finish();
                }
                break;
        }
    }

    @Override
    public void executeFragment(String fragmentName, Object obj) {
        displayView(fragmentName, obj);
    }

    @Override
    public void goBack() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void displayView(String fragmentName, Object obj) {
        fragmentTransaction = mFragmentManager.beginTransaction();

        if (fragmentName.equalsIgnoreCase(Constants.TakeChallengeFragment)) {
            mFragment = new TakeChallengeFragment(mContext,challengetype,clng,rewards);
        } else if (fragmentName.equalsIgnoreCase(Constants.ProceedChallengeFragment)) {
            mFragment = new ProceedChallengeFragment(mContext,challengetype,clng);
        }

        if (mFragment != null) {
            try {
                if (fragmentName.equals(Constants.TakeChallengeFragment)) {
                    fragmentTransaction.replace(R.id.challengeContainer, mFragment);
                    fragmentTransaction.commit();
                } else if (fragmentName.equals(Constants.ProceedChallengeFragment)) {
                    fragmentTransaction.replace(R.id.challengeContainer, mFragment).addToBackStack(fragmentName);
                    fragmentTransaction.commit();
                } else {
                    fragmentTransaction.replace(R.id.challengeContainer, mFragment).addToBackStack(fragmentName);
                    fragmentTransaction.commit();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

