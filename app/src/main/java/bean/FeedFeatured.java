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
 * Created by dss-17 on 8/4/17.
 */

public class FeedFeatured extends RealmObject implements Parcelable{
    @PrimaryKey
    String id;
    String file;
    String video;
    String title;
    String description;
    String created;
    String updated;
    String totalComment;
    String totalLike;
    boolean allowComment;
    String start;
    String end;
    int totalView;
    boolean allowShare;
    String favorite;
    String like;
    boolean isNews;
    String videoThumbnail;
    RealmList<Image> images = new RealmList<>();
    Image image;
    ObjectModel objectModel;
    Category category;

    public FeedFeatured() {
    }

    public FeedFeatured(JSONObject jsonObject) {
        try {
            if (jsonObject.has("id")) {
                setId(jsonObject.getString("id"));
            }
            if (jsonObject.has("file")) {
                if (!jsonObject.isNull("file")) {
                    setFile(jsonObject.getString("file"));
                }
            }
            if (jsonObject.has("video")) {
                if (!jsonObject.isNull("video")) {
                    setVideo(jsonObject.getString("video"));
                }
            }
            if (jsonObject.has("title")) {
                setTitle(jsonObject.getString("title"));
            }
            if (jsonObject.has("start")) {
                setStart(jsonObject.getString("start"));
            }
            if (jsonObject.has("end")) {
                setEnd(jsonObject.getString("end"));
            }
            if (jsonObject.has("description") && !jsonObject.isNull("description")) {
                setDescription(jsonObject.getString("description"));
            }
            if (jsonObject.has("created")) {
                setCreated(jsonObject.getString("created"));
            }
            if (jsonObject.has("is_news")) {
                setNews(jsonObject.getBoolean("is_news"));
            }
            if (jsonObject.has("updated")) {
                setUpdated(jsonObject.getString("updated"));
            }
            if (jsonObject.has("favorite")) {
                if (!jsonObject.isNull("favorite")) {
                    setFavorite(jsonObject.getString("favorite"));
                }
            }
            if (jsonObject.has("vid_thumb")) {
                if (!jsonObject.isNull("vid_thumb")) {
                    setVideoThumbnail(jsonObject.getString("vid_thumb"));
                }
            }
            if (jsonObject.has("like")) {
                if (!jsonObject.isNull("like")) {
                    setLike(jsonObject.getString("like"));
                }
            }
            if (jsonObject.has("total_views")) {
                setTotalView(jsonObject.getInt("total_views"));
            }
            if (jsonObject.has("total_comment")) {
                setTotalComment(jsonObject.getString("total_comment"));
            }
            if (jsonObject.has("total_like")) {
                setTotalLike(jsonObject.getString("total_like"));
            }
            if (jsonObject.has("images")) {
                JSONObject imagesJsonObject = jsonObject.getJSONObject("images");
                if (!imagesJsonObject.isNull("img")) {
                    JSONObject imgJsonObject = imagesJsonObject.getJSONObject("img");
                    Image image = new Image(imgJsonObject);
                    setImage(new Image(imgJsonObject));
                    images.add(image);
                }
                if (!imagesJsonObject.isNull("img2")) {
                    JSONObject imgJsonObject = imagesJsonObject.getJSONObject("img2");
                    Image image = new Image(imgJsonObject);
                    images.add(image);
                }
                if (!imagesJsonObject.isNull("img3")) {
                    JSONObject imgJsonObject = imagesJsonObject.getJSONObject("img3");
                    Image image = new Image(imgJsonObject);
                    images.add(image);
                }
            }
            if (jsonObject.has("category")) {
                if (!jsonObject.isNull("category")) {
                    setCategory(new Category(jsonObject.getJSONObject("category")));
                }
            }
            if (jsonObject.has("object")) {
                if (!jsonObject.isNull("object")) {
                    setObjectModel(new ObjectModel(jsonObject.getJSONObject("object")));
                }
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

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getTotalComment() {
        return totalComment;
    }

    public void setTotalComment(String totalComment) {
        this.totalComment = totalComment;
    }

    public String getTotalLike() {
        return totalLike;
    }

    public void setTotalLike(String totalLike) {
        this.totalLike = totalLike;
    }

    public boolean isAllowComment() {
        return allowComment;
    }

    public void setAllowComment(boolean allowComment) {
        this.allowComment = allowComment;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public int getTotalView() {
        return totalView;
    }

    public void setTotalView(int totalView) {
        this.totalView = totalView;
    }

    public boolean isAllowShare() {
        return allowShare;
    }

    public void setAllowShare(boolean allowShare) {
        this.allowShare = allowShare;
    }

    public String getFavorite() {
        return favorite;
    }

    public void setFavorite(String favorite) {
        this.favorite = favorite;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public boolean isNews() {
        return isNews;
    }

    public void setNews(boolean news) {
        isNews = news;
    }

    public String getVideoThumbnail() {
        return videoThumbnail;
    }

    public void setVideoThumbnail(String videoThumbnail) {
        this.videoThumbnail = videoThumbnail;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public RealmList<Image> getImages() {
        return images;
    }

    public void setImages(RealmList<Image> images) {
        this.images = images;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public ObjectModel getObjectModel() {
        return objectModel;
    }

    public void setObjectModel(ObjectModel objectModel) {
        this.objectModel = objectModel;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.file);
        dest.writeString(this.video);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.created);
        dest.writeString(this.updated);
        dest.writeString(this.totalComment);
        dest.writeString(this.totalLike);
        dest.writeByte(this.allowComment ? (byte) 1 : (byte) 0);
        dest.writeString(this.start);
        dest.writeString(this.end);
        dest.writeInt(this.totalView);
        dest.writeByte(this.allowShare ? (byte) 1 : (byte) 0);
        dest.writeString(this.favorite);
        dest.writeString(this.like);
        dest.writeByte(this.isNews ? (byte) 1 : (byte) 0);
        dest.writeString(this.videoThumbnail);
        dest.writeList(this.images);
        dest.writeParcelable(this.image, flags);
        dest.writeParcelable(this.objectModel, flags);
        dest.writeParcelable(this.category, flags);
    }

    protected FeedFeatured(Parcel in) {
        this.id = in.readString();
        this.file = in.readString();
        this.video = in.readString();
        this.title = in.readString();
        this.description = in.readString();
        this.created = in.readString();
        this.updated = in.readString();
        this.totalComment = in.readString();
        this.totalLike = in.readString();
        this.allowComment = in.readByte() != 0;
        this.start = in.readString();
        this.end = in.readString();
        this.totalView = in.readInt();
        this.allowShare = in.readByte() != 0;
        this.favorite = in.readString();
        this.like = in.readString();
        this.isNews = in.readByte() != 0;
        this.videoThumbnail = in.readString();
        this.images = new RealmList<>();
        in.readList(this.images, Image.class.getClassLoader());
        this.image = in.readParcelable(Image.class.getClassLoader());
        this.objectModel = in.readParcelable(ObjectModel.class.getClassLoader());
        this.category = in.readParcelable(Category.class.getClassLoader());
    }

    public static final Creator<FeedFeatured> CREATOR = new Creator<FeedFeatured>() {
        @Override
        public FeedFeatured createFromParcel(Parcel source) {
            return new FeedFeatured(source);
        }

        @Override
        public FeedFeatured[] newArray(int size) {
            return new FeedFeatured[size];
        }
    };
}
