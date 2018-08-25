package utils;

import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dss-17 on 5/17/17.
 */

public class DataHolder {
    private static DataHolder instance;
    private List<QBUser> qbUsers;
    private QBUser signInQbUser;
    private QBUser qbUser;
    private List<QBUser> selectedUsersForGroupList;

    private DataHolder() {
        qbUsers = new ArrayList<>();
        selectedUsersForGroupList = new ArrayList<>();
    }

    public void setQbUsers(List<QBUser> qbUsers) {
        this.qbUsers = qbUsers;
    }

    public static synchronized DataHolder getInstance() {
        if (instance == null) {
            instance = new DataHolder();
        }
        return instance;
    }

    public void addQbUsers(List<QBUser> qbUsers) {
        for (QBUser qbUser : qbUsers) {
            addQbUser(qbUser);
        }
    }

    public void addQbUserToGroup(QBUser qbUser){
        selectedUsersForGroupList.add(qbUser);
    }

    public void removeQbUserFromGroup(QBUser qbUser){
        selectedUsersForGroupList.remove(qbUser);
    }

    public void removeAllQbUserFromList(){
        selectedUsersForGroupList.clear();
    }

    public void addQbUser(QBUser qbUser) {
        if (!qbUsers.contains(qbUser)) {
            qbUsers.add(qbUser);
        }
    }


    public QBUser getQbUser() {
        return qbUser;
    }

    public void setQbUser(QBUser qbUser) {
        this.qbUser = qbUser;
    }

    public void updateQbUserList(int location, QBUser qbUser) {
        if (location != -1) {
            qbUsers.set(location, qbUser);
        }
    }

    public List<QBUser> getSelectedUsersForGroupList() {
        return selectedUsersForGroupList;
    }

    public void removeSelectedUsersForGroupList(QBUser bUsers) {
        selectedUsersForGroupList.remove(qbUsers);
    }

    public void setSelectedUsersForGroupList(List<QBUser> selectedUsersForGroupList) {
        this.selectedUsersForGroupList = selectedUsersForGroupList;
    }

    public List<QBUser> getQBUsers() {
        return qbUsers;
    }

    public void clear() {
        qbUsers.clear();
    }

    public QBUser getSignInQbUser() {
        return signInQbUser;
    }

    public void setSignInQbUser(QBUser singInQbUser) {
        this.signInQbUser = singInQbUser;
    }

    public boolean isSignedIn() {
        return signInQbUser != null;
    }
}
