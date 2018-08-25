package bean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import singleton.InterfaceManager;

/**
 * Created by jessica on 12/27/17.
 */

public class NewEVoucher extends RealmObject implements Serializable{

    @PrimaryKey
    private int id;

    private String name;
    private String thumbnailImagePath;
    private String displayImagePath;
    private String redemptionType;
    private Boolean isUsed;
    private String validUntil;
    private String termsAndConditions;
    private String eVoucher;
    private String rewardId;
    private String usedValidationType;
    public NewEVoucher(){

    }

    public NewEVoucher(JSONObject jsonObject) {
        try {
            if (jsonObject.has("id")) {
                setId(jsonObject.getInt("id"));
            }

            if (jsonObject.has("reward")) {
                JSONObject rewardJsonObject = jsonObject.getJSONObject("reward");
                if(rewardJsonObject.has("thumbnail_img_url")){
                    String imageUrl = rewardJsonObject.getString("thumbnail_img_url");
                    setThumbnailImagePath(imageUrl.substring(1,imageUrl.length()));
                }
                if(rewardJsonObject.has("display_img_url")){
                    String imageUrl = rewardJsonObject.getString("display_img_url");
                    setDisplayImagePath(imageUrl.substring(1,imageUrl.length()));
                }
                if(rewardJsonObject.has("valid_until")){
                    String valid_until = rewardJsonObject.getString("valid_until");
                    setValidUntil(valid_until);
                }
                if(rewardJsonObject.has("terms_and_conditions")){
                    setTermsAndConditions(rewardJsonObject.getString("terms_and_conditions"));
                }
                if (rewardJsonObject.has("name")){
                    setName(rewardJsonObject.getString("name"));
                }
                if(rewardJsonObject.has("pk")){
                    setRewardId(rewardJsonObject.getString("pk"));
                }
            }
            if (jsonObject.has("evoucher")) {
                seteVoucherId(jsonObject.getString("evoucher"));
            }
            if (jsonObject.has("is_used")) {
                setUsed(jsonObject.getBoolean("is_used"));
            }
            if (jsonObject.has("used_validation_type")){
                setUsedValidationType(jsonObject.getString("used_validation_type"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbnailImagePath() {
        return thumbnailImagePath;
    }

    public void setThumbnailImagePath(String thumbnailImagePath) {
        this.thumbnailImagePath = thumbnailImagePath;
    }

    public String getDisplayImagePath() {
        return displayImagePath;
    }

    public void setDisplayImagePath(String displayImagePath) {
        this.displayImagePath = displayImagePath;
    }

    public String getRedemptionType() {
        return redemptionType;
    }

    public void setRedemptionType(String redemptionType) {
        this.redemptionType = redemptionType;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }

    public String getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(String validUntil) {
        this.validUntil = validUntil;
    }

    public String getTermsAndConditions() {
        return termsAndConditions;
    }

    public void setTermsAndConditions(String termsAndConditions) {
        this.termsAndConditions = termsAndConditions;
    }

    public String geteVoucher() {
        return eVoucher;
    }

    public void seteVoucherId(String eVoucher) {
        this.eVoucher = eVoucher;
    }

    public String getRewardId() {
        return rewardId;
    }

    public void setRewardId(String rewardId) {
        this.rewardId = rewardId;
    }

    public String getUsedValidationType() {
        return usedValidationType;
    }

    public void setUsedValidationType(String usedValidationType) {
        this.usedValidationType = usedValidationType;
    }
}
