package activity.userprofile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.root.skor.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import InternetConnection.CheckInternetConnection;
import constants.Constants;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.entity.StringEntity;
import database.SharedDatabase;
import utils.AppController;
import utils.Loader;


public class ResetPasswordActivity extends Activity {

    Animation shake;
    String url;
    AlertDialog alertDialog;
    EditText emailEdit;
    Button backButton;
    public static boolean istrue = false;
    Button submit;
    public static String baseurl;
    String mailValue;
    CheckInternetConnection checkInternet;
    String result;
    public String token;
    public SharedDatabase sharedDatabase;
    HttpResponse response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password);
        emailEdit = (EditText) findViewById(R.id.edit_email);
        emailEdit.setText("@sKiRs17");
        if (LoginActivity.PACKAGE_NAME.equals("com.root.unilever.ae")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(Color.parseColor("#0157ac"));
            }
        }
        sharedDatabase = new SharedDatabase(getApplicationContext());
        token = sharedDatabase.getToken();
        shake = AnimationUtils.loadAnimation(ResetPasswordActivity.this, R.anim.shake);
        emailEdit.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (emailEdit.getText().toString().equals("xxxxxx")) {
                        raiseAddServiceUrlAlertDialog(getApplicationContext());
                    } else if (emailEdit.getText().toString().equals("yyyyyy")) {
                        connectivityMessage(""+Constants.BASEURL);
                    } else {
                        mailValue = emailEdit.getText().toString().trim();
                        checkInternet = new CheckInternetConnection(ResetPasswordActivity.this);
                        if (checkInternet.isConnectingToInternet()) {
                            try {
                                getResetPassword();
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            checkInternet.showAlertDialog("Internet Connection Status", "No Internet Connection.", false);
                        }
                    }
                    return false;
                }

                return false;
            }
        });
        backButton = (Button) findViewById(R.id._backBtn);
        backButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        submit = (Button) findViewById(R.id._submit);
        submit.setOnClickListener(new View.OnClickListener() {

                                      @Override
                                      public void onClick(View v) {
                                          if (!emailEdit.getText().toString().equals("") && !emailEdit.getText().toString().equals(null)) {

                                              if (emailEdit.getText().toString().equals("@sKiRs17")) {
                                                  raiseAddServiceUrlAlertDialog(getApplicationContext());
                                              } else if (emailEdit.getText().toString().equals("yyyyyy")) {
                                                  connectivityMessage(""+ Constants.BASEURL);
                                              } else {
                                                  mailValue = emailEdit.getText().toString().trim();
                                                  checkInternet = new CheckInternetConnection(ResetPasswordActivity.this);
                                                  if (checkInternet.isConnectingToInternet()) {
                                                      try {
                                                          getResetPassword();
                                                      } catch (NullPointerException e) {
                                                          e.printStackTrace();
                                                      } catch (Exception e) {
                                                          e.printStackTrace();
                                                      }
                                                  } else {
                                                      connectivityMessage("Internet Connection Status");
                                                  }
                                              }
                                          } else {
                                              if (getApplicationContext() != null) {
                                                  connectivityMessage("Please Fill Email Id");
                                              }
                                          }


                                      }
                                  }

        );

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
  /* ********************Provide way to connect to dev environment to show AlertDialog  **************************/

    public void raiseAddServiceUrlAlertDialog(Context context) {

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ResetPasswordActivity.this);

        alertBuilder.setTitle("Service Url");
        alertBuilder.setMessage("Please Add Sevice Url.");
        final EditText editText = new EditText(getApplicationContext());
        editText.setTextColor(Color.BLACK);
        editText.setHint("Please Add Service Url");
        editText.setText("http://staging-delhi.skorpoints.com/");
        alertBuilder.setView(editText);
        alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });
        alertBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                String url = editText.getText().toString().toLowerCase().trim();
                if (!url.equals("") && !url.equals(null)) {
                    url = url.replaceAll("\\s+", "");
                    if (url.equals("https://skorpoints.com/")) {
                        Constants.BASEURL = url;
                        baseurl = url;
                        istrue = true;
                        System.out.println("Base url is" + Constants.BASEURL);
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                    } else if (url.equals("http://staging-delhi.skorpoints.com/")) {
                        Constants.BASEURL = url;
                        istrue = true;
                        baseurl = url;
                        System.out.println("Base url is" + Constants.BASEURL);
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                    } else {
                        if (getApplicationContext() != null) {
                            connectivityMessage( "Please Provide Right Plateform");
                        }
                    }


                } else {
                    if (getApplicationContext() != null) {
                        emailEdit.getText().clear();
                        connectivityMessage("Please Fill Base URL");
                    }
                }
            }
        });


        alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    public void getResetPassword() {
        Loader.showProgressDialog(this);
        StringEntity entity = null;

        try {
            JSONObject jsonObject11 = new JSONObject();
            jsonObject11.put("email", mailValue);
            entity = new StringEntity(jsonObject11.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.addHeader("USER-AGENT", AppController.useragent);
        client.addHeader("connection", "Keep-Alive");
        client.setTimeout(800000);
        String url = Constants.BASEURL + Constants.RESET_PASSWORD;
        client.post(ResetPasswordActivity.this, url, entity, "application/json", new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String res) {
                Loader.dialogDissmiss(getApplicationContext());

                if (statusCode == 200 || statusCode == 201) {
                    if (getApplicationContext() != null) {
                        connectivityMessage( "Successfully Send");
                        Intent intent = new Intent(ResetPasswordActivity.this, ResetPasswordConfirmation.class);
                        startActivity(intent);
                        finish();
                        AppController.getInstance().getMixpanelAPI().track("ForgetPassword");
                    }
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                System.out.println("hheehgjh fail" + res);
                if (getApplicationContext() != null) {
                    Loader.dialogDissmiss(getApplicationContext());
                }
                try {
                    JSONArray jsonArray = new JSONArray(res);
                    String JsonString = jsonArray.getString(0);
                    if (getApplicationContext() != null) {
                        connectivityMessage( "" + JsonString);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (statusCode == 500) {
                    if (getApplicationContext() != null) {
                        connectivityMessage( "We've encountered a technical error.our team is working on it. please try again later");
                        Loader.dialogDissmiss(getApplicationContext());
                    }
                }

            }
        });
    }
    /* ********************Provide way to connect to dev environment  **************************/

   /* private class BackgroundTask extends AsyncTask<Void, Void, Void> {


        public BackgroundTask() {
            Loader.showProgressDialog(getApplicationContext());
        }

        @Override
        protected void onPreExecute() {
            Loader.showProgressDialog(getApplicationContext());
        }

        @Override
        protected void onPostExecute(Void result) {
            Loader.dialogDissmiss(getApplicationContext());

        }

        @Override
        protected Void doInBackground(Void... params) {
            url = Constants.BASEURL + "profiles/api/users/current/reset_password/";
            String tag_json_obj = "json_obj_req";
            final JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("email", mailValue);
            } catch (JSONException ex) {

            }
            StringRequest jsonObjReq = new StringRequest(url,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            // Log.d(TAG, response.toString());

                            System.out.println("Response error isRRRRRRRRRR" + response);
                            if (getApplicationContext() != null) {
                                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                            }
                            Intent intent = new Intent(ResetPasswordActivity.this, ResetPasswordConfirmation.class);
                            startActivity(intent);
                            finish();
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                    if (getApplicationContext() != null) {
                        Toast.makeText(getApplicationContext(),
                                "Sorry, this user doesn't exist.", Toast.LENGTH_LONG).show();
                    }
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    // params.put("email", "test@rewardz.sg");
                    return params;
                }

                *//**
     * Passing some request headers
     * *//*
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("USER-AGENT", LoginActivity.useragent);
                    headers.put("Content-Type", "application/json");

                    return headers;
                }

            };

            // Adding request to request queue

            AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);


            return null;
        }

    }*/

    /*private Response.Listener<JSONObject> createMyReqSuccessListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // Do whatever you want to do with response;
                // Like response.tags.getListing_count(); etc. etc.
            }
        };
    }

    private Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Do whatever you want to do with error.getMessage();
            }
        };
    }
    *//* ******************Reset Paaword to calling Api Post Method****************************//*

    class ResetPasswordAPI extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... params) {

            String url = Constants.BASEURL + Constants.RESET_PASSWORD;
            final JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("email", mailValue);
            } catch (JSONException ex) {

            }
            String clientId = "099153c2625149bc8ecb3e85e03f0022";
            HttpEntity httpEntity = null;
            HttpClient client = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000);
            HttpPost httpPost = new HttpPost(url);
            AndroidDeviceNames deviceNames = new AndroidDeviceNames(getApplicationContext());
            httpPost.addHeader("connection", "Keep-Alive");
            httpPost.addHeader("USER-AGENT", LoginActivity.useragent);

            httpPost.setHeader("Content-type", "application/json");
            //ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();

            try {
                StringEntity stringEntity = new StringEntity(jsonObject.toString(), "UTF-8");
                // adding post params
                httpPost.setEntity(stringEntity);
                //}
                response = client.execute(httpPost);
                httpEntity = response.getEntity();

                // response = EntityUtils.toString(httpEntity);
                response = client.execute(httpPost);
                result = EntityUtils.toString(response.getEntity());

            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Loader.showProgressDialog(getApplicationContext());
        }

        @Override
        protected void onPostExecute(String result) {

            int statusCode = response.getStatusLine().getStatusCode();
            switch (statusCode) {
                case 200:
                    try {
                        if (getApplicationContext() != null) {
                            Toast.makeText(getApplicationContext(), "Successfully Send", Toast.LENGTH_LONG).show();
                        }
                        Intent intent = new Intent(ResetPasswordActivity.this, ResetPasswordConfirmation.class);
                        startActivity(intent);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Loader.dialogDissmiss(getApplicationContext());
                    // finishActivity(0);
                    break;
                case 400:
                case 500:
                default:
                    if (getApplicationContext() != null) {
                        Toast.makeText(getApplicationContext(), "Sorry, this user doesn't exist.", Toast.LENGTH_LONG).show();
                    }

                    Loader.dialogDissmiss(getApplicationContext());
                    finishActivity(0);
                    break;
            }
        }
    }*/
}