package activity.skorchat;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.listeners.QBChatDialogTypingListener;
import com.quickblox.chat.model.QBAttachment;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.request.QBMessageGetBuilder;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBProgressCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.messages.QBPushNotifications;
import com.quickblox.messages.model.QBEnvironment;
import com.quickblox.messages.model.QBEvent;
import com.quickblox.messages.model.QBNotificationType;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.root.skor.R;

import org.jivesoftware.smack.XMPPException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import adaptor.MessageAdapter;
import constants.Constants;
import cz.msebera.android.httpclient.Header;
import database.SharedDatabase;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import listener.SelectAttachmentListener;
import singleton.InterfaceManager;
import singleton.SettingsManager;
import singleton.UserManager;
import utils.AndroidDeviceNames;
import utils.AppController;
import utils.ArialRegularTextView;
import utils.DataHolder;
import utils.Loader;

//import com.quickblox.messages.QBPushNotifications;
//import com.quickblox.messages.model.QBEnvironment;
//import com.quickblox.messages.model.QBEvent;
//import com.quickblox.messages.model.QBNotificationType;

/**
 * Created by dss-17 on 5/12/17.
 */

public class ChattingActivity extends AppCompatActivity {
    LinearLayout backButton, attachmentsLinearLayout, documentButton, cameraButton, galleryButton, videoButton, locationButton, contactButton;
    ArialRegularTextView groupNameTextView, groupMembersTextView, isTypingTextView;
    RelativeLayout groupNameRelativeLayout;
    FrameLayout groupPictureFrameLayout;
    ImageView submitButton, attachmentButton, voiceCallImageView, videoCallImageView, optionImageView;
    TextView recipientNameTitle, profilePictureTextView, groupPictureTextView;
    EditText message;
    CircleImageView recipientPhoto, onlineIndicatorImageView, groupPictureImageView;
    Set<String> supportedFileExtSetOfStrings = new HashSet<>();
    File file = null;
    Uri photoUri;
    String linkUrl = "";
    String previousMessageId = "";
    Boolean isAttachmentOpened = true;
    String filename, contactName, contactNumber;

    QBChatDialog qbChatDialog;
    RecyclerView recyclerView;
    QBUser opposingUser;

    ChatMessageListener chatMessageListener;
    LinearLayoutManager linearLayoutManager;
    MessageAdapter adapter;
    QBUser currentUser = QBChatService.getInstance().getUser();

    static final int REQUEST_IMAGE_VIDEO_CAPTURE = 1;
    static final int REQUEST_IMAGE_VIDEO_PICK = 2;
    static final int REQUEST_DOCUMENT_PICK = 3;
    static final int REQUEST_CONTACT_PICK = 4;
    static final int REQUEST_LOCATION_PICK = 5;
    static final int REQUEST_VIDEO_CAPTURE = 6;
    static final int REQUEST_CAPTION = 7;

    private static final int IMAGE_SIZE_LIMIT_KB = 1024 * 100;
    static File documentFile;

    //maps
    String lat_lng;
    MediaMetadataRetriever mediaMetadataRetriever;

    //for calling api checkValidURL
    AndroidDeviceNames deviceNames;
    public static String useragent = null;
    public SharedDatabase sharedDatabase;
    boolean isRunning = false;
    String token;
    String web_url, web_imageUrl, web_title, web_description;

    ProgressDialog progressDialog;

    //for paging
    public PtrClassicFrameLayout ptrClassicFrameLayout;
    int currentPage = 0;
    Boolean isLoadingNextPage = false;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client2;
    String userTypingName = "";
    List<QBUser> groupMembersList = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);
        initializeSupportedExt();
        sharedDatabase = new SharedDatabase(getApplicationContext());
        token = sharedDatabase.getToken();
        deviceNames = new AndroidDeviceNames(ChattingActivity.this);
        useragent = "Skor/3 Android|" + deviceNames.getDeviceName() + "|" + deviceNames.getAPIVerison() + "|" + getVersionCode();

        recipientPhoto = (CircleImageView) findViewById(R.id.activity_chating_userProfileImageView);
        onlineIndicatorImageView = (CircleImageView) findViewById(R.id.activity_chatting_onlineIndicatorCircleImageView);
        groupPictureImageView = (CircleImageView) findViewById(R.id.activity_chatting_groupPictureImageView);
        backButton = (LinearLayout) findViewById(R.id.activity_chating_backButton);
        attachmentsLinearLayout = (LinearLayout) findViewById(R.id.activity_chating_attachmentsLinearLayout);
        documentButton = (LinearLayout) findViewById(R.id.activity_chating_documentButton);
        cameraButton = (LinearLayout) findViewById(R.id.activity_chating_cameraButton);
        galleryButton = (LinearLayout) findViewById(R.id.activity_chating_galleryButton);
        videoButton = (LinearLayout) findViewById(R.id.activity_chating_videoButton);
        locationButton = (LinearLayout) findViewById(R.id.activity_chating_locationButton);
        contactButton = (LinearLayout) findViewById(R.id.activity_chating_contactButton);
        groupNameRelativeLayout = (RelativeLayout) findViewById(R.id.activity_chatting_groupNameRelativeLayout);
        groupPictureFrameLayout = (FrameLayout) findViewById(R.id.activity_chatting_groupPictureFrameLayout);
        submitButton = (ImageView) findViewById(R.id.activity_chating_submitButton);
        attachmentButton = (ImageView) findViewById(R.id.activity_chating_attachmentButton);
        voiceCallImageView = (ImageView) findViewById(R.id.activity_chatting_voiceCallImageView);
        videoCallImageView = (ImageView) findViewById(R.id.activity_chating_videoCallImageView);
        optionImageView = (ImageView) findViewById(R.id.activity_chatting_optionImageView);
        recipientNameTitle = (TextView) findViewById(R.id.activity_chating_name);
        groupPictureTextView = (TextView) findViewById(R.id.activity_chatting_groupPictureTextView);
        groupNameTextView = (ArialRegularTextView) findViewById(R.id.activity_chatting_groupNameTextView);
        groupMembersTextView = (ArialRegularTextView) findViewById(R.id.activity_chatting_groupMembersTextView);
        isTypingTextView = (ArialRegularTextView) findViewById(R.id.activity_chatting_isTypingTextView);
        profilePictureTextView = (TextView) findViewById(R.id.activity_chatting_profilePictureTextView);
        message = (EditText) findViewById(R.id.activity_chating_messageEditText);
        recyclerView = (RecyclerView) findViewById(R.id.activity_chating_messageRecyclerView);

        chatMessageListener = new ChatMessageListener();

        final CountDownTimer countDownTimer = new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                isRunning = true;
            }

            @Override
            public void onFinish() {
                qbChatDialog.sendStopTypingNotification(new QBEntityCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid, Bundle bundle) {

                    }

                    @Override
                    public void onError(QBResponseException e) {

                    }
                });
                isRunning = false;
            }
        };

        message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                qbChatDialog.sendIsTypingNotification(new QBEntityCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid, Bundle bundle) {

                    }

                    @Override
                    public void onError(QBResponseException e) {

                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isRunning) {
                    countDownTimer.cancel();
                }
                countDownTimer.start();
            }
        });
        message.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    qbChatDialog.sendStopTypingNotification(new QBEntityCallback<Void>() {
                        @Override
                        public void onSuccess(Void aVoid, Bundle bundle) {

                        }

                        @Override
                        public void onError(QBResponseException e) {

                        }
                    });

                    return true;
                }
                return false;
            }
        });

        message.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    qbChatDialog.sendStopTypingNotification(new QBEntityCallback<Void>() {
                        @Override
                        public void onSuccess(Void aVoid, Bundle bundle) {

                        }

                        @Override
                        public void onError(QBResponseException e) {

                        }
                    });
                }
            }
        });


        linearLayoutManager = new LinearLayoutManager(ChattingActivity.this);

        progressDialog = new ProgressDialog(ChattingActivity.this);
        progressDialog.setMessage("Downloading File");
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(true);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Chatting Activity", message.getText().toString().trim());
                if (!message.getText().toString().trim().equals("")) {
                    final QBChatMessage chatMessage = new QBChatMessage();
                    chatMessage.setBody(message.getText().toString().trim());
                    chatMessage.setProperty("save_to_history", "1");
                    chatMessage.setDateSent(System.currentTimeMillis() / 1000);
                    chatMessage.setMarkable(true);
                    chatMessage.setSenderId(currentUser.getId());
                    qbChatDialog.sendMessage(chatMessage, new QBEntityCallback<Void>() {
                        @Override
                        public void onSuccess(Void aVoid, Bundle bundle) {
                            QBEvent event = new QBEvent();
                            StringifyArrayList<Integer> userIds = new StringifyArrayList<Integer>();
                            userIds.addAll(qbChatDialog.getOccupants());
                            userIds.remove(currentUser.getId());
                            event.setUserIds(userIds);
                            event.setEnvironment(QBEnvironment.PRODUCTION);
                            event.setNotificationType(QBNotificationType.PUSH);
                            JSONObject json = new JSONObject();
                            try {
                                json.put("message", message.getText().toString().trim());

                                // custom parameters
                                if (qbChatDialog.getType() == QBDialogType.PRIVATE) {
                                    json.put("user_id", opposingUser.getId());
                                }
                                json.put("thread_id", qbChatDialog.getDialogId());
                                json.put("chat_type", qbChatDialog.getType().toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            event.setMessage(json.toString());
                            message.getText().clear();

                            QBPushNotifications.createEvent(event).performAsync(new QBEntityCallback<QBEvent>() {
                                @Override
                                public void onSuccess(QBEvent qbEvent, Bundle bundle) {
                                }

                                @Override
                                public void onError(QBResponseException e) {
                                    Toast.makeText(ChattingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });


                            //QBDialogType group already have refreshMessage() function, no need to call it again
                            if (QBDialogType.PRIVATE.equals(qbChatDialog.getType())) {
                                refreshMessage(chatMessage);
                            }
                        }

                        @Override
                        public void onError(QBResponseException e) {
                            Toast.makeText(ChattingActivity.this, "failed send message", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        groupNameRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChattingActivity.this, GroupInfoActivity.class);

                String groupOnlineUserCount = null;
                try {
                    groupOnlineUserCount = qbChatDialog.getOnlineUsers() + "";
                } catch (XMPPException e) {
                    e.printStackTrace();
                }
                intent.putExtra("groupOnlineUserCount", groupOnlineUserCount);
//                intent.putExtra("qbChatDialog", qbChatDialog);
                SettingsManager.getInstance().setQbChatDialog(qbChatDialog);
                intent.putExtra("groupName", qbChatDialog.getName());
                intent.putExtra("groupPhoto", qbChatDialog.getPhoto());
                intent.putExtra("groupMemberCount", qbChatDialog.getOccupants().size() + "");

                startActivity(intent);
            }
        });

        attachmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAttachmentOpened) {
                    attachmentsLinearLayout.setVisibility(View.VISIBLE);
                    isAttachmentOpened = false;
                } else {
                    attachmentsLinearLayout.setVisibility(View.GONE);
                    isAttachmentOpened = true;
                }
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askPermission(REQUEST_IMAGE_VIDEO_CAPTURE);
            }
        });

        optionImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(ChattingActivity.this, v, Gravity.NO_GRAVITY);
                popupMenu.inflate(R.menu.item_chat_options);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.item_chat_options_settings:
                                Intent settingsIntent = new Intent(ChattingActivity.this, SkorChatSettingsActivity.class);
                                startActivity(settingsIntent);
                                break;
                            case R.id.item_chat_options_createGroup:
                                Intent createGroupIntent = new Intent(ChattingActivity.this, CreateGroupActivity.class);
                                DataHolder.getInstance().addQbUserToGroup(opposingUser);
                                startActivity(createGroupIntent);
                                break;
                            case R.id.item_chat_options_clearChat:
                                QBMessageGetBuilder messageGetBuilder = new QBMessageGetBuilder();
                                messageGetBuilder.setLimit(500);
                                messageGetBuilder.setSkip(0);
                                messageGetBuilder.sortDesc("date_sent");

                                QBRestChatService.getDialogMessages(qbChatDialog, messageGetBuilder).performAsync(new QBEntityCallback<ArrayList<QBChatMessage>>() {
                                    @Override
                                    public void onSuccess(final ArrayList<QBChatMessage> qbChatMessages, Bundle bundle) {

                                        //for clear chat
                                        Set<String> messagesIdset = new HashSet<String>();

                                        for (int i = 0; i < qbChatMessages.size(); i++) {
                                            final int finalI = i;
                                            messagesIdset.add(qbChatMessages.get(finalI).getId());
                                        }

                                        QBRestChatService.deleteMessages(messagesIdset, false).performAsync(new QBEntityCallback<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid, Bundle bundle) {
                                                finish();
                                            }

                                            @Override
                                            public void onError(QBResponseException e) {
                                                Toast.makeText(ChattingActivity.this, "failed clear chat", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onError(QBResponseException e) {
                                        Toast.makeText(ChattingActivity.this, "failed load chat", Toast.LENGTH_SHORT).show();
                                    }

                                });
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askPermission(REQUEST_IMAGE_VIDEO_PICK);
            }
        });

        documentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askPermission(REQUEST_DOCUMENT_PICK);
            }
        });

        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askPermission(REQUEST_CONTACT_PICK);
            }
        });

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askPermission(REQUEST_LOCATION_PICK);
            }
        });

        videoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askPermission(REQUEST_VIDEO_CAPTURE);
            }
        });

        voiceCallImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent voiceCallIntent = new Intent(ChattingActivity.this, CallActivity.class);
                voiceCallIntent.putExtra("opposingUser", opposingUser);
                startActivity(voiceCallIntent);
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.v("ChattingActivity", " FirstVisibleItemPos= " + linearLayoutManager.findFirstVisibleItemPosition()
                        + " FirstCompletelyVisibleItemPos=  " + linearLayoutManager.findFirstCompletelyVisibleItemPosition()
                        + " LastVisibleItemPos=  " + linearLayoutManager.findLastVisibleItemPosition()
                        + " LastCompletelyVisibleItemPos=  " + linearLayoutManager.findLastCompletelyVisibleItemPosition()
                );
                if (linearLayoutManager.findFirstVisibleItemPosition() == 0) {
                    if (!isLoadingNextPage) {
                        loadChatHistory(currentPage * 10);
                    }
                }

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            photoUri = Uri.fromFile(file);
            try {
                OutputStream outStream;
                Bitmap bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
                if (photoUri != null) {
                    bitmap = MediaStore.Images.Media.getBitmap(ChattingActivity.this.getContentResolver(), photoUri);
                } else {
                    bitmap = (Bitmap) data.getExtras().getParcelable("data");
                }
                photoUri = getImageUri(bitmap);
            } catch (IOException e) {
                Log.e("error creating file", e.getLocalizedMessage());
                e.printStackTrace();
            }
            Compressor compressor = new Compressor(ChattingActivity.this);

            try {
                file = compressor
                        .setMaxWidth(300)
                        .setMaxHeight(300)
                        .setQuality(75)
                        .compressToFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(ChattingActivity.this, CaptionActivity.class);
            intent.putExtra("file", file);
            startActivityForResult(intent, REQUEST_CAPTION);
//                uploadAttachment(file, "image", "");
        } else if (requestCode == REQUEST_IMAGE_VIDEO_PICK && resultCode == RESULT_OK) {
            onSelectFromGalleryResult(data);
        } else if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Uri capturedVideo = data.getData();
            String selectedVideoPath = getPath(capturedVideo);

            if (selectedVideoPath == null) {
                selectedVideoPath = capturedVideo.getPath();
            }

            File file = new File(selectedVideoPath);
            file.getAbsoluteFile();

            Intent intent = new Intent(ChattingActivity.this, CaptionActivity.class);
            intent.putExtra("file", file);
            intent.putExtra("selectedVideoPath", selectedVideoPath);
            startActivityForResult(intent, REQUEST_CAPTION);
        } else if (requestCode == REQUEST_DOCUMENT_PICK && resultCode == RESULT_OK) {
            Uri selectedDocumentUri = data.getData();
            String mimeType = getContentResolver().getType(selectedDocumentUri);
            if (mimeType == null) {
                String path = getPath(this, selectedDocumentUri);
                if (path == null) {
//                    filename = FilenameUtils.getName(selectedDocumentUri.toString());
                } else {
                    File file = new File(path);
                    filename = file.getName();
                }
            } else {
                Uri returnUri = data.getData();
                Cursor returnCursor = getContentResolver().query(returnUri, null, null, null, null);
                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                returnCursor.moveToFirst();
                filename = returnCursor.getString(nameIndex);
                String size = Long.toString(returnCursor.getLong(sizeIndex));
            }
            File fileSave = new File(getExternalFilesDir(null), "generated.doc");
            String sourcePath = getExternalFilesDir(null).toString();
            try {
                copyFileStream(new File(sourcePath + "/" + filename), selectedDocumentUri, this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.v("File size : ", fileSave.length() + "");
            Log.v("File Name : ", filename);
//            String tempFileName = filename;
//            String[] fileNameSplittedString = tempFileName.split("\\.");
//            String fileExt = fileNameSplittedString[1];
//            if(supportedFileExtSetOfStrings.contains("."+fileExt)){
            fileSave = new File(sourcePath + "/" + filename);
            if (fileSave.length() / (Math.pow(1024, 2)) < 25) {
                uploadAttachment(fileSave, "document", "");
            } else {
                Toast.makeText(this, "File size is too large, please upload file with size below 25mb", Toast.LENGTH_SHORT).show();
            }
//            }else{
//                Toast.makeText(this, "File extension is not supported, please choose another file", Toast.LENGTH_SHORT).show();
//            }
        } else if (requestCode == REQUEST_CONTACT_PICK && resultCode == RESULT_OK) {
            createvCard(data);
        } else if (requestCode == REQUEST_LOCATION_PICK) {
            if (data != null) {
//                lat_lng = data.getStringExtra("lat_lng");
//                sendLocation(lat_lng, "location");

                Place place = PlacePicker.getPlace(data, this);
                String latLng = String.format(place.getLatLng() + "");

                String lat = latLng.substring(latLng.indexOf("(") + 1, latLng.indexOf(","));
                String lng = latLng.substring(latLng.indexOf(",") + 1, latLng.indexOf(")"));
                String lat_lng = lat + "_" + lng;
                sendLocation(lat_lng, "location");
            }
        } else if (requestCode == REQUEST_CAPTION) {
            Bundle extras = data.getExtras();
            File file = (File) extras.get("file");
            String caption = (String) extras.get("caption");
            uploadAttachment(file, "", caption);
        }
    }

    QBChatDialogTypingListener typingListener = new QBChatDialogTypingListener() {
        @Override
        public void processUserIsTyping(String s, final Integer integer) {
            if (!userTypingName.equals("")) {
                if (isTypingTextView.getVisibility() == View.GONE) {
                    isTypingTextView.setVisibility(View.VISIBLE);
                }
            } else {
                for (QBUser user : groupMembersList) {
                    if (user.getId().equals(integer)) {
                        userTypingName = user.getFullName();
                        isTypingTextView.setText(user.getFullName() + " is typing...");
                        isTypingTextView.setVisibility(View.VISIBLE);
                        break;
                    }
                }
            }
        }

        @Override
        public void processUserStopTyping(String s, final Integer integer) {
            userTypingName = "";
            isTypingTextView.setVisibility(View.GONE);
        }
    };

    private void initializeSupportedExt() {
        supportedFileExtSetOfStrings.add(".pdf");
        supportedFileExtSetOfStrings.add(".doc");
        supportedFileExtSetOfStrings.add(".docx");
        supportedFileExtSetOfStrings.add(".xls");
        supportedFileExtSetOfStrings.add(".xlsx");
        supportedFileExtSetOfStrings.add(".ppt");
        supportedFileExtSetOfStrings.add(".pptx");
        supportedFileExtSetOfStrings.add(".jpeg");
        supportedFileExtSetOfStrings.add(".png");
        supportedFileExtSetOfStrings.add(".jpg");
        supportedFileExtSetOfStrings.add(".mp4");
    }

    private void copyFileStream(File dest, Uri uri, Context context) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = context.getContentResolver().openInputStream(uri);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;

            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            is.close();
            os.close();
        }
    }

    public String createvCard(Intent data) {
        Uri contactData = data.getData();

        //get contact name
        Cursor c = getContentResolver().query(contactData, null, null, null, null);
        if (c.moveToFirst()) {
            contactName = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }

        //get contact number
        Cursor contactCursor = getContentResolver().query(contactData, new String[]{ContactsContract.Contacts._ID}, null, null, null);
        String id = null;
        if (contactCursor.moveToFirst()) {
            id = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts._ID));
        }
        contactCursor.close();
        contactNumber = null;
        Cursor phoneCursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "= ? ",
                new String[]{id}, null);
        if (phoneCursor.moveToFirst()) {
            contactNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }
        phoneCursor.close();

        File vcfFile = new File(this.getExternalFilesDir(null), contactName + ".vcf");
        FileWriter fw;
        try {
            fw = new FileWriter(vcfFile);
            fw.write("BEGIN:VCARD\r\n");
            fw.write("VERSION:3.0\r\n");
            fw.write("N:" + contactName + "\r\n");
            fw.write("TEL;TYPE=WORK,VOICE:" + contactNumber + "\r\n");
            fw.write("END:VCARD\r\n");
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        uploadAttachment(vcfFile, "contact", "");
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getPath(Context context, Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static String getDocumentFilePath(Context context, final Uri uri) {
        if (uri == null) {
            return null;
        }
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        documentFile = new File(data);

        return data;
    }

    private void onSelectFromGalleryResult(Intent data) {
        Uri selectedImageOrVideoUri = data.getData();

        if (selectedImageOrVideoUri.toString().contains("image")
                || selectedImageOrVideoUri.toString().contains(".png")
                || selectedImageOrVideoUri.toString().contains(".jpg")) {
            File file = new File("");
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageOrVideoUri);
                file = convertBitmapToFile(this, bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
//            uploadAttachment(file, "image");
            Intent intent = new Intent(ChattingActivity.this, CaptionActivity.class);
            intent.putExtra("file", file);
            startActivityForResult(intent, REQUEST_CAPTION);
        } else {
            String selectedVideoPath = getPath(selectedImageOrVideoUri);
            File file = new File(selectedVideoPath);
            file.getAbsoluteFile();
//            uploadAttachment(file, "video");
            Intent intent = new Intent(ChattingActivity.this, CaptionActivity.class);
            intent.putExtra("file", file);
            intent.putExtra("selectedVideoPath", selectedVideoPath);
            startActivityForResult(intent, REQUEST_CAPTION);
        }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    public File convertBitmapToFile(Context context, Bitmap imageBitmap) {
        File file = null;
        Calendar calendar = Calendar.getInstance();
        try {
            file = new File(context.getCacheDir(), calendar.getTimeInMillis() + ".jpg");

            //Convert bitmap to byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            byte[] bitmapdata = bos.toByteArray();

            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            Log.v("error", e.toString());
        }
        return file;
    }

    public void sendLocation(final String lat_lng, final String type) {
        final QBChatMessage chatMessage = new QBChatMessage();
        chatMessage.setProperty("save_to_history", "1");
        chatMessage.setDateSent(System.currentTimeMillis() / 1000);
        chatMessage.setMarkable(true);
        chatMessage.setSenderId(currentUser.getId());

        QBAttachment attachment = new QBAttachment(type);
        attachment.setId(lat_lng);
        attachment.setType(type);
        chatMessage.addAttachment(attachment);

        qbChatDialog.sendMessage(chatMessage, new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {

                //validation for group is not necessary because it will cause duplicate message.
                if (QBDialogType.PRIVATE.equals(qbChatDialog.getType())) {
                    refreshMessage(chatMessage);
                }
            }

            @Override
            public void onError(QBResponseException e) {
                Toast.makeText(ChattingActivity.this, "failed send message", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void uploadAttachment(final File file, final String type, final String caption) {

        Loader.showProgressDialog(this);
        QBContent.uploadFileTask(file, true, type, new QBProgressCallback() {

            @Override
            public void onProgressUpdate(int i) {

            }
        }).performAsync(new QBEntityCallback<QBFile>() {
            @Override
            public void onSuccess(QBFile qbFile, Bundle bundle) {
                // create a message
                final QBChatMessage qbChatMessage = new QBChatMessage();
                qbChatMessage.setSaveToHistory(true); // Save a message to history

                QBAttachment attachment = new QBAttachment(type);
                attachment.setId(qbFile.getId().toString());
//                attachment.setUrl(qbFile.getPublicUrl());
//                attachment.setName(qbFile.getName());
//                attachment.setType(type);
                qbChatMessage.addAttachment(attachment);
                qbChatMessage.setProperty("url", qbFile.getPrivateUrl());
                qbChatMessage.setProperty("name", qbFile.getName());
                qbChatMessage.setProperty("size", String.valueOf(qbFile.getSize()));
                qbChatMessage.setProperty("type", qbFile.getContentType());

                // send a message
                qbChatMessage.setProperty("save_to_history", "1");
                qbChatMessage.setDateSent(System.currentTimeMillis() / 1000);
                qbChatMessage.setMarkable(true);
                qbChatMessage.setSenderId(currentUser.getId());
                if (caption != null) {
                    qbChatMessage.setBody(caption);
                }

                qbChatDialog.sendMessage(qbChatMessage, new QBEntityCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid, Bundle bundle) {

                        //validation for group is not necessary because it will cause double message.
                        if (QBDialogType.PRIVATE.equals(qbChatDialog.getType())) {
                            refreshMessage(qbChatMessage);
                        }
                        Loader.dialogDissmiss(getApplicationContext());
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Loader.dialogDissmiss(getApplicationContext());
                        Toast.makeText(ChattingActivity.this, "failed send message", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onError(QBResponseException e) {
                String errorMessage = "";

                for (int i = 0; i < e.getErrors().size(); i++) {
                    errorMessage += e.getErrors().get(i);
                    errorMessage += ".";
                }

                Toast.makeText(ChattingActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initChat() {
        switch (qbChatDialog.getType()) {
            case GROUP:
            case PUBLIC_GROUP:
                joinGroupChat();
                break;
            case PRIVATE:
                loadDialogUsers();
                break;
            default:
                finish();
                break;
        }
    }

    private void joinGroupChat() {
        inflateDialogs();

//        DiscussionHistory discussionHistory = new DiscussionHistory();
//        discussionHistory.setMaxStanzas(0);
//
//        qbChatDialog.join(discussionHistory, new QBEntityCallback() {
//            @Override
//            public void onSuccess(Object o, Bundle bundle) {
//                inflateDialogs();
//            }
//
//            @Override
//            public void onError(QBResponseException e) {
//                Toast.makeText(ChattingActivity.this, "failed join chat", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private void loadDialogUsers() {
//        ChatHelper.getInstance().getUsersFromDialog(qbChatDialog, new QBEntityCallback<ArrayList<QBUser>>() {
//            @Override
//            public void onSuccess(ArrayList<QBUser> users, Bundle bundle) {
//                setChatNameToActionBar();
//                loadChatHistory();
//            }
//            @Override
//            public void onError(QBResponseException e) {
//                showErrorSnackbar(R.string.chat_load_users_error, e,
//                        new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                loadDialogUsers();
//                            }
//                        });
//            }
//        });
        loadChatHistory(0);
    }

    public void loadChatHistory(final int skip) {
        QBMessageGetBuilder messageGetBuilder = new QBMessageGetBuilder();
        messageGetBuilder.setLimit(10);
        messageGetBuilder.setSkip(skip);
        messageGetBuilder.sortDesc("date_sent");
        currentPage++;

        isLoadingNextPage = true;

        QBRestChatService.getDialogMessages(qbChatDialog, messageGetBuilder).performAsync(new QBEntityCallback<ArrayList<QBChatMessage>>() {
            @Override
            public void onSuccess(final ArrayList<QBChatMessage> qbChatMessages, Bundle bundle) {
                // Get attachment size
                int count = 0;
                for (QBChatMessage qBChatMessage : qbChatMessages) {
                    if (qBChatMessage.getAttachments().size() != 0) {
                        count++;
                    }
                }
                SettingsManager.getInstance().setCurrentAttachmentSize(count);

                for (QBChatMessage a : qbChatMessages) {
                    Log.v("chat-before", "chat: " + a.getBody());
                }

                if (sharedDatabase.getLastSkip() < skip) {
                    sharedDatabase.setLastMessagesCount(qbChatMessages.size());
                }

                sharedDatabase.setLastSkip(skip);

                Collections.reverse(qbChatMessages);

                for (QBChatMessage a : qbChatMessages) {
                    Log.v("chat-after", "chat: " + a.getBody());
                }

                for (int i = 0; i < qbChatMessages.size(); i++) {
                    QBChatMessage currentQbChatMessage = qbChatMessages.get(i);

                    if (qbChatMessages.get(i).getAttachments() != null) {
                        if (qbChatMessages.get(i).getAttachments().size() != 0) {
                            final int finalI = i;
                            if (!qbChatMessages.get(i).getAttachments().iterator().next().getId().contains("_")) {
                                QBContent.getFile(Integer.parseInt(qbChatMessages.get(i).getAttachments().iterator().next().getId())).performAsync(new QBEntityCallback<QBFile>() {
                                    @Override
                                    public void onSuccess(QBFile qbFile, Bundle bundle) {
                                        qbChatMessages.get(finalI).setProperty("url", qbFile.getPrivateUrl());
                                        qbChatMessages.get(finalI).setProperty("name", qbFile.getName());
                                        qbChatMessages.get(finalI).setProperty("size", String.valueOf(qbFile.getSize()));
                                        qbChatMessages.get(finalI).setProperty("type", qbFile.getContentType());
//                                        adapter.updateAdapter(skip, qbChatMessages);
//                                        recyclerView.getLayoutManager().scrollToPosition(adapter.getItemCount() - 1);
                                    }

                                    @Override
                                    public void onError(QBResponseException e) {

                                    }
                                });
                            }
                        } else {
                            if (qbChatMessages.get(i).getBody().contains("https://") || qbChatMessages.get(i).getBody().contains("http://")) {

                                String textAfterHttp = currentQbChatMessage.getBody().substring(currentQbChatMessage.getBody().indexOf("http"));

                                // Validate if the body has a link
                                if (textAfterHttp != null) {

                                    // Validate if the link is positioned at the very back of the sentence
                                    if (!textAfterHttp.contains(" ")) {
                                        linkUrl = textAfterHttp;
                                    } else {
                                        linkUrl = textAfterHttp.substring(0, textAfterHttp.indexOf(" "));
                                    }
                                }
                                checkValidURL(linkUrl, qbChatMessages, i, skip);
                            }
                        }
                    }
                }

                adapter.updateAdapter(skip, qbChatMessages);
                recyclerView.getLayoutManager().scrollToPosition(qbChatMessages.size() - 1);

                isLoadingNextPage = false;
            }

            @Override
            public void onError(QBResponseException e) {
                Toast.makeText(ChattingActivity.this, "failed load message", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class ChatMessageListener implements QBChatDialogMessageListener {
        @Override
        public void processMessage(final String s, final QBChatMessage qbChatMessage, Integer integer) {
            if (!isDuplicated(qbChatMessage.getId())) {
                if (qbChatMessage.getAttachments() != null) {
                    if (qbChatMessage.getAttachments().size() != 0) {
                        if (!qbChatMessage.getAttachments().iterator().next().getId().contains("_")) {
                            try {
                                QBFile qbFile = QBContent.getFile(Integer.parseInt(qbChatMessage.getAttachments().iterator().next().getId())).perform();
                                qbChatMessage.setProperty("url", qbFile.getPrivateUrl());
                                qbChatMessage.setProperty("name", qbFile.getName());
                                qbChatMessage.setProperty("size", String.valueOf(qbFile.getSize()));
                                qbChatMessage.setProperty("type", qbFile.getContentType());
                            } catch (QBResponseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    refreshMessage(qbChatMessage);
                }
            }
        }

        @Override
        public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {

        }
    }

    private boolean isDuplicated(String qbChatId) {
        if (previousMessageId.equalsIgnoreCase(qbChatId)) {
            previousMessageId = qbChatId;
            return true;
        }
        previousMessageId = qbChatId;
        return false;
    }

    public void refreshMessage(QBChatMessage message) {
        adapter.add(message);
        recyclerView.getLayoutManager().scrollToPosition(adapter.getItemCount() - 1);
    }

    public SelectAttachmentListener selectAttachmentListener = new SelectAttachmentListener() {
        @Override
        public void onSelectAttachmentListener(QBChatMessage qbChatMessage) {
            if (qbChatMessage.getAttachments() != null) {
                if (qbChatMessage.getAttachments().size() != 0) {
                    if (qbChatMessage.getProperties().get("type") == null) {
                        String locationId = qbChatMessage.getAttachments().iterator().next().getId();
                        String[] parts = locationId.split("_");
                        if (parts.length > 1) {
                            String lat = parts[0];
                            String lng = parts[1];

                            String label = "Direction";
                            String uriBegin = "geo:" + lat + "," + lng;
                            String query = lat + "," + lng + "(" + label + ")";
                            String encodedQuery = Uri.encode(query);
                            String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
                            Uri uri = Uri.parse(uriString);
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        }
                    } else if (qbChatMessage.getProperties().get("type").equals("application/pdf") ||
                            qbChatMessage.getProperties().get("type").equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") ||
                            qbChatMessage.getProperties().get("type").equals("application/msword") ||
                            qbChatMessage.getProperties().get("type").equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") ||
                            qbChatMessage.getProperties().get("type").equals("application/vnd.ms-excel") ||
                            qbChatMessage.getProperties().get("type").equals("application/vnd.openxmlformats-officedocument.presentationml.presentation") ||
                            qbChatMessage.getProperties().get("type").equals("application/vnd.ms-powerpoint")) {
                        DownloadManager downloadManager = (DownloadManager) AppController.getInstance().getSystemService(Context.DOWNLOAD_SERVICE);
                        AppController.getInstance().registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

                        Uri uri = Uri.parse(qbChatMessage.getProperties().get("url"));
                        DownloadManager.Request request = new DownloadManager.Request(uri);
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, qbChatMessage.getProperties().get("name"));
                        request.setVisibleInDownloadsUi(false);
                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                        downloadManager.enqueue(request);

//                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(qbChatMessage.getProperties().get("url")));
//                        startActivity(browserIntent);

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        if (qbChatMessage.getProperties().get("type").equals("application/pdf")) {
                            intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + qbChatMessage.getProperties().get("name"))), "application/pdf");
                        } else if (qbChatMessage.getProperties().get("type").equals("application/msword")) {
                            intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + qbChatMessage.getProperties().get("name"))), "application/msword");
                        } else if (qbChatMessage.getProperties().get("type").equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
                            intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + qbChatMessage.getProperties().get("name"))), "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
                        } else if (qbChatMessage.getProperties().get("type").equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
                            intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + qbChatMessage.getProperties().get("name"))), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                        } else if (qbChatMessage.getProperties().get("type").equals("application/vnd.ms-excel")) {
                            intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + qbChatMessage.getProperties().get("name"))), "application/vnd.ms-excel");
                        } else if (qbChatMessage.getProperties().get("type").equals("application/vnd.openxmlformats-officedocument.presentationml.presentation")) {
                            intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + qbChatMessage.getProperties().get("name"))), "application/vnd.openxmlformats-officedocument.presentationml.presentation");
                        } else if (qbChatMessage.getProperties().get("type").equals("application/vnd.ms-powerpoint")) {
                            intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + qbChatMessage.getProperties().get("name"))), "application/vnd.ms-powerpoint");
                        }
                        startActivity(intent);
                    } else if (qbChatMessage.getProperties().get("type").equals("image/png") || (qbChatMessage.getProperties().get("type").equals("image/jpeg")) || (qbChatMessage.getProperties().get("type").equals("video/mp4"))) {
                        Intent intent = new Intent(ChattingActivity.this, PreviewActivity.class);
                        intent.putExtra("url", qbChatMessage.getProperties().get("url"));
                        intent.putExtra("name", qbChatMessage.getProperties().get("name"));
                        intent.putExtra("type", qbChatMessage.getProperties().get("type"));
                        startActivity(intent);
                    } else if (qbChatMessage.getProperties().get("type").equals("text/x-vcard")) {
//                        DownloadManager downloadManager = (DownloadManager) AppController.getInstance().getSystemService(Context.DOWNLOAD_SERVICE);
//                        AppController.getInstance().registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
//
//                        Uri uri = Uri.parse(qbChatMessage.getProperties().get("url"));
//                        DownloadManager.Request request = new DownloadManager.Request(uri);
//                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, qbChatMessage.getProperties().get("name"));
//                        request.setVisibleInDownloadsUi(false);
//                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
//                        downloadManager.enqueue(request);

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + qbChatMessage.getProperties().get("name"))), "text/x-vcard"); //storage path is path of your vcf file and vFile is name of that file.
                        startActivity(intent);
                    }

                }
            }

        }
    };


    BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            String action = intent.getAction();
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(downloadId);
            }
        }
    };

    @Override
    protected void onDestroy() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        qbChatDialog = (QBChatDialog) getIntent().getSerializableExtra("qbChatDialog");
//        qbChatDialog = SettingsManager.getInstance().getQbChatDialog();
        submitButton.setClickable(false);
        String dialogId = getIntent().getStringExtra("qbChatDialogId");
        QBRestChatService.getChatDialogById(dialogId).performAsync(
                new QBEntityCallback<QBChatDialog>() {
                    @Override
                    public void onSuccess(QBChatDialog dialog, Bundle params) {
                        qbChatDialog = dialog;
                        qbChatDialog.addMessageListener(chatMessageListener);
                        if (qbChatDialog.getType() == QBDialogType.PRIVATE) {
                            for (int qbUserId : qbChatDialog.getOccupants()) {
                                if (qbUserId != QBChatService.getInstance().getUser().getId()) {
                                    QBUsers.getUser(qbUserId).performAsync(new QBEntityCallback<QBUser>() {

                                        @Override
                                        public void onSuccess(QBUser qbUser, Bundle bundle) {
                                            opposingUser = qbUser;
                                            inflateDialogs();
                                        }

                                        @Override
                                        public void onError(QBResponseException e) {

                                        }
                                    });
                                }
                            }
                        } else {
                            joinGroupChat();
                        }
                    }

                    @Override
                    public void onError(QBResponseException responseException) {

                    }
                });
    }

    private void inflateDialogs() {
        submitButton.setClickable(true);
        for (int i = 0; i < qbChatDialog.getOccupants().size(); i++) {
            QBUsers.getUser(qbChatDialog.getOccupants().get(i)).performAsync(new QBEntityCallback<QBUser>() {
                @Override
                public void onSuccess(QBUser qbUser, Bundle bundle) {
                    groupMembersList.add(qbUser);
                }

                @Override
                public void onError(QBResponseException e) {

                }
            });
        }

        mediaMetadataRetriever = new MediaMetadataRetriever();
        if (qbChatDialog.getType() == QBDialogType.PRIVATE) {
            recipientNameTitle.setText(qbChatDialog.getName());
            groupNameRelativeLayout.setVisibility(View.GONE);
            groupPictureFrameLayout.setVisibility(View.GONE);
            optionImageView.setVisibility(View.VISIBLE);
            String qbPhoto = opposingUser.getCustomData();
            profilePictureTextView.setText(InterfaceManager.sharedInstance().getInitial(opposingUser.getFullName()));
            profilePictureTextView.setAllCaps(true);
            ColorDrawable cd = new ColorDrawable(ContextCompat.getColor(ChattingActivity.this, R.color.loading_yellow));
            recipientPhoto.setImageDrawable(cd);
            profilePictureTextView.setVisibility(View.VISIBLE);
            String photoUrl = "";
            try {
                if (qbPhoto != null) {
                    if (!qbPhoto.equalsIgnoreCase("")) {
                        JSONObject photoJSONObject = new JSONObject(qbPhoto);
                        photoUrl = photoJSONObject.getString("avatar_url");
                        Glide.with(this).load(photoUrl)
                                .apply(RequestOptions.placeholderOf(cd))
                                .apply(RequestOptions.noAnimation())
                                .into(recipientPhoto);
                        profilePictureTextView.setVisibility(View.GONE);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ColorDrawable colorDrawable;
            if (opposingUser.getLastRequestAt() != null) {
                long currentTime = System.currentTimeMillis();
                long userLastRequestAtTime = opposingUser.getLastRequestAt().getTime();

                if ((currentTime - userLastRequestAtTime) < 5 * 60 * 1000) {
                    colorDrawable = new ColorDrawable(ContextCompat.getColor(ChattingActivity.this, R.color.skor_chat_online_indicator));
                    onlineIndicatorImageView.setImageDrawable(colorDrawable);
                }
            }
        } else {
            recipientPhoto.setVisibility(View.GONE);
            recipientNameTitle.setVisibility(View.GONE);
            onlineIndicatorImageView.setVisibility(View.GONE);
            voiceCallImageView.setVisibility(View.GONE);
            videoCallImageView.setVisibility(View.GONE);
            profilePictureTextView.setVisibility(View.GONE);
            optionImageView.setVisibility(View.GONE);
            groupNameRelativeLayout.setVisibility(View.VISIBLE);
            groupPictureFrameLayout.setVisibility(View.VISIBLE);
            groupNameTextView.setText(qbChatDialog.getName());
            int numberOfOnlineUsers = 0;
//            try {
//                if (qbChatDialog.getOnlineUsers() != null) {
//                    numberOfOnlineUsers = qbChatDialog.getOnlineUsers().size();
//                }
//            } catch (XMPPException e) {
//                e.printStackTrace();
//            }
            groupMembersTextView.setText(qbChatDialog.getOccupants().size() + " members," + numberOfOnlineUsers + " online");
            String qbPhoto = qbChatDialog.getPhoto();
            groupPictureTextView.setText(InterfaceManager.sharedInstance().getInitial(qbChatDialog.getName()));
            groupPictureTextView.setAllCaps(true);
            ColorDrawable cd = new ColorDrawable(ContextCompat.getColor(ChattingActivity.this, R.color.bluecolor));
            groupPictureImageView.setImageDrawable(cd);
            groupPictureTextView.setVisibility(View.VISIBLE);
            if (qbPhoto != null && !qbPhoto.equalsIgnoreCase("null")) {
                if (!qbPhoto.equalsIgnoreCase("")) {
                    Glide.with(this).load(qbPhoto)
                            .apply(RequestOptions.placeholderOf(cd))
                            .apply(RequestOptions.noAnimation())
                            .into(groupPictureImageView);
                    groupPictureTextView.setVisibility(View.GONE);
                }
            }
        }


        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        adapter = new MessageAdapter(ChattingActivity.this, selectAttachmentListener, opposingUser);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter.recipientInfo(qbChatDialog);

        attachmentsLinearLayout.setVisibility(View.GONE);

        QBRestChatService.markMessagesAsRead(qbChatDialog.getDialogId(), null).performAsync(new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {

            }

            @Override
            public void onError(QBResponseException e) {

            }
        });
        loadChatHistory(0);
    }

    public void checkValidURL(String linkForCheck, final ArrayList<QBChatMessage> qbChatMessages1, final int position, final int skip) {
//        Loader.showProgressDialog(this);
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("url", linkForCheck);
        RequestParams params = new RequestParams(paramMap);

        String authProvider = SettingsManager.getInstance().getAuthProvider();
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(800000);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("USER-AGENT", useragent);
        client.addHeader("Content-Type", "application/x-www-form-urlencoded");
        client.addHeader("Authorization", authProvider + " " + token);
        String url = Constants.BASEURL + "link_preview/api/preview/";
        client.post(url, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (statusCode == 400) {
                    if (getApplicationContext() != null) {
//                        connectivityMessage("Unable to log in with provided credentials.");
                        Toast.makeText(ChattingActivity.this, "Unable to log in with provided credentials.", Toast.LENGTH_SHORT).show();
                    }
                }

                Loader.dialogDissmiss(getApplicationContext());

                if (statusCode == 500) {
                    if (getApplicationContext() != null) {
//                        connectivityMessage( "We've encountered a technical error.our team is working on it. please try again later");
                        Toast.makeText(ChattingActivity.this, "We've encountered a technical error.our team is working on it. please try again later", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                Loader.dialogDissmiss(getApplicationContext());
                try {
                    JSONObject jsonObject = new JSONObject(responseString);
                    web_url = jsonObject.getString("url");
                    web_imageUrl = jsonObject.getString("image_url");
                    web_description = jsonObject.getString("description");
                    web_title = jsonObject.getString("title");

                    qbChatMessages1.get(position).setProperty("web_url", web_url);
                    qbChatMessages1.get(position).setProperty("web_title", web_title);
                    qbChatMessages1.get(position).setProperty("web_description", web_description);
                    qbChatMessages1.get(position).setProperty("web_imageUrl", web_imageUrl);

                    int index = position;
                    if (skip != sharedDatabase.getLastSkip()) {
                        if (sharedDatabase.getLastSkip() > skip) {
                            index += sharedDatabase.getLastMessagesCount();
                        }
                    }

                    adapter.updateAttachment(index, qbChatMessages1.get(position));
//                    adapter.updateAdapter(0, qbChatMessages1);
                    recyclerView.getLayoutManager().scrollToPosition(adapter.getItemCount() - 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBytes, Throwable throwable) {
                super.onFailure(statusCode, headers, responseBytes, throwable);
//                Loader.dialogDissmiss(getApplicationContext());
                if (statusCode == 401) {
                    UserManager.getInstance().logOut(ChattingActivity.this);
                }

                if (statusCode == 400) {
                    if (getApplicationContext() != null) {
                        Toast.makeText(ChattingActivity.this, "Unable to log in with provided credentials.", Toast.LENGTH_SHORT).show();
                    }
                }
                if (statusCode == 500) {
                    if (getApplicationContext() != null) {
                        Toast.makeText(ChattingActivity.this, "We've encountered a technical error.our team is working on it. please try again later", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public String getVersionCode() {
        String versionCode = null;
        try {
            PackageManager manager = getPackageManager();
            PackageInfo info = manager.getPackageInfo(getApplicationContext().getPackageName(), 0);
            versionCode = String.valueOf(info.versionCode);
//            versionName1 = String.valueOf(info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;

    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "jpeg" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName, ".jpeg", storageDir
        );

        return image;
    }

    public Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(ChattingActivity.this.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void askPermission(int requestCode) {
        ArrayList<String> requestPermissionArrayList = new ArrayList<>();
        String[] requestPermissionsString;
        switch (requestCode) {
            case REQUEST_IMAGE_VIDEO_CAPTURE:
                if (ActivityCompat.checkSelfPermission(ChattingActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissionArrayList.add(Manifest.permission.CAMERA);
                }
                if (ActivityCompat.checkSelfPermission(ChattingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissionArrayList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
                if (requestPermissionArrayList.size() > 0) {
                    requestPermissionsString = requestPermissionArrayList.toArray(new String[requestPermissionArrayList.size()]);
                    ActivityCompat.requestPermissions(ChattingActivity.this, requestPermissionsString, REQUEST_IMAGE_VIDEO_CAPTURE);
                } else {
                    executeIntent(REQUEST_IMAGE_VIDEO_CAPTURE);
                }
                break;
            case REQUEST_IMAGE_VIDEO_PICK:
                if (ActivityCompat.checkSelfPermission(ChattingActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissionArrayList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
                if (requestPermissionArrayList.size() > 0) {
                    requestPermissionsString = requestPermissionArrayList.toArray(new String[requestPermissionArrayList.size()]);
                    ActivityCompat.requestPermissions(ChattingActivity.this, requestPermissionsString, REQUEST_IMAGE_VIDEO_PICK);
                } else {
                    executeIntent(REQUEST_IMAGE_VIDEO_PICK);
                }
                break;
            case REQUEST_VIDEO_CAPTURE:
                if (ActivityCompat.checkSelfPermission(ChattingActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissionArrayList.add(Manifest.permission.CAMERA);
                }
                if (ActivityCompat.checkSelfPermission(ChattingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissionArrayList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
                if (requestPermissionArrayList.size() > 0) {
                    requestPermissionsString = requestPermissionArrayList.toArray(new String[requestPermissionArrayList.size()]);
                    ActivityCompat.requestPermissions(ChattingActivity.this, requestPermissionsString, REQUEST_VIDEO_CAPTURE);
                } else {
                    executeIntent(REQUEST_VIDEO_CAPTURE);
                }
                break;
            case REQUEST_CONTACT_PICK:
                if (ActivityCompat.checkSelfPermission(ChattingActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissionArrayList.add(Manifest.permission.READ_CONTACTS);
                }
                if (requestPermissionArrayList.size() > 0) {
                    requestPermissionsString = requestPermissionArrayList.toArray(new String[requestPermissionArrayList.size()]);
                    ActivityCompat.requestPermissions(ChattingActivity.this, requestPermissionsString, REQUEST_CONTACT_PICK);
                } else {
                    executeIntent(REQUEST_CONTACT_PICK);
                }
                break;
            case REQUEST_DOCUMENT_PICK:
                if (ActivityCompat.checkSelfPermission(ChattingActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissionArrayList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
                if (requestPermissionArrayList.size() > 0) {
                    requestPermissionsString = requestPermissionArrayList.toArray(new String[requestPermissionArrayList.size()]);
                    ActivityCompat.requestPermissions(ChattingActivity.this, requestPermissionsString, REQUEST_DOCUMENT_PICK);
                } else {
                    executeIntent(REQUEST_DOCUMENT_PICK);
                }
                break;
            case REQUEST_LOCATION_PICK:
                if (ActivityCompat.checkSelfPermission(ChattingActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissionArrayList.add(Manifest.permission.ACCESS_FINE_LOCATION);
                }
                if (requestPermissionArrayList.size() > 0) {
                    requestPermissionsString = requestPermissionArrayList.toArray(new String[requestPermissionArrayList.size()]);
                    ActivityCompat.requestPermissions(ChattingActivity.this, requestPermissionsString, REQUEST_LOCATION_PICK);
                } else {
                    executeIntent(REQUEST_LOCATION_PICK);
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int flag = 0;
        boolean isEligibleToExecuteIntent = false;
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                flag++;
            }
        }
        if (flag == grantResults.length) {
            isEligibleToExecuteIntent = true;
        }

        if (isEligibleToExecuteIntent) {
            executeIntent(requestCode);
        }
    }

    private void executeIntent(int requestCode) {
        switch (requestCode) {
            case REQUEST_IMAGE_VIDEO_CAPTURE:
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                try {
                    file = createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));

                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_VIDEO_CAPTURE);
                }
                break;
            case REQUEST_IMAGE_VIDEO_PICK:
                Intent pickImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickImageIntent.setType("*/*");
                pickImageIntent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "video/*"});
                startActivityForResult(pickImageIntent, REQUEST_IMAGE_VIDEO_PICK);
                break;
            case REQUEST_VIDEO_CAPTURE:
                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
                }
                break;
            case REQUEST_CONTACT_PICK:
                Intent pickContactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(pickContactIntent, REQUEST_CONTACT_PICK);
                break;
            case REQUEST_DOCUMENT_PICK:
                Intent pickDocumentIntent = new Intent(Intent.ACTION_GET_CONTENT);

//                pickDocumentIntent.addCategory(Intent.CATEGORY_OPENABLE);

                //pdf, doc, docx, ppt, pptx, xls, xlsx
                String[] mimetypesDocument = {"application/msword", "application/pdf",
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                        "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                        "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"};
                pickDocumentIntent.setType("*/*");
                pickDocumentIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypesDocument);
                startActivityForResult(pickDocumentIntent, REQUEST_DOCUMENT_PICK);
                break;
            case REQUEST_LOCATION_PICK:
//                Intent intent = new Intent(ChattingActivity.this, ShareLocationActivity.class);
//                intent.putExtra("lat_lng", lat_lng);
//                startActivity(intent);
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(this), REQUEST_LOCATION_PICK);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}

