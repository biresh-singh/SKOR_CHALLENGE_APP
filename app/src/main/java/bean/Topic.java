package bean;

import io.realm.RealmObject;

/**
 * Created by Dihardja Software Solutions on 3/14/18.
 */

public class Topic extends RealmObject {
    String topic;

    public Topic() {
    }

    public Topic(String topic) {
        setTopic(topic);
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
