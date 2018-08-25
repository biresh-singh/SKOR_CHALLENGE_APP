package bean;

import java.io.Serializable;

/**
 * Created by dss-10 on 4/18/17.
 */

public class RewardContactDetail implements Serializable {
    public int pk = 0;
    public String name = "";
    public String email = "";
    public String phone = "";
    public String sms_number = "";

    public RewardContactDetail(int pk, String name, String email, String phone, String sms_number) {
        this.pk = pk;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.sms_number = sms_number;
    }
}
