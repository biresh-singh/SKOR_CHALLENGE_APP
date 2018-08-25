package bean;


public class HistoryItem {
   public String mName;
    public String mPoints;
    public String mdate;
    public String iconUrl;
    public String facilityLocationName;
    public String mRedemptionCode;

    public HistoryItem(String name, String points, String date, String iconUrl, String facilityLocationName,String redemptionCode)
    {
        this.mName=name;
        this.mPoints=points;
        this.mdate=date;
        this.iconUrl=iconUrl;
        this.facilityLocationName=facilityLocationName;
        this.mRedemptionCode=redemptionCode;

    }
}
