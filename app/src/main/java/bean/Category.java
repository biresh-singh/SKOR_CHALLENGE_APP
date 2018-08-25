package bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by dss-17 on 9/18/17.
 */

public class Category extends RealmObject implements Parcelable {
    @PrimaryKey
    String id;
    String categoryName;

    public Category() {
    }

    public Category(JSONObject jsonObject) {

        try {
            if (jsonObject.has("id")) {
                setId(jsonObject.getString("id"));
            }
            if (jsonObject.has("name")) {
                setCategoryName(jsonObject.getString("name"));
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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.categoryName);
    }

    protected Category(Parcel in) {
        this.id = in.readString();
        this.categoryName = in.readString();
    }

    public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel source) {
            return new Category(source);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };
}
