package bean;


public class FaciltyCheckinItem {
//    public String mImageURL = "";
//    public String mFacilityItem = "";
//    public String mRating = "";
//    public String mAddress;
//
//    public FaciltyCheckinItem(String imageurl, String facilityitem, String rating, String address) {
//        this.mImageURL = imageurl;
//        this.mFacilityItem = facilityitem;
//        this.mRating = rating;
//        this.mAddress = address;
//    }

    public String pk;
    public String name;
    public String mImageURL = "";
    public String thumbnailImageURL = "";
    public String displayImageURL = "";
    public String locationAddress;
    public String locationName;

    public FaciltyCheckinItem(String pk, String name, String mImageURL, String thumbnailImageURL, String displayImageURL, String locationAddress, String locationName) {
        this.pk = pk;
        this.name = name;
        this.mImageURL = mImageURL;
        this.thumbnailImageURL = thumbnailImageURL;
        this.displayImageURL = displayImageURL;
        this.locationAddress = locationAddress;
        this.locationName = locationName;
    }
}
