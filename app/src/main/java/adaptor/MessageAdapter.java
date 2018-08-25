package adaptor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBIncomingMessagesManager;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.root.skor.R;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import listener.SelectAttachmentListener;

/**
 * Created by dss-17 on 5/15/17.
 */

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<QBChatMessage> qbChatMessages = new ArrayList<>();
    private Context context;
    private QBUser qbUser;
    Calendar calendar = Calendar.getInstance();
    SelectAttachmentListener selectAttachmentListener;
    QBChatDialog qbChatDialog;

    private static int OWN_MESSAGE = 0;
    private static int OPPONENT_MESSAGE = 1;

    public void recipientInfo(QBChatDialog qbChatDialog) {
        this.qbChatDialog = qbChatDialog;
    }

    public MessageAdapter(Context context, SelectAttachmentListener selectAttachmentListener, QBUser qbUser) {
        this.context = context;
        this.selectAttachmentListener = selectAttachmentListener;
        this.qbUser = qbUser;
    }

    public void updateAdapter(int skip, ArrayList<QBChatMessage> qbChatMessages) {
        ArrayList<QBChatMessage> qbChatMessageArrayList = new ArrayList<>();
        qbChatMessageArrayList.addAll(qbChatMessages);

        if (skip > 0) {
            qbChatMessageArrayList.addAll(this.qbChatMessages);
        }

        this.qbChatMessages = qbChatMessageArrayList;

        for (QBChatMessage a : this.qbChatMessages) {
            Log.v("chat-adapter", "skip: " + skip + " chat: " + a.getBody());
        }

        notifyDataSetChanged();
    }

    public void updateAttachment(int position, QBChatMessage message) {
        this.qbChatMessages.remove(position);
        this.qbChatMessages.add(position, message);

        notifyDataSetChanged();
    }

    private boolean isIncoming(QBChatMessage chatMessage) {
        return chatMessage.getSenderId() != null && !chatMessage.getSenderId().equals(QBChatService.getInstance().getUser().getId());
    }

    public void add(QBChatMessage message) {
        this.qbChatMessages.add(message);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == OWN_MESSAGE) {
            return new MessageAdapter.ItemOwnMessageHolder(LayoutInflater.from(context).inflate(R.layout.item_message, parent, false));
        } else {
            return new MessageAdapter.ItemOpponentMessageHolder(LayoutInflater.from(context).inflate(R.layout.item_opponent_message, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Calendar calendarForMessageTime = Calendar.getInstance();
        calendarForMessageTime.setTimeInMillis(qbChatMessages.get(position).getDateSent() * 1000);
        if (isIncoming(qbChatMessages.get(position))) {
            final ItemOpponentMessageHolder itemOpponentMessageHolder = (ItemOpponentMessageHolder) holder;
            // incoming message

            String type = qbChatDialog.getType().toString();
            if (type.equals("GROUP")) {
                itemOpponentMessageHolder.userNameTextView.setVisibility(View.VISIBLE);
                itemOpponentMessageHolder.timeStampGroupChat.setVisibility(View.VISIBLE);
                itemOpponentMessageHolder.timeStampPrivateChat.setVisibility(View.GONE);
                itemOpponentMessageHolder.userProfileFrameLayout.setVisibility(View.VISIBLE);

                QBUsers.getUser(qbChatMessages.get(position).getSenderId()).performAsync(new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser qbUser, Bundle bundle) {
                        itemOpponentMessageHolder.userNameTextView.setText(qbUser.getFullName());

//                        if(qbUser.getCustomData() != null && !qbUser.getCustomData().equals("null")){
//                            Glide.with(context).load(qbUser.getCustomData()).into(itemOpponentMessageHolder.userProfileImageView);
//                            itemOpponentMessageHolder.profilePictureTextView.setVisibility(View.GONE);
//                        }
                        String name = qbUser.getFullName().charAt(0) + "";
                        ColorDrawable cd = new ColorDrawable(ContextCompat.getColor(context, R.color.loading_yellow));
                        itemOpponentMessageHolder.userProfileImageView.setImageDrawable(cd);
                        itemOpponentMessageHolder.profilePictureTextView.setText(name);
                        itemOpponentMessageHolder.profilePictureTextView.setAllCaps(true);
                    }

                    @Override
                    public void onError(QBResponseException e) {

                    }
                });

            } else {
                itemOpponentMessageHolder.userNameTextView.setVisibility(View.GONE);
                itemOpponentMessageHolder.timeStampGroupChat.setVisibility(View.GONE);
                itemOpponentMessageHolder.timeStampPrivateChat.setVisibility(View.VISIBLE);
                itemOpponentMessageHolder.userProfileFrameLayout.setVisibility(View.GONE);
            }

            //timestamp
            if (calendar.get(Calendar.MONTH) == calendarForMessageTime.get(Calendar.MONTH) &&
                    calendar.get(Calendar.YEAR) == calendarForMessageTime.get(Calendar.YEAR) &&
                    calendar.get(Calendar.DAY_OF_MONTH) == calendarForMessageTime.get(Calendar.DAY_OF_MONTH)) {
                DateFormat messageTimeFormat = new SimpleDateFormat("h:mm a");
                String dateFormatForMessage = messageTimeFormat.format(calendarForMessageTime.getTime());
                itemOpponentMessageHolder.timeStampGroupChat.setText(dateFormatForMessage);
                itemOpponentMessageHolder.timeStampPrivateChat.setText(dateFormatForMessage);
            } else {
                DateFormat messageTimeFormat = new SimpleDateFormat("MMM d");
                String dateFormatForMessage = messageTimeFormat.format(calendarForMessageTime.getTime());
                itemOpponentMessageHolder.timeStampGroupChat.setText(dateFormatForMessage);
                itemOpponentMessageHolder.timeStampPrivateChat.setText(dateFormatForMessage);
            }

            itemOpponentMessageHolder.attachmentPhoto.setImageDrawable(null);

            if (qbChatMessages.get(position).getAttachments() == null) {
                // message chat type

                itemOpponentMessageHolder.message.setVisibility(View.VISIBLE);
                itemOpponentMessageHolder.attachmentPhoto.setVisibility(View.GONE);
                itemOpponentMessageHolder.playButton.setVisibility(View.GONE);
                itemOpponentMessageHolder.webDescriptionLinearLayout.setVisibility(View.GONE);
                itemOpponentMessageHolder.documentContactLinearLayout.setVisibility(View.GONE);

                itemOpponentMessageHolder.message.setText(qbChatMessages.get(position).getBody());
                if (qbChatMessages.get(position).getProperties().get("web_title") != null && qbChatMessages.get(position).getProperties().get("web_imageUrl") != null) {
                    itemOpponentMessageHolder.webDescriptionLinearLayout.setVisibility(View.VISIBLE);
                    itemOpponentMessageHolder.message.setText(qbChatMessages.get(position).getBody());
                    itemOpponentMessageHolder.webTitle.setText(qbChatMessages.get(position).getProperties().get("web_title"));
                    itemOpponentMessageHolder.webDescription.setText(qbChatMessages.get(position).getProperties().get("web_description"));
                    Glide.with(context).load(qbChatMessages.get(position).getProperties().get("web_imageUrl")).into(itemOpponentMessageHolder.webImage);
                }

            } else {
                if (qbChatMessages.get(position).getAttachments().size() == 0) {
                    // message chat type

                    itemOpponentMessageHolder.message.setVisibility(View.VISIBLE);
                    itemOpponentMessageHolder.attachmentPhoto.setVisibility(View.GONE);
                    itemOpponentMessageHolder.playButton.setVisibility(View.GONE);
                    itemOpponentMessageHolder.webDescriptionLinearLayout.setVisibility(View.GONE);
                    itemOpponentMessageHolder.documentContactLinearLayout.setVisibility(View.GONE);

                    itemOpponentMessageHolder.message.setText(qbChatMessages.get(position).getBody());
                    if (qbChatMessages.get(position).getProperties().size() < 2) {
                        itemOpponentMessageHolder.webDescriptionLinearLayout.setVisibility(View.VISIBLE);
                        itemOpponentMessageHolder.message.setText(qbChatMessages.get(position).getBody());
                        itemOpponentMessageHolder.webTitle.setText(qbChatMessages.get(position).getProperties().get("web_title"));
                        itemOpponentMessageHolder.webDescription.setText(qbChatMessages.get(position).getProperties().get("web_description"));
                        Glide.with(context).load(qbChatMessages.get(position).getProperties().get("web_imageUrl")).into(itemOpponentMessageHolder.webImage);
                    }

                } else {
                    // attachment, location, etc type

                    if (qbChatMessages.get(position).getProperties().get("type") == null) {
                        // location type

                        itemOpponentMessageHolder.message.setVisibility(View.GONE);
                        itemOpponentMessageHolder.attachmentPhoto.setVisibility(View.VISIBLE);
                        itemOpponentMessageHolder.playButton.setVisibility(View.GONE);
                        itemOpponentMessageHolder.webDescriptionLinearLayout.setVisibility(View.GONE);
                        itemOpponentMessageHolder.documentContactLinearLayout.setVisibility(View.GONE);

                        String locationId = qbChatMessages.get(position).getAttachments().iterator().next().getId();
                        String[] parts = locationId.split("_");
                        if (parts.length > 1) {
                            String lat = parts[0];
                            String lng = parts[1];
                            String mapUrl = "http://maps.googleapis.com/maps/api/staticmap?zoom=15&size=560x240&markers=size:mid|color:red|"
                                    + lat
                                    + ","
                                    + lng
                                    + "&sensor=false";
                            Glide.with(context).load(mapUrl).into(itemOpponentMessageHolder.attachmentPhoto);
                        }
                    } else {
                        if (qbChatMessages.get(position).getProperties().get("type").equals("image/jpeg") || qbChatMessages.get(position).getProperties().get("type").equals("image/png")) {
                            // image type

                            itemOpponentMessageHolder.message.setVisibility(View.VISIBLE);
                            itemOpponentMessageHolder.attachmentPhoto.setVisibility(View.VISIBLE);
                            itemOpponentMessageHolder.playButton.setVisibility(View.GONE);
                            itemOpponentMessageHolder.webDescriptionLinearLayout.setVisibility(View.GONE);
                            itemOpponentMessageHolder.documentContactLinearLayout.setVisibility(View.GONE);

                            Glide.with(context).load(qbChatMessages.get(position).getProperties().get("url")).into(itemOpponentMessageHolder.attachmentPhoto);
                            if (qbChatMessages.get(position).getBody().equals("null") || qbChatMessages.get(position).getBody().equals("")) {
                                itemOpponentMessageHolder.message.setVisibility(View.GONE);
                            } else {
                                itemOpponentMessageHolder.message.setText(qbChatMessages.get(position).getBody());
                            }

                        } else if (qbChatMessages.get(position).getProperties().get("type").equals("application/pdf") ||
                                qbChatMessages.get(position).getProperties().get("type").equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") ||
                                qbChatMessages.get(position).getProperties().get("type").equals("application/msword") ||
                                qbChatMessages.get(position).getProperties().get("type").equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") ||
                                qbChatMessages.get(position).getProperties().get("type").equals("application/vnd.ms-excel") ||
                                qbChatMessages.get(position).getProperties().get("type").equals("application/vnd.openxmlformats-officedocument.presentationml.presentation") ||
                                qbChatMessages.get(position).getProperties().get("type").equals("application/vnd.ms-powerpoint")) {
                            // document type

                            itemOpponentMessageHolder.message.setVisibility(View.GONE);
                            itemOpponentMessageHolder.attachmentPhoto.setVisibility(View.GONE);
                            itemOpponentMessageHolder.playButton.setVisibility(View.GONE);
                            itemOpponentMessageHolder.webDescriptionLinearLayout.setVisibility(View.GONE);
                            itemOpponentMessageHolder.documentContactLinearLayout.setVisibility(View.VISIBLE);

                            String documentType;
                            if (qbChatMessages.get(position).getProperties().get("type").equals("application/pdf")) {
                                documentType = "PDF";
                            } else if (qbChatMessages.get(position).getProperties().get("type").equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") ||
                                    qbChatMessages.get(position).getProperties().get("type").equals("application/msword")) {
                                documentType = "Word Document";
                            } else if (qbChatMessages.get(position).getProperties().get("type").equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") ||
                                    qbChatMessages.get(position).getProperties().get("type").equals("application/vnd.ms-excel")) {
                                documentType = "Excel Document";
                            } else if (qbChatMessages.get(position).getProperties().get("type").equals("application/vnd.openxmlformats-officedocument.presentationml.presentation") ||
                                    qbChatMessages.get(position).getProperties().get("type").equals("application/vnd.ms-powerpoint")) {
                                documentType = "Power Point Presentation";
                            } else {
                                documentType = "Document";
                            }

                            Glide.with(context).load(R.drawable.ic_word).into(itemOpponentMessageHolder.documentOrContactImage);
                            itemOpponentMessageHolder.documentOrContactName.setText(qbChatMessages.get(position).getProperties().get("name"));
                            double size = Integer.parseInt(qbChatMessages.get(position).getProperties().get("size")) / 1000.0;
                            String fileSize = String.format("%.1f", size) + " kb " + documentType;
                            itemOpponentMessageHolder.documentSize.setText(fileSize);

                        } else if (qbChatMessages.get(position).getProperties().get("type").equals("text/x-vcard")) {
                            // contact type

                            itemOpponentMessageHolder.message.setVisibility(View.GONE);
                            itemOpponentMessageHolder.attachmentPhoto.setVisibility(View.GONE);
                            itemOpponentMessageHolder.playButton.setVisibility(View.GONE);
                            itemOpponentMessageHolder.webDescriptionLinearLayout.setVisibility(View.GONE);
                            itemOpponentMessageHolder.documentContactLinearLayout.setVisibility(View.VISIBLE);

                            Glide.with(context).load(R.drawable.contact).into(itemOpponentMessageHolder.documentOrContactImage);
                            itemOpponentMessageHolder.documentOrContactName.setText(qbChatMessages.get(position).getProperties().get("name"));
                            itemOpponentMessageHolder.documentSize.setText(qbChatMessages.get(position).getProperties().get("size"));

                            double size = Integer.parseInt(qbChatMessages.get(position).getProperties().get("size")) / 1000.0;
                            String fileSize = String.format("%.1f", size) + " kb";
                            itemOpponentMessageHolder.documentSize.setText(fileSize);
                        } else if (qbChatMessages.get(position).getProperties().get("type").equals("video/mp4")) {
                            // video type

                            itemOpponentMessageHolder.message.setVisibility(View.VISIBLE);
                            itemOpponentMessageHolder.attachmentPhoto.setVisibility(View.VISIBLE);
                            itemOpponentMessageHolder.playButton.setVisibility(View.VISIBLE);
                            itemOpponentMessageHolder.webDescriptionLinearLayout.setVisibility(View.GONE);
                            itemOpponentMessageHolder.documentContactLinearLayout.setVisibility(View.GONE);

                            new LoadVideoThumbnail(itemOpponentMessageHolder.attachmentPhoto).execute(qbChatMessages.get(position).getProperties().get("url"));
                            if (qbChatMessages.get(position).getBody().equals("null") || qbChatMessages.get(position).getBody().equals("")) {
                                itemOpponentMessageHolder.message.setVisibility(View.GONE);
                            } else {
                                itemOpponentMessageHolder.message.setText(qbChatMessages.get(position).getBody());
                            }
                        }
                    }

                    itemOpponentMessageHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectAttachmentListener.onSelectAttachmentListener(qbChatMessages.get(position));
                        }
                    });
                }
            }
        } else {
            ItemOwnMessageHolder itemOwnMessageHolder = (ItemOwnMessageHolder) holder;
            // outgoing message

            Collection<Integer> readId = qbChatMessages.get(position).getReadIds();
            String type = qbChatDialog.getType().toString();
//            String totalReads = String.valueOf(readId.size()-1);

            //timestamp
            if (calendar.get(Calendar.MONTH) == calendarForMessageTime.get(Calendar.MONTH) &&
                    calendar.get(Calendar.YEAR) == calendarForMessageTime.get(Calendar.YEAR) &&
                    calendar.get(Calendar.DAY_OF_MONTH) == calendarForMessageTime.get(Calendar.DAY_OF_MONTH)) {
                DateFormat messageTimeFormat = new SimpleDateFormat("h:mm a");
                String dateFormatForMessage = messageTimeFormat.format(calendarForMessageTime.getTime());
                itemOwnMessageHolder.timeStamp.setText(dateFormatForMessage);
                if (readId != null) {
                    if (readId.size() > 1) {
                        if (type.equals("GROUP")) {
//                            itemOwnMessageHolder.timeStamp.setText(dateFormatForMessage + " READ BY " + totalReads);
                        } else {
                            itemOwnMessageHolder.timeStamp.setText(dateFormatForMessage + " READ");
                        }
                    }
                }
            } else {
                DateFormat messageTimeFormat = new SimpleDateFormat("MMM d");
                String dateFormatForMessage = messageTimeFormat.format(calendarForMessageTime.getTime());
                itemOwnMessageHolder.timeStamp.setText(dateFormatForMessage);
                if (readId.size() > 1) {
                    if (type.equals("GROUP")) {
//                        itemOwnMessageHolder.timeStamp.setText(dateFormatForMessage + " READ BY " + totalReads);
                    } else {
                        itemOwnMessageHolder.timeStamp.setText(dateFormatForMessage + " READ");
                    }
                }
            }

            itemOwnMessageHolder.attachmentPhoto.setImageDrawable(null);

            if (qbChatMessages.get(position).getAttachments() == null) {
                //message chat type

                itemOwnMessageHolder.message.setVisibility(View.VISIBLE);
                itemOwnMessageHolder.attachmentPhoto.setVisibility(View.GONE);
                itemOwnMessageHolder.playButton.setVisibility(View.GONE);
                itemOwnMessageHolder.webDescriptionLinearLayout.setVisibility(View.GONE);
                itemOwnMessageHolder.documentContactLinearLayout.setVisibility(View.GONE);

                itemOwnMessageHolder.message.setText(qbChatMessages.get(position).getBody());
                if (qbChatMessages.get(position).getProperties().get("web_title") != null && qbChatMessages.get(position).getProperties().get("web_imageUrl") != null) {
                    itemOwnMessageHolder.webDescriptionLinearLayout.setVisibility(View.VISIBLE);
                    itemOwnMessageHolder.message.setText(qbChatMessages.get(position).getBody());
                    itemOwnMessageHolder.webTitle.setText(qbChatMessages.get(position).getProperties().get("web_title"));
                    itemOwnMessageHolder.webDescription.setText(qbChatMessages.get(position).getProperties().get("web_description"));
                    Glide.with(context).load(qbChatMessages.get(position).getProperties().get("web_imageUrl")).into(itemOwnMessageHolder.webImage);
                }

            } else {
                if (qbChatMessages.get(position).getAttachments().size() == 0) {
                    // message chat type

                    itemOwnMessageHolder.message.setVisibility(View.VISIBLE);
                    itemOwnMessageHolder.attachmentPhoto.setVisibility(View.GONE);
                    itemOwnMessageHolder.playButton.setVisibility(View.GONE);
                    itemOwnMessageHolder.webDescriptionLinearLayout.setVisibility(View.GONE);
                    itemOwnMessageHolder.documentContactLinearLayout.setVisibility(View.GONE);

                    itemOwnMessageHolder.message.setText(qbChatMessages.get(position).getBody());
                    if (qbChatMessages.get(position).getProperties().size() < 2) {
                        itemOwnMessageHolder.webDescriptionLinearLayout.setVisibility(View.VISIBLE);
                        itemOwnMessageHolder.message.setText(qbChatMessages.get(position).getBody());
                        itemOwnMessageHolder.webTitle.setText(qbChatMessages.get(position).getProperties().get("web_title"));
                        itemOwnMessageHolder.webDescription.setText(qbChatMessages.get(position).getProperties().get("web_description"));
                        Glide.with(context).load(qbChatMessages.get(position).getProperties().get("web_imageUrl")).into(itemOwnMessageHolder.webImage);

                        itemOwnMessageHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                selectAttachmentListener.onSelectAttachmentListener(qbChatMessages.get(position));
                            }
                        });
                    }

                } else {
                    // attachment, location, etc type

                    if (qbChatMessages.get(position).getProperties().get("type") == null) {
                        // location type

                        itemOwnMessageHolder.message.setVisibility(View.GONE);
                        itemOwnMessageHolder.attachmentPhoto.setVisibility(View.VISIBLE);
                        itemOwnMessageHolder.playButton.setVisibility(View.GONE);
                        itemOwnMessageHolder.webDescriptionLinearLayout.setVisibility(View.GONE);
                        itemOwnMessageHolder.documentContactLinearLayout.setVisibility(View.GONE);

                        String locationId = qbChatMessages.get(position).getAttachments().iterator().next().getId();
                        String[] parts = locationId.split("_");
                        if (parts.length > 1) {
                            String lat = parts[0];
                            String lng = parts[1];
                            String mapUrl = "http://maps.googleapis.com/maps/api/staticmap?zoom=15&size=560x240&markers=size:mid|color:red|"
                                    + lat
                                    + ","
                                    + lng
                                    + "&sensor=false";
                            Glide.with(context).load(mapUrl).into(itemOwnMessageHolder.attachmentPhoto);
                        }
                    } else {
                        if (qbChatMessages.get(position).getProperties().get("type").equals("image/jpeg") || qbChatMessages.get(position).getProperties().get("type").equals("image/png")) {
                            // image type

                            itemOwnMessageHolder.message.setVisibility(View.VISIBLE);
                            itemOwnMessageHolder.attachmentPhoto.setVisibility(View.VISIBLE);
                            itemOwnMessageHolder.playButton.setVisibility(View.GONE);
                            itemOwnMessageHolder.webDescriptionLinearLayout.setVisibility(View.GONE);
                            itemOwnMessageHolder.documentContactLinearLayout.setVisibility(View.GONE);

                            Glide.with(context).load(qbChatMessages.get(position).getProperties().get("url")).into(itemOwnMessageHolder.attachmentPhoto);
                            if (qbChatMessages.get(position).getBody().equals("null") || qbChatMessages.get(position).getBody().equals("")) {
                                itemOwnMessageHolder.message.setVisibility(View.GONE);
                            } else {
                                itemOwnMessageHolder.message.setText(qbChatMessages.get(position).getBody());
                            }

                        } else if (qbChatMessages.get(position).getProperties().get("type").equals("application/pdf") ||
                                qbChatMessages.get(position).getProperties().get("type").equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") ||
                                qbChatMessages.get(position).getProperties().get("type").equals("application/msword") ||
                                qbChatMessages.get(position).getProperties().get("type").equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") ||
                                qbChatMessages.get(position).getProperties().get("type").equals("application/vnd.ms-excel") ||
                                qbChatMessages.get(position).getProperties().get("type").equals("application/vnd.openxmlformats-officedocument.presentationml.presentation") ||
                                qbChatMessages.get(position).getProperties().get("type").equals("application/vnd.ms-powerpoint")) {
                            // document type

                            itemOwnMessageHolder.message.setVisibility(View.GONE);
                            itemOwnMessageHolder.attachmentPhoto.setVisibility(View.GONE);
                            itemOwnMessageHolder.playButton.setVisibility(View.GONE);
                            itemOwnMessageHolder.webDescriptionLinearLayout.setVisibility(View.GONE);
                            itemOwnMessageHolder.documentContactLinearLayout.setVisibility(View.VISIBLE);

                            String documentType;
                            if (qbChatMessages.get(position).getProperties().get("type").equals("application/pdf")) {
                                documentType = "PDF";
                            } else if (qbChatMessages.get(position).getProperties().get("type").equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") ||
                                    qbChatMessages.get(position).getProperties().get("type").equals("application/msword")) {
                                documentType = "Word Document";
                            } else if (qbChatMessages.get(position).getProperties().get("type").equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") ||
                                    qbChatMessages.get(position).getProperties().get("type").equals("application/vnd.ms-excel")) {
                                documentType = "Excel Document";
                            } else if (qbChatMessages.get(position).getProperties().get("type").equals("application/vnd.openxmlformats-officedocument.presentationml.presentation") ||
                                    qbChatMessages.get(position).getProperties().get("type").equals("application/vnd.ms-powerpoint")) {
                                documentType = "Power Point Presentation";
                            } else {
                                documentType = "Document";
                            }

                            Glide.with(context).load(R.drawable.ic_word).into(itemOwnMessageHolder.documentOrContactImage);
                            itemOwnMessageHolder.documentOrContactName.setText(qbChatMessages.get(position).getProperties().get("name"));
                            double size = Integer.parseInt(qbChatMessages.get(position).getProperties().get("size")) / 1000.0;
                            String fileSize = String.format("%.1f", size) + " kb " + documentType;
                            itemOwnMessageHolder.documentSize.setText(fileSize);

                        } else if (qbChatMessages.get(position).getProperties().get("type").equals("text/x-vcard")) {
                            // contact type

                            itemOwnMessageHolder.message.setVisibility(View.GONE);
                            itemOwnMessageHolder.attachmentPhoto.setVisibility(View.GONE);
                            itemOwnMessageHolder.playButton.setVisibility(View.GONE);
                            itemOwnMessageHolder.webDescriptionLinearLayout.setVisibility(View.GONE);
                            itemOwnMessageHolder.documentContactLinearLayout.setVisibility(View.VISIBLE);

                            Glide.with(context).load(R.drawable.contact).into(itemOwnMessageHolder.documentOrContactImage);
                            itemOwnMessageHolder.documentOrContactName.setText(qbChatMessages.get(position).getProperties().get("name"));
                            itemOwnMessageHolder.documentSize.setText(qbChatMessages.get(position).getProperties().get("size"));

                            double size = Integer.parseInt(qbChatMessages.get(position).getProperties().get("size")) / 1000.0;
                            String fileSize = String.format("%.1f", size) + " kb";
                            itemOwnMessageHolder.documentSize.setText(fileSize);

                        } else if (qbChatMessages.get(position).getProperties().get("type").equals("video/mp4")) {
                            // video type

                            itemOwnMessageHolder.message.setVisibility(View.VISIBLE);
                            itemOwnMessageHolder.attachmentPhoto.setVisibility(View.VISIBLE);
                            itemOwnMessageHolder.playButton.setVisibility(View.VISIBLE);
                            itemOwnMessageHolder.webDescriptionLinearLayout.setVisibility(View.GONE);
                            itemOwnMessageHolder.documentContactLinearLayout.setVisibility(View.GONE);

                            new LoadVideoThumbnail(itemOwnMessageHolder.attachmentPhoto).execute(qbChatMessages.get(position).getProperties().get("url"));
                            if (qbChatMessages.get(position).getBody().equals("null") || qbChatMessages.get(position).getBody().equals("")) {
                                itemOwnMessageHolder.message.setVisibility(View.GONE);
                            } else {
                                itemOwnMessageHolder.message.setText(qbChatMessages.get(position).getBody());
                            }
                        }
                    }
                    itemOwnMessageHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectAttachmentListener.onSelectAttachmentListener(qbChatMessages.get(position));
                        }
                    });
                }
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isIncoming(qbChatMessages.get(position))) {
            return OPPONENT_MESSAGE;
        } else {
            return OWN_MESSAGE;
        }
    }

    @Override
    public int getItemCount() {
        return qbChatMessages.size();
    }

    public class ItemOwnMessageHolder extends RecyclerView.ViewHolder {
        LinearLayout webDescriptionLinearLayout, documentContactLinearLayout;
        RelativeLayout parentLayout;
        TextView message, timeStamp, webDescription, webTitle, documentOrContactName, documentSize;
        ImageView attachmentPhoto, playButton, webImage, documentOrContactImage;

        public ItemOwnMessageHolder(View view) {
            super(view);
            parentLayout = (RelativeLayout) view.findViewById(R.id.item_own_message_linearLayout);
            message = (TextView) view.findViewById(R.id.item_own_message_message);
            timeStamp = (TextView) view.findViewById(R.id.item_own_message_timeStampAndReadyBy);
            attachmentPhoto = (ImageView) view.findViewById(R.id.item_own_message_attachment);
            playButton = (ImageView) view.findViewById(R.id.item_own_message_playButton);
            webDescription = (TextView) view.findViewById(R.id.item_own_message_webDescription);
            webTitle = (TextView) view.findViewById(R.id.item_own_message_webTitle);
            webDescription = (TextView) view.findViewById(R.id.item_own_message_webDescription);
            webImage = (ImageView) view.findViewById(R.id.item_own_message_webImage);
            webDescriptionLinearLayout = (LinearLayout) view.findViewById(R.id.item_own_message_webDescriptionLinearLayout);
            documentContactLinearLayout = (LinearLayout) view.findViewById(R.id.item_own_message_documentContactLinearLayout);
            documentOrContactImage = (ImageView) view.findViewById(R.id.item_own_message_documentContactImageView);
            documentOrContactName = (TextView) view.findViewById(R.id.item_own_message_documentContactName);
            documentSize = (TextView) view.findViewById(R.id.item_own_message_documentSize);
        }
    }

    public class ItemOpponentMessageHolder extends RecyclerView.ViewHolder {
        LinearLayout webDescriptionLinearLayout, documentContactLinearLayout;
        RelativeLayout parentLayout;
        TextView message, timeStampGroupChat, timeStampPrivateChat, profilePictureTextView, webDescription, webTitle, documentOrContactName, documentSize;
        ImageView attachmentPhoto, playButton, webImage, documentOrContactImage;
        CircleImageView userProfileImageView;
        TextView userNameTextView;
        FrameLayout userProfileFrameLayout;

        public ItemOpponentMessageHolder(View view) {
            super(view);
            parentLayout = (RelativeLayout) view.findViewById(R.id.item_opponent_message_relativeLayout);
            message = (TextView) view.findViewById(R.id.item_opponent_message_message);
            timeStampGroupChat = (TextView) view.findViewById(R.id.item_opponent_message_timeStampGroupChatTextView);
            timeStampPrivateChat = (TextView) view.findViewById(R.id.item_opponent_message_timeStampPrivateChatTextView);
            attachmentPhoto = (ImageView) view.findViewById(R.id.item_opponent_message_attachment);
            userProfileImageView = (CircleImageView) view.findViewById(R.id.item_opponent_message_userProfileImageView);
            userNameTextView = (TextView) view.findViewById(R.id.item_opponent_message_userNameTextView);
            userProfileFrameLayout = (FrameLayout) view.findViewById(R.id.item_opponent_message_userProfileFrameLayout);
            profilePictureTextView = (TextView) view.findViewById(R.id.item_opponent_message_userProfilePictureTextView);
            playButton = (ImageView) view.findViewById(R.id.item_opponent_message_playButton);
            webTitle = (TextView) view.findViewById(R.id.item_opponent_message_webTitle);
            webDescription = (TextView) view.findViewById(R.id.item_opponent_message_webDescription);
            webImage = (ImageView) view.findViewById(R.id.item_opponent_message_webImage);
            webDescriptionLinearLayout = (LinearLayout) view.findViewById(R.id.item_opponent_message_webDescriptionLinearLayout);
            documentContactLinearLayout = (LinearLayout) view.findViewById(R.id.item_opponent_message_documentContactLinearLayout);
            documentOrContactImage = (ImageView) view.findViewById(R.id.item_opponent_message_documentContactImageView);
            documentOrContactName = (TextView) view.findViewById(R.id.item_opponent_message_documentContactName);
            documentSize = (TextView) view.findViewById(R.id.item_opponent_message_documentSize);
        }
    }

    Bitmap bitmap = null;

    public Bitmap retriveVideoFrameFromVideo(String videoPath) throws Throwable {
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14)
                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            else
                mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Throwable("Exception in retriveVideoFrameFromVideo(String videoPath)" + e.getMessage());
        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }

    private class LoadVideoThumbnail extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;

        public LoadVideoThumbnail(ImageView imageView) {
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                return retriveVideoFrameFromVideo(params[0]);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            if (imageViewReference != null) {
                ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                    }
                }
            }
        }
    }


//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        View List;
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        if (convertView == null) {
//            List = inflater.inflate(R.layout.item_message, null);
//
//            LinearLayout parentLinearLayout = (LinearLayout) List.findViewById(R.id.item_message_linearLayout);
//            TextView message = (TextView) List.findViewById(R.id.item_own_message_message);
//            TextView timeStamp = (TextView)List.findViewById(R.id.item_own_message_timeStamp);
//            ImageView attachmentPhoto = (ImageView) List.findViewById(R.id.item_own_message_attachment);
//
//            if(isIncoming(qbChatMessages.get(position))) {
//                // incoming message
//                parentLinearLayout.setBackgroundResource(R.drawable.left_bubble);
//            } else {
//                // outgoing message
//                parentLinearLayout.setBackgroundResource(R.drawable.right_bubble );
//            }
//
//            //pdf, docx, doc , xlsx, xls, pptx ,ppt
//            // application/pdf,
//            // application/vnd.openxmlformats-officedocument.wordprocessingml.document,
//            // application/msword,
//            // application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,
//            // application/vnd.ms-excel,
//            // application/vnd.openxmlformats-officedocument.presentationml.presentation,
//            // application/vnd.ms-powerpoint
//
//            attachmentPhoto.setImageDrawable(null);
//
//            if(qbChatMessages.get(position).getAttachments().size()!=0) {
//                if (qbChatMessages.get(position).getAttachments().iterator().hasNext()) {
//                    if(qbChatMessages.get(position).getAttachments().iterator().next().getType().equals("application/pdf") ||
//                            qbChatMessages.get(position).getAttachments().iterator().next().getType().equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") ||
//                            qbChatMessages.get(position).getAttachments().iterator().next().getType().equals("application/msword") ||
//                            qbChatMessages.get(position).getAttachments().iterator().next().getType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") ||
//                            qbChatMessages.get(position).getAttachments().iterator().next().getType().equals("application/vnd.ms-excel") ||
//                            qbChatMessages.get(position).getAttachments().iterator().next().getType().equals("application/vnd.openxmlformats-officedocument.presentationml.presentation") ||
//                            qbChatMessages.get(position).getAttachments().iterator().next().getType().equals("application/vnd.ms-powerpoint")){
//                        attachmentPhoto.setVisibility(View.VISIBLE);
//                        Glide.with(context).load(qbChatMessages.get(position).getAttachments().iterator().next().getUrl()).into(attachmentPhoto);
//                        message.setText(qbChatMessages.get(position).getAttachments().iterator().next().getName());
//                    } else if(qbChatMessages.get(position).getAttachments().iterator().next().getType().equals("location")){
//                        attachmentPhoto.setVisibility(View.VISIBLE);
//                        String locationId = qbChatMessages.get(position).getAttachments().iterator().next().getId();
//                        String[] parts = locationId.split("_");
//                        String lat = parts[0];
//                        String lng = parts[1];
//
//                        String mapUrl = "http://maps.googleapis.com/maps/api/staticmap?zoom=15&size=560x240&markers=size:mid|color:red|"
//                                + lat
//                                + ","
//                                + lng
//                                + "&sensor=false";
//                        Glide.with(context).load(mapUrl).into(attachmentPhoto);
//                    } else if(qbChatMessages.get(position).getAttachments().iterator().next().getType().equals("image/jpeg")) {
//                        attachmentPhoto.setVisibility(View.VISIBLE);
//                        Glide.with(context).load(qbChatMessages.get(position).getAttachments().iterator().next().getUrl()).into(attachmentPhoto);
//                        message.setText(qbChatMessages.get(position).getAttachments().iterator().next().getName());
//                    } else if(qbChatMessages.get(position).getAttachments().iterator().next().getType().equals("text/x-vcard")){
//                        attachmentPhoto.setVisibility(View.VISIBLE);
//                        Glide.with(context).load(qbChatMessages.get(position).getAttachments().iterator().next().getUrl()).into(attachmentPhoto);
//                        message.setText(qbChatMessages.get(position).getAttachments().iterator().next().getName());
//                    }
//                }
//            } else {
//                attachmentPhoto.setVisibility(View.GONE);
//                message.setText(qbChatMessages.get(position).getBody());
//                timeStamp.setText(utils.TimeUtils.getDate(qbChatMessages.get(position).getDateSent() * 1000));
//            }
//
//            return List;
//        } else {
//            List = convertView;
//
//            LinearLayout parentLinearLayout = (LinearLayout) List.findViewById(R.id.item_message_linearLayout);
//            TextView message = (TextView) List.findViewById(R.id.item_own_message_message);
//            TextView timeStamp = (TextView)List.findViewById(R.id.item_own_message_timeStamp);
//            ImageView attachmentPhoto = (ImageView) List.findViewById(R.id.item_own_message_attachment);
//
//            if(isIncoming(qbChatMessages.get(position))) {
//                // incoming message
//                parentLinearLayout.setBackgroundResource(R.drawable.left_bubble);
//            } else {
//                // outgoing message
//                parentLinearLayout.setBackgroundResource(R.drawable.right_bubble );
//            }
//
//            attachmentPhoto.setImageDrawable(null);
//
//            if(qbChatMessages.get(position).getAttachments().size()!=0) {
//                if (qbChatMessages.get(position).getAttachments().iterator().hasNext()) {
//                    if(qbChatMessages.get(position).getAttachments().iterator().next().getType().equals("application/pdf") ||
//                            qbChatMessages.get(position).getAttachments().iterator().next().getType().equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") ||
//                            qbChatMessages.get(position).getAttachments().iterator().next().getType().equals("application/msword") ||
//                            qbChatMessages.get(position).getAttachments().iterator().next().getType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") ||
//                            qbChatMessages.get(position).getAttachments().iterator().next().getType().equals("application/vnd.ms-excel") ||
//                            qbChatMessages.get(position).getAttachments().iterator().next().getType().equals("application/vnd.openxmlformats-officedocument.presentationml.presentation") ||
//                            qbChatMessages.get(position).getAttachments().iterator().next().getType().equals("application/vnd.ms-powerpoint")){
//                        attachmentPhoto.setVisibility(View.VISIBLE);
//                        Glide.with(context).load(qbChatMessages.get(position).getAttachments().iterator().next().getUrl()).into(attachmentPhoto);
//                        message.setText(qbChatMessages.get(position).getAttachments().iterator().next().getName());
//                    } else if(qbChatMessages.get(position).getAttachments().iterator().next().getType().equals("location")){
//                        attachmentPhoto.setVisibility(View.VISIBLE);
//                        String locationId = qbChatMessages.get(position).getAttachments().iterator().next().getId();
//                        String[] parts = locationId.split("_");
//                        String lat = parts[0];
//                        String lng = parts[1];
//
//                        String mapUrl = "http://maps.googleapis.com/maps/api/staticmap?zoom=15&size=560x240&markers=size:mid|color:red|"
//                                + lat
//                                + ","
//                                + lng
//                                + "&sensor=false";
//                        Glide.with(context).load(mapUrl).into(attachmentPhoto);
//                    } else if(qbChatMessages.get(position).getAttachments().iterator().next().getType().equals("image/jpeg")){
//                        attachmentPhoto.setVisibility(View.VISIBLE);
//                        Glide.with(context).load(qbChatMessages.get(position).getAttachments().iterator().next().getUrl()).into(attachmentPhoto);
//                        message.setText(qbChatMessages.get(position).getAttachments().iterator().next().getName());
//                    } else if(qbChatMessages.get(position).getAttachments().iterator().next().getType().equals("text/x-vcard")){
//                        attachmentPhoto.setVisibility(View.VISIBLE);
//                        Glide.with(context).load(qbChatMessages.get(position).getAttachments().iterator().next().getUrl()).into(attachmentPhoto);
//                        message.setText(qbChatMessages.get(position).getAttachments().iterator().next().getName());
//                    }
//                }
//            } else {
//                attachmentPhoto.setVisibility(View.GONE);
//                message.setText(qbChatMessages.get(position).getBody());
//                timeStamp.setText(utils.TimeUtils.getDate(qbChatMessages.get(position).getDateSent() * 1000));
//            }
//        }
//
//        return List;
//    }

//    @Override
//    public int getCount() {
//        return qbChatMessages.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return null;
//    }


}
