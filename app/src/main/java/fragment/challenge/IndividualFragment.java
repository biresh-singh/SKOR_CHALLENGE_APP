package fragment.challenge;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DailyTotalResult;
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.android.gms.plus.Plus;
import com.root.skor.R;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import CustomClass.RobotoRegularTextView;
import InternetConnection.CheckInternetConnection;
import adaptor.IndividualAdaptor;
import bean.Category;
import database.SharedDatabase;
import fragment.PointsSummeryFragments;
import interfaceSkor.ApiInterface;
import model.Challenge;
import model.GetAllChallenge;
import model.GetChallengesResponse;
import model.Tags;
import retrofit2.Call;
import retrofit2.Callback;
import util.RetrofitClient;
import utils.AppController;
import utils.Loader;

/**
 * Created by biresh.singh on 15-06-2018.
 */

@SuppressLint("ValidFragment")
public class IndividualFragment extends Fragment {
    private Context mContext;
    private RecyclerView rvIndividual;
    private RobotoRegularTextView  txtNoDate;
    private IndividualAdaptor individualAdaptor = null;
    List<Challenge> mIndividualList=new ArrayList<>();
    public static GoogleApiClient mClient = null;
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
    static SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd");
    private static final String DATE_FORMAT = "yyyy.MM.dd HH:mm:ss";
    JSONArray stepDataJsonArray;
    JSONObject stepPostJsonObject;
    long daysDiference;
    int total = 0;
    public static final String TAG = "BasicHistoryApi";
    public SharedDatabase sharedDatabase;
    CheckInternetConnection checkInternetConnection;
    ArrayList<String> stepsList = new ArrayList<>();
    private String token;
    public IndividualFragment(Context context) {

        this.mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_individual, container, false);
        checkInternetConnection = new CheckInternetConnection(mContext);
        sharedDatabase = new SharedDatabase(mContext);
        //buildFitnessClient();
        final SharedDatabase sharedDatabase = new SharedDatabase(getContext());
        token = sharedDatabase.getToken();
        GetIndividualChallenge(token);
        initView(view);
        return view;
    }

    private void initView(View view) {

        rvIndividual = (RecyclerView) view.findViewById(R.id.rvIndividual);
        txtNoDate= (RobotoRegularTextView) view.findViewById(R.id.txtNoDate);
        /*individualAdaptor = new IndividualAdaptor(mContext);
        rvIndividual.setItemAnimator(new DefaultItemAnimator());
        rvIndividual.setAdapter(individualAdaptor);*/

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        rvIndividual.setLayoutManager(gridLayoutManager);
        rvIndividual.setHasFixedSize(true);
        rvIndividual.setNestedScrollingEnabled(false);
        individualAdaptor = new IndividualAdaptor(mContext);
        rvIndividual.setAdapter(individualAdaptor);
       // new IndividualFragment.InsertAndVerifyDataTask().execute();
    }

    private void initRecyclerView() {

    }

/*
    private void buildFitnessClient() {
        // Create the Google API Client
        try {
            if (mContext != null) {
                mClient = new GoogleApiClient.Builder(mContext)
                        .addApi(Fitness.HISTORY_API)
                        .addApi(Fitness.SENSORS_API)
                        .addApi(Fitness.CONFIG_API)
                        .addApi(Plus.API)
                        .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                        .addConnectionCallbacks(
                                new GoogleApiClient.ConnectionCallbacks() {
                                    @Override
                                    public void onConnected(Bundle bundle) {

                                        if (mClient.isConnected()) {
                                            int sdkVersion = Build.VERSION.SDK_INT;


                                        }

                                        new IndividualFragment.InsertAndVerifyDataTask().execute();
                                    }

                                    @Override
                                    public void onConnectionSuspended(int i) {
                                        Log.i(TAG, "Connection lost.  Cause: Network Lost.");
                                    }
                                }
                        )
                        .addOnConnectionFailedListener(
                                new GoogleApiClient.OnConnectionFailedListener() {
                                    // Called whenever the API client fails to connect.
                                    @Override
                                    public void onConnectionFailed(ConnectionResult result) {
                                        Log.i(TAG, "Connection lost.  Cause: Network Lost.");
                                    }
                                }
                        )
                        .build();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
*/

    /*private class InsertAndVerifyDataTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            //First, create a new dataset and insertion request.
            DataSet dataSet = insertFitnessData();
            Log.i(TAG, "Inserting the dataset in the History API");
            com.google.android.gms.common.api.Status insertStatus =
                    Fitness.HistoryApi.insertData(mClient, dataSet)
                            .await(1, TimeUnit.MINUTES);

            // Before querying the data, check to see if the insertion succeeded.
            if (!insertStatus.isSuccess()) {
                mClient.connect();
                Log.i(TAG, "There was a problem inserting the dataset.");
                // callTotalPointsSummeryApi();
                return null;
            }

            // At this point, the data has been inserted and can be read.
            Log.i(TAG, "Data insert was successful!");
            DataReadRequest readRequest = queryFitnessData();
            DataReadResult dataReadResult =
                    Fitness.HistoryApi.readData(mClient, readRequest).await(1, TimeUnit.MINUTES);
            printData(dataReadResult);

            return null;
        }
    }
*/
    public void updateUI()
    {
        GetIndividualChallenge(token);
    }

   /* private void printData(DataReadResult dataReadResult) {
        // [START parse_read_data_result]
        // If the DataReadRequest object specified aggregated data, dataReadResult will be returned
        // as buckets containing DataSets, instead of just DataSets.
        if (dataReadResult.getBuckets().size() > 0) {
            Log.i(TAG, "Number of returned buckets of DataSets is: "
                    + dataReadResult.getBuckets().size());
            for (Bucket bucket : dataReadResult.getBuckets()) {

                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    dumpDataSet(dataSet);
                }
            }
            readAllStep();
        } else if (dataReadResult.getDataSets().size() > 0) {
            Log.i(TAG, "Number of returned DataSets is: "
                    + dataReadResult.getDataSets().size());
            for (DataSet dataSet : dataReadResult.getDataSets()) {
                dumpDataSet(dataSet);
            }
            readAllStep();

        }

    }

    private void dumpDataSet(DataSet dataSet) {
        try {
            Log.i(TAG, "Data returned for Data type: " + dataSet.getDataType().getName());

            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);


            for (DataPoint dp : dataSet.getDataPoints()) {

                DataSource dataSource = dp.getOriginalDataSource();
                if (dataSource != null) {

                    Log.i(TAG, "\tgetApplicationPackagename: " + dataSource.getAppPackageName());
                    Log.i(TAG, "Stream :" + dataSource.getStreamName());
                }

                //Log.i(TAG, "\tType: " + dataSource.getAppPackageName());
                Log.i(TAG, "\tType: " + dp.getDataType().getName());
                Log.i(TAG, "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
                Log.i(TAG, "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
                for (Field field : dp.getDataType().getFields()) {
                    Log.i(TAG, "\tField: " + field.getName() +
                            " Value: " + dp.getValue(field));
                    stepsList.add(dp.getValue(field).toString());

                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
                    String dateInString = formatter.format(dp.getStartTime(TimeUnit.MILLISECONDS));
                    JSONObject jsonObject1 = new JSONObject();


                    jsonObject1.put("value", Integer.parseInt(dp.getValue(field).toString()));
                    jsonObject1.put("date_time", dateInString);
                    stepDataJsonArray.put(jsonObject1);

                }

                stepPostJsonObject.put("steps", stepDataJsonArray);

                Log.i(TAG, "\tstepList: " + stepsList);

            }


        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    private DataReadRequest queryFitnessData() {
        // [START build_read_data_request]
        // Setting a start and end date using a range of 1 week before this moment.

        daysDiference = daysDifference(sharedDatabase.getPoststepdate());
        ++daysDiference;
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        if (daysDiference != 0) {
            cal.add(Calendar.DAY_OF_YEAR, (int) -daysDiference);
        } else {
            cal.add(Calendar.DAY_OF_YEAR, -1);
        }
        *//*  cal.add(Calendar.WEEK_OF_YEAR, -1);*//*
        long startTime = cal.getTimeInMillis();

        DateFormat dateFormat = DateFormat.getDateInstance();
        Log.e("History", "Range Start: " + dateFormat.format(startTime));
        Log.e("History", "Range End: " + dateFormat.format(endTime));

//Check how many steps were walked and recorded in the last 7 days
        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();
        return readRequest;
    }

    public long daysDifference(String previousDate) {
        SimpleDateFormat myFormat = new SimpleDateFormat("dd MM yyyy");
        String inputString1 = previousDate;
        String inputString2 = mFormatter.format(new Date());


        long diff = 0;

        try {
            Date date1 = mFormatter.parse(inputString1);
            Date date2 = mFormatter.parse(inputString2);
            diff = date2.getTime() - date1.getTime();

            System.out.println("Days: YYYYYYYYYYYYYYYYYYYYYYYYY  " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
            diff = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return diff;
    }*/


   /* private DataSet insertFitnessData() {
        Log.i(TAG, "Creating a new data insert request");

        // [START build_insert_data_request]
        // Set a start and end time for our data, using a start time of 1 hour before this moment.
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.HOUR_OF_DAY, -1);
        long startTime = cal.getTimeInMillis();

        // Create a data source
        DataSource dataSource = new DataSource.Builder()
                .setAppPackageName(mContext)
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .setName(TAG + " - step count")
                .setType(DataSource.TYPE_RAW)
                .setStreamName("user_input")


                .build();

        // Create a data set
        int stepCountDelta = 0;
        DataSet dataSet = DataSet.create(dataSource);
        // For each data point, specify a start time, end time, and the data value -- in this case,
        // the number of new steps.
        DataPoint dataPoint = dataSet.createDataPoint()
                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);
        dataPoint.getValue(Field.FIELD_STEPS).setInt(stepCountDelta);
        dataSet.add(dataPoint);
        // [END build_insert_data_request]

        return dataSet;
    }

    public void readAllStep() {

        try {
            PendingResult<DailyTotalResult> result = Fitness.HistoryApi.readDailyTotal(mClient, DataType.TYPE_STEP_COUNT_DELTA);
            DailyTotalResult totalResult = result.await(30, TimeUnit.SECONDS);
            if (totalResult.getStatus().isSuccess()) {
                DataSet totalSet = totalResult.getTotal();
                total = totalSet.isEmpty()
                        ? 0
                        : totalSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
                //  stepsList.add(String.valueOf(total));
            } else {
                // handle failure
            }
            try {
                if (daysDiference == 1) {

                    String dateFormat = formatter.format(new Date());
                    stepDataJsonArray = new JSONArray();
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("value", total);
                    jsonObject1.put("date_time", dateFormat);
                    stepDataJsonArray.put(jsonObject1);
                    stepPostJsonObject = new JSONObject();
                    stepPostJsonObject.put("steps", stepDataJsonArray);
                }


            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
        catch(Exception ex)
        {
            String string = ex.getMessage().toString();
        }

    }
*/
  /*  private class LoginAsyncTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            if (mClient != null) {
                mClient.connect();
            }
            super.onPreExecute();
        }

        protected Void doInBackground(Void... args) {
            // Parsse response data
            return null;
        }

        protected void onPostExecute(Void result) {

            //move activity
            super.onPostExecute(result);
        }
    }*/

    /*@Override
    public void onStart() {
        super.onStart();


        // Connect to the Fitness API
        Log.i(TAG, "Connecting...");
        if (checkInternetConnection.isConnectingToInternet()) {

                new IndividualFragment.LoginAsyncTask().execute();

        } else {
            if (getResources() != null) {

            }
            // Toast.makeText(getActivity(), "No Internet Connection.", Toast.LENGTH_LONG).show();
        }
    }*/

    @Override
    public void onStop() {
        super.onStop();

       /* if (mClient.isConnected()) {
            mClient.disconnect();
        }*/
    }

    public static JSONObject objectToJSONObject(Object object){
        Object json = null;
        JSONObject jsonObject = null;
        try {
            json = new JSONTokener(object.toString()).nextValue();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (json instanceof JSONObject) {
            jsonObject = (JSONObject) json;
        }
        return jsonObject;
    }

    public static JSONArray objectToJSONArray(Object object){
        Object json = null;
        JSONArray jsonArray = null;
        try {
            json = new JSONTokener(object.toString()).nextValue();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (json instanceof JSONArray) {
            jsonArray = (JSONArray) json;
        }
        return jsonArray;
    }

    public void processJSON(Object obj){
        JSONObject jsonObj = null;
        JSONArray jsonArr = null;
        jsonObj = objectToJSONObject(obj);
        jsonArr = objectToJSONArray(obj);
        if (jsonObj != null) {
            //process JSONObject
        } else if (jsonArr != null) {
            //process JSONArray
        }
    }

    private void GetIndividualChallenge(String token) {
       //Loader.showProgressDialog(getActivity());
        if(mIndividualList.size()>0)
        {
            mIndividualList.clear();
            individualAdaptor.setData(mIndividualList);
        }
        ApiInterface apiService= RetrofitClient.getClient().create(ApiInterface.class);;


        Call<GetChallengesResponse> call = apiService.GetChallenges(AppController.useragent,"Token "+token+"");
        call.enqueue(new Callback<GetChallengesResponse>() {
            @Override
            public void onResponse(Call<GetChallengesResponse> call, retrofit2.Response<GetChallengesResponse> response) {
                try{
                    if(response.body()!=null) {
                        GetAllChallenge objCheckedIn = response.body().getResult();
                        List<Challenge> individualchallengeLists=objCheckedIn.getlstIndividualchallenge();
                        if (individualchallengeLists != null && individualchallengeLists.size() > 0) {
                            /*Object tagsList=individualchallengeLists.get(0).getTags();

                            processJSON(tagsList);
                            List<Tags> customers = (List<Tags>)tagsList;

                            for(Tags t:customers)
                            {
                                int i=t.getid();
                                String string = t.getName();
                            }*/
                            rvIndividual.setVisibility(View.VISIBLE);
                            txtNoDate.setVisibility(View.GONE);
                            mIndividualList.addAll(individualchallengeLists);
                            individualAdaptor.setData(mIndividualList);
                        }
                        else
                        {
                            rvIndividual.setVisibility(View.GONE);
                            txtNoDate.setVisibility(View.VISIBLE);
                        }
                        //readAllStep();
                        //Loader.dialogDissmiss(getActivity());
                    }
                    else
                    {

                    }

                }

                catch (NullPointerException ex)
                {

                }
                catch (IndexOutOfBoundsException ex)
                {

                }
                catch(IllegalArgumentException ex)
                {

                }
                catch (Exception ex) {

                }
            }



            @Override
            public void onFailure(Call<GetChallengesResponse>call, Throwable t) {
                // Log error here since request failed
                Loader.dialogDissmiss(getActivity());
            }
        });








    }

}