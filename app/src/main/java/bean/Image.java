package bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by dss-17 on 8/25/17.
 */

public class Image extends RealmObject implements Parcelable {
    @PrimaryKey
    String id;
    String thumbnail;
    String display;

    public Image(){
    }

    public Image(JSONObject jsonObject) {
        try {
            if (jsonObject.has("thumbnail")) {
                setId(jsonObject.getString("thumbnail"));
                setThumbnail(jsonObject.getString("thumbnail"));
            }
            if (jsonObject.has("display")) {
                setDisplay(jsonObject.getString("display"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.thumbnail);
        dest.writeString(this.display);
    }

    protected Image(Parcel in) {
        this.id = in.readString();
        this.thumbnail = in.readString();
        this.display = in.readString();
    }

    public static final Parcelable.Creator<Image> CREATOR = new Parcelable.Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel source) {
            return new Image(source);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };
}