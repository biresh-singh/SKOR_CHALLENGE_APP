package bean;

import java.io.Serializable;

/**
 * Created by dss-10 on 4/18/17.
 */

public class RewardLocation implements Serializable {
    public int pk = 0;
    public String name = "";
    public int reward = 0;
    public String city = "";
    public String address = "";
    public RewardContactDetail contactDetail;
    public double latitude = 0.00;
    public double longitude = 0.00;

    public RewardLocation(int pk, String name, int reward, String city, String address, RewardContactDetail contactDetail, double latitude, double longitude) {
        this.pk = pk;
        this.name = name;
        this.reward = reward;
        this.city = city;
        this.address = address;
        this.contactDetail = contactDetail;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
