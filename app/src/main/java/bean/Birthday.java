package bean;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by dss-17 on 8/23/17.
 */

public class Birthday extends RealmObject{
    @PrimaryKey
    private int id;
    private int day;
    private int month;
    private String date;
    private String type;
    private String thumbnail;
    private String userFirstName;
    private String userLastName;
    private String userEmail;

    public Birthday() {
    }

    public Birthday(JSONObject jsonObject) {
        try {
            if (jsonObject.has("id")) {
                setId(jsonObject.getInt("id"));
            }
            if (jsonObject.has("day")) {
                setDay(jsonObject.getInt("day"));
            }
            if (jsonObject.has("month")) {
                setMonth(jsonObject.getInt("month"));
            }
            if (jsonObject.has("date")) {
                setDate(jsonObject.getString("date"));
            }
            if (jsonObject.has("type")) {
                setType(jsonObject.getString("type"));
            }
            if (jsonObject.has("thumbnail")) {
                setThumbnail(jsonObject.getString("thumbnail"));
            }
            if (jsonObject.has("user_first_name")) {
                setUserFirstName(jsonObject.getString("user_first_name"));
            }
            if (jsonObject.has("user_last_name")) {
                setUserLastName(jsonObject.getString("user_last_name"));
            }
            if (jsonObject.has("user_email")) {
                setUserEmail(jsonObject.getString("user_email"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
