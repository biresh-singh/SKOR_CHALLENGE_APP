package fragment.SkorChat;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.quickblox.users.model.QBUser;
import com.root.skor.R;

import java.util.ArrayList;
import java.util.List;

import activity.skorchat.CreateGroupActivity;
import adaptor.ContactDisplayAdapter;
import adaptor.CreateGroupAdapter;
import database.SharedDatabase;
import in.myinnos.alphabetsindexfastscrollrecycler.IndexFastScrollRecyclerView;
import listener.SelectContactListener;
import listener.SelectContactListener2;
import utils.AppController;
import utils.DataHolder;

public class CreateGroupStep1Fragment extends Fragment {
    private List<QBUser> qbUsersList = new ArrayList<>();
    IndexFastScrollRecyclerView recyclerViewContact;
    RecyclerView recyclerViewContactDisplay;
    CreateGroupAdapter createGroupAdapter;
    EditText searchEditText;
    ContactDisplayAdapter contactDisplayAdapter;
    ArrayList<Integer> occupantIdsList = new ArrayList<Integer>();
    List<QBUser> qbUsersListForDisplay = new ArrayList<>();

    //update group occupants
    CreateGroupActivity createGroupActivity;

    SharedDatabase sharedDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        qbUsersList = DataHolder.getInstance().getQBUsers();
//        sharedDatabase = new SharedDatabase(getContext());
//        qbUsersList = sharedDatabase.getQbUsers();
        if(DataHolder.getInstance().getSelectedUsersForGroupList().size() != 0) {
            qbUsersListForDisplay.addAll(DataHolder.getInstance().getSelectedUsersForGroupList());
            occupantIdsList.add(DataHolder.getInstance().getSelectedUsersForGroupList().get(0).getId());
        }
        createGroupActivity = (CreateGroupActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_group_step1, null);

        recyclerViewContact = (IndexFastScrollRecyclerView) view.findViewById(R.id.fragment_create_group_step1_contactRecyclerView);
        recyclerViewContactDisplay = (RecyclerView) view.findViewById(R.id.fragment_create_group_step1_contactDisplayRecyclerView);
        searchEditText = (EditText) view.findViewById(R.id.fragment_create_group_step1_messageEditText);
        if (!createGroupActivity.isFromGroupInfoActivity.equals("isFromGroupInfoActivity")) {
            contactDisplayAdapter = new ContactDisplayAdapter(getActivity(), selectContactListener, "fromCreateGroup");
        } else {
            contactDisplayAdapter = new ContactDisplayAdapter(getActivity(), selectContactListener2, "fromGroupInfo");
        }
        recyclerViewContactDisplay.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewContactDisplay.setAdapter(contactDisplayAdapter);
        recyclerViewContact.setIndexBarTransparentValue((float)1);
        recyclerViewContact.setIndexBarTextColor("#d54a22");
        recyclerViewContact.setIndexBarColor("#FAFAFA");
        recyclerViewContact.setIndexBarCornerRadius(0);
        recyclerViewContact.setTypeface(Typeface.createFromAsset(AppController.getInstance().getAssets(), "appfont/arial-bold.ttf"));
        recyclerViewContact.setPreviewPadding(0);
        recyclerViewContact.setIndexbarMargin((float)10);
        if (!createGroupActivity.isFromGroupInfoActivity.equals("isFromGroupInfoActivity")) {
            createGroupAdapter = new CreateGroupAdapter(getActivity(), selectContactListener, DataHolder.getInstance().getQBUsers(), "fromCreateGroup");
        } else {
            createGroupAdapter = new CreateGroupAdapter(getActivity(), selectContactListener2, DataHolder.getInstance().getQBUsers(), "fromGroupInfo");
        }
        recyclerViewContact.setAdapter(createGroupAdapter);
        recyclerViewContact.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));

        searchEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text

                Log.v("CONTACTSFRAGMENT", "onTextChanged" + searchEditText.getText().toString());
                createGroupAdapter.searchContact(cs);

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.v("CONTACTSFRAGMENT", "afterTextChanged" + searchEditText.getText().toString());
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub
                Log.v("CONTACTSFRAGMENT", "beforeTextChanged" + searchEditText.getText().toString());
            }


        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
//        contactDisplayAdapter.updateAdapter(qbUsersListForDisplay);
//        createGroupAdapter.notifyDataSetChanged();
        if (contactDisplayAdapter.getItemCount() == 0) {
            recyclerViewContactDisplay.setVisibility(View.GONE);
        } else {
            recyclerViewContactDisplay.setVisibility(View.VISIBLE);
        }

        //update group occupants
        createGroupAdapter.occupantsList(createGroupActivity.qbUsers);
        contactDisplayAdapter.occupantList(createGroupActivity.qbUsers);
        recyclerViewContactDisplay.setVisibility(View.VISIBLE);
    }

    SelectContactListener selectContactListener = new SelectContactListener() {
        @Override
        public void onSelectContactListener(QBUser qbUser, boolean isFromDisplay) {
            Boolean isAdded = false;
            int positionToBeRemoved = 0;
            for (int i = 0; i<qbUsersListForDisplay.size(); i++){
                if (qbUsersListForDisplay.get(i).equals(qbUser)) {
                    isAdded = true;
                    positionToBeRemoved = i;
                }
            }
            if(isAdded){
                qbUsersListForDisplay.remove(positionToBeRemoved);
                occupantIdsList.remove(positionToBeRemoved);
            } else {
                qbUsersListForDisplay.add(qbUser);
                occupantIdsList.add(qbUser.getId());
            }

            contactDisplayAdapter.updateAdapter(qbUsersListForDisplay);

            if(isFromDisplay){
                createGroupAdapter.updateAdapter(qbUser);
            }

            if (contactDisplayAdapter.getItemCount() == 0) {
                recyclerViewContactDisplay.setVisibility(View.GONE);
            } else {
                recyclerViewContactDisplay.setVisibility(View.VISIBLE);
            }
            CreateGroupActivity.updateNumberOfParticipants(qbUsersListForDisplay.size());
        }
    };

    SelectContactListener2 selectContactListener2 = new SelectContactListener2() {
        @Override
        public void onSelectContactListener(QBUser qbUser, boolean isFromDisplay) {
            if(isFromDisplay){
                createGroupAdapter.updateAdapter(qbUser);
                createGroupActivity.qbUsers.remove(qbUser);
                contactDisplayAdapter.occupantList(createGroupActivity.qbUsers);
            } else {
                if (createGroupActivity.qbUsers.contains(qbUser)) {
                    createGroupActivity.qbUsers.remove(qbUser);
                    contactDisplayAdapter.updateAdapter(createGroupActivity.qbUsers);
                } else {
                    createGroupActivity.qbUsers.add(qbUser);
                    contactDisplayAdapter.updateAdapter(createGroupActivity.qbUsers);
                }
            }
        }
    };
}
