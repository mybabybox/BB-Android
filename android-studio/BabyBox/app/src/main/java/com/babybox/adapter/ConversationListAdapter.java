package com.babybox.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.babybox.R;
import com.babybox.app.AppController;
import com.babybox.app.ConversationCache;
import com.babybox.app.UserInfoCache;
import com.babybox.util.DateTimeUtil;
import com.babybox.util.ImageUtil;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.ConversationVM;
import com.babybox.viewmodel.NewMessageVM;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ConversationListAdapter extends BaseAdapter {
    private static final String TAG = ConversationListAdapter.class.getName();

    private Activity activity;
    private LayoutInflater inflater;

    private List<ConversationVM> conversations;
    private boolean showPost;

    private LinearLayout conversationLayout;
    private LinearLayout postImageLayout, hasImageLayout;
    private ImageView userImage, postImage;
    private TextView userText, postTitleText, lastMessageText, buyText, sellText, soldText, dateText, unreadCountText;

    private RelativeLayout sellerAdminLayout;
    private Spinner colorSpinner, orderTransactionStateSpinner;

    private PopupWindow commentPopup;
    private TextView noteText, commentSendButton;
    private EditText commentEditText;

    private ColorSpinnerAdapter colorAdapter;
    private ArrayAdapter<String> stateAdapter;
    private SparseBooleanArray selectedItemsIds;

    private boolean pending = false;

    public ConversationListAdapter(Activity activity, List<ConversationVM> conversationVMs) {
        this(activity, conversationVMs, true);
    }

    public ConversationListAdapter(Activity activity, List<ConversationVM> conversations, boolean showPost) {
        this.activity = activity;
        this.conversations = conversations;
        this.showPost = showPost;
        this.selectedItemsIds = new SparseBooleanArray();
    }

    @Override
    public int getCount() {
        return conversations.size();
    }

    @Override
    public ConversationVM getItem(int i) {
        return conversations.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (view == null)
            view = inflater.inflate(R.layout.conversation_list_item, null);

        conversationLayout = (LinearLayout) view.findViewById(R.id.conversationLayout);
        userText = (TextView) view.findViewById(R.id.userText);
        postTitleText = (TextView) view.findViewById(R.id.postTitleText);
        lastMessageText = (TextView) view.findViewById(R.id.lastMessageText);
        hasImageLayout = (LinearLayout) view.findViewById(R.id.hasImageLayout);
        buyText = (TextView) view.findViewById(R.id.buyText);
        sellText = (TextView) view.findViewById(R.id.sellText);
        soldText = (TextView) view.findViewById(R.id.soldText);
        dateText = (TextView) view.findViewById(R.id.dateText);
        unreadCountText = (TextView) view.findViewById(R.id.unreadCountText);
        userImage = (ImageView) view.findViewById(R.id.userImage);
        postImageLayout = (LinearLayout) view.findViewById(R.id.postImageLayout);
        postImage = (ImageView) view.findViewById(R.id.postImage);
        sellerAdminLayout = (RelativeLayout) view.findViewById(R.id.sellerAdminLayout);
        colorSpinner = (Spinner) view.findViewById(R.id.colorSpinner);
        orderTransactionStateSpinner = (Spinner) view.findViewById(R.id.orderTransactionStateSpinner);
        noteText = (TextView) view.findViewById(R.id.noteText);

        final ConversationVM item = conversations.get(i);

        Log.d(TAG, "[" + i + "|id=" + item.id + "] " + item.getPostTitle() + " unread=" + item.getUnread());
        if (item.getUnread() > 0) {
            conversationLayout.setBackgroundDrawable(this.activity.getResources().getDrawable(R.drawable.rect_border_notification_new));
        } else {
            conversationLayout.setBackgroundDrawable(this.activity.getResources().getDrawable(R.color.white));
        }

        /*
        conversationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.unread = 0L;   // reset unread count
                ViewUtil.startMessageListActivity(activity, item.id, false);
            }
        });
        */

        /*
        conversationLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                alertDialogBuilder.setMessage(activity.getString(R.string.post_delete_confirm));
                alertDialogBuilder.setPositiveButton(activity.getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteConversation(item.getId());
                    }
                });
                alertDialogBuilder.setNegativeButton(activity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return false;
            }
        });
        */

        unreadCountText.setText(item.getUnread() + "");
        unreadCountText.setVisibility(item.getUnread() > 0 ? View.VISIBLE : View.GONE);

        ImageUtil.displayThumbnailProfileImage(item.getUserId(), userImage);
        if (showPost) {
            postImageLayout.setVisibility(View.VISIBLE);
            ImageUtil.displayPostImage(item.getPostImage(), postImage);
            ViewUtil.setConversationImageTag(view, item);
        } else {
            postImageLayout.setVisibility(View.GONE);
        }

        userText.setText(item.getUserName());
        postTitleText.setText(item.getPostTitle());
        dateText.setText(DateTimeUtil.getTimeAgo(item.getLastMessageDate()));
        ViewUtil.setHtmlText(item.getLastMessage(), lastMessageText, activity);

        hasImageLayout.setVisibility(item.lastMessageHasImage? View.VISIBLE : View.GONE);

        // seller admin

        //Log.d(TAG, "post="+item.getLastMessage()+" owner="+item.postOwner);
        if (item.postOwner && UserInfoCache.getUser().isAdmin()) {
            sellerAdminLayout.setVisibility(View.VISIBLE);

            initColorSpinner(item);
            colorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    //String value = colorSpinner.getSelectedItem().toString();
                    if (colorAdapter.getItem(i) != null) {
                        int value = colorAdapter.getItem(i);
                        Log.d(TAG, "colorSpinner.onItemSelected: state=" + value);
                        ViewUtil.HighlightColor color = ViewUtil.parseHighlightColorFromValue(value);
                        if (color != null) {
                            updateHighlightColor(item, color);
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            initOrderTransactionStateSpinner(item);
            orderTransactionStateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    //String value = orderTransactionStateSpinner.getSelectedItem().toString();
                    if (stateAdapter.getItem(i) != null) {
                        String value = stateAdapter.getItem(i);
                        Log.d(TAG, "orderTransactionStateSpinner.onItemSelected: state=" + value);
                        ViewUtil.ConversationOrderTransactionState state = ViewUtil.parseConversationOrderTransactionStateFromValue(value);
                        if (state != null) {
                            updateOrderTransactionState(item, state);
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            noteText.setText(item.note);
            noteText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initNotePopup(item);
                }
            });
        } else {
            sellerAdminLayout.setVisibility(View.GONE);
        }

        return view;
    }

    private void updateHighlightColor(final ConversationVM conversation, final ViewUtil.HighlightColor color) {
        Log.d(TAG, "updateHighlightColor: conversation="+conversation.id+" to color="+color.name());
        AppController.getApiService().highlightConversation(conversation.id, color.name(), new Callback<Response>() {
            @Override
            public void success(Response responseObj, Response response) {
                conversation.highlightColor = color.name();
                colorAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(activity, "Failed to highlight conversation", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "updateHighlightColor: failure", error);
            }
        });
    }

    private void updateOrderTransactionState(final ConversationVM conversation, final ViewUtil.ConversationOrderTransactionState state) {
        Log.d(TAG, "updateOrderTransactionState: conversation="+conversation.id+" to state="+state.name());
        AppController.getApiService().updateConversationOrderTransactionState(conversation.id, state.name(), new Callback<Response>() {
            @Override
            public void success(Response responseObj, Response response) {
                conversation.orderTransactionState = state.name();
                stateAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(activity, "Failed to update order status", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "updateOrderTransactionState: failure", error);
            }
        });
    }

    private void setColorSpinner(ViewUtil.HighlightColor color) {
        int value = ViewUtil.getHighlightColorValue(color);
        int pos = ((ColorSpinnerAdapter)colorSpinner.getAdapter()).getPosition(value);
        colorSpinner.setSelection(pos);
    }

    private void initColorSpinner(ConversationVM conversation) {
        if (colorAdapter == null) {
            colorAdapter = new ColorSpinnerAdapter(colorSpinner.getContext());
        }

        colorSpinner.setAdapter(colorAdapter);
        colorSpinner.setFocusable(false);
        colorSpinner.setFocusableInTouchMode(false);

        // set previous value
        setColorSpinner(ViewUtil.parseHighlightColor(conversation.highlightColor));
    }

    private void setOrderTransactionStateSpinner(ViewUtil.ConversationOrderTransactionState state) {
        String value = ViewUtil.getConversationOrderTransactionStateValue(state);
        int pos = ((ArrayAdapter)orderTransactionStateSpinner.getAdapter()).getPosition(value);
        orderTransactionStateSpinner.setSelection(pos);
    }

    private void initOrderTransactionStateSpinner(ConversationVM conversation) {
        if (stateAdapter == null) {
            List<String> orderTransactionStates = new ArrayList<>();
            orderTransactionStates.addAll(ViewUtil.getConversationOrderTransactionStateValues());

            stateAdapter = new ArrayAdapter<>(
                    orderTransactionStateSpinner.getContext(),
                    R.layout.spinner_item_admin,
                    orderTransactionStates);
        }

        orderTransactionStateSpinner.setAdapter(stateAdapter);
        orderTransactionStateSpinner.setFocusable(false);
        orderTransactionStateSpinner.setFocusableInTouchMode(false);

        // set previous value
        setOrderTransactionStateSpinner(ViewUtil.parseConversationOrderTransactionState(conversation.orderTransactionState));
    }

    private void initNotePopup(final ConversationVM conversation) {
        //mainLayout.getForeground().setAlpha(20);
        //mainLayout.getForeground().setColorFilter(R.color.light_gray, PorterDuff.Mode.OVERLAY);

        try {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final View layout = inflater.inflate(R.layout.comment_popup_window,
                    (ViewGroup) activity.findViewById(R.id.popupElement));

            if (commentPopup == null) {
                commentPopup = new PopupWindow(
                        layout,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, //activityUtil.getRealDimension(DefaultValues.COMMENT_POPUP_HEIGHT),
                        true);

                commentPopup.setOutsideTouchable(false);
                commentPopup.setFocusable(true);
                commentPopup.setBackgroundDrawable(new BitmapDrawable(activity.getResources(), ""));
                commentPopup.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

                commentPopup.setTouchInterceptor(new View.OnTouchListener() {
                    public boolean onTouch(View view, MotionEvent event) {
                        return false;
                    }
                });

                commentEditText = (EditText) layout.findViewById(R.id.commentEditText);
                commentEditText.setLongClickable(true);

                commentSendButton = (TextView) layout.findViewById(R.id.commentSendButton);
                commentSendButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateNote(conversation);
                    }
                });

                ImageView commentCancelButton = (ImageView) layout.findViewById(R.id.commentCancelButton);
                commentCancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        commentPopup.dismiss();
                        commentPopup = null;
                    }
                });

                ImageView commentBrowseImage = (ImageView) layout.findViewById(R.id.browseImage);
                commentBrowseImage.setVisibility(View.GONE);
            }

            commentEditText.setText(conversation.note);
            commentEditText.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    commentEditText.selectAll();
                    return true;
                }
            });

            commentPopup.showAtLocation(layout, Gravity.BOTTOM, 0, 0);
            ViewUtil.popupInputMethodWindow(activity);
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), "initNotePopup: failure", e);
        }
    }

    private void updateNote(final ConversationVM conversation) {
        if (pending) {
            return;
        }

        final String comment = commentEditText.getText().toString().trim();
        /*
        if (StringUtils.isEmpty(comment)) {
            Toast.makeText(activity, activity.getString(R.string.invalid_comment_body_empty), Toast.LENGTH_SHORT).show();
            return;
        }
        */

        ViewUtil.showSpinner(activity);

        pending = true;
        final NewMessageVM newMessage = new NewMessageVM(conversation.id, comment);
        AppController.getApiService().updateConversationNote(newMessage, new Callback<Response>() {
            @Override
            public void success(Response responseObj, Response response) {
                conversation.note = comment;
                noteText.setText(comment);
                notifyDataSetChanged();

                //Toast.makeText(activity, activity.getString(R.string.comment_success), Toast.LENGTH_SHORT).show();
                reset();
                pending = false;
                ViewUtil.stopSpinner(activity);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "updateNote.api.updateConversationNote: failed with error", error);
                Toast.makeText(activity, activity.getString(R.string.comment_failed), Toast.LENGTH_SHORT).show();
                reset();
                pending = false;
                ViewUtil.stopSpinner(activity);
            }
        });
    }

    private void reset() {
        if (commentPopup != null) {
            commentEditText.setText("");
            commentPopup.dismiss();
            commentPopup = null;
        }
    }

    public void deleteConversation(final Long id) {
        ViewUtil.showSpinner(activity);
        ConversationCache.delete(id, new Callback<Response>() {
            @Override
            public void success(Response responseObject, Response response) {
                notifyDataSetChanged();
                ViewUtil.stopSpinner(activity);
            }

            @Override
            public void failure(RetrofitError error) {
                ViewUtil.stopSpinner(activity);
                Log.e(TAG, "deleteConversation: failure", error);
            }
        });
    }

    public void toggleSelection(int position) {
        selectView(position, !selectedItemsIds.get(position));
    }

    public void removeSelection() {
        selectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value) {
        if (value) {
            selectedItemsIds.put(position, value);
        } else {
            selectedItemsIds.delete(position);
        }
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return selectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return selectedItemsIds;
    }
}
