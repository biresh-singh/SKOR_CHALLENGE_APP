package bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Dihardja Software Solutions on 3/15/18.
 */

public class SurveyPollingTopic implements Parcelable {

    public int id;
    public int organizationId;
    public String name;
    public Date createdDate;

    private static SimpleDateFormat mDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

    public SurveyPollingTopic() {
    }

    public SurveyPollingTopic(JSONObject jsonObject) {
        try {
            if (jsonObject.has("id"))
                id = jsonObject.getInt("id");
            if (jsonObject.has("organization"))
                organizationId = jsonObject.getInt("organization");
            if (jsonObject.has("name"))
                name = jsonObject.getString("name");
            if (jsonObject.has("created_at"))
                createdDate = mDateTimeFormat.parse(jsonObject.getString("created_at"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SurveyPollingTopic(Parcel in) {
        id = in.readInt();
        organizationId = in.readInt();
        name = in.readString();
        createdDate = new Date(in.readLong());
    }

    public static final Creator<SurveyPollingTopic> CREATOR = new Creator<SurveyPollingTopic>() {
        @Override
        public SurveyPollingTopic createFromParcel(Parcel in) {
            return new SurveyPollingTopic(in);
        }

        @Override
        public SurveyPollingTopic[] newArray(int size) {
            return new SurveyPollingTopic[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(organizationId);
        parcel.writeString(name);
        parcel.writeLong(createdDate.getTime());
    }
}
