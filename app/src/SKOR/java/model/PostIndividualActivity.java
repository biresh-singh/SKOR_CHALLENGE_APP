package model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by biresh.singh on 13-08-2018.
 */

public class PostIndividualActivity implements Parcelable {

    @SerializedName("challenge")
    @Expose
    private int challengeID;

    @SerializedName("status")
    @Expose
    private int status;

    @SerializedName("user_activity")
    @Expose
    private PostUserActivity user_activity;

    @SerializedName("user")
    @Expose
    private int userID;

    public int getchallengeID() {
        return challengeID;
    }
    public void setchallengeID(int challengeID) {
        this.challengeID = challengeID;
    }

    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }


    public PostUserActivity getUserActivity() {
        return user_activity;
    }
    public void setUserActivity(PostUserActivity user_activity) {
        this.user_activity = user_activity;
    }

    public int getUser() {
        return userID;
    }
    public void setUser(int userID) {
        this.userID = userID;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(challengeID);
        dest.writeInt(status);
        dest.writeInt(userID);
        dest.writeParcelable(user_activity,flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PostIndividualActivity> CREATOR
            = new Creator<PostIndividualActivity>() {
        public PostIndividualActivity createFromParcel(Parcel in) {
            return new PostIndividualActivity(in);
        }

        public PostIndividualActivity[] newArray(int size) {
            return new PostIndividualActivity[size];
        }
    };

    public PostIndividualActivity(Parcel in) {
        challengeID = in.readInt();
        status = in.readInt();
        userID = in.readInt();
        user_activity=in.readParcelable(PostUserActivity.class.getClassLoader());
    }
}
