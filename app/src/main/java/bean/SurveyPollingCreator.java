package bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by Dihardja Software Solutions on 3/19/18.
 */

public class SurveyPollingCreator implements Parcelable {

    public String image;
    public String name;
    public int id;

    public SurveyPollingCreator() {
    }

    public SurveyPollingCreator(JSONObject jsonObject) {
        try {
            if (jsonObject.has("image"))
                image = jsonObject.getString("image");
            if (jsonObject.has("name"))
                name = jsonObject.getString("name");
            if (jsonObject.has("id"))
                id = jsonObject.getInt("id");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected SurveyPollingCreator(Parcel in) {
        image = in.readString();
        name = in.readString();
        id = in.readInt();
    }

    public static final Creator<SurveyPollingCreator> CREATOR = new Creator<SurveyPollingCreator>() {
        @Override
        public SurveyPollingCreator createFromParcel(Parcel in) {
            return new SurveyPollingCreator(in);
        }

        @Override
        public SurveyPollingCreator[] newArray(int size) {
            return new SurveyPollingCreator[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(image);
        parcel.writeString(name);
        parcel.writeInt(id);
    }
}
