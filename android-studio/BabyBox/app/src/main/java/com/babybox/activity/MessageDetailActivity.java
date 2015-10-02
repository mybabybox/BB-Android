package com.babybox.activity;

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.babybox.R;
import com.babybox.adapter.MessageListAdapter;
import com.babybox.app.AppController;
import com.babybox.app.BroadcastService;
import com.babybox.app.TrackedFragmentActivity;
import com.babybox.util.DefaultValues;
import com.babybox.util.ImageUtil;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.NewMessageVM;
import com.babybox.viewmodel.MessageVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedInput;

public class MessageDetailActivity extends TrackedFragmentActivity {

    private TextView commentEdit;
    private FrameLayout mainFrameLayout;
    private EditText commentEditText;
    private PopupWindow commentPopup;
    private String selectedImagePath = null;
    private Uri selectedImageUri = null;

    private List<ImageView> commentImages = new ArrayList<>();
    private List<File> photos = new ArrayList<>();

    private TextView title;
    private ImageView backImage, profileButton;
    private List<MessageVM> messageVMList;
    private MessageListAdapter adapter;
    private ListView listView;
    private View listHeader;
    private RelativeLayout loadMoreLayout;

    private Long conversationId;
    private Long receiverId;
    private String receiverName;
    private Long offset = 1L;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("in receiver:::");
            messageVMList.clear();
            messageVMList.addAll(AppController.getInstance().messageVMList);
            System.out.println("in rec size::"+messageVMList.size());
            adapter.notifyDataSetChanged();
            // adapter = new MessageListAdapter(MessageDetailActivity.this, messageVMList);
            //listView.setAdapter(adapter);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.message_detail_activity);

        LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
        listHeader = layoutInflater.inflate(R.layout.message_detail_list_header, null);
        loadMoreLayout = (RelativeLayout) listHeader.findViewById(R.id.loadMoreLayout);

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(getLayoutInflater().inflate(R.layout.message_detail_actionbar, null),
                new ActionBar.LayoutParams(
                        ActionBar.LayoutParams.MATCH_PARENT,
                        ActionBar.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER
                )
        );

        commentEdit = (TextView) findViewById(R.id.commentEdit);
        mainFrameLayout = (FrameLayout) findViewById(R.id.mainFrameLayout);
        profileButton = (ImageView) findViewById(R.id.profileButton);

        Intent intent = new Intent(this, BroadcastService.class);

        listView = (ListView)findViewById(R.id.messageList);
        title = (TextView) findViewById(R.id.titleText);

        listView.addHeaderView(listHeader);
        listHeader.setVisibility(View.INVISIBLE);

        messageVMList = new ArrayList<>();

        conversationId = getIntent().getLongExtra(ViewUtil.BUNDLE_KEY_ID, 0l);
        receiverId = getIntent().getLongExtra(ViewUtil.BUNDLE_KEY_USER_ID, 0l);
        receiverName = getIntent().getStringExtra(ViewUtil.BUNDLE_KEY_USER_NAME);

        title.setText(receiverName);

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewUtil.startUserProfileActivity(MessageDetailActivity.this, receiverId, "");
            }
        });

        getMessages(conversationId, 0l);

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
        mainFrameLayout.getForeground().setAlpha(20);
        mainFrameLayout.getForeground().setColorFilter(R.color.gray, PorterDuff.Mode.OVERLAY);

        try {
            LayoutInflater inflater = (LayoutInflater) MessageDetailActivity.this
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
                        Log.d(MessageDetailActivity.this.getClass().getSimpleName(), "onLongClick");
                        startActionMode(new ActionMode.Callback() {
                            final int PASTE_MENU_ITEM_ID = 0;

                            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                                Log.d(MessageDetailActivity.this.getClass().getSimpleName(), "onPrepareActionMode");
                                return true;
                            }

                            public void onDestroyActionMode(ActionMode mode) {
                            }

                            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                                Log.d(MessageDetailActivity.this.getClass().getSimpleName(), "onCreateActionMode: menu size=" + menu.size());
                                menu.add(0, PASTE_MENU_ITEM_ID, 0, "Paste");
                                menu.setQwertyMode(false);
                                return true;
                            }

                            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                                Log.d(MessageDetailActivity.this.getClass().getSimpleName(), "onActionItemClicked: item clicked=" + item.getItemId() + " title=" + item.getTitle());
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
                                ViewUtil.popupInputMethodWindow(MessageDetailActivity.this);
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

                TextView commentSendButton = (TextView) layout.findViewById(R.id.commentSendButton);
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
                        if (photos.size() == DefaultValues.MAX_MESSAGE_IMAGES) {
                            Toast.makeText(MessageDetailActivity.this, MessageDetailActivity.this.getString(R.string.pm_max_images), Toast.LENGTH_SHORT).show();
                        } else {
                            ImageUtil.openPhotoPicker(MessageDetailActivity.this);
                        }
                    }
                });

                if (commentImages.size() == 0) {
                    commentImages.add((ImageView) layout.findViewById(R.id.commentImage));

                    for (ImageView commentImage : commentImages) {
                        commentImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                removeCommentImage();
                            }
                        });
                    }
                }
            }

            commentPopup.showAtLocation(layout, Gravity.BOTTOM, 0, 0);
            ViewUtil.popupInputMethodWindow(this);
        } catch (Exception e) {
            Log.e(MessageDetailActivity.class.getSimpleName(), "initCommentPopup: exception", e);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ViewUtil.SELECT_IMAGE_REQUEST_CODE && resultCode == RESULT_OK &&
                data != null && photos.size() < DefaultValues.MAX_MESSAGE_IMAGES) {

            selectedImageUri = data.getData();
            selectedImagePath = ImageUtil.getRealPathFromUri(this, selectedImageUri);

            String path = selectedImageUri.getPath();
            Log.d(this.getClass().getSimpleName(), "onActivityResult: selectedImageUri=" + path + " selectedImagePath=" + selectedImagePath);

            Bitmap bitmap = ImageUtil.resizeAsPreviewThumbnail(selectedImagePath);
            if (bitmap != null) {
                setCommentImage(bitmap);
            } else {
                Toast.makeText(MessageDetailActivity.this, MessageDetailActivity.this.getString(R.string.photo_size_too_big), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MessageDetailActivity.this, MessageDetailActivity.this.getString(R.string.photo_not_found), Toast.LENGTH_SHORT).show();
        }

        // pop back soft keyboard
        ViewUtil.popupInputMethodWindow(this);
    }

    private void resetCommentImages() {
        commentImages = new ArrayList<>();
        photos = new ArrayList<>();
    }

    private void setCommentImage(Bitmap bitmap) {
        ImageView commentImage = commentImages.get(photos.size());
        commentImage.setImageDrawable(new BitmapDrawable(this.getResources(), bitmap));
        commentImage.setVisibility(View.VISIBLE);
        File photo = new File(selectedImagePath);
        photos.add(photo);
    }

    private void removeCommentImage() {
        if (photos.size() > 0) {
            int toRemove = photos.size()-1;
            commentImages.get(toRemove).setImageDrawable(null);
            photos.remove(toRemove);
        }
    }

    private void doMessage() {
        String comment = commentEditText.getText().toString().trim();
        if (StringUtils.isEmpty(comment) && commentImages.size() == 0) {
            Toast.makeText(MessageDetailActivity.this, MessageDetailActivity.this.getString(R.string.invalid_comment_body_empty), Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(this.getClass().getSimpleName(), "doMessage: receiverId=" + receiverId + " message=" + comment.substring(0, Math.min(5, comment.length())));

        final boolean withPhotos = photos.size() > 0;
        NewMessageVM newMessage = new NewMessageVM(conversationId, receiverId, comment, withPhotos);
        AppController.getMockApi().sendMessage(newMessage, AppController.getInstance().getSessionId(), new Callback<Response>() {
            @Override
            public void success(Response responseMap, Response response) {
                String responseVM = "";
                TypedInput body = responseMap.getBody();
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(body.in()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        responseVM = responseVM + line;
                    }

                    if (withPhotos) {
                        JSONObject obj = new JSONObject(responseVM);
                        Long messageId = obj.getLong("id");
                        uploadPhotos(messageId);
                    }

                    getMessages(conversationId, 0l);

                    reset();
                } catch (JSONException e) {
                    Log.e(MessageDetailActivity.class.getSimpleName(), "doMessage.api.sendMessage: exception", e);
                } catch (IOException e) {
                    Log.e(MessageDetailActivity.class.getSimpleName(), "doMessage.api.sendMessage: exception", e);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(MessageDetailActivity.this.getClass().getSimpleName(), "doMessage.api.sendMessage: failed with error", error);
                Toast.makeText(MessageDetailActivity.this, MessageDetailActivity.this.getString(R.string.pm_send_failed), Toast.LENGTH_SHORT).show();
                reset();
            }
        });
    }

    private void uploadPhotos(long messageId) {
        for (File photo : photos) {
            photo = ImageUtil.resizeAsJPG(photo);   // IMPORTANT: resize before upload
            TypedFile typedFile = new TypedFile("application/octet-stream", photo);
            AppController.getMockApi().uploadMessagePhoto(AppController.getInstance().getSessionId(), messageId, typedFile, new Callback<Response>() {
                @Override
                public void success(Response array, Response response) {
                    getMessages(conversationId, 0l);
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(MessageDetailActivity.class.getSimpleName(), "uploadPhotos.api.uploadMessagePhoto: failure", error);
                }
            });
        }
    }

    private void getMessages(Long id, Long offset) {
        ViewUtil.showSpinner(MessageDetailActivity.this);
        AppController.getMockApi().getMessages(id, offset, AppController.getInstance().getSessionId(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response1) {
                String responseVm = "";
                TypedInput body = response.getBody();
                messageVMList.clear();
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(body.in()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        responseVm = responseVm + line;
                    }

                    JSONObject obj = new JSONObject(responseVm);

                    JSONArray userGroupArray = obj.getJSONArray("message");
                    for (int i = 0; i < userGroupArray.length(); i++) {
                        JSONObject object1 = userGroupArray.getJSONObject(i);
                        MessageVM vm = new MessageVM();
                        vm.setId(object1.getLong(ViewUtil.BUNDLE_KEY_ID));
                        vm.setHasImage(object1.getBoolean("hasImage"));
                        vm.setSnm(object1.getString("snm"));
                        vm.setSuid(object1.getLong("suid"));
                        vm.setCd(object1.getLong("cd"));
                        vm.setTxt(object1.getString("txt"));

                        if (!object1.isNull("imgs")) {
                            System.out.println("fill image:::" + object1.getLong("imgs"));
                            vm.setImgs(object1.getLong("imgs"));
                        }
                        messageVMList.add(vm);

                        if (messageVMList.size() >= DefaultValues.CONVERSATION_MESSAGE_COUNT) {
                            listHeader.setVisibility(View.VISIBLE);
                        }
                    }

                    Collections.sort(messageVMList, new Comparator<MessageVM>() {
                        public int compare(MessageVM m1, MessageVM m2) {
                            //return Long.compare(m1.getCd(), m2.getCd());
                            return ((Long)m1.getCd()).compareTo(m2.getCd());
                        }
                    });

                    adapter = new MessageListAdapter(MessageDetailActivity.this, messageVMList);
                    listView.setAdapter(adapter);
                } catch (IOException e) {
                    Log.e(MessageDetailActivity.class.getSimpleName(), "getMessages.api.getMessages: exception", e);
                } catch (JSONException e) {
                    Log.e(MessageDetailActivity.class.getSimpleName(), "getMessages.api.getMessages: exception", e);
                }

                ViewUtil.stopSpinner(MessageDetailActivity.this);
            }

            @Override
            public void failure(RetrofitError error) {
                ViewUtil.stopSpinner(MessageDetailActivity.this);
                Log.e(MessageDetailActivity.class.getSimpleName(), "getMessages.api.getMessages: failure", error);
            }
        });
    }

    private void loadMoreMessages(Long id,Long offset) {
        ViewUtil.showSpinner(MessageDetailActivity.this);
        AppController.getMockApi().getMessages(id, offset, AppController.getInstance().getSessionId(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response1) {
                listHeader.setVisibility(View.INVISIBLE);
                String responseVm = "";
                TypedInput body = response.getBody();
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(body.in()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        responseVm = responseVm + line;
                    }

                    JSONObject obj = new JSONObject(responseVm);

                    JSONArray userGroupArray = obj.getJSONArray("message");
                    for (int i = 0; i < userGroupArray.length(); i++) {
                        JSONObject object1 = userGroupArray.getJSONObject(i);
                        MessageVM vm = new MessageVM();
                        vm.setId(object1.getLong(ViewUtil.BUNDLE_KEY_ID));
                        vm.setHasImage(object1.getBoolean("hasImage"));
                        vm.setSnm(object1.getString("snm"));
                        vm.setSuid(object1.getLong("suid"));
                        vm.setCd(object1.getLong("cd"));
                        vm.setTxt(object1.getString("txt"));

                        if(!object1.isNull("imgs")) {
                            vm.setImgs(object1.getLong("imgs"));
                        }
                        messageVMList.add(vm);

                        if (userGroupArray.length() == DefaultValues.CONVERSATION_MESSAGE_COUNT) {
                            listHeader.setVisibility(View.VISIBLE);
                        }
                    }

                    Collections.sort(messageVMList, new Comparator<MessageVM>() {
                        public int compare(MessageVM m1, MessageVM m2) {
                            //return Long.compare(m1.getCd(), m2.getCd());
                            return ((Long) m1.getCd()).compareTo(m2.getCd());
                        }
                    });

                    adapter.notifyDataSetChanged();

                    // restore previous listView position
                    View childView = listView.getChildAt(0);
                    int top = (childView == null)? 0 : (childView.getTop() - listView.getPaddingTop());
                    listView.setSelectionFromTop(userGroupArray.length(), top);
                } catch (IOException e) {
                    Log.e(MessageDetailActivity.class.getSimpleName(), "loadMoreMessages.api.getMessages: exception", e);
                } catch (JSONException e) {
                    Log.e(MessageDetailActivity.class.getSimpleName(), "loadMoreMessages.api.getMessages: exception", e);
                }

                ViewUtil.stopSpinner(MessageDetailActivity.this);
            }

            @Override
            public void failure(RetrofitError error) {
                ViewUtil.stopSpinner(MessageDetailActivity.this);
                Log.e(MessageDetailActivity.class.getSimpleName(), "loadMoreMessages.api.getMessages: failure", error);
            }
        });
    }

    private void reset() {
        if (commentPopup != null) {
            commentPopup.dismiss();
            commentPopup = null;
        }
        resetCommentImages();
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
}






