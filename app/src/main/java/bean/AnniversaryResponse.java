package bean;

import org.json.JSONArray;
import org.json.JSONException;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by dss-17 on 8/23/17.
 */

public class AnniversaryResponse extends RealmObject{
    @PrimaryKey
    private String id;
    private RealmList<Anniversary> anniversaryRealmList = new RealmList<>();

    public AnniversaryResponse() {
    }

    public AnniversaryResponse(JSONArray jsonArray) {
        setId("0");

        for (int i=0; i<jsonArray.length(); i++) {
            try {
                Anniversary anniversary = new Anniversary(jsonArray.getJSONObject(i));
                anniversaryRealmList.add(anniversary);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public RealmList<Anniversary> getAnniversaryRealmList() {
        return anniversaryRealmList;
    }

    public void setAnniversaryRealmList(RealmList<Anniversary> anniversaryRealmList) {
        this.anniversaryRealmList = anniversaryRealmList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
