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
import adaptor.MyChallengeAdaptor;
import database.SharedDatabase;
import interfaceSkor.ApiInterface;
import model.Challenge;
import model.GetAllChallenge;
import model.GetChallengesResponse;
import model.MyChallenge;
import model.MyChallengesResponse;
import retrofit2.Call;
import retrofit2.Callback;
import util.RetrofitClient;
import utils.AppController;
import utils.Loader;

/**
 * Created by biresh.singh on 15-06-2018.
 */

@SuppressLint("ValidFragment")
public class MyChallengeFragment extends Fragment {
    private Context mContext;
    private RecyclerView rvChallenge;
    private MyChallengeAdaptor myChallengeAdaptor = null;
    private RobotoRegularTextView  txtNoDate;
    List<MyChallenge> mMyList=new ArrayList<>();
    public List<MyChallenge> mychallengeLists;
    private static MyChallengeFragment instance = null;
    private String token;
    public MyChallengeFragment(Context context) {
        this.mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_challenge, container, false);

        final SharedDatabase sharedDatabase = new SharedDatabase(getContext());
        token = sharedDatabase.getToken();
        //GetMyChallenge(token);
        initView(view);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
    }

    public static MyChallengeFragment getInstance() {
        return instance;
    }
    private void GetMyChallenge(String token) {
        //Loader.showProgressDialog(getActivity());
        if(mMyList.size()>0)
        {
            mMyList.clear();
            myChallengeAdaptor.setData(mMyList);
        }
        ApiInterface apiService= RetrofitClient.getClient().create(ApiInterface.class);;


        Call<MyChallengesResponse> call = apiService.GetMyChallenges(AppController.useragent,"Token "+token+"");
        call.enqueue(new Callback<MyChallengesResponse>() {
            @Override
            public void onResponse(Call<MyChallengesResponse> call, retrofit2.Response<MyChallengesResponse> response) {
                try{
                    if(response.body()!=null) {
                        //Loader.dialogDissmiss(getActivity());
                        mychallengeLists=response.body().getResult();
                        if (mychallengeLists != null && mychallengeLists.size() > 0) {
                            rvChallenge.setVisibility(View.VISIBLE);
                            txtNoDate.setVisibility(View.GONE);
                            mMyList.addAll(mychallengeLists);
                            myChallengeAdaptor.setData(mMyList);
                        }
                        else
                        {
                            rvChallenge.setVisibility(View.GONE);
                            txtNoDate.setVisibility(View.VISIBLE);
                        }

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
            public void onFailure(Call<MyChallengesResponse>call, Throwable t) {
                // Log error here since request failed
                Loader.dialogDissmiss(getActivity());
            }
        });








    }

    public void updateUI()
    {
        GetMyChallenge(token);
    }

    @Override
    public void onResume()
    {
        //do the data changes. In this case, I am refreshing the arrayList cart_list and then calling the listview to refresh.
        GetMyChallenge(token);
        super.onResume();
    }

    /*@Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser)
        {
            GetDashboardDetail();
          *//*  final Handler handler = new Handler();
            final int delay = 10000; //milliseconds

            handler.postDelayed(new Runnable(){
                public void run(){
                    //do something
                    GetDashboardDetail();
                    handler.postDelayed(this, delay);
                }
            }, delay);*//*
        }

    }*/


    private void initView(View view) {
        rvChallenge = (RecyclerView) view.findViewById(R.id.rvChallenge);
        txtNoDate= (RobotoRegularTextView) view.findViewById(R.id.txtNoDate);
     /*   myChallengeAdaptor = new MyChallengeAdaptor(mContext);
        rvChallenge.setItemAnimator(new DefaultItemAnimator());
        rvChallenge.setAdapter(myChallengeAdaptor);*/

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        rvChallenge.setLayoutManager(gridLayoutManager);
        rvChallenge.setHasFixedSize(true);
        rvChallenge.setNestedScrollingEnabled(false);
        myChallengeAdaptor = new MyChallengeAdaptor(mContext);
        rvChallenge.setItemAnimator(new DefaultItemAnimator());
        rvChallenge.setAdapter(myChallengeAdaptor);
    }
}
