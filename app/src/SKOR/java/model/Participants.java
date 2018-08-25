package model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by biresh.singh on 04-08-2018.
 */

public class Participants implements Parcelable {

    @SerializedName("pk")
    @Expose
    private int pk;

    @SerializedName("first_name")
    @Expose
    private String first_name;

    @SerializedName("last_name")
    @Expose
    private String last_name;

    @SerializedName("department_name")
    @Expose
    private String department_name;

    @SerializedName("profile_pic_url")
    @Expose
    private String profile_pic_url;

    public int getid() {
        return pk;
    }
    public void setid(int pk) {
        this.pk = pk;
    }

    public String getFirstName() {
        return first_name;
    }

    public void setFirstName(String first_name) {
        this.first_name = first_name;
    }

    public String getLastName() {
        return last_name;
    }

    public void setLastName(String last_name) {
        this.last_name = last_name;
    }

    public String getDepartmentName() {
        return department_name;
    }

    public void setDepartmentName(String department_name) {
        this.department_name = department_name;
    }

    public String getPartcipantImage() {
        return profile_pic_url;
    }

    public void setPartcipantImage(String profile_pic_url) {
        this.profile_pic_url = profile_pic_url;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(pk);
        dest.writeString(first_name);
        dest.writeString(last_name);
        dest.writeString(department_name);
        dest.writeString(profile_pic_url);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Participants> CREATOR
            = new Creator<Participants>() {
        public Participants createFromParcel(Parcel in) {
            return new Participants(in);
        }

        public Participants[] newArray(int size) {
            return new Participants[size];
        }
    };

    public Participants(Parcel in) {
        pk = in.readInt();
        first_name = in.readString();
        last_name = in.readString();
        department_name = in.readString();
        profile_pic_url = in.readString();
    }

}
