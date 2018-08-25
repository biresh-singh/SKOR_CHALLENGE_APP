package activity.skorchat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.request.QBDialogRequestBuilder;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.GenericQueryRule;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.root.skor.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.SharedDatabase;
import fragment.SkorChat.CreateGroupStep1Fragment;
import fragment.SkorChat.CreateGroupStep2Fragment;
import singleton.SettingsManager;
import utils.ArialRegularTextView;
import utils.DataHolder;
/**
 * Created by dss-17 on 6/2/17.
 */
public class CreateGroupActivity extends AppCompatActivity {
    private static final String ORDER_RULE = "order";
    private static final String ORDER_VALUE = "desc date created_at";
    private static final int LIMIT_USERS = 100;
    private QBPagedRequestBuilder qbPagedBuilder;
    LinearLayout containerLinearLayout;
    ArialRegularTextView nextTextView, doneTextView, groupTitle;
    static ArialRegularTextView numberOfUsersTextView;
    ImageView backImageView;
    ArrayList<QBUser> qbUserList = new ArrayList<>();
    QBUser opponentUser;

    SharedDatabase sharedDatabase;
    int currentPage = 1;
    boolean isFromCreateGroup = false;

    //update group occupants
    public ArrayList<QBUser> qbUsers = new ArrayList<>();
    QBDialogRequestBuilder qbDialogRequestBuilder = new QBDialogRequestBuilder();
    QBChatDialog qbChatDialog;
    List<Integer> occupants = new ArrayList<>();
    public String isFromGroupInfoActivity = "";
    HashMap<Integer, String> groupMemberHashMap = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        containerLinearLayout = (LinearLayout) findViewById(R.id.activity_create_group_containerLinearLayout);
        nextTextView = (ArialRegularTextView) findViewById(R.id.activity_create_group_nextTextView);
        backImageView = (ImageView) findViewById(R.id.activity_create_group_backButtonImageView);
        groupTitle = (ArialRegularTextView) findViewById(R.id.activity_create_group_title);
        doneTextView = (ArialRegularTextView) findViewById(R.id.activity_create_group_doneTextView);
        numberOfUsersTextView = (ArialRegularTextView) findViewById(R.id.activity_create_group_numberOfUsersTextView);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.activity_create_group_containerLinearLayout);
        if(fragment instanceof CreateGroupStep1Fragment){
            nextTextView.setText("Next");
        }
        setQBPagedBuilder(currentPage);
        initializeListener();

        sharedDatabase = new SharedDatabase(this);

        groupTitle.setText("Create Group");
        doneTextView.setVisibility(View.GONE);
        nextTextView.setVisibility(View.VISIBLE);
        isFromCreateGroup = getIntent().getBooleanExtra("isFromCreateGroup",false);

        //update group occupants
        if (getIntent().getStringExtra("isFromGroupInfoActivity") != null) {
            isFromGroupInfoActivity = getIntent().getStringExtra("isFromGroupInfoActivity");
        }
        if (isFromGroupInfoActivity != null) {
            if (isFromGroupInfoActivity.equals("isFromGroupInfoActivity")) {

                groupTitle.setText("Update Member");
                numberOfUsersTextView.setVisibility(View.GONE);
                nextTextView.setVisibility(View.GONE);
                doneTextView.setVisibility(View.VISIBLE);

                qbChatDialog = SettingsManager.getInstance().getQbChatDialog();
                occupants.addAll(qbChatDialog.getOccupants());
                QBUser qbUser = QBChatService.getInstance().getUser();
                for (int i=0; i<occupants.size(); i++) {
                    if(occupants.get(i).equals(qbUser.getId())) {
                        occupants.remove(i);
                    }
                }
                for (int i=0; i<occupants.size(); i++) {
                    QBUsers.getUser(occupants.get(i)).performAsync(new QBEntityCallback<QBUser>() {
                        @Override
                        public void onSuccess(QBUser qbUser, Bundle bundle) {
                            qbUsers.add(qbUser);
                        }

                        @Override
                        public void onError(QBResponseException e) {

                        }
                    });
                }
            }


            doneTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    groupMemberHashMap = new HashMap<Integer, String>();

                    for (int i=0; i<qbUsers.size(); i++) {
                        groupMemberHashMap.put(qbUsers.get(i).getId(), "toBeAdded");
                    }

                    for(int i=0; i<occupants.size(); i++) {
                        int currentOccupantId = occupants.get(i);

                        if(groupMemberHashMap.get(currentOccupantId) != null) {
                            groupMemberHashMap.put(currentOccupantId, "old");
                        } else {
                            groupMemberHashMap.put(currentOccupantId, "toBeRemoved");
                        }
                    }

                    for(Map.Entry<Integer, String> entry : groupMemberHashMap.entrySet()) {
                        if (entry.getValue().equals("toBeAdded")) {
                            qbDialogRequestBuilder.addUsers(entry.getKey());
                        } else if (entry.getValue().equals("toBeRemoved")){
                            qbDialogRequestBuilder.removeUsers(entry.getKey());
                        }
                    }

                    QBRestChatService.updateGroupChatDialog(qbChatDialog, qbDialogRequestBuilder).performAsync(new QBEntityCallback<QBChatDialog>() {
                        @Override
                        public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                            Toast.makeText(CreateGroupActivity.this, "success update member", Toast.LENGTH_SHORT).show();
                            SettingsManager.getInstance().setQbChatDialog(qbChatDialog);
                            finish();
                        }

                        @Override
                        public void onError(QBResponseException e) {
                            Toast.makeText(CreateGroupActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataHolder.getInstance().removeAllQbUserFromList();
    }

    public void initializeListener(){

        View.OnClickListener backTapped = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        };
        backImageView.setOnClickListener(backTapped);
    }

    private void getAllUsers(boolean showProgress) {
        QBUsers.getUsers(qbPagedBuilder).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {

                int currentPage = bundle.getInt("current_page");
                int total_entries = bundle.getInt("total_entries");

                qbUserList.addAll(qbUsers);

                if(qbUserList.size() < total_entries){
                    setQBPagedBuilder(currentPage+1);
                } else {
                    QBRequestGetBuilder requestBuilder = new QBRequestGetBuilder();
                    requestBuilder.setLimit(1000);
                    Collections.sort(qbUserList, new Comparator<QBUser>() {
                        @Override
                        public int compare(QBUser o1, QBUser o2) {
                            return o1.getFullName().compareToIgnoreCase(o2.getFullName());
                        }
                    });

                    DataHolder.getInstance().setQbUsers(qbUserList);
//                    sharedDatabase.setQbUsers(qbUserList);
                    numberOfUsersTextView.setText(DataHolder.getInstance().getSelectedUsersForGroupList().size() + "/" + DataHolder.getInstance().getQBUsers().size());
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.activity_create_group_containerLinearLayout, new CreateGroupStep1Fragment()).addToBackStack(null).commitAllowingStateLoss();
                    View.OnClickListener nextTapped = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(getSupportFragmentManager().getBackStackEntryCount() == 1){
                                if(DataHolder.getInstance().getSelectedUsersForGroupList().size() == 0 || DataHolder.getInstance().getSelectedUsersForGroupList() == null){
                                    Toast.makeText(CreateGroupActivity.this, "Please choose members of the group first", Toast.LENGTH_SHORT).show();
                                }else{
                                    FragmentManager fragmentManager = getSupportFragmentManager();
                                    fragmentManager.beginTransaction().replace(R.id.activity_create_group_containerLinearLayout, new CreateGroupStep2Fragment()).addToBackStack(null).commitAllowingStateLoss();
                                    nextTextView.setText("Done");
                                }
                            }
                            else{
                                CreateGroupStep2Fragment createGroupStep2Fragment = (CreateGroupStep2Fragment) getSupportFragmentManager().findFragmentById(R.id.activity_create_group_containerLinearLayout);
                                createGroupStep2Fragment.createGroup();
                            }
                        }
                    };
                    nextTextView.setOnClickListener(nextTapped);

                }

            }
            @Override
            public void onError(QBResponseException e) {
//                progressDialog.dismiss();
//                setOnRefreshListener.setEnabled(false);
//                setOnRefreshListener.setRefreshing(false);
//
//                View rootLayout = findViewById(R.id.swipy_refresh_layout);
//                showSnackbarError(rootLayout, R.string.errors, e, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        getAllUsers(false);
//                    }
//                });
            }
        });
    }
    public static void updateNumberOfParticipants(int numberOfParticipants){
        numberOfUsersTextView.setText(numberOfParticipants+"/"+DataHolder.getInstance().getQBUsers().size());
    }

    private void setQBPagedBuilder(int page) {
        qbPagedBuilder = new QBPagedRequestBuilder();
        GenericQueryRule genericQueryRule = new GenericQueryRule(ORDER_RULE, ORDER_VALUE);

        ArrayList<GenericQueryRule> rule = new ArrayList<>();
        rule.add(genericQueryRule);

        qbPagedBuilder.setPage(page);
        qbPagedBuilder.setPerPage(LIMIT_USERS);
        qbPagedBuilder.setRules(rule);

        getAllUsers(true);

    }

    @Override
    public void onBackPressed() {
        if (isFromCreateGroup) {
//            if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
//                QBRestChatService.updateGroupChatDialog(SettingsManager.getInstance().getQbChatDialog(), qbDialogRequestBuilder).performAsync(new QBEntityCallback<QBChatDialog>() {
//                    @Override
//                    public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
//                        SettingsManager.getInstance().setQbChatDialog(qbChatDialog);
//                        finish();
//                    }
//
//                    @Override
//                    public void onError(QBResponseException e) {
//                        Toast.makeText(CreateGroupActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//            } else {
                nextTextView.setText("Next");
            if(getSupportFragmentManager().getBackStackEntryCount() == 1){
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.popBackStack();
            }
        }
//            QBRestChatService.updateGroupChatDialog(SettingsManager.getInstance().getQbChatDialog(), qbDialogRequestBuilder).performAsync(new QBEntityCallback<QBChatDialog>() {
//                @Override
//                public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
//                    SettingsManager.getInstance().setQbChatDialog(qbChatDialog);
//                    finish();
//                }
//
//                @Override
//                public void onError(QBResponseException e) {
//                    Toast.makeText(CreateGroupActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });
        else{
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.popBackStack();
        }
        super.onBackPressed();
    }
}