package fragment.challenge;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.root.skor.R;

import java.util.ArrayList;
import java.util.List;

import CustomClass.RobotoRegularTextView;
import adaptor.IndividualAdaptor;
import adaptor.TeamAdaptor;
import database.SharedDatabase;
import interfaceSkor.ApiInterface;
import model.Challenge;
import model.GetAllChallenge;
import model.GetChallengesResponse;
import retrofit2.Call;
import retrofit2.Callback;
import util.RetrofitClient;
import utils.AppController;
import utils.Loader;

/**
 * Created by biresh.singh on 15-06-2018.
 */


@SuppressLint("ValidFragment")
public class TeamFragment extends Fragment {
    private Context mContext;
    private RecyclerView rvTeam;
    private TeamAdaptor teamAdaptor = null;
    private RobotoRegularTextView  txtNoDate;
    List<Challenge> mTeamList=new ArrayList<>();
    private String token;
    public TeamFragment(Context context) {
        this.mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team, container, false);
        final SharedDatabase sharedDatabase = new SharedDatabase(getContext());
        token = sharedDatabase.getToken();
        GetTeamChallenge(token);
        initView(view);
        return view;
    }

    private void initView(View view) {
        rvTeam = (RecyclerView) view.findViewById(R.id.rvTeam);
        txtNoDate= (RobotoRegularTextView) view.findViewById(R.id.txtNoDate);
       /* teamAdaptor = new TeamAdaptor(mContext);
        rvTeam.setItemAnimator(new DefaultItemAnimator());
        rvTeam.setAdapter(teamAdaptor);*/

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        rvTeam.setLayoutManager(gridLayoutManager);
        rvTeam.setHasFixedSize(true);
        rvTeam.setNestedScrollingEnabled(false);
        teamAdaptor = new TeamAdaptor(mContext);
        rvTeam.setAdapter(teamAdaptor);
    }

    public void updateUI()
    {
        GetTeamChallenge(token);
    }
    private void GetTeamChallenge(String token) {
        //Loader.showProgressDialog(getActivity());
        if(mTeamList.size()>0)
        {
            mTeamList.clear();
            teamAdaptor.setData(mTeamList);
        }
        ApiInterface apiService= RetrofitClient.getClient().create(ApiInterface.class);;


        Call<GetChallengesResponse> call = apiService.GetChallenges(AppController.useragent,"Token "+token+"");
        call.enqueue(new Callback<GetChallengesResponse>() {
            @Override
            public void onResponse(Call<GetChallengesResponse> call, retrofit2.Response<GetChallengesResponse> response) {
                try{
                    if(response.body()!=null) {
                        GetAllChallenge objCheckedIn = response.body().getResult();
                        List<Challenge> teamchallengeLists=objCheckedIn.getlstTeamchallenge();
                        if (teamchallengeLists != null && teamchallengeLists.size() > 0) {
                            rvTeam.setVisibility(View.VISIBLE);
                            txtNoDate.setVisibility(View.GONE);
                            mTeamList.addAll(teamchallengeLists);
                            teamAdaptor.setData(mTeamList);
                        }
                        else
                        {
                            rvTeam.setVisibility(View.GONE);
                            txtNoDate.setVisibility(View.VISIBLE);
                        }
                       // Loader.dialogDissmiss(getActivity());
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
            public void onFailure(Call<GetChallengesResponse>call, Throwable t) {
                // Log error here since request failed
                //String str="dsds";
            }
        });








    }

}

