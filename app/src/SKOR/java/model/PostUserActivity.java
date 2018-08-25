package model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by biresh.singh on 13-08-2018.
 */

public class PostUserActivity implements Parcelable {

    @SerializedName("day")
    @Expose
    private int day;

    @SerializedName("date")
    @Expose
    private String Date;

    @SerializedName("Data")
    @Expose
    private String Data;

    @SerializedName("time")
    @Expose
    private String Time;

    public PostUserActivity() {
    }

    public int getDay() {
        return day;
    }
    public void setDay(int day) {
        this.day = day;
    }

    public String getDate() {
        return Date;
    }
    public void setStatus(String Date) {
        this.Date = Date;
    }

    public String getData() {
        return Data;
    }
    public void setData(String Data) {
        this.Data = Data;
    }

    public String getTime() {
        return Time;
    }
    public void setTime(String Time) {
        this.Time = Time;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(day);
        dest.writeString(Date);
        dest.writeString(Data);
        dest.writeString(Time);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PostUserActivity> CREATOR
            = new Creator<PostUserActivity>() {
        public PostUserActivity createFromParcel(Parcel in) {
            return new PostUserActivity(in);
        }

        public PostUserActivity[] newArray(int size) {
            return new PostUserActivity[size];
        }
    };

    public PostUserActivity(Parcel in) {
        day = in.readInt();
        Date = in.readString();
        Data = in.readString();
        Time= in.readString();
    }
}
