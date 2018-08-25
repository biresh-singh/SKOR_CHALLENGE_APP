package activity.skorchat;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.quickblox.auth.session.QBSessionManager;
import com.quickblox.auth.session.QBSettings;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.utils.DialogUtils;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.ServiceZone;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.root.skor.R;

import org.jivesoftware.smack.SmackException;

import activity.history.FeedBackSupportActivity;
import database.SharedDatabase;
import de.hdodenhof.circleimageview.CircleImageView;
import singleton.InterfaceManager;
import singleton.SettingsManager;
import utils.AppController;
import utils.ArialRegularTextView;

public class SkorChatSettingsActivity extends AppCompatActivity {
    CircleImageView profileImageView;
    ArialRegularTextView profileNameTextView;
    LinearLayout backButtonLinearLayout;
    TextView profilePictureTextView;
    QBUser currentUser = QBChatService.getInstance().getUser();
    String token;
    SharedDatabase sharedDatabase;
    RelativeLayout giveFeedbackButton;
    RelativeLayout technicalSupportButton, merchantSupportButton;
    private static final String TAG = "SkorChatSettingsActivit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skor_chat_settings);
        sharedDatabase = new SharedDatabase(getApplicationContext());
        token = sharedDatabase.getToken();

//        QBSettings.getInstance().init(this, "12",
//                "7ZuALddq5z3GJe2", "kD-X6N-5X7shBnr");
//        QBSettings.getInstance().setAccountKey("UxUxELheGmvVTWvFsA1R");
//
//        QBSettings.getInstance().setEndpoints("https://apibrainstorm.quickblox.com", "chatbrainstorm.quickblox.com", ServiceZone.PRODUCTION);
//        QBSettings.getInstance().setZone(ServiceZone.PRODUCTION);

//        Log.e(TAG, "onCreate: "+ QBSettings.getInstance().toString() );

        findViewById();
        initializeListener();
        initialize();
    }

    private void findViewById() {
        profileImageView = (CircleImageView) findViewById(R.id.activity_skor_chat_settings_profilePictureImageView);
        profileNameTextView = (ArialRegularTextView) findViewById(R.id.activity_skor_chat_settings_profileNameTextView);
        profilePictureTextView = (TextView) findViewById(R.id.activity_skor_chat_settings_profilePictureTextView);
        backButtonLinearLayout = (LinearLayout) findViewById(R.id.activity_skor_chat_settings_backButton);
        giveFeedbackButton = (RelativeLayout) findViewById(R.id.activity_skor_chat_settings_giveFeedbackRelativeLayout);
        technicalSupportButton = (RelativeLayout) findViewById(R.id.rl_technicalSupport);
        merchantSupportButton = (RelativeLayout) findViewById(R.id.rl_merchantSupport);
    }

    private void initializeListener() {
        View.OnClickListener backTapped = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        };
        backButtonLinearLayout.setOnClickListener(backTapped);


        View.OnClickListener giveFeedbackTapped = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SkorChatSettingsActivity.this, FeedBackSupportActivity.class);
                startActivity(intent);
            }
        };
        giveFeedbackButton.setOnClickListener(giveFeedbackTapped);
        technicalSupportButton.setOnClickListener(technicalSupportTapped);
        merchantSupportButton.setOnClickListener(merchantSupportTapped);
    }

    View.OnClickListener technicalSupportTapped = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Log.e(TAG, "onClick: " + sharedDatabase.getCustomerSupportTechnicalSupportEmail());
            QBUsers.getUserByEmail(sharedDatabase.getCustomerSupportTechnicalSupportEmail()).performAsync(new QBEntityCallback<QBUser>() {
                @Override
                public void onSuccess(final QBUser qbUser, Bundle bundle) {
                    QBChatDialog dialog = DialogUtils.buildPrivateDialog(qbUser.getId());


                    QBRestChatService.createChatDialog(dialog).performAsync(new QBEntityCallback<QBChatDialog>() {
                        @Override
                        public void onSuccess(final QBChatDialog qbChatDialog, Bundle params) {
//                            Toast.makeText(getApplicationContext(), "success create chat dialog", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(SkorChatSettingsActivity.this, ChattingActivity.class);
                            SettingsManager.getInstance().setQbChatDialog(qbChatDialog);
                            intent.putExtra("opposingUser", qbUser);
                            startActivity(intent);
                        }

                        @Override
                        public void onError(QBResponseException responseException) {
                            Toast.makeText(getApplication(), "failed create chat dialog", Toast.LENGTH_SHORT).show();
                        }
                    });


                }

                @Override
                public void onError(QBResponseException e) {
                    Toast.makeText(SkorChatSettingsActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    View.OnClickListener merchantSupportTapped = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            QBUsers.getUserByEmail("info@skorpoints.com").performAsync(new QBEntityCallback<QBUser>() {
                @Override
                public void onSuccess(final QBUser qbUser, Bundle bundle) {
                    QBChatDialog dialog = DialogUtils.buildPrivateDialog(qbUser.getId());


                    QBRestChatService.createChatDialog(dialog).performAsync(new QBEntityCallback<QBChatDialog>() {
                        @Override
                        public void onSuccess(final QBChatDialog qbChatDialog, Bundle params) {
//                            Toast.makeText(getApplicationContext(), "success create chat dialog", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(SkorChatSettingsActivity.this, ChattingActivity.class);
                            SettingsManager.getInstance().setQbChatDialog(qbChatDialog);
                            intent.putExtra("opposingUser", qbUser);
                            startActivity(intent);
                        }

                        @Override
                        public void onError(QBResponseException responseException) {
                            Toast.makeText(getApplication(), "failed create chat dialog", Toast.LENGTH_SHORT).show();
                        }
                    });


                }

                @Override
                public void onError(QBResponseException e) {
                    Toast.makeText(SkorChatSettingsActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    private void initialize() {
        String fullName;
        if (sharedDatabase.getLastName().equals("")) {
            fullName = sharedDatabase.getFirstName();
        } else {
            fullName = sharedDatabase.getFirstName() + " " + sharedDatabase.getLastName();
        }
        profilePictureTextView.setText(InterfaceManager.sharedInstance().getInitial(fullName));
        profilePictureTextView.setAllCaps(true);
        profilePictureTextView.setVisibility(View.VISIBLE);
        ColorDrawable cd = new ColorDrawable(ContextCompat.getColor(SkorChatSettingsActivity.this, R.color.loading_yellow));
        profileImageView.setImageDrawable(cd);
        if (currentUser.getCustomData() != null && !currentUser.getCustomData().equals("")) {
            Glide.with(SkorChatSettingsActivity.this).load(sharedDatabase.getProfilePic()).into(profileImageView);
            profilePictureTextView.setVisibility(View.GONE);
        }
        profileNameTextView.setText(fullName);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        AppController.getInstance().initQuickblox(sharedDatabase.getQuckblox_appId(), sharedDatabase.getQuckblox_authKey(), sharedDatabase.getQuickblox_authSecret());
    }
}
