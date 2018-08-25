package bean;


public class PushNotificationItem {
    public String mpk;
    public String mSender;
    public String mMessage;
    public String mImage;
    public String mObject_id;
    public String mObject_type;
    public String mIs_read;
    public String mCreated;

    public PushNotificationItem(String pk, String sender, String message, String image, String object_id, String object_type, String is_read, String created) {
        this.mpk = pk;
        this.mSender = sender;
        this.mMessage = message;
        this.mImage = image;
        this.mObject_id = object_id;
        this.mObject_type = object_type;
        this.mIs_read = is_read;
        this.mCreated = created;

    }

}
