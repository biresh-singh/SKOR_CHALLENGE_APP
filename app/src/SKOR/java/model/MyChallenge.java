package model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import bean.Image;

/**
 * Created by biresh.singh on 04-08-2018.
 */

public class MyChallenge implements Parcelable {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("user")
    @Expose
    private int user;

    @SerializedName("challenge")
    @Expose
    private Challenge challenge;

    @SerializedName("user_activity")
    @Expose
    private PostUserActivity user_activity;



    public MyChallenge()
    {

    }


    public int getid() {
        return id;
    }
    public void setid(int id) {
        this.id = id;
    }

    public int getUserid() {
        return user;
    }
    public void setUserid(int user) {
        this.user = user;
    }

    public Challenge getChallenge() {
        return challenge;
    }
    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    public PostUserActivity getUserActivity() {
        return user_activity;
    }
    public void setUserActivity(PostUserActivity user_activity) {
        this.user_activity = user_activity;
    }



    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(id);
        dest.writeInt(user);
        dest.writeParcelable(challenge,flags);
        dest.writeParcelable(user_activity,flags);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MyChallenge> CREATOR
            = new Creator<MyChallenge>() {
        public MyChallenge createFromParcel(Parcel in) {
            MyChallenge chg=new MyChallenge();

            return new MyChallenge(in);
        }

        public MyChallenge[] newArray(int size) {
            return new MyChallenge[size];
        }
    };

    public MyChallenge(Parcel in) {
        id = in.readInt();
        user = in.readInt();
        challenge = in.readParcelable(Challenge.class.getClassLoader());
        user_activity = in.readParcelable(PostUserActivity.class.getClassLoader());

    }



}
