package database;

import android.content.Context;
import android.content.SharedPreferences;

import com.root.skor.R;

import java.util.Set;

public class SharedDatabase {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;
    public static final String SHAREDDATABASE = "" + R.string.app_name;

    public SharedDatabase(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(SHAREDDATABASE, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

    }

    public void clearPref(){
        editor.clear();
        editor.commit();
    }

    public void pushNotifReceived(Boolean pushNotifReceived) {
        editor.putBoolean("pushNotifReceived", pushNotifReceived);
        editor.commit();
    }

    public boolean getPushNotifReceived() {
        boolean pushNotifReceived = sharedPreferences.getBoolean("pushNotifReceived", false);
        return pushNotifReceived;
    }

    public void userToken(String token) {
        editor.putString("token", token);
        editor.commit();
    }

    public String getToken() {
        String token = sharedPreferences.getString("token", "");
        return token;
    }

    public void setIsfirtstime(boolean isfirtstime) {
        editor.putBoolean("isfirtstime", isfirtstime);
        editor.commit();
    }

    public boolean getIsfirtstime() {
        boolean token = sharedPreferences.getBoolean("isfirtstime", true);
        return token;
    }
    public void setIsfirtstimeWhatsOn(boolean isfirtstime) {
        editor.putBoolean("isfirtstimes", isfirtstime);
        editor.commit();
    }

    public boolean getIsfirtstimeWhatsOn() {
        boolean token = sharedPreferences.getBoolean("isfirtstimes", false);
        return token;
    }

    public void setGcmRegId(String gcmRegId) {
        editor.putString("gcmRegId", gcmRegId);
        editor.commit();
    }

    public String getGcmRegId() {
        String gcmRegId = sharedPreferences.getString("gcmRegId", "");
        return gcmRegId;
    }

    public void setFlag(String flag) {
        editor.putString("flag", flag);
        editor.commit();
    }

    public String getFlag() {
        String flag = sharedPreferences.getString("flag", "");
        return flag;
    }

    public void setUserName(String username) {
        editor.putString("username", username);
        editor.commit();
    }

    public String getUserName() {
        String username = sharedPreferences.getString("username", "");
        return username;
    }

    public void setIsStaff(boolean isStaff) {
        editor.putBoolean("isStaff", isStaff);
        boolean commit = editor.commit();
    }

    public boolean getIsStaff() {
        return sharedPreferences.getBoolean("isStaff", false);
    }

    public void setOrganizationChat(String organizationChat) {
        editor.putString("organizationChat", organizationChat);
        editor.commit();
    }

    public String getOrganizationChat() {
        String organizationChat = sharedPreferences.getString("organizationChat", "");
        return organizationChat;
    }

    public void setUserPassword(String gcmRegId) {
        editor.putString("password", gcmRegId);
        editor.commit();
    }

    public String getUserPassword() {
        String gcmRegId = sharedPreferences.getString("password", "");
        return gcmRegId;
    }

    public void setMobileNumber(String mobileNumber) {
        editor.putString("mobileNumber", mobileNumber);
        editor.commit();

    }

    public String getMobileNumber() {
        String gcmRegId = sharedPreferences.getString("mobileNumber", "");
        return gcmRegId;
    }

    public void setOrganizationLogo(String organizationLogo) {
        editor.putString("organizationLogo", organizationLogo);
        editor.commit();
    }

    public String getOrganizationLogo() {
        String setOrganizationLogo = sharedPreferences.getString("organizationLogo", "");
        return setOrganizationLogo;
    }

    public void setDepartmentName(String departmentName) {
        editor.putString("department_name", departmentName);
        editor.commit();
    }

    public String getDepartmentName() {
        String gcmRegId = sharedPreferences.getString("department_name", "");

        return gcmRegId;
    }

    public String getEnablefacilitycheckin() {
        String gcmRegId = sharedPreferences.getString("enable_facility_checkin", "");
        return gcmRegId;
    }

    public void setEnablefacilitycheckin(String enablefacilitycheckin) {
        editor.putString("enable_facility_checkin", enablefacilitycheckin);
        editor.commit();
    }

    public void setEnableReferralPage(String enableReferralPage) {
        editor.putString("enable_referral_page", enableReferralPage);
        editor.commit();
    }

    public String getEnableReferralPage() {
        String gcmRegId = sharedPreferences.getString("enable_referral_page", "");
        return gcmRegId;
    }

    public void setStepactivitylimit(String stepactivitylimit) {
        editor.putString("step_activity_limit", stepactivitylimit);
        editor.commit();
    }

    public String getStepactivitylimit() {
        String stepactivitylimit = sharedPreferences.getString("step_activity_limit", "");
        return stepactivitylimit;
    }

    public void setEnableactivitytracker(String enableactivitytracker) {
        editor.putString("enable_activity_tracker", enableactivitytracker);
        editor.commit();
    }

    public String getEnableactivitytracker() {
        String enableactivitytracker = sharedPreferences.getString("enable_activity_tracker", "");
        return enableactivitytracker;
    }

    public void setProfilePic(String profilepic_url) {
        editor.putString("profile_pic_url", profilepic_url);
        editor.commit();
    }

    public String getProfilePic() {
        String profilepicurl = sharedPreferences.getString("profile_pic_url", "");
        return profilepicurl;
    }

    public void setHasdiscountcategories(boolean hasdiscountcategories) {
        editor.putBoolean("has_discount_categories", hasdiscountcategories);
        editor.commit();
    }

    public boolean getHasdiscountcategories() {
        boolean hasdiscountcategories = sharedPreferences.getBoolean("has_discount_categories", false);
        return hasdiscountcategories;
    }

    public void setHaspointcategories(boolean haspointcategories) {
        editor.putBoolean("has_point_categories", haspointcategories);
        editor.commit();
    }

    public boolean getHaspointcategories() {
        boolean haspointcategories = sharedPreferences.getBoolean("has_point_categories", false);
        return haspointcategories;
    }

    public void setEmail(String email) {
        editor.putString("email", email);
        editor.commit();
    }

    public String getEmail() {
        String haspointcategories = sharedPreferences.getString("email", "");
        return haspointcategories;
    }





    public void setFirstName(String first_name) {
        editor.putString("first_name", first_name);
        editor.commit();
    }

    public String getLastName() {
        String last_name = sharedPreferences.getString("last_name", "");
        return last_name;
    }

    public void setLastName(String last_name) {
        editor.putString("last_name", last_name);
        editor.commit();
    }

    public String getFirstName() {
        String first_name = sharedPreferences.getString("first_name", "");
        return first_name;
    }

    public void setPosition(int position) {
        editor.putInt("position", position);
        editor.commit();
    }

    public String getGfitaccount() {
        String first_name = sharedPreferences.getString("gfitaccount", "");
        return first_name;
    }

    public void setGfitaccount(String position) {
        editor.putString("gfitaccount", position);
        editor.commit();
    }

    public int getPosition() {
        int position = sharedPreferences.getInt("position", 0);
        return position;
    }

    public void setType(String type) {
        editor.putString("type", type);
        editor.commit();
    }

    public String getType() {
        String type = sharedPreferences.getString("type", "");
        return type;
    }

    public void setAuthorizedToQuickblox(boolean isAuthorizedToQuickblox) {
        editor.putBoolean("isAuthorizedToQuickblox", isAuthorizedToQuickblox);
        editor.commit();
    }

    public boolean getAuthorizedToQuickblox() {
        boolean isAuthorizedToQuickblox = sharedPreferences.getBoolean("isAuthorizedToQuickblox", false);
        return isAuthorizedToQuickblox;
    }

    public void setSubCategory(String type) {
        editor.putString("subCat", type);
        editor.commit();
    }

    public void setCategory(String type) {
        editor.putString("cat", type);
        editor.commit();
    }

    public String getSubCategory() {
        String type = sharedPreferences.getString("subCat", "");
        return type;
    }

    public String getCategory() {
        String type = sharedPreferences.getString("cat", "");
        return type;
    }

    public void setIsconnected(boolean isconnected) {
        editor.putBoolean("isconnected", isconnected);
        editor.commit();
    }

    public boolean getIsconnected() {
        boolean isconnected = sharedPreferences.getBoolean("isconnected", false);
        return isconnected;
    }

    public void setNavigationdrawerlearned(boolean navigationdrawerlearned) {
        editor.putBoolean("navigation_drawer_learned", navigationdrawerlearned);
        editor.commit();
    }

    public boolean getNavigationdrawerlearned() {
        boolean navigationdrawerlearned = sharedPreferences.getBoolean("navigation_drawer_learned", false);
        return navigationdrawerlearned;
    }

    public void setMessage(String message) {
        editor.putString("message", message);
        editor.commit();
    }

    public String getMessage() {
        String message = sharedPreferences.getString("message", "");
        return message;
    }

    public void setObjectid(String objectid) {
        editor.putString("object_id", objectid);
        editor.commit();
    }

    public String getObjectid() {
        String objectid = sharedPreferences.getString("object_id", "");
        return objectid;
    }

    public void setObjecttype(String objecttype) {
        editor.putString("object_type", objecttype);
        editor.commit();
    }

    public String getObjecttype() {
        String objecttype = sharedPreferences.getString("object_type", "");
        return objecttype;
    }

    public void setFrom(String from) {
        editor.putString("from", from);
        editor.commit();
    }

    public String getFrom() {
        String from = sharedPreferences.getString("from", "");
        return from;
    }

    public void setImage(String image) {
        editor.putString("image", image);
        editor.commit();
    }

    public String getImage() {
        String image = sharedPreferences.getString("image", "");
        return image;
    }

    public void setPk(String pk) {
        editor.putString("pk", pk);
        editor.commit();
    }

    public String getPk() {
        String pk = sharedPreferences.getString("pk", "");
        return pk;
    }

    public String getPoststepdate() {
        String poststepdate = sharedPreferences.getString("post_step_date", "");
        return poststepdate;
    }

    public void setPoststepdate(String poststepdate) {
        editor.putString("post_step_date", poststepdate);
        editor.commit();
    }

    public Set<String> getPkArrayListKey() {
        Set<String> poststepdate = sharedPreferences.getStringSet("eligibleRewardPkArrayListKey", null);
        return poststepdate;
    }

    public void setPkArrayListKey(Set<String> poststepdate) {
        editor.putStringSet("eligibleRewardPkArrayListKey", poststepdate);
        editor.commit();
    }

    public String getOrderofitems() {
        String orderofitems = sharedPreferences.getString("order_of_items", "");
        return orderofitems;
    }

    public void setOrderofitems(String orderofitems) {
        editor.putString("order_of_items", orderofitems);
        editor.commit();
    }

    public void setIsDiscount(boolean isDiscount) {
        editor.putBoolean("isDiscount", isDiscount);
        editor.commit();
    }

    public boolean isDiscount() {
        boolean isDiscount = sharedPreferences.getBoolean("isDiscount", false);
        return isDiscount;
    }

    public void setQuickblox_appId(String appId){
        editor.putString("quickblox_appId", appId);
        editor.commit();
    }

    public String getQuckblox_appId() {
        String appId = sharedPreferences.getString("quickblox_appId", "");
        return appId;
    }

    public void setQuickblox_authKey(String authKey){
        editor.putString("quickblox_authKey", authKey);
        editor.commit();
    }

    public String getQuckblox_authKey() {
        String authKey = sharedPreferences.getString("quickblox_authKey", "");
        return authKey;
    }

    public void setQuickblox_authSecret(String authSecret){
        editor.putString("quickblox_authSecret", authSecret);
        editor.commit();
    }

    public String getQuickblox_authSecret() {
        String authSecret = sharedPreferences.getString("quickblox_authSecret", "");
        return authSecret;
    }

    public void setLastMessagesCount(int count) {
        editor.putInt("lastMessagesCount", count);
        editor.commit();
    }

    public int getLastMessagesCount() {
        int count = sharedPreferences.getInt("lastMessagesCount", 0);
        return count;
    }

    public void setLastSkip(int count) {
        editor.putInt("lastSkip", count);
        editor.commit();
    }

    public int getLastSkip() {
        int count = sharedPreferences.getInt("lastSkip", 0);
        return count;
    }

    public void setChatType(String chatType){
        editor.putString("chatType",chatType);
        editor.commit();
    }

    public String getChatType(){
        return sharedPreferences.getString("chatType","");
    }

    public void setDialogId(String dialogId){
        editor.putString("dialogId",dialogId);
        editor.commit();
    }

    public String getDialogId(){
        return sharedPreferences.getString("dialogId","");
    }

    public void setUserId(Integer userId){
        editor.putInt("userId",userId);
        editor.commit();
    }

    public int getUserPk(){
        return sharedPreferences.getInt("userPk",0);
    }

    public void setUserPk(Integer userId){
        editor.putInt("userPk",userId);
        editor.commit();
    }

    public int getUserId(){
        return sharedPreferences.getInt("userId",0);
    }

    public void setCustomerSupportAuthorizationKey(String authSecret){
        editor.putString("authorization_key", authSecret);
        editor.commit();
    }
    public String getCustomerSupportAuthorizationKey() {
        String authSecret = sharedPreferences.getString("authorization_key", "");
        return authSecret;
    }
    public void setCustomerSupportApplicationId(String authSecret){
        editor.putString("application_id", authSecret);
        editor.commit();
    }
    public String getCustomerSupportApplicationId() {
        String authSecret = sharedPreferences.getString("application_id", "");
        return authSecret;
    }
    public void setCustomerSupportAuthorizationSecret(String authSecret){
        editor.putString("authorization_secret", authSecret);
        editor.commit();
    }
    public String getCustomerSupportAuthorizationSecret() {
        String authSecret = sharedPreferences.getString("authorization_secret", "");
        return authSecret;
    }
    public void setCustomerSupportTechnicalSupportEmail(String authSecret){
        editor.putString("technical_support_email", authSecret);
        editor.commit();
    }
    public String getCustomerSupportTechnicalSupportEmail() {
        String authSecret = sharedPreferences.getString("technical_support_email", "");
        return authSecret;
    }
    public void setCustomerSupportMerchantSupportEmail(String authSecret){
        editor.putString("merchant_support_email", authSecret);
        editor.commit();
    }
    public String getCustomerSupportMerchantSupportEmail() {
        String authSecret = sharedPreferences.getString("merchant_support_email", "");
        return authSecret;
    }

    public int getSelectedWalletId(){
        int walletId = sharedPreferences.getInt("walletId",0);
        return walletId;
    }

    public void setSelectedWalletId(int walletId){
        editor.putInt("walletId",walletId);
        editor.commit();
    }

}
