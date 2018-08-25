package activity.userprofile;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.root.skor.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;

import java.util.HashMap;

import InternetConnection.CheckInternetConnection;
import constants.Constants;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import database.SharedDatabase;
import event.RefreshTokenEvent;
import singleton.SettingsManager;
import singleton.UserManager;
import utils.AppController;
import utils.Loader;


public class ChangePassword extends AppCompatActivity {
    EditText old_password1;
    EditText new_password1;
    EditText confirm_password1;
    Button save;
    RelativeLayout titlebar;
    ImageView back_button;

    public static String PACKAGE_NAME;

    HashMap<String, String> params = new HashMap<>();
    CheckInternetConnection checkInternetConnection;
    public SharedDatabase sharedDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        old_password1 = (EditText) findViewById(R.id.oldPassword);
        new_password1 = (EditText) findViewById(R.id.newPassword);
        confirm_password1 = (EditText) findViewById(R.id.confirmnewPassword);
        back_button = (ImageView) findViewById(R.id.back_button);
        PACKAGE_NAME = getApplicationContext().getPackageName();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(old_password1, InputMethodManager.SHOW_IMPLICIT);
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        checkInternetConnection = new CheckInternetConnection(getApplicationContext());
        sharedDatabase = new SharedDatabase(getApplicationContext());
        old_password1.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        new_password1.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        confirm_password1.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        titlebar = (RelativeLayout) findViewById(R.id.titlebar);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                MainActivity.isInternetConnection = false;
                finish();

            }
        });

        /* ****************Save Button Clicking LListener******************************/

        save = (Button) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (checkInternetConnection.isConnectingToInternet()) {
                                            if (new_password1.getText().toString().length() >= 8) {

                                                String old_password = old_password1.getText().toString().trim();
                                                String new_password = new_password1.getText().toString().trim();
                                                String confirm_password = confirm_password1.getText().toString().trim();
                                                if (new_password.equals(confirm_password)) {

                                                    params.put("old_password", old_password);
                                                    params.put("new_password", new_password);
                                                    try {
                                                        callChangePasswordAPI();
                                                    } catch (NullPointerException e) {
                                                        e.printStackTrace();
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                } else {
                                                    if (getApplicationContext() != null) {
                                                        connectivityMessage("New Password and Confirm Password should be same.");
                                                    }
                                                }
                                            } else {
                                                if (getApplicationContext() != null) {
                                                    connectivityMessage("Password Must be 8 Character long!");
                                                }
                                            }
                                        } else {

                                            connectivityMessage("Waiting for network!");
                                        }

                                    }
                                }
        );
    }

/* ****************Calling Change Password  Post Api ******************************/


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

    public void callChangePasswordAPI() {

        Loader.showProgressDialog(this);
        String token = sharedDatabase.getToken();
        JSONObject jsonObject = new JSONObject();
        StringEntity entity = null;
        try {
            jsonObject.put("old_password", old_password1.getText().toString());
            jsonObject.put("new_password", new_password1.getText().toString());
            entity = new StringEntity(jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        String authProvider = SettingsManager.getInstance().getAuthProvider();
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.addHeader("USER-AGENT", AppController.useragent);
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("Content-Type", "application/json");
        client.setTimeout(800000);
        String url = Constants.BASEURL + Constants.CHANGE_PASSWORD;
        client.post(ChangePassword.this, url, entity, "application/json", new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        Loader.dialogDissmiss(getApplicationContext());
                        MainActivity.isInternetConnection = false;
                        if (getApplicationContext() != null) {
                            connectivityMessage("Password Changed Successfully.");
                        }
                        finish();
                        AppController.getInstance().getMixpanelAPI().track("ChangePassword");
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        System.out.println("hheehgjh fail" + res);
                        String res12 = null;


                        res = res.replace("[", "");
                        res = res.replace("]", "");
                        res = res.replace("\"", "");
                        if (statusCode == 400) {
                            if (getApplicationContext() != null) {
                                connectivityMessage("" + res);
                            }
                        }

                        if (statusCode == 401) {
                            UserManager.getInstance().logOut(ChangePassword.this);
                        }

                        Loader.dialogDissmiss(getApplicationContext());
                        if (statusCode == 500) {
                            if (getApplicationContext() != null) {
                                connectivityMessage("We've encountered a technical error.our team is working on it. please try again later");
                            }
                        }
                    }
                }
        );
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
            callChangePasswordAPI();
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


}