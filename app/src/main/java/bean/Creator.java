package bean;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by dss-17 on 9/19/17.
 */

public class Creator extends RealmObject {
    @PrimaryKey
    String id;
    String image;
    String name;

    public Creator() {

    }

    public Creator(JSONObject jsonObject) {

        try {
            if (jsonObject.has("id")) {
                setId(jsonObject.getString("id"));
            }
            if (jsonObject.has("name")) {
                setName(jsonObject.getString("name"));
            }
            if (jsonObject.has("image")) {
                setImage(jsonObject.getString("image"));
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
