package model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by biresh.singh on 04-08-2018.
 */

public class Images implements Parcelable {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("image")
    @Expose
    private String image;


    public int getid() {
        return id;
    }
    public void setid(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(id);
        dest.writeString(image);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Images> CREATOR
            = new Creator<Images>() {
        public Images createFromParcel(Parcel in) {
            return new Images(in);
        }

        public Images[] newArray(int size) {
            return new Images[size];
        }
    };

    public Images(Parcel in) {
        id = in.readInt();
        image = in.readString();


    }

}
