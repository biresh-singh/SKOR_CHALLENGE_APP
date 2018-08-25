package bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by dss-17 on 9/18/17.
 */

public class SurveyPollingCategory implements Parcelable {
    public int id;
    public int organization;
    public String name;

    public SurveyPollingCategory(Parcel in) {
        id = in.readInt();
        organization = in.readInt();
        name = in.readString();
    }

    public SurveyPollingCategory(JSONObject jsonObject) {
        try {
            if (jsonObject.has("id")) {
                id = jsonObject.getInt("id");
            }
            if (jsonObject.has("organization")) {
                organization = jsonObject.getInt("organization");
            }
            if (jsonObject.has("name")) {
                name = jsonObject.getString("name");
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static final Creator<SurveyPollingCategory> CREATOR = new Creator<SurveyPollingCategory>() {
        public SurveyPollingCategory createFromParcel(Parcel in) {
            return new SurveyPollingCategory(in);
        }

        public SurveyPollingCategory[] newArray(int size) {
            return new SurveyPollingCategory[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeInt(id);
        out.writeInt(organization);
        out.writeString(name);
    }
}
