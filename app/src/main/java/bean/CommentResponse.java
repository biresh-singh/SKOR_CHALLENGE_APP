package bean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by dss-17 on 9/19/17.
 */

public class CommentResponse extends RealmObject{
    @PrimaryKey
    String id;
    String next;
    String previous;
    RealmList<Comment> commentRealmList = new RealmList<>();

    public CommentResponse() {

    }


    public CommentResponse(JSONObject jsonObject) {
        try {
            setId("0");

            if (jsonObject.has("next")) {
                String next = jsonObject.getString("next");
                if (next != null) {
                    setNext(next);
                }
            }
            if (jsonObject.has("previous")) {
                String previous = jsonObject.getString("previous");
                if (previous != null) {
                    setPrevious(previous);
                }
            }
            if (jsonObject.has("results")) {
                JSONArray jsonArray = jsonObject.getJSONArray("results");

                for (int i=0; i<jsonArray.length(); i++) {
                    Comment comment = new Comment(jsonArray.getJSONObject(i));
                    commentRealmList.add(comment);
                }
                setCommentRealmList(commentRealmList);
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

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public RealmList<Comment> getCommentRealmList() {
        return commentRealmList;
    }

    public void setCommentRealmList(RealmList<Comment> commentRealmList) {
        this.commentRealmList = commentRealmList;
    }
}
