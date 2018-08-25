package activity.event;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.root.skor.R;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.viewpagerindicator.CirclePageIndicator;

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
import java.nio.charset.Charset;
import java.sql.Time;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import InternetConnection.CheckInternetConnection;
import activity.userprofile.LoginActivity;
import activity.userprofile.MainActivity;
import adaptor.ImageAdapter;
import bean.PhotoItem;
import constants.Constants;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import database.SharedDatabase;
import event.RefreshTokenEvent;
import interfaces.CallbackInterface;
import singleton.SettingsManager;
import singleton.UserManager;
import utils.AppController;

public class EventDetailActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, CallbackInterface {

    TextView startDate, website;
    TextView descriptionText;
    TextView address;
    private int mShortAnimationDuration;
    Double lattitude = 0d;
    public static String id;
    Location location;
    LocationManager locationManager;
    Double longitude;
    File filePath = null;
    String provider;
    ImageView facilityWeblink;
    TextView termsandConditions;
    ImageView downArrow;
    Double facilityLatitude = 0d;
    Double facilityLongitude = 0d;
    LinearLayout dialNumber;
    String cordinates;
    String title, caption;
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    boolean isTodayEvent = false;
    AlertDialog alertDialog = null;
    public static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
    public static final Charset UTF_8 = Charset.forName("UTF-8");
    Time timeEvent;
    ViewPager viewPager;
    ImageAdapter imageAdapter;
    TextView facilityName;
    LinearLayout back, linearlayout;
    CirclePageIndicator mIndicator;
    ImageView backgroundImage;
    TextView enddate;
    LinearLayout imageupload_layout;
    Button attending, image_upload;
    String rsvp_state = "";
    ArrayList<PhotoItem> photoItemsArrayList = new ArrayList<>();
    boolean isPasswordRequired = false;
    TextView whomToContact, phoneNumber, emailAddress;
    HashMap<String, String> params = new HashMap<>();
    CheckInternetConnection checkInternetConnection;
    GoogleApiClient mGoogleApiClient;
    public String PACKAGE_NAME;
    SharedPreferences sharedPreferencesToken;
    Button CropImageViewNo, CropImageView1;
    CropImageView mCropImageView;
    RelativeLayout Crop;
    public SharedDatabase sharedDatabase;
    public String token;
    TextView SeeAllPhoto;
    String popMessage = "";
    android.support.v7.widget.RecyclerView recyclerView;
    LayoutInflater layoutInflater;
    private String step_activity_limit, enable_activity_tracker;
    SharedPreferences.Editor tokenEditor;
    private Date startTime;
    private Date endTime;

    public EventDetailActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        PACKAGE_NAME = getApplicationContext().getPackageName();
        sharedDatabase = new SharedDatabase(getApplicationContext());
        token = sharedDatabase.getToken();
        step_activity_limit = sharedDatabase.getStepactivitylimit();
        enable_activity_tracker = sharedDatabase.getEnableactivitytracker();


        if (enable_activity_tracker.equals("0") || enable_activity_tracker.equals("")) {

        } else {
            buildClient();
        }

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        checkInternetConnection = new CheckInternetConnection(getApplicationContext());
        imageupload_layout = (LinearLayout) findViewById(R.id.imageupload_layout);
        backgroundImage = (ImageView) findViewById(R.id.backgroundimage);
        termsandConditions = (TextView) findViewById(R.id.terms_conditions);
        dialNumber = (LinearLayout) findViewById(R.id.dial);
        image_upload = (Button) findViewById(R.id.image_upload);
        facilityName = (TextView) findViewById(R.id.title);
        attending = (Button) findViewById(R.id.attending);
        whomToContact = (TextView) findViewById(R.id.whom_to_contact);
        phoneNumber = (TextView) findViewById(R.id.phonenumber);
        emailAddress = (TextView) findViewById(R.id.email);
        facilityWeblink = (ImageView) findViewById(R.id.facility_weblink);
        mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
        downArrow = (ImageView) findViewById(R.id.down_arrow);
        enddate = (TextView) findViewById(R.id.end);
        enddate.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        attending.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        address = (TextView) findViewById(R.id.address);
        viewPager = (ViewPager) findViewById(R.id.pagers);
        mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        SeeAllPhoto = (TextView) findViewById(R.id.see_all_photo);

        linearlayout = (LinearLayout) findViewById(R.id.linearlayout);
        address.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        startDate = (TextView) findViewById(R.id.start);
        startDate.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        website = (TextView) findViewById(R.id.website);
        descriptionText = (TextView) findViewById(R.id.description_textview);
        id = getIntent().getStringExtra("id");
        if (checkInternetConnection.isConnectingToInternet()) {
            try {
                eventDetailApi();
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

//        File file = new File(Environment.getExternalStorageDirectory(), "/MyFolder/ganteng.png");
//        Log.d("beraks : ", file.getAbsolutePath());
//
//        Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//        backgroundImage.setImageBitmap(myBitmap);

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        System.out.println(sdf.format(cal.getTime()));
        timeEvent = Time.valueOf(sdf.format(cal.getTime()));
        website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent webLinkIntent = new Intent(Intent.ACTION_VIEW);
                webLinkIntent.setData(Uri.parse(website.getText().toString()));
                startActivity(webLinkIntent);
            }
        });
        back = (LinearLayout) findViewById(R.id.back);
         /* ******************* Fininshing Activity ***************************/
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.isInternetConnection = false;
                sharedDatabase.setPosition(1);
                sharedDatabase.setType("all");
                finish();
            }
        });
         /* *******************Button Clicking to checking status attending attended ***************************/
        attending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTodayEvent) {
                    if (isPasswordRequired) {
                        raiseAlertDilog();
                    } else {
                        if (checkInternetConnection.isConnectingToInternet()) {

                            if (rsvp_state.equals("not_attending")) {
                                params.put("state", "attended");
                            } else if (rsvp_state.equals("attending")) {
                                params.put("state", "attended");
                            }
                            if (!isGpsEnabled()) {
                                raiseLocationAlertDialog(getApplicationContext());
                            } else {
                               /* new BackgroundTaskAsync().execute("");*/
                                try {
                                    getHttpRequest();
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }

                        } else {
                            connectivityMessage("Waiting for network!");

                        }
                    }

                } else {
                    if (checkInternetConnection.isConnectingToInternet()) {

                        if (rsvp_state.equals("not_attending")) {
                            params.put("state", "attending");
                        } else if (rsvp_state.equals("attending")) {
                            params.put("state", "not_attending");
                        } else if (rsvp_state.equals("attended")) {
                            params.put("state", "not_attending");
                        }
                        if (!isGpsEnabled()) {
                            raiseLocationAlertDialog(getApplicationContext());
                        } else {
                             /* new BackgroundTaskAsync().execute("");*/
                            try {
                                getHttpRequest();
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    } else {
                        connectivityMessage("Waiting for network!");
                    }
                }
            }
        });
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);

        raiseRuntimePermisionForLocation();
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //DO OP WITH LOCATION SERVICE
            location = locationManager.getLastKnownLocation(provider);
        }

        if (location != null) {

            lattitude = location.getLatitude();
            longitude = location.getLongitude();
        }
        recyclerView = (android.support.v7.widget.RecyclerView) findViewById(R.id.recyclerview);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mCropImageView = (CropImageView) findViewById(R.id.CropImageView);

        Crop = (RelativeLayout) findViewById(R.id.crop);
        CropImageViewNo = (Button) findViewById(R.id.CropImageViewNo);

        CropImageViewNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Crop.setVisibility(View.GONE);

            }
        });
        CropImageView1 = (Button) findViewById(R.id.CropImageView1);
        image_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(EventDetailActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(EventDetailActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                }

                photoItemsArrayList = new ArrayList<PhotoItem>();
                startActivityForResult(getPickImageChooserIntent(), 200);
            }
        });
        CropImageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Bitmap cropped = mCropImageView.getCroppedImage(500, 500);
                    if (cropped != null)

                        imageupload_layout.setVisibility(View.VISIBLE);
                    Crop.setVisibility(View.GONE);

                    customAlert(cropped);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        SeeAllPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SeeAllPhotoActivity.class);
                intent.putExtra("id", id);
                startActivityForResult(intent, 2);
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

    @Override
    public void onHandleSelection() {

        Intent intent = new Intent(EventDetailActivity.this, SeeAllPhotoActivity.class);
        intent.putExtra("id", id);
        startActivityForResult(intent, 2);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 2) {
            photoItemsArrayList = new ArrayList<PhotoItem>();

            id = data.getStringExtra("MESSAGE");
            try {
                eventDetailApi();
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (resultCode == Activity.RESULT_OK) {
            Uri imageUri = getPickImageResultUri(data);
            Crop.setVisibility(View.VISIBLE);
            boolean requirePermissions = false;
            if (!requirePermissions) {
                mCropImageView.setImageUriAsync(imageUri);
            }
            if (resultCode == 0) {

                Crop.setVisibility(View.GONE);
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
      /*  final TextView addtitle=(TextView)view.findViewById(R.id.addtitle);*/
        final EditText title1 = (EditText) findViewById(R.id.titlebar);

        final Button submit = (Button) findViewById(R.id.submit);
        final Button cancel = (Button) findViewById(R.id.cancel);
        final Button yes = (Button) findViewById(R.id.yes);
        final Button no = (Button) findViewById(R.id.no);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    imageupload_layout.setVisibility(View.GONE);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    photoItemsArrayList = new ArrayList<PhotoItem>();


                    title = "";
                    submit.setVisibility(View.VISIBLE);
                    cancel.setVisibility(View.VISIBLE);
                    title1.setVisibility(View.VISIBLE);
                    titleText.setVisibility(View.GONE);
                    yes.setVisibility(View.GONE);
                  /*  addtitle.setVisibility(View.VISIBLE);*/
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
                title1.setVisibility(View.VISIBLE);
              /*  addtitle.setVisibility(View.GONE);*/

                submit.setVisibility(View.VISIBLE);
                cancel.setVisibility(View.VISIBLE);
                yes.setVisibility(View.GONE);
                titleText.setVisibility(View.GONE);
                no.setVisibility(View.GONE);
            }
        });
        imageView.setImageBitmap(bp1);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!title1.getText().toString().isEmpty()) {
                    if (checkInternetConnection.isConnectingToInternet()) {
                        try {
                            InputMethodManager inputManager = (InputMethodManager)
                                    getSystemService(Context.INPUT_METHOD_SERVICE);

                            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);
                            photoItemsArrayList = new ArrayList<PhotoItem>();
                            title = title1.getText().toString();
                            title1.getText().clear();
                            imageupload_layout.setVisibility(View.GONE);

                            saveIntoFile(bp1);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    } else {
                        connectivityMessage("Waiting for network!");
                    }


                } else {
                    titleText.setVisibility(View.VISIBLE);
                    title1.setVisibility(View.GONE);
                    submit.setVisibility(View.GONE);
                    cancel.setVisibility(View.GONE);
                    yes.setVisibility(View.VISIBLE);
                  /*  addtitle.setVisibility(View.VISIBLE);*/
                    no.setVisibility(View.VISIBLE);
                    titleText.setText(" Are you sure you dont want to add title?");
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
                    callEventPhotoAPI();
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

    public synchronized void buildClient() {

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        JSONObject props = new JSONObject();
        try {
            props.put("eventId", id);
            AppController.getInstance().getMixpanelAPI().track("EventDetail", props);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void callEventPhotoAPI() {
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
                    connectivityMessage("Updated Successfully");
                    try {
                        eventPhotoGalleriesApi();
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

  /* ******************* Event Api Parsing ***************************/


    public void eventDetailApi() {
        String authProvider = SettingsManager.getInstance().getAuthProvider();

        HashMap<String, String> paramMap = new HashMap<String, String>();
        RequestParams params = new RequestParams(paramMap);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("USER-AGENT", AppController.useragent);
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("Content-Type", "application/json");
        client.get(Constants.BASEURL + Constants.EVENT_DETAIL + id + "/", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                super.onSuccess(statusCode, headers, jsonObject);
                if (getApplicationContext() != null) {
                    updateUI(jsonObject.toString());
                    eventPhotoGalleriesApi();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                eventPhotoGalleriesApi();

                if (statusCode == 401) {
                    UserManager.getInstance().logOut(EventDetailActivity.this);
                }

                if (statusCode == 400) {
                    connectivityMessage("" + errorResponse);
                    if (getApplicationContext() != null) {
                        if (statusCode == 400) {
                            connectivityMessage("" + errorResponse);
                        }

                    }
                }
            }
        });
    }

    /* *******************Parsing Data To set Textview Imageview On Display Layout ***************************/

    public void updateUI(String jsonresponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonresponse);
            try {
                String imageUrl = Constants.BASEURL + jsonObject.getString("display");
                Glide.with(getApplicationContext()).load(imageUrl).into(backgroundImage);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
            System.out.println("json is " + jsonObject);
            isPasswordRequired = jsonObject.getBoolean("password_required");
            facilityName.setText(jsonObject.getString("name"));
            String itemDate1 = jsonObject.getString("start");
            rsvp_state = jsonObject.getString("rsvp_state");
            String enddateOfEvent = jsonObject.getString("end");
            String[] splitEnddate = enddateOfEvent.split("T");
            String eventEnddate = splitEnddate[0];
            String eventEndTime = splitEnddate[1];
            String eventdate, eventStartTime;
            String[] split = itemDate1.split("T");
            eventdate = split[0];
            eventStartTime = split[1];
            String[] splitEventEnddate = eventEnddate.split("-");
            eventEnddate = splitEventEnddate[2] + "-" + splitEventEnddate[1] + "-" + splitEventEnddate[0];
            String[] splitDate = eventdate.split("-");

            eventdate = splitDate[2] + "-" + splitDate[1] + "-" + splitDate[0];
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
            Calendar cal = Calendar.getInstance();


            String selectedMonthIndex = new SimpleDateFormat("MM").format(cal.getTime());
            String currentYear = new SimpleDateFormat("yyyy").format(cal.getTime());
            String currentDay = new SimpleDateFormat("dd").format(cal.getTime());
            String currentTime = new SimpleDateFormat("HH:mm:ss").format(cal.getTime());
            String currentDate = currentDay + "-" + selectedMonthIndex + "-" + currentYear;
            try {
                startTime = formatter1.parse(itemDate1);
                endTime = formatter1.parse(enddateOfEvent);
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
            if (startTime.after(cal.getTime())) {

                attending.setBackgroundResource(R.drawable.attending_background);
                if (rsvp_state.equals("not_attending")) {
                    attending.setText("Attend");
                    popMessage = "You are successfully attending Event";
                } else if (rsvp_state.equals("attending")) {
                    popMessage = "You have successfully Skip Event";
                    attending.setText("Attended");
                }

            } else if (cal.getTime().after(endTime)) {
                attending.setVisibility(View.GONE);
            } else {
                attending.setBackgroundResource(R.drawable.attending_background);
                if (rsvp_state.equals("not_attending")) {
                    popMessage = "You have successfully Checked In Event ";
                    attending.setText("Check In");
                } else if (rsvp_state.equals("attending")) {
                    popMessage = "You have successfully Checked In Event";
                    attending.setText("Check In");
                } else if (rsvp_state.equals("attended")) {
                    attending.setText("Checked In");
                    attending.setClickable(false);
                }
                isTodayEvent = true;
            }

            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
                String start_date = jsonObject.getString("start");
                String[] splitStartDate = start_date.split("T");

                Date date = formatter.parse(start_date);
                cal = Calendar.getInstance();
                cal.setTime(date);
                String currentMonth = new SimpleDateFormat("MMM").format(cal.getTime());
                String day = new SimpleDateFormat("dd").format(cal.getTime());
                String eventcurrentYear = new SimpleDateFormat("yyyy").format(cal.getTime());
                String[] splitStartTimeForMinuts = splitStartDate[1].split(":");
                //     startDate.setText("START : " + day+ " "+currentMonth+" "+eventcurrentYear+ " at "+splitStartTimeForMinuts[0]+":"+splitStartTimeForMinuts[1]+" PM");

                int starttimeComapare = Integer.parseInt(splitStartTimeForMinuts[0]);
                if (starttimeComapare >= 12) {
                    startDate.setText("START : " + day + " " + currentMonth + " " + eventcurrentYear + " at " + splitStartTimeForMinuts[0] + ":" + splitStartTimeForMinuts[1]);

                } else {
                    startDate.setText("START : " + day + " " + currentMonth + " " + eventcurrentYear + " at " + splitStartTimeForMinuts[0] + ":" + splitStartTimeForMinuts[1]);

                }
                //   startDate.setText("START : " + day+ " "+currentMonth+" "+eventcurrentYear+ " at "+splitStartTimeForMinuts[0]+":"+splitStartTimeForMinuts[1]);
                String end_date = jsonObject.getString("end");
                String[] splitEndDate = end_date.split("T");
                Date dateEnd = formatter.parse(end_date);
                cal = Calendar.getInstance();
                cal.setTime(dateEnd);
                String currentMonthEnd = new SimpleDateFormat("MMM").format(cal.getTime());
                String dayEnd = new SimpleDateFormat("dd").format(cal.getTime());
                String eventcurrentYearEnd = new SimpleDateFormat("yyyy").format(cal.getTime());
                String[] splitendTimeForMinuts = splitEndDate[1].split(":");
                int endtimeComapare = Integer.parseInt(splitendTimeForMinuts[0]);
                if (endtimeComapare >= 12) {
                    enddate.setText("END     : " + dayEnd + " " + currentMonthEnd + " " + eventcurrentYearEnd + " at " + splitendTimeForMinuts[0] + ":" + splitendTimeForMinuts[1]);

                } else {
                    enddate.setText("END     : " + dayEnd + " " + currentMonthEnd + " " + eventcurrentYearEnd + " at " + splitendTimeForMinuts[0] + ":" + splitendTimeForMinuts[1]);

                }
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
            address.setText(Html.fromHtml(Html.fromHtml(jsonObject.getString("address")).toString()));

            String desc = jsonObject.getString("description");
            byte ptextdesc[] = desc.getBytes(ISO_8859_1);
            String description = new String(ptextdesc, UTF_8);
            String redemptiontype = jsonObject.getString("description");
            if (Build.VERSION.SDK_INT >= 25) {
                descriptionText.setText(Html.fromHtml(redemptiontype, Html.FROM_HTML_MODE_COMPACT));
            } else {
                descriptionText.setText(Html.fromHtml(redemptiontype));
            }
            Linkify.addLinks(descriptionText, Linkify.ALL);

            JSONObject contactJsonObject = jsonObject.getJSONObject("contact_details");
            whomToContact.setText(contactJsonObject.getString("name"));
            emailAddress.setText(contactJsonObject.getString("email"));
            phoneNumber.setText(contactJsonObject.getString("phone"));
            JSONArray cooprdinatesJsonArray = jsonObject.getJSONArray("coordinates");
            cordinates = cooprdinatesJsonArray.getString(0);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
      /* *******************Location find To given Runtime Permission Using MarshMallow Devices ***************************/

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
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
    /* *******************Location find To given Runtime Permission Using MarshMallow Devices ***************************/

    public void raiseRuntimePermisionForLocation() {
        if (ContextCompat.checkSelfPermission(EventDetailActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(EventDetailActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(EventDetailActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(EventDetailActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);


            } else {
                ActivityCompat.requestPermissions(EventDetailActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);


            }
        }
    }


    protected void onStart() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        if (enable_activity_tracker.equals("0") || enable_activity_tracker.equals("")) {
            Log.d("hello", "hello");
        } else {
            try {
                if (mGoogleApiClient != null) {
                    mGoogleApiClient.connect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onStart();
    }

    protected void onStop() {
        EventBus.getDefault().unregister(this);
        if (enable_activity_tracker.equals("0") || enable_activity_tracker.equals("")) {
            Log.d("hello", "hello");
        } else {
            try {
                if (mGoogleApiClient != null) {
                    mGoogleApiClient.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
        super.onStop();
    }

    private boolean isGpsEnabled() {
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        return service.isProviderEnabled(LocationManager.GPS_PROVIDER) && service.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void raiseLocationAlertDialog(Context context) {


        AlertDialog.Builder builder = new AlertDialog.Builder(EventDetailActivity.this);

        View view = layoutInflater.inflate(R.layout.location_not_found_pop_up, null);
        Button cancel = (Button) view.findViewById(R.id.cancel);
        Button OK = (Button) view.findViewById(R.id.ok);
        //final EditText passwordEditText=(EditText)view.findViewById(R.id.event_password_edittext);
        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(myIntent);

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        builder.setView(view);


        alertDialog = builder.create();
        alertDialog.show();
    }

    public void raiseAlertDilog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(EventDetailActivity.this);
        View view = layoutInflater.inflate(R.layout.event_checkin_popup, null);
        Button cancel = (Button) view.findViewById(R.id.cancel);
        Button OK = (Button) view.findViewById(R.id.ok);
        final EditText passwordEditText = (EditText) view.findViewById(R.id.event_password_edittext);
        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                if (checkInternetConnection.isConnectingToInternet()) {

                    params.put("state", "attended");
                    params.put("password", passwordEditText.getText().toString());
                    if (passwordEditText.getText().toString().length() != 0) {
                        if (!isGpsEnabled()) {
                            raiseLocationAlertDialog(getApplicationContext());
                        } else {
                             /* new BackgroundTaskAsync().execute("");*/
                            alertDialog.dismiss();
                            getHttpRequest();

                        }
                    } else {
                        alertDialog.dismiss();
                        if (getApplicationContext() != null) {
                            connectivityMessage("Please Enter the Password");
                        }
                    }
                } else {
                    if (getApplicationContext() != null) {
                        connectivityMessage("No Internet Connection");
                    }
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        builder.setView(view);

        alertDialog = builder.create();
        alertDialog.show();


    }


    public void eventPhotoGalleriesApi() {
        String authProvider = SettingsManager.getInstance().getAuthProvider();

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
                    photoItemsArrayList = new ArrayList<PhotoItem>();
                    parsePhotoGalleryImageData(jsonObject.toString());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if (getApplicationContext() != null) {
                    if (getApplicationContext() != null) {

                        if (statusCode == 400) {
                            Toast.makeText(getApplicationContext(), "" + errorResponse, Toast.LENGTH_LONG).show();
                        }
                        if (photoItemsArrayList.size() != 0) {

                            linearlayout.setVisibility(View.VISIBLE);
                            SeeAllPhoto.setVisibility(View.VISIBLE);
                        } else {

                            linearlayout.setVisibility(View.GONE);
                            SeeAllPhoto.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });
    }


    public void parsePhotoGalleryImageData(String jsonobject) {
        try {
            PhotoItem photoItem;
            JSONObject jsonObject = new JSONObject(jsonobject);
            JSONArray userPhotosJsonArray = jsonObject.getJSONArray("user_photos");
            for (int i = 0; i < userPhotosJsonArray.length(); i++) {
                JSONObject userPhotosJsonObject = userPhotosJsonArray.getJSONObject(i);
                String displayImage = userPhotosJsonObject.getString("thumbnail_url");
                photoItem = new PhotoItem(displayImage, "", "", "", id);
                photoItemsArrayList.add(photoItem);
            }
            JSONArray organizerPhotosJsonArray = jsonObject.getJSONArray("organizer_photos");

            for (int i = 0; i < organizerPhotosJsonArray.length(); i++) {
                JSONObject organizationPhotosJsonObject = organizerPhotosJsonArray.getJSONObject(i);
                String displayImage = organizationPhotosJsonObject.getString("thumbnail_url");
                String title = organizationPhotosJsonObject.getString("title");

                String imageThumbnail = organizationPhotosJsonObject.getString("display_url");
                JSONObject ownerJsonObject = organizationPhotosJsonObject.getJSONObject("owner");
                String fullName = ownerJsonObject.getString("full_name");

                photoItem = new PhotoItem(displayImage, imageThumbnail, fullName, title, id);
                photoItemsArrayList.add(photoItem);

            }

            if (photoItemsArrayList.size() != 0) {

                linearlayout.setVisibility(View.VISIBLE);
                SeeAllPhoto.setVisibility(View.VISIBLE);
            } else {

                linearlayout.setVisibility(View.GONE);
                SeeAllPhoto.setVisibility(View.GONE);
            }
            SeeAllPhoto.setText("See All Photo  (" + photoItemsArrayList.size() + ")");
            imageAdapter = new ImageAdapter(EventDetailActivity.this, photoItemsArrayList);
            recyclerView.setAdapter(imageAdapter);

        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

      /* ********************* Checking Button To Clicking get Response Api *************************/

    public void getHttpRequest() {
//        SharedPreferences sharedPreferencesToken = getApplicationContext().getSharedPreferences("token", 1);
//        String token = sharedPreferencesToken.getString("token", "");

        SharedDatabase sharedDatabase = new SharedDatabase(EventDetailActivity.this);
        String token = sharedDatabase.getToken();

        StringEntity entity = null;
        JSONObject jsonObject11 = null;
        try

        {
            jsonObject11 = new JSONObject(params);
            entity = new StringEntity(jsonObject11.toString());
        } catch (
                Exception e
                )

        {
            e.printStackTrace();
        }

        String authProvider = SettingsManager.getInstance().getAuthProvider();

        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.addHeader("USER-AGENT", AppController.useragent);
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("connection", "Keep-Alive");
        client.setTimeout(800000);
        String url = Constants.BASEURL + Constants.EVENT_DETAIL + id + "/rsvp/";
        client.post(EventDetailActivity.this, url, entity, "application/json", new

                TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        if (getApplicationContext() != null) {
                            try {

                                JSONObject jsonObject = new JSONObject(res);
                                String jsonresponcestring = jsonObject.getString("state");
                                if (jsonresponcestring.equals("attending")) {
                                    String response = jsonObject.getString("state");
                                    response = response.substring(0, 1).toUpperCase() + response.substring(1).toLowerCase();
                                    Toast.makeText(getApplicationContext(), "Attendance Confirmed", Toast.LENGTH_LONG).show();
                                } else if (jsonresponcestring.equals("attended")) {
                                    Toast.makeText(getApplicationContext(), "Check In Confirmed", Toast.LENGTH_LONG).show();

                                } else if (jsonresponcestring.equals("not_attending")) {
                                    Toast.makeText(getApplicationContext(), "RSVP Withdrawn", Toast.LENGTH_LONG).show();

                                }
                            } catch (JSONException ex) {
                                JSONArray jsonArray = null;
                                String jsonBadResponse = null;
                                ex.printStackTrace();
                                try {
                                    jsonArray = new JSONArray(res);
                                    jsonBadResponse = jsonArray.getString(0);
                                    Toast.makeText(getApplicationContext(), jsonBadResponse, Toast.LENGTH_LONG).show();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                ex.printStackTrace();

                            }
                            try {
                                eventDetailApi();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        System.out.println("hheehgjh fail" + res);
                        if (getApplicationContext() != null) {
                            try {

                                JSONObject jsonObject = new JSONObject(res);
                                String jsonresponcestring = jsonObject.getString("state");
                                if (jsonresponcestring.equals("attending")) {
                                    String response = jsonObject.getString("state");
                                    response = response.substring(0, 1).toUpperCase() + response.substring(1).toLowerCase();
                                    Toast.makeText(getApplicationContext(), "Attendance Confirmed", Toast.LENGTH_LONG).show();
                                } else if (jsonresponcestring.equals("attended")) {
                                    Toast.makeText(getApplicationContext(), "Check In Confirmed", Toast.LENGTH_LONG).show();

                                } else if (jsonresponcestring.equals("not_attending")) {
                                    Toast.makeText(getApplicationContext(), "RSVP Withdrawn", Toast.LENGTH_LONG).show();

                                }
                            } catch (JSONException ex) {
                                JSONArray jsonArray = null;
                                String jsonBadResponse = null;
                                ex.printStackTrace();
                                try {
                                    jsonArray = new JSONArray(res);
                                    jsonBadResponse = jsonArray.getString(0);
                                    Toast.makeText(getApplicationContext(), jsonBadResponse, Toast.LENGTH_LONG).show();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                ex.printStackTrace();

                            }
                            eventDetailApi();

                            if (statusCode == 500) {
                                if (getApplicationContext() != null) {
                                    Toast.makeText(getApplicationContext(), "We've encountered a technical error.our team is working on it. please try again later", Toast.LENGTH_LONG).show();
                                }
                            }

                        }
                    }
                }
        );
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (!isGpsEnabled()) {
            raiseLocationAlertDialog(getApplicationContext());
        }
        if (mLastLocation != null) {
            try {

                facilityLatitude = mLastLocation.getLatitude();
                facilityLongitude = mLastLocation.getLongitude();
                DecimalFormat df = new DecimalFormat("#.##########");
                facilityLatitude = Double.valueOf(df.format(facilityLatitude));
                facilityLongitude = Double.valueOf(df.format(facilityLongitude));
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            }
            // params.put("coordinates" ,"77.2809912464,28.5361114663");
            params.put("coordinates", facilityLongitude.toString() + "," + facilityLatitude.toString());
        } else {
            params.put("coordinates", "0.8534757,0.645645");
            //  Toast.makeText(getApplicationContext(),"Location Not Found",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Subscribe
    public void onRefreshTokenEvent(RefreshTokenEvent event) {
        if (event.message == null) {
            eventDetailApi();
        } else {
            android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(this).create();
            alertDialog.setTitle("Error");
            alertDialog.setMessage(event.message);
            alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_NEUTRAL, "Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    UserManager.getInstance().logOut(EventDetailActivity.this);
                }
            });
        }
    }


    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */

}

