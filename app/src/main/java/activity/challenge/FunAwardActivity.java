package activity.challenge;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.root.skor.R;

import java.util.List;

import CustomClass.RobotoRegularTextView;
import adaptor.FunAwardAdaptor;
import adaptor.QRCodeListAdaptor;
import constants.Constants;
import database.SharedDatabase;
import de.hdodenhof.circleimageview.CircleImageView;
import interfaceSkor.ApiInterface;
import model.Challenge;
import model.GetParticipantsResponse;
import model.Participants;
import retrofit2.Call;
import retrofit2.Callback;
import util.RetrofitClient;
import utils.AppController;
import utils.Loader;

/**
 * Created by biresh.singh on 17-06-2018.
 */

public class FunAwardActivity extends AppCompatActivity implements View.OnClickListener {
    private Context mContext;
    private ImageButton btnBack;
    private TextView tvTitle;
    private RelativeLayout rlTop;
    private RecyclerView rvCompetitors;
    private GridLayoutManager gridLayoutManager;
    private FunAwardAdaptor mFunAwardAdaptor = null;
    private EditText etSearch;
    private String challengetype;
    Challenge clng;
    private RobotoRegularTextView txtNoDate;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fun_award);
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

            }
        }
        GetCollegueLists(clng.getid());
        initView();
    }

    private void GetCollegueLists(int ChallengeID)
    {
        //Loader.showProgressDialog(mContext);
        final SharedDatabase sharedDatabase = new SharedDatabase(this);
        String token = sharedDatabase.getToken();
        ApiInterface apiService= RetrofitClient.getClient().create(ApiInterface.class);;


        Call<GetParticipantsResponse> call = apiService.Getinvitations(ChallengeID, AppController.useragent,"Token "+token+"");
        call.enqueue(new Callback<GetParticipantsResponse>() {
            @Override
            public void onResponse(Call<GetParticipantsResponse> call, retrofit2.Response<GetParticipantsResponse> response) {
                try{
                    if(response.body()!=null) {
                        List<Participants> lstParticipants = response.body().getResult();

                        if (lstParticipants != null && lstParticipants.size() > 0) {

                            rvCompetitors.setVisibility(View.VISIBLE);

                            mFunAwardAdaptor.setData(lstParticipants);
                            txtNoDate.setVisibility(View.GONE);

                        }
                        else
                        {

                            rvCompetitors.setVisibility(View.GONE);
                            txtNoDate.setVisibility(View.VISIBLE);
                        }
                        // Loader.dialogDissmiss(mContext);
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
                Loader.dialogDissmiss(mContext);
            }
        });
    }

    private void initView() {
        mContext = this;
        btnBack = (ImageButton) findViewById(R.id.btnBack);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        rlTop = (RelativeLayout) findViewById(R.id.rlTop);
        etSearch=(EditText) findViewById(R.id.etSearch);
        txtNoDate= (RobotoRegularTextView) findViewById(R.id.txtNoDate);
        rvCompetitors = (RecyclerView) findViewById(R.id.rvCompetitors);
        btnBack.setOnClickListener(this);
        /*mFunAwardAdaptor = new FunAwardAdaptor(mContext);

        gridLayoutManager = new GridLayoutManager(this,1);
        rvCompetitors.setHasFixedSize(true);
        //rvCompetitors.setItemAnimator(new DefaultItemAnimator());
        rvCompetitors.setLayoutManager(gridLayoutManager);
        rvCompetitors.setAdapter(mFunAwardAdaptor);*/
        tvTitle.setText(clng.getChallengetitle());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        rvCompetitors.setLayoutManager(gridLayoutManager);
        rvCompetitors.setHasFixedSize(true);
        rvCompetitors.setNestedScrollingEnabled(false);
        mFunAwardAdaptor = new FunAwardAdaptor(mContext);
        rvCompetitors.setAdapter(mFunAwardAdaptor);


       /* rvCompetitors.setItemAnimator(new DefaultItemAnimator());
        rvCompetitors.setAdapter(mFunAwardAdaptor);*/

        etSearch.requestFocus();


    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                finish();
                break;
        }
    }
}
