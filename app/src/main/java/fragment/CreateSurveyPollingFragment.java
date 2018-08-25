package fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.root.skor.R;

import java.util.HashMap;

import activity.surveypolling.Constants;
import activity.surveypolling.CreateSurveyPollingDetailActivity;
import activity.surveypolling.MySurveyActivity;
import activity.surveypolling.VerifyRequestActivity;
import activity.userprofile.LoginActivity;
import activity.userprofile.MainActivity;
import cz.msebera.android.httpclient.Header;
import database.SharedDatabase;
import singleton.SettingsManager;
import singleton.UserManager;

public class CreateSurveyPollingFragment extends Fragment {

    //Layout
    private RelativeLayout mVerifyRequestLayout;
    private TextView mVerifyRequestNumberLabel;
    private Dialog mDialog;
    private Button createSurveyButton, createPollingButton, mySurveyButton;
    LinearLayout toolbarLinearLayout;
    private NavigationDrawerFragment navigationDrawerFragment;


    //Variable
    private int mUserType = Constants.TYPE_USER;
    //Intent Request Code
    private static final int CREATE_SURVEY = 1;
    private static final int CREATE_POLLING = 2;
    private static final int VERIFY_REQUEST = 3;

    public CreateSurveyPollingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_survey_polling, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initLayout(view);
        initEvent();
    }

    protected void initData() {
        Bundle arguments = getArguments();
        mUserType = arguments.getInt(Constants.PARAM_USER_TYPE, Constants.TYPE_USER);
    }

    protected void initLayout(View view) {
        toolbarLinearLayout = (LinearLayout) view.findViewById(R.id.fragment_create_survey_polling_toolbarMenuPanelLinearLayout);
        mVerifyRequestLayout = (RelativeLayout) view.findViewById(R.id.verify_request_layout);
        mVerifyRequestNumberLabel = (TextView) view.findViewById(R.id.verify_request_number_label);
        createSurveyButton = (Button) view.findViewById(R.id.fragment_create_survey_polling_createSurveyButton);
        createPollingButton = (Button) view.findViewById(R.id.fragment_create_survey_polling_createPollingButton);
        mySurveyButton = (Button) view.findViewById(R.id.fragment_create_survey_polling_mySurveyButton);

        mVerifyRequestNumberLabel.setVisibility(View.GONE);

        //Show/Hide verify request
        if (mUserType == Constants.TYPE_ADMIN)
            mVerifyRequestLayout.setVisibility(View.VISIBLE);
        else
            mVerifyRequestLayout.setVisibility(View.GONE);

        createSurveyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CreateSurveyPollingDetailActivity.class);
                intent.putExtra(Constants.PARAM_MODE, Constants.MODE_SURVEY);
                intent.putExtra(Constants.PARAM_USER_TYPE, mUserType);
                startActivityForResult(intent, CREATE_SURVEY);
            }
        });

        createPollingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CreateSurveyPollingDetailActivity.class);
                intent.putExtra(Constants.PARAM_MODE, Constants.MODE_POLLING);
                intent.putExtra(Constants.PARAM_USER_TYPE, mUserType);
                startActivityForResult(intent, CREATE_POLLING);
            }
        });

        mVerifyRequestLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), VerifyRequestActivity.class);
                startActivityForResult(intent, VERIFY_REQUEST);
            }
        });

        mySurveyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), MySurveyActivity.class));
            }
        });
    }

    protected void initEvent() {
        toolbarLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });
    }

    private void callVerifyRequestBadgeApi() {
        constants.Constants.BASEURL = getResources().getString(R.string.base_url);
        final SharedDatabase sharedDatabase = new SharedDatabase(getContext());
        String token = sharedDatabase.getToken();

        mDialog = new Dialog(getContext());
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setCancelable(false);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.loader);
        mDialog.show();

        String authProvider = SettingsManager.getInstance().getAuthProvider();

        HashMap<String, String> paramMap = new HashMap<>();

        RequestParams params = new RequestParams(paramMap);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(30000);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("Content_Type", "application/x-www-form-urlencoded");
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("USER-AGENT", LoginActivity.useragent);
        client.get(constants.Constants.BASEURL + Constants.GET_VERIFY_BADGE_COUNT,
                params, new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String response) {
                        if (mDialog != null) {
                            mDialog.dismiss();
                            mDialog = null;
                        }

                        try {
                            //Check if response is a number
                            int badgeCount = Integer.parseInt(response);
                            //Validate count
                            if (badgeCount > 0) {
                                mVerifyRequestNumberLabel.setText(response);
                                mVerifyRequestNumberLabel.setVisibility(View.VISIBLE);
                            }
                            else
                                mVerifyRequestNumberLabel.setVisibility(View.GONE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        throwable.printStackTrace();
                        if (mDialog != null) {
                            mDialog.dismiss();
                            mDialog = null;
                        }

                        if (statusCode == 401) {
                            UserManager.getInstance().logOut(getActivity());
                        } else if (statusCode == 400) {

                        } else if (statusCode == 500) {

                        }
                    }
                });
    }
}
