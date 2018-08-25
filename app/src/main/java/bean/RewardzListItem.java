package bean;

import java.io.Serializable;


public class RewardzListItem implements Serializable {

    public String mImageURL = "";
    public String mFacilityItem = "";
    public String mRating = "";
    public String mAddress;
    public String jsonResponse;
    public String pk;
    public String mPoint;
    public boolean mredeemableFlag;
    public int reward;

    public RewardzListItem(String pk, String imageurl, String facilityitem, String rating, String address, String jsonResponse, String point, boolean redeemableFlag, int reward) {
        this.pk = pk;
        this.mImageURL = imageurl;
        this.mFacilityItem = facilityitem;
        this.mRating = rating;
        this.mAddress = address;
        this.jsonResponse = jsonResponse;
        this.mPoint = point;
        this.mredeemableFlag = redeemableFlag;
        this.reward = reward;
    }
}
