package model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by biresh.singh on 13-08-2018.
 */

public class UserActivity implements Parcelable {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("user")
    @Expose
    private int user;

    @SerializedName("challenge")
    @Expose
    private int challenge;


    @SerializedName("user_activity")
    @Expose
    private PostUserActivity user_activity;

    @SerializedName("status")
    @Expose
    private int status;





    public int getID() {
        return id;
    }
    public void setID(int id) {
        this.id = id;
    }

    public int getUser() {
        return user;
    }
    public void setUser(int user) {
        this.user = user;
    }


    public int getChallenge() {
        return challenge;
    }
    public void setChallenge(int challenge) {
        this.challenge = challenge;
    }

    public PostUserActivity getUserActivity() {
        return user_activity;
    }
    public void setUserActivity(PostUserActivity user_activity) {
        this.user_activity = user_activity;
    }

    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }



    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(id);
        dest.writeInt(user);
        dest.writeInt(challenge);
        dest.writeParcelable(user_activity,flags);
        dest.writeInt(status);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserActivity> CREATOR
            = new Creator<UserActivity>() {
        public UserActivity createFromParcel(Parcel in) {
            return new UserActivity(in);
        }

        public UserActivity[] newArray(int size) {
            return new UserActivity[size];
        }
    };

    public UserActivity(Parcel in) {
        id = in.readInt();
        user = in.readInt();
        challenge = in.readInt();
        user_activity = in.readParcelable(PostUserActivity.class.getClassLoader());
        status = in.readInt();
    }
}
