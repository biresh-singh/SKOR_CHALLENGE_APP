package activity.rewardz;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.root.skor.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import InternetConnection.CheckInternetConnection;
import activity.userprofile.MainActivity;
import adaptor.SelectLocationsAdapter;
import bean.RewardContactDetail;
import bean.RewardLocation;
import bean.RewardzListItem;
import constants.Constants;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import database.SharedDatabase;
import event.RefreshTokenEvent;
import fragment.PushNotificationListFragment;
import me.dm7.barcodescanner.core.ViewFinderView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import singleton.SettingsManager;
import singleton.UserManager;
import utils.AppController;
import utils.Loader;

public class RewardzDetailActivity extends Activity implements ZXingScannerView.ResultHandler{
    TextView vendePass;
    EditText passCode;
    TextView text1;
    ImageView mobileImage;
    Button submit;
    Button cancel;
    Button confirm;
    View passcodeView;
    TextView submit1;
    String jsonresponse;
    Boolean isUsedValidation = false;
    TextView thankyou, mobile_text1;
    ImageView backgroundImage;
    TextView website;
    FrameLayout qrCodeFrameLayout;
    View qrCodeView;
    ZXingScannerView zXingScannerView;
    public static TextView descriptionText;
    public static ArrayList<Double> latitudeCoordinateArray = new ArrayList<>();
    public static ArrayList<Double> longitudeCoordinateArray = new ArrayList<>();
    ArrayList<String> currentLocationLatLongHashMap = new ArrayList<>();
    String redemptionType = "";
    String category = "";
    int type = 0;
    String id;
    int value;
    String finalRedeemCode;
    ImageView facilityWeblink;
    String res1;
    TextView termsandConditions;
    int JsonArrayLocationResposeLength;
    String boucherRewardz;
    public static int selectedIndex = 0;
    LinearLayout dialNumber;
    String mobileNumber;
    TextView facilityName;
    LinearLayout back;
    AlertDialog alertDialog, alertDialog1, alertDialog2;
    Button redeem;
    SpannableString str;
    TextView readmodre;
    public ViewPager viewPager;
    TextView whomToContact, phoneNumber;
    public static TextView emailAddress;
    String pk;
    String emailSet;
    String descriptionS;
    WebView wv1;
    TextView presslink;
    String mobileNumberLogin;
    JSONArray locationJsonArray1;
    StringEntity entity;
    ImageView downarrow1;
    String venderPasscode;
    String email;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    JSONObject locationJsonArray;
    public CheckInternetConnection checkInternetConnection;
    TextView validTill;
    String name;
    String points;
    public static JSONObject jsonObject = null;
    ArrayList<String> mobileNumberArray = new ArrayList<>();
    ArrayList<String> addressArray = new ArrayList<>();
    public static TextView gold_gym, description_textview1;
    RelativeLayout addressLayout;
    public final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9+._%-+]{1,256}" +
                    "@" +
                    "[a-zA-Z0-9][a-zA-Z0-9-]{0,64}" +
                    "(" +
                    "." +
                    "[a-zA-Z0-9][a-zA-Z0-9-]{0,25}" +
                    ")+"
    );
    public SharedDatabase sharedDatabase;
    public String token;
    RewardzListItem rewardzObject;
    String viewType = "";
    Boolean mredeemableFlag;

    SelectLocationsAdapter selectLocationsAdapter;
    FrameLayout selectLocationFrameLayout;
    ListView locationsListView;
    FrameLayout cancelFrameLayout, okFrameLayout;
    List<RewardLocation> rewardLocationList = new ArrayList<>();
    int selectedLocationPosition = 0;
    int selectedLocationPositionTemp = 0;
    RewardLocation selectedLocation;

    AppBarLayout appBarLayout;
    NestedScrollView nestedScrollView;
    RelativeLayout mainRelativeLayout;
    ImageView directionImageView;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            selectedIndex = data.getIntExtra("position", 0);
            try {

                JSONObject locationJsonObject = locationJsonArray1.getJSONObject(selectedIndex);

                JSONObject contactJsonObject = locationJsonObject.getJSONObject("contact_details");
                emailSet = contactJsonObject.getString("email");
                phoneNumber.setText(contactJsonObject.getString("phone"));
                descriptionText.setText(locationJsonObject.getString("address"));
                if (!emailSet.equals(" ")) {
                    emailAddress.setText(emailSet);
                } else {
                    emailAddress.setText("N/A");
                }

            } catch (JSONException ex) {
                ex.printStackTrace();

            }


        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rewardz_detail);

        LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        backgroundImage = (ImageView) findViewById(R.id.backgroundimage);
        termsandConditions = (TextView) findViewById(R.id.terms_conditions);
        dialNumber = (LinearLayout) findViewById(R.id.dial);
        facilityName = (TextView) findViewById(R.id.title);
        validTill = (TextView) findViewById(R.id.valid);
        addressLayout = (RelativeLayout) findViewById(R.id.address_layout);
        presslink = (TextView) findViewById(R.id.press_link);
        gold_gym = (TextView) findViewById(R.id.gold_gym);
        sharedDatabase = new SharedDatabase(getApplicationContext());
        token = sharedDatabase.getToken();
        description_textview1 = (TextView) findViewById(R.id.description_textview1);
        wv1 = (WebView) findViewById(R.id.webView);
        whomToContact = (TextView) findViewById(R.id.whom_to_contact);
        phoneNumber = (TextView) findViewById(R.id.phonenumber);
        emailAddress = (TextView) findViewById(R.id.email);
        redeem = (Button) findViewById(R.id.redeem);
        redeem.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));

        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        nestedScrollView = (NestedScrollView) findViewById(R.id.rewardz_detail_mainNestedScrollView);
        mainRelativeLayout = (RelativeLayout) findViewById(R.id.rewardz_detail_mainRelativeLayout);

        selectLocationFrameLayout = (FrameLayout) findViewById(R.id.rewardz_detail_locationFrameLayout);
        locationsListView = (ListView) findViewById(R.id.rewardz_detail_locationsListView);
        cancelFrameLayout = (FrameLayout) findViewById(R.id.rewardz_detail_cancelButton);
        okFrameLayout = (FrameLayout) findViewById(R.id.rewardz_detail_okButton);
        directionImageView = (ImageView) findViewById(R.id.rewardz_detail_directionImageView);

        selectLocationsAdapter = new SelectLocationsAdapter(this, listener);
        locationsListView.setAdapter(selectLocationsAdapter);
        locationsListView.setDivider(null);

        facilityWeblink = (ImageView) findViewById(R.id.facility_weblink);
        website = (TextView) findViewById(R.id.website);
        readmodre = (TextView) findViewById(R.id.readmore);
        descriptionText = (TextView) findViewById(R.id.description_textview);
        descriptionText.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        gold_gym.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        description_textview1.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        id = getIntent().getStringExtra("id");
        rewardzObject = (RewardzListItem) getIntent().getSerializableExtra("rewardzobject");

        if (getIntent().hasExtra("viewType")) {
            viewType = getIntent().getStringExtra("viewType");
        } else {
            viewType = "";
        }

        if (viewType.equalsIgnoreCase("nearMe")) {
            pk = String.valueOf(rewardzObject.reward);
        } else {
            pk = getIntent().getStringExtra("pk");
        }

        if (getIntent().getExtras().getBoolean("isFromNearMe")) {
            mredeemableFlag = true;
        }

        mobileNumber = sharedDatabase.getMobileNumber();
        email = sharedDatabase.getEmail();
        mobileNumberLogin = mobileNumber;
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        checkInternetConnection = new CheckInternetConnection(getApplicationContext());
        if (checkInternetConnection.isConnectingToInternet()) {
            try {
                rewardzDetailApi();
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            connectivityMessage("Waiting for Network!");
        }
        viewPager = (ViewPager) findViewById(R.id.pager);
        str = new SpannableString("Press Link for more detail");
        str.setSpan(new UnderlineSpan(), 0, str.length(), Spanned.SPAN_PARAGRAPH);
        PushNotificationListFragment.ismatchpk = false;
        presslink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetConnection.isConnectingToInternet()) {
                    Intent intent = new Intent(RewardzDetailActivity.this, WebviewBrochureActivity.class);
                    intent.putExtra("brochureURL", boucherRewardz);
                    startActivity(intent);
//                    if (boucherRewardz.contains(".jpg") || boucherRewardz.contains(".png") || boucherRewardz.contains("jpeg")) {
//                        try {
//                            new DownloadImagesTask(RewardzDetailActivity.this).execute(boucherRewardz);
//                        } catch (NullPointerException e) {
//                            e.printStackTrace();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//
//                    } else {
//                        try {
//                            new DownloadShowPdf(RewardzDetailActivity.this).execute(boucherRewardz);
//                        } catch (NullPointerException e) {
//                            e.printStackTrace();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                    }
                } else {
                    connectivityMessage("Waiting for Network!");
                }

            }
        });

        downarrow1 = (ImageView) findViewById(R.id.down_arrow1);
        website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!website.getText().toString().equals("N/A")) {
                    Intent webLinkIntent = new Intent(Intent.ACTION_VIEW);

                    webLinkIntent.setData(Uri.parse(website.getText().toString()));
                    startActivity(webLinkIntent);
                }

            }
        });

        phoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mobileNumberArray.size() > 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RewardzDetailActivity.this);
                    builder.setTitle("Please select any mobile number for call");
                    LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext()
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view = layoutInflater.inflate(R.layout.mobile_item_layout, null);
                    ListView mobileItemListView = (ListView) view.
                            findViewById(R.id.mobile_item_listview);
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                            (RewardzDetailActivity.this, android.R.layout.
                                    simple_spinner_item, mobileNumberArray);
                    mobileItemListView.setAdapter(dataAdapter);

                    mobileItemListView.setOnItemClickListener(new AdapterView.
                            OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            String mobNumber = mobileNumberArray.get(position);
                            Intent intent = new Intent(Intent.ACTION_DIAL,
                                    Uri.fromParts("tel", mobNumber, null));
                            startActivity(intent);
                            alertDialog.dismiss();
                        }
                    });
                    builder.setView(view);
                    builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }

                    });
                    alertDialog = builder.create();
                    alertDialog.show();


                } else {
                    Intent intent = new Intent(Intent.ACTION_DIAL,
                            Uri.fromParts("tel", phoneNumber.getText().toString(), null));
                    startActivity(intent);
                }
            }
        });

        emailAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]
                        {emailAddress.getText().toString()});
                i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
                i.putExtra(Intent.EXTRA_TEXT, "body of email");
                i.putExtra(Intent.EXTRA_TEXT, R.string.intent_message);
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    if (getApplicationContext() != null) {
                        connectivityMessage(
                                "There are no email clients installed.");
                    }
                }
            }
        });

        cancelFrameLayout.setOnClickListener(cancelSelectLocationListener);
        okFrameLayout.setOnClickListener(okSelectLocationListener);
        directionImageView.setOnClickListener(directionImageViewListener);

        downarrow1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appBarLayout.setEnabled(false);
                nestedScrollView.setEnabled(false);
                mainRelativeLayout.setEnabled(false);

                selectLocationFrameLayout.setVisibility(View.VISIBLE);
                selectLocationsAdapter.updateAdapter(rewardLocationList, selectedLocationPosition);

//                if (addressArray.size() > 1) {
//                    Intent intent = new Intent(getApplicationContext(),
//                            MultipleAddressActivity.class);
//                    intent.putStringArrayListExtra("address_array",
//                            addressArray);
//                    startActivityForResult(intent, 100);
//                }
            }
        });

//        addressLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (addressArray.size() > 1) {
//                    Intent intent = new Intent(getApplicationContext(),
//                            MultipleAddressActivity.class);
//                    intent.putStringArrayListExtra("address_array",
//                            addressArray);
//                    startActivityForResult(intent, 100);
//                }
//            }
//        });
        back = (LinearLayout) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.isInternetConnection = false;
                sharedDatabase.setPosition(1);
                sharedDatabase.setType("all");
                finish();
            }
        });
        redeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetConnection.isConnectingToInternet()) {
                    try {
                        callRedeemApi();
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

    }

    /* ********************calling redemption type api and Alert Dialog and redemption type Api for get method**************************/

    public void callRedeemApi() {
        final LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (redemptionType.equals("flash_password")) {
            if(isUsedValidation){

            }
            AlertDialog.Builder builder = new AlertDialog.Builder(RewardzDetailActivity.this);
            passcodeView = layoutInflater.inflate(R.layout.dialog_vendor_passcode_input, null);
            vendePass = (TextView) passcodeView.findViewById(R.id.vender_Pass);
            passCode = (EditText) passcodeView.findViewById(R.id.pass_code);
            text1 = (TextView) passcodeView.findViewById(R.id.text1);
            submit = (Button) passcodeView.findViewById(R.id.submit);
            qrCodeFrameLayout = (FrameLayout) passcodeView.findViewById(R.id.qr_code_image);
            RelativeLayout imageView = (RelativeLayout) passcodeView.findViewById(R.id.logo);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog1.dismiss();
                }
            });
            builder.setView(passcodeView);
            alertDialog1 = builder.create();
            alertDialog1.show();
            qrCodeFrameLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final LayoutInflater layoutInflaterQRCode = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                    qrCodeView = layoutInflaterQRCode.inflate(R.layout.layout_qr_scan,null);
                    final FrameLayout backFrameLayout = qrCodeView.findViewById(R.id.layout_qr_scan_backFrameLayout);
                    final FrameLayout scannerFrameLayout = qrCodeView.findViewById(R.id.layout_qr_scan_scannerFrameLayout);
                    final FrameLayout backgroundFrameLayout = qrCodeView.findViewById(R.id.layout_qr_scan_backgroundFrameLayout);
                    zXingScannerView = new ZXingScannerView(RewardzDetailActivity.this) ;

                    checkPermissions();

                    ViewTreeObserver viewTreeObserver = mainRelativeLayout.getViewTreeObserver();
                    viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            int h = scannerFrameLayout.getHeight();
                            int w = scannerFrameLayout.getWidth();
                            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                                    (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,w,new DisplayMetrics())
                                    ,(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,h,new DisplayMetrics()));
                            zXingScannerView.setMinimumHeight(h);
                            zXingScannerView.setMinimumWidth(w);
                        }
                    });

                    zXingScannerView.setAutoFocus(true);
                    List<BarcodeFormat> barcodeFormats = new ArrayList<>();
                    barcodeFormats.add(BarcodeFormat.QR_CODE);

                    zXingScannerView.setFormats(barcodeFormats);
                    scannerFrameLayout.addView(zXingScannerView);

                    alertDialog1.hide();
                    qrCodeView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    mainRelativeLayout.addView(qrCodeView);
                    redeem.setVisibility(View.GONE);

                    backgroundFrameLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });

                    backFrameLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onBackPressed();
                        }
                    });
                }
            });
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    venderPasscode = passCode.getText().toString();
                    if (!passCode.getText().toString().isEmpty()) {
                        if (checkInternetConnection.isConnectingToInternet()) {
                            try {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("reward", pk);
                                if (currentLocationLatLongHashMap.size() > 0) {
                                    jsonObject.put("reward_location", currentLocationLatLongHashMap.get(selectedIndex));
                                }
                                JSONObject user_data = new JSONObject();
                                user_data.put("password", venderPasscode);
                                jsonObject.put("user_data", user_data);
                                entity = new StringEntity(jsonObject.toString());
                                alertDialog1.dismiss();
                                flashWithPassword(entity);

                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {
                            connectivityMessage("Waiting for Network!");
                        }
                    } else {
                        if (getApplicationContext() != null) {
                            connectivityMessage("Please Enter vendor passcode");
                        }
                    }
                }
            });
        } else if (redemptionType.equals("e_voucher")) {
            if (sharedDatabase.isDiscount()) {
                eboucher();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(RewardzDetailActivity.this);
                View view = layoutInflater.inflate(R.layout.confirmation_popup, null);
                TextView pointsText = (TextView) view.findViewById(R.id.points_text);
                Button submit = (Button) view.findViewById(R.id.submit);
                Button cancel = (Button) view.findViewById(R.id.cancel);
                String redeemingPointText = "Redeeming this reward will deduct " + "<b>" + points + "</b>" + " from your account.";
                pointsText.setText(Html.fromHtml(redeemingPointText));
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog1.dismiss();
                    }
                });
                RelativeLayout logo = (RelativeLayout) view.findViewById(R.id.logo);
                logo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog1.dismiss();
                    }
                });
                builder.setView(view);
                alertDialog1 = builder.create();
                alertDialog1.show();
                alertDialog1.setCancelable(false);
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (checkInternetConnection.isConnectingToInternet()) {
                            alertDialog1.dismiss();
                            try {
                                eboucher();
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
            }
        }else if (redemptionType.equals("new_evoucher")) {
            if (sharedDatabase.isDiscount()) {
                newEvoucher();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(RewardzDetailActivity.this);
                View view = layoutInflater.inflate(R.layout.confirmation_popup, null);
                TextView pointsText = (TextView) view.findViewById(R.id.points_text);
                Button submit = (Button) view.findViewById(R.id.submit);
                Button cancel = (Button) view.findViewById(R.id.cancel);
                String redeemingPointText = "Redeeming this reward will deduct " + "<b>" + points + "</b>" + " from your account.";
                pointsText.setText(Html.fromHtml(redeemingPointText));
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog1.dismiss();
                    }
                });
                RelativeLayout logo = (RelativeLayout) view.findViewById(R.id.logo);
                logo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog1.dismiss();
                    }
                });
                builder.setView(view);
                alertDialog1 = builder.create();
                alertDialog1.show();
                alertDialog1.setCancelable(false);
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (checkInternetConnection.isConnectingToInternet()) {
                            alertDialog1.dismiss();
                            try {
                                newEvoucher();
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
            }
        } else if (redemptionType.equals("flash")) {
            if (checkInternetConnection.isConnectingToInternet()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RewardzDetailActivity.this);
                View view = layoutInflater.inflate(R.layout.confirmation_popup, null);
                vendePass = (TextView) view.findViewById(R.id.vender_Pass);
                passCode = (EditText) view.findViewById(R.id.pass_code);
                text1 = (TextView) view.findViewById(R.id.text1);
                submit = (Button) view.findViewById(R.id.submit);
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("reward", pk);
                    if (currentLocationLatLongHashMap.size() > 0) {
                        jsonObject.put("reward_location", currentLocationLatLongHashMap.get(selectedIndex));
                    }
//                    JSONObject user_data = new JSONObject();
//                    user_data.put("password", venderPasscode);
//                    jsonObject.put("user_data", user_data);
                    entity = new StringEntity(jsonObject.toString());
//                    alertDialog1.dismiss();

                    Log.v("RewardzDetail", "entity: " + jsonObject);

                    flashWithPassword(entity);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                connectivityMessage("Waiting for Network!");
            }
        } else if (redemptionType.equals("physical_voucher")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(RewardzDetailActivity.this);
            View view = layoutInflater.inflate(R.layout.confirmation_popup, null);
            TextView pointsText = (TextView) view.findViewById(R.id.points_text);
            Button submit = (Button) view.findViewById(R.id.submit);
            Button cancel = (Button) view.findViewById(R.id.cancel);
            String redeemingPointText = "Redeeming this reward will deduct " + "<b>" + points + "</b>" + " from your account.";
            pointsText.setText(Html.fromHtml(redeemingPointText));
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog1.dismiss();
                }
            });
            RelativeLayout logo = (RelativeLayout) view.findViewById(R.id.logo);
            logo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog1.dismiss();
                }
            });
            builder.setView(view);
            alertDialog1 = builder.create();
            alertDialog1.show();
            alertDialog1.setCancelable(false);
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (checkInternetConnection.isConnectingToInternet()) {
                        alertDialog1.dismiss();
                        try {
                            eboucher();
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
        } else if (redemptionType.equals("mobile_topup")) {
            if (!mobileNumberLogin.equals("")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RewardzDetailActivity.this);
                View view = layoutInflater.inflate(R.layout.confirmation_popup, null);
                TextView pointsText = (TextView) view.findViewById(R.id.points_text);
                Button submit = (Button) view.findViewById(R.id.submit);
                Button cancel = (Button) view.findViewById(R.id.cancel);
                String redeemingPointText = "Redeeming this reward will deduct " + "<b>" + points + "</b>" + " from your account.";
                pointsText.setText(Html.fromHtml(redeemingPointText));
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog1.dismiss();
                    }
                });
                RelativeLayout logo = (RelativeLayout) view.findViewById(R.id.logo);
                logo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog1.dismiss();
                    }
                });
                builder.setView(view);
                alertDialog1 = builder.create();
                alertDialog1.show();
                alertDialog1.setCancelable(false);
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (checkInternetConnection.isConnectingToInternet()) {
                            alertDialog1.dismiss();
                            try {
                                mobileTopup();
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

            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(RewardzDetailActivity.this);
                View view = layoutInflater.inflate(R.layout.confirmation_popup, null);
                TextView pointsText = (TextView) view.findViewById(R.id.points_text);
                Button submit = (Button) view.findViewById(R.id.submit);
                Button cancel = (Button) view.findViewById(R.id.cancel);
                String redeemingPointText = "Redeeming this reward will deduct " + "<b>" + points + "</b>" + " from your account.";
                pointsText.setText(Html.fromHtml(redeemingPointText));
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog1.dismiss();
                    }
                });
                RelativeLayout logo = (RelativeLayout) view.findViewById(R.id.logo);
                logo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog1.dismiss();
                    }
                });
                builder.setView(view);
                alertDialog1 = builder.create();
                alertDialog1.show();
                alertDialog1.setCancelable(false);
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (checkInternetConnection.isConnectingToInternet()) {
                            alertDialog1.dismiss();
                            try {
                                mobiletopup();
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
            }

        } else if (redemptionType.equals("data_topup")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(RewardzDetailActivity.this);
            View view = layoutInflater.inflate(R.layout.confirmation_popup, null);
            TextView pointsText = (TextView) view.findViewById(R.id.points_text);
            Button submit = (Button) view.findViewById(R.id.submit);
            Button cancel = (Button) view.findViewById(R.id.cancel);
            String redeemingPointText = "Redeeming this reward will deduct " + "<b>" + points + "</b>" + " from your account.";
            pointsText.setText(Html.fromHtml(redeemingPointText));
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog1.dismiss();
                }
            });
            RelativeLayout logo = (RelativeLayout) view.findViewById(R.id.logo);
            logo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog1.dismiss();
                }
            });
            builder.setView(view);
            alertDialog1 = builder.create();
            alertDialog1.show();
            alertDialog1.setCancelable(false);
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (checkInternetConnection.isConnectingToInternet()) {
                        alertDialog1.dismiss();
                        try {
                            datatopup();
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


        } else if (redemptionType.equals("electricity_topup")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(RewardzDetailActivity.this);
            View view = layoutInflater.inflate(R.layout.confirmation_popup, null);
            TextView pointsText = (TextView) view.findViewById(R.id.points_text);
            Button submit = (Button) view.findViewById(R.id.submit);
            Button cancel = (Button) view.findViewById(R.id.cancel);
            String redeemingPointText = "Redeeming this reward will deduct " + "<b>" + points + "</b>" + " from your account.";
            pointsText.setText(Html.fromHtml(redeemingPointText));
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog1.dismiss();
                }
            });
            RelativeLayout logo = (RelativeLayout) view.findViewById(R.id.logo);
            logo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog1.dismiss();
                }
            });
            builder.setView(view);
            alertDialog1 = builder.create();
            alertDialog1.show();
            alertDialog1.setCancelable(false);
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (checkInternetConnection.isConnectingToInternet()) {
                        alertDialog1.dismiss();
                        try {
                            electricitytopup();
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
        } else if (redemptionType.equals("promo_code")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(RewardzDetailActivity.this);
            View view = layoutInflater.inflate(R.layout.confirmation_popup, null);
            TextView pointsText = (TextView) view.findViewById(R.id.points_text);
            Button submit = (Button) view.findViewById(R.id.submit);
            Button cancel = (Button) view.findViewById(R.id.cancel);
            String redeemingPointText = "Redeeming this reward will deduct " + "<b>" + points + "</b>" + " from your account.";
            pointsText.setText(Html.fromHtml(redeemingPointText));
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog1.dismiss();
                }
            });
            RelativeLayout logo = (RelativeLayout) view.findViewById(R.id.logo);
            logo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog1.dismiss();
                }
            });
            builder.setView(view);
            alertDialog1 = builder.create();
            alertDialog1.show();
            alertDialog1.setCancelable(false);
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (checkInternetConnection.isConnectingToInternet()) {
                        alertDialog1.dismiss();
                        try {
                            AlertDialog.Builder builder = new AlertDialog.Builder(RewardzDetailActivity.this);
                            View view = layoutInflater.inflate(R.layout.flashwithpassword_activity, null);
                            vendePass = (TextView) view.findViewById(R.id.vender_Pass);
                            passCode = (EditText) view.findViewById(R.id.pass_code);
                            text1 = (TextView) view.findViewById(R.id.text1);
                            Button submit = (Button) view.findViewById(R.id.submit);
                            RelativeLayout imageView = (RelativeLayout) view.findViewById(R.id.logo);
                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alertDialog1.dismiss();
                                }
                            });
                            ImageView edit_email1 = (ImageView) view.findViewById(R.id.passcode_image);
                            builder.setView(view);
                            alertDialog1 = builder.create();
                            alertDialog1.show();
                            submit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    venderPasscode = passCode.getText().toString();
                                    if (!passCode.getText().toString().isEmpty()) {
                                        if (checkInternetConnection.isConnectingToInternet()) {
                                            try {
                                                JSONObject jsonObject = new JSONObject();
                                                jsonObject.put("reward", pk);
                                                if (currentLocationLatLongHashMap.size() > 0) {
                                                    jsonObject.put("reward_location", currentLocationLatLongHashMap.get(selectedIndex));
                                                }
                                                JSONObject user_data = new JSONObject();
                                                user_data.put("password", venderPasscode);
                                                user_data.put("email", email);
                                                jsonObject.put("user_data", user_data);
                                                entity = new StringEntity(jsonObject.toString());
                                                alertDialog1.dismiss();
                                                promoCodeRedeem(entity);
                                            } catch (NullPointerException e) {
                                                e.printStackTrace();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        } else {
                                            connectivityMessage("Waiting for Network!");
                                        }
                                    } else {
                                        if (getApplicationContext() != null) {
                                            connectivityMessage("Please Enter Vendor Password");
                                        }
                                    }
                                }
                            });
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

        }


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

    public void datatopup() {

        LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        AlertDialog.Builder builder = new AlertDialog.Builder(RewardzDetailActivity.this);
        View view = layoutInflater.inflate(R.layout.data_topup, null);
        vendePass = (TextView) view.findViewById(R.id.vender_Pass);
        passCode = (EditText) view.findViewById(R.id.customer_id);
        text1 = (TextView) view.findViewById(R.id.Customer_id_Text);
        mobileImage = (ImageView) view.findViewById(R.id.customer_id_image);
        submit = (Button) view.findViewById(R.id.submit);
        cancel = (Button) view.findViewById(R.id.cancel);
        confirm = (Button) view.findViewById(R.id.confirm);
        mobile_text1 = (TextView) view.findViewById(R.id.mobile_text1);
        RelativeLayout logo = (RelativeLayout) view.findViewById(R.id.logo);
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog1.dismiss();
            }
        });
        builder.setView(view);
        alertDialog1 = builder.create();
        alertDialog1.show();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!passCode.getText().toString().isEmpty()) {
                    if (checkInternetConnection.isConnectingToInternet()) {
                        String CustomerId = passCode.getText().toString();
                        dataTopup(CustomerId);


                    } else {
                        connectivityMessage("Waiting for Network!");

                    }


                } else {
                    if (getApplicationContext() != null) {
                        connectivityMessage("Please Enter your Customer Id number");
                    }
                }
            }
        });
    }

    public void electricitytopup() {
        LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        AlertDialog.Builder builder = new AlertDialog.Builder(RewardzDetailActivity.this);
        View view = layoutInflater.inflate(R.layout.electricity_layout, null);
        vendePass = (TextView) view.findViewById(R.id.vender_Pass);
        passCode = (EditText) view.findViewById(R.id.pln_number);
        text1 = (TextView) view.findViewById(R.id.mobile_text);
        mobileImage = (ImageView) view.findViewById(R.id.electricity_image);
        submit = (Button) view.findViewById(R.id.submit);
        cancel = (Button) view.findViewById(R.id.cancel);
        confirm = (Button) view.findViewById(R.id.confirm);
        thankyou = (TextView) view.findViewById(R.id.thankyou);
        mobile_text1 = (TextView) view.findViewById(R.id.mobile_text1);
        RelativeLayout logo = (RelativeLayout) view.findViewById(R.id.logo);
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog1.dismiss();
            }
        });
        builder.setView(view);
        alertDialog1 = builder.create();
        alertDialog1.show();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!passCode.getText().toString().isEmpty()) {
                    String plnnumber = passCode.getText().toString();
                    electricityTopup(plnnumber);


                } else {
                    if (getApplicationContext() != null) {
                        connectivityMessage("Please Enter your pln number");
                    }
                }
            }
        });
    }

    public void mobiletopup() {
        LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        AlertDialog.Builder builder = new AlertDialog.Builder(RewardzDetailActivity.this);
        View view = layoutInflater.inflate(R.layout.activity_redemption, null);
        vendePass = (TextView) view.findViewById(R.id.vender_Pass);
        passCode = (EditText) view.findViewById(R.id.mobile);
        text1 = (TextView) view.findViewById(R.id.mobile_text);
        mobileImage = (ImageView) view.findViewById(R.id.mobileImage);
        submit = (Button) view.findViewById(R.id.submit);
        cancel = (Button) view.findViewById(R.id.cancel);
        confirm = (Button) view.findViewById(R.id.confirm);
        submit1 = (TextView) view.findViewById(R.id.submit1);
        thankyou = (TextView) view.findViewById(R.id.thankyou);
        mobile_text1 = (TextView) view.findViewById(R.id.mobile_text1);
        RelativeLayout logo = (RelativeLayout) view.findViewById(R.id.logo);
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog1.dismiss();
            }
        });
        builder.setView(view);
        alertDialog1 = builder.create();
        alertDialog1.show();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!passCode.getText().toString().isEmpty()) {
                    mobileNumber = passCode.getText().toString();
                    mobileTopup();

                } else {
                    if (getApplicationContext() != null) {
                        connectivityMessage("Please Enter your phone");
                    }
                }
            }
        });
    }

    /* ******************calling Rewardz Detail Api for get method****************************/

    public void rewardzDetailApi() {
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
        String url = Constants.BASEURL + Constants.REWARDZ_DETAIL + pk + "/";
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                super.onSuccess(statusCode, headers, jsonObject);
                Loader.dialogDissmiss(getApplicationContext());
                selectedIndex = 0;
                if (getIntent().hasExtra("typeDiscountOrPoints")) {
                    String typeDiscountsorPoint = getIntent().getStringExtra("typeDiscountOrPoints");
                    if (typeDiscountsorPoint.equals("discount")) {
                        if (getIntent().hasExtra("isFromNearMe")) {
                            if (mredeemableFlag.equals(true)) {
                                updateUI(jsonObject.toString());
                            }
                        } else if (rewardzObject.mredeemableFlag) {
                            updateUI(jsonObject.toString());
                        } else {
                            updateUI(jsonObject.toString());
                            final AlertDialog alertDialog;
                            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext()
                                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            AlertDialog.Builder builder = new AlertDialog.Builder(RewardzDetailActivity.this);
                            View view = layoutInflater.inflate(R.layout.callmepoppoint_activity, null);
                            Button submit = (Button) view.findViewById(R.id.submit);
                            TextView title = (TextView) view.findViewById(R.id.title);
                            TextView text1 = (TextView) view.findViewById(R.id.vender_Pass);
                            title.setText("");
                            try {
                                text1.setText("Advance to tier " + jsonObject.getString("tier") + " to redeem this reward");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            builder.setView(view);
                            alertDialog = builder.create();
                            alertDialog.show();

                            alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    finish();
                                }
                            });
                            submit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    finish();
                                }
                            });
                        }
                    } else {
                        updateUI(jsonObject.toString());
                    }
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Loader.dialogDissmiss(getApplicationContext());
                if (statusCode == 401) {
                    UserManager.getInstance().logOut(RewardzDetailActivity.this);
                }

                if (statusCode == 400) {
                    if (getApplicationContext() != null) {
                        connectivityMessage("" + errorResponse);
                    }
                }
                selectedIndex = 0;
                if (statusCode == 500) {
                    if (getApplicationContext() != null) {
                        connectivityMessage("We've encountered a technical error.our team is working on it. please try again later");
                    }
                }
            }
        });
    }

    /* ******************calling when redemption type(mobile) show another dailogbox****************************/

    public void OferJsonResponseWorkedOnAlertDilogbox() {
        if (value == 200) {
            vendePass.setText("Confirmation");
            passCode.setVisibility(View.GONE);
            submit.setVisibility(View.GONE);
            mobileImage.setVisibility(View.GONE);
            text1.setVisibility(View.GONE);
            mobile_text1.setVisibility(View.VISIBLE);
            String value = "\"" + name + "\"";
            mobile_text1.setText("Are you sure you want to redeem " + value + " Reward for this Phone No");
            cancel.setVisibility(View.VISIBLE);
            confirm.setVisibility(View.VISIBLE);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog1.dismiss();
                }
            });
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vendePass.setText("Thank You!");
                    cancel.setVisibility(View.GONE);
                    mobile_text1.setVisibility(View.GONE);
                    submit1.setVisibility(View.VISIBLE);
                    confirm.setVisibility(View.GONE);
                    thankyou.setVisibility(View.VISIBLE);
                    thankyou.setText(finalRedeemCode);
                    submit1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog1.dismiss();
                        }
                    });
                }
            });
        } else {
            vendePass.setText("ERROR!");
            passCode.setVisibility(View.GONE);
            submit.setVisibility(View.GONE);
            mobileImage.setVisibility(View.GONE);
            text1.setVisibility(View.GONE);
            cancel.setVisibility(View.GONE);
            mobile_text1.setVisibility(View.GONE);
            submit1.setVisibility(View.VISIBLE);
            confirm.setVisibility(View.GONE);
            thankyou.setVisibility(View.VISIBLE);
            thankyou.setText(res1);
            submit1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog1.dismiss();
                }
            });
        }
    }


    /* *****************parsing json data and set to layout*****************************/

    public void updateUI(String jsonresponse) {
        try {
            selectedIndex = 0;
            latitudeCoordinateArray = new ArrayList<>();
            longitudeCoordinateArray = new ArrayList<>();
            jsonObject = new JSONObject(jsonresponse);
            try {
                String imageUrl = Constants.BASEURL + jsonObject.getString("display_img_url");
                Glide
                        .with(getApplicationContext())
                        .load(imageUrl)
                       /* .resize(970, 740)*/ // resizes the image to these dimensions (in pixel). does not respect aspect ratio
                        .into(backgroundImage);

            } catch (JSONException ex) {
                ex.printStackTrace();
            }
            System.out.println("json is " + jsonObject);
            points = jsonObject.getString("points");
            System.out.println("points  " + points);
            String website1 = jsonObject.getString("website");
            int length = website1.length();
            if (!website1.equals("")) {
                if (length > 36) {
                    String firstSubstring = website1.substring(0, 35);
                    String secondSubstring = website1.substring(35, length);
                    String finaladdress = firstSubstring + "\n" + secondSubstring;
                    website.setText(finaladdress);
                } else {
                    website.setText(website1);
                }
            } else {
                website.setText("N/A");
            }
            name = jsonObject.getString("name");
            facilityName.setText(Html.fromHtml(jsonObject.getString("name")));
            gold_gym.setText(Html.fromHtml(jsonObject.getString("name")));
            redemptionType = jsonObject.getString("redemption_type");
            if(jsonObject.has("used_validation_type")) {
                isUsedValidation = jsonObject.getBoolean("used_validation_type");
            }
            category = (jsonObject.getJSONObject("category")).getString("slug");
            type = (jsonObject.getJSONObject("category")).getInt("type");
            boucherRewardz = jsonObject.getString("brochure");
            boucherRewardz = boucherRewardz.replace("http", "https");
            if (!boucherRewardz.equals("null")) {
                presslink.setText(str);
            } else {
                presslink.setVisibility(View.GONE);
            }


            String rewardzDesc = Html.fromHtml(jsonObject.getString("description")).toString();
            rewardzDesc = rewardzDesc.replace("\n", "");
            description_textview1.setText(rewardzDesc);
            descriptionS = jsonObject.getString("terms_and_conditions");
            if (!descriptionS.equals("")) {
                descriptionS = descriptionS.replace("font-size: 7pt", "font-size: 10pt");
                descriptionS = descriptionS.replace("</strong>", "");
                descriptionS = descriptionS.replace("<strong>", "");
                descriptionS = descriptionS.replace("<p>", "");
                wv1.getSettings().setJavaScriptEnabled(true);
                wv1.loadData(descriptionS, "text/html", "utf-8");
                wv1.setBackgroundColor(444444);
                WebSettings settings = wv1.getSettings();
                settings.setDefaultFontSize(14);
            } else {
                wv1.loadData("N/A", "text/html", "utf-8");
                wv1.setBackgroundColor(444444);
            }
            String tillDate = jsonObject.getString("valid_until");

            if (!tillDate.equals("null")) {
                String s[] = tillDate.split("-");
                String day = s[2];
                String day1[] = day.split("T");
                String day2 = day1[0];
                String month = s[1];
                String year = s[0];

                if (month.equals("01")) {
                    month = "January";
                }
                if (month.equals("02")) {
                    month = "February";
                }
                if (month.equals("03")) {
                    month = " March";
                }
                if (month.equals("04")) {
                    month = " April";
                }
                if (month.equals("05")) {
                    month = " May";
                }
                if (month.equals("06")) {
                    month = " June";
                }
                if (month.equals("07")) {
                    month = " July";
                }
                if (month.equals("08")) {
                    month = " August";
                }
                if (month.equals("09")) {
                    month = " September";
                }
                if (month.equals("10")) {
                    month = " October";
                }
                if (month.equals("11")) {
                    month = " November";
                }
                if (month.equals("12")) {
                    month = " December";
                }
                String validDate = day2 + "" + month + " " + year;
                validTill.setText("VALID TILL  " + validDate);

            } else {
                validTill.setText("N/A");
            }
            locationJsonArray1 = jsonObject.getJSONArray("locations");

            JsonArrayLocationResposeLength = locationJsonArray1.length();
            JSONObject locationJsonObject1;
            if (JsonArrayLocationResposeLength != 0) {

                if (viewType.equalsIgnoreCase("nearMe")) {
                    for (int i = 0; i < locationJsonArray1.length(); i++) {
                        if (locationJsonArray1.getJSONObject(i).getString("pk").equalsIgnoreCase(rewardzObject.pk)) {
                            descriptionText.setText(Html.fromHtml(locationJsonArray1.getJSONObject(i).getString("address")));
                        }
                    }
                } else {
                    locationJsonObject1 = locationJsonArray1.getJSONObject(0);
                    if (JsonArrayLocationResposeLength == 1) {
                        descriptionText.setText(Html.fromHtml(locationJsonObject1.getString("address")));
                        downarrow1.setVisibility(View.GONE);
                    } else {
                        descriptionText.setText(Html.fromHtml(locationJsonObject1.getString("address")));
                    }
                }

            } else {
                descriptionText.setText("N/A");
                downarrow1.setVisibility(View.GONE);

            }
            JSONObject locationJsonObject = null;
            for (int j = 0; j < locationJsonArray1.length(); j++) {
                locationJsonObject = locationJsonArray1.getJSONObject(j);
                JSONArray coordinatesJsonArray = locationJsonObject.getJSONArray("coordinates");
                //  latitudeCoordinate = coordinatesJsonArray.getString(0);

                latitudeCoordinateArray.add(coordinatesJsonArray.getDouble(0));
                longitudeCoordinateArray.add(coordinatesJsonArray.getDouble(1));
            }
            final JSONObject merchantJsonObject = jsonObject.getJSONObject("merchant");
            String descriptionLength = Html.fromHtml(merchantJsonObject.getString("description")).toString();
            String desText = descriptionLength.replace("\n", "");
            if (!descriptionLength.equals(" ")) {
                if (desText.length() <= 100) {
                    termsandConditions.setText(Html.fromHtml(desText));

                } else {
                    readmodre.setVisibility(View.VISIBLE);
                    termsandConditions.setText(Html.fromHtml(desText.substring(0, 100) + "..."));
                }
                readmodre.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {

                            termsandConditions.setVisibility(View.VISIBLE);
                            readmodre.setVisibility(View.GONE);
                            String rewardzDesc = Html.fromHtml(merchantJsonObject.getString("description")).toString();

                            termsandConditions.setText(rewardzDesc.replace("\n", ""));
                        } catch (JSONException js) {
                            js.printStackTrace();
                        }
                    }
                });
            } else {
                termsandConditions.setText("N/A");
            }

            rewardLocationList = new ArrayList<>();
            JSONArray locationsArray = jsonObject.getJSONArray("locations");
            for (int i = 0; i < locationsArray.length(); i++) {
                JSONObject location = locationsArray.getJSONObject(i);

                JSONObject contactDetail = location.getJSONObject("contact_details");
                RewardContactDetail rewardContactDetail = new RewardContactDetail(
                        contactDetail.getInt("pk"),
                        contactDetail.getString("name"),
                        contactDetail.getString("email"),
                        contactDetail.getString("phone"),
                        contactDetail.getString("sms_number"));

                RewardLocation rewardLocation = new RewardLocation(
                        location.getInt("pk"),
                        location.getString("name"),
                        location.getInt("reward"),
                        location.getString("city"),
                        location.getString("address"),
                        rewardContactDetail,
                        location.getJSONArray("coordinates").getDouble(1),
                        location.getJSONArray("coordinates").getDouble(0)
                );

                rewardLocationList.add(rewardLocation);
            }

            selectedLocationPosition = 0;
            selectedLocationPositionTemp = selectedLocationPosition;
            if (rewardLocationList.size() > 0) {
                selectedLocation = rewardLocationList.get(selectedLocationPosition);
                directionImageView.setVisibility(View.VISIBLE);
                downarrow1.setVisibility(View.VISIBLE);
            } else {
                directionImageView.setVisibility(View.GONE);
                downarrow1.setVisibility(View.GONE);
            }

            locationJsonArray = jsonObject.getJSONObject("contact_details");
            String phone_text = locationJsonArray.getString("phone");
            if (!phone_text.equals("")) {
                if (phone_text.contains("/")) {
                    String[] splitMobileText = phone_text.split("/");
                    for (int i = 0; i < splitMobileText.length; i++) {
                        mobileNumberArray.add(splitMobileText[i]);
                    }
                    phoneNumber.setText(mobileNumberArray.get(0));
                } else {
                    phoneNumber.setText(phone_text);
                }
            } else {
                phoneNumber.setText("N/A");
            }
            emailSet = locationJsonArray.getString("email");
            if (!emailSet.equals(" ")) {
                emailAddress.setText(emailSet);
            } else {
                emailAddress.setText("N/A");
            }

            for (int i = 0; i < locationJsonArray1.length(); i++) {
                JSONObject locjsonObject = locationJsonArray1.getJSONObject(i);

                addressArray.add(locjsonObject.getString("address"));

             /*   JSONObject jsonObject=new JSONObject();

                jsonObject=locjsonObject.getJSONObject("contact_details");*/
                currentLocationLatLongHashMap.add(locjsonObject.getString("pk"));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    ///////////////////////////// promo code redeem ////////////////////////////////////////////
    public void promoCodeRedeem(StringEntity stringEntity) {
        String entity = stringEntity.toString();
        Log.v("entity", entity);
        Loader.showProgressDialog(this);
//        SharedPreferences sharedPreferencesToken = getApplicationContext().getSharedPreferences("token", 1);
//        String token = sharedPreferencesToken.getString("token", "");
        String authProvider = SettingsManager.getInstance().getAuthProvider();

        HashMap<String, String> paramMap = new HashMap<String, String>();

        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(80000);
        client.addHeader("USER-AGENT", AppController.useragent);
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("Content-Type", "application/json");
        String url = Constants.BASEURL + Constants.REDEMPTION_REWARDZ;
        client.post(RewardzDetailActivity.this, url, stringEntity, "application/json", new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        Loader.dialogDissmiss(getApplicationContext());
                        String description = "";
                        String referencecode = "";
                        String code = "";
                        System.out.println("hheehgjh" + res);
                        try {
                            JSONObject jsonObject1 = new JSONObject(res);
                            description = jsonObject1.getString("description");
                            description = Html.fromHtml(description).toString();

                            referencecode = Html.fromHtml(jsonObject1.getString("reference")).toString();
                            code = Html.fromHtml(jsonObject1.getString("code")).toString();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        value = statusCode;
                        if (value == 200) {
                            Intent intent = new Intent(RewardzDetailActivity.this, RedeemedRewardzActivity.class);
                            intent.putExtra("pk", pk);
                            intent.putExtra("redemptionType", redemptionType);
                            intent.putExtra("code", code);
                            intent.putExtra("category", category);
                            intent.putExtra("type", type);
                            intent.putExtra("reference", referencecode);
                            intent.putExtra("description", description);
                            startActivity(intent);
                        }
                        //mixpanel
                        JSONObject props = new JSONObject();
                        try {
                            props.put("rewardId", pk);
                            AppController.getInstance().getMixpanelAPI().track("RewardRedemeed", props);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        System.out.println("hheehgjh fail" + res);
                        value = statusCode;
                        Loader.dialogDissmiss(getApplicationContext());

                        if (statusCode == 500) {
                            if (getApplicationContext() != null) {
                                connectivityMessage("We've encountered a technical error.our team is working on it. please try again later");
                            }
                        }
                        try {
                            JSONArray jsonArray = new JSONArray(res);
                            res1 = jsonArray.getString(0);
                            if (value == statusCode) {
//                                if (redemptionType.equals("flash_password")) {
//
//
//                                } else {
//                                    callMePopUp();
//                                }
                                final AlertDialog alertDialog;

                                LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext()
                                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                AlertDialog.Builder builder = new AlertDialog.Builder(RewardzDetailActivity.this);
                                View view = layoutInflater.inflate(R.layout.callmepoppoint_activity, null);
                                TextView vendePass = (TextView) view.findViewById(R.id.vender_Pass);
                                TextView title = (TextView) view.findViewById(R.id.title);
                                vendePass.setText(res1);
                                title.setText("ERROR RESPONSE");
                                Button submit1 = (Button) view.findViewById(R.id.submit);
                                RelativeLayout mobileImage = (RelativeLayout) view.findViewById(R.id.logo);
                                builder.setView(view);
                                alertDialog = builder.create();

                                alertDialog.show();

                                submit1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog1.dismiss();
                                        alertDialog.dismiss();
                                    }
                                });
                                mobileImage.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        alertDialog1.dismiss();
                                        alertDialog.dismiss();
                                    }
                                });
                            }
                        } catch (JSONException js) {
                            js.printStackTrace();
                            ;
                        }


                    }
                }
        );

    }


    ///////////////////////////// flash With Password and Flash redeemed Api////////////////////////////////////////////
    public void flashWithPassword(StringEntity stringEntity) {
        String authProvider = SettingsManager.getInstance().getAuthProvider();

        Loader.showProgressDialog(this);
        HashMap<String, String> paramMap = new HashMap<String, String>();

        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(80000);
        client.addHeader("USER-AGENT", AppController.useragent);
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("Content-Type", "application/json");
        String url = Constants.BASEURL + Constants.REDEMPTION_REWARDZ;
        client.post(RewardzDetailActivity.this, url, stringEntity, "application/json", new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        Loader.dialogDissmiss(getApplicationContext());
                        String description = "";
                        String referencecode = "";
                        System.out.println("hheehgjh" + res);
                        try {
                            if (redemptionType.equals("flash_password")) {
                                JSONObject jsonObject1 = new JSONObject(res);
                                description = jsonObject1.getString("description");
                                description = description.replace("<Flash", "Flash");
                                description = Html.fromHtml(description).toString();
                                referencecode = Html.fromHtml(jsonObject1.getString("reference")).toString();
                            } else {
                                JSONObject jsonObject1 = new JSONObject(res);
                                description = jsonObject1.getString("description");
                                description = description.replace("<Flash", "Flash");


                                referencecode = Html.fromHtml(jsonObject1.getString("reference")).toString();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        value = statusCode;
                        if (value == 200) {
                            Intent intent = new Intent(RewardzDetailActivity.this, RedeemedRewardzActivity.class);
                            intent.putExtra("pk", pk);
                            intent.putExtra("redemptionType", redemptionType);
                            intent.putExtra("category", category);
                            intent.putExtra("type", type);
                            intent.putExtra("reference", referencecode);
                            intent.putExtra("description", description);
                            startActivity(intent);
                        }

                        //mixpanel
                        JSONObject props = new JSONObject();
                        try {
                            props.put("rewardId", pk);
                            AppController.getInstance().getMixpanelAPI().track("RewardRedemeed", props);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        System.out.println("hheehgjh fail" + res);
                        value = statusCode;
                        Loader.dialogDissmiss(getApplicationContext());

                        if (statusCode == 500) {
                            if (getApplicationContext() != null) {
                                connectivityMessage("We've encountered a technical error.our team is working on it. please try again later");
                            }
                        }
                        try {
                            JSONArray jsonArray = new JSONArray(res);
                            res1 = jsonArray.getString(0);
                            if (value == statusCode) {
                                if (redemptionType.equals("flash_password")) {
                                    ;
                                    final AlertDialog alertDialog;

                                    LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext()
                                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    AlertDialog.Builder builder = new AlertDialog.Builder(RewardzDetailActivity.this);
                                    View view = layoutInflater.inflate(R.layout.callmepoppoint_activity, null);
                                    TextView vendePass = (TextView) view.findViewById(R.id.vender_Pass);
                                    TextView title = (TextView) view.findViewById(R.id.title);
                                    vendePass.setText(res1);
                                    title.setText("ERROR RESPONSE");
                                    Button submit1 = (Button) view.findViewById(R.id.submit);
                                    RelativeLayout mobileImage = (RelativeLayout) view.findViewById(R.id.logo);
                                    builder.setView(view);
                                    alertDialog = builder.create();

                                    alertDialog.show();

                                    submit1.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            alertDialog1.dismiss();
                                            alertDialog.dismiss();
                                        }
                                    });
                                    mobileImage.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            alertDialog1.dismiss();
                                            alertDialog.dismiss();
                                        }
                                    });

                                } else {
                                    callMePopUp();
                                }

                            }
                        } catch (JSONException js) {
                            js.printStackTrace();
                            ;
                        }


                    }
                }
        );

    }


    //////////////////////////////////eboucher and physical voucher redeemed api/////////////////////////////////////////////
    public void eboucher() {
        Loader.showProgressDialog(this);
        JSONObject jsonObject = new JSONObject();
        try {
            String phone1 = "";

            jsonObject.put("reward", pk);

            JSONObject user_data = new JSONObject();
            user_data.put("phone", phone1);
            user_data.put("email", email);

            jsonObject.put("user_data", user_data);
            entity = new StringEntity(jsonObject.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        String authProvider = SettingsManager.getInstance().getAuthProvider();
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(80000);
        client.addHeader("USER-AGENT", AppController.useragent);
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("Content-Type", "application/json");
        String url = Constants.BASEURL + Constants.REDEMPTION_REWARDZ;
        client.post(RewardzDetailActivity.this, url, entity, "application/json", new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        Loader.dialogDissmiss(getApplicationContext());
                        String description = "";
                        String referencecode = "";
                        System.out.println("hheehgjh" + res);
                        try {
                            JSONObject jsonObject1 = new JSONObject(res);
                            if (jsonObject1.has("description")) {
                                description = jsonObject1.getString("description");
                                description = Html.fromHtml(description).toString();
                            } else {
                                referencecode = jsonObject1.getString("code").toString();
                            }
                            if (jsonObject1.has("code")) {
                                referencecode = jsonObject1.getString("code").toString();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        value = statusCode;
                        if (value == 200) {
                            Intent intent = new Intent(RewardzDetailActivity.this, RedeemedRewardzActivity.class);
                            intent.putExtra("pk", pk);
                            intent.putExtra("redemptionType", redemptionType);
                            intent.putExtra("category", category);
                            intent.putExtra("type", type);
                            intent.putExtra("reference", referencecode);
                            intent.putExtra("description", description);
                       /*     if (redemptionType.equals("e_voucher")) {
                                alertDialog1.dismiss();
                            }*/

                            startActivity(intent);
                        }

                        //mixpanel
                        JSONObject props = new JSONObject();
                        try {
                            props.put("rewardId", pk);
                            AppController.getInstance().getMixpanelAPI().track("RewardRedemeed", props);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        System.out.println("hheehgjh fail" + res);
                        value = statusCode;
                        Loader.dialogDissmiss(getApplicationContext());
                        if (statusCode == 500) {
                            if (getApplicationContext() != null) {
                                connectivityMessage("We've encountered a technical error.our team is working on it. please try again later");
                                Loader.dialogDissmiss(getApplicationContext());
                            }
                        }
                        try {


                            JSONArray jsonArray = new JSONArray(res);
                            jsonresponse = jsonArray.getString(0);

                            if (redemptionType.equals("e_voucher")) {

                                final AlertDialog alertDialog;

                                LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext()
                                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                AlertDialog.Builder builder = new AlertDialog.Builder(RewardzDetailActivity.this);
                                View view = layoutInflater.inflate(R.layout.callmepoppoint_activity, null);
                                TextView vendePass = (TextView) view.findViewById(R.id.vender_Pass);
                                TextView title = (TextView) view.findViewById(R.id.title);
                                vendePass.setText(jsonresponse);
                                title.setText("ERROR RESPONSE");
                                Button submit1 = (Button) view.findViewById(R.id.submit);
                                RelativeLayout mobileImage = (RelativeLayout) view.findViewById(R.id.logo);
                                builder.setView(view);
                                alertDialog = builder.create();
                                alertDialog.show();

                                submit1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog.dismiss();
                                    }
                                });
                                mobileImage.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog.dismiss();
                                    }
                                });


                            } else {
                                callMePopUp();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }
    //////////////////////////////////new evoucher and physical voucher redeemed api/////////////////////////////////////////////
    public void newEvoucher() {
        Loader.showProgressDialog(this);
        final JSONObject jsonObject = new JSONObject();
        try {
            String phone1 = "";

            jsonObject.put("reward", pk);

            JSONObject user_data = new JSONObject();
            user_data.put("phone", phone1);
            user_data.put("email", email);

            jsonObject.put("user_data", user_data);
            entity = new StringEntity(jsonObject.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        String authProvider = SettingsManager.getInstance().getAuthProvider();
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(80000);
        client.addHeader("USER-AGENT", AppController.useragent);
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("Content-Type", "application/json");
        String url = Constants.BASEURL + Constants.REDEMPTION_REWARDZ;
        client.post(RewardzDetailActivity.this, url, entity, "application/json", new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        Loader.dialogDissmiss(getApplicationContext());
                        int voucherId = 0;
                        System.out.println("hheehgjh" + res);
                        try {
                            JSONObject jsonObject1 = new JSONObject(res);
                            Log.d("RewardzDetailActivity","Success redeem response : "+jsonObject1);
                            if (jsonObject1.has("wallet")) {
                                voucherId = Integer.valueOf(jsonObject1.getString("wallet")).intValue();
                                sharedDatabase.setSelectedWalletId(voucherId);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        value = statusCode;
                        if (value == 200) {
                            Toast.makeText(RewardzDetailActivity.this, "Reward Redeemed", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RewardzDetailActivity.this,MainActivity.class);
                            intent.putExtra("action",Constants.ACTION_WALLET);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
//                            bundle.putString();
                        }

                        //mixpanel
                        JSONObject props = new JSONObject();
                        try {
                            props.put("rewardId", pk);
                            AppController.getInstance().getMixpanelAPI().track("RewardRedemeed", props);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        System.out.println("hheehgjh fail" + res);
                        value = statusCode;
                        Loader.dialogDissmiss(getApplicationContext());
                        if (statusCode == 500) {
                            if (getApplicationContext() != null) {
                                connectivityMessage("We've encountered a technical error.our team is working on it. please try again later");
                                Loader.dialogDissmiss(getApplicationContext());
                            }
                        }
                        try {


                            JSONArray jsonArray = new JSONArray(res);
                            jsonresponse = jsonArray.getString(0);

                            if (redemptionType.equals("new_evoucher")) {

                                final AlertDialog alertDialog;

                                LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext()
                                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                AlertDialog.Builder builder = new AlertDialog.Builder(RewardzDetailActivity.this);
                                View view = layoutInflater.inflate(R.layout.callmepoppoint_activity, null);
                                TextView vendePass = (TextView) view.findViewById(R.id.vender_Pass);
                                TextView title = (TextView) view.findViewById(R.id.title);
                                vendePass.setText(jsonresponse);
                                title.setText("ERROR RESPONSE");
                                Button submit1 = (Button) view.findViewById(R.id.submit);
                                RelativeLayout mobileImage = (RelativeLayout) view.findViewById(R.id.logo);
                                builder.setView(view);
                                alertDialog = builder.create();
                                alertDialog.show();

                                submit1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog.dismiss();
                                    }
                                });
                                mobileImage.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog.dismiss();
                                    }
                                });


                            } else {
                                callMePopUp();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }

    ///////////////////////////////////topup voucher and mobile voucher redeemed api//////////////////////////////////////////////////
    public void mobileTopup() {
        Loader.showProgressDialog(this);
        JSONObject jsonObject = new JSONObject();
        try {
            int pk1 = Integer.parseInt(pk);
            jsonObject.put("reward", pk1);
            JSONObject user_data = new JSONObject();
            user_data.put("mobile", mobileNumber);
            jsonObject.put("user_data", user_data);
            entity = new StringEntity(jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String authProvider = SettingsManager.getInstance().getAuthProvider();
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.addHeader("USER-AGENT", AppController.useragent);
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("Content-Type", "application/json");
        client.setTimeout(800000);
        String url = Constants.BASEURL + Constants.REDEMPTION_REWARDZ;
        client.post(RewardzDetailActivity.this, url, entity, "application/json", new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        Loader.dialogDissmiss(getApplicationContext());

                        String redeemCode = "";
                        String referencecode = "";
                        System.out.println("hheehgjh" + res);
                        try {
                            JSONObject jsonObject1 = new JSONObject(res);
                            redeemCode = jsonObject1.getString("description");
                            redeemCode = redeemCode.replace("<Flash", "Flash");
                            redeemCode = Html.fromHtml(redeemCode).toString();
                            redeemCode = redeemCode.replace("\n", "");
                            finalRedeemCode = redeemCode;
                            referencecode = Html.fromHtml(jsonObject1.getString("reference")).toString();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (mobileNumberLogin == mobileNumber) {
                            final AlertDialog alertDialog;
                            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext()
                                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            AlertDialog.Builder builder = new AlertDialog.Builder(RewardzDetailActivity.this);
                            View view = layoutInflater.inflate(R.layout.callmepoppoint_activity, null);
                            Button submit = (Button) view.findViewById(R.id.submit);
                            TextView title = (TextView) view.findViewById(R.id.title);
                            TextView text1 = (TextView) view.findViewById(R.id.vender_Pass);
                            title.setText("Thank You!");

                            text1.setText(redeemCode);
                            RelativeLayout mobileImage = (RelativeLayout) view.findViewById(R.id.logo);
                            builder.setView(view);
                            alertDialog = builder.create();
                            alertDialog.show();

                            submit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alertDialog.dismiss();
                                }
                            });
                            mobileImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alertDialog.dismiss();
                                }
                            });
                        }

                        //mixpanel
                        JSONObject props = new JSONObject();
                        try {
                            props.put("rewardId", pk);
                            AppController.getInstance().getMixpanelAPI().track("RewardRedemeed", props);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        System.out.println("hheehgjh fail" + res);
                        if (statusCode == 500) {
                            connectivityMessage("We've encountered a technical error.our team is working on it. please try again later");
                            Loader.dialogDissmiss(getApplicationContext());
                        }
                        Loader.dialogDissmiss(getApplicationContext());
                        String res12 = null;
                        try {
                            JSONArray jsonArray = new JSONArray(res);
                            res12 = jsonArray.getString(0);

                            res1 = jsonArray.getString(0);

                            Loader.dialogDissmiss(getApplicationContext());
                            value = statusCode;
                            if (mobileNumberLogin == mobileNumber) {
                                final AlertDialog alertDialog;
                                LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext()
                                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                AlertDialog.Builder builder = new AlertDialog.Builder(RewardzDetailActivity.this);
                                View view = layoutInflater.inflate(R.layout.callmepoppoint_activity, null);
                                Button submit1 = (Button) view.findViewById(R.id.submit);
                                TextView title = (TextView) view.findViewById(R.id.title);
                                TextView text1 = (TextView) view.findViewById(R.id.vender_Pass);
                                title.setText("ERROR!");
                                text1.setText(res12);
                                RelativeLayout mobileImage = (RelativeLayout) view.findViewById(R.id.logo);
                                builder.setView(view);
                                alertDialog = builder.create();
                                alertDialog.show();
                                submit1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog.dismiss();

                                    }
                                });
                                mobileImage.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog.dismiss();
                                    }
                                });
                            } else {
                                OferJsonResponseWorkedOnAlertDilogbox();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }


    //////////////////////////electricityTopup redeem api///////////////////////////////////
    public void electricityTopup(String plnNumber) {
        Loader.showProgressDialog(this);
        JSONObject jsonObject = new JSONObject();
        try {
            int pk1 = Integer.parseInt(pk);

            jsonObject.put("reward", pk1);
            JSONObject user_data = new JSONObject();
            user_data.put("pln_number", plnNumber);
            jsonObject.put("user_data", user_data);
            entity = new StringEntity(jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String authProvider = SettingsManager.getInstance().getAuthProvider();
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.addHeader("USER-AGENT", AppController.useragent);
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("Content-Type", "application/json");
        client.setTimeout(800000);
        String url = Constants.BASEURL + Constants.REDEMPTION_REWARDZ;
        client.post(RewardzDetailActivity.this, url, entity, "application/json", new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        Loader.dialogDissmiss(getApplicationContext());

                        String description = "";
                        String referencecode = "";
                        System.out.println("hheehgjh" + res);
                        try {
                            JSONObject jsonObject1 = new JSONObject(res);
                            description = Html.fromHtml(jsonObject1.getString("description")).toString();
                            referencecode = Html.fromHtml(jsonObject1.getString("reference")).toString();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (statusCode == 200) {
                            Intent intent = new Intent(RewardzDetailActivity.this, RedeemedRewardzActivity.class);
                            intent.putExtra("pk", pk);
                            intent.putExtra("redemptionType", redemptionType);
                            intent.putExtra("category", category);
                            intent.putExtra("type", type);
                            intent.putExtra("reference", referencecode);
                            intent.putExtra("description", description);
                            if (redemptionType.equals("electricity_topup")) {
                                alertDialog1.dismiss();
                            }
                            startActivity(intent);
                        }

                        //mixpanel
                        JSONObject props = new JSONObject();
                        try {
                            props.put("rewardId", pk);
                            AppController.getInstance().getMixpanelAPI().track("RewardRedemeed", props);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        System.out.println("hheehgjh fail" + res);
                        if (statusCode == 500) {
                            if (getApplicationContext() != null) {
                                connectivityMessage("We've encountered a technical error.our team is working on it. please try again later");
                                Loader.dialogDissmiss(getApplicationContext());
                            }
                        }
                        Loader.dialogDissmiss(getApplicationContext());
                        String res12 = null;


                        try {
                            JSONArray jsonArray = new JSONArray(res);
                            res1 = jsonArray.getString(0);
                            final AlertDialog alertDialog;

                            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext()
                                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            AlertDialog.Builder builder = new AlertDialog.Builder(RewardzDetailActivity.this);
                            View view = layoutInflater.inflate(R.layout.callmepoppoint_activity, null);
                            TextView vendePass = (TextView) view.findViewById(R.id.vender_Pass);
                            TextView title = (TextView) view.findViewById(R.id.title);
                            vendePass.setText(res1);
                            title.setText("ERROR RESPONSE");
                            Button submit1 = (Button) view.findViewById(R.id.submit);
                            RelativeLayout mobileImage = (RelativeLayout) view.findViewById(R.id.logo);
                            builder.setView(view);
                            alertDialog = builder.create();
                            alertDialog1.dismiss();
                            alertDialog.show();

                            submit1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alertDialog.dismiss();
                                }
                            });
                            mobileImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alertDialog.dismiss();
                                }
                            });


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }

    //////////////////////////DataTopup Support redeem api///////////////////////////////////
    public void dataTopup(String customerid) {
        Loader.showProgressDialog(this);
        HashMap<String, String> paramMap = new HashMap<String, String>();
        JSONObject jsonObject = new JSONObject();
        try {
            int pk1 = Integer.parseInt(pk);

            jsonObject.put("reward", pk1);
            JSONObject user_data = new JSONObject();
            user_data.put("customer_id", customerid);
            jsonObject.put("user_data", user_data);
            entity = new StringEntity(jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String authProvider = SettingsManager.getInstance().getAuthProvider();
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.addHeader("USER-AGENT", AppController.useragent);
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("Content-Type", "application/json");
        client.setTimeout(800000);
        String url = Constants.BASEURL + Constants.REDEMPTION_REWARDZ;
        client.post(RewardzDetailActivity.this, url, entity, "application/json", new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        Loader.dialogDissmiss(getApplicationContext());
                        String description = "";
                        String referencecode = "";
                        System.out.println("hheehgjh" + res);
                        try {
                            JSONObject jsonObject1 = new JSONObject(res);
                            description = Html.fromHtml(jsonObject1.getString("description")).toString();
                            referencecode = Html.fromHtml(jsonObject1.getString("reference")).toString();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (statusCode == 200) {
                            Intent intent = new Intent(RewardzDetailActivity.this, RedeemedRewardzActivity.class);
                            intent.putExtra("pk", pk);
                            intent.putExtra("redemptionType", redemptionType);
                            intent.putExtra("category", category);
                            intent.putExtra("type", type);
                            intent.putExtra("reference", referencecode);
                            intent.putExtra("description", description);
                            if (redemptionType.equals("data_topup")) {
                                alertDialog1.dismiss();
                            }
                            startActivity(intent);
                        }

                        //mixpanel
                        JSONObject props = new JSONObject();
                        try {
                            props.put("rewardId", pk);
                            AppController.getInstance().getMixpanelAPI().track("RewardRedemeed", props);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        System.out.println("hheehgjh fail" + res);
                        Loader.dialogDissmiss(getApplicationContext());
                        if (statusCode == 500) {
                            if (getApplicationContext() != null) {
                                connectivityMessage("We've encountered a technical error.our team is working on it. please try again later");
                                Loader.dialogDissmiss(getApplicationContext());
                            }
                        }
                        String res12 = null;


                        try {
                            JSONArray jsonArray = new JSONArray(res);
                            res1 = jsonArray.getString(0);
                            final AlertDialog alertDialog;

                            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext()
                                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            AlertDialog.Builder builder = new AlertDialog.Builder(RewardzDetailActivity.this);
                            View view = layoutInflater.inflate(R.layout.callmepoppoint_activity, null);
                            TextView vendePass = (TextView) view.findViewById(R.id.vender_Pass);
                            TextView title = (TextView) view.findViewById(R.id.title);
                            vendePass.setText(res1);
                            title.setText("ERROR RESPONSE");
                            Button submit1 = (Button) view.findViewById(R.id.submit);
                            RelativeLayout mobileImage = (RelativeLayout) view.findViewById(R.id.logo);
                            builder.setView(view);
                            alertDialog = builder.create();
                            alertDialog1.dismiss();
                            alertDialog.show();

                            submit1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alertDialog.dismiss();
                                }
                            });
                            mobileImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alertDialog.dismiss();
                                }
                            });


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }

    /* *****************when Api send Wrong Response*****************************/

    public void callMePopUp() {
        final AlertDialog alertDialog;
        LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        AlertDialog.Builder builder = new AlertDialog.Builder(RewardzDetailActivity.this);
        View view = layoutInflater.inflate(R.layout.callmepoppoint_activity, null);

        TextView vendePass = (TextView) view.findViewById(R.id.vender_Pass);
        vendePass.setText(jsonresponse);
        Button submit1 = (Button) view.findViewById(R.id.submit);
        RelativeLayout mobileImage = (RelativeLayout) view.findViewById(R.id.logo);
        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();

        submit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        mobileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        JSONObject props = new JSONObject();
        try {
            props.put("rewardId", pk);
            props.put("categoryName", category);
            AppController.getInstance().getMixpanelAPI().track("RewardDetail", props);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if(zXingScannerView!=null) {
            zXingScannerView.stopCamera();
            mainRelativeLayout.removeView(qrCodeView);

            alertDialog1.show();
            redeem.setVisibility(View.VISIBLE);
        }
        else{
            super.onBackPressed();

        }
    }

    SelectLocationsAdapter.SelectLocationListener listener = new SelectLocationsAdapter.SelectLocationListener() {
        @Override
        public void onSelectLocationListener(int position) {
            selectedLocationPositionTemp = position;
        }
    };

    FrameLayout.OnClickListener cancelSelectLocationListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            selectedLocationPositionTemp = selectedLocationPosition;

            selectLocationFrameLayout.setVisibility(View.GONE);
            appBarLayout.setEnabled(true);
            nestedScrollView.setEnabled(true);
            mainRelativeLayout.setEnabled(true);
        }
    };

    FrameLayout.OnClickListener okSelectLocationListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            selectedLocationPosition = selectedLocationPositionTemp;

            selectLocationFrameLayout.setVisibility(View.GONE);
            appBarLayout.setEnabled(true);
            nestedScrollView.setEnabled(true);
            mainRelativeLayout.setEnabled(true);

            descriptionText.setText(rewardLocationList.get(selectedLocationPosition).address);
            selectedLocation = rewardLocationList.get(selectedLocationPosition);
        }
    };

    ImageView.OnClickListener directionImageViewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            double latitude = selectedLocation.latitude;
            double longitude = selectedLocation.longitude;

            String label = selectedLocation.address;
            String uriBegin = "geo:" + latitude + "," + longitude;
            String query = latitude + "," + longitude + "(" + label + ")";
            String encodedQuery = Uri.encode(query);
            String uriString = uriBegin + "?q=" + encodedQuery + "&z=15";
            Uri uri = Uri.parse(uriString);
            try {
                startActivity(new Intent(android.content.Intent.ACTION_VIEW, uri));
            } catch (Exception e) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                String geoUri = "http://maps.google.com/maps?q=loc:" + latitude + "," + longitude + "(" + label + ")";
                intent.setData(Uri.parse(geoUri));

                try {
                    startActivity(intent);
                } catch (Exception e1) {
                    final AlertDialog.Builder alrt;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        alrt = new AlertDialog.Builder(RewardzDetailActivity.this, android.R.style.Theme_Material_Light_Dialog_Alert);
                    } else {
                        alrt = new AlertDialog.Builder(RewardzDetailActivity.this);
                    }
                    alrt.setMessage("Please install Google Maps to use this feature");

                    alrt.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    AlertDialog alertDialog = alrt.create();

                    alertDialog.show();
                }
            }
        }
    };


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
            rewardzDetailApi();
        } else {
            android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(this).create();
            alertDialog.setTitle("Error");
            alertDialog.setMessage(event.message);
            alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_NEUTRAL, "Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//                    UserManager.getInstance().logOut();
                }
            });
        }
    }

    void setCamera() {
        zXingScannerView.setResultHandler(this);
        zXingScannerView.startCamera();
    }

    @Override
    public void handleResult(Result result) {
        Log.v("scannnnnnnnnner = ", result.getText());
        Log.v("scannnnnnnnnner = ", result.getBarcodeFormat().toString());

        zXingScannerView.stopCamera();
        // HANDLE RESULT
        passCode.setText(result.toString());
        mainRelativeLayout.removeView(qrCodeView);
        alertDialog1.show();
        redeem.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private static class CustomViewFinderView extends ViewFinderView {
        public static final String TRADE_MARK_TEXT = "";
        public static final int TRADE_MARK_TEXT_SIZE_SP = 40;
        public final Paint PAINT = new Paint();

        public CustomViewFinderView(Context context) {
            super(context);
            init();
        }

        public CustomViewFinderView(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            init();
        }

        private void init() {
            PAINT.setColor(Color.GREEN);
            PAINT.setAntiAlias(true);
            float textPixelSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, TRADE_MARK_TEXT_SIZE_SP, getResources().getDisplayMetrics());
            PAINT.setTextSize(textPixelSize);
        }

        @Override
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            drawTradeMark(canvas);
        }

        private void drawTradeMark(Canvas canvas) {
            Rect framingRect = getFramingRect();
            float tradeMarkTop;
            float tradeMarkLeft;

            if (framingRect != null) {
                tradeMarkTop = framingRect.bottom + PAINT.getTextSize() + 10;
                tradeMarkLeft = framingRect.left;
            } else {
                tradeMarkTop = 10;
                tradeMarkLeft = canvas.getHeight() - PAINT.getTextSize() - 10;
            }
            canvas.drawText(TRADE_MARK_TEXT, tradeMarkLeft, tradeMarkTop, PAINT);

        }
    }

    private void checkPermissions() {
        ArrayList<String> permissionArrayList = new ArrayList<>();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionArrayList.add(android.Manifest.permission.CAMERA);
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionArrayList.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (permissionArrayList.size() > 0) {
            String[] permissions = permissionArrayList.toArray(new String[permissionArrayList.size()]);
            ActivityCompat.requestPermissions(this, permissions, 0);
        } else {
            initiateRequest();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        int flag = 0;
        if (grantResults.length > 0) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    flag++;
                }
            }
            if (flag == permissions.length) {
                initiateRequest();
            }
        }
    }

    private void initiateRequest(){
        setCamera();
    }
}