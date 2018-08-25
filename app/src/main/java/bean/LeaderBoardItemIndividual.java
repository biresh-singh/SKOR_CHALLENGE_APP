package bean;


public class LeaderBoardItemIndividual {
    public double mTotalPoints;
    public String mPk;
    public String mFirstName;
    public String mImageUrl;


    public LeaderBoardItemIndividual(double totalPoints, String pk, String firstName, String imageUrl) {
        this.mTotalPoints = totalPoints;
        this.mPk = pk;
        this.mFirstName = firstName;
        this.mImageUrl = imageUrl;

    }
}
