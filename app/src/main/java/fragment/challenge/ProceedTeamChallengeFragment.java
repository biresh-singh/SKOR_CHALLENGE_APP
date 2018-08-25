package fragment.challenge;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.root.skor.R;

import org.json.JSONException;
import org.json.JSONObject;

import CustomClass.RobotoRegularTextView;
import activity.challenge.FunAwardActivity;
import activity.challenge.GymCheckinDetailsActivity;
import activity.challenge.HealthyArticleDetailsActivity;
import activity.challenge.PhotoHuntActivity;
import activity.challenge.QRTreasureHuntActivity;
import activity.challenge.TeamInviteFriendsChallengeActivity;
import activity.challenge.WalkingDetailsActivity;
import activity.challenge.WriteArticleDetailsActivity;
import activity.challenge.WriteFunArticleChallengeActivity;
import activity.challenge.YogaClassDetailsActivity;
import activity.userprofile.MainActivity;
import adaptor.RulesAdaptor;
import constants.Constants;
import database.SharedDatabase;
import interfaceSkor.ApiInterface;
import model.Challenge;
import model.PostUserActivity;
import model.UserActivity;
import model.UserActivityResponse;
import retrofit2.Call;
import retrofit2.Callback;
import util.RetrofitClient;
import utils.Loader;

/**
 * Created by biresh.singh on 16-06-2018.
 */


@SuppressLint("ValidFragment")
public class ProceedTeamChallengeFragment extends Fragment implements View.OnClickListener{
    private Context mContext;
    private TextView tvProceedChallenge;
    private String Challengetype;
    private Challenge Challenge;
    private RulesAdaptor ruleAdaptor = null;
    private RecyclerView rvRules;
    public ProceedTeamChallengeFragment(Context context, String challengetype, Challenge mChallenge) {
        this.mContext = context;
        this.Challengetype=challengetype;
        this.Challenge=mChallenge;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_proceed_challenge, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {

        tvProceedChallenge = (TextView) view.findViewById(R.id.tvProceedChallenge);
        rvRules= (RecyclerView) view.findViewById(R.id.rvRules);
        RobotoRegularTextView txtNoDate= (RobotoRegularTextView) view.findViewById(R.id.txtNoDate);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        rvRules.setLayoutManager(gridLayoutManager);
        rvRules.setHasFixedSize(true);
        rvRules.setNestedScrollingEnabled(true);
        ruleAdaptor = new RulesAdaptor(mContext);
        rvRules.setAdapter(ruleAdaptor);

        if (Challenge.getRule().size() > 0) {
            ruleAdaptor.setData(Challenge.getRule());
            txtNoDate.setVisibility(View.GONE);
            rvRules.setVisibility(View.VISIBLE);
        }
        else
        {
            txtNoDate.setVisibility(View.VISIBLE);
            rvRules.setVisibility(View.GONE);
        }

        tvProceedChallenge.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.tvProceedChallenge:
                final SharedDatabase sharedDatabase = new SharedDatabase(getContext());
                String token = sharedDatabase.getToken();
                int UserID=sharedDatabase.getUserPk();

                ApiJsonMap(Challenge.getid(),UserID,token);

                /*Intent intent=null;
                if(Challengetype.equals(Constants.TREASUREHUNT))
                {
                    intent= new Intent(mContext, QRTreasureHuntActivity.class);
                }
                else if(Challengetype.equals(Constants.YOGACLASS))
                {
                    intent= new Intent(mContext, YogaClassDetailsActivity.class);
                }
                else if(Challengetype.equals(Constants.FUNNIESTARTICLE))
                {
                    intent= new Intent(mContext, WriteArticleDetailsActivity.class);
                }
                else if(Challengetype.equals(Constants.HEALTHYLIVINGARTICLES))
                {
                    intent= new Intent(mContext, HealthyArticleDetailsActivity.class);
                }
                else if(Challengetype.equals(Constants.GYMCHECKIN))
                {
                    intent= new Intent(mContext, GymCheckinDetailsActivity.class);
                }
                else if(Challengetype.equals(Constants.PHOTOHUNT))
                {
                    intent= new Intent(mContext, PhotoHuntActivity.class);
                }
                else if(Challengetype.equals(Constants.WALKINGACTIVITY))
                {
                    intent= new Intent(mContext, WalkingDetailsActivity.class);
                }
                else if(Challengetype.equals(Constants.EMPLOYEEFUN))
                {
                    intent= new Intent(mContext, FunAwardActivity.class);
                }
                startActivity(intent);*/
             /*   Intent intent = new Intent(mContext, QRScannerDetailsActivity.class);
                startActivity(intent);*/
                break;
        }
    }


    private JsonObject ApiJsonMap(int ChallengeID, int UserID, String Token) {

        JsonObject gsonObject = new JsonObject();
        try {
            PostUserActivity objUserActivity=new PostUserActivity();
            JSONObject jsonUserAObj_ = new JSONObject();

            JSONObject jsonObj_ = new JSONObject();
            jsonObj_.put("challenge", ChallengeID);
            jsonObj_.put("user_activity", jsonUserAObj_);
            jsonObj_.put("status", 0);
            jsonObj_.put("user", UserID);


            JsonParser jsonParser = new JsonParser();
            gsonObject = (JsonObject) jsonParser.parse(jsonObj_.toString());
            ProceedChallenge(gsonObject,Token);


        } catch (JSONException e) {
            // e.printStackTrace();
        }

        return gsonObject;
    }


    private void ProceedChallenge(JsonObject objJson,String token)
    {
        Loader.showProgressDialog(getActivity());

        ApiInterface apiService= RetrofitClient.getClient().create(ApiInterface.class);;


        Call<UserActivityResponse> call = apiService.ProceedChallenge(objJson,"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36","Token "+token+"","application/json");
        call.enqueue(new Callback<UserActivityResponse>() {
            @Override
            public void onResponse(Call<UserActivityResponse> call, retrofit2.Response<UserActivityResponse> response) {
                try{
                    if(response.body()!=null) {
                        UserActivity objActivity = response.body().getResult();
                        /*SnackbarManager.show(Snackbar.with(mContext).text("Challenge has been accepted successfully").textColor(Color.WHITE)
                                .color(Color.parseColor("#FF9B30")));*/
                        ConfirmationPopup(getActivity());
                        //getActivity().finish();




                        /*Intent intent=null;
                        if(Challenge.getValidationID()==1)
                        {
                            intent= new Intent(mContext, WalkingDetailsActivity.class);
                        }
                        intent.putExtra(Constants.GETCHALLENGEDATA,Challenge);
                        startActivity(intent);*/

                        /*if (lstParticipants != null && lstParticipants.size() > 0) {
                            tvParticipant.setHint(lstParticipants.size()+" Participants");
                        }
                        else
                        {
                            tvParticipant.setHint(lstParticipants.size()+" Participants");
                        }*/
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
            public void onFailure(Call<UserActivityResponse>call, Throwable t) {
                // Log error here since request failed
                Loader.dialogDissmiss(getActivity());
            }
        });
    }

    private void ConfirmationPopup(Activity context) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_team_challenge);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
        wlp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(wlp);
        dialog.show();



        TextView tvMyChallenge = (TextView) dialog.findViewById(R.id.tvMyChallenge);
        TextView tvMessage = (TextView) dialog.findViewById(R.id.tvMessage);
        ImageView ivClose = (ImageView) dialog.findViewById(R.id.ivClose);

        String text_view_str = "You have taken a <b> team challenge</b>, so build your team by inviting your friends to the team.";
        tvMessage.setText(Html.fromHtml(text_view_str));

        ivClose.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          dialog.dismiss();
                                      }
                                  });

        tvMyChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
             /*   Intent intent = new Intent(getContext(), TeamInviteFriendsChallengeActivity.class);

                startActivity(intent);*/

                Bundle bundle=new Bundle();
                Intent intent = new Intent(mContext, TeamInviteFriendsChallengeActivity.class);
                intent.putExtra(Constants.CHALLENGETYPE,Constants.TREASUREHUNT);
                intent.putExtra(Constants.GETCHALLENGEDATA,Challenge);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });

      /*  tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(WriteFunArticleChallengeActivity.this, MainActivity.class);
                intent.putExtra("action", Constants.ACTION_NONE);
                *//*intent.putExtra("screen", "individual");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);*//*
                startActivity(intent);
                finish();
            }
        });
        tvMyChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(WriteFunArticleChallengeActivity.this, MainActivity.class);
                intent.putExtra("action", Constants.ACTION_NONE);
                *//*intent.putExtra("screen", "myChallenge");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);*//*
                startActivity(intent);
                finish();
            }
        });*/
    }
}

