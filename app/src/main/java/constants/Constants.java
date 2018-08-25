package constants;


public class Constants {

  //public static String BASEURL = "http://skordev.com/";
  public static String BASEURL="";
  public static String BASESTAGINFURL="http://staging-delhi.skorpoints.com/";
  public static String CHANGE_PASSWORD = "profiles/api/users/current/set_password/";
  public static String LOGIN_AUTH = "profiles/api/token-auth/";
  public static String LOGIN_OAUTH = "profiles/api/oauth/";
  public static String WHATS_ON = "events/api/whats_on";
  public static String ANNOUNCEMENT_DETAIL = "events/api/announcements/";
  public static String PUSH_NOTIFICATION = "push_notifications/api/notifications/";
  public static String EVENTS_URL = "events/api/events/weekly";
  public static String EVENTS_MONTHLY = "events/api/events/monthly";
  public static String RESET_PASSWORD = "profiles/api/users/reset_password/";
  public static String FACILTY_CHECKIN = "facilities/api/categories/";
  public static String CHECKIN = "facilities/api/checkin/";
  public static String PHOTO_GALLARIES="photo_galleries/api/event_gallery/";
  public static String EVENT_DETAIL = "events/api/events/";
  public static String REWARDZ_API = "rewards/api/rewards/";
  public static String REWARDZ_SELECT_CITY = "rewards/api/cities/";
  public static String REWARDZ_BY_CITY = "rewards/api/rewards/?city=";
  public static String TOTAL_POINTS_SUMMERY = "finance/api/transactions/points_summary/";
  public static String REFERAL_API = "messages/api/send/";
  public static String HISTORY_API = "finance/api/transactions";
  public static String EDITPROFILE = "profiles/api/users/current/";
  public static String REWARDZ_DETAIL = "rewards/api/rewards/";
  public static String REWARDZ_LOCATION_ID="rewards/api/locations/";
  public static String STEPS_POST = "services/api/activities/steps/";
  public static String EMERGENCY_SERVICES = "profiles/api/contact_details/emergency_contact/";
  public static String REWARDS_ITEMS = "rewards/api/categories/";
  public static String REDEMPTION_REWARDZ = "redemptions/api/redemptions/";
  public static String NEWEVOUCHER_REDEMPTION = "wallet/api/validate_voucher/";
  public static String NEWEVOUCHER_USE_VALIDATION = "wallet/api/used_validation/";
  public static String LEADERBOARD_BUSINESS = "profiles/api/leaderboard/";
  public static String CONNECT_SERVICE = "services/api/connect/";
  public static String RUNKEEPER = "services/api/disconnect/strava/";
  public static String CUSTOMER_SUPPORT = "profiles/api/customersupport/";
  public static String PUSH_NOTIFICATION_REFERESH = "push_notifications/api/gcm/";
  public static final String GET_UNUSED_WALLET = "wallet/api/wallet/?is_used=False&amp;pagination=false";
  public static final String GET_USED_WALLET = "wallet/api/wallet/?is_used=True&amp;pagination=false";
  public static final String POST_CONNECTED_DEVICES_TOKEN = "services/";
  public static final String GCM_REGISTER = "push_notifications/api/gcm/";
  public static final String IMAGE_UPLOAD="photo_galleries/api/event_gallery/";
  public static String RESULT = "http_code";
  public final static int GET = 1;
  public final static int POST = 2;
  public final static int DELETE = 3;
  public final static int PUT = 4;
  public final static int PUT_EDIT_PROFILE = 6;

  public static String ACTION_NONE = "none";
  public static String ACTION_CONNECTED_APPS = "connected_apps";
  public static String ACTION_REFER = "refer";
  public static String ACTION_FACILITY_CHECKIN = "facility_checkin";
  public static String ACTION_EVENT_CHECKIN = "event_checkin";
  public static String ACTION_POINT_SUMMERY = "point_summery";
  public static String ACTION_CHALLENGE = "action_challenge";
  public static String ACTION_WALLET = "wallet";

  public static String REWARDS_BY_LOCATION = "rewards/api/locations/nearby/";

  public static final String ChallengeFragment = "ChallengeFragment";
  public static final String TakeChallengeFragment = "TakeChallengeFragment";
  public static final String ProceedChallengeFragment = "ProceedChallengeFragment";

  public static final String TeamChallengeFragment = "TeamChallengeFragment";
  public static final String TakeTeamChallengeFragment = "TakeTeamChallengeFragment";
  public static final String ProceedTeamChallengeFragment = "ProceedTeamChallengeFragment";

  public static final String CHALLENGETYPE = "challengetype";
  public static final String TREASUREHUNT = "treasurehunt";
  public static final String YOGACLASS = "YOGACLASS";
  public static final String EMPLOYEEFUN = "employeefun";
  public static final String GYMCHECKIN = "gymcheckin";
  public static final String PHOTOHUNT = "photohunt";
  public static final String HEALTHYLIVINGARTICLES = "healthylivingarticles";
  public static final String WALKINGACTIVITY = "walkingactivity";
  public static final String FUNNIESTARTICLE = "funniestarticle";

  public static final String GETCHALLENGEDATA = "getchallengedata";
  public static final String GETCHALLENGEREWARDSDATA = "getchallengerewards";
}
