package activity.userprofile;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.system.ErrnoException;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.root.skor.BuildConfig;
import com.root.skor.R;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import InternetConnection.CheckInternetConnection;
import constants.Constants;
import database.SharedDatabase;
import event.RefreshTokenEvent;
import singleton.SettingsManager;
import utils.AppController;
import utils.CircleImageView;
import utils.Loader;

public class EditProfile extends FragmentActivity implements DatePickerDialog.OnDateSetListener {
    public static JSONObject jsonObject;
    public SharedDatabase sharedDatabase;
    public String token;
    ImageView back_button, firstnameImage, lastnameImage, emailImage, mobileImage;
    TextView dob, changePhoto;
    File filePath = null;
    Spinner martialstatusspinner;
    CircleImageView thumbnail_pic1;
    ImageView companyImage, address1Image, dobImage, departmentmage;
    EditText first_name1, last_name1, email1, phone_number1, organization_name1, address1, DD, MM, YYYY;
    TextView department;
    CheckInternetConnection checkInternetConnection;
    Button CropImageViewNo, CropImageView1;
    CropImageView mCropImageView;
    String dateOFBirth = "--";
    TextView saveProfile;
    String martialStatus = "Single";
    RelativeLayout dateLayout, Crop, relativeLayoutvisiblity;
    String dateBirth = "";
    Uri mCropImageUri;
    ArrayAdapter<CharSequence> adapter;

    public static String mobilenumber;
    public static final String DATEPICKER_TAG = "datepicker";

    String first_name, last_name, phone_number, organization_name, email;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedDatabase = new SharedDatabase(getApplicationContext());
        token = sharedDatabase.getToken();
        setContentView(R.layout.activity_editprofile);
        final Calendar calendar = Calendar.getInstance();
        raiseRuntimePermisionForLocation();
        final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), false);
        if (savedInstanceState != null) {
            DatePickerDialog dpd = (DatePickerDialog) getSupportFragmentManager().findFragmentByTag(DATEPICKER_TAG);
            if (dpd != null) {
                dpd.setOnDateSetListener(this);
            }
        }

        first_name1 = (EditText) findViewById(R.id.firstname);
        last_name1 = (EditText) findViewById(R.id.lastName);
        checkInternetConnection = new CheckInternetConnection(getApplicationContext());
        email1 = (EditText) findViewById(R.id.email);
        department = (TextView) findViewById(R.id.department);
        YYYY = (EditText) findViewById(R.id.YYYY);
        MM = (EditText) findViewById(R.id.MM);
        DD = (EditText) findViewById(R.id.DD);
        dob = (TextView) findViewById(R.id.dob);
        dateLayout = (RelativeLayout) findViewById(R.id.datelayout);
        phone_number1 = (EditText) findViewById(R.id.mobile);
        organization_name1 = (EditText) findViewById(R.id.comapnyName);
        address1 = (EditText) findViewById(R.id.address1);
        saveProfile = (TextView) findViewById(R.id.save);
        companyImage = (ImageView) findViewById(R.id.companyImage);
        back_button = (ImageView) findViewById(R.id.back);
        thumbnail_pic1 = (CircleImageView) findViewById(R.id.imageurl);
        firstnameImage = (ImageView) findViewById(R.id.firstnameImage);
        lastnameImage = (ImageView) findViewById(R.id.lastnameImage);
        emailImage = (ImageView) findViewById(R.id.emailImage);
        mobileImage = (ImageView) findViewById(R.id.mobileImage);
        address1Image = (ImageView) findViewById(R.id.address1Image);
        dobImage = (ImageView) findViewById(R.id.dobImage);
        departmentmage = (ImageView) findViewById(R.id.departmentmage);
        jsonObject = new JSONObject();
        changePhoto = (TextView) findViewById(R.id.changePhoto);
        martialstatusspinner = (Spinner) findViewById(R.id.martial_status);
        adapter = ArrayAdapter.createFromResource(this,
                R.array.martial_sttaus, R.layout.spinner_text);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        martialstatusspinner.setAdapter(adapter);
        first_name1.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        last_name1.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        email1.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        department.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        YYYY.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        MM.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        DD.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        dob.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        phone_number1.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        organization_name1.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        address1.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        changePhoto.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        mCropImageView = (CropImageView) findViewById(R.id.CropImageView);
        relativeLayoutvisiblity = (RelativeLayout) findViewById(R.id.rele);

        if (checkInternetConnection.isConnectingToInternet()) {
            try {
                callProfileAPI();
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            connectivityMessage("Waiting for Network!");
        }
        Crop = (RelativeLayout) findViewById(R.id.crop);
        CropImageViewNo = (Button) findViewById(R.id.CropImageViewNo);

        /* ****************Button  Clicking to Cropping ImageView Api******************************/

        CropImageViewNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Crop.setVisibility(View.GONE);
                relativeLayoutvisiblity.setVisibility(View.VISIBLE);
                try {
                    callProfileAPI();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        CropImageView1 = (Button) findViewById(R.id.CropImageView1);

        /* ****************Image Crooping  Listener******************************/

        CropImageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Bitmap cropped = mCropImageView.getCroppedImage(500, 500);
                    if (cropped != null)

                        Crop.setVisibility(View.GONE);
                    relativeLayoutvisiblity.setVisibility(View.VISIBLE);
                    saveIntoFile(cropped);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        if (getIntent().getStringExtra("thumbnail_pic1") != null) {
            Glide.with(getApplicationContext())
                    .load(getIntent().getStringExtra("thumbnail_pic"))
                    .apply(RequestOptions.placeholderOf(R.drawable.no_image))
                    .apply(RequestOptions.errorOf(R.drawable.no_image))
                    .into(thumbnail_pic1);
        } else {
        }


        if (BuildConfig.FLAVOR.equals("SKOR")) {
            dateLayout.setEnabled(true);
        } else {
            dateLayout.setEnabled(false);
        }

        dateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        EditProfile.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );

                dpd.show(getSupportFragmentManager(), "Datepickerdialog");
            }
        });
        /* ****************Martial Status Sppiner Adaptor******************************/

        martialstatusspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] martialStatusArray = getResources().getStringArray(R.array.martial_sttaus);
                martialStatus = martialStatusArray[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Button edit = (Button) findViewById(R.id.saveButton);

        /* ****************Save Button Clicking Api Listener******************************/

        saveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetConnection.isConnectingToInternet()) {
                    try {
                        saveProfile();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    connectivityMessage("Waiting for Network!");
                }
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    saveProfile();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        LinearLayout back = (LinearLayout) findViewById(R.id.back1);
        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                /*finish();*/
                MainActivity.isInternetConnection = false;
                Intent intent = new Intent(getApplicationContext(), MyProfile.class);
                startActivity(intent);
                finish();
            }
        });

        /* ****************text  clicking Listener to change Imageview Image on gallery Camera******************************/

        changePhoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                startActivityForResult(getPickImageChooserIntent(), 200);
                //showAddProfilePicDialog();

            }
        });

        /* ****************ImageView Clicking Listener to change Imageview Image on gallery or camera******************************/

        thumbnail_pic1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //  showAddProfilePicDialog();
                startActivityForResult(getPickImageChooserIntent(), 200);
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
/* ****************Cropping Image On Perfoming Action ******************************/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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

/* **************** clicking Listener perfoming to get Image on Croppinng******************************/

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

    /**
     * Get URI to image received from capture  by camera.
     */
    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "pickImageResult.jpeg"));
        }
        return outputFileUri;
    }

    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null && data.getData() != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

    /**
     * Test if we can open the given Android URI to test if permission required error is thrown.<br>
     */
    public boolean isUriRequiresPermissions(Uri uri) {
        try {
            ContentResolver resolver = getContentResolver();
            InputStream stream = resolver.openInputStream(uri);
            stream.close();
            return false;
        } catch (FileNotFoundException e) {
            if (e.getCause() instanceof ErrnoException) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }
/* ****************clicking in save button to calling Sav Api******************************/

    public void saveProfile() {
        CheckInternetConnection checkInternetConnection = new CheckInternetConnection(getApplicationContext());
        if (checkInternetConnection.isConnectingToInternet()) {
            first_name = first_name1.getText().toString();
            last_name = last_name1.getText().toString();
            email = email1.getText().toString();
            phone_number = phone_number1.getText().toString();
            mobilenumber = phone_number;
            sharedDatabase.setMobileNumber(mobilenumber);
            organization_name = organization_name1.getText().toString();
            if (!first_name.equals("") || !last_name.equals("") || !email.equals("") || !organization_name.equals("")) {
                if (!phone_number.equals("") && !phone_number.equals(null)) {
                    if (phone_number.length() > 15) {
                        if (getApplicationContext() != null) {
                            connectivityMessage( "Enter valid phone Number.");
                        }
                        return;
                    } else {
                        if (checkInternetConnection.isConnectingToInternet()) {
                            try {
                                if (!dateOFBirth.equals("--")) {
                                    callProfileEditAPI();
                                } else {
                                    if (getApplicationContext() != null) {
                                        connectivityMessage("Please enter your DOB");
                                    }
                                }
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            connectivityMessage("Waiting for Network!");
                        }
                    }
                } else {
                    if (getApplicationContext() != null) {
                        connectivityMessage( "Please Enter Your Phone Number.");

                    }
                }

            } else {
                if (getApplicationContext() != null) {
                    connectivityMessage( "Please Fill all the fields");                }
            }
        } else {
            connectivityMessage("Waiting for Network!");
        }
    }
/* ****************Calling Edit Profile Api to set dat On Layout******************************/

    public void callProfileAPI() {
        String authProvider = SettingsManager.getInstance().getAuthProvider();

        Loader.showProgressDialog(this);
        HashMap<String, String> paramMap = new HashMap<String, String>();
        RequestParams params = new RequestParams(paramMap);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(80000);
        client.addHeader("USER-AGENT", AppController.useragent);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("Content-Type", "application/json");
        client.get(Constants.BASEURL + Constants.EDITPROFILE, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject jsonObject) {
                super.onSuccess(statusCode, headers, jsonObject);
                AppController.getInstance().getMixpanelAPI().track("EditProfile");
                Loader.dialogDissmiss(getApplicationContext());
                try {

                    first_name1.setText(jsonObject.getString("first_name"));
                    phone_number1.setText(jsonObject.getString("phone_number"));
                    last_name1.setText(jsonObject.getString("last_name"));
                    address1.setText(jsonObject.getString("address"));
                    email1.setText(jsonObject.getString("email"));
                    sharedDatabase.setMobileNumber(jsonObject.getString("phone_number"));
                    organization_name1.setText(jsonObject.getString("organization_name"));
                    dateOFBirth = jsonObject.getString("date_of_birth");
                    String searchedItem = jsonObject.getString("martial_status");
                    int itemPosition = adapter.getPosition(searchedItem);
                    if (itemPosition == -1) {
                    } else {
                        martialstatusspinner.setSelection(itemPosition);
                    }
                    String skorDepartment = jsonObject.getString("department_name");
                    if (!skorDepartment.equals("null")) {
                        department.setText(skorDepartment);
                    } else {
                        department.setText("");
                    }

                    if (!dateOFBirth.equals("null")) {
                        String[] splitDateOfBirth = dateOFBirth.split("-");
                        YYYY.setText(splitDateOfBirth[0]);
                        MM.setText(splitDateOfBirth[1]);
                        DD.setText(splitDateOfBirth[2]);
                        dateOFBirth = splitDateOfBirth[0] + "-" + splitDateOfBirth[1] + "-" + splitDateOfBirth[2];
                    } else {
                        dateOFBirth = "--";

                    }

                    Glide.with(getApplicationContext()).load(Constants.BASEURL + jsonObject.getString("profile_pic_url")).into(thumbnail_pic1);

                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Loader.dialogDissmiss(getApplicationContext());
                if (statusCode == 400) {
                    if (getApplicationContext() != null) {
                        connectivityMessage( "" + errorResponse);
                    }
                }
                if (statusCode == 500) {
                    if (getApplicationContext() != null) {
                        connectivityMessage( "We've encountered a technical error.our team is working on it. please try again later");
                    }
                }
            }
        });
    }
/* ****************Sending Data On Server To post Api ******************************/

    public void callProfileEditAPI() {

        Loader.showProgressDialog(this);
        RequestParams paramMap = new RequestParams();
        paramMap.put("email", email1.getText().toString());
        paramMap.put("phone_number", phone_number1.getText().toString());
        paramMap.put("organization_name", organization_name1.getText().toString());
        paramMap.put("first_name", first_name1.getText().toString());
        paramMap.put("last_name", last_name1.getText().toString());
        paramMap.put("address", address1.getText().toString());
        sharedDatabase.setMobileNumber(phone_number1.getText().toString());
        sharedDatabase.setEmail(email1.getText().toString());
        paramMap.put("martial_status", martialStatus.toLowerCase());
        if (dateOFBirth.equals("")) {
            dateOFBirth = YYYY.getText().toString() + "-" + MM.getText().toString() + "-" + DD.getText().toString();
            paramMap.put("date_of_birth", dateOFBirth);
        } else {
            dateOFBirth = YYYY.getText().toString() + "-" + MM.getText().toString() + "-" + DD.getText().toString();
            paramMap.put("date_of_birth", dateOFBirth);
        }

        if (filePath != null) {
            try {
                paramMap.put("profile_pic", filePath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        String authProvider = SettingsManager.getInstance().getAuthProvider();

        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("USER-AGENT", AppController.useragent);
        client.addHeader("Authorization", authProvider + " " + token);
        client.put(Constants.BASEURL + Constants.EDITPROFILE, paramMap, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject jsonObject) {
                super.onSuccess(statusCode, headers, jsonObject);
                if (getApplicationContext() != null) {
                    Loader.dialogDissmiss(getApplicationContext());
                    connectivityMessage( "Updated Successfully");
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if (getApplicationContext() != null) {
                    Loader.dialogDissmiss(getApplicationContext());
                    if (statusCode == 500) {
                        if (getApplicationContext() != null) {
                            connectivityMessage("We've encountered a technical error.our team is working on it. please try again later");
                        }
                    }
                }
            }
        });
    }
/* ****************Peconverting image bitmap to file******************************/


    public void saveIntoFile(Bitmap bitmap) throws IOException {
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
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, fileOutputStream);
        fileOutputStream.flush();
        fileOutputStream.close();
        thumbnail_pic1.setImageBitmap(bitmap);

        filePath = new File(mainfile.toString());
        //  Toast.makeText(getApplicationContext(),"Profile  Saved",Toast.LENGTH_LONG).show();


        System.out.println("File path is " + filePath.toString());

    }
/* **************** Run Time Permission to read and Write External Device in marshMallow******************************/

    public void raiseRuntimePermisionForLocation() {
        if (ContextCompat.checkSelfPermission(EditProfile.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(EditProfile.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(EditProfile.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {

                ActivityCompat.requestPermissions(EditProfile.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);

            }
        }
    }

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
        if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mCropImageView.setImageUriAsync(mCropImageUri);
        } else {
            if (getApplicationContext() != null) {
                connectivityMessage("Required permissions are not granted");
            }
        }
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {

        try {
            Calendar c = Calendar.getInstance();
            int currentMonth1 = c.get(Calendar.MONTH) + 1;
            int currentDay = c.get(Calendar.DATE);
            int currentYear = c.get(Calendar.YEAR);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date currentDate = df.parse(currentYear + "-" + (currentMonth1 - 1) + "-" + currentDay);
            Date selectedDate = df.parse(year + "-" + month + "-" + day);
            if (selectedDate.compareTo(currentDate) < 0) {
                dateOFBirth = String.valueOf(year) + "-" + String.valueOf(++month) + "-" + String.valueOf(day);
                DD.setText(String.valueOf(day));
                MM.setText(String.valueOf(month));
                YYYY.setText(String.valueOf(year));
            }
            if (selectedDate.compareTo(currentDate) > 0) {
                dateOFBirth = "--";
                DD.setText("");
                MM.setText("");
                YYYY.setText("");
                if (getApplicationContext() != null) {
                    connectivityMessage("You can't Select future date");
                }
            }
            if (selectedDate.compareTo(currentDate) == 0) {
                dateOFBirth = String.valueOf(year) + "-" + String.valueOf(++month) + "-" + String.valueOf(day);
                DD.setText(String.valueOf(day));
                MM.setText(String.valueOf(month));
                YYYY.setText(String.valueOf(year));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }


    @Override
    public void onBackPressed() {
//        Intent intent = new Intent();
//        intent.setAction(Intent.ACTION_MAIN);
//        intent.addCategory(Intent.CATEGORY_HOME);
//        startActivity(intent);
        MainActivity.isInternetConnection = false;
        Intent intent = new Intent(getApplicationContext(), MyProfile.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
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
            callProfileAPI();
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