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

public class SurveyPollingLike implements Parcelable {

    public SurveyPollingCreator creator;
    public int surveyId;
    public Date createdDate;

    private static SimpleDateFormat mDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

    public SurveyPollingLike() {
    }

    public SurveyPollingLike(JSONObject jsonObject) {
        try {
            if (jsonObject.has("creator"))
                creator = new SurveyPollingCreator(jsonObject.getJSONObject("creator"));
            if (jsonObject.has("survey"))
                surveyId = jsonObject.getInt("survey");
            if (jsonObject.has("created"))
                createdDate = mDateTimeFormat.parse(jsonObject.getString("created"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SurveyPollingLike(Parcel in) {
        creator = in.readParcelable(SurveyPollingCreator.class.getClassLoader());
        surveyId = in.readInt();
        createdDate = new Date(in.readLong());
    }

    public static final Creator<SurveyPollingLike> CREATOR = new Creator<SurveyPollingLike>() {
        @Override
        public SurveyPollingLike createFromParcel(Parcel in) {
            return new SurveyPollingLike(in);
        }

        @Override
        public SurveyPollingLike[] newArray(int size) {
            return new SurveyPollingLike[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(creator, i);
        parcel.writeInt(surveyId);
        parcel.writeLong(createdDate.getTime());
    }
}
