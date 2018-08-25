package bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.annotations.PrimaryKey;

/**
 * Created by Ferry on 2/27/18.
 */

public class Department implements Parcelable {
    @PrimaryKey
    public int id;
    public String name;
    public String slug;

    public Department(String name) {
        this.name = name;
        slug = name;
    }

    public Department(Parcel in) {
        id = in.readInt();
        name = in.readString();
        slug = in.readString();
    }

    public Department(JSONObject jsonObject) {
        try {
            if (jsonObject.has("pk")) {
                id = jsonObject.getInt("pk");
            }
            if (jsonObject.has("name")) {
                name = jsonObject.getString("name");
            }
            if (jsonObject.has("slug")) {
                slug = jsonObject.getString("slug");
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static final Creator<Department> CREATOR = new Creator<Department>() {
        public Department createFromParcel(Parcel in) {
            return new Department(in);
        }

        public Department[] newArray(int size) {
            return new Department[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeInt(id);
        out.writeString(name);
        out.writeString(slug);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        Department other = (Department) obj;
        if (id != other.id) {
            return false;
        }
        if (name == null) {
            if (other.name != null)
                return false;
        }
        else if (!name.equals(other.name))
            return false;
        if (slug == null) {
            if (other.slug != null)
                return false;
        }
        else if (!slug.equals(other.slug))
            return false;

        return true;
    }
}
