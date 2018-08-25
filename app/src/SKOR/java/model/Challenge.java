package model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import bean.Image;

/**
 * Created by biresh.singh on 04-08-2018.
 */

public class Challenge implements Parcelable {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("organization")
    @Expose
    private Organization organization;

    @SerializedName("challenge_title")
    @Expose
    private String challenge_title;

    @SerializedName("start_date_time")
    @Expose
    private String start_date_time;

    @SerializedName("end_date_time")
    @Expose
    private String end_date_time;

    @SerializedName("duration")
    @Expose
    private String duration;

    @SerializedName("frequency")
    @Expose
    private int frequency;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("rules")
    @Expose
    private List<Rule> rules;

    @SerializedName("challenge_type")
    @Expose
    private ChallengeType challenge_type;


    private int challenge_typeID;
    private String challenge_typeName;

    @SerializedName("validation")
    @Expose
    private Validation validation;

    private int validationID;
    private String validationName;

    @SerializedName("rewards")
    @Expose
    private Rewards rewards;

    private int rewardsID;
    private String rewardsName;

    @SerializedName("value")
    @Expose
    private int value;

    @SerializedName("tags")
    @Expose
    private List<Tags> tags;

    @SerializedName("icon")
    @Expose
    private Icon icon;

    @SerializedName("images")
    @Expose
    private List<Images> images;

    public Challenge()
    {

    }


    public int getid() {
        return id;
    }
    public void setid(int id) {
        this.id = id;
    }

    public Organization getOrganization() {
        return organization;
    }
    public void setOrganization(Organization organization) {
        this.organization = organization;
    }


    public String getChallengetitle() {
        return challenge_title;
    }

    public void setChallengetitle(String challenge_title) {
        this.challenge_title = challenge_title;
    }

    public String getStartdatetime() {
        return start_date_time;
    }

    public void setStartdatetime(String start_date_time) {
        this.start_date_time = start_date_time;
    }

    public String getEnddatetime() {
        return end_date_time;
    }

    public void setEnddatetime(String end_date_time) {
        this.end_date_time = end_date_time;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.end_date_time = duration;
    }

    public int getFrequency() {
        return frequency;
    }
    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Rule> getRule() {
        return rules;
    }
    public void setRule(List<Rule> rules) {
        this.rules = rules;
    }

    public ChallengeType getChallengeType() {
        return challenge_type;
    }
    public void setChallengeType(ChallengeType challenge_type) {
        this.challenge_type = challenge_type;
    }

    public int getChallengeTypeID() {
        return challenge_typeID;
    }
    public void setChallengeTypeID(int challenge_typeID) {
        this.challenge_typeID = challenge_typeID;
    }

    public String getChallengeTypeName() {
        return challenge_typeName;
    }
    public void setChallengeTypeName(String challenge_typeName) {
        this.challenge_typeName = challenge_typeName;
    }

    public Validation getValidation() {
        return validation;
    }
    public void setValidation(Validation validation) {
        this.validation = validation;
    }

    public int getValidationID() {
        return validationID;
    }
    public void setValidationID(int validationID) {
        this.validationID = validationID;
    }

    public String getValidationName() {
        return validationName;
    }
    public void setValidationName(String validationName) {
        this.validationName = validationName;
    }

    public Rewards getRewards() {
        return rewards;
    }
    public void setRewards(Rewards rewards) {
        this.rewards = rewards;
    }

    public int getRewardsID() {
        return rewardsID;
    }
    public void setRewardsID(int rewardsID) {
        this.rewardsID = rewardsID;
    }

    public String getRewardName() {
        return rewardsName;
    }
    public void setRewardName(String rewardsName) {
        this.rewardsName = rewardsName;
    }

    public int getValue() {
        return value;
    }
    public void setValue(int value) {
        this.value = value;
    }

    public List<Tags> getTags() {
        return tags;
    }
    public void setTags(List<Tags> tags) {
        this.tags = tags;
    }

    public Icon getIcon() {
        return icon;
    }
    public void setIcon(Icon icon) {
        this.icon = icon;
    }


    public List<Images> getImages() {
        return images;
    }

    public void setImages(List<Images> images) {
        this.images = images;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(id);
        dest.writeString(challenge_title);
        dest.writeString(start_date_time);
        dest.writeString(end_date_time);
        dest.writeString(duration);
        dest.writeInt(frequency);
        dest.writeString(description);

        //dest.writeParcelable(rewards , flags);
       // dest.writeValue(rewards);
       // dest.writeParcelable(rewards,flags);

        dest.writeList(rules);
        dest.writeInt(value);
        dest.writeList(tags);
        dest.writeList(images);

        dest.writeInt(challenge_typeID);
        dest.writeString(challenge_typeName);
        dest.writeInt(validationID);
        dest.writeString(validationName);
        dest.writeInt(rewardsID);
        dest.writeString(rewardsName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Challenge> CREATOR
            = new Creator<Challenge>() {
        public Challenge createFromParcel(Parcel in) {
            Challenge chg=new Challenge();

            return new Challenge(in);
        }

        public Challenge[] newArray(int size) {
            return new Challenge[size];
        }
    };

    public Challenge(Parcel in) {
        id = in.readInt();
        challenge_title = in.readString();
        start_date_time = in.readString();
        end_date_time = in.readString();
        duration = in.readString();

        //rewards=in.readParcelable(Rewards.class.getClassLoader());
        frequency = in.readInt();
        description = in.readString();
        rules=in.readArrayList(Rule.class.getClassLoader());
        value = in.readInt();
        //tags=in.readValue(Tags.class.getClassLoader());
        tags=in.readArrayList(Tags.class.getClassLoader());
        images=in.readArrayList(Image.class.getClassLoader());

        challenge_typeID = in.readInt();
        challenge_typeName = in.readString();
        validationID = in.readInt();
        validationName = in.readString();
        rewardsID = in.readInt();
        rewardsName = in.readString();
    }



}
