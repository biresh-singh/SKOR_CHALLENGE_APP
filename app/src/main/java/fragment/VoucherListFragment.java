package fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.root.skor.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import activity.rewardz.NewVoucherDetailActivity;
import adaptor.VoucherAdapter;
import bean.NewEVoucher;
import constants.Constants;
import cz.msebera.android.httpclient.Header;
import database.SharedDatabase;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import singleton.SettingsManager;
import singleton.UserManager;
import utils.AppController;
import utils.Loader;

public class VoucherListFragment extends Fragment {
    private Realm realm;
    private RecyclerView recyclerView;
    private VoucherAdapter voucherAdapter;
    private int type;
    private int selectedWalletId;
    private boolean isFromNotif = false;
    private SharedDatabase sharedDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_voucher_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedDatabase = new SharedDatabase(getActivity().getApplicationContext());
        selectedWalletId = sharedDatabase.getSelectedWalletId();
        sharedDatabase.setSelectedWalletId(0);
        // type == 0 equals to In Progress Wallet, type == 1 equals to Completed Wallet
        type = getArguments().getInt("type");
        isFromNotif = getArguments().getBoolean("isFromNotif",false);
        realm = AppController.getRealm();
        recyclerView = view.findViewById(R.id.fragment_voucher_list_recyclerView);
        voucherAdapter = new VoucherAdapter(getContext(),type);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(voucherAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadLocalData();
    }

    public void getVoucherAPI() {

        String authProvider = SettingsManager.getInstance().getAuthProvider();
        final SharedDatabase sharedDatabase = new SharedDatabase(getContext());
        String token = sharedDatabase.getToken();
        HashMap<String, String> paramMap = new HashMap<String, String>();
        RequestParams params = new RequestParams(paramMap);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(80000);
        client.addHeader("USER-AGENT", AppController.useragent);
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("Content-Type", "application/json");
        String url = type == 0 ? Constants.BASEURL + Constants.GET_UNUSED_WALLET : Constants.BASEURL + Constants.GET_USED_WALLET;
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                RealmList<NewEVoucher> newEVoucherRealmList = new RealmList<>();
                try {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject eVoucherJSON = jsonArray.getJSONObject(i);
                        NewEVoucher newEVoucher = new NewEVoucher(eVoucherJSON);
                        newEVoucherRealmList.add(newEVoucher);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                realm.beginTransaction();
                realm.copyToRealmOrUpdate(newEVoucherRealmList);
                realm.commitTransaction();
                ArrayList<NewEVoucher> newEvoucherArrayList = new ArrayList<NewEVoucher>();
                newEvoucherArrayList.addAll(newEVoucherRealmList);
                loadData(newEvoucherArrayList);
                if(selectedWalletId!=0) {
                    NewEVoucher selectedEVoucher = new NewEVoucher();
                    for (NewEVoucher newEvoucher : newEvoucherArrayList) {
                        if (newEvoucher.getId() == selectedWalletId) {
                            selectedEVoucher = newEvoucher;
                            selectedWalletId = 0;
                            break;
                        }
                    }
                    Intent intent = new Intent(getActivity(), NewVoucherDetailActivity.class);
                    intent.putExtra("eVoucher", selectedEVoucher.getId());
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Loader.dialogDissmiss(getActivity());
                if (statusCode == 401) {
                    UserManager.getInstance().logOut(getActivity());
                }
                if (statusCode == 400) {
                    connectivityMessage("" + errorResponse);
                }

                if (statusCode == 500) {
                    connectivityMessage("We've encountered a technical error.our team is working on it. please try again later");
                }
            }
        });
    }

    public void connectivityMessage(String msg) {

        if (msg.equals("Network Connecting....")) {
            SnackbarManager.show(Snackbar.with(getActivity()).text(msg).textColor(Color.WHITE)
                    .color(Color.parseColor("#FF9B30")), getActivity());
        } else if (msg.equals("Network Connected")) {
            SnackbarManager.show(Snackbar.with(getActivity()).text(msg).textColor(Color.WHITE)
                    .color(Color.parseColor("#4BCC1F")), getActivity());
        } else {
            SnackbarManager.show(Snackbar.with(getActivity()).text(msg).textColor(Color.WHITE)
                    .color(Color.RED), getActivity());
        }

    }

    private void reloadLocalData(){
        if (!Loader.isDialogShowing()) {
            Loader.showProgressDialog(getContext());
        }
        boolean isUsed = type == 0 ? false : true;
        RealmResults<NewEVoucher> newEVoucherRealmResults = realm.where(NewEVoucher.class).equalTo("isUsed", isUsed).findAll();
        ArrayList<NewEVoucher> newEVoucherArrayList = new ArrayList<>();
        newEVoucherArrayList.addAll(newEVoucherRealmResults.subList(0,newEVoucherRealmResults.size()));
        if(newEVoucherArrayList.size()!=0){
            loadData(newEVoucherArrayList);
        }
        getVoucherAPI();
    }

    private void loadData(ArrayList<NewEVoucher> newEVoucherArrayList){
        Loader.dialogDissmiss(getContext());
        voucherAdapter.updateAdapter(newEVoucherArrayList);
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
