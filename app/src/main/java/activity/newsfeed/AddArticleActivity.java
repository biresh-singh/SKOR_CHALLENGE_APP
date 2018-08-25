package activity.newsfeed;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.root.skor.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import constants.Constants;
import cz.msebera.android.httpclient.Header;
import database.SharedDatabase;
import event.RefreshFeedEvent;
import event.RefreshTokenEvent;
import id.zelory.compressor.Compressor;
import singleton.ServerManager;
import singleton.SettingsManager;
import singleton.UserManager;
import utils.AndroidDeviceNames;
import utils.Loader;

/**
 * Created by dss-17 on 9/11/17.
 */

public class AddArticleActivity extends AppCompatActivity {
    LinearLayout backButton, photosLinearLayout, videosLinearLayout, documentsLinearLayout, chooseAttachmentLinearLayout, previewAttachmentLinearLayout;
    ImageView previewAttachment1ImageView, previewAttachment2ImageView, previewAttachment3ImageView, erasePreview1ImageView, erasePreview2ImageView, erasePreview3ImageView;
    FrameLayout previewAttachment1FrameLayout, previewAttachment2FrameLayout, previewAttachment3FrameLayout;
    Button postButton;
    EditText titleEditText, descriptionEditText;

    int REQUEST_IMAGE_CAPTURE = 100;
    int REQUEST_IMAGE_PICKER = 200;
    int REQUEST_VIDEO_PICKER = 300;
    int REQUEST_DOCUMENT_PICKER = 400;

    int pictureCount = 0;

    ArrayList<String> currentPhotoPathArrayList = new ArrayList<>();
    ArrayList<File> files = new ArrayList<>();
    File videoFile, documentFile;
    boolean isImage, isVideo, isDocument = false;
    String filename;
    Bitmap bitmapVideo;

    //for api call
    //this is for api call
    AndroidDeviceNames deviceNames;
    String versionName = "";
    public static String useragent = null;
    SharedDatabase sharedDatabase;
    String token;

    File file1 = null;
    Uri photoUri;
    String selectedImagePath;
    String title = "";
    String description = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_article);

        //this is for api call
        deviceNames = new AndroidDeviceNames(AddArticleActivity.this);
        useragent = "Skor/3 Android|" + deviceNames.getDeviceName() + "|" + deviceNames.getAPIVerison() + "|" + getVersionCode();

        sharedDatabase = new SharedDatabase(AddArticleActivity.this);
        token = sharedDatabase.getToken();

        backButton = (LinearLayout) findViewById(R.id.activity_add_article_backLinearLayout);
        postButton = (Button) findViewById(R.id.activity_add_article_postButton);
        titleEditText = (EditText) findViewById(R.id.activity_add_article_titleEditText);
        descriptionEditText = (EditText) findViewById(R.id.activity_add_article_descriptionEditText);

        //choose attachment
        chooseAttachmentLinearLayout = (LinearLayout) findViewById(R.id.activity_add_article_chooseAttachmentLinearLayout);
        photosLinearLayout = (LinearLayout) findViewById(R.id.activity_add_article_photosLinearLayout);
        videosLinearLayout = (LinearLayout) findViewById(R.id.activity_add_article_videosLinearLayout);
        documentsLinearLayout = (LinearLayout) findViewById(R.id.activity_add_article_documentsLinearLayout);

        //image preview attachment
        previewAttachmentLinearLayout = (LinearLayout) findViewById(R.id.activity_add_article_previewAttachmentLinearLayout);
        previewAttachment1ImageView = (ImageView) findViewById(R.id.activity_add_article_previewAttachment1ImageView);
        previewAttachment2ImageView = (ImageView) findViewById(R.id.activity_add_article_previewAttachment2ImageView);
        previewAttachment3ImageView = (ImageView) findViewById(R.id.activity_add_article_previewAttachment3ImageView);
        previewAttachment1FrameLayout = (FrameLayout) findViewById(R.id.activity_add_article_previewAttachment1FrameLayout);
        previewAttachment2FrameLayout = (FrameLayout) findViewById(R.id.activity_add_article_previewAttachment2FrameLayout);
        previewAttachment3FrameLayout = (FrameLayout) findViewById(R.id.activity_add_article_previewAttachment3FrameLayout);

        //erase preview button
        erasePreview1ImageView = (ImageView) findViewById(R.id.activity_add_article_previewAttachment1eraseImageView);
        erasePreview2ImageView = (ImageView) findViewById(R.id.activity_add_article_previewAttachment2eraseImageView);
        erasePreview3ImageView = (ImageView) findViewById(R.id.activity_add_article_previewAttachment3eraseImageView);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Loader.showProgressDialog(AddArticleActivity.this);
                title = titleEditText.getText().toString();
                description = descriptionEditText.getText().toString();

                if (title.trim().equals("") && description.trim().equals("")) {
                    Toast.makeText(AddArticleActivity.this, "Please fill in the title and description!", Toast.LENGTH_SHORT).show();
                    Loader.dialogDissmiss(AddArticleActivity.this);
                } else {
                    if (!isImage && !isVideo && !isDocument) {
                        addArticle(title, description, null, null, null, null, null);
                    } else if (isImage) {
                        if (files.size() == 0) {
                            addArticle(title, description, null, null, null, null, null);
                        } else if (files.size() == 1) {
                            addArticle(title, description, files.get(0), null, null, null, null);
                        } else if (files.size() == 2) {
                            addArticle(title, description, files.get(0), files.get(1), null, null, null);
                        } else if (files.size() == 3) {
                            addArticle(title, description, files.get(0), files.get(1), files.get(2), null, null);
                        }
                    } else if (isVideo) {
                        if (videoFile.length() / (Math.pow(1024, 2)) < 25) {
                            addArticle(title, description, null, null, null, videoFile, null);
                        } else {
                            Loader.dialogDissmiss(AddArticleActivity.this);
                            Toast.makeText(AddArticleActivity.this, "File size is too large, please upload file with size below 25mb", Toast.LENGTH_SHORT).show();
                        }
                    } else if (isDocument) {
                        addArticle(title, description, null, null, null, null, documentFile);
                    }
                }
            }
        });

        photosLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission("photo");
            }
        });

        previewAttachment2ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission("photo");
            }
        });

        previewAttachment3ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission("photo");
            }
        });

        videosLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission("video");
            }
        });

        documentsLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission("document");
            }
        });

        erasePreview1ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isVideo) {
                    bitmapVideo = null;
                    chooseAttachmentLinearLayout.setVisibility(View.VISIBLE);
                    previewAttachmentLinearLayout.setVisibility(View.GONE);
                } else if (isDocument) {
                    documentFile = null;

                    chooseAttachmentLinearLayout.setVisibility(View.VISIBLE);
                    previewAttachmentLinearLayout.setVisibility(View.GONE);
                } else {
                    //isImage
                    if (files.size() == 1) {
                        //removing file
                        files = new ArrayList<File>();

                        pictureCount = 0;

                        chooseAttachmentLinearLayout.setVisibility(View.VISIBLE);
                        previewAttachmentLinearLayout.setVisibility(View.GONE);
                    } else if (files.size() == 2) {
                        //removing file
                        files.remove(0);

                        pictureCount = 1;

                        previewAttachment2ImageView.setImageBitmap(null);
                        previewAttachment2ImageView.setClickable(true);

                        Glide.with(AddArticleActivity.this).load(files.get(0)).into(previewAttachment1ImageView);

                        previewAttachment3FrameLayout.setVisibility(View.INVISIBLE);
                    } else{
                        //files size = 3

                        //removing file
                        files.remove(0);

                        pictureCount = 2;

                        previewAttachment3ImageView.setImageBitmap(null);
                        previewAttachment3ImageView.setClickable(true);

                        Glide.with(AddArticleActivity.this).load(files.get(0)).into(previewAttachment1ImageView);
                        Glide.with(AddArticleActivity.this).load(files.get(1)).into(previewAttachment2ImageView);

                    }
                }
            }
        });

        erasePreview2ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (files.size() == 2) {
                    //removing file
                    files.remove(1);

                    pictureCount = 1;

                    previewAttachment2ImageView.setImageBitmap(null);
                    previewAttachment2ImageView.setClickable(true);

                    previewAttachment3FrameLayout.setVisibility(View.INVISIBLE);
                } else{
                    //files size = 3

                    //removing file
                    files.remove(1);

                    pictureCount = 2;

                    previewAttachment3ImageView.setImageBitmap(null);
                    previewAttachment3ImageView.setClickable(true);

                    Glide.with(AddArticleActivity.this).load(files.get(1)).into(previewAttachment2ImageView);

                }
            }
        });

        erasePreview3ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //removing file
                files.remove(2);

                pictureCount = 2;

                previewAttachment3ImageView.setImageBitmap(null);
                previewAttachment3ImageView.setClickable(true);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE) {
            chooseAttachmentLinearLayout.setVisibility(View.GONE);
            previewAttachmentLinearLayout.setVisibility(View.VISIBLE);

            isImage = true;
            pictureCount += 1;

            Compressor compressor = new Compressor(AddArticleActivity.this);
            try {
                file1 = compressor
                        .setMaxWidth(300)
                        .setMaxHeight(300)
                        .setQuality(75)
                        .compressToFile(file1);
                files.add(file1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (pictureCount == 1) {
                previewAttachment1FrameLayout.setVisibility(View.VISIBLE);
                previewAttachment1ImageView.setClickable(false);
                Glide.with(this).load(files.get(0)).into(previewAttachment1ImageView);

                previewAttachment2FrameLayout.setVisibility(View.VISIBLE);

                erasePreview2ImageView.setClickable(false);

            } else if (pictureCount == 2) {
                previewAttachment1FrameLayout.setVisibility(View.VISIBLE);
                previewAttachment1ImageView.setClickable(false);

                previewAttachment2FrameLayout.setVisibility(View.VISIBLE);
                previewAttachment2ImageView.setClickable(false);
                Glide.with(this).load(files.get(1)).into(previewAttachment2ImageView);

                previewAttachment3FrameLayout.setVisibility(View.VISIBLE);

                erasePreview2ImageView.setClickable(true);
                erasePreview3ImageView.setClickable(false);

            } else {
                //pictureCount == 3

                previewAttachment1FrameLayout.setVisibility(View.VISIBLE);
                previewAttachment1ImageView.setClickable(false);

                previewAttachment2FrameLayout.setVisibility(View.VISIBLE);
                previewAttachment2ImageView.setClickable(false);

                previewAttachment3FrameLayout.setVisibility(View.VISIBLE);
                previewAttachment3ImageView.setClickable(false);
                Glide.with(this).load(files.get(2)).into(previewAttachment3ImageView);
                erasePreview2ImageView.setClickable(true);
                erasePreview3ImageView.setClickable(true);

            }
        }

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_IMAGE_PICKER) {
            chooseAttachmentLinearLayout.setVisibility(View.GONE);
            previewAttachmentLinearLayout.setVisibility(View.VISIBLE);

            isImage = true;
            pictureCount += 1;

            Uri selectedImageOrVideoUri = data.getData();

            File file = new File("");
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageOrVideoUri);
                file = convertBitmapToFile(this, bitmap);
                files.add(file);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (pictureCount == 1) {
                previewAttachment1FrameLayout.setVisibility(View.VISIBLE);
                previewAttachment1ImageView.setClickable(false);
                Glide.with(this).load(files.get(0)).into(previewAttachment1ImageView);

                previewAttachment2FrameLayout.setVisibility(View.VISIBLE);

                erasePreview2ImageView.setClickable(false);

            } else if (pictureCount == 2) {
                previewAttachment1FrameLayout.setVisibility(View.VISIBLE);
                previewAttachment1ImageView.setClickable(false);

                previewAttachment2FrameLayout.setVisibility(View.VISIBLE);
                previewAttachment2ImageView.setClickable(false);
                Glide.with(this).load(files.get(1)).into(previewAttachment2ImageView);

                previewAttachment3FrameLayout.setVisibility(View.VISIBLE);

                erasePreview2ImageView.setClickable(true);
                erasePreview3ImageView.setClickable(false);

            } else {
                //pictureCount == 3

                previewAttachment1FrameLayout.setVisibility(View.VISIBLE);
                previewAttachment1ImageView.setClickable(false);

                previewAttachment2FrameLayout.setVisibility(View.VISIBLE);
                previewAttachment2ImageView.setClickable(false);

                previewAttachment3FrameLayout.setVisibility(View.VISIBLE);
                previewAttachment3ImageView.setClickable(false);
                Glide.with(this).load(files.get(2)).into(previewAttachment3ImageView);
                erasePreview2ImageView.setClickable(true);
                erasePreview3ImageView.setClickable(true);

            }
        }


        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_VIDEO_PICKER) {
            chooseAttachmentLinearLayout.setVisibility(View.GONE);
            previewAttachmentLinearLayout.setVisibility(View.VISIBLE);

            isVideo = true;

            Uri capturedVideo = data.getData();
            String selectedVideoPath = getPath(capturedVideo);

            //place the thumbnail
            previewAttachment1FrameLayout.setVisibility(View.VISIBLE);
            previewAttachment1ImageView.setClickable(false);
            previewAttachment1ImageView.setImageBitmap(bitmapVideo);

            if (selectedVideoPath == null) {
                selectedVideoPath = capturedVideo.getPath();
            }

            videoFile = new File(selectedVideoPath);
            videoFile.getAbsoluteFile();

            if (videoFile.length() / (Math.pow(1024, 2)) < 20) {

            } else {
                Toast.makeText(this, "File size is too large, please upload file with size below 20mb", Toast.LENGTH_SHORT).show();
                chooseAttachmentLinearLayout.setVisibility(View.VISIBLE);
                previewAttachmentLinearLayout.setVisibility(View.GONE);
            }

        }

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_DOCUMENT_PICKER) {
            chooseAttachmentLinearLayout.setVisibility(View.GONE);
            previewAttachmentLinearLayout.setVisibility(View.VISIBLE);
            previewAttachment1FrameLayout.setVisibility(View.VISIBLE);

            isDocument = true;

            Uri selectedDocumentUri = data.getData();
            String mimeType = getContentResolver().getType(selectedDocumentUri);
            if (mimeType == null) {
                String path = getPath(selectedDocumentUri);
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
            documentFile = new File(getExternalFilesDir(null), "generated.doc");
            String sourcePath = getExternalFilesDir(null).toString();
            try {
                copyFileStream(new File(sourcePath + "/" + filename), selectedDocumentUri, this);
            } catch (Exception e) {
                e.printStackTrace();
            }

            documentFile = new File(sourcePath + "/" + filename);

            if (mimeType.equals("application/pdf")) {
                previewAttachment1ImageView.setBackground(getDrawable(R.drawable.pdf));
            }
            if (mimeType.equals("application/msword")) {
                previewAttachment1ImageView.setBackground(getDrawable(R.drawable.docs));
            }
            if (mimeType.equals("application/mswordapplication/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
                previewAttachment1ImageView.setBackground(getDrawable(R.drawable.docs));
            }
            if (mimeType.equals("application/vnd.ms-powerpoint")) {
                previewAttachment1ImageView.setBackground(getDrawable(R.drawable.slide));
            }
            if (mimeType.equals("application/vnd.openxmlformats-officedocument.presentationml.presentation")) {
                previewAttachment1ImageView.setBackground(getDrawable(R.drawable.slide));
            }
            if (mimeType.equals("application/vnd.ms-excel")) {
                previewAttachment1ImageView.setBackground(getDrawable(R.drawable.sheets));
            }
            if (mimeType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
                previewAttachment1ImageView.setBackground(getDrawable(R.drawable.sheets));
            }

            if (documentFile.length() / (Math.pow(1024, 2)) < 5) {

            } else {
                Toast.makeText(this, "File size is too large, please upload file with size below 5mb", Toast.LENGTH_SHORT).show();
                chooseAttachmentLinearLayout.setVisibility(View.VISIBLE);
                previewAttachmentLinearLayout.setVisibility(View.GONE);
                documentFile = null;
            }
        }
    }

    public void addArticle(String title, String description, File image1, File image2, File image3, File video, File document) {

        RequestParams requestParams = new RequestParams();
        requestParams.put("title", title);
        requestParams.put("description", description);

        try {
            requestParams.put("img", image1);
            requestParams.put("img2", image2);
            requestParams.put("img3", image3);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            requestParams.put("video", video);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            requestParams.put("file", documentFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String authProvider = SettingsManager.getInstance().getAuthProvider();
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(1000 * 120);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("USER-AGENT", useragent);
        client.post(Constants.BASEURL + "feeds/api/list/", requestParams,  new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject jsonObject) {
                super.onSuccess(statusCode, headers, jsonObject);
                if (getApplicationContext() != null) {
                    if (SettingsManager.getInstance().IsCanCreateFeed().equals("Pending")) {
                        Toast.makeText(AddArticleActivity.this, "your news will be reviewed by admin", Toast.LENGTH_SHORT).show();
                    }

                    Loader.dialogDissmiss(AddArticleActivity.this);
                    Toast.makeText(AddArticleActivity.this, "success", Toast.LENGTH_SHORT).show();
                    EventBus.getDefault().post(new RefreshFeedEvent());
                    finish();
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Loader.dialogDissmiss(AddArticleActivity.this);
                if (statusCode == 401) {
                    UserManager.getInstance().logOut(AddArticleActivity.this);
                }

                if (getApplicationContext() != null) {
                    connectivityMessage("We've encountered a technical error.our team is working on it. please try again later");
                }
            }
        });
    }

    public void addArticleSync(String title, String description, File image1, File image2, File image3, File video, File document) {

        RequestParams requestParams = new RequestParams();
        requestParams.put("title", title);
        requestParams.put("description", description);

        try {
            requestParams.put("img", image1);
            requestParams.put("img2", image2);
            requestParams.put("img3", image3);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            requestParams.put("video", video);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            requestParams.put("file", documentFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String authProvider = SettingsManager.getInstance().getAuthProvider();

        SyncHttpClient client = new SyncHttpClient();
        client.setTimeout(1000 * 120);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("USER-AGENT", useragent);
        client.post(Constants.BASEURL + "feeds/api/list/", requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (getApplicationContext() != null) {
                    Loader.dialogDissmiss(AddArticleActivity.this);
                    Toast.makeText(AddArticleActivity.this, "success", Toast.LENGTH_SHORT).show();
                    EventBus.getDefault().post(new RefreshFeedEvent());
                    finish();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Loader.dialogDissmiss(AddArticleActivity.this);
                if (getApplicationContext() != null) {
                    connectivityMessage("We've encountered a technical error.our team is working on it. please try again later");
                }
            }
        });
    }

    public void connectivityMessage(String msg) {
        if (msg.equals("Network Connecting....")) {
            if (AddArticleActivity.this != null) {
                SnackbarManager.show(Snackbar.with(AddArticleActivity.this).text(msg).textColor(Color.WHITE).color(Color.parseColor("#4BCC1F")));
            }
        } else if (msg.equals("Network Connected")) {
            if (AddArticleActivity.this != null) {
                SnackbarManager.show(Snackbar.with(AddArticleActivity.this).text(msg).textColor(Color.WHITE).color(Color.parseColor("#4BCC1F")));
            }
        } else {
            if (AddArticleActivity.this != null) {
                SnackbarManager.show(Snackbar.with(AddArticleActivity.this).text(msg).textColor(Color.WHITE).color(Color.RED));
            }
        }
    }

    public String getVersionCode() {
        String versionCode = null;
        try {
            PackageManager manager = getApplicationContext().getPackageManager();
            PackageInfo info = manager.getPackageInfo(getApplicationContext().getPackageName(), 0);
            versionCode = String.valueOf(info.versionCode);
            versionName = String.valueOf(info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
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


    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            String videoPath = cursor.getString(column_index);

            //save video thumbnail
            bitmapVideo = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Video.Thumbnails.MICRO_KIND);
            return cursor.getString(column_index);
        } else
            return null;
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

    private void startDialogPicture() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
        myAlertDialog.setTitle("Open with");
//        myAlertDialog.setMessage("How do you want to set your picture?");

        myAlertDialog.setPositiveButton("Gallery",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent pictureActionIntent = null;

                        pictureActionIntent = new Intent(
                                Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(
                                pictureActionIntent, REQUEST_IMAGE_PICKER);

                    }
                });

        myAlertDialog.setNegativeButton("Camera",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                        try {
                            file1 = createImageFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file1));

                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

                    }
                });
        myAlertDialog.show();
    }

    private void startDialogVideo() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
        myAlertDialog.setTitle("Open with");
//        myAlertDialog.setMessage("How do you want to set your picture?");

        myAlertDialog.setPositiveButton("Gallery",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent videoPick = null;
                        videoPick = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        videoPick.setType("video/*");
                        startActivityForResult(videoPick, REQUEST_VIDEO_PICKER);
                    }
                });

        myAlertDialog.setNegativeButton("Camera",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                        startActivityForResult(takeVideoIntent, REQUEST_VIDEO_PICKER);
                    }
                });
        myAlertDialog.show();
    }

    public Intent getDocumentChooserIntent() {
        Intent documentPick = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        String[] mimetypesDocument = {"application/msword", "application/pdf",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"};
        documentPick.setType("*/*");
        documentPick.putExtra(Intent.EXTRA_MIME_TYPES, mimetypesDocument);
//        Intent chooserIntent = Intent.createChooser(documentPick, "Open in...");
        return documentPick;
    }


    public void requestPermission(String intentType) {
        ArrayList<String> requestPermissionArrayList = new ArrayList<>();
        String [] requestPermissionsString;

        if(ActivityCompat.checkSelfPermission(AddArticleActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            requestPermissionArrayList.add(Manifest.permission.CAMERA);
        }
        if(ActivityCompat.checkSelfPermission(AddArticleActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissionArrayList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(ActivityCompat.checkSelfPermission(AddArticleActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissionArrayList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if(requestPermissionArrayList.size() > 0){
            requestPermissionsString = requestPermissionArrayList.toArray(new String[requestPermissionArrayList.size()]);
            ActivityCompat.requestPermissions(AddArticleActivity.this, requestPermissionsString, 100);
        }else{
            if (intentType.equals("document")) {
                startActivityForResult(getDocumentChooserIntent(), REQUEST_DOCUMENT_PICKER);
            } else if (intentType.equals("video")){
                startDialogVideo();
            } else {
                startDialogPicture();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe
    public void onRefreshTokenEvent(RefreshTokenEvent event) {
        if (event.message == null) {
            addArticle(title, description, null, null, null, null, null);
        }else{
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Error");
            alertDialog.setMessage(event.message);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//                    UserManager.getInstance().logOut();
                }
            });
        }
    }

}
