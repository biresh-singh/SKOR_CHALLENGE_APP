package activity.rewardz;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.common.BitMatrix;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.root.skor.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import InternetConnection.CheckInternetConnection;
import bean.NewEVoucher;
import constants.Constants;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import database.SharedDatabase;
import io.realm.Realm;
import jp.wasabeef.glide.transformations.GrayscaleTransformation;
import me.dm7.barcodescanner.core.ViewFinderView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import singleton.InterfaceManager;
import singleton.SettingsManager;
import utils.AppController;
import utils.Loader;

public class NewVoucherDetailActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private FrameLayout closeFrameLayout;
    private FrameLayout howToFrameLayout;
    private FrameLayout rootFrameLayout;
    private FrameLayout thankYouFrameLayout;
    private FrameLayout barcodeFrameLayout;
    private FrameLayout useDescriptionFrameLayout;
    private RelativeLayout voucherNameRelativeLayout;
    private TextView completedTextView;
    private TextView voucherNameTextView;
    private TextView expiryDateTextView;
    private WebView howToWebView;
    private TextView barcodeTextView;
    private ImageView voucherImageView;
    private Button validateCodeButton;
    private ImageView barcodeImageView;

//  POPUP VIEW
    private View passcodeView;
    private TextView vendorPass;
    private EditText passCode;
    private TextView text1;
    private Button submit;
    private FrameLayout qrCodeFrameLayout;
    private AlertDialog alertDialog;
    private View qrCodeView;
    private ZXingScannerView zXingScannerView;


    private NewEVoucher newEVoucher;
    private int type;
    private CheckInternetConnection checkInternetConnection;
    private SharedDatabase sharedDatabase;
    private Realm realm;
    private AlertDialog alertDialog1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_voucher_detail);
        sharedDatabase = new SharedDatabase(getApplicationContext());
        checkInternetConnection = new CheckInternetConnection(getApplicationContext());
        realm = AppController.getRealm();
        initialize();
    }

    private void initialize(){
        closeFrameLayout = findViewById(R.id.closeFrameLayout);
        voucherNameTextView = findViewById(R.id.voucherNameTextView);
        expiryDateTextView = findViewById(R.id.voucherExpiryTextView);
        howToWebView = findViewById(R.id.howToWebView);
        barcodeTextView = findViewById(R.id.barcodeTextView);
        voucherImageView = findViewById(R.id.voucherImageView);
        validateCodeButton = findViewById(R.id.validateCodeButton);
        barcodeImageView = findViewById(R.id.barcodeImageView);
        howToFrameLayout = findViewById(R.id.howToFrameLayout);
        thankYouFrameLayout = findViewById(R.id.thanksNoteFrameLayout);
        barcodeFrameLayout = findViewById(R.id.barcodeFrameLayout);
        completedTextView = findViewById(R.id.completedTextView);
        voucherNameRelativeLayout = findViewById(R.id.voucherNameRelativeLayout);
        rootFrameLayout = findViewById(R.id.rootFrameLayout);
        useDescriptionFrameLayout = findViewById(R.id.useDescriptionFrameLayout);

        closeFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        realm = AppController.getRealm();
        int newEvoucherId = getIntent().getIntExtra("eVoucher",0);
        newEVoucher = realm.where(NewEVoucher.class).equalTo("id",newEvoucherId).findFirst();
        type = getIntent().getIntExtra("type",0);
        validateCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!newEVoucher.getUsedValidationType().equalsIgnoreCase("True")){
                    initVendorView();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(NewVoucherDetailActivity.this);
                    View popUpView = LayoutInflater.from(NewVoucherDetailActivity.this).inflate(R.layout.confirmation_popup, null);
                    TextView descriptionText = (TextView) popUpView.findViewById(R.id.confirmation_text);
                    RelativeLayout logo = (RelativeLayout) popUpView.findViewById(R.id.logo);
                    popUpView.findViewById(R.id.points_text).setVisibility(View.GONE);
                    Button yesButton = (Button) popUpView.findViewById(R.id.submit);
                    Button noButton = (Button) popUpView.findViewById(R.id.cancel);

                    descriptionText.setText("You have redeemed this reward");
                    yesButton.setText("YES");
                    noButton.setText("NO");

                    alertDialog1 = builder.create();

                    logo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog1.dismiss();
                        }
                    });

                    noButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog1.dismiss();
                        }
                    });

                    yesButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog1.dismiss();
                            handleUseValidation();
                        }
                    });

                    builder.setView(popUpView);
                    alertDialog1 = builder.create();
                    alertDialog1.show();
                    alertDialog1.setCancelable(false);

//                    initUsed();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initViews();
    }

    private void initViews(){
        voucherNameTextView.setText(newEVoucher.getName());
        barcodeTextView.setText(newEVoucher.geteVoucher());
        if(type == 1) {
            initUsed();
        }else{
            initUnused();
        }
        initBarcode();
    }

    private void initBarcode(){
        String text= newEVoucher.geteVoucher(); // Whatever you need to encode in Barcode
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.CODE_128,100,300);
            int height = bitMatrix.getHeight();
            int width = bitMatrix.getWidth();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++){
                for (int y = 0; y < height; y++){
                    bmp.setPixel(x, y, bitMatrix.get(x,y) ? Color.BLACK : Color.WHITE);
                }
            }
            barcodeImageView.setImageBitmap(bmp);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void initUsed(){
        Long dateLong = InterfaceManager.sharedInstance().utcToDateInMillis(newEVoucher.getValidUntil());
        String date = InterfaceManager.sharedInstance().millisToDateInVoucher(dateLong);
        howToFrameLayout.setVisibility(View.GONE);
        thankYouFrameLayout.setVisibility(View.VISIBLE);
        barcodeFrameLayout.setVisibility(View.VISIBLE);
        completedTextView.setVisibility(View.VISIBLE);
        validateCodeButton.setBackground(ContextCompat.getDrawable(this,R.drawable.gray_rounded_background));
        expiryDateTextView.setText("Expires on "+date);
        if(newEVoucher.getUsedValidationType().equalsIgnoreCase("True")) {
            validateCodeButton.setText("USED");
        }else{
            validateCodeButton.setText("VALIDATED");
        }
        useDescriptionFrameLayout.setVisibility(View.GONE);
        voucherNameRelativeLayout.setBackgroundColor(ContextCompat.getColor(this,R.color.light_gray2));
        Glide.with(this)
                .load(Constants.BASEURL+newEVoucher.getDisplayImagePath())
                .apply(RequestOptions.bitmapTransform(new GrayscaleTransformation()))
                .into(voucherImageView);
        validateCodeButton.setClickable(false);
    }

    private void initUnused(){
        Long dateLong = InterfaceManager.sharedInstance().utcToDateInMillis(newEVoucher.getValidUntil());
        String date = InterfaceManager.sharedInstance().millisToDateInVoucher(dateLong);
        String termsAndConditions = "";
        Glide.with(this).load(Constants.BASEURL+newEVoucher.getDisplayImagePath()).into(voucherImageView);
        expiryDateTextView.setText("Expires on "+date);
//        howToTextView.setMovementMethod(new ScrollingMovementMethod());
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//            howToTextView.setText(Html.fromHtml(newEVoucher.getTermsAndConditions(),Html.FROM_HTML_SEPARATOR_LINE_BREAK_LIST,null ,new UITagHandler()).toString());
//        } else {
//            howToTextView.setText(Html.fromHtml(newEVoucher.getTermsAndConditions(), null, new UITagHandler()).toString());
//        }
        useDescriptionFrameLayout.setVisibility(View.GONE);
        validateCodeButton.setText("VALIDATE CODE");
        if(newEVoucher.getUsedValidationType().equalsIgnoreCase("True")){
            validateCodeButton.setText("USED");
            useDescriptionFrameLayout.setVisibility(View.VISIBLE);
        }
        termsAndConditions = "<head><body style="+"color:white; font-size:4px;"+">" + newEVoucher.getTermsAndConditions()+"</body></head>";
        howToWebView.loadData(termsAndConditions, "text/html" , null);
        howToWebView.setBackgroundColor(ContextCompat.getColor(this,R.color.red_orange));

    }

    private void initVendorView(){
        final LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        passcodeView = layoutInflater.inflate(R.layout.dialog_vendor_passcode_input, null);
        vendorPass = (TextView) passcodeView.findViewById(R.id.vender_Pass);
        passCode = (EditText) passcodeView.findViewById(R.id.pass_code);
        text1 = (TextView) passcodeView.findViewById(R.id.text1);
        submit = (Button) passcodeView.findViewById(R.id.submit);
        qrCodeFrameLayout = (FrameLayout) passcodeView.findViewById(R.id.qr_code_image);
        RelativeLayout imageView = (RelativeLayout) passcodeView.findViewById(R.id.logo);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        builder.setView(passcodeView);
        alertDialog = builder.create();
        alertDialog.show();
        qrCodeFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final LayoutInflater layoutInflaterQRCode = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                qrCodeView = layoutInflaterQRCode.inflate(R.layout.layout_qr_scan,null);
                final FrameLayout backFrameLayout = qrCodeView.findViewById(R.id.layout_qr_scan_backFrameLayout);
                final FrameLayout scannerFrameLayout = qrCodeView.findViewById(R.id.layout_qr_scan_scannerFrameLayout);
                final FrameLayout backgroundFrameLayout = qrCodeView.findViewById(R.id.layout_qr_scan_backgroundFrameLayout);
                zXingScannerView = new ZXingScannerView(NewVoucherDetailActivity.this);

                checkPermissions();
                zXingScannerView.setResultHandler(NewVoucherDetailActivity.this);

                ViewTreeObserver viewTreeObserver = rootFrameLayout.getViewTreeObserver();
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        int h = scannerFrameLayout.getHeight();
                        int w = scannerFrameLayout.getWidth();
                        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,w,new DisplayMetrics())
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

                alertDialog.hide();
                qrCodeView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                rootFrameLayout.addView(qrCodeView);

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
                String vendorPasscode = passCode.getText().toString();
                if (!passCode.getText().toString().isEmpty()) {
                    if (checkInternetConnection.isConnectingToInternet()) {
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("wallet_id", newEVoucher.getId());
                            jsonObject.put("reward",newEVoucher.getRewardId());
                            jsonObject.put("password", vendorPasscode);
                            StringEntity entity = new StringEntity(jsonObject.toString());
                            alertDialog.dismiss();
                            redeemVoucher(entity);
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
    }

    private void handleUseValidation() {
        if (checkInternetConnection.isConnectingToInternet()) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("wallet_id", newEVoucher.getId());
                jsonObject.getString("wallet_id");
                StringEntity entity = new StringEntity(jsonObject.toString());
                redeemVoucher(entity);
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            connectivityMessage("Waiting for Network!");
        }
    }

    private void initiateRequest(){
        setCamera();
    }

    void setCamera() {
        zXingScannerView.setResultHandler(this);
        zXingScannerView.startCamera();
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
    public void handleResult(Result result) {
        Log.v("scannnnnnnnnner = ", result.getText());
        Log.v("scannnnnnnnnner = ", result.getBarcodeFormat().toString());

        zXingScannerView.stopCamera();
        // HANDLE RESULT
        passCode.setText(result.toString());
        rootFrameLayout.removeView(qrCodeView);
        alertDialog.show();
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

    @Override
    public void onBackPressed() {
        if (!newEVoucher.getUsedValidationType().equalsIgnoreCase("True")) {
            zXingScannerView.stopCamera();
            rootFrameLayout.removeView(qrCodeView);
            alertDialog.show();
        }else{
            finish();
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


    public void redeemVoucher(final StringEntity stringEntity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(NewVoucherDetailActivity.this);
        alertDialog1 = builder.create();
        alertDialog1.show();
        alertDialog1.setCancelable(false);
        String authProvider = SettingsManager.getInstance().getAuthProvider();
        String token = sharedDatabase.getToken();
        Loader.showProgressDialog(this);
        HashMap<String, String> paramMap = new HashMap<String, String>();

        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(80000);
        client.addHeader("USER-AGENT", AppController.useragent);
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("Content-Type", "application/json");
        String extensionUrl = Constants.NEWEVOUCHER_REDEMPTION;
        if(newEVoucher.getUsedValidationType().equalsIgnoreCase("True")) {
            extensionUrl = Constants.NEWEVOUCHER_USE_VALIDATION;
        }
            String url = Constants.BASEURL + extensionUrl;
        client.post(this, url, stringEntity, "application/json", new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        Loader.dialogDissmiss(getApplicationContext());
                        if (statusCode == 200) {
                            int voucherid = newEVoucher.getId();
                            if(alertDialog1.isShowing()){
                                alertDialog1.dismiss();
                            }
                            Toast.makeText(NewVoucherDetailActivity.this, "Success", Toast.LENGTH_SHORT).show();
                            realm.beginTransaction();
                            NewEVoucher newEVoucher = realm.where(NewEVoucher.class).equalTo("id", voucherid).findFirst();
                            newEVoucher.setUsed(true);
                            realm.copyToRealmOrUpdate(newEVoucher);
                            realm.commitTransaction();
                            initUsed();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        System.out.println("hheehgjh fail" + res);
                        Loader.dialogDissmiss(getApplicationContext());

                        if (statusCode == 500) {
                            if (getApplicationContext() != null) {
                                connectivityMessage("We've encountered a technical error.our team is working on it. please try again later");
                            }
                        }else{
                            connectivityMessage(res);
                        }
                    }
                }
        );

    }
}
