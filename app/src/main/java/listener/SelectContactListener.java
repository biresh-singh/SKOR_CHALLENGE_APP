package listener;

import com.quickblox.users.model.QBUser;

/**
 * Created by dss-17 on 5/18/17.
 */

public interface SelectContactListener {
    public void onSelectContactListener(QBUser qbUser,boolean isFromDisplay);
}
