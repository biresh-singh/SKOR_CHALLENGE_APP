package singleton;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.quickblox.chat.model.QBChatDialog;

import utils.AppController;

/**
 * Created by mac on 10/26/17.
 */

public class SettingsManager {
    private static Context context;
    private static SharedPreferences PREF = null;
    private static SharedPreferences.Editor PREF_EDITOR = null;
    private static SettingsManager SETTINGSMANAGER = null;
    private QBChatDialog qbChatDialog = new QBChatDialog();

    public static SettingsManager getInstance() {
        if (SETTINGSMANAGER == null) {
            PREF = AppController.getAppContext().getSharedPreferences("com.root.skor", Context.MODE_PRIVATE);
            PREF_EDITOR = PREF.edit();
            SETTINGSMANAGER = new SettingsManager();

        }
        return SETTINGSMANAGER;
    }

    public SettingsManager() {
        context = AppController.getAppContext();
    }

    public int getInt(String key) {
        return PREF.getInt(key, 0);
    }

    public void setInt(String key, int value) {
        PREF_EDITOR.putInt(key, value);
        PREF_EDITOR.commit();
    }

    public boolean getBool(String key, boolean defaultValue) {
        return PREF.getBoolean(key, defaultValue);
    }

    public void setBool(String key, boolean value) {
        PREF_EDITOR.putBoolean(key, value);
        PREF_EDITOR.commit();
    }

    public void setStr(String key, String value) {
        PREF_EDITOR.putString(key, value);
        PREF_EDITOR.commit();
    }

    public String getStr(String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return PREF.getString(key, "");
    }


    public void setCurrentAttachmentSize(int attachmentSize) {
        setInt("currentAttachmentSize", attachmentSize);
    }

    public int getCurrentAttachmentSize() {
        return getInt("currentAttachmentSize");
    }

    public void setQbChatDialog(QBChatDialog qbChatDialog) {
        this.qbChatDialog = qbChatDialog;
    }

    public QBChatDialog getQbChatDialog() {
        return this.qbChatDialog;
    }

    public void setIsCanCreateFeed(String value) {
        setStr("IsCanCreateFeed", value);
    }

    public String IsCanCreateFeed() {
        return getStr("IsCanCreateFeed");
    }

    public void setAuthProvider(String value) {
        setStr("auth_provider", value);
    }
    public String getAuthProvider() {
        return getStr("auth_provider");
    }
    public void setDepartmentJSON(String departmentJSON){
        setStr("departmentJSON",departmentJSON);
    }
    public String getDepartmentJSON(){
        return getStr("departmentJSON");
    }
}