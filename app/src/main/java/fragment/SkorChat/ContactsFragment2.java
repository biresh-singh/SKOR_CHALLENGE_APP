package fragment.SkorChat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.arasthel.asyncjob.AsyncJob;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.utils.DialogUtils;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.GenericQueryRule;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.root.skor.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jivesoftware.smack.XMPPException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import activity.skorchat.ChattingActivity;
import activity.skorchat.CreateGroupActivity;
import adaptor.Contacts2Adapter;
import adaptor.DialogAdapter2;
import adaptor.GroupAdapter;
import bean.QuickbloxChat;
import bean.QuickbloxUser;
import constants.Constants;
import cz.msebera.android.httpclient.Header;
import database.SharedDatabase;
import event.RefreshTokenEvent;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import listener.SelectContactOnContactListListener;
import listener.SelectGroupListener;
import rx.Observable;
import singleton.SettingsManager;
import singleton.UserManager;
import utils.AndroidDeviceNames;
import utils.AppController;
import utils.Loader;

/**
 * Created by mac on 10/30/17.
 */

public class ContactsFragment2 extends Fragment {
    private QBPagedRequestBuilder qbPagedBuilder;
    private static final String ORDER_RULE = "order";
    private static final String ORDER_VALUE = "desc date created_at";
    private static final String USER_CONTACT_TYPE = "user";
    private static final String GROUP_CONTACT_TYPE = "group";
    private static final int LIMIT_USERS = 1000;
    private JSONObject departmentJSONObject = new JSONObject();
    private ArrayList<String> departmentList = new ArrayList<String>();
    private Map<String, ArrayList<QuickbloxUser>> departmentGroupingMap = new HashMap<>();

    int currentPage = 1;
    EditText searchEditText;
    public static String useragent = null;
    AndroidDeviceNames deviceNames;
    String versionName1 = "";
    SharedDatabase sharedDatabase;
    String token;
    private ArrayList<QuickbloxUser> quickbloxUserArrayList = new ArrayList<>();
    private ArrayList<QuickbloxChat> quickbloxChatArrayList = new ArrayList<>();
    private Realm realm;
    private GroupAdapter groupAdapter;
    private Contacts2Adapter contacts2Adapter;
    private RecyclerView mGroupRv, mContactRv;
    private TextView mGroupTexView, mContactTextView;
    private LinearLayout mCreateGroupLinearLayout, rootLinearLayout;
    private RelativeLayout mGroupRelativeLayout, mContactRelativeLayout;
    private LinearLayoutManager mLayoutManager, mContactLayoutManager;
    private ImageView mGroupIcon, mContactIcon;
    private FrameLayout closeFrameLayout;
//    private ScrollView scrollView;

    private boolean isGroupOpen = false, isContactOpen = false, isLoadingUserContactsDone = false, isLoadingGroupChatsDone = false;
    private static final String TAG = "ContactsFragment2";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts2, null);
        searchEditText = (EditText) view.findViewById(R.id.activity_chating_messageEditText);

        mCreateGroupLinearLayout = (LinearLayout) view.findViewById(R.id.createGroupLinearLayout);
        mGroupRv = (RecyclerView) view.findViewById(R.id.groupRv);
        mContactRv = (RecyclerView) view.findViewById(R.id.contactRv);
        mGroupTexView = (TextView) view.findViewById(R.id.groupTexView);
        mContactTextView = (TextView) view.findViewById(R.id.contactTexView);
        mGroupRelativeLayout = (RelativeLayout) view.findViewById(R.id.groupRelativeLayout);
        mContactRelativeLayout = (RelativeLayout) view.findViewById(R.id.contactRelativeLayout);
        mGroupIcon = (ImageView) view.findViewById(R.id.groupIcon);
        mContactIcon = (ImageView) view.findViewById(R.id.contactIcon);
        rootLinearLayout = (LinearLayout) view.findViewById(R.id.rootLinearLayout);
        closeFrameLayout = (FrameLayout) view.findViewById(R.id.fragment_contacts2_closeFrameLayout);
//        scrollView = (ScrollView) view.findViewById(R.id.scrollView);

        //this is for api call
        deviceNames = new AndroidDeviceNames(getContext());
        useragent = "Skor/3 Android|" + deviceNames.getDeviceName() + "|" + deviceNames.getAPIVerison() + "|" + getVersionCode();

        mCreateGroupLinearLayout.setOnClickListener(createGroupTapped);
        mGroupRelativeLayout.setOnClickListener(groupTexViewTapped);
        mContactRelativeLayout.setOnClickListener(contactTextViewTapped);
        closeFrameLayout.setOnClickListener(clearSearchTapped);

        realm = AppController.getRealm();
        sharedDatabase = new SharedDatabase(getContext());
        token = sharedDatabase.getToken();

        initRecycler();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadLocalData();
        searchEditText.setText("");
    }

    View.OnClickListener createGroupTapped = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getActivity(), CreateGroupActivity.class);
            intent.putExtra("isFromCreateGroup", true);
            startActivity(intent);
        }
    };

    View.OnClickListener clearSearchTapped = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            searchEditText.setText("");
        }
    };

    TextWatcher textChangedListener = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
            // When user changed the Text
            Log.v("CONTACTSFRAGMENT", "onTextChanged" + searchEditText.getText().toString());
            closeFrameLayout.setVisibility(cs.toString().equalsIgnoreCase("") ? View.GONE : View.VISIBLE);
            groupAdapter.searchContact(cs);
            contacts2Adapter.searchContact(cs);
        }

        @Override
        public void afterTextChanged(Editable s) {
            Log.v("CONTACTSFRAGMENT", "afterTextChanged" + searchEditText.getText().toString());
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            Log.v("CONTACTSFRAGMENT", "beforeTextChanged" + searchEditText.getText().toString());
        }

    };

    View.OnClickListener groupTexViewTapped = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            isGroupOpen = !isGroupOpen;
            mGroupRv.setVisibility(isGroupOpen ? View.VISIBLE : View.GONE);
//            int totalCellHeight = groupAdapter.getCellHeight() * quickbloxChatArrayList.size();
//            mGroupRv.getLayoutParams().height = totalCellHeight;
            mGroupIcon.setBackground(isGroupOpen ? getResources().getDrawable(R.drawable.dropdpwn) : getResources().getDrawable(R.drawable.dropup));
        }
    };

    View.OnClickListener contactTextViewTapped = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int totalScrollViewHeight = 0;
            isContactOpen = !isContactOpen;
            mContactRv.setVisibility(isContactOpen ? View.VISIBLE : View.GONE);
//            int departmentViewHeight = contacts2Adapter.getAdapterHeight().get(0);
//            int contactViewHeight = contacts2Adapter.getAdapterHeight().get(1);
//            totalScrollViewHeight = departmentList.size() *  departmentViewHeight + quickbloxUserArrayList.size() * contactViewHeight;
//            scrollView.getLayoutParams().height = (totalScrollViewHeight);
            mContactIcon.setBackground(isContactOpen ? getResources().getDrawable(R.drawable.dropdpwn) : getResources().getDrawable(R.drawable.dropup));
        }
    };

    private void initRecycler() {
        groupAdapter = new GroupAdapter(getActivity(), selectGroupListener);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mGroupRv.setLayoutManager(mLayoutManager);
        mGroupRv.setAdapter(groupAdapter);
        mGroupRv.setNestedScrollingEnabled(false);

        contacts2Adapter = new Contacts2Adapter(getContext(), selectContactListener);
        mContactLayoutManager = new LinearLayoutManager(getActivity());
        mContactRv.setLayoutManager(mContactLayoutManager);
        mContactRv.setAdapter(contacts2Adapter);
        mContactRv.setNestedScrollingEnabled(false);
    }

    public String getVersionCode() {
        String versionCode = null;
        try {
            PackageManager manager = getActivity().getPackageManager();
            PackageInfo info = manager.getPackageInfo(getActivity().getPackageName(), 0);
            versionCode = String.valueOf(info.versionCode);
            versionName1 = String.valueOf(info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    private void setQBPagedBuilder(int page) {
        qbPagedBuilder = new QBPagedRequestBuilder();
        GenericQueryRule genericQueryRule = new GenericQueryRule(ORDER_RULE, ORDER_VALUE);

        ArrayList<GenericQueryRule> rule = new ArrayList<>();
        rule.add(genericQueryRule);

        qbPagedBuilder.setPage(page);
        qbPagedBuilder.setPerPage(LIMIT_USERS);
        qbPagedBuilder.setRules(rule);

        getAllUsers();
    }

    private void reloadLocalData() {
//        Loader.showProgressDialog(getContext());
        quickbloxUserArrayList = new ArrayList<>();
        quickbloxChatArrayList = new ArrayList<>();

        RealmResults<QuickbloxUser> quickbloxUserRealmResult = realm.where(QuickbloxUser.class).findAll();
        quickbloxUserArrayList.addAll(quickbloxUserRealmResult.subList(0, quickbloxUserRealmResult.size()));

        RealmResults<QuickbloxChat> quickbloxChatRealmResults = realm.where(QuickbloxChat.class).equalTo("type", QBDialogType.GROUP.getCode()).findAll();
        quickbloxChatArrayList.addAll(quickbloxChatRealmResults.subList(0, quickbloxChatRealmResults.size()));

        JSONObject departmentJSON = new JSONObject();
        try {
            departmentJSON = new JSONObject(SettingsManager.getInstance().getDepartmentJSON());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (quickbloxUserArrayList.size() != 0 && departmentJSON.length() != 0) {
            loadUserData(quickbloxUserArrayList, departmentJSON);
        }
        if (quickbloxChatArrayList.size() != 0) {
            loadGroupData(quickbloxChatArrayList);
        }

        getGroupDialogs();
        getDepartment();
    }

    private void loadUserData(final ArrayList<QuickbloxUser> quickbloxUserArrayList, JSONObject departmentJSON) {
        //avoid duplicate list
        departmentList = new ArrayList<String>();
        departmentGroupingMap = new HashMap<>();

//        checkLoading("user");
        Collections.sort(quickbloxUserArrayList, new Comparator<QuickbloxUser>() {
            @Override
            public int compare(QuickbloxUser o1, QuickbloxUser o2) {
                return o1.getFullName().compareToIgnoreCase(o2.getFullName());
            }
        });
        mContactTextView.setText("Contacts (" + quickbloxUserArrayList.size() + ")");
        Iterator<String> iter = departmentJSON.keys();
        while (iter.hasNext()) {
            String key = iter.next();
            try {
                Object value = departmentJSON.get(key);
                if (!departmentList.contains((String) value)) {
                    departmentList.add((String) value);
                }

                for (int i = 0; i < quickbloxUserArrayList.size(); i++) {
                    if (key.equals(quickbloxUserArrayList.get(i).getEmail())) {
                        String departmentName = (String) value;
                        QuickbloxUser quickbloxUser = quickbloxUserArrayList.get(i);

                        ArrayList<QuickbloxUser> emailList = new ArrayList<QuickbloxUser>();
                        if (departmentGroupingMap.get(departmentName) != null) {
                            emailList.addAll(departmentGroupingMap.get(departmentName));
                        }
                        emailList.add(quickbloxUser);
                        departmentGroupingMap.put((String) value, emailList);

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Sort Department Ascending
        Collections.sort(departmentList, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareToIgnoreCase(o2);
            }
        });
        // Contact Department Ascending
        for (String departmentName : departmentList) {
            if (departmentGroupingMap.get(departmentName) != null) {
                Collections.sort(departmentGroupingMap.get(departmentName), new Comparator<QuickbloxUser>() {
                    @Override
                    public int compare(QuickbloxUser qbQuickbloxUser, QuickbloxUser t1) {
                        return qbQuickbloxUser.getFullName().compareToIgnoreCase(t1.getFullName());
                    }
                });
            }
        }
        contacts2Adapter.updateAdapter(departmentList, departmentGroupingMap, quickbloxUserArrayList);
        searchEditText.addTextChangedListener(textChangedListener);
    }

    private void loadGroupData(ArrayList<QuickbloxChat> quickbloxChatArrayList) {
        checkLoading("group");
        mGroupTexView.setText("Group Contacts (" + quickbloxChatArrayList.size() + ")");
        // Sort Group Contact Ascending
//        Collections.sort(quickbloxChatArrayList, new Comparator<Object>() {
//            @Override
//            public int compare(Object o1, Object o2) {
//                QBChatDialog qBChatDialog1 = (QBChatDialog) o1;
//                QBChatDialog qBChatDialog2 = (QBChatDialog) o2;
//                return qBChatDialog1.getName().compareToIgnoreCase(qBChatDialog2.getName());
//            }
//        });
        groupAdapter.updateAdapter(quickbloxChatArrayList);
    }

    private void getGroupDialogs() {
        QBRequestGetBuilder requestBuilder = new QBRequestGetBuilder();
        requestBuilder.setLimit(1000);

        QBRestChatService.getChatDialogs(null, requestBuilder).performAsync(
                new QBEntityCallback<ArrayList<QBChatDialog>>() {
                    @Override
                    public void onSuccess(final ArrayList<QBChatDialog> qbChatDialogs, Bundle params) {

                        /*
                        GROUP DIALOG SECTION
                        */
                        ArrayList<QuickbloxChat> groupChatDialogList = new ArrayList<QuickbloxChat>();
                        RealmList<QuickbloxChat> quickbloxChatRealmList = new RealmList<QuickbloxChat>();
                        for (int i = 0; i < qbChatDialogs.size(); i++) {
                            if (qbChatDialogs.get(i).getType().name().equals("GROUP")) {
                                QBChatDialog qbChatDialog = qbChatDialogs.get(i);
                                groupChatDialogList.add(new QuickbloxChat(
                                        qbChatDialog.getDialogId(),
                                        qbChatDialog.getLastMessage(),
                                        qbChatDialog.getLastMessageDateSent(),
                                        qbChatDialog.getPhoto(),
                                        qbChatDialog.getUserId(),
                                        qbChatDialog.getUnreadMessageCount(),
                                        qbChatDialog.getName(),
                                        qbChatDialog.getOccupants().size(),
                                        qbChatDialog.getType().getCode(),
                                        0,
                                        qbChatDialog.getRecipientId()));
                            }
                        }
                        quickbloxChatRealmList.addAll(groupChatDialogList);

                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(quickbloxChatRealmList);
                        realm.commitTransaction();
                        loadGroupData(groupChatDialogList);
                    }

                    @Override
                    public void onError(QBResponseException responseException) {

                    }
                });
    }

    private void getAllUsers() {
        QBUsers.getUsers(qbPagedBuilder).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {

                //this is the old one, only can retrieve 100 contact max.
                int total_entries = bundle.getInt("total_entries");

                // minus one for current login user
                if (quickbloxUserArrayList.size() < total_entries - 1) {
                    int loggedInId = QBChatService.getInstance().getUser().getId();
                    boolean isLoggedInUserRemoved = false;
                    for (int i = 0; i < qbUsers.size(); i++) {
                        QBUser qbUser = qbUsers.get(i);
                        int userId = qbUser.getId();
                        String fullname = qbUser.getFullName();
                        String email = qbUser.getEmail();
                        Date lastRequestAt = qbUser.getLastRequestAt();
                        String customData = qbUser.getCustomData();
                        quickbloxUserArrayList.add(new QuickbloxUser(userId, fullname, email, lastRequestAt, customData));
//                        if (!isLoggedInUserRemoved) {
//                            if (quickbloxUserArrayList.get(i).getId() == loggedInId) {
//                                quickbloxUserArrayList.remove(i);
//                                realm.beginTransaction();
//                                realm.where(QuickbloxUser.class).equalTo("id", loggedInId).findFirst().deleteFromRealm();
//                                realm.commitTransaction();
//                                isLoggedInUserRemoved = true;
//                            }
//                        }
                    }
                    //this is the new one, logic for retrieve all contact.
                    int currentPage = bundle.getInt("current_page");
                    setQBPagedBuilder(currentPage + 1);
                } else {
                    RealmList<QuickbloxUser> quickbloxUserRealmList = new RealmList<QuickbloxUser>();
                    quickbloxUserRealmList.addAll(quickbloxUserArrayList);
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(quickbloxUserRealmList);
                    realm.commitTransaction();
                    loadUserData(quickbloxUserArrayList, departmentJSONObject);
                }
            }

            @Override
            public void onError(QBResponseException e) {
//                progressDialog.dismiss();
            }
        });
    }

    SelectContactOnContactListListener selectContactListener = new SelectContactOnContactListListener() {
        @Override
        public void onSelectContactOnContactListListener(QuickbloxUser quickbloxUser, boolean isFromDisplay) {
            QBChatDialog dialog = DialogUtils.buildPrivateDialog(quickbloxUser.getId());
            QBRestChatService.createChatDialog(dialog).performAsync(new QBEntityCallback<QBChatDialog>() {
                @Override
                public void onSuccess(final QBChatDialog qbChatDialog, Bundle params) {
//                    Toast.makeText(getContext(), "success create chat dialog", Toast.LENGTH_SHORT).show();
                    getUser(qbChatDialog.getDialogId());
                }

                @Override
                public void onError(QBResponseException responseException) {
                    Toast.makeText(getContext(), "failed create chat dialog", Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    SelectGroupListener selectGroupListener = new SelectGroupListener() {
        @Override
        public void onSelectGroupListener(String qbChatDialogId) {
            getUser(qbChatDialogId);
        }
    };

    private void getUser(String qbChatDialogId) {

        Intent intent = new Intent(getContext(), ChattingActivity.class);
        //                            intent.putExtra("qbChatDialog", qbChatDialog);
        intent.putExtra("qbChatDialogId", qbChatDialogId);
        startActivity(intent);
    }

    private void getDepartment() {
        String authProvider = SettingsManager.getInstance().getAuthProvider();

        HashMap<String, String> paramMap = new HashMap<String, String>();

        RequestParams params = new RequestParams(paramMap);
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(10000);
        client.addHeader("connection", "Keep-Alive");
        client.addHeader("Content-Type", "application/x-www-form-urlencoded");
        client.addHeader("Authorization", authProvider + " " + token);
        client.addHeader("USER-AGENT", useragent);
        client.get(Constants.BASEURL + "/profiles/api/users_department/?pagination=false", params, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    departmentJSONObject = new JSONObject(responseString);
                    SettingsManager.getInstance().setDepartmentJSON(responseString);
                    //get all contacts
                    setQBPagedBuilder(currentPage);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (statusCode == 401) {
                    UserManager.getInstance().logOut(getActivity());
                }
            }
        });
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
            getDepartment();
        } else {
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

    private void checkLoading(String type) {
        if (type.equalsIgnoreCase(USER_CONTACT_TYPE)) {
            isLoadingUserContactsDone = true;
        } else if (type.equalsIgnoreCase(GROUP_CONTACT_TYPE)) {
            isLoadingGroupChatsDone = true;
        }
        if (isLoadingGroupChatsDone && isLoadingUserContactsDone) {
            Loader.dialogDissmiss(getContext());
        }
    }
}
