package bean;

import org.json.JSONArray;
import org.json.JSONException;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by dss-17 on 8/31/17.
 */

public class PointsCategoryResponse extends RealmObject {
    String id;
    RealmList<PointsCategory> pointsCategories = new RealmList<>();
    PointsCategory pointsCategory;

    public PointsCategoryResponse() {

    }

    public PointsCategoryResponse(JSONArray jsonArray) {
        setId("0");

        for (int i=0; i<jsonArray.length(); i++) {
            try {
                setPointsCategory(new PointsCategory(jsonArray.getJSONObject(i)));
                pointsCategories.add(getPointsCategory());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RealmList<PointsCategory> getPointsCategories() {
        return pointsCategories;
    }

    public void setPointsCategories(RealmList<PointsCategory> pointsCategories) {
        this.pointsCategories = pointsCategories;
    }

    public PointsCategory getPointsCategory() {
        return pointsCategory;
    }

    public void setPointsCategory(PointsCategory pointsCategory) {
        this.pointsCategory = pointsCategory;
    }
}
