package com.babybox.activity;

import android.app.ActionBar;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.babybox.R;
import com.babybox.adapter.EmoticonListAdapter;
import com.babybox.app.AppController;
import com.babybox.app.EmoticonCache;
import com.babybox.app.TrackedFragmentActivity;
import com.babybox.util.DateTimeUtil;
import com.babybox.util.DefaultValues;
import com.babybox.util.ImageMapping;
import com.babybox.util.ImageUtil;
import com.babybox.util.SharingUtil;
import com.babybox.util.UrlUtil;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.CommentPost;
import com.babybox.viewmodel.CommentResponse;
import com.babybox.viewmodel.EmoticonVM;
import com.babybox.viewmodel.PostVM;

import org.parceler.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ProductActivity extends TrackedFragmentActivity {

    private FrameLayout mainLayout;
    private ImageView backImage, whatsappAction, copyLinkAction;

    private ImageView productImage;
    private TextView titleText, descText, priceText;
    private Button chatButton, buyButton, sendButton;
    private LinearLayout likeLayout;
    private ImageView likeImage;
    private TextView likeText;

    private PopupWindow commentPopup, emoPopup;
    private TextView commentText, catNameText, timeText, numViewsText, numCommentsText;

    private EditText commentEditText;
    private ImageView commentCancelButton, commentEmoImage;
    private Button commentSendButton;

    private PostVM post;
    private long postId, catId;
    private Boolean isLiked = false;

    private List<EmoticonVM> emoticonVMList = new ArrayList<>();
    private EmoticonListAdapter emoticonListAdapter;

    private void setPost(PostVM post) {
        this.post = post;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.product_activity);

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(getLayoutInflater().inflate(R.layout.product_actionbar, null),
                new ActionBar.LayoutParams(
                        ActionBar.LayoutParams.MATCH_PARENT,
                        ActionBar.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER
                )
        );

        mainLayout = (FrameLayout) findViewById(R.id.mainLayout);
        backImage = (ImageView) findViewById(R.id.backImage);
        whatsappAction = (ImageView) findViewById(R.id.whatsappAction);
        copyLinkAction = (ImageView) findViewById(R.id.copyLinkAction);

        productImage = (ImageView) findViewById(R.id.productImage);
        titleText = (TextView) findViewById(R.id.titleText);
        descText = (TextView) findViewById(R.id.descText);
        priceText = (TextView) findViewById(R.id.priceText);

        chatButton = (Button) findViewById(R.id.chatButton);
        buyButton = (Button) findViewById(R.id.buyButton);
        sendButton = (Button) findViewById(R.id.sendButton);
        likeLayout = (LinearLayout) findViewById(R.id.likeLayout);
        likeImage = (ImageView) findViewById(R.id.likeImage);
        likeText = (TextView) findViewById(R.id.likeText);

        commentText = (TextView) findViewById(R.id.commentText);
        catNameText = (TextView) findViewById(R.id.catNameText);
        timeText = (TextView) findViewById(R.id.timeText);
        numViewsText = (TextView) findViewById(R.id.numViewsText);
        numCommentsText = (TextView) findViewById(R.id.numCommentsText);

        commentText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initCommentPopup();
            }
        });

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //getActionBar().setDisplayHomeAsUpEnabled(true);
        //getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        //getActionBar().setTitle("Details");

        postId = getIntent().getLongExtra(ViewUtil.BUNDLE_KEY_POST_ID, 0L);
        catId = getIntent().getLongExtra(ViewUtil.BUNDLE_KEY_CATEGORY_ID, 0L);

        getProduct();
    }

    private void getProduct() {
        ViewUtil.showSpinner(this);

        AppController.getApiService().getPost(postId, new Callback<PostVM>() {
            @Override
            public void success(final PostVM post, Response response) {
                setPost(post);

                // Show images in slider
                if (post.hasImage) {
                    ImageUtil.displayOriginalPostImage(post.images[0], productImage);
                } else {
                    Log.w(ProductActivity.class.getSimpleName(), "getProduct: postId="+post.id+" has no image!!");
                }

                catNameText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ProductActivity.this, CategoryActivity.class);
                        intent.putExtra(ViewUtil.BUNDLE_KEY_ID, getIntent().getLongExtra(ViewUtil.BUNDLE_KEY_CATEGORY_ID, 0L));
                        intent.putExtra(ViewUtil.BUNDLE_KEY_SOURCE, "FromProductActivity");
                        startActivity(intent);
                    }
                });

                catNameText.setText(post.getCategoryName());
                descText.setText(post.getDesc());
                priceText.setText(ViewUtil.priceFormat(post.getPrice()));
                timeText.setText(DateTimeUtil.getTimeAgo(post.getCreatedDate()));
                numViewsText.setText(post.getNumViews() + "");
                numCommentsText.setText(post.getNumComments() + " " + getString(R.string.comments));

                ViewUtil.setHtmlText(post.getTitle(), titleText, ProductActivity.this, true);

                isLiked = post.isLiked();
                if (isLiked) {
                    ViewUtil.selectLikeButtonStyle(likeImage, likeText, post.getNumLikes());
                } else {
                    ViewUtil.unselectLikeButtonStyle(likeImage, likeText, post.getNumLikes());
                }

                likeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isLiked) {
                            unlike(postId);
                        } else {
                            like(postId);
                        }
                    }
                });

                // actionbar actions...
                whatsappAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharingUtil.shareToWhatapp(post, ProductActivity.this);
                    }
                });

                copyLinkAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ViewUtil.copyToClipboard(UrlUtil.createPostUrl(post))) {
                            Toast.makeText(ProductActivity.this, ProductActivity.this.getString(R.string.url_copy_success), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ProductActivity.this, ProductActivity.this.getString(R.string.url_copy_failed), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                ViewUtil.stopSpinner(ProductActivity.this);
            }

            @Override
            public void failure(RetrofitError error) {
                if (RetrofitError.Kind.NETWORK.equals(error.getKind()) ||
                        RetrofitError.Kind.HTTP.equals(error.getKind())) {
                    Toast.makeText(ProductActivity.this, ProductActivity.this.getString(R.string.post_not_found), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProductActivity.this, ProductActivity.this.getString(R.string.connection_timeout_message), Toast.LENGTH_SHORT).show();
                }

                ViewUtil.stopSpinner(ProductActivity.this);

                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        finish();
                    }
                }, DefaultValues.DEFAULT_HANDLER_DELAY);

                Log.e(ProductActivity.class.getSimpleName(), "getQnaDetail: failure", error);
            }
        });
    }

    private void initCommentPopup() {
        mainLayout.getForeground().setAlpha(20);
        mainLayout.getForeground().setColorFilter(R.color.light_gray, PorterDuff.Mode.OVERLAY);

        try {
            LayoutInflater inflater = (LayoutInflater) ProductActivity.this
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
                        Log.d(ProductActivity.this.getClass().getSimpleName(), "onLongClick");
                        startActionMode(new ActionMode.Callback() {
                            final int PASTE_MENU_ITEM_ID = 0;

                            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                                Log.d(ProductActivity.this.getClass().getSimpleName(), "onPrepareActionMode");
                                return true;
                            }

                            public void onDestroyActionMode(ActionMode mode) {
                            }

                            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                                Log.d(ProductActivity.this.getClass().getSimpleName(), "onCreateActionMode: menu size=" + menu.size());
                                menu.add(0, PASTE_MENU_ITEM_ID, 0, "Paste");
                                menu.setQwertyMode(false);
                                return true;
                            }

                            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                                Log.d(ProductActivity.this.getClass().getSimpleName(), "onActionItemClicked: item clicked=" + item.getItemId() + " title=" + item.getTitle());
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
                                ViewUtil.popupInputMethodWindow(ProductActivity.this);
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

                commentSendButton = (Button) layout.findViewById(R.id.sendButton);
                commentSendButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doComment();
                    }
                });

                commentCancelButton = (ImageView) layout.findViewById(R.id.cancelButton);
                commentCancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        commentPopup.dismiss();
                        commentPopup = null;
                    }
                });

                commentEmoImage = (ImageView) layout.findViewById(R.id.emoImage);
                commentEmoImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        initEmoticonPopup();
                    }
                });
            }

            if (emoticonVMList.isEmpty() && EmoticonCache.getEmoticons().isEmpty()) {
                EmoticonCache.refresh();
            }

            commentPopup.showAtLocation(layout, Gravity.BOTTOM, 0, 0);
            ViewUtil.popupInputMethodWindow(this);
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), "initCommentPopup: failure", e);
        }
    }

    private void doComment() {
        String comment = commentEditText.getText().toString().trim();
        if (StringUtils.isEmpty(comment)) {
            Toast.makeText(ProductActivity.this, ProductActivity.this.getString(R.string.invalid_comment_body_empty), Toast.LENGTH_SHORT).show();
            return;
        }

        ViewUtil.showSpinner(this);

        Log.d(this.getClass().getSimpleName(), "doComment: postId="+getIntent().getLongExtra(ViewUtil.BUNDLE_KEY_POST_ID, 0L)+" comment="+comment.substring(0, Math.min(5, comment.length())));
        AppController.getApi().answerOnQuestion(new CommentPost(getIntent().getLongExtra(ViewUtil.BUNDLE_KEY_POST_ID, 0L), comment, false), AppController.getInstance().getSessionId(), new Callback<CommentResponse>() {
            @Override
            public void success(CommentResponse array, Response response) {
                //getComments(getIntent().getLongExtra(ViewUtil.BUNDLE_KEY_POST_ID, 0L), 0);  // reload page
                Toast.makeText(ProductActivity.this, ProductActivity.this.getString(R.string.comment_success), Toast.LENGTH_LONG).show();

                reset();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(ProductActivity.this.getClass().getSimpleName(), "doComment.api.answerOnQuestion: failed with error", error);
                Toast.makeText(ProductActivity.this, ProductActivity.this.getString(R.string.comment_failed), Toast.LENGTH_SHORT).show();
                reset();
            }
        });
    }

    private void like(Long postId) {
        AppController.getApiService().likePost(postId, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                ViewUtil.selectLikeButtonStyle(likeImage, likeText, post.getNumLikes());
                isLiked = true;
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(ProductActivity.class.getSimpleName(), "like: failure", error);
            }
        });
    }

    private void unlike(Long postId) {
        AppController.getApiService().unlikePost(postId, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                ViewUtil.unselectLikeButtonStyle(likeImage, likeText, post.getNumLikes());
                isLiked = false;
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(ProductActivity.class.getSimpleName(), "unlike: failure", error);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // comment out more options for now...
        //MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.product_action_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.report:
                Log.d(this.getClass().getSimpleName(), "onOptionsItemSelected: "+item.getItemId());
                return true;
            default:
                return false;
        }
    }

    /*
    private void getComments(Long postID, final int offset) {
        ViewUtil.showSpinner(this);

        AppController.getApi().getComments(postID,offset,AppController.getInstance().getSessionId(),new Callback<List<CommunityPostCommentVM>>(){
            @Override
            public void success(List<CommunityPostCommentVM> commentVMs, Response response) {
                List<CommunityPostCommentVM> communityPostCommentVMs = new ArrayList<CommunityPostCommentVM>();
                if (offset == 0) {   // insert new_post itself for first page only
                    postVm.imageLoaded = false;
                    communityPostCommentVMs.add(postVm);
                }
                communityPostCommentVMs.addAll(commentVMs);

                ViewUtil.stopSpinner(ProductActivity.this);
            }

            @Override
            public void failure(RetrofitError error) {
                ViewUtil.stopSpinner(ProductActivity.this);
                Log.e(ProductActivity.class.getSimpleName(), "getComments: failure", error);
            }
        });
    }
    */

    private void initEmoticonPopup() {
        mainLayout.getForeground().setAlpha(20);
        mainLayout.getForeground().setColorFilter(R.color.light_gray, PorterDuff.Mode.OVERLAY);

        try {
            //We need to get the instance of the LayoutInflater, use the context of this activity
            LayoutInflater inflater = (LayoutInflater) ProductActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            //Inflate the view from a predefined XML layout
            View layout = inflater.inflate(R.layout.emoticon_popup_window,
                    (ViewGroup) findViewById(R.id.popupElement));

            // hide soft keyboard when select emoticon
            ViewUtil.hideInputMethodWindow(this, layout);

            if (emoPopup == null) {
                emoPopup = new PopupWindow(layout,
                        ViewUtil.getRealDimension(DefaultValues.EMOTICON_POPUP_WIDTH),
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        true);
            }

            emoPopup.setBackgroundDrawable(new BitmapDrawable(getResources(), ""));
            emoPopup.setOutsideTouchable(false);
            emoPopup.setFocusable(true);
            emoPopup.showAtLocation(layout, Gravity.CENTER, 0, 0);

            if (emoticonVMList.isEmpty()) {
                emoticonVMList = EmoticonCache.getEmoticons();
            }
            emoticonListAdapter = new EmoticonListAdapter(this,emoticonVMList);

            GridView gridView = (GridView) layout.findViewById(R.id.emoGrid);
            gridView.setAdapter(emoticonListAdapter);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    ImageMapping.insertEmoticon(emoticonVMList.get(i), commentEditText);
                    emoPopup.dismiss();
                    emoPopup = null;
                    ViewUtil.popupInputMethodWindow(ProductActivity.this);
                }
            });
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), "initEmoticonPopup: failure", e);
        }
    }

    private void reset() {
        if (commentPopup != null) {
            commentPopup.dismiss();
            commentPopup = null;
        }
        if (emoPopup != null) {
            emoPopup.dismiss();
            emoPopup = null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        reset();
    }
}