package singleton;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import constants.Constants;
import cz.msebera.android.httpclient.Header;
import database.SharedDatabase;
import event.RefreshTokenEvent;
import utils.AndroidDeviceNames;
import utils.AppController;


/**
 * Created by dss-17 on 9/19/17.
 */

public class ServerManager {

    Context mContext;
    AndroidDeviceNames deviceNames;
    SharedDatabase sharedDatabase;
    private static ServerManager SERVERMANAGER = null;
    String versionName = "";
    public static final String TAG = ServerManager.class.getSimpleName();


    public static ServerManager getInstance() {
        if (SERVERMANAGER == null) {
            synchronized (ServerManager.class) {
                if (SERVERMANAGER == null) {
                    SERVERMANAGER = new ServerManager();
                }
            }
        }
        return SERVERMANAGER;
    }

    public ServerManager() {
        mContext = AppController.getAppContext();
        sharedDatabase = new SharedDatabase(AppController.getAppContext());
        deviceNames = new AndroidDeviceNames(AppController.getAppContext());
    }

//    public void refreshToken() {
//        String authProvider = SettingsManager.getInstance().getAuthProvider();
//        String useragent = "Skor/3 Android|" + deviceNames.getDeviceName() + "|" + deviceNames.getAPIVerison() + "|" + getVersionCode();
//        String token = sharedDatabase.getToken();
//
//        HashMap<String, String> paramMap = new HashMap<String, String>();
//        paramMap.put("provider", authProvider);
//
//        RequestParams params = new RequestParams(paramMap);
//        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
//        client.setTimeout(800000);
////        client.addHeader("connection", "Keep-Alive");
////        client.addHeader("Content-Type", "application/x-www-form-urlencoded");
//        client.addHeader("Authorization", authProvider + " " + token);
//        client.addHeader("USER-AGENT", useragent);
//        client.post(Constants.BASEURL + "profiles/api/oauth/refresh/", params, new TextHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                try {
//                    JSONObject jsonObject = new JSONObject(responseString);
//                    Log.e(TAG, "onSuccess: " + jsonObject.toString());
//                    sharedDatabase.userToken(jsonObject.getString("token"));
//                    EventBus.getDefault().post(new RefreshTokenEvent(null));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                Log.e(TAG, "onFailure: " +statusCode);
//                Log.e(TAG, "onFailure: " +responseString);
////                EventBus.getDefault().post(new RefreshTokenEvent("Log Out"));
//            }
//        });
//    }

    public String getVersionCode() {
        String versionCode = null;
        try {
            PackageManager manager = mContext.getPackageManager();
            PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
            versionCode = String.valueOf(info.versionCode);
            versionName = String.valueOf(info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

}
