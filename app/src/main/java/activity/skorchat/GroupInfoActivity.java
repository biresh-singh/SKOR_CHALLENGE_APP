package activity.skorchat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.request.QBDialogRequestBuilder;
import com.quickblox.chat.request.QBMessageGetBuilder;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.root.skor.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import adaptor.GroupParticipantAdapter;
import singleton.SettingsManager;
import utils.DataHolder;

/**
 * Created by dss-17 on 7/14/17.
 */

public class GroupInfoActivity extends AppCompatActivity {
    LinearLayout backButton;
    EditText groupNameEditText;
    TextView groupName, groupMembers,
            doneButton, groupPhotoTextView, participantSizeTextView, editPictureButton,
            addParticipantButton, leaveGroupButton, clearChatButton, attachmentTextView;
    de.hdodenhof.circleimageview.CircleImageView groupPhotoCircleImageView;
    RecyclerView currentParticipantRecyclerView;

    //adapter initialization
    GroupParticipantAdapter groupParticipantAdapter;
    LinearLayoutManager linearLayoutManager;

    QBChatDialog qbChatDialog = new QBChatDialog();
    QBUser qbUser = QBChatService.getInstance().getUser();
    List<QBUser> qbUsersList = new ArrayList<>();

    //for update group
    QBDialogRequestBuilder qbDialogRequestBuilder = new QBDialogRequestBuilder();

    File photoFile = null;
    Uri selectedImageUri;
    String mCurrentPhotoPath;
    private final int REQUEST_CAMERA_PERMISSION = 0;
    private final int REQUEST_GALLERY_PERMISSION = 1;
    boolean isEligibleToSetGroupAvatar = false;
    private static final String TAG = "GroupInfoActivity";
    List<Integer> occupants = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);

        backButton = (LinearLayout) findViewById(R.id.activity_group_info_backButton);
        groupName = (TextView) findViewById(R.id.activity_group_info_groupNameTextView);
        groupNameEditText = (EditText) findViewById(R.id.activity_group_info_groupNameEditText);
        groupMembers = (TextView) findViewById(R.id.activity_group_info_groupMembersTextView);
        doneButton = (TextView) findViewById(R.id.activity_group_info_doneButton);
        groupPhotoCircleImageView = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.activity_group_info_photoCircleImageView);
        groupPhotoTextView = (TextView) findViewById(R.id.activity_group_info_photoTextView);
        participantSizeTextView = (TextView) findViewById(R.id.activity_group_info_participantSize);
        editPictureButton = (TextView) findViewById(R.id.activity_group_info_editPhotoButton);
        addParticipantButton = (TextView) findViewById(R.id.activity_group_info_addParticipantButton);
        currentParticipantRecyclerView = (RecyclerView) findViewById(R.id.activity_group_info_currentParticipantRecyclerView);
        leaveGroupButton = (TextView) findViewById(R.id.activity_group_info_leaveGroupButton);
        clearChatButton = (TextView) findViewById(R.id.activity_group_info_clearChat);
        attachmentTextView = (TextView) findViewById(R.id.activity_group_info_attachment);

        //adapter initialization
        groupParticipantAdapter = new GroupParticipantAdapter(this);
        linearLayoutManager = new LinearLayoutManager(this);
        currentParticipantRecyclerView.setAdapter(groupParticipantAdapter);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        currentParticipantRecyclerView.setLayoutManager(linearLayoutManager);
        currentParticipantRecyclerView.setNestedScrollingEnabled(false);

//        qbChatDialog = (QBChatDialog) getIntent().getSerializableExtra("qbChatDialog");

        String groupNameString = getIntent().getStringExtra("groupName");
        String groupMemberCountString = getIntent().getStringExtra("groupMemberCount");
        String groupOnlineUserCountString = getIntent().getStringExtra("groupOnlineUserCount");
        String groupPhotoString = getIntent().getStringExtra("groupPhoto");
        groupName.setText(groupNameString);
        groupNameEditText.setText(groupNameString);
//        groupMembers.setText(groupMemberCountString + " members " + groupMemberCountString + " online");
//        groupMembers.setText(groupMemberCountString + " members");
        if (groupPhotoString != null) {
            Glide.with(GroupInfoActivity.this).load(groupPhotoString)
                    .apply(RequestOptions.placeholderOf(R.drawable.profile_circle))
                    .into(groupPhotoCircleImageView);
            groupPhotoTextView.setVisibility(View.GONE);
        } else {
            groupPhotoTextView.setVisibility(View.VISIBLE);
            groupPhotoTextView.setText(groupNameString.charAt(0));
        }

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = ProgressDialog.show(GroupInfoActivity.this, "", "Updating group, please wait...", true);
                if (photoFile != null) {
                    QBContent.uploadFileTask(photoFile, true, null).performAsync(new QBEntityCallback<QBFile>() {
                        @Override
                        public void onSuccess(QBFile qbFile, Bundle bundle) {
                            qbChatDialog.setPhoto(qbFile.getPublicUrl());
                            QBRestChatService.updateGroupChatDialog(qbChatDialog, qbDialogRequestBuilder).performAsync(new QBEntityCallback<QBChatDialog>() {
                                @Override
                                public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                                    Toast.makeText(GroupInfoActivity.this, "Success Update Group Profile", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                    finish();
                                }

                                @Override
                                public void onError(QBResponseException e) {
                                    Toast.makeText(GroupInfoActivity.this, "Failed Update Group Profile", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            });
                        }

                        @Override
                        public void onError(QBResponseException e) {
                            progressDialog.dismiss();
                        }
                    });
                } else {
                    qbChatDialog.setName(groupNameEditText.getText().toString());
                    QBRestChatService.updateGroupChatDialog(qbChatDialog, qbDialogRequestBuilder).performAsync(new QBEntityCallback<QBChatDialog>() {
                        @Override
                        public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                            Toast.makeText(GroupInfoActivity.this, "Success Update Group Profile", Toast.LENGTH_SHORT).show();
                            finish();
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onError(QBResponseException e) {
                            Toast.makeText(GroupInfoActivity.this, "Failed Update Group Profile", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        });


        addParticipantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get group creator
                int groupAdminId = qbChatDialog.getUserId();
                if (qbUser.getId().equals(groupAdminId)) {
                    Intent intent = new Intent(GroupInfoActivity.this, CreateGroupActivity.class);
                    intent.putExtra("isFromGroupInfoActivity", "isFromGroupInfoActivity");
//                    SettingsManager.getInstance().setQbChatDialog(qbChatDialog);
//                    intent.putExtra("qbChatDialog", qbChatDialog);
                    startActivity(intent);
                } else {
                    Toast.makeText(GroupInfoActivity.this, "You're not the admin of the group", Toast.LENGTH_SHORT).show();
                }
            }
        });

        leaveGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qbDialogRequestBuilder.removeUsers(qbUser.getId());
                QBRestChatService.updateGroupChatDialog(qbChatDialog, qbDialogRequestBuilder).performAsync(new QBEntityCallback<QBChatDialog>() {
                    @Override
                    public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                        Toast.makeText(GroupInfoActivity.this, "Success leave Group", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Toast.makeText(GroupInfoActivity.this, "Failed leave Group", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        clearChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                                Toast.makeText(GroupInfoActivity.this, "success clear chat", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                            @Override
                            public void onError(QBResponseException e) {
                                Toast.makeText(GroupInfoActivity.this, "failed clear chat", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Toast.makeText(GroupInfoActivity.this, "failed load chat", Toast.LENGTH_SHORT).show();
                    }

                });
            }
        });

        editPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(GroupInfoActivity.this);
                final CharSequence options[] = new CharSequence[]{
                        "Take a Picture",
                        "Choose from Gallery",
                };
                alertBuilder.setTitle("Complete Action Using");
                alertBuilder.setItems(options, new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getGroupPicture(which);
                    }
                });
                alertBuilder.show();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        attachmentTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupInfoActivity.this, AttachmentActivity.class);
                startActivity(intent);
            }
        });

        attachmentTextView.setText("Shared Media and Docs (" + SettingsManager.getInstance().getCurrentAttachmentSize() + ")");
    }

    @Override
    protected void onResume() {
        super.onResume();
        qbChatDialog = SettingsManager.getInstance().getQbChatDialog();
        occupants = qbChatDialog.getOccupants();
        groupMembers.setText(occupants.size() + " members");
        groupParticipantAdapter.setAdminId(qbChatDialog.getUserId());
        participantSizeTextView.setText("Participants (" + occupants.size() + ")");

        try {
            groupParticipantAdapter.clearAdapter();
            getParticipants();
        } catch (QBResponseException e) {
            e.printStackTrace();
        }
    }

    private void getParticipants() throws QBResponseException {
        for (int i = 0; i < occupants.size(); i++) {
            qbUsersList.add(QBUsers.getUser(occupants.get(i)).perform());
        }

        // Sorting by ascending
        Collections.sort(qbUsersList, new Comparator<QBUser>() {
            @Override
            public int compare(QBUser o1, QBUser o2) {
                return o1.getFullName().compareToIgnoreCase(o2.getFullName());
            }
        });

        groupParticipantAdapter.updateAdapter(qbUsersList);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getGroupPicture(int which) {
        ArrayList<String> requestPermissionArrayList = new ArrayList<>();
        String[] requestPermissionsString;
        switch (which) {
            case 0:
                if (ActivityCompat.checkSelfPermission(GroupInfoActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissionArrayList.add(Manifest.permission.CAMERA);
                }
                if (ActivityCompat.checkSelfPermission(GroupInfoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissionArrayList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
                if (requestPermissionArrayList.size() > 0) {
                    requestPermissionsString = requestPermissionArrayList.toArray(new String[requestPermissionArrayList.size()]);
                    requestPermissions(requestPermissionsString, REQUEST_CAMERA_PERMISSION);
                } else {
                    setGroupAvatar(REQUEST_CAMERA_PERMISSION);
                }
                break;
            case 1:
                if (ActivityCompat.checkSelfPermission(GroupInfoActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissionArrayList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
                if (requestPermissionArrayList.size() > 0) {
                    requestPermissionsString = requestPermissionArrayList.toArray(new String[requestPermissionArrayList.size()]);
                    requestPermissions(requestPermissionsString, REQUEST_GALLERY_PERMISSION);
                } else {
                    setGroupAvatar(REQUEST_GALLERY_PERMISSION);
                }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int flag = 0;
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                flag++;
            }
        }
        if (flag == grantResults.length) {
            isEligibleToSetGroupAvatar = true;
        } else {
            isEligibleToSetGroupAvatar = false;
        }
        if (isEligibleToSetGroupAvatar) {
            setGroupAvatar(requestCode);
        }
    }

    private void setGroupAvatar(int requestCode) {
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (photoFile != null) {
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                }
                startActivityForResult(cameraIntent, REQUEST_CAMERA_PERMISSION);
                break;
            case REQUEST_GALLERY_PERMISSION:
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (photoFile != null) {
                    galleryIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                }
                galleryIntent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*"});

                startActivityForResult(galleryIntent, REQUEST_GALLERY_PERMISSION);
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "jpeg" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName, ".jpeg", storageDir
        );

        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final int CAMERA_PHOTO = 0;
        final int GALLERY_PHOTO = 1;

        final android.os.Handler handler = new android.os.Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                OutputStream outStream = null;
                if (photoFile != null) {
                    selectedImageUri = Uri.fromFile(photoFile);
                }
                Bitmap bmp = null;
                if (resultCode == GroupInfoActivity.RESULT_OK && data != null) {
                    if (requestCode == CAMERA_PHOTO) {
                        selectedImageUri = Uri.fromFile(photoFile);
                        Bitmap bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
                        try {
                            if (selectedImageUri != null) {
                                bitmap = MediaStore.Images.Media.getBitmap(GroupInfoActivity.this.getContentResolver(), selectedImageUri);

                            } else {
                                bitmap = (Bitmap) data.getExtras().getParcelable("data");
                            }
                        } catch (IOException e) {
                            Log.d("error", " IOException");
                        }
                        selectedImageUri = getImageUri(bitmap);
                        Glide.with(GroupInfoActivity.this).load(selectedImageUri).into(groupPhotoCircleImageView);
                    } else if (requestCode == GALLERY_PHOTO) {
                        selectedImageUri = data.getData();

                        try {
                            if (selectedImageUri != null) {
                                bmp = MediaStore.Images.Media.getBitmap(GroupInfoActivity.this.getContentResolver(), selectedImageUri);
                                outStream = new FileOutputStream(photoFile);
                                bmp.compress(Bitmap.CompressFormat.JPEG, 75, outStream);
                            } else {
                                bmp = (Bitmap) data.getExtras().get("data");
                            }
                            selectedImageUri = getImageUri(bmp);
                            Glide.with(GroupInfoActivity.this).load(selectedImageUri).into(groupPhotoCircleImageView);
                        } catch (IOException e) {
                            Log.d("error", " IOException");
                        }
                    }
                } else if (resultCode == GroupInfoActivity.RESULT_OK && selectedImageUri != null) {
                    if (requestCode == CAMERA_PHOTO) {
                        selectedImageUri = Uri.fromFile(photoFile);
                        Bitmap bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
                        try {
                            if (selectedImageUri != null) {
                                bitmap = MediaStore.Images.Media.getBitmap(GroupInfoActivity.this.getContentResolver(), selectedImageUri);

                            } else {
                                bitmap = (Bitmap) data.getExtras().getParcelable("data");
                            }
                        } catch (IOException e) {
                            Log.d("error", " IOException");
                        }
                        selectedImageUri = getImageUri(bitmap);
                        Glide.with(GroupInfoActivity.this).load(selectedImageUri).into(groupPhotoCircleImageView);
                    } else if (requestCode == GALLERY_PHOTO) {
                        selectedImageUri = data.getData();
                        try {
                            if (selectedImageUri != null) {
                                bmp = MediaStore.Images.Media.getBitmap(GroupInfoActivity.this.getContentResolver(), selectedImageUri);
                                outStream = new FileOutputStream(photoFile);
                                bmp.compress(Bitmap.CompressFormat.JPEG, 75, outStream);
                            } else {
                                bmp = (Bitmap) data.getExtras().get("data");
                            }
                            selectedImageUri = getImageUri(bmp);
                            Glide.with(GroupInfoActivity.this).load(selectedImageUri).into(groupPhotoCircleImageView);
                        } catch (IOException e) {
                            Log.d("error", " IOException");
                        }
                    }
                }


            }
        }, 1000);
    }

    public Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(GroupInfoActivity.this.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


}
