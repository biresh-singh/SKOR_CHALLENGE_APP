package model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by biresh.singh on 04-08-2018.
 */

public class Rewards implements Parcelable {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("name")
    @Expose
    private String name;


    public int getid() {
        return id;
    }
    public void setid(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(id);
        dest.writeString(name);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Rewards> CREATOR
            = new Creator<Rewards>() {
        public Rewards createFromParcel(Parcel in) {
            return new Rewards(in);
        }

        public Rewards[] newArray(int size) {
            return new Rewards[size];
        }
    };

    public Rewards(Parcel in) {
        id = in.readInt();
        name = in.readString();


    }

}
