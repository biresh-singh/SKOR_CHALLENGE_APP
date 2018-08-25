package bean;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.RealmObject;

/**
 * Created by dss-17 on 8/31/17.
 */

public class PointsCategory extends RealmObject {
    private String name;
    private String slug;
    private String icon;
    private String color;
    private Double points;
    private String value;

    public PointsCategory() {

    }

    public PointsCategory(JSONObject jsonObject) {

        try {
            if (jsonObject.has("name")) {
                setName(jsonObject.getString("name"));
            }
            if (jsonObject.has("slug")) {
                setSlug(jsonObject.getString("slug"));
            }
            if (jsonObject.has("icon")) {
                setIcon(jsonObject.getString("icon"));
            }
            if (jsonObject.has("color")) {
                setColor(jsonObject.getString("color"));
            }
            if (jsonObject.has("points")) {
                setPoints(jsonObject.getDouble("points"));
            }
            if (jsonObject.has("value")) {
                setValue(jsonObject.getString("value"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Double getPoints() {
        return points;
    }

    public void setPoints(Double points) {
        this.points = points;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
