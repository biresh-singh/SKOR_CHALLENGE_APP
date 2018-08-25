package fragment.SkorChat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.root.skor.R;

import activity.skorchat.SkorChatSettingsActivity;
import activity.userprofile.MainActivity;
import database.SharedDatabase;

/**
 * Created by dss-17 on 5/12/17.
 */

public class SkorChatFragment extends Fragment {

    FragmentManager fragmentManager;
    LinearLayout contactsLinearLayout, chatsLinearLayout, callsLinearLayout,menuPanelLinearLayout;
    public ImageView contactsImageView, chatsImageView, callsImageView, settingsImageView;
    TextView contactsTextView, chatsTextView, callsTextView;
    RelativeLayout titlebar;
    public SharedDatabase sharedDatabase;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_skor_chat, null);

        sharedDatabase = new SharedDatabase(getActivity());
        fragmentManager = getChildFragmentManager();
        contactsLinearLayout = (LinearLayout) view.findViewById(R.id.fragment_skor_chat_contactsLinearLayout);
        chatsLinearLayout = (LinearLayout) view.findViewById(R.id.fragment_skor_chat_chatsLinearLayout);
        callsLinearLayout = (LinearLayout) view.findViewById(R.id.fragment_skor_chat_callsLinearLayout);
        menuPanelLinearLayout = (LinearLayout) view.findViewById(R.id.fragment_skor_chat_menupanelLinearLayout);
        contactsImageView = (ImageView) view.findViewById(R.id.fragment_skor_chat_contactsImageView);
        callsImageView = (ImageView) view.findViewById(R.id.fragment_skor_chat_callsImageView);
        chatsImageView = (ImageView) view.findViewById(R.id.fragment_skor_chat_chatsImageView);
        settingsImageView = (ImageView) view.findViewById(R.id.fragment_skor_chat_settingsImageView);
        contactsTextView = (TextView) view.findViewById(R.id.fragment_skor_chat_contactsTextView);
        chatsTextView = (TextView) view.findViewById(R.id.fragment_skor_chat_chatsTextView);
        callsTextView = (TextView) view.findViewById(R.id.fragment_skor_chat_callsTextView);
        titlebar = (RelativeLayout) view.findViewById(R.id.titlebar);

        fragmentManager.beginTransaction()
                .replace(R.id.fragment_skor_chat_container, new ChatFragment())
                .commitNow();
        fragmentTracker();

        chatsLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_skor_chat_container, new ChatFragment())
                        .commitNowAllowingStateLoss();
                fragmentTracker();
            }
        });

        contactsLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction()
//                        .replace(R.id.fragment_skor_chat_container, new ContactsFragment())
                        .replace(R.id.fragment_skor_chat_container, new ContactsFragment2())
                        .commitNowAllowingStateLoss();
                fragmentTracker();
            }
        });

        callsLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_skor_chat_container, new CallsFragment())
                        .commitNowAllowingStateLoss();
                fragmentTracker();
            }
        });

        settingsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(getContext(), SkorChatSettingsActivity.class);
                startActivity(settingsIntent);
            }
        });

        menuPanelLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        return view;
    }
    void fragmentTracker() {
        Fragment mFragment = getChildFragmentManager().findFragmentById(R.id.fragment_skor_chat_container);

        contactsLinearLayout.setBackgroundResource(R.color.tab_gray);
        contactsImageView.setImageResource(R.drawable.tab_contact);
        contactsTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.tab_text_gray));
        chatsLinearLayout.setBackgroundResource(R.color.tab_gray);
        chatsImageView.setImageResource(R.drawable.tab_chat);
        chatsTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.tab_text_gray));
        callsLinearLayout.setBackgroundResource(R.color.tab_gray);
        callsImageView.setImageResource(R.drawable.tab_phone);
        callsTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.tab_text_gray));

//        if(mFragment instanceof  ContactsFragment){
        if (mFragment instanceof ContactsFragment2) {
            contactsLinearLayout.setBackgroundResource(R.color.tab_highlight_gray);
            contactsImageView.setImageResource(R.drawable.tab_contact_on);
            contactsTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.tab_text_highlight_red));
        } else if (mFragment instanceof ChatFragment) {
            chatsLinearLayout.setBackgroundResource(R.color.tab_highlight_gray);
            chatsImageView.setImageResource(R.drawable.tab_chat_on);
            chatsTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.tab_text_highlight_red));
        } else if (mFragment instanceof CallsFragment) {
            callsLinearLayout.setBackgroundResource(R.color.tab_highlight_gray);
            callsImageView.setImageResource(R.drawable.tab_phone_on);
            callsTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.tab_text_highlight_red));
        }

        View view = getActivity().getCurrentFocus();
        if (view != null){
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }




}
