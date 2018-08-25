package bean;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import singleton.InterfaceManager;

/**
 * Created by jessica on 1/18/18.
 */

public class QuickbloxUser extends RealmObject {
    @PrimaryKey
    private Integer id;
    private String fullName;
    private String email;
    private Date lastRequestAt;
    private String customData;

    public QuickbloxUser() {
    }

    public QuickbloxUser(JSONObject userJSONObject) {
        try {
            if (userJSONObject.has("id")) {
                setId(userJSONObject.getInt("id"));
            }
            if(userJSONObject.has("fullName")){
                setFullName(userJSONObject.getString("fullName"));
            }
            if(userJSONObject.has("email")){
                setEmail(userJSONObject.getString("email"));
            }
            if(userJSONObject.has("lastRequestAt")){
                setLastRequestAt(new Date(InterfaceManager.sharedInstance().utcToDateInMillis(userJSONObject.getString("lastRequestAt"))));
            }
            if(userJSONObject.has("custom_data")){
                setCustomData(userJSONObject.getString("custom_data"));
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public QuickbloxUser(Integer id, String fullName, String email, Date lastRequestAt, String customData) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.lastRequestAt = lastRequestAt;
        this.customData = customData;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getLastRequestAt() {
        return lastRequestAt;
    }

    public void setLastRequestAt(Date lastRequestAt) {
        this.lastRequestAt = lastRequestAt;
    }

    public String getCustomData() {
        return customData;
    }

    public void setCustomData(String customData) {
        this.customData = customData;
    }
}
