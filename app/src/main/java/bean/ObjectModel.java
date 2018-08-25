package bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by dss-17 on 9/5/17.
 */

public class ObjectModel extends RealmObject implements Parcelable {
    @PrimaryKey
    String id;
    String type;
    String description;
    String name;
    Image image;
    //for survey polling
    boolean haveVoted;
    int totalVoted;
    int totalLike;
    String state;
    int points;
    int totalView;
    int totalComments;
    RealmList<Topic> topics;

    public ObjectModel() {

    }

    public ObjectModel(JSONObject jsonObject) {
        try {
            if (jsonObject.has("id")) {
                setId(jsonObject.getString("id"));
            }
            if (jsonObject.has("type")) {
                setType(jsonObject.getString("type"));
            }
            if (jsonObject.has("name")) {
                setName(jsonObject.getString("name"));
            }
            if (jsonObject.has("description")) {
                setDescription(jsonObject.getString("description"));
            }
            if (jsonObject.has("image")) {
                setImage(new Image(jsonObject.getJSONObject("image")));
            }
            //for survey polling
            if (jsonObject.has("have_voted")) {
                setHaveVoted(jsonObject.getBoolean("have_voted"));
            }
            if (jsonObject.has("total_voted")) {
                setTotalVoted(jsonObject.getInt("total_voted"));
            }
            if (jsonObject.has("total_like")) {
                setTotalLike(jsonObject.getInt("total_like"));
            }
            if (jsonObject.has("state")) {
                setState(jsonObject.getString("state"));
            }
            if (jsonObject.has("points")) {
                setPoints(jsonObject.getInt("points"));
            }
            if (jsonObject.has("total_view")) {
                setTotalView(jsonObject.getInt("total_view"));
            }
            if (jsonObject.has("total_comments")) {
                setTotalComments(jsonObject.getInt("total_comments"));
            }
            if (jsonObject.has("topics")) {
                RealmList<Topic> topicRealmList = new RealmList<>();
                JSONArray jsonArray = jsonObject.getJSONArray("topics");
                for (int i = 0; i < jsonArray.length(); i++) {
                    topicRealmList.add(new Topic(jsonArray.getString(i)));
                }
                setTopics(topicRealmList);
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    //for survey polling
    public boolean isHaveVoted() {
        return haveVoted;
    }

    public void setHaveVoted(boolean haveVoted) {
        this.haveVoted = haveVoted;
    }

    public int getTotalVoted() {
        return totalVoted;
    }

    public void setTotalVoted(int totalVoted) {
        this.totalVoted = totalVoted;
    }

    public int getTotalLike() {
        return totalLike;
    }

    public void setTotalLike(int totalLike) {
        this.totalLike = totalLike;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getTotalView() {
        return totalView;
    }

    public void setTotalView(int totalView) {
        this.totalView = totalView;
    }

    public int getTotalComments() {
        return totalComments;
    }

    public void setTotalComments(int totalComments) {
        this.totalComments = totalComments;
    }

    public RealmList<Topic> getTopics() {
        return topics;
    }

    public void setTopics(RealmList<Topic> topics) {
        this.topics = topics;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.type);
        dest.writeString(this.description);
        dest.writeString(this.name);
        dest.writeParcelable(this.image, flags);
    }

    protected ObjectModel(Parcel in) {
        this.id = in.readString();
        this.type = in.readString();
        this.description = in.readString();
        this.name = in.readString();
        this.image = in.readParcelable(Image.class.getClassLoader());
    }

    public static final Parcelable.Creator<ObjectModel> CREATOR = new Parcelable.Creator<ObjectModel>() {
        @Override
        public ObjectModel createFromParcel(Parcel source) {
            return new ObjectModel(source);
        }

        @Override
        public ObjectModel[] newArray(int size) {
            return new ObjectModel[size];
        }
    };
}
