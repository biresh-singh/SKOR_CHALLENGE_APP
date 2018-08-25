package bean;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by dss-17 on 10/11/17.
 */

public class Image3 extends RealmObject{
    @PrimaryKey
    String id;
    String thumbnail;
    String display;

    public Image3(){
    }

    public Image3(JSONObject jsonObject) {
        try {
            if (jsonObject.has("thumbnail")) {
                setThumbnail(jsonObject.getString("thumbnail"));
            }
            if (jsonObject.has("display")) {
                setDisplay(jsonObject.getString("display"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }
}
