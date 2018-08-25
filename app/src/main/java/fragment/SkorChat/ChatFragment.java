package fragment.SkorChat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBIncomingMessagesManager;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.root.skor.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import activity.skorchat.ChattingActivity;
import adaptor.DialogAdapter;
import adaptor.DialogAdapter2;
import bean.QuickbloxChat;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import listener.DeleteDialogListener;
import listener.SelectUserForChatListener;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import singleton.SettingsManager;
import utils.AppController;
import utils.DataHolder;

/**
 * Created by dss-17 on 5/17/17.
 */

public class ChatFragment extends Fragment {

    DialogAdapter2 dialogAdapter;
    RecyclerView recyclerView;

    QBIncomingMessagesManager qbIncomingMessagesManager;
    QBChatService qbChatService;
    Realm realm;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, null);
        recyclerView = view.findViewById(R.id.fragment_chat_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        qbChatService = QBChatService.getInstance();
        realm = AppController.getRealm();
        qbIncomingMessagesManager = qbChatService.getIncomingMessagesManager();
        try {
            qbIncomingMessagesManager.addDialogMessageListener(new QBChatDialogMessageListener() {
                @Override
                public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {
                    getChatDialog(s);
                }

                @Override
                public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {

                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        List<QBUser> userList = DataHolder.getInstance().getQBUsers();
        final Map<Integer, QBUser> qbUserHashmap = new HashMap<>();
        for (int i = 0; i < userList.size(); i++) {
            qbUserHashmap.put(userList.get(i).getId(), userList.get(i));
        }
        dialogAdapter = new DialogAdapter2(getActivity(), listener, deleteDialogListener, dialogAdapterInterface, qbUserHashmap);
        recyclerView.setAdapter(dialogAdapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadLocalData();
    }

    private void reloadLocalData(){
        RealmResults<QuickbloxChat> quickbloxChatRealmResults = realm.where(QuickbloxChat.class).findAll();
        ArrayList<QuickbloxChat> quickbloxChatArrayList = new ArrayList<>();
        quickbloxChatArrayList.addAll(quickbloxChatRealmResults.subList(0,quickbloxChatRealmResults.size()));
        if(quickbloxChatArrayList.size()!=0){
            loadData(quickbloxChatArrayList);
        }
        receiveChatList();
    }

    private void getChatDialog(String chatId){
        QBRestChatService.getChatDialogById(chatId).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                QuickbloxChat quickbloxChat = new QuickbloxChat(
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
                        qbChatDialog.getRecipientId());
                dialogAdapter.itemChanged(quickbloxChat);
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });
    }

    private void receiveChatList() {
        QBRequestGetBuilder requestBuilder = new QBRequestGetBuilder();
        requestBuilder.setLimit(100);
        QBRestChatService.getChatDialogs(null, requestBuilder).performAsync(
                new QBEntityCallback<ArrayList<QBChatDialog>>() {
                    @Override
                    public void onSuccess(final ArrayList<QBChatDialog> qbChatDialogs, Bundle params) {
                        ArrayList<QuickbloxChat> quickbloxChatDialogList = new ArrayList<QuickbloxChat>();
                        RealmList<QuickbloxChat> quickbloxChatRealmList = new RealmList<QuickbloxChat>();
                        for (int i = 0; i < qbChatDialogs.size(); i++) {
                                QBChatDialog qbChatDialog = qbChatDialogs.get(i);
                                quickbloxChatDialogList.add(new QuickbloxChat(
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
                        quickbloxChatRealmList.addAll(quickbloxChatDialogList);
                        realm.beginTransaction();
                        realm.where(QuickbloxChat.class).findAll().deleteAllFromRealm();
                        realm.copyToRealmOrUpdate(quickbloxChatRealmList);
                        realm.commitTransaction();
                        loadData(quickbloxChatDialogList);
                    }

                    @Override
                    public void onError(QBResponseException responseException) {

                    }
                });
    }

    private void loadData(ArrayList<QuickbloxChat> quickbloxChatArrayList){
        Observable<ArrayList<QuickbloxChat>> observable = Observable.just(quickbloxChatArrayList);
        Observer<ArrayList<QuickbloxChat>> observer = new Observer<ArrayList<QuickbloxChat>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull ArrayList<QuickbloxChat> quickbloxChatArrayList) {

            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
//        observable.subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(observer);
        dialogAdapter.updateAdapter(quickbloxChatArrayList);
    }

    DeleteDialogListener deleteDialogListener = new DeleteDialogListener() {
        @Override
        public void onDeleteDialogListener(QuickbloxChat qbChatDialog) {
//            QBRestChatService.deleteDialog(qbChatDialog.getDialogId(), true).performAsync(new QBEntityCallback<Void>() {
//                @Override
//                public void onSuccess(Void aVoid, Bundle bundle) {
//                    Toast.makeText(getContext(), "success delete dialog", Toast.LENGTH_SHORT).show();
//                    receiveChatList();
//                }
//
//                @Override
//                public void onError(QBResponseException e) {
//                    Toast.makeText(getContext(), "failed delete dialog", Toast.LENGTH_SHORT).show();
//                }
//            });
        }
    };

    SelectUserForChatListener listener = new SelectUserForChatListener() {
        @Override
        public void onSelectUserForChatListener(final String qbChatDialogId) {
            startActivity(new Intent(getContext(), ChattingActivity.class).putExtra("qbChatDialogId",qbChatDialogId));
        }
    };

    DialogAdapter2.DialogAdapterInterface dialogAdapterInterface = new DialogAdapter2.DialogAdapterInterface() {

        @Override
        public void onItemChanged(ArrayList<QuickbloxChat> chatList) {
            RealmList<QuickbloxChat> quickbloxChatRealmList = new RealmList<>();
            quickbloxChatRealmList.addAll(chatList);
            realm.beginTransaction();
            realm.where(QuickbloxChat.class).findAll().deleteAllFromRealm();
            realm.copyToRealmOrUpdate(chatList);
            realm.commitTransaction();
        }
    };
}
