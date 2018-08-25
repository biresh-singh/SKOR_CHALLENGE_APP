package fragment.SkorChat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;
import com.root.skor.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import activity.skorchat.ChattingActivity;
import adaptor.CreateGroupParticipantsAdapter;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import singleton.SettingsManager;
import utils.ArialRegularEditText;
import utils.ArialRegularTextView;
import utils.DataHolder;


public class CreateGroupStep2Fragment extends Fragment {

    ArialRegularTextView editPictureTextView, participantsTextView;
    CircleImageView groupChatPictureImageView;
    ArialRegularEditText groupChatNameEditText;
    RecyclerView participantsRecyclerView;
    List<QBUser> groupMemberList = new ArrayList<>();
    String qbPhoto, mCurrentPhotoPath;
    int totalUsers;
    File photoFile = null;
    Uri selectedImageUri;
    CreateGroupParticipantsAdapter createGroupParticipantsAdapter;
    boolean isEligibleToSetGroupAvatar = false;

    private final int REQUEST_CAMERA_PERMISSION = 0;
    private final int REQUEST_GALLERY_PERMISSION = 1;

    public CreateGroupStep2Fragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void initialize() {
        groupMemberList = DataHolder.getInstance().getSelectedUsersForGroupList();
        totalUsers = DataHolder.getInstance().getQBUsers().size();
        createGroupParticipantsAdapter = new CreateGroupParticipantsAdapter(getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        participantsRecyclerView.setLayoutManager(linearLayoutManager);
        participantsRecyclerView.setAdapter(createGroupParticipantsAdapter);
        participantsTextView.setText("(" + groupMemberList.size() + " of " + totalUsers + ")");
    }

    public void createGroup() {
        if (groupChatNameEditText.getText() == null || groupChatNameEditText.getText().toString().equals("")) {
            Toast.makeText(getActivity(), "Please name your group", Toast.LENGTH_SHORT).show();
        } else {
            final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "", "Creating group, please wait...", true);
            ArrayList<Integer> occupantIdsList = new ArrayList<Integer>();
            for (QBUser qbUser : groupMemberList) {
                    occupantIdsList.add(qbUser.getId());
            }

            final QBChatDialog dialog = new QBChatDialog();
            dialog.setName(groupChatNameEditText.getText().toString());
            dialog.setType(QBDialogType.GROUP);
            dialog.setOccupantsIds(occupantIdsList);

            Compressor compressor = new Compressor(getActivity());

            if (photoFile != null) {
                try {
                    photoFile = compressor
                            .setMaxWidth(300)
                            .setMaxHeight(300)
                            .setQuality(75)
                            .compressToFile(photoFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Boolean fileIsPublic = true;
                QBContent.uploadFileTask(photoFile, fileIsPublic, null).performAsync(new QBEntityCallback<QBFile>() {
                    @Override
                    public void onSuccess(QBFile qbFile, Bundle bundle) {
                        dialog.setPhoto(qbFile.getPublicUrl().toString());

                        QBRestChatService.createChatDialog(dialog).performAsync(new QBEntityCallback<QBChatDialog>() {
                            @Override
                            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                                ArrayList<Integer> qbUserIdList = new ArrayList<Integer>();
                                for (int qbUserId : qbChatDialog.getOccupants()) {
                                    qbUserIdList.add(qbUserId);
                                }
                                progressDialog.hide();

                                Intent intent = new Intent(getActivity(), ChattingActivity.class);
//                                intent.putExtra("qbChatDialog", qbChatDialog);
                                SettingsManager.getInstance().setQbChatDialog(qbChatDialog);
                                intent.putExtra("qbUserIdList", qbUserIdList);
                                startActivity(intent);
                                getActivity().finish();
                                Toast.makeText(getActivity(), "Group has been created", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(QBResponseException e) {
                                Toast.makeText(getActivity(), "Failed to create group", Toast.LENGTH_SHORT).show();
                                progressDialog.hide();
                            }
                        });
                    }

                    @Override
                    public void onError(QBResponseException e) {

                    }
                });
            } else {
                QBRestChatService.createChatDialog(dialog).performAsync(new QBEntityCallback<QBChatDialog>() {
                    @Override
                    public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                        ArrayList<Integer> qbUserIdList = new ArrayList<Integer>();
                        for (int qbUserId : qbChatDialog.getOccupants()) {
                            qbUserIdList.add(qbUserId);
                        }
                        progressDialog.hide();

                        Intent intent = new Intent(getActivity(), ChattingActivity.class);
                        intent.putExtra("qbChatDialogId", qbChatDialog.getDialogId());
                        startActivity(intent);
                        getActivity().finish();
                        Toast.makeText(getActivity(), "Group has been created", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Toast.makeText(getActivity(), "Failed to create group", Toast.LENGTH_SHORT).show();
                        progressDialog.hide();
                    }
                });
            }
        }
    }


    private void initializeListener() {

        View.OnClickListener editPictureTapped = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                final CharSequence options[] = new CharSequence[]{
                        "Take a Picture",
                        "Choose from Gallery",
                };
                alertBuilder.setTitle("Complete Action Using");
                alertBuilder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getGroupPicture(which);
                    }
                });
                alertBuilder.show();
            }
        };
        editPictureTextView.setOnClickListener(editPictureTapped);
        groupChatPictureImageView.setOnClickListener(editPictureTapped);
    }

    @Override
    public void onResume() {
        super.onResume();
        createGroupParticipantsAdapter.updateAdapter(groupMemberList);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_group_step2, null);
        editPictureTextView = (ArialRegularTextView) view.findViewById(R.id.fragment_create_group_step2_editPictureTextView);
        participantsTextView = (ArialRegularTextView) view.findViewById(R.id.fragment_create_group_step2_participantsTextView);
        groupChatPictureImageView = (CircleImageView) view.findViewById(R.id.fragment_create_group_step2_groupChatPictureImageView);
        groupChatNameEditText = (ArialRegularEditText) view.findViewById(R.id.fragment_create_group_step2_groupChatNameEditText);
        participantsRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_create_group_step2_participantsRecyclerView);
        initialize();
        initializeListener();
        return view;
    }

    private void getGroupPicture(int which) {
        ArrayList<String> requestPermissionArrayList = new ArrayList<>();
        String[] requestPermissionsString;
        switch (which) {
            case 0:
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissionArrayList.add(Manifest.permission.CAMERA);
                }
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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
                if (resultCode == getActivity().RESULT_OK && data != null) {
                    if (requestCode == CAMERA_PHOTO) {
                        selectedImageUri = Uri.fromFile(photoFile);
                        Bitmap bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
                        try {
                            if (selectedImageUri != null) {
                                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);

                            } else {
                                bitmap = (Bitmap) data.getExtras().getParcelable("data");
                            }
                        } catch (IOException e) {
                            Log.d("error", " IOException");
                        }
                        selectedImageUri = getImageUri(bitmap);
                        Glide.with(getActivity()).load(selectedImageUri).into(groupChatPictureImageView);
                    } else if (requestCode == GALLERY_PHOTO) {
                        selectedImageUri = data.getData();

                        try {
                            if (selectedImageUri != null) {
                                bmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                                outStream = new FileOutputStream(photoFile);
                                bmp.compress(Bitmap.CompressFormat.JPEG, 75, outStream);
                            } else {
                                bmp = (Bitmap) data.getExtras().get("data");
                            }
                            selectedImageUri = getImageUri(bmp);
                            Glide.with(getActivity()).load(selectedImageUri).into(groupChatPictureImageView);
                        } catch (IOException e) {
                            Log.d("error", " IOException");
                        }
                    }
                } else if (resultCode == getActivity().RESULT_OK && selectedImageUri != null) {
                    if (requestCode == CAMERA_PHOTO) {
                        selectedImageUri = Uri.fromFile(photoFile);
                        Bitmap bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
                        try {
                            if (selectedImageUri != null) {
                                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);

                            } else {
                                bitmap = (Bitmap) data.getExtras().getParcelable("data");
                            }
                        } catch (IOException e) {
                            Log.d("error", " IOException");
                        }
                        selectedImageUri = getImageUri(bitmap);
                        Glide.with(getActivity()).load(selectedImageUri).into(groupChatPictureImageView);
                    } else if (requestCode == GALLERY_PHOTO) {
                        selectedImageUri = data.getData();
                        try {
                            if (selectedImageUri != null) {
                                bmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                                outStream = new FileOutputStream(photoFile);
                                bmp.compress(Bitmap.CompressFormat.JPEG, 75, outStream);
                            } else {
                                bmp = (Bitmap) data.getExtras().get("data");
                            }
                            selectedImageUri = getImageUri(bmp);
                            Glide.with(getActivity()).load(selectedImageUri).into(groupChatPictureImageView);
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
        String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


}
