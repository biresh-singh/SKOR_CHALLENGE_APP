package fragment.challenge;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.root.skor.R;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import CustomClass.RobotoRegularTextView;
import constants.Constants;
import database.SharedDatabase;
import interfaceSkor.ApiInterface;
import listener.NavigationDeleget;
import model.Challenge;
import model.GetParticipantsResponse;
import model.Participants;
import model.Rewards;
import retrofit2.Call;
import retrofit2.Callback;
import util.RetrofitClient;
import utils.AppController;
import utils.Loader;

/**
 * Created by biresh.singh on 16-06-2018.
 */


@SuppressLint("ValidFragment")
public class TakeTeamChallengeFragment extends Fragment implements View.OnClickListener{
    private Context mContext;
    private NavigationDeleget mNavigationDeleget = null;
    private TextView tvTakeChallenge,tvChalengeTypeTitle,tvChallengeDesc,tvChallengeTarget,tvChallengeDays,tvChallengeExpires,tvPoints;
    private String Challengetype;
    private Challenge Challenge;
    private Rewards rewards;
    private RobotoRegularTextView tvParticipant;
    public TakeTeamChallengeFragment(Context context, String challengetype, Challenge mChallenge, Rewards mRewards) {
        this.mContext = context;
        this.mNavigationDeleget = (NavigationDeleget) mContext;
        this.Challengetype=challengetype;
        this.Challenge=mChallenge;
        this.rewards=mRewards;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_take_team_challenge, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        tvTakeChallenge = (TextView) view.findViewById(R.id.tvTakeChallenge);
        tvChalengeTypeTitle= (TextView) view.findViewById(R.id.tvChalengeTypeTitle);
        tvPoints= (TextView) view.findViewById(R.id.tvPoints);
        tvChallengeDesc= (TextView) view.findViewById(R.id.tvChallengeDesc);
        tvChallengeTarget= (TextView) view.findViewById(R.id.tvChallengeTarget);
        tvChallengeDays= (TextView) view.findViewById(R.id.tvChallengeTeamDays);
        tvChallengeExpires= (TextView) view.findViewById(R.id.tvChallengeTeamExpires);
        tvParticipant= (RobotoRegularTextView) view.findViewById(R.id.tvParticipant);

        tvChalengeTypeTitle.setText(Challenge.getChallengetitle());
        tvChallengeDesc.setText(Challenge.getDescription());
        tvPoints.setText("+"+ NumberFormat.getInstance().format(Challenge.getValue())+" "+rewards.getName());
        tvChallengeExpires.setText(Challenge.getEnddatetime());

        GetParticipant(Challenge.getid());
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");


        Date today = new Date();
        try {
            Date Expiredate = inputFormat.parse(Challenge.getEnddatetime().toString());
            Date Startdate = inputFormat.parse(Challenge.getStartdatetime().toString());
            String formattedExpireDate = outputFormat.format(Expiredate);
            String formattedStartDate = outputFormat.format(Startdate);
            long i=getDaysBetweenDates(Expiredate,Startdate);



            tvChallengeExpires.setText(formattedExpireDate);
            tvChallengeDays.setText(i+" Day");
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        /*if(Challengetype.equals(Constants.TREASUREHUNT))
        {
            tvChalengeTypeTitle.setText("Treasure Hunt | Find QR Codes");
            tvChallengeDesc.setText("Find QR codes that placed on some areas the one who find all firsts are the winner and challenge will close automatically.");
            tvChallengeTarget.setText("10 QR Code");
            tvChallengeDays.setText("1 Day");
            tvChallengeExpires.setText("27/04/18");
        }
        else if(Challengetype.equals(Constants.YOGACLASS))
        {
            tvChalengeTypeTitle.setText("Yoga class in office");
            tvChallengeDesc.setText("Attend 10 consecutive yoga class. indisd sidnsdnsds dsd sdn sdnskdnskdnsdjk  sjk dskjdssjd sjd skd.");
            tvChallengeTarget.setText("10 Class");
            tvChallengeDays.setText("10 Days");
            tvChallengeExpires.setText("27/04/18");
        }
        else if(Challengetype.equals(Constants.FUNNIESTARTICLE))
        {
            tvChalengeTypeTitle.setText("Create funniest article");
            tvPoints.setText("+1000 Stars/100 Likes");
            tvPoints.setTextColor(getResources().getColor(R.color.gplus_color_4));
            tvChallengeDesc.setText("Create funniest article and upload to SKOR news feed indisd sidnsdnsds dsd sdn sdnskdnskdnsdjk  sjk dskjdssjd sjd skd.");
            tvChallengeTarget.setText("1 Article");
            tvChallengeDays.setText("10 Days");
            tvChallengeExpires.setText("20/05/18");
        }
        else if(Challengetype.equals(Constants.EMPLOYEEFUN))
        {
            tvChalengeTypeTitle.setText("Employee Fun Award");
            tvPoints.setText("+50,000 Points");
            tvPoints.setTextColor(getResources().getColor(R.color.holo_blue_dark));
            tvChallengeDesc.setText("sdds dsd sd sld sd sd sd skd sdk skindisd sidnsdnsds dsd sdn sdnskdnskdnsdjk  sjk dskjdssjd sjd skd.");
            tvChallengeTarget.setText("1 Vote");
            tvChallengeDays.setText("1 Day");
            tvChallengeExpires.setText("27/04/18");
        }
        else if(Challengetype.equals(Constants.GYMCHECKIN))
        {
            tvChalengeTypeTitle.setText("Check in to Gym");
            tvPoints.setText("+7,000 Points");
            tvPoints.setTextColor(getResources().getColor(R.color.gplus_color_4));
            tvChallengeDesc.setText("Check in to Gym/Health facility every week. indisd sidnsdnsds dsd sdn sdnskdnskdnsdjk  sjk dskjdssjd sjd skd.");
            tvChallengeTarget.setText("20 Check In");
            tvChallengeDays.setText("4 Weeks");
            tvChallengeExpires.setText("27/04/18");
        }
        else if(Challengetype.equals(Constants.PHOTOHUNT))
        {
            tvChalengeTypeTitle.setText("Photo hunt contest");
            tvPoints.setText("+10,000 Points");
            tvPoints.setTextColor(getResources().getColor(R.color.holo_blue_dark));
            tvChallengeDesc.setText("Team/Individual using 3rd party AR apps take. indisd sidnsdnsds dsd sdn sdnskdnskdnsdjk  sjk dskjdssjd sjd skd.");
            tvChallengeTarget.setText("20 Images");
            tvChallengeDays.setText("1 Day");
            tvChallengeExpires.setText("27/04/18");
        }
        else if(Challengetype.equals(Constants.HEALTHYLIVINGARTICLES))
        {
            tvChalengeTypeTitle.setText("Read healthy living article");
            tvPoints.setText("+5,000 Points");
            tvPoints.setTextColor(getResources().getColor(R.color.gplus_color_4));
            tvChallengeDesc.setText("Reand and comment 5 healthy category article in SKOR News timeline");
            tvChallengeTarget.setText("5 Comment");
            tvChallengeDays.setText("5 Days");
            tvChallengeExpires.setText("27/04/18");
        }
        else if(Challengetype.equals(Constants.WALKINGACTIVITY))
        {
            tvChalengeTypeTitle.setText("Walking activity in office area");
            tvPoints.setText("10,000 Points");
            tvPoints.setTextColor(getResources().getColor(R.color.gplus_color_4));
            tvChallengeDesc.setText("Walk min 2000 steps/day in your office area. dskdnso dnskdn skdnskd  skd  ");
            tvChallengeTarget.setText("2000 Steps");
            tvChallengeDays.setText("10 Days");
            tvChallengeExpires.setText("27/04/18");
        }*/
        tvTakeChallenge.setOnClickListener(this);
    }

    public long getDaysBetweenDates(Date d1, Date d2){
        return TimeUnit.MILLISECONDS.toDays(d1.getTime() - d2.getTime());
    }


    private void GetParticipant(int ChallengeID)
    {
        Loader.showProgressDialog(getActivity());
        final SharedDatabase sharedDatabase = new SharedDatabase(getContext());
        String token = sharedDatabase.getToken();
        ApiInterface apiService= RetrofitClient.getClient().create(ApiInterface.class);;


        Call<GetParticipantsResponse> call = apiService.GetParticipants(ChallengeID, AppController.useragent,"Token "+token+"");
        call.enqueue(new Callback<GetParticipantsResponse>() {
            @Override
            public void onResponse(Call<GetParticipantsResponse> call, retrofit2.Response<GetParticipantsResponse> response) {
                try{
                    if(response.body()!=null) {
                        List<Participants> lstParticipants = response.body().getResult();

                        if (lstParticipants != null && lstParticipants.size() > 0) {
                            tvParticipant.setHint(lstParticipants.size()+" Participants");
                        }
                        else
                        {
                            tvParticipant.setHint(lstParticipants.size()+" Participants");
                        }
                        Loader.dialogDissmiss(getActivity());
                    }
                    else
                    {

                    }

                }

                catch (NullPointerException ex)
                {

                }
                catch (IndexOutOfBoundsException ex)
                {

                }
                catch(IllegalArgumentException ex)
                {

                }
                catch (Exception ex) {

                }
            }



            @Override
            public void onFailure(Call<GetParticipantsResponse>call, Throwable t) {
                // Log error here since request failed
                Loader.dialogDissmiss(getActivity());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.tvTakeChallenge:
                mNavigationDeleget.executeFragment(Constants.ProceedTeamChallengeFragment,null);
                break;
        }

    }
}
