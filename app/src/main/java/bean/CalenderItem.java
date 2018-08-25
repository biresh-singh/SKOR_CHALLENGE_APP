package bean;

import java.util.Date;


public class CalenderItem {

    public String mImageUrl;
    public String meventName;
    public String mEventdate;
    public String mEventTime;
    public boolean mIsEvent;
    public boolean mIsAppointsments;
    public String mRsvpState;
    public String mEventId;
    public String jsonObjectResponse;
    public String type;
    public Date mDate;
    public boolean mIsGreetings;
    public String address;
    public String email;

    public CalenderItem(String imageurl, String eventname, String eventDate, String eventtime, boolean isEvent, boolean isAppointments, String rsvp_state, String eventId, String jsonObjectResponse, String type, boolean isGreetings, String address, String email) {
        this.mImageUrl = imageurl;
        this.meventName = eventname;
        this.mEventdate = eventDate;
        this.mEventTime = eventtime;
        this.mIsEvent = isEvent;
        this.mIsAppointsments = isAppointments;
        this.mRsvpState = rsvp_state;
        this.mEventId = eventId;
        this.mIsGreetings = isGreetings;
        this.jsonObjectResponse = jsonObjectResponse;
        this.type = type;
        this.address = address;
        this.email = email;

    }

    public void setDate(Date date) {
        this.mDate = date;
    }

    public Date getDate() {
        return mDate;
    }

}
