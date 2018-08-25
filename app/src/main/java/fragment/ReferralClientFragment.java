package fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.root.skor.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import InternetConnection.CheckInternetConnection;
import activity.userprofile.MainActivity;
import constants.Constants;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import database.SharedDatabase;
import event.RefreshTokenEvent;
import singleton.SettingsManager;
import singleton.UserManager;
import utils.AppController;
import utils.Loader;


public class ReferralClientFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    LinearLayout panel;
    LinearLayout linearLayout;
    EditText fullname, company, title, phone, email, comment;
    Button submit;
    TextView saveReferral;
    StringEntity entity;
    CoordinatorLayout coordinatorLayout;
    CheckInternetConnection checkInternetConnection;
    View rootView;
    public static final String DATEPICKER_TAG = "datepicker";
    TextView textView1, textView2;
    public SharedDatabase sharedDatabase;
    public String token;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_referal_client, container, false);
        linearLayout = (LinearLayout) rootView.findViewById(R.id.lin);
        saveReferral = (TextView) rootView.findViewById(R.id.save);
        sharedDatabase = new SharedDatabase(getActivity());
        token = sharedDatabase.getToken();
        sharedDatabase.setPosition(10);
        sharedDatabase.setType("all");
        checkInternetConnection = new CheckInternetConnection(getActivity());
        final Calendar calendar = Calendar.getInstance();
        final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), false);
        if (savedInstanceState != null) {
            DatePickerDialog dpd = (DatePickerDialog) getFragmentManager().findFragmentByTag(DATEPICKER_TAG);
            if (dpd != null) {
                dpd.setOnDateSetListener(this);
            }
        }
        textView1 = (TextView) rootView.findViewById(R.id.yourRewards);
        textView2 = (TextView) rootView.findViewById(R.id.allRewards);

        fullname = (EditText) rootView.findViewById(R.id.fullname);
        company = (EditText) rootView.findViewById(R.id.company);
        title = (EditText) rootView.findViewById(R.id.title);
        phone = (EditText) rootView.findViewById(R.id.phone);
        email = (EditText) rootView.findViewById(R.id.email);
        comment = (EditText) rootView.findViewById(R.id.comment);
        coordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.myCoordinatorLayout);
        fullname.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        company.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        title.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        phone.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        email.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        comment.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "appfont/RobotoCondensed-Regular.ttf"));
        submit = (Button) rootView.findViewById(R.id.submit);
        comment.setFilters(new InputFilter[]{new MaxLinesInputFilter(4)});
        comment.setMovementMethod(null);
//        getDeviceResolution();
        textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetConnection.isConnectingToInternet()) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
                    NavigationDrawerFragment.NavigationDrawerCallbacks navigataionCallback = null;
                    navigataionCallback = (NavigationDrawerFragment.NavigationDrawerCallbacks) getActivity();
                    navigataionCallback.onNavigationDrawerItemSelected(10, "all");
                } else {
                    if (coordinatorLayout != null) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.RED);
                        snackbar.show();
                    }
                }
            }
        });

        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetConnection.isConnectingToInternet()) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
                    NavigationDrawerFragment.NavigationDrawerCallbacks navigataionCallback = null;
                    navigataionCallback = (NavigationDrawerFragment.NavigationDrawerCallbacks) getActivity();
                    navigataionCallback.onNavigationDrawerItemSelected(11, "all");
                } else {
                    if (coordinatorLayout != null) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.RED);
                        snackbar.show();
                    }
                }
            }
        });
        panel = (LinearLayout) rootView.findViewById(R.id.menupanel);
        panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetConnection.isConnectingToInternet()) {

                    try {
                        callReferCleintApi();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    if (coordinatorLayout != null) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.RED);
                        snackbar.show();
                    }
                }
            }
        });
        saveReferral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetConnection.isConnectingToInternet()) {
                    try {
                        callReferCleintApi();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    if (coordinatorLayout != null) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.RED);
                        snackbar.show();
                    }
                }
            }
        });

        return rootView;
    }

    public void getDeviceResolution() {
        if (getResources().getConfiguration().smallestScreenWidthDp >= 240) {
            textView1.setTextSize(10);
            textView2.setTextSize(10);
        } else if (getResources().getConfiguration().smallestScreenWidthDp >= 320) {
//            textView1.setTextSize(8);
//            textView2.setTextSize(8);
        } else if (getResources().getConfiguration().smallestScreenWidthDp >= 480) {
            //textView1.setTextSize(8);
            //textView2.setTextSize(8);
        } else if (getResources().getConfiguration().smallestScreenWidthDp >= 600) {
//            textView1.setTextSize(8);
//            textView2.setTextSize(8);
        } else {
//            textView1.setTextSize(8);
//            textView2.setTextSize(8);
        }
    }

    /* ****************checking condition for values are right or not******************************/

    public void callReferCleintApi() {


        if (phone.getText().length() > 15) {
            if (coordinatorLayout != null) {
                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Phone number is not valid", Snackbar.LENGTH_LONG);
                View snackBarView = snackbar.getView();
                snackBarView.setBackgroundColor(Color.RED);
                snackbar.show();
            }
        } else if (company.getText().toString().isEmpty() || email.getText().toString().isEmpty() || fullname.getText().toString().isEmpty()) {
            if (coordinatorLayout != null) {
                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Please fill all field", Snackbar.LENGTH_LONG);
                View snackBarView = snackbar.getView();
                snackBarView.setBackgroundColor(Color.RED);
                snackbar.show();
            }
        } else {
            String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
            if (Patterns.PHONE.matcher(phone.getText().toString()).matches()) {

            } else {
                if (coordinatorLayout != null) {
                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Phone number is not valid", Snackbar.LENGTH_LONG);
                    View snackBarView = snackbar.getView();
                    snackBarView.setBackgroundColor(Color.RED);
                    snackbar.show();
                }
                return;
            }

            Pattern pattern = Pattern.compile(regex);
            String emailAddress = email.getText().toString();
            Matcher matcher = pattern.matcher(emailAddress);
            JSONObject clientReferalJsonObject = new JSONObject();
            try {
                if (matcher.matches()) {
                    if (!title.getText().toString().isEmpty()) {
                        clientReferalJsonObject.put("title", title.getText().toString());
                    }
                    clientReferalJsonObject.put("company", company.getText().toString());
                    if (!comment.getText().toString().isEmpty()) {
                        clientReferalJsonObject.put("comments", comment.getText().toString());
                    }
                    if (!phone.getText().toString().isEmpty()) {
                        clientReferalJsonObject.put("phone", phone.getText().toString());
                    }
                    clientReferalJsonObject.put("full_name", fullname.getText().toString());
                    clientReferalJsonObject.put("date", "2015-12-22");
                    clientReferalJsonObject.put("email", email.getText().toString());
                    clientReferalJsonObject.put("message_type", "client_referral");
                    entity = new StringEntity(clientReferalJsonObject.toString());
                    if (checkInternetConnection.isConnectingToInternet()) {
                        try {
                            callRefferalAPI(entity);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (coordinatorLayout != null) {
                            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                            View snackBarView = snackbar.getView();
                            snackBarView.setBackgroundColor(Color.RED);
                            snackbar.show();
                        }
                    }

                } else {
                    if (coordinatorLayout != null) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Email Address is not valid", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.RED);
                        snackbar.show();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    /* ******************Calling Refereal Api for gost method to set layout****************************/

    public void callRefferalAPI(StringEntity entity) {
        String authProvider = SettingsManager.getInstance().getAuthProvider();

        Loader.showProgressDialog(getActivity());
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(80000);
        client.addHeader("connection", "Keep-Alive");

        client.addHeader("USER-AGENT", AppController.useragent);
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("Content-Type", "application/json");
        client.post(getActivity(), Constants.BASEURL + Constants.REFERAL_API, entity, "application/json", new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        Loader.dialogDissmiss(getActivity());
                        fullname.setText("");
                        comment.setText("");
                        company.setText("");
                        title.setText("");
                        phone.setText("");
                        email.setText("");

                        if (coordinatorLayout != null) {
                            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Successfully Submit", Snackbar.LENGTH_LONG);
                            View snackBarView = snackbar.getView();
                            snackBarView.setBackgroundColor(Color.RED);
                            snackbar.show();
                            AppController.getInstance().getMixpanelAPI().track("BusinessReferral");
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        Loader.dialogDissmiss(getActivity());
                        if (statusCode == 401) {
                            UserManager.getInstance().logOut(getActivity());
                        }

                        if (statusCode == 400) {
                            if (coordinatorLayout != null) {
                                Snackbar snackbar = Snackbar.make(coordinatorLayout, "" + res, Snackbar.LENGTH_LONG);
                                View snackBarView = snackbar.getView();
                                snackBarView.setBackgroundColor(Color.RED);
                                snackbar.show();
                            }
                        }

                        if (statusCode == 500) {
                            if (coordinatorLayout != null) {
                                Snackbar snackbar = Snackbar.make(coordinatorLayout, "We've encountered a technical error.our team is working on it. please try again later", Snackbar.LENGTH_LONG);
                                View snackBarView = snackbar.getView();
                                snackBarView.setBackgroundColor(Color.RED);
                                snackbar.show();
                            }
                        }
                    }
                }
        );
    }


    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int i, int i1, int i2) {

    }

    public class MaxLinesInputFilter implements InputFilter {

        private final int mMax;

        public MaxLinesInputFilter(int max) {
            mMax = max;
        }

        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            int newLinesToBeAdded = countOccurrences(source.toString(), '\n');
            int newLinesBefore = countOccurrences(dest.toString(), '\n');
            if (newLinesBefore >= mMax - 1 && newLinesToBeAdded > 0) {
                // filter
                return "";
            }

            // do nothing
            return null;
        }

        /**
         * @return the maximum lines enforced by this input filter
         */
        public int getMax() {
            return mMax;
        }


        public int countOccurrences(String string, char charAppearance) {
            int count = 0;
            for (int i = 0; i < string.length(); i++) {
                if (string.charAt(i) == charAppearance) {
                    count++;
                }
            }
            return count;
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
            callRefferalAPI(entity);
        }else{
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
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

