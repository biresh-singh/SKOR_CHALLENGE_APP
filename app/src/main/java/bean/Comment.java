package bean;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by dss-17 on 9/19/17.
 */

public class Comment extends RealmObject{
    @PrimaryKey
    String id;
    String text;
    String created;
    Creator creator;

    public Comment() {
    }

    public Comment(JSONObject jsonObject) {
        try {
            if (jsonObject.has("id")) {
                setId(jsonObject.getString("id"));
            }
            if (jsonObject.has("text")) {
                setText(jsonObject.getString("text"));
            }
            if (jsonObject.has("created")) {
                setCreated(jsonObject.getString("created"));
            }
            if (jsonObject.has("creator")) {
                setCreator(new Creator(jsonObject.getJSONObject("creator")));
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public Creator getCreator() {
        return creator;
    }

    public void setCreator(Creator creator) {
        this.creator = creator;
    }
}
