package activity.rewardz;


import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import InternetConnection.CheckInternetConnection;
import bean.PhotoItem;
import constants.Constants;
import cz.msebera.android.httpclient.Header;
import database.SharedDatabase;
import event.RefreshTokenEvent;
import singleton.SettingsManager;
import singleton.UserManager;
import utils.AppController;
import utils.Loader;


public class RedeemedRewardzActivity extends Activity implements OnMapReadyCallback {
    ImageView backgroundImage;
    TextView descriptionText, code;
    LinearLayout back;
    String pk;
    TextView email11;
    WebView descriptionwebview;
    String referencecode, redeemcode, codeToDisplay;
    String redemptionType;
    boolean ismap = false;
    GoogleMap googleMap;
    Double latitude;
    Double longitude;
    String rewardRedemptionPk;
    public CheckInternetConnection checkInternetConnection;
    TextView powerdbySkor;
    String redemptionDate;
    TextView descriptionTest, emailAddress, gold_gym, description_textview1;
    public String token;
    public SharedDatabase sharedDatabase;

    LinearLayout footerLinearLayout;
    ImageView takePhoto;
    ArrayList<PhotoItem> photoItemsArrayList = new ArrayList<>();
    String caption;
    RelativeLayout cropRelativeLayout;
    Button noCropButton, saveCropButton;
    CropImageView cropImageView;
    LinearLayout uploadLinearLayout;
    //    EditText titleEditText;
    Button submitButton, cancelButton;
    ImageView croppedImageView;
    File filePath = null;
    Bitmap croppedBitmap = null;
    String id;
    String rewardzidLocation;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 2) {
            photoItemsArrayList = new ArrayList<PhotoItem>();

            id = data.getStringExtra("MESSAGE");
            try {
//                eventDetailApi();
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (resultCode == Activity.RESULT_OK) {
            Uri imageUri = getPickImageResultUri(data);
            cropRelativeLayout.setVisibility(View.VISIBLE);
            footerLinearLayout.setVisibility(View.GONE);
            boolean requirePermissions = false;
            if (!requirePermissions) {
                cropImageView.setImageUriAsync(imageUri);
            }
            if (resultCode == 0) {

                cropRelativeLayout.setVisibility(View.GONE);
            }
        }
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeemed);
        back = (LinearLayout) findViewById(R.id.back);
        code = (TextView) findViewById(R.id.code);
        checkInternetConnection = new CheckInternetConnection(getApplicationContext());
        descriptionTest = (TextView) findViewById(R.id.description);
        gold_gym = (TextView) findViewById(R.id.gold_gym);
        backgroundImage = (ImageView) findViewById(R.id.image_background);
        descriptionwebview = (WebView) findViewById(R.id.descriptionwebview);
        emailAddress = (TextView) findViewById(R.id.email);
        email11 = (TextView) findViewById(R.id.email11);
        sharedDatabase = new SharedDatabase(getApplicationContext());
        token = sharedDatabase.getToken();
        descriptionText = (TextView) findViewById(R.id.description_textview);
        powerdbySkor = (TextView) findViewById(R.id.powerdbySkor);

        footerLinearLayout = (LinearLayout) findViewById(R.id.activity_redeemed_footerLinearLayout);
        takePhoto = (ImageView) findViewById(R.id.takePhoto);
        cropRelativeLayout = (RelativeLayout) findViewById(R.id.activity_redeemed_cropRelativeLayout);
        noCropButton = (Button) findViewById(R.id.activity_redeemed_noCrop);
        saveCropButton = (Button) findViewById(R.id.activity_redeemed_saveCrop);
        cropImageView = (CropImageView) findViewById(R.id.activity_redeemed_cropImageView);
        uploadLinearLayout = (LinearLayout) findViewById(R.id.activity_redeemed_uploadLinearLayout);
//        titleEditText = (EditText) findViewById(R.id.activity_redeemed_titleEditText);
        submitButton = (Button) findViewById(R.id.activity_redeemed_submitButton);
        cancelButton = (Button) findViewById(R.id.activity_redeemed_cancelButton);
        croppedImageView = (ImageView) findViewById(R.id.activity_redeemed_croppedImageView);
        
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss");
        String dateTime = df.format(c.getTime());
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        description_textview1 = (TextView) findViewById(R.id.description_textview1);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rewardRedemptionPk = getIntent().getStringExtra("RewardRedemptionPk");
        redemptionDate = getIntent().getStringExtra("redemptionPk");

        takePhoto.setVisibility(View.GONE);

        String category = sharedDatabase.getType();
        String subCategory = "";
        int type = 0;

        if(getIntent().hasExtra("category")) {
            subCategory = getIntent().getExtras().getString("category");
        }

        if(getIntent().hasExtra("type")) {
            type = getIntent().getExtras().getInt("type");
        }

        if(getIntent().hasExtra("redemptionType")) {
            redemptionType = getIntent().getExtras().getString("redemptionType");
        }

        switch (type) {
            case 1:
                takePhoto.setVisibility(View.VISIBLE);
                break;
            case 2:
                if(subCategory.equalsIgnoreCase("voucher")) {
                    takePhoto.setVisibility(View.GONE);
                } else if(subCategory.equalsIgnoreCase("e-voucher")) {
                    takePhoto.setVisibility(View.VISIBLE);
                }
                else {
                    takePhoto.setVisibility(View.GONE);
                }
                break;
            default:
                takePhoto.setVisibility(View.GONE);
                break;
        }

        if (rewardRedemptionPk != null) {
            try {
                JSONObject jsonObject = new JSONObject(rewardRedemptionPk);
                String redemptiontype = jsonObject.getString("description");
                descriptionwebview.getSettings().setJavaScriptEnabled(true);
                descriptionwebview.loadData(redemptiontype, "text/html", "utf-8");
                descriptionwebview.setBackgroundColor(444444);
                WebSettings settings = descriptionwebview.getSettings();
                settings.setDefaultFontSize(14);
                String rewardName = jsonObject.getString("reward_name");
                gold_gym.setText("" + rewardName);
                String redemptioncode = jsonObject.getString("redemption_code");
                code.setText("CODE " + redemptioncode);
                rewardzidLocation = jsonObject.getString("reward_location_id");
                System.out.print("rewardRedemptionPk " + rewardRedemptionPk);
                email11.setText("Redeemed on the " + redemptionDate);
                if (!rewardzidLocation.equals("null")) {
                    ismap = true;
                    if (checkInternetConnection.isConnectingToInternet()) {
                        rewardzRedeemApi(rewardzidLocation);
                    } else {
                        if (getApplicationContext() != null) {
                            connectivityMessage("Waiting for network!");
                        }
                    }
                } else {
                    if (getApplicationContext() != null) {
                        connectivityMessage(
                                "Data not found.");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            pk = getIntent().getStringExtra("pk");
            referencecode = getIntent().getStringExtra("reference");
            redeemcode = getIntent().getStringExtra("description");
            redeemcode = redeemcode.trim();
            redemptionType = getIntent().getStringExtra("redemptionType");
            email11.setText("Redeemed on the " + df.format(c.getTime()));
            if (redemptionType.equals("flash_password")) {
                code.setText("CODE " + referencecode);
                descriptionwebview.getSettings().setJavaScriptEnabled(true);
                descriptionwebview.loadData(redeemcode, "text/html", "utf-8");
                descriptionwebview.setBackgroundColor(444444);
                WebSettings settings = descriptionwebview.getSettings();
                settings.setDefaultFontSize(14);

            } else if (redemptionType.equals("flash")) {
                descriptionwebview.getSettings().setJavaScriptEnabled(true);
                descriptionwebview.loadData(redeemcode, "text/html", "utf-8");
                descriptionwebview.setBackgroundColor(444444);
                WebSettings settings = descriptionwebview.getSettings();
                settings.setDefaultFontSize(14);
                code.setText("CODE " + referencecode);

            } else if (redemptionType.equals("e_voucher")) {
                code.setVisibility(View.GONE);
                powerdbySkor.setVisibility(View.GONE);
                descriptionTest.setVisibility(View.VISIBLE);
                descriptionwebview.setVisibility(View.GONE);
                descriptionTest.setText("Your voucher code is \n" + referencecode + " \n\n" + redeemcode);

                descriptionTest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            } else if (redemptionType.equals("physical_voucher")) {
                descriptionwebview.getSettings().setJavaScriptEnabled(true);
                descriptionwebview.loadData(redeemcode, "text/html", "utf-8");
                descriptionwebview.setBackgroundColor(444444);
                WebSettings settings = descriptionwebview.getSettings();
                settings.setDefaultFontSize(14);
                code.setVisibility(View.GONE);
                powerdbySkor.setVisibility(View.GONE);

            } else if (redemptionType.equals("mobile_topup")) {
                code.setText("CODE " + referencecode);
                descriptionwebview.getSettings().setJavaScriptEnabled(true);
                descriptionwebview.loadData(redeemcode, "text/html", "utf-8");
                descriptionwebview.setBackgroundColor(444444);
                WebSettings settings = descriptionwebview.getSettings();
                settings.setDefaultFontSize(14);

            } else if (redemptionType.equals("electricity_topup")) {
                code.setText("CODE " + referencecode);
                descriptionwebview.getSettings().setJavaScriptEnabled(true);
                descriptionwebview.loadData(redeemcode, "text/html", "utf-8");
                descriptionwebview.setBackgroundColor(444444);
                WebSettings settings = descriptionwebview.getSettings();
                settings.setDefaultFontSize(14);

            } else if (redemptionType.equals("data_topup")) {
                code.setText("CODE " + referencecode);
                descriptionwebview.getSettings().setJavaScriptEnabled(true);
                descriptionwebview.loadData(redeemcode, "text/html", "utf-8");
                descriptionwebview.setBackgroundColor(444444);
                WebSettings settings = descriptionwebview.getSettings();
                settings.setDefaultFontSize(14);

            } else if (redemptionType.equals("promo_code")) {
                code.setText("CODE " + referencecode);
                descriptionwebview.getSettings().setJavaScriptEnabled(true);
                descriptionwebview.loadData(redeemcode, "text/html", "utf-8");
                descriptionwebview.setBackgroundColor(444444);
                WebSettings settings = descriptionwebview.getSettings();
                settings.setDefaultFontSize(14);
            }

            descriptionText.setText(RewardzDetailActivity.descriptionText.getText().toString());
            emailAddress.setText(RewardzDetailActivity.emailAddress.getText().toString());
            descriptionText.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));

            emailAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL, new String[]
                            {RewardzDetailActivity.emailAddress.getText().toString()});
                    i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
                    i.putExtra(Intent.EXTRA_TEXT, "body of email");
                    i.putExtra(Intent.EXTRA_TEXT, R.string.intent_message);
                    try {
                        startActivity(Intent.createChooser(i, "Send mail..."));
                    } catch (android.content.ActivityNotFoundException ex) {
                        connectivityMessage(
                                "There are no email clients installed.");
                    }
                }
            });
            try {
                if (googleMap == null) {
                    MapFragment mapFragment = (MapFragment) getFragmentManager()
                            .findFragmentById(R.id.map);
                    mapFragment.getMapAsync(this);
                }


                gold_gym.setText(RewardzDetailActivity.gold_gym.getText().toString());
                description_textview1.setText(RewardzDetailActivity.description_textview1.getText().toString());
                String imageUrl = Constants.BASEURL + RewardzDetailActivity.jsonObject.getString("display_img_url");
                Glide.with(getApplicationContext()).load(imageUrl).into(backgroundImage);

            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }

        if(type == 2 && subCategory.equalsIgnoreCase("e-voucher")) {
            code.setText("CODE " + getIntent().getExtras().getString("code"));
            descriptionwebview.getSettings().setJavaScriptEnabled(true);
            descriptionwebview.loadData(redeemcode, "text/html", "utf-8");
            descriptionwebview.setBackgroundColor(444444);
            WebSettings settings = descriptionwebview.getSettings();
            settings.setDefaultFontSize(14);
        }

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(RedeemedRewardzActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(RedeemedRewardzActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                }

                photoItemsArrayList = new ArrayList<PhotoItem>();
                startActivityForResult(getPickImageChooserIntent(), 200);
            }
        });

        noCropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropRelativeLayout.setVisibility(View.GONE);
                footerLinearLayout.setVisibility(View.VISIBLE);
            }
        });

        saveCropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    croppedBitmap = cropImageView.getCroppedImage(500, 500);
                    if (croppedBitmap != null) {

                        uploadLinearLayout.setVisibility(View.VISIBLE);
                        croppedImageView.setVisibility(View.VISIBLE);
//                        cropRelativeLayout.setVisibility(View.GONE);
                        croppedImageView.setImageBitmap(croppedBitmap);
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    uploadLinearLayout.setVisibility(View.GONE);
                    footerLinearLayout.setVisibility(View.VISIBLE);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetConnection.isConnectingToInternet()) {
                    try {
                        InputMethodManager inputManager = (InputMethodManager)
                                getSystemService(Context.INPUT_METHOD_SERVICE);

                        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                        photoItemsArrayList = new ArrayList<PhotoItem>();
//                        title = titleEditText.getText().toString();
//                        titleEditText.getText().clear();


//                        cropRelativeLayout.setVisibility(View.GONE);
//                        uploadLinearLayout.setVisibility(View.GONE);
//                        footerLinearLayout.setVisibility(View.VISIBLE);

                        saveIntoFile(croppedBitmap);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    connectivityMessage("Waiting for network!");
                }
            }
        });
    }

    public void saveIntoFile(Bitmap bitmap) throws IOException {
        try {
            Bitmap bitmap1 = bitmap;
            Random random = new Random();
            int ii = 100000;

            boolean success = true;

            ii = random.nextInt(ii);
            String fname = "MyPic_" + ii + ".png";
            File direct = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/MyFolder");

            Log.d("direct : ", direct.toString());
            if (!direct.exists()) {
                Log.d("directory not exists", "");
                File wallpaperDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/MyFolder");
                success = wallpaperDirectory.mkdirs();

                if (success) {
                    Log.d("success on mkdir", "");
                } else {
                    Log.d("failed on mkdir", "");
                }
            }

            File mainfile = new File(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/MyFolder"), fname);


            if (mainfile.exists()) {
                mainfile.delete();
            }
            // raiseRuntimePermisionForLocation();
            FileOutputStream fileOutputStream;
            fileOutputStream = new FileOutputStream(mainfile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, fileOutputStream);
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
                try {
                    callRewardzPhotoAPI();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (getApplicationContext() != null) {
                    connectivityMessage("Image Size Should be less than 6 MB");
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void callRewardzPhotoAPI() {
        Loader.showProgressDialog(this);
        RequestParams paramMap = new RequestParams();

//        paramMap.put("pk", rewardzObject.pk);
//        paramMap.put("reward", rewardzObject.pk);
        if (filePath != null) {
            try {
                paramMap.put("attachment", filePath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

//        paramMap.put("")
        String authProvider = SettingsManager.getInstance().getAuthProvider();
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("USER-AGENT", AppController.useragent);
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("Content-Disposition", "form-data");
        String url = Constants.BASEURL + "redemptions/api/redemptions/" + referencecode + "/upload/";
        client.post(url, paramMap, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if (getApplicationContext() != null) {
                    Loader.dialogDissmiss(getApplicationContext());
                    connectivityMessage("Updated Successfully");
                    try {
                        cropRelativeLayout.setVisibility(View.GONE);
                        uploadLinearLayout.setVisibility(View.GONE);
                        footerLinearLayout.setVisibility(View.VISIBLE);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (getApplicationContext() != null) {
                    Loader.dialogDissmiss(getApplicationContext());

                    if (statusCode == 500) {
                        if (getApplicationContext() != null) {
                            connectivityMessage("We've encountered a technical error.our team is working on it. please try again later");
                        }
                    }
                    try {
                        JSONObject jsonObject = new JSONObject(responseString);
                        String errormessage = jsonObject.getString("detail");
                        if (getApplicationContext() != null) {
                            connectivityMessage("" + errormessage);
                        }
                    } catch (JSONException js) {
                        js.printStackTrace();
                    }
                    if (statusCode == 413) {
                        if (getApplicationContext() != null) {
                            connectivityMessage("Image Size Should be less than 6 MB");
                        }

                    }
                }

            }
        });
    }

    public Intent getPickImageChooserIntent() {
        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        // collect all camera intents

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

        // the main intent is the last in the  list (fucking android) so pickup the useless one

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

    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "pickImageResult.jpeg"));
            caption = "caption";
        }

        return outputFileUri;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {


                }
                return;
            }


        }
    }


    public void rewardzRedeemApi(String locationid) {
        String authProvider = SettingsManager.getInstance().getAuthProvider();

        Loader.showProgressDialog(this);
        HashMap<String, String> paramMap = new HashMap<String, String>();
        RequestParams params = new RequestParams(paramMap);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(80000);
        client.addHeader("USER-AGENT", AppController.useragent);
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("Content-Type", "application/json");
        String url = Constants.BASEURL + Constants.REWARDZ_LOCATION_ID + locationid + "/";
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                super.onSuccess(statusCode, headers, jsonObject);
                Loader.dialogDissmiss(getApplicationContext());

                updateUI(jsonObject.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Loader.dialogDissmiss(getApplicationContext());
                if (statusCode == 401) {
                    UserManager.getInstance().logOut(RedeemedRewardzActivity.this);
                }
                if (statusCode == 400) {
                    connectivityMessage( "" + errorResponse);
                }

                if (statusCode == 500) {
                    connectivityMessage("We've encountered a technical error.our team is working on it. please try again later");
                }
            }
        });
    }

    public void updateUI(String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONObject rewardJsonObject = jsonObject.getJSONObject("reward");
            String description = rewardJsonObject.getString("description");
            description_textview1.setText(Html.fromHtml(description));
            String thumbnail_img_url = rewardJsonObject.getString("thumbnail_img_url");
            Glide.with(getApplicationContext()).load(Constants.BASEURL + thumbnail_img_url).into(backgroundImage);
            JSONObject contactdetailsJsonObject = rewardJsonObject.getJSONObject("contact_details");
            String email = contactdetailsJsonObject.getString("email");
            emailAddress.setText("" + email);
            String address = jsonObject.getString("address");
            descriptionText.setText("" + address);
            JSONArray coordinatesJsonArray = jsonObject.getJSONArray("coordinates");
            latitude = coordinatesJsonArray.getDouble(0);
            longitude = coordinatesJsonArray.getDouble(1);

            try {
                if (googleMap == null) {
                    MapFragment mapFragment = (MapFragment) getFragmentManager()
                            .findFragmentById(R.id.map);
                    mapFragment.getMapAsync(this);
                }


            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (JSONException js) {
            js.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void connectivityMessage(String msg) {

        if (msg.equals("Network Connecting....")) {
            SnackbarManager.show(Snackbar.with(getApplicationContext()).text(msg).textColor(Color.WHITE)
                    .color(Color.parseColor("#FF9B30")), this);
        } else if (msg.equals("Network Connected")) {
            SnackbarManager.show(Snackbar.with(getApplicationContext()).text(msg).textColor(Color.WHITE)
                    .color(Color.parseColor("#4BCC1F")), this);
        } else {
            SnackbarManager.show(Snackbar.with(getApplicationContext()).text(msg).textColor(Color.WHITE)
                    .color(Color.RED), this);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            if (!ismap) {
                this.googleMap = googleMap;
                if (RewardzDetailActivity.longitudeCoordinateArray.size() > 0) {
                    LatLng rewrdzMapCordinate = new LatLng(RewardzDetailActivity.longitudeCoordinateArray.get(RewardzDetailActivity.selectedIndex), RewardzDetailActivity.latitudeCoordinateArray.get(RewardzDetailActivity.selectedIndex));
                    googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                    googleMap.addMarker(new MarkerOptions().
                            position(rewrdzMapCordinate).title(RewardzDetailActivity.descriptionText.getText().toString()));
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(RewardzDetailActivity.longitudeCoordinateArray.get(RewardzDetailActivity.selectedIndex), RewardzDetailActivity.latitudeCoordinateArray.get(RewardzDetailActivity.selectedIndex)), 17.0f));
                }
            } else {
                System.out.println(latitude + "  map  " + longitude);
                LatLng rewrdzMapCordinate = new LatLng(longitude, latitude);
                googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                googleMap.addMarker(new MarkerOptions().
                        position(rewrdzMapCordinate).title(descriptionText.getText().toString()));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(longitude, latitude), 17.0f));

            }
        } catch (Exception ex) {
            ex.printStackTrace();
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
            rewardzRedeemApi(rewardzidLocation);
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




