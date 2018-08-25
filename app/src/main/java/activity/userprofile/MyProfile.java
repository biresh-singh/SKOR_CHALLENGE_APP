package activity.userprofile;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.root.skor.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import InternetConnection.CheckInternetConnection;
import activity.skorchat.PreviewActivity;
import constants.Constants;
import database.SharedDatabase;
import event.RefreshTokenEvent;
import singleton.SettingsManager;
import singleton.UserManager;
import utils.AndroidDeviceNames;
import utils.AppController;
import utils.CircleImageView;
import utils.Loader;


public class MyProfile extends Activity {
    LinearLayout back;
    TextView edit, changePhoto, desc_text, finance, Email_id;
    CircleImageView profilePic;
    ImageView logoutButton;
    CheckInternetConnection checkInternetConnection;
    public static boolean isEditprofile = false;
    AndroidDeviceNames deviceNames;
    public String token;
    public SharedDatabase sharedDatabase;
    QBChatService qbChatService = QBChatService.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myprofile);
        logoutButton = (ImageView) findViewById(R.id.logout);
        back = (LinearLayout) findViewById(R.id.back);
        edit = (TextView) findViewById(R.id.edit);
        changePhoto = (TextView) findViewById(R.id.announcement_from);
        desc_text = (TextView) findViewById(R.id.desc_text);
        finance = (TextView) findViewById(R.id.finance);
        deviceNames = new AndroidDeviceNames(MyProfile.this);
        sharedDatabase = new SharedDatabase(getApplicationContext());
        token = sharedDatabase.getToken();

        Email_id = (TextView) findViewById(R.id.email_id);
        profilePic = (CircleImageView) findViewById(R.id.imageurl);
        profilePic.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        checkInternetConnection = new CheckInternetConnection(getApplicationContext());

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEditprofile = true;
                MainActivity.isInternetConnection = false;
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("action", Constants.ACTION_NONE);
                startActivity(intent);
                finish();


            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditProfile.class);
                startActivity(intent);
                finish();

            }
        });
        if (checkInternetConnection.isConnectingToInternet()) {
            try {
                callProfileAPI();
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            connectivityMessage("Waiting for Network!");
        }
    }

    public void connectivityMessage(String msg) {

        if (msg.equals("Network Connecting....")) {
            if (getApplicationContext() != null) {
                SnackbarManager.show(Snackbar.with(getApplicationContext()).text(msg).textColor(Color.WHITE)
                        .color(Color.parseColor("#FF9B30")), this);
            }
        } else if (msg.equals("Network Connected")) {
            if (getApplicationContext() != null) {
                SnackbarManager.show(Snackbar.with(getApplicationContext()).text(msg).textColor(Color.WHITE)
                        .color(Color.parseColor("#4BCC1F")), this);
            }
        } else {
            if (getApplicationContext() != null) {
                SnackbarManager.show(Snackbar.with(getApplicationContext()).text(msg).textColor(Color.WHITE)
                        .color(Color.RED), this);
            }
        }

    }

    public void UpdateUi(JSONObject jsonObject) {
        try {
            final String imageUrl = Constants.BASEURL + jsonObject.getString("profile_pic_url");
            desc_text.setText(jsonObject.getString("first_name") + " " + jsonObject.getString("last_name"));
            finance.setText(jsonObject.getString("organization_name"));
            Email_id.setText(jsonObject.getString("email"));
            Glide.with(getApplicationContext()).load(imageUrl).into(profilePic);
            sharedDatabase.setFirstName(jsonObject.getString("first_name"));
            sharedDatabase.setProfilePic(jsonObject.getString("profile_pic_url"));


            profilePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplication(), PreviewActivity.class);
                    intent.putExtra("url", imageUrl);
                    intent.putExtra("name", "profile");
                    intent.putExtra("type", "image");
                    startActivity(intent);
                }
            });
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }
    /* ********************Calling My Profile Api**************************/

    public void callProfileAPI() {
        String authProvider = SettingsManager.getInstance().getAuthProvider();

        Loader.showProgressDialog(this);
        HashMap<String, String> paramMap = new HashMap<String, String>();

        RequestParams params = new RequestParams(paramMap);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(80000);
        client.addHeader("USER-AGENT", AppController.useragent);
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("Content-Type", "application/json");
        client.get(Constants.BASEURL + Constants.EDITPROFILE, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject jsonObject) {
                super.onSuccess(statusCode, headers, jsonObject);
                if (getApplicationContext() != null) {
                    Loader.dialogDissmiss(getApplicationContext());
                    UpdateUi(jsonObject);
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Loader.dialogDissmiss(getApplicationContext());
                if (statusCode == 401) {
                    UserManager.getInstance().logOut(MyProfile.this);
                }

                if (statusCode == 400) {
                    if (getApplicationContext() != null) {
                        Toast.makeText(getApplicationContext(), "" + errorResponse, Toast.LENGTH_LONG).show();
                    }
                }
                if (statusCode == 500) {
                    if (getApplicationContext() != null) {
                        Toast.makeText(getApplicationContext(), "We've encountered a technical error.our team is working on it. please try again later", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("action", Constants.ACTION_NONE);
        startActivity(intent);
        finish();
        super.onBackPressed();
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
            try {
                callProfileAPI();
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
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

    private void logout(){
//        boolean isLoggedIn = qbChatService.isLoggedIn();
//        if(!isLoggedIn){
//            return;
//        }

        final Intent intent = new Intent(MyProfile.this, LoginActivity.class);
        AppController.getInstance().getMixpanelAPI().track("Logout");
        qbChatService.logout(new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                qbChatService.destroy();
                sharedDatabase.clearPref();
                AppController.getInstance().clearRealmCache();
                QBUsers.signOut().performAsync(new QBEntityCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid, Bundle bundle) {
                        AppController.getInstance().clearApplicationData();
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        AppController.getInstance().clearApplicationData();
                        startActivity(intent);
                        finish();
                    }
                });

            }

            @Override
            public void onError(QBResponseException e) {
                AppController.getInstance().clearApplicationData();
                startActivity(intent);
                finish();
            }
        });



    }
}
