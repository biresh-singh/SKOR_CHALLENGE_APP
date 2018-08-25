package singleton;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;

import org.jivesoftware.smack.SmackException;

import activity.userprofile.LoginActivity;
import activity.userprofile.MyProfile;
import database.SharedDatabase;
import utils.AppController;

/**
 * Created by Dihardja Software Solutions on 11/14/17.
 */

public class UserManager {

    SharedDatabase sharedDatabase;
    private static UserManager SERVERMANAGER = null;
    public static final String TAG = ServerManager.class.getSimpleName();
    QBChatService qbChatService;


    public static UserManager getInstance() {
        if (SERVERMANAGER == null) {
            synchronized (UserManager.class) {
                if (SERVERMANAGER == null) {
                    SERVERMANAGER = new UserManager();
                }
            }
        }
        return SERVERMANAGER;
    }

    public UserManager() {
        sharedDatabase = new SharedDatabase(AppController.getAppContext());
    }

    public void logOut(final Activity mActivity) {


        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mActivity);
        builder.setTitle("Warning");
        builder.setMessage("Sorry, your login session has expired. Please re-login by using your updated PassAXA")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        sharedDatabase.setStepactivitylimit("0");
                        sharedDatabase.setEnableactivitytracker("0");
                        sharedDatabase.setPosition(0);
                        sharedDatabase.userToken("");
                        sharedDatabase.setAuthorizedToQuickblox(false);
                        final Intent intent = new Intent(mActivity, LoginActivity.class);
                        AppController.getInstance().getMixpanelAPI().track("Logout");

                        try {
                            qbChatService.getInstance().logout();
                        } catch (SmackException.NotConnectedException e) {
                            e.printStackTrace();
                        }

                        QBUsers.signOut().performAsync(new QBEntityCallback<Void>() {
                            @Override
                            public void onSuccess(Void aVoid, Bundle bundle) {
                                mActivity.startActivity(intent);
                                mActivity.finish();
                            }

                            @Override
                            public void onError(QBResponseException e) {
                                mActivity.startActivity(intent);
                                mActivity.finish();
                            }
                        });
                    }
                });
        final android.app.AlertDialog alert = builder.create();
        alert.show();
    }
}
