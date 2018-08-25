package model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by biresh.singh on 04-08-2018.
 */

public class Tags implements Parcelable {

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

    public static final Creator<Tags> CREATOR
            = new Creator<Tags>() {
        public Tags createFromParcel(Parcel in) {
            return new Tags(in);
        }

        public Tags[] newArray(int size) {
            return new Tags[size];
        }
    };

    public Tags(Parcel in) {
        id = in.readInt();
        name = in.readString();


    }

}
