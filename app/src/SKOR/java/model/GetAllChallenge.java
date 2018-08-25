package model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by biresh.singh on 05-08-2018.
 */

public class GetAllChallenge implements Parcelable {




    @SerializedName("individual_challenge")
    @Expose
    private List<Challenge> lstIndividualchallenge;

    @SerializedName("team_challenge")
    @Expose
    private List<Challenge> lstTeamchallenge;


    public GetAllChallenge() {
    }




    public List<Challenge> getlstIndividualchallenge() {
        return lstIndividualchallenge;
    }

    public void setlstIndividualchallenge(List<Challenge> lstIndividualchallenge) {
        this.lstIndividualchallenge = lstIndividualchallenge;
    }

    public List<Challenge> getlstTeamchallenge() {
        return lstTeamchallenge;
    }

    public void setlstTeamchallenge(List<Challenge> lstTeamchallenge) {
        this.lstTeamchallenge = lstTeamchallenge;
    }



    @Override
    public void writeToParcel(Parcel dest, int flags) {


        dest.writeList(lstIndividualchallenge);
        dest.writeList(lstTeamchallenge);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GetAllChallenge> CREATOR
            = new Creator<GetAllChallenge>() {
        public GetAllChallenge createFromParcel(Parcel in) {
            return new GetAllChallenge(in);
        }

        public GetAllChallenge[] newArray(int size) {
            return new GetAllChallenge[size];
        }
    };

    public GetAllChallenge(Parcel in) {


        lstIndividualchallenge = in.readArrayList(Challenge.class.getClassLoader());
        lstTeamchallenge = in.readArrayList(Challenge.class.getClassLoader());
    }

}