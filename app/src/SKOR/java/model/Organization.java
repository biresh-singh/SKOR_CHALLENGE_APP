package model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by biresh.singh on 04-08-2018.
 */

public class Organization implements Parcelable {

    @SerializedName("pk")
    @Expose
    private int pk;

    @SerializedName("name")
    @Expose
    private String name;


    @SerializedName("slug")
    @Expose
    private String slug;


    public Organization()
    {}

    public int getPk() {
        return pk;
    }
    public void setPk(int pk) {
        this.pk = pk;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getslug() {
        return slug;
    }

    public void setslug(String slug) {
        this.slug = slug;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(pk);
        dest.writeString(name);
        dest.writeString(slug);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Organization> CREATOR
            = new Creator<Organization>() {
        public Organization createFromParcel(Parcel in) {
            return new Organization(in);
        }

        public Organization[] newArray(int size) {
            return new Organization[size];
        }
    };

    public Organization(Parcel in) {
        pk = in.readInt();
        name = in.readString();
        slug = in.readString();

    }
}

