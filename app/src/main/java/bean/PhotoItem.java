package bean;


public class PhotoItem {
    public String mPhoto;
    public String mImageThumbnail;
    public String mFullName;
    public String mTitle;
    public String mId;
    public PhotoItem(String photo, String imageThumbnail, String fullName, String title, String id){
        this.mTitle=title;
        this.mPhoto=photo;
        this.mImageThumbnail=imageThumbnail;
        this.mFullName=fullName;
        this.mId=id;

    }

}
