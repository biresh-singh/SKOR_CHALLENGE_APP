package activity.history;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.root.skor.BuildConfig;
import com.root.skor.R;

import activity.userprofile.MainActivity;
import constants.Constants;
import database.SharedDatabase;
import utils.CircleImageView;


public class BirthdayWishActivity extends AppCompatActivity {
    private CircleImageView userImage;
    private TextView announcementFrom;
    private TextView announcementDetail;
    private TextView sendYourWishes;
    private LinearLayout back;

    private String selectedAnnouncementjson;
    private String emailAdddress;
    private String email;
    private String PACKAGE_NAME;
    private String type;
    private String image;
    private boolean isToday = false;
    private String id;

    public SharedDatabase sharedDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.birthday_wish);
        userImage = (CircleImageView) findViewById(R.id.imageurl);
        announcementFrom = (TextView) findViewById(R.id.announcement_from);
        announcementDetail = (TextView) findViewById(R.id.desc_text);
        sendYourWishes = (TextView) findViewById(R.id.sendwish_button);
        back = (LinearLayout) findViewById(R.id.back);

        type = getIntent().getStringExtra("type");
        email = getIntent().getStringExtra("email");
        image = getIntent().getStringExtra("image");

        if (type != null) {
            if (type.equals("Birthday")) {
                announcementFrom.setText("It’s your colleague birthday today");
            } else {
                announcementFrom.setText("It’s your colleague anniversary today");
            }
        }

        announcementDetail.setText(email);
        Glide.with(this).load(Constants.BASEURL + image).into(userImage);

//        isToday = getIntent().getBooleanExtra("istoday", false);
//        Calendar now = Calendar.getInstance();
//        PACKAGE_NAME = getApplicationContext().getPackageName();
//        int currentMonth1 = now.get(Calendar.MONTH) + 1;
//        int currentDay = now.get(Calendar.DATE);
//        int currentYear = now.get(Calendar.YEAR);
//        sharedDatabase=new SharedDatabase(getApplicationContext());
//        email=sharedDatabase.getEmail();
//        selectedAnnouncementjson = getIntent().getStringExtra("selected_announcement_json");
//        try {
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//            Date currentDate = sdf.parse(currentYear + "-" + currentMonth1 + "-" + currentDay);
//            JSONObject jsonObject = new JSONObject(selectedAnnouncementjson);
//            emailAdddress = jsonObject.getString("user_email");
//            String date = jsonObject.getString("date");
//            Date jsonDate = sdf.parse(date);
//            id = jsonObject.getString("id");
//            String month = jsonObject.getString("month");
//            int day = jsonObject.getInt("day");
//            type = jsonObject.getString("type");
//            Glide.with(getApplicationContext()).load(Constants.BASEURL + jsonObject.getString("thumbnail")).into(userImage);
//            if (type.equals("Birthday")) {
//                if (!email.equals(emailAdddress)) {
//                    String previousMOnth = dateformat(month);
//                    if (currentDate.compareTo(jsonDate) > 0) {
//                        announcementFrom.setText("It was your colleague birthday on " + day + "" + previousMOnth);
//                    } else if (currentDate.compareTo(jsonDate) < 0) {
//                        announcementFrom.setText("It's  your colleague birthday on " + day + "" + previousMOnth);
//                    } else if (currentDate.compareTo(jsonDate) == 0) {
//                        announcementFrom.setText("It's your colleague birthday today.");
//                    }
//
//                } else {
//
//                    String previousMOnth = dateformat(month);
//                    if (currentDate.compareTo(jsonDate) > 0) {
//                        announcementFrom.setText("It was your  birthday on " + day + "" + previousMOnth);
//                    } else if (currentDate.compareTo(jsonDate) < 0) {
//                        announcementFrom.setText("It's  your  birthday  on " + day + "" + previousMOnth);
//                    } else if (currentDate.compareTo(jsonDate) == 0) {
//                        announcementFrom.setText("It's your  birthday today.");
//                    }
//                    sendYourWishes.setVisibility(View.GONE);
//                }
//            } else {
//                if (!email.equals(emailAdddress)) {
//                    String previousMOnth = dateformat(month);
//                    if (currentDate.compareTo(jsonDate) > 0) {
//                        announcementFrom.setText("It was your colleague anniversary on " + day + "" + previousMOnth);
//                    } else if (currentDate.compareTo(jsonDate) < 0) {
//                        announcementFrom.setText("It's  your colleague anniversary  on " + day + "" + previousMOnth);
//                    } else if (currentDate.compareTo(jsonDate) == 0) {
//                        announcementFrom.setText("It's your colleague anniversary today.");
//                    }
//
//                } else {
//                    String previousMOnth = dateformat(month);
//                    if (currentDate.compareTo(jsonDate) > 0) {
//                        announcementFrom.setText("It was your  anniversary on " + day + "" + previousMOnth);
//                    } else if (currentDate.compareTo(jsonDate) < 0) {
//                        announcementFrom.setText("It's  your  anniversary on " + day + "" + previousMOnth);
//                    } else if (currentDate.compareTo(jsonDate) == 0) {
//                        announcementFrom.setText("It's your  anniversary today.");
//                    }
//                    sendYourWishes.setVisibility(View.GONE);
//                }
//            }
//            announcementDetail.setText(Html.fromHtml(Html.fromHtml(jsonObject.getString("user_email")).toString()));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        } catch (JSONException ex) {
//            ex.printStackTrace();
//        }

        sendYourWishes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equals("Birthday")) {
                    String message = "\n\n" + "Wish you a wonderful birthday." + "\n\n\n\n\n" + getString(R.string.intent_message);
                    MainActivity.isInternetConnection = false;
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                    i.putExtra(Intent.EXTRA_SUBJECT, "Happy Birthday");
                    if (BuildConfig.FLAVOR.equals("SKOR")) {
                        i.putExtra(Intent.EXTRA_TEXT, "Wish you a wonderful birthday");
                    } else {
                        i.putExtra(Intent.EXTRA_TEXT, message );
                    }
                    try {
                        startActivity(Intent.createChooser(i, "Send mail..."));
                    } catch (android.content.ActivityNotFoundException ex) {
                        if (getApplicationContext() != null) {
                            connectivityMessage("There are no email clients installed.");
                        }
                    }
                } else {
                    String message = "\n\n" + "Wish you a wonderful Anniversary." + "\n\n\n\n\n" + getString(R.string.intent_message);
                    MainActivity.isInternetConnection = false;
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                    i.putExtra(Intent.EXTRA_SUBJECT, "Happy Anniversary");
                    if (BuildConfig.FLAVOR.equals("SKOR")) {
                        i.putExtra(Intent.EXTRA_TEXT, "Wish you a wonderful anniversary");
                    } else {
                        i.putExtra(Intent.EXTRA_TEXT, message );
                    }
                    try {
                        startActivity(Intent.createChooser(i, "Send mail..."));
                    } catch (android.content.ActivityNotFoundException ex) {
                        if (getApplicationContext() != null) {
                            connectivityMessage( "There are no email clients installed.");
                        }
                    }
                }
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.isInternetConnection = false;
                finish();
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
    protected void onResume() {
        super.onResume();

//        JSONObject props = new JSONObject();
//        try {
//            props.put("greetingId", id);
//            AppController.getInstance().getMixpanelAPI().track("GreetingDetail");
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    private String dateformat(String created) {

        if (created.equals("1")) {
            created = " Jan";
        }
        if (created.equals("2")) {
            created = " Feb";
        }
        if (created.equals("3")) {
            created = " Mar";
        }
        if (created.equals("4")) {
            created = " Apr";
        }
        if (created.equals("5")) {
            created = " May";
        }
        if (created.equals("6")) {
            created = " Jun";
        }
        if (created.equals("7")) {
            created = " Jul";
        }
        if (created.equals("8")) {
            created = " Aug";
        }
        if (created.equals("9")) {
            created = " Sep";
        }
        if (created.equals("10")) {
            created = " Oct";
        }
        if (created.equals("11")) {
            created = " Nov";
        }
        if (created.equals("12")) {
            created = " Dec";
        }

        return created;
    }
}

