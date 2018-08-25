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

public class SurveyPollingComment implements Parcelable {

    public SurveyPollingCreator creator;
    public String commentText;
    public int surveyId;
    public Date createdDate;
    public Date updatedDate;

    private static SimpleDateFormat mDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

    public SurveyPollingComment() {
    }

    public SurveyPollingComment(JSONObject jsonObject) {
        try {
            if (jsonObject.has("creator"))
                creator = new SurveyPollingCreator(jsonObject.getJSONObject("creator"));
            if (jsonObject.has("comment"))
                commentText = jsonObject.getString("comment");
            if (jsonObject.has("survey"))
                surveyId = jsonObject.getInt("survey");
            if (jsonObject.has("created"))
                createdDate = mDateTimeFormat.parse(jsonObject.getString("created"));
            if (jsonObject.has("updated"))
                updatedDate = mDateTimeFormat.parse(jsonObject.getString("updated"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SurveyPollingComment(Parcel in) {
        creator = in.readParcelable(SurveyPollingCreator.class.getClassLoader());
        commentText = in.readString();
        surveyId = in.readInt();
        createdDate = new Date(in.readLong());
        updatedDate = new Date(in.readLong());
    }

    public static final Creator<SurveyPollingComment> CREATOR = new Creator<SurveyPollingComment>() {
        @Override
        public SurveyPollingComment createFromParcel(Parcel in) {
            return new SurveyPollingComment(in);
        }

        @Override
        public SurveyPollingComment[] newArray(int size) {
            return new SurveyPollingComment[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(creator, i);
        parcel.writeString(commentText);
        parcel.writeInt(surveyId);
        parcel.writeLong(createdDate.getTime());
        parcel.writeLong(updatedDate.getTime());
    }
}
