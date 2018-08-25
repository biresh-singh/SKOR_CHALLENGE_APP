package utils;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.quickblox.auth.session.BaseService;
import com.quickblox.auth.session.QBSettings;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.LogLevel;
import com.quickblox.core.ServiceZone;
import com.quickblox.core.StoringMechanism;
import com.quickblox.core.exception.BaseServiceException;
import com.root.skor.BuildConfig;

import java.io.File;
import java.util.Date;

import database.SharedDatabase;
import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class AppController extends MultiDexApplication implements Application.ActivityLifecycleCallbacks, ComponentCallbacks2{

//    public static final String TAG = AppController.class.getSimpleName();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    public static Context context;
    private static AppController mInstance;

    Realm realm;

    public boolean isAppRunning = true;

    public Application app;
    SharedDatabase sharedDatabase;

    //mixpanel
    String projectTokenForSKOR = "47e7342775d1ae413a289f75ba287f74";
    String projectTokenForAXA = "e9e5b237907c2befab13561cfe5b2a85";
    public MixpanelAPI mixpanelAPI;
    public Activity currentActivity;
    public Boolean isActive = true;
    public static String stateOfLifeCycle = "";
    private static String TAG = AppController.class.getName();
    public static boolean wasInBackground = false;

    public AndroidDeviceNames deviceNames;
    public String versionName = "";
    public static String useragent = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        sharedDatabase = new SharedDatabase(getApplicationContext());
        app = (Application) getApplicationContext();
        Realm.init(this);

        AppController.context = getApplicationContext();

        VMRuntime.getRuntime().setMinimumHeapSize(4096 * 1024 * 1024);

        Fabric.with(this, new Crashlytics());

        if(BuildConfig.FLAVOR.equals("SKOR")) {
            mixpanelAPI = MixpanelAPI.getInstance(this, projectTokenForSKOR);
        } else {
            mixpanelAPI = MixpanelAPI.getInstance(this, projectTokenForAXA);
        }

        //track mixpanel Distinct ID
        mixpanelAPI.identify(sharedDatabase.getEmail());

        //notify app is in background or foreground
        registerActivityLifecycleCallbacks(this);

        //quickblox
        QBSettings.getInstance().setLogLevel(LogLevel.DEBUG);
        QBChatService.setDebugEnabled(true);

        deviceNames = new AndroidDeviceNames(getAppContext());
        useragent = "Skor/3 Android|" + deviceNames.getDeviceName() + "|" + deviceNames.getAPIVerison() + "|" + getVersionCode();
    }

    public static Context getAppContext() {
        return AppController.context;
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public MixpanelAPI getMixpanelAPI() {
        return mixpanelAPI;
    }

    public void setMixpanelAPI(MixpanelAPI mixpanelAPI) {
        this.mixpanelAPI = mixpanelAPI;
    }

    public void initQuickblox(String id, String key, String secret) {
        QBSettings.getInstance().setStoringMehanism(StoringMechanism.SECURED); //call before init method for QBSettings

        QBSettings.getInstance().init(getApplicationContext(),
                id,
                key,
                secret);


//        QBSettings.getInstance().init(getApplicationContext(),
//                "19",
//                "c9MVGH3fgjQ2mOz",
//                "uO3Mzm3bTQV4aM6"   );


//        QBSettings.getInstance().setAccountKey("XvdrzDNEpFxvyKsoSFeN");
        QBSettings.getInstance().setAccountKey("qV9AYyBCbpPEqByyQ52k"); // production

        QBSettings.getInstance().setEndpoints("apibrainstorm.quickblox.com", "chatbrainstorm.quickblox.com", ServiceZone.PRODUCTION);
        QBSettings.getInstance().setZone(ServiceZone.PRODUCTION);

        QBSettings.getInstance().setLogLevel(LogLevel.DEBUG);
        QBChatService.setDebugEnabled(true);

        try {
            BaseService.getBaseService().setTokenExpirationDate(new Date());
            BaseService.createFromExistentToken(BaseService.getBaseService().getToken(),BaseService.getBaseService().getTokenExpirationDate());
        } catch (BaseServiceException e) {
            e.printStackTrace();
        }
    }

    public static Realm getRealm() {
        if (mInstance.realm != null) {
            return mInstance.realm;
        }
        try {
            mInstance.realm = Realm.getDefaultInstance();
        } catch (Exception e) {
            RealmConfiguration config = new RealmConfiguration.Builder()
                    .deleteRealmIfMigrationNeeded()
                    .build();
            mInstance.realm = Realm.getInstance(config);
        }
        return mInstance.realm;
    }

    public void clearRealmCache() {
        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        wasInBackground = false;
        stateOfLifeCycle = "Create";
    }

    @Override
    public void onActivityStarted(Activity activity) {
        stateOfLifeCycle = "Start";
    }

    @Override
    public void onActivityResumed(Activity activity) {
        stateOfLifeCycle = "Resume";
        currentActivity = activity;
        if(isActive){
            AppController.getInstance().getMixpanelAPI().timeEvent("SessionDuration");
            isActive = false;
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        stateOfLifeCycle = "Pause";
    }

    @Override
    public void onActivityStopped(Activity activity) {
        stateOfLifeCycle = "Stop";
        if(activity == currentActivity) {
            AppController.getInstance().getMixpanelAPI().track("SessionDuration");
            isActive = true;
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        wasInBackground = false;
        stateOfLifeCycle = "Destroy";
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        wasInBackground = false;
        stateOfLifeCycle = "Destroy";
    }

    @Override
    public void onTrimMemory(int level) {
        if (stateOfLifeCycle.equals("Stop")) {
            wasInBackground = true;
        }
        super.onTrimMemory(level);
    }

    public void clearApplicationData() {
        File cache = getCacheDir();
        File appDir = new File(cache.getParent());
        if(appDir.exists()){
            String[] children = appDir.list();
            for(String s : children){
                if(!s.equals("lib")){
                    deleteDir(new File(appDir, s));
                    Log.i("TAG", "File /data/data/APP_PACKAGE/" + s +" DELETED");
                }
            }
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        return dir.delete();
    }

    public String getVersionCode() {
        String versionCode = null;
        try {
            PackageManager manager = getPackageManager();
            PackageInfo info = manager.getPackageInfo(getApplicationContext().getPackageName(), 0);
            versionCode = String.valueOf(info.versionCode);
            versionName = String.valueOf(info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

}
