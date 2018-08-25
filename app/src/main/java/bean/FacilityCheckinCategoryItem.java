package bean;

/**
 * Created by dss-17 on 3/17/17.
 */

public class FacilityCheckinCategoryItem {
    public String pk;
    public String name;
    public String thumbnailImageURL = "";
    public String icon = "";

    public FacilityCheckinCategoryItem(String pk, String name, String thumbnailImageURL, String icon) {
        this.pk = pk;
        this.name = name;
        this.thumbnailImageURL = thumbnailImageURL;
        this.icon = icon;
    }
}
