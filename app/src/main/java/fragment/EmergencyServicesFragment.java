package fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.root.skor.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import InternetConnection.CheckInternetConnection;
import activity.userprofile.MainActivity;
import constants.Constants;
import database.SharedDatabase;
import event.RefreshTokenEvent;
import singleton.SettingsManager;
import singleton.UserManager;
import utils.AndroidDeviceNames;
import utils.AppController;
import utils.Loader;


public class EmergencyServicesFragment extends Fragment {
    TextView smslabel;
    TextView phoneLabel;
    TextView emaillabel;
    TextView emailTextView;
    TextView phoneTexzView;
    TextView sms;
    LinearLayout emerGencyCall;
    LinearLayout phoneCallLayout;
    LinearLayout smsLayout;
    LinearLayout emailLayout;
    CheckInternetConnection checkInternetConnection;
    CoordinatorLayout coordinatorLayout;
    public SharedDatabase sharedDatabase;
    public String token;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_emergency_service, null);
        LinearLayout panel = (LinearLayout) view.findViewById(R.id.menupanel);
        smslabel = (TextView) view.findViewById(R.id.sms_label);
        phoneLabel = (TextView) view.findViewById(R.id.phone_label);
        emaillabel = (TextView) view.findViewById(R.id.email_label);
        emailTextView = (TextView) view.findViewById(R.id.email);
        phoneTexzView = (TextView) view.findViewById(R.id.phonenumber);
        emerGencyCall = (LinearLayout) view.findViewById(R.id.emergency_call);
        phoneCallLayout = (LinearLayout) view.findViewById(R.id.phone_call_layout);
        smsLayout = (LinearLayout) view.findViewById(R.id.sms_layout);
        emailLayout = (LinearLayout) view.findViewById(R.id.email_layout);
        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.myCoordinatorLayout);
        sharedDatabase = new SharedDatabase(getActivity());
        token = sharedDatabase.getToken();
        checkInternetConnection = new CheckInternetConnection(getActivity());

        sms = (TextView) view.findViewById(R.id.sms);
        smslabel.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        phoneLabel.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        emaillabel.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        emailTextView.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        phoneTexzView.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        sms.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));

        panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });
        phoneTexzView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.isInternetConnection = false;
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneTexzView.getText().toString(), null));
                startActivity(intent);
            }
        });
        emerGencyCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.isInternetConnection = false;
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneTexzView.getText().toString(), null));
                startActivity(intent);
            }
        });
        emailTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.isInternetConnection = false;
                String message = "body of email" + "\n\n" + getString(R.string.intent_message);
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{emailTextView.getText().toString()});
                i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
                i.putExtra(Intent.EXTRA_TEXT, message);
               /* i.putExtra(Intent.EXTRA_TEXT, getString(R.string.intent_message));*/
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    if (coordinatorLayout != null) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "There are no email clients installed.", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.RED);
                    }
                }

            }
        });

        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.isInternetConnection = false;
                String number = sms.getText().toString();  // The number on which you want to send SMS
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", number, null)));
            }
        });
        phoneCallLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.isInternetConnection = false;
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneTexzView.getText().toString(), null));
                startActivity(intent);
            }
        });
        smsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.isInternetConnection = false;
                String number = sms.getText().toString();  // The number on which you want to send SMS
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", number, null)));
            }
        });
        emailLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.isInternetConnection = false;
                Intent i = new Intent(Intent.ACTION_SEND);
                String message = "body of email" + "\n\n" + getString(R.string.intent_message);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{emailTextView.getText().toString()});
                i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
                i.putExtra(Intent.EXTRA_TEXT, message);
              /*  i.putExtra(Intent.EXTRA_TEXT, R.string.intent_message);*/
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "There are no email clients installed.", Snackbar.LENGTH_LONG);
                    View snackBarView = snackbar.getView();
                    snackBarView.setBackgroundColor(Color.RED);
                    snackbar.show();
                }
            }
        });
        if (checkInternetConnection.isConnectingToInternet()) {
            try {

                callEmergencyServicesApi();
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (coordinatorLayout != null) {
                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                View snackBarView = snackbar.getView();
                snackBarView.setBackgroundColor(Color.RED);
                snackbar.show();
            }
            // Toast.makeText(getActivity(), "No Internet Connection.", Toast.LENGTH_LONG).show();
        }

        return view;
    }

    public String getVersionCode() {
        String versionCode = null;
        try {
            PackageManager manager = getActivity().getPackageManager();
            PackageInfo info = manager.getPackageInfo(getActivity().getPackageName(), 0);
            versionCode = String.valueOf(info.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;

    }

    /* ********************Calling Emergency Service Api for get Method**************************/

    public void callEmergencyServicesApi() {
        String authProvider = SettingsManager.getInstance().getAuthProvider();

        Loader.showProgressDialog(getActivity());
        HashMap<String, String> paramMap = new HashMap<String, String>();
        RequestParams params = new RequestParams(paramMap);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(80000);
        AndroidDeviceNames deviceNames = new AndroidDeviceNames(getActivity());
        String userAjent = "Skor/1 Android|" + deviceNames.getDeviceName() + "|" + deviceNames.getAPIVerison() + "|" + getVersionCode();
        client.addHeader("USER-AGENT", userAjent);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("Content-Type", "application/json");
        client.get(Constants.BASEURL + Constants.EMERGENCY_SERVICES, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONArray jsonArray) {
                super.onSuccess(statusCode, headers, jsonArray);

                Loader.dialogDissmiss(getActivity());
                try {


                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    phoneTexzView.setText(jsonObject.getString("phone"));
                    sms.setText(jsonObject.getString("sms_number"));
                    emailTextView.setText(jsonObject.getString("email"));

                } catch (JSONException ex) {
                    ex.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Loader.dialogDissmiss(getActivity());
                if (statusCode == 400) {
                    if (coordinatorLayout != null) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "" + errorResponse, Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.RED);
                    }
                }
                if (statusCode == 500) {
                    if (coordinatorLayout != null) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "We've encountered a technical error.our team is working on it. please try again later", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.RED);
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        AppController.getInstance().getMixpanelAPI().track("EmergencyService");
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe
    public void onRefreshTokenEvent(RefreshTokenEvent event) {
        if (event.message == null) {
            callEmergencyServicesApi();
        }else{
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
            alertDialog.setTitle("Error");
            alertDialog.setMessage(event.message);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//                    UserManager.getInstance().logOut();
                }
            });
        }
    }

}
