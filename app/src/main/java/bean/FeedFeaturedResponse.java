package bean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by dss-17 on 8/25/17.
 */

public class FeedFeaturedResponse extends RealmObject {
    @PrimaryKey
    String id;
    RealmList<FeedFeatured> feedFeaturedRealmList = new RealmList<>();
    String ts_prev;
    String ts_next;

    public FeedFeaturedResponse() {

    }

    public FeedFeaturedResponse(JSONObject jsonObject) {
        setId("0");

        try {
            JSONArray jsonArray = jsonObject.getJSONArray("results");

            if (jsonObject.has("results")) {
                if (jsonArray.length() != 0) {
                    RealmList<FeedFeatured> theFeedFeaturedRealmList = new RealmList<>();
                    for (int i=0; i<jsonArray.length(); i++) {
                        FeedFeatured feedFeatured = new FeedFeatured(jsonArray.getJSONObject(i));
                        theFeedFeaturedRealmList.add(feedFeatured);

                    }
                    setFeedFeaturedArrayList(theFeedFeaturedRealmList);
                }
            }

            if (jsonObject.has("ts_next")) {
                setTs_next(jsonObject.getString("ts_next"));
            }

            if (jsonObject.has("ts_prev")) {
                setTs_prev(jsonObject.getString("ts_prev"));
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

    public RealmList<FeedFeatured> getFeedFeaturedRealmList() {
        return feedFeaturedRealmList;
    }

    public void setFeedFeaturedArrayList(RealmList<FeedFeatured> feedFeaturedRealmList) {
        this.feedFeaturedRealmList = feedFeaturedRealmList;
    }

    public String getTs_prev() {
        return ts_prev;
    }

    public void setTs_prev(String ts_prev) {
        this.ts_prev = ts_prev;
    }

    public String getTs_next() {
        return ts_next;
    }

    public void setTs_next(String ts_next) {
        this.ts_next = ts_next;
    }
}
