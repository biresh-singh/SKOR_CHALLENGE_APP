package fragment.SkorChat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Toast;

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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import activity.skorchat.ChattingActivity;
import adaptor.ContactsAdapter;
import listener.SelectContactListener;
import listener.SelectGroupListener;
import singleton.SettingsManager;

/**
 * Created by dss-17 on 5/16/17.
 */

public class ContactsFragment extends Fragment {
    private QBPagedRequestBuilder qbPagedBuilder;
    private static final String ORDER_RULE = "order";
    private static final String ORDER_VALUE = "desc date created_at";
    private static final int LIMIT_USERS = 100;

    ExpandableListView contactsExpandableListView;

    private List<String> headerList;

    private List<QBUser> qbUsersList = new ArrayList<>();

    int currentPage = 1;

    ContactsAdapter contactsAdapter;

    static File photoFile = null;
    EditText searchEditText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, null);
        contactsExpandableListView = (ExpandableListView) view.findViewById(R.id.activity_chating_expandableListView);
        searchEditText = (EditText) view.findViewById(R.id.activity_chating_messageEditText);

        setQBPagedBuilder(currentPage);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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

    private void getAllUsers(boolean showProgress) {
//        Loader.showProgressDialog(getContext());
        QBUsers.getUsers(qbPagedBuilder).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {


                //this is the old one, only can retrieve 100 contact max.
//                DataHolder.getInstance().addQbUsers(qbUsers);
//                qbUsersList.addAll(DataHolder.getInstance().getQBUsers());

//                this is the new one, logic for retrieve all contact.
                int currentPage = bundle.getInt("current_page");
                int total_entries = bundle.getInt("total_entries");

                qbUsersList.addAll(qbUsers);

                if (qbUsersList.size() < total_entries) {
                    setQBPagedBuilder(currentPage + 1);
                } else {
                    headerList = new ArrayList<String>();
                    headerList.add("Group Contacts");
                    headerList.add("Contacts");

                    QBRequestGetBuilder requestBuilder = new QBRequestGetBuilder();
                    requestBuilder.setLimit(1000);

                    QBRestChatService.getChatDialogs(null, requestBuilder).performAsync(
                            new QBEntityCallback<ArrayList<QBChatDialog>>() {
                                @Override
                                public void onSuccess(final ArrayList<QBChatDialog> qbChatDialogs, Bundle params) {

                                    ArrayList<QBChatDialog> qbChatDialogs1 = new ArrayList<QBChatDialog>();

                                    for (int i = 0; i < qbChatDialogs.size(); i++) {
                                        if (qbChatDialogs.get(i).getType().name().equals("GROUP")) {
                                            qbChatDialogs1.add(qbChatDialogs.get(i));
                                        }
                                    }

                                    List<Object> qbUserListObjectTemp = new ArrayList<Object>();
                                    List<Object> qbGroupListObjectTemp = new ArrayList<Object>();
                                    qbGroupListObjectTemp.addAll(qbChatDialogs1);
                                    int listId;
                                    int loggedInId = QBChatService.getInstance().getUser().getId();
                                    for (QBUser qbUser : qbUsersList) {
                                        listId = qbUser.getId();
                                        if (listId != loggedInId) {
                                            qbUserListObjectTemp.add(qbUser);
                                        }
                                    }
                                    Collections.sort(qbUserListObjectTemp, new Comparator<Object>() {
                                        @Override
                                        public int compare(Object o1, Object o2) {
                                            return ((QBUser) o1).getFullName().compareToIgnoreCase(((QBUser) o2).getFullName());
                                        }
                                    });


                                    contactsAdapter = new ContactsAdapter(getContext(), headerList, qbUserListObjectTemp, qbGroupListObjectTemp, selectContactListener, selectGroupListener);
                                    contactsExpandableListView.setAdapter(contactsAdapter);

                                    searchEditText.addTextChangedListener(new TextWatcher() {

                                        @Override
                                        public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                                            // When user changed the Text

                                            Log.v("CONTACTSFRAGMENT", "onTextChanged" + searchEditText.getText().toString());
                                            contactsAdapter.searchContact(cs);

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


                                    });
                                }

                                @Override
                                public void onError(QBResponseException responseException) {

                                }
                            });
                }

            }

            @Override
            public void onError(QBResponseException e) {
//                progressDialog.dismiss();

            }
        });
    }

    SelectContactListener selectContactListener = new SelectContactListener() {
        @Override
        public void onSelectContactListener(QBUser qbUser, boolean isFromDisplay) {
            QBChatDialog dialog = DialogUtils.buildPrivateDialog(qbUser.getId());
            QBRestChatService.createChatDialog(dialog).performAsync(new QBEntityCallback<QBChatDialog>() {
                @Override
                public void onSuccess(final QBChatDialog qbChatDialog, Bundle params) {
//                    Toast.makeText(getContext(), "success create chat dialog", Toast.LENGTH_SHORT).show();
                    initChat(qbChatDialog.getDialogId());
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
            initChat(qbChatDialogId);
        }
    };

    private void initChat(String qbChatDialogId) {
        Intent intent = new Intent(getContext(), ChattingActivity.class);
        intent.putExtra("qbChatDialogId", qbChatDialogId);
        startActivity(intent);
    }
}
