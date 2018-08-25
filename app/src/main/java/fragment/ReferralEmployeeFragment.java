package fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
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

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.root.skor.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;

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

public class ReferralEmployeeFragment extends Fragment {

    EditText postion, name, phone, mobile, email, comment;
    Button referanemployee;
    LinearLayout panel;
    LinearLayout linearLayout;
    CoordinatorLayout coordinatorLayout;
    CheckInternetConnection checkInternetConnection;
    TextView saveReferral;
    StringEntity entity;
    public SharedDatabase sharedDatabase;
    public String token;
    View rootView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_employee_referral, null);
        checkInternetConnection = new CheckInternetConnection(getActivity());
        linearLayout = (LinearLayout) rootView.findViewById(R.id.lin);
        final TextView textView1 = (TextView) rootView.findViewById(R.id.yourRewards);
        final TextView textView2 = (TextView) rootView.findViewById(R.id.allRewards);
        postion = (EditText) rootView.findViewById(R.id.position);
        name = (EditText) rootView.findViewById(R.id.nameofthecandidate);
        phone = (EditText) rootView.findViewById(R.id.hponehome);
        sharedDatabase = new SharedDatabase(getActivity());
        token = sharedDatabase.getToken();
        coordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.myCoordinatorLayout);
        mobile = (EditText) rootView.findViewById(R.id.phonemobile);
        saveReferral = (TextView) rootView.findViewById(R.id.save);
        email = (EditText) rootView.findViewById(R.id.email);
        comment = (EditText) rootView.findViewById(R.id.comment);
        referanemployee = (Button) rootView.findViewById(R.id.referanemployee);
        comment.setFilters(new InputFilter[]{new MaxLinesInputFilter(4)});
        comment.setMovementMethod(null);

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
                    if (coordinatorLayout!= null) {
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
                    if (coordinatorLayout!= null) {
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
        referanemployee.setOnClickListener(new View.OnClickListener() {
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
                    if (coordinatorLayout!= null) {
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
                    if (coordinatorLayout!= null) {
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


    /* ******************checking condition value are right or not****************************/

    public void callReferCleintApi() {
        if (phone.getText().length() > 15) {
            if (coordinatorLayout!= null) {
                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Phone number is not valid", Snackbar.LENGTH_LONG);
                View snackBarView = snackbar.getView();
                snackBarView.setBackgroundColor(Color.RED);
                snackbar.show();
            }
        } else if (mobile.getText().length() > 15) {
            if (coordinatorLayout!= null) {
                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Phone number is not valid", Snackbar.LENGTH_LONG);
                View snackBarView = snackbar.getView();
                snackBarView.setBackgroundColor(Color.RED);
                snackbar.show();
            }
        } else if (name.getText().toString().isEmpty() || postion.getText().toString().isEmpty() || email.getText().toString().isEmpty()) {
            if (coordinatorLayout!= null) {
                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Please fill all field", Snackbar.LENGTH_LONG);
                View snackBarView = snackbar.getView();
                snackBarView.setBackgroundColor(Color.RED);
                snackbar.show();
            }
        } else {
            String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
            if (Patterns.PHONE.matcher(phone.getText().toString()).matches()) {

            } else {
                if (getActivity() != null) {
                    if (coordinatorLayout!= null) {
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Phone number is not valid", Snackbar.LENGTH_LONG);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundColor(Color.RED);
                        snackbar.show();
                    }
                }
                return;
            }
            Pattern pattern = Pattern.compile(regex);
            String emailAddress = email.getText().toString();
            Matcher matcher = pattern.matcher(emailAddress);
            JSONObject user_data = new JSONObject();
            try {
                if (matcher.matches()) {
                    user_data.put("full_name", name.getText().toString());
                    user_data.put("position", postion.getText().toString());
                    if (!comment.getText().toString().isEmpty()) {
                        user_data.put("comments", comment.getText().toString());
                    }
                    if (!mobile.getText().toString().isEmpty()) {
                        user_data.put("phone", mobile.getText().toString());
                    }
                    if (!phone.getText().toString().isEmpty()) {
                        user_data.put("phone_home", phone.getText().toString());
                    }
                    user_data.put("email", email.getText().toString());
                    user_data.put("message_type", "employee_referral");
                    entity = new StringEntity(user_data.toString());
                    if (checkInternetConnection.isConnectingToInternet()) {
                        callRefferalAPI(entity);
                    } else {
                        if (getActivity() != null) {
                            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Waiting for Network!", Snackbar.LENGTH_LONG);
                            View snackBarView = snackbar.getView();
                            snackBarView.setBackgroundColor(Color.RED);
                            snackbar.show();
                        }
                    }


                } else {
                    if (getActivity() != null) {
                        if (coordinatorLayout!= null) {
                            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Email Address is not valid", Snackbar.LENGTH_LONG);
                            View snackBarView = snackbar.getView();
                            snackBarView.setBackgroundColor(Color.RED);
                            snackbar.show();
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /* ******************employee referal api for post method****************************/

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
                        name.setText("");
                        postion.setText("");
                        phone.setText("");
                        mobile.setText("");
                        email.setText("");
                        comment.setText("");
                        if (getActivity() != null) {
                            if (coordinatorLayout!= null) {
                                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Successfully Submit", Snackbar.LENGTH_LONG);
                                View snackBarView = snackbar.getView();
                                snackBarView.setBackgroundColor(Color.RED);
                                snackbar.show();
                                AppController.getInstance().getMixpanelAPI().track("EmployeeReferral");
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        System.out.println("hheehgjh fail" + res);
                        String res12 = null;
                        Loader.dialogDissmiss(getActivity());
                        if (statusCode == 401) {
                            UserManager.getInstance().logOut(getActivity());
                        }

                        if (statusCode == 400) {

                            if (coordinatorLayout!= null) {
                                Snackbar snackbar = Snackbar.make(coordinatorLayout, ""+res, Snackbar.LENGTH_LONG);
                                View snackBarView = snackbar.getView();
                                snackBarView.setBackgroundColor(Color.RED);
                                snackbar.show();

                            }
                        }
                        if (statusCode == 500) {
                            if (coordinatorLayout!= null) {
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

        /**
         * Counts the number occurrences of the given char.
         *
         * @param string         the string
         * @param charAppearance the char
         * @return number of occurrences of the char
         */
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
                    UserManager.getInstance().logOut(getActivity());
                }
            });
        }
    }


}
