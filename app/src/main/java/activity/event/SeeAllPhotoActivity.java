package activity.event;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.root.skor.R;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import InternetConnection.CheckInternetConnection;
import activity.userprofile.MainActivity;
import adaptor.SeeAllPhotoAdapter;
import bean.PhotoItem;
import constants.Constants;
import cz.msebera.android.httpclient.Header;
import database.SharedDatabase;
import event.RefreshTokenEvent;
import singleton.SettingsManager;
import singleton.UserManager;
import utils.AppController;
import utils.Loader;


public class SeeAllPhotoActivity extends AppCompatActivity {
    GridView gridView;
    public static String id;
    TextView add, all, organizer, user;
    String title = "";
    String caption = "";
    File filePath;
    Button CropImageViewNo, CropImageView1;
    CropImageView mCropImageView;
    RelativeLayout Crop, relativeLayoutvisiblity;
    public SharedDatabase sharedDatabase;
    public String token;

    LinearLayout back;
    String type = "all";
    public static int position1;
    AlertDialog alertDialog;
    public SeeAllPhotoAdapter seeAllPhotoAdapter;
    LinearLayout imageupload_layout;
    ArrayList<PhotoItem> photoItemsArrayList = new ArrayList<>();
    public static ArrayList<PhotoItem> photoItemsImageThumbnailArrayList = new ArrayList<>();
    CheckInternetConnection checkInternetConnection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_all_photo);
        gridView = (GridView) findViewById(R.id.grid);
        sharedDatabase = new SharedDatabase(getApplicationContext());
        token = sharedDatabase.getToken();
        id = getIntent().getStringExtra("id");
        add = (TextView) findViewById(R.id.add);
        all = (TextView) findViewById(R.id.all);
        imageupload_layout = (LinearLayout) findViewById(R.id.imageupload_layout);

        organizer = (TextView) findViewById(R.id.organizer);
        user = (TextView) findViewById(R.id.user);
        back = (LinearLayout) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("MESSAGE", id);
                setResult(2, intent);
                finish();
            }
        });
        checkInternetConnection = new CheckInternetConnection(this);
        if (checkInternetConnection.isConnectingToInternet()) {
            eventPhotoGalleriesApi();
        } else {
            connectivityMessage("Waiting for network!");
        }
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                photoItemsArrayList = new ArrayList<PhotoItem>();
                startActivityForResult(getPickImageChooserIntent(), 200);

            }

        });
        mCropImageView = (CropImageView) findViewById(R.id.CropImageView);
        relativeLayoutvisiblity = (RelativeLayout) findViewById(R.id.rele);

        Crop = (RelativeLayout) findViewById(R.id.crop);
        CropImageViewNo = (Button) findViewById(R.id.CropImageViewNo);

        CropImageViewNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Crop.setVisibility(View.GONE);
                relativeLayoutvisiblity.setVisibility(View.VISIBLE);

            }
        });
        CropImageView1 = (Button) findViewById(R.id.CropImageView1);
        CropImageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Bitmap cropped = mCropImageView.getCroppedImage(500, 500);
                    if (cropped != null)
                        imageupload_layout.setVisibility(View.VISIBLE);
                    Crop.setVisibility(View.GONE);

                    customAlert(cropped);

                     /*    customAlert(cropped);*/
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "all";
                photoItemsArrayList = new ArrayList<PhotoItem>();
                photoItemsImageThumbnailArrayList = new ArrayList<PhotoItem>();
                all.setBackgroundColor(Color.parseColor("#4EB1F5"));
                organizer.setBackgroundResource(0);
                user.setBackgroundResource(0);
                all.setTextColor(Color.WHITE);
                organizer.setTextColor(Color.BLACK);
                user.setTextColor(Color.BLACK);
                eventPhotoGalleriesApi();
            }
        });
        organizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "organizer";
                photoItemsArrayList = new ArrayList<PhotoItem>();
                photoItemsImageThumbnailArrayList = new ArrayList<PhotoItem>();
                organizer.setBackgroundColor(Color.parseColor("#4EB1F5"));
                user.setBackgroundResource(0);
                all.setBackgroundResource(0);
                organizer.setTextColor(Color.WHITE);
                all.setTextColor(Color.BLACK);
                user.setTextColor(Color.BLACK);
                eventPhotoGalleriesApi();
            }
        });
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "user";
                photoItemsArrayList = new ArrayList<PhotoItem>();
                photoItemsImageThumbnailArrayList = new ArrayList<PhotoItem>();
                user.setBackgroundColor(Color.parseColor("#4EB1F5"));
                organizer.setBackgroundResource(0);
                all.setBackgroundResource(0);
                user.setTextColor(Color.WHITE);
                all.setTextColor(Color.BLACK);
                organizer.setTextColor(Color.BLACK);
                eventPhotoGalleriesApi();
            }
        });
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (photoItemsImageThumbnailArrayList.size() != 0) {
                    position1 = position;
                    Intent intent = new Intent(getApplicationContext(), ZoomImageViewActivity.class);

                    startActivityForResult(intent, 2);

                }
            }
        });


    }

    public void connectivityMessage(String msg) {

        if (msg.equals("Network Connecting....")) {
            if (getApplicationContext() != null) {
                SnackbarManager.show(Snackbar.with(getApplicationContext()).text(msg).textColor(Color.WHITE)
                        .color(Color.parseColor("#FF9B30")), this);
            }
        } else if (msg.equals("Network Connected")) {
            if (getApplicationContext() != null) {
                SnackbarManager.show(Snackbar.with(getApplicationContext()).text(msg).textColor(Color.WHITE)
                        .color(Color.parseColor("#4BCC1F")), this);
            }
        } else {
            if (getApplicationContext() != null) {
                SnackbarManager.show(Snackbar.with(getApplicationContext()).text(msg).textColor(Color.WHITE)
                        .color(Color.RED), this);
            }
        }

    }


    public Intent getPickImageChooserIntent() {
        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        // collect all gallery intents

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));
        return chooserIntent;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 2) {
            photoItemsArrayList = new ArrayList<PhotoItem>();

            id = data.getStringExtra("MESSAGE");
            eventPhotoGalleriesApi();
        }
        if (resultCode == Activity.RESULT_OK) {
            Uri imageUri = getPickImageResultUri(data);
            Crop.setVisibility(View.VISIBLE);
            relativeLayoutvisiblity.setVisibility(View.GONE);
            boolean requirePermissions = false;
            if (!requirePermissions) {
                mCropImageView.setImageUriAsync(imageUri);
            }
            if (resultCode == 0) {

                Crop.setVisibility(View.GONE);
                relativeLayoutvisiblity.setVisibility(View.VISIBLE);
            }

        }
    }

    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "pickImageResult.jpeg"));
            caption = "caption";
        }

        return outputFileUri;
    }

    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null && data.getData() != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
            caption = "caption";
        }
        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

    public void customAlert(final Bitmap bp1) {
        final ImageView imageView = (ImageView) findViewById(R.id.vender_Pass);
        final TextView titleText = (TextView) findViewById(R.id.title1);
        final EditText EditTextTilte = (EditText) findViewById(R.id.title);

        final Button submit = (Button) findViewById(R.id.submit);
        final Button cancel = (Button) findViewById(R.id.cancel);
        final Button yes = (Button) findViewById(R.id.yes);
        final Button no = (Button) findViewById(R.id.no);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    relativeLayoutvisiblity.setVisibility(View.VISIBLE);
                    imageupload_layout.setVisibility(View.GONE);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!EditTextTilte.getText().toString().isEmpty()) {
                    if (checkInternetConnection.isConnectingToInternet()) {
                        try {
                            InputMethodManager inputManager = (InputMethodManager)
                                    getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);
                            photoItemsArrayList = new ArrayList<PhotoItem>();
                            relativeLayoutvisiblity.setVisibility(View.VISIBLE);
                            title = EditTextTilte.getText().toString();
                            EditTextTilte.getText().clear();
                            imageupload_layout.setVisibility(View.GONE);

                            saveIntoFile(bp1);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    } else {
                        connectivityMessage("Waiting for network!");
                    }


                } else {
                    titleText.setVisibility(View.VISIBLE);

                    EditTextTilte.setVisibility(View.GONE);
                    EditTextTilte.getText().clear();
                    submit.setVisibility(View.GONE);
                    cancel.setVisibility(View.GONE);
                    yes.setVisibility(View.VISIBLE);
                    no.setVisibility(View.VISIBLE);
                    titleText.setText(" Are you sure you dont want to add title?");
                }
            }
        });

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    photoItemsArrayList = new ArrayList<PhotoItem>();
                    relativeLayoutvisiblity.setVisibility(View.VISIBLE);
                    title = "";
                    EditTextTilte.setVisibility(View.VISIBLE);
                    submit.setVisibility(View.VISIBLE);
                    cancel.setVisibility(View.VISIBLE);
                    yes.setVisibility(View.GONE);
                    titleText.setVisibility(View.GONE);
                    no.setVisibility(View.GONE);
                    imageupload_layout.setVisibility(View.GONE);

                    saveIntoFile(bp1);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditTextTilte.setVisibility(View.VISIBLE);
                submit.setVisibility(View.VISIBLE);
                cancel.setVisibility(View.VISIBLE);
                yes.setVisibility(View.GONE);
                titleText.setVisibility(View.GONE);
                no.setVisibility(View.GONE);
            }
        });
        imageView.setImageBitmap(bp1);
    }


    public void saveIntoFile(Bitmap bitmap) throws IOException {
        try {
            Bitmap bitmap1 = bitmap;
            Random random = new Random();
            int ii = 100000;
            ii = random.nextInt(ii);
            String fname = "MyPic_" + ii + ".png";
            File direct = new File(Environment.getExternalStorageDirectory() + "/MyFolder");

            if (!direct.exists()) {
                File wallpaperDirectory = new File("/sdcard/MyFolder/");
                wallpaperDirectory.mkdirs();
            }

            File mainfile = new File(new File("/sdcard/MyFolder/"), fname);
            if (mainfile.exists()) {
                mainfile.delete();
            }
            // raiseRuntimePermisionForLocation();
            FileOutputStream fileOutputStream;
            fileOutputStream = new FileOutputStream(mainfile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 70, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            filePath = new File(mainfile.toString());
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] imageInByte = stream.toByteArray();
            long lengthbmp = imageInByte.length / 1000000;
            System.out.println("image size in mb==" + lengthbmp);
            if (lengthbmp <= 6) {
                filePath = new File(mainfile.toString());
                callEventPhotoAPI();
            } else {
                if (getApplicationContext() != null) {
                    connectivityMessage("Image Size Should be less than 6 MB");                }
            }

            // if()

            // System.out.println("File path is " + filePath.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void callEventPhotoAPI() {

        Loader.showProgressDialog(this);
        String firstName = sharedDatabase.getFirstName();
        String lastName = sharedDatabase.getLastName();
        String owner = firstName + " " + lastName;
        RequestParams paramMap = new RequestParams();
        if (title.equals("")) {

        } else {
            paramMap.put("title", title);
        }
        paramMap.put("caption", caption);
        paramMap.put("owner", owner);
        if (filePath != null) {
            try {
                paramMap.put("image", filePath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        String authProvider = SettingsManager.getInstance().getAuthProvider();

        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("USER-AGENT", AppController.useragent);
        client.addHeader("Authorization", authProvider + " " + token);
        String url = Constants.BASEURL + Constants.IMAGE_UPLOAD + id + "/upload/";
        client.post(url, paramMap, new TextHttpResponseHandler() {


            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if (getApplicationContext() != null) {
                    Loader.dialogDissmiss(getApplicationContext());
                    connectivityMessage( "Updated Successfully");                    eventPhotoGalleriesApi();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (getApplicationContext() != null) {
                    Loader.dialogDissmiss(getApplicationContext());
                    if (statusCode == 400) {
                        if (getApplicationContext() != null) {
                            connectivityMessage( "" + responseString);                        }
                    }
                    try {
                        JSONObject jsonObject = new JSONObject(responseString);
                        String errormessage = jsonObject.getString("detail");
                        if (errormessage != null) {
                            if (getApplicationContext() != null) {
                                connectivityMessage( "" + responseString);                            }
                        } else {
                            if (getApplicationContext() != null) {
                                connectivityMessage( "Something went wrong");
                            }
                        }
                    } catch (JSONException js) {
                        js.printStackTrace();
                    }
                    if (statusCode == 401) {
                        UserManager.getInstance().logOut(SeeAllPhotoActivity.this);
                    }

                    if (statusCode == 500) {
                        if (getApplicationContext() != null) {
                            connectivityMessage("We've encountered a technical error.our team is working on it. please try again later");                        }
                    }
                    if (statusCode == 413) {
                        if (getApplicationContext() != null) {
                            connectivityMessage("Image Size Should be less than 6 MB");                        }
                    }
                }

            }
        });
    }

    public void eventPhotoGalleriesApi() {
        String authProvider = SettingsManager.getInstance().getAuthProvider();

        Loader.showProgressDialog(this);
        HashMap<String, String> paramMap = new HashMap<String, String>();
        RequestParams params = new RequestParams(paramMap);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("USER-AGENT", AppController.useragent);
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("Content-Type", "application/json");
        client.get(Constants.BASEURL + Constants.PHOTO_GALLARIES + id + "/", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                super.onSuccess(statusCode, headers, jsonObject);
                if (getApplicationContext() != null) {
                    Loader.dialogDissmiss(getApplicationContext());
                    photoItemsImageThumbnailArrayList = new ArrayList<PhotoItem>();
                    photoItemsArrayList = new ArrayList<PhotoItem>();
                    parsePhotoGalleryImageData(jsonObject.toString());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if (getApplicationContext() != null) {
                    Loader.dialogDissmiss(getApplicationContext());
                    if (statusCode == 400) {
                        connectivityMessage("" + errorResponse);                    }
                }
            }
        });
    }


    public void parsePhotoGalleryImageData(String jsonobject) {
        try {
            JSONObject jsonObject = new JSONObject(jsonobject);
            PhotoItem photoItem;
            JSONArray userPhotosJsonArray = jsonObject.getJSONArray("user_photos");

            JSONArray organizerPhotosJsonArray = jsonObject.getJSONArray("organizer_photos");
            if (type.equals("user")) {
                for (int i = 0; i < userPhotosJsonArray.length(); i++) {
                    JSONObject userPhotosJsonObject = userPhotosJsonArray.getJSONObject(i);
                    String displayImage = userPhotosJsonObject.getString("thumbnail_url");

                    String title = userPhotosJsonObject.getString("title");

                    String imageThumbnail = userPhotosJsonObject.getString("display_url");
                    JSONObject ownerJsonObject = userPhotosJsonObject.getJSONObject("owner");
                    String fullName = ownerJsonObject.getString("full_name");
                    photoItem = new PhotoItem(displayImage, imageThumbnail, fullName, title, id);
                    photoItemsArrayList.add(photoItem);
                    photoItemsImageThumbnailArrayList.add(photoItem);
                }
                if (getApplicationContext() != null) {
                    seeAllPhotoAdapter = new SeeAllPhotoAdapter(getApplicationContext(), photoItemsArrayList);
                    gridView.setAdapter(seeAllPhotoAdapter);
                }
            }
            if (type.equals("all")) {
                for (int i = 0; i < userPhotosJsonArray.length(); i++) {
                    JSONObject userPhotosJsonObject = userPhotosJsonArray.getJSONObject(i);
                    String displayImage = userPhotosJsonObject.getString("thumbnail_url");
                    String imageThumbnail = userPhotosJsonObject.getString("display_url");
                    String title = userPhotosJsonObject.getString("title");
                    JSONObject ownerJsonObject = userPhotosJsonObject.getJSONObject("owner");
                    String fullName = ownerJsonObject.getString("full_name");

                    photoItem = new PhotoItem(displayImage, imageThumbnail, fullName, title, id);
                    photoItemsArrayList.add(photoItem);
                    photoItemsImageThumbnailArrayList.add(photoItem);
                }
                for (int i = 0; i < organizerPhotosJsonArray.length(); i++) {
                    JSONObject userPhotosJsonObject = organizerPhotosJsonArray.getJSONObject(i);
                    String displayImage = userPhotosJsonObject.getString("thumbnail_url");
                    String title = userPhotosJsonObject.getString("title");

                    String imageThumbnail = userPhotosJsonObject.getString("display_url");
                    JSONObject ownerJsonObject = userPhotosJsonObject.getJSONObject("owner");
                    String fullName = ownerJsonObject.getString("full_name");

                    photoItem = new PhotoItem(displayImage, imageThumbnail, fullName, title, id);
                    photoItemsArrayList.add(photoItem);
                    photoItemsImageThumbnailArrayList.add(photoItem);
                }
                if (getApplicationContext() != null) {
                    seeAllPhotoAdapter = new SeeAllPhotoAdapter(getApplicationContext(), photoItemsArrayList);
                    gridView.setAdapter(seeAllPhotoAdapter);
                }
            }
            if (type.equals("organizer")) {
                for (int i = 0; i < organizerPhotosJsonArray.length(); i++) {
                    JSONObject userPhotosJsonObject = organizerPhotosJsonArray.getJSONObject(i);
                    String displayImage = userPhotosJsonObject.getString("thumbnail_url");
                    String title = userPhotosJsonObject.getString("title");

                    String imageThumbnail = userPhotosJsonObject.getString("display_url");
                    JSONObject ownerJsonObject = userPhotosJsonObject.getJSONObject("owner");
                    String fullName = ownerJsonObject.getString("full_name");

                    photoItem = new PhotoItem(displayImage, imageThumbnail, fullName, title, id);
                    photoItemsArrayList.add(photoItem);
                    photoItemsImageThumbnailArrayList.add(photoItem);
                }
                if (getApplicationContext() != null) {
                    seeAllPhotoAdapter = new SeeAllPhotoAdapter(getApplicationContext(), photoItemsArrayList);
                    gridView.setAdapter(seeAllPhotoAdapter);
                }

            }


        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        // code here to show dialog
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        MainActivity.isInternetConnection = false;
        intent.putExtra("action", Constants.ACTION_POINT_SUMMERY);
        startActivity(intent);
        finish();
        super.onBackPressed();  // optional depending on your needs
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
            eventPhotoGalleriesApi();
        }else{
            android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(this).create();
            alertDialog.setTitle("Error");
            alertDialog.setMessage(event.message);
            alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_NEUTRAL, "Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    UserManager.getInstance().logOut(SeeAllPhotoActivity.this);
                }
            });
        }
    }

}
