package com.babybox.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.babybox.R;
import com.babybox.adapter.MessageListAdapter;
import com.babybox.app.AppController;
import com.babybox.app.ConversationCache;
import com.babybox.app.TrackedFragmentActivity;
import com.babybox.util.DefaultValues;
import com.babybox.util.ImageUtil;
import com.babybox.util.SelectedImage;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.ConversationVM;
import com.babybox.viewmodel.MessageVM;
import com.babybox.viewmodel.NewMessageVM;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MessageListActivity extends TrackedFragmentActivity {

    private TextView postTitleText, postPriceText, commentEdit;
    private FrameLayout mainFrameLayout;
    private EditText commentEditText;
    private PopupWindow commentPopup;

    private TextView titleText;
    private ImageView backImage, profileButton, postImage;

    private ListView listView;
    private View listHeader;
    private RelativeLayout postLayout, loadMoreLayout;

    private TextView commentSendButton;

    private List<MessageVM> messages = new ArrayList<>();
    private MessageListAdapter adapter;

    private List<ImageView> commentImages = new ArrayList<>();

    private Uri selectedImageUri = null;
    private List<SelectedImage> selectedImages = new ArrayList<>();

    private Long conversationId;
    private Long offset = 1L;

    private boolean pending = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.message_list_activity);

        LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
        listHeader = layoutInflater.inflate(R.layout.message_list_header, null);
        loadMoreLayout = (RelativeLayout) listHeader.findViewById(R.id.loadMoreLayout);

        listView = (ListView)findViewById(R.id.messageList);

        postLayout = (RelativeLayout) findViewById(R.id.postLayout);
        postImage = (ImageView) findViewById(R.id.postImage);
        postTitleText = (TextView) findViewById(R.id.postTitleText);
        postPriceText = (TextView) findViewById(R.id.postPriceText);

        mainFrameLayout = (FrameLayout) findViewById(R.id.mainFrameLayout);
        profileButton = (ImageView) findViewById(R.id.profileButton);
        commentEdit = (TextView) findViewById(R.id.commentEdit);

        // conversation
        conversationId = getIntent().getLongExtra(ViewUtil.BUNDLE_KEY_ID, 0L);
        final ConversationVM conversation = ConversationCache.getOpenedConversation(conversationId);

        setActionBarTitle(conversation.getUserName());
        postTitleText.setText(conversation.getPostTitle());
        postPriceText.setText(ViewUtil.priceFormat(conversation.getPostPrice()));
        ImageUtil.displayPostImage(conversation.getPostImage(), postImage);

        listView.addHeaderView(listHeader);
        listHeader.setVisibility(View.INVISIBLE);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final MessageVM item = adapter.getItem(i);
                if (ViewUtil.copyToClipboard(item.getBody())) {
                    Toast.makeText(MessageListActivity.this, MessageListActivity.this.getString(R.string.text_copy_success), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MessageListActivity.this, MessageListActivity.this.getString(R.string.text_copy_failed), Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        // load messages
        loadMessages(conversationId);

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewUtil.startUserProfileActivity(MessageListActivity.this, conversation.getUserId());
            }
        });

        postLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewUtil.startProductActivity(MessageListActivity.this, conversation.getPostId());
            }
        });

        loadMoreLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadMoreMessages(conversationId, offset);
                offset++;
            }
        });

        commentEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initCommentPopup();
            }
        });

        backImage = (ImageView) this.findViewById(R.id.backImage);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initCommentPopup() {
        //mainFrameLayout.getForeground().setAlpha(20);
        //mainFrameLayout.getForeground().setColorFilter(R.color.gray, PorterDuff.Mode.OVERLAY);

        try {
            LayoutInflater inflater = (LayoutInflater) MessageListActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final View layout = inflater.inflate(R.layout.comment_popup_window,
                    (ViewGroup) findViewById(R.id.popupElement));

            if (commentPopup == null) {
                commentPopup = new PopupWindow(
                        layout,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, //activityUtil.getRealDimension(DefaultValues.COMMENT_POPUP_HEIGHT),
                        true);

                commentPopup.setOutsideTouchable(false);
                commentPopup.setFocusable(true);
                commentPopup.setBackgroundDrawable(new BitmapDrawable(getResources(), ""));
                commentPopup.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

                commentPopup.setTouchInterceptor(new View.OnTouchListener() {
                    public boolean onTouch(View view, MotionEvent event) {
                        return false;
                    }
                });

                commentEditText = (EditText) layout.findViewById(R.id.commentEditText);
                commentEditText.setLongClickable(true);

                // NOTE: UGLY WORKAROUND or pasting text to comment edit!!!
                commentEditText.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Log.d(MessageListActivity.this.getClass().getSimpleName(), "onLongClick");
                        startActionMode(new ActionMode.Callback() {
                            final int PASTE_MENU_ITEM_ID = 0;

                            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                                Log.d(MessageListActivity.this.getClass().getSimpleName(), "onPrepareActionMode");
                                return true;
                            }

                            public void onDestroyActionMode(ActionMode mode) {
                            }

                            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                                Log.d(MessageListActivity.this.getClass().getSimpleName(), "onCreateActionMode: menu size=" + menu.size());
                                menu.add(0, PASTE_MENU_ITEM_ID, 0, "Paste");
                                menu.setQwertyMode(false);
                                return true;
                            }

                            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                                Log.d(MessageListActivity.this.getClass().getSimpleName(), "onActionItemClicked: item clicked=" + item.getItemId() + " title=" + item.getTitle());
                                switch (item.getItemId()) {
                                    case PASTE_MENU_ITEM_ID:
                                        final ClipboardManager clipBoard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                        if (clipBoard != null && clipBoard.getPrimaryClip() != null && clipBoard.getPrimaryClip().getItemAt(0) != null) {
                                            String paste = clipBoard.getPrimaryClip().getItemAt(0).getText().toString();
                                            commentEditText.getText().insert(commentEditText.getSelectionStart(), paste);
                                        }
                                }

                                mode.finish();

                                // popup again
                                commentPopup.showAtLocation(layout, Gravity.BOTTOM, 0, 0);
                                ViewUtil.popupInputMethodWindow(MessageListActivity.this);
                                return true;
                            }
                        });
                        return true;
                    }
                });

                /*
                commentEditText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        Log.d(DetailActivity.this.getClass().getSimpleName(), "onPrepareActionMode");
                        return false;
                    }

                    public void onDestroyActionMode(ActionMode mode) {
                    }

                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        Log.d(DetailActivity.this.getClass().getSimpleName(), "onCreateActionMode");
                        return false;
                    }

                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        Log.d(DetailActivity.this.getClass().getSimpleName(), "onActionItemClicked");
                        return false;
                    }
                });
                */

                commentSendButton = (TextView) layout.findViewById(R.id.commentSendButton);
                commentSendButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doMessage();
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

                ImageView commentBrowseButton = (ImageView) layout.findViewById(R.id.browseImage);
                commentBrowseButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImageUtil.openPhotoPicker(MessageListActivity.this);
                    }
                });

                if (commentImages.size() == 0) {
                    commentImages.add((ImageView) layout.findViewById(R.id.commentImage));

                    for (ImageView commentImage : commentImages) {
                        commentImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                removeSelectedCommentImage();
                            }
                        });
                    }
                }
            }

            commentPopup.showAtLocation(layout, Gravity.BOTTOM, 0, 0);
            ViewUtil.popupInputMethodWindow(this);
        } catch (Exception e) {
            Log.e(MessageListActivity.class.getSimpleName(), "initCommentPopup: exception", e);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (selectedImages.size() >= DefaultValues.MAX_MESSAGE_IMAGES) {
                //Toast.makeText(MessageListActivity.this,
                //        String.format(MessageListActivity.this.getString(R.string.pm_max_images), DefaultValues.MAX_MESSAGE_IMAGES), Toast.LENGTH_SHORT).show();
                //return;

                selectedImages.clear();
            }

            if ( (requestCode == ViewUtil.SELECT_GALLERY_IMAGE_REQUEST_CODE  && data != null) ||
                    requestCode == ViewUtil.SELECT_CAMERA_IMAGE_REQUEST_CODE )  {

                String imagePath = "";
                if (requestCode == ViewUtil.SELECT_GALLERY_IMAGE_REQUEST_CODE  && data != null) {
                    selectedImageUri = data.getData();
                    imagePath = ImageUtil.getRealPathFromUri(this, selectedImageUri);
                } else if (requestCode == ViewUtil.SELECT_CAMERA_IMAGE_REQUEST_CODE) {
                    File picture = new File(ImageUtil.CAMERA_IMAGE_TEMP_PATH);
                    selectedImageUri = Uri.fromFile(picture);
                    imagePath = picture.getPath();
                }

                Log.d(this.getClass().getSimpleName(), "onActivityResult: imagePath=" + imagePath);

                Bitmap bitmap = ImageUtil.resizeToUpload(imagePath);
                if (bitmap != null) {
                    ViewUtil.startSelectImageActivity(this, selectedImageUri);
                } else {
                    Toast.makeText(this, getString(R.string.photo_size_too_big), Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == ViewUtil.CROP_IMAGE_REQUEST_CODE) {
                String croppedImagePath = data.getStringExtra(ViewUtil.INTENT_RESULT_OBJECT);
                setCommentImage(croppedImagePath);
            }

            // pop back soft keyboard
            ViewUtil.popupInputMethodWindow(this);
        }
    }

    private void setCommentImage(String imagePath) {
        if (!StringUtils.isEmpty(imagePath)) {
            selectedImages.add(new SelectedImage(0, imagePath));

            ImageView imageView = commentImages.get(0);
            Bitmap bp = ImageUtil.resizeAsPreviewThumbnail(imagePath);
            imageView.setImageBitmap(bp);
            //imageView.setImageURI(Uri.parse(imagePath);

            Log.d(this.getClass().getSimpleName(), "setCommentImage: imagePath="+imagePath);
        }
    }

    private void removeSelectedCommentImage() {
        SelectedImage toRemove = getSelectedCommentImage(0);
        selectedImages.remove(toRemove);
        commentImages.get(0).setImageDrawable(null);
    }

    private SelectedImage getSelectedCommentImage(int index) {
        for (SelectedImage image : selectedImages) {
            if (image.index.equals(index)) {
                return image;
            }
        }
        return null;
    }

    private void doBuy() {
        initCommentPopup();
        if (commentEditText != null) {
            commentEditText.setText(getString(R.string.pm_buy_message));
            doMessage();
        }
    }

    private void doMessage() {
        if (pending) {
            return;
        }

        final boolean withPhotos = selectedImages.size() > 0;
        String body = commentEditText.getText().toString().trim();
        if (StringUtils.isEmpty(body) && !withPhotos) {
            Toast.makeText(MessageListActivity.this, MessageListActivity.this.getString(R.string.invalid_comment_body_empty), Toast.LENGTH_SHORT).show();
            return;
        }

        //Log.d(this.getClass().getSimpleName(), "doMessage: message=" + comment.substring(0, Math.min(5, comment.length())));

        ViewUtil.showSpinner(MessageListActivity.this);

        pending = true;
        NewMessageVM newMessage = new NewMessageVM(conversationId, body, selectedImages);
        AppController.getApiService().newMessage(newMessage, new Callback<MessageVM>() {
            @Override
            public void success(MessageVM message, Response response) {
                messages.add(message);
                adapter.notifyDataSetChanged();
                listView.smoothScrollToPosition(messages.size());

                ViewUtil.setActivityResult(MessageListActivity.this, conversationId);
                reset();
                pending = false;
                ViewUtil.stopSpinner(MessageListActivity.this);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(MessageListActivity.this.getClass().getSimpleName(), "doMessage.api.newMessage: failed with error", error);
                Toast.makeText(MessageListActivity.this, MessageListActivity.this.getString(R.string.pm_send_failed), Toast.LENGTH_SHORT).show();
                reset();
                pending = false;
                ViewUtil.stopSpinner(MessageListActivity.this);
            }
        });
    }

    private int parseAndAddMessages(String responseBody) {
        int count = 0;
        try {
            JSONObject obj = new JSONObject(responseBody);

            JSONArray messagesObj = obj.getJSONArray(ViewUtil.JSON_KEY_MESSAGE_KEY);
            count = messagesObj.length();
            for (int i = 0; i < count; i++) {
                JSONObject messageObj = messagesObj.getJSONObject(i);
                Log.d(MessageListActivity.class.getSimpleName(), "getMessages.api.getMessages: message["+i+"]="+messageObj.toString());
                MessageVM vm = new MessageVM(messageObj);
                messages.add(vm);
            }

            Collections.sort(messages, new Comparator<MessageVM>() {
                public int compare(MessageVM m1, MessageVM m2) {
                    return m1.getCreatedDate().compareTo(m2.getCreatedDate());
                }
            });
        } catch (JSONException e) {
            Log.e(MessageListActivity.class.getSimpleName(), "getMessages.api.getMessages: exception", e);
        }
        return count;
    }

    private void loadMessages(Long conversationId) {
        ViewUtil.showSpinner(MessageListActivity.this);
        AppController.getApiService().getMessages(conversationId, 0L, new Callback<Response>() {
            @Override
            public void success(Response responseObject, Response response) {
                listHeader.setVisibility(View.INVISIBLE);

                messages.clear();

                String responseBody = ViewUtil.getResponseBody(responseObject);
                int count = parseAndAddMessages(responseBody);
                if (count >= DefaultValues.CONVERSATION_MESSAGE_COUNT) {
                    listHeader.setVisibility(View.VISIBLE);
                }

                adapter = new MessageListAdapter(MessageListActivity.this, messages, true);
                listView.setAdapter(adapter);

                // send a buy message to seller
                boolean buy = getIntent().getBooleanExtra(ViewUtil.BUNDLE_KEY_ARG1, false);
                if (buy) {
                    doBuy();
                }

                ViewUtil.stopSpinner(MessageListActivity.this);
            }

            @Override
            public void failure(RetrofitError error) {
                ViewUtil.stopSpinner(MessageListActivity.this);
                Log.e(MessageListActivity.class.getSimpleName(), "getMessages.api.getMessages: failure", error);
            }
        });
    }

    private void loadMoreMessages(Long id, Long offset) {
        ViewUtil.showSpinner(MessageListActivity.this);
        AppController.getApiService().getMessages(id, offset, new Callback<Response>() {
            @Override
            public void success(Response responseObject, Response response) {
                listHeader.setVisibility(View.INVISIBLE);

                String responseBody = ViewUtil.getResponseBody(responseObject);
                int count = parseAndAddMessages(responseBody);
                if (count >= DefaultValues.CONVERSATION_MESSAGE_COUNT) {
                    listHeader.setVisibility(View.VISIBLE);
                }

                adapter.notifyDataSetChanged();

                // restore previous listView position
                View childView = listView.getChildAt(0);
                int top = (childView == null)? 0 : (childView.getTop() - listView.getPaddingTop());
                listView.setSelectionFromTop(count, top);

                ViewUtil.stopSpinner(MessageListActivity.this);
            }

            @Override
            public void failure(RetrofitError error) {
                ViewUtil.stopSpinner(MessageListActivity.this);
                Log.e(MessageListActivity.class.getSimpleName(), "loadMoreMessages.api.getMessages: failure", error);
            }
        });
    }

    private void reset() {
        selectedImages.clear();

        if (commentPopup != null) {
            commentEditText.setText("");
            commentPopup.dismiss();
            commentPopup = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        //startService(intent);
        //registerReceiver(broadcastReceiver, new IntentFilter(BroadcastService.BROADCAST_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();

        //unregisterReceiver(broadcastReceiver);
        //stopService(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        reset();
    }
}