package gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.root.skor.R;

import java.util.Map;

import activity.userprofile.MainActivity;
import constants.Constants;
import database.SharedDatabase;


public class MyfireBaseIntentService extends FirebaseMessagingService {
    public static boolean gcmintent = false;
    public static boolean gcmintent1 = false;
    public static final int NOTIFICATION_ID = 1000;
    NotificationManager mNotificationManager;
    SharedDatabase sharedDatabase;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        final Map<String,String> extras = remoteMessage.getData();

        Log.v("extras", extras + "");
        String message = extras.get("message");
        String object_id = extras.get("object_id");
        String object_type = extras.get("object_type");
        String from = extras.get("from");
        String image = extras.get("image");
        String pk = extras.get("pk");
        String badgeCounter = extras.get("badge");

        String dialog_id = extras.get("thread_id");
        String chat_type = extras.get("chat_type");
        Integer user_id = -1;
        if(extras.get("user_id")!=null){
            user_id = Integer.valueOf(extras.get("user_id"));
        }

        if (message != null) {
            sendNotification(object_id, message, object_type, from, image, pk, dialog_id, chat_type, user_id);
        }
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @Override
    public void onMessageSent(String s) {
        super.onMessageSent(s);
    }

    @Override
    public void onSendError(String s, Exception e) {
        super.onSendError(s, e);
    }


//    @Override
//    public void zzm(Intent intent) {
//        super.zzm(intent);
//        final Bundle extras = intent.getExtras();
////        if (!extras.isEmpty()) {
////            if ( extras.getString("type") != null)
////            {
////                if ( extras.getString("type").equals(QBDialogType.GROUP)) // handling notif from group
////                {
////                    QBUsers.getUser(Integer.valueOf(extras.getString("user_id"))).performAsync(new QBEntityCallback<QBUser>() {
////                        @Override
////                        public void onSuccess(QBUser qbUser, Bundle bundle) {
////                            QBChatDialog qbChatDialog = DialogUtils.buildPrivateDialog(qbUser.getId());
////
////                            Intent intentGroup = new Intent(MyfireBaseIntentService.this, ChattingActivity.class);
////                            intentGroup.putExtra("qbUser", qbUser);
////                            SettingsManager.getInstance().setQbChatDialog(qbChatDialog);
////                            startActivity(intentGroup);
////                        }
////
////                        @Override
////                        public void onError(QBResponseException e) {
////
////                        }
////                    });
////
////                }else{ // handling notif from private
//////                    QBChatDialog dialog= DialogUtils.buildPrivateDialog(extras.getInt("user_id"));
////
////                    new Handler(Looper.myLooper()){
////                        @Override
////                        public void handleMessage(Message msg) {
////                            super.handleMessage(msg);
////
////                            QBRestChatService.createChatDialog(DialogUtils.buildPrivateDialog(extras.getInt("user_id"))).performAsync(new QBEntityCallback<QBChatDialog>() {
////                                @Override
////                                public void onSuccess(final QBChatDialog qbChatDialog, Bundle params) {
////                                    Log.e("s", "onSuccess: ");
////
////                                    QBUsers.getUser(extras.getInt("user_id")).performAsync(new QBEntityCallback<QBUser>() {
////                                        @Override
////                                        public void onSuccess(QBUser qbUser, Bundle bundle) {
////                                            Intent intentPrivate = new Intent(MyfireBaseIntentService.this, ChattingActivity.class);
////                                            intentPrivate.putExtra("qbUser", qbUser);
////                                            SettingsManager.getInstance().setQbChatDialog(qbChatDialog);
////                                            startActivity(intentPrivate);
////                                        }
////
////                                        @Override
////                                        public void onError(QBResponseException e) {
////
////                                        }
////                                    });
////                                }
////
////                                @Override
////                                public void onError(QBResponseException responseException) {
////                                    Log.e("s", "onError: ");
////                                }
////                            });
////                        }
////                    };
////
////
////                }
////
////            }

        //Notification
//        Log.v("extras", extras + "");
//        String message = extras.getString("message");
//        String object_id = extras.getString("object_id");
//        String object_type = extras.getString("object_type");
//        String from = extras.getString("from");
//        String image = extras.getString("image");
//        String pk = extras.getString("pk");
//        String badgeCounter = extras.getString("badge");
//
//        String dialog_id = extras.getString("dialog_id");
//        String chat_type = extras.getString("chat_type");
//        Integer user_id = 1;
//        if(extras.getString("user_id")!=null){
//            user_id = Integer.valueOf(extras.getString("user_id"));
//        }
//
//        if (message != null) {
//            sendNotification(object_id, message, object_type, from, image, pk, dialog_id, chat_type, user_id);
//        }
//    }

    private void sendNotification(final String object_id, final String message, final String object_type, final String from, final String image, final String pk, final String dialogId, final String chatType, final Integer userId) {
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String appName = getResources().getString(R.string.app_name);
        Intent intent = new Intent(this, MainActivity.class);
        if (message != null) {
            sharedDatabase = new SharedDatabase(getApplicationContext());
            gcmintent = true;
            gcmintent1 = true;
            sharedDatabase.setMessage(message);
            sharedDatabase.setObjectid(object_id);
            sharedDatabase.setObjecttype(object_type);
            sharedDatabase.setFrom(from);
            sharedDatabase.setImage(image);
            sharedDatabase.setPk(pk);
            sharedDatabase.setDialogId(dialogId);
            sharedDatabase.setChatType(chatType);

            if (userId != -1)
                sharedDatabase.setUserId(userId);

//            if(object_type.equalsIgnoreCase("Announcement")) {
//                intent.putExtra("announcementId", object_id);
//            }
            intent.putExtra("isFromNotif",true);
            sharedDatabase.pushNotifReceived(true);

            intent.putExtra("action", Constants.ACTION_NONE);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Uri notificationsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());
            mBuilder.setContentTitle(appName);
            mBuilder.setAutoCancel(true);
            mBuilder.setVibrate(new long[]{100, 250, 100, 250, 100, 250});
            mBuilder.setSound(notificationsound);
            mBuilder.setContentText(message);
            mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
            mBuilder.setLargeIcon(bm).setSmallIcon(getNotificationIcon());
            mBuilder.setContentIntent(contentIntent);
            mBuilder.setColor(Color.parseColor("#ED4E23"));
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

        } else {
            gcmintent = false;
            sharedDatabase.setMessage("");
            sharedDatabase.setObjectid("");
            sharedDatabase.setObjecttype("");
            sharedDatabase.setFrom("");
            sharedDatabase.setImage("");
            sharedDatabase.setPk("");
        }

    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT);
        return useWhiteIcon ? R.mipmap.popimage : R.mipmap.ic_launcher;
    }

}
