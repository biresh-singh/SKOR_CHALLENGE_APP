package bean;


public class LeaderBoardItem {
    public Double mTotalPoints;
    public String mPk;
    public Double mAveragePoint;
    public String mMemberPoint;
    public String mName;

    public LeaderBoardItem(Double totalPoints, String pk, Double averagePoint, String memberPoint, String name) {
        this.mTotalPoints = totalPoints;
        this.mPk = pk;
        this.mAveragePoint = averagePoint;
        this.mMemberPoint = memberPoint;
        this.mName = name;
    }
}
