package activity.skorchat;

import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.quickblox.users.model.QBUser;
import com.root.skor.R;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;
import singleton.InterfaceManager;
import utils.ArialRegularTextView;

public class CallActivity extends AppCompatActivity {
    ArialRegularTextView recipientNameTextView, callStatusTextView;
    CircleImageView recipientProfilePictureImageView;
    TextView recipientProfilePictureTextView;
    QBUser qbUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        findViewById();
    }

    @Override
    protected void onResume() {
        super.onResume();
        qbUser = (QBUser) getIntent().getSerializableExtra("opposingUser");
        initialize();
    }

    private void findViewById(){
        recipientNameTextView = (ArialRegularTextView) findViewById(R.id.activity_call_recipientsNameTextView);
        callStatusTextView = (ArialRegularTextView) findViewById(R.id.activity_calling_callStatusTextView);
        recipientProfilePictureImageView = (CircleImageView) findViewById(R.id.activity_call_recipientsPhotoImageView);
        recipientProfilePictureTextView = (TextView) findViewById(R.id.activity_call_profilePictureTextView);
    }

    private void initialize(){
        recipientNameTextView.setText(qbUser.getFullName());
        String qbPhoto = qbUser.getCustomData();
        recipientProfilePictureTextView.setText(InterfaceManager.sharedInstance().getInitial(qbUser.getFullName()));
        recipientProfilePictureTextView.setAllCaps(true);
        ColorDrawable cd = new ColorDrawable(ContextCompat.getColor(CallActivity.this,R.color.loading_yellow));
        recipientProfilePictureImageView.setImageDrawable(cd);
        recipientProfilePictureImageView.setVisibility(View.VISIBLE);
        String photoUrl = "";
        try {
            if(qbPhoto!=null) {
                if(!qbPhoto.equalsIgnoreCase("")) {
                    JSONObject photoJSONObject = new JSONObject(qbPhoto);
                    photoUrl = photoJSONObject.getString("avatar_url");
                    Glide.with(this).load(photoUrl)
                            .apply(RequestOptions.placeholderOf(cd))
                            .apply(RequestOptions.noAnimation())
                            .into(recipientProfilePictureImageView);
                    recipientProfilePictureTextView.setVisibility(View.GONE);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
