package bean;

import org.json.JSONArray;
import org.json.JSONException;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by dss-17 on 8/23/17.
 */

public class BirthdayResponse extends RealmObject{
    @PrimaryKey
    private String id;
    private RealmList<Birthday> birthdayRealmList = new RealmList<>();

    public BirthdayResponse() {
    }

    public BirthdayResponse(JSONArray jsonArray) {
        setId("0");

        for (int i=0; i<jsonArray.length(); i++) {
            try {
                Birthday birthday = new Birthday(jsonArray.getJSONObject(i));
                birthdayRealmList.add(birthday);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public RealmList<Birthday> getBirthdayRealmList() {
        return birthdayRealmList;
    }

    public void setBirthdayRealmList(RealmList<Birthday> birthdayRealmList) {
        this.birthdayRealmList = birthdayRealmList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
