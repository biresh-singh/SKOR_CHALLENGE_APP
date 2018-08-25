package bean;

import java.io.Serializable;

/**
 * Created by Dihardja Software Solutions on 12/27/16.
 */

public class SelectCityListItem implements Serializable {

    String pk="";
    String city="";

    public SelectCityListItem() {
    }

    public SelectCityListItem(String pk, String city)
    {
        this.pk=pk;
        this.city=city;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPk() {
        return pk;
    }

    public String getCity() {
        return city;
    }
}
