package com.babybox.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.babybox.R;
import com.babybox.adapter.CommentListAdapter;
import com.babybox.app.AppController;
import com.babybox.app.ConversationCache;
import com.babybox.app.TrackedFragmentActivity;
import com.babybox.app.UserInfoCache;
import com.babybox.fragment.AbstractFeedViewFragment;
import com.babybox.fragment.AbstractFeedViewFragment.ItemChangedState;
import com.babybox.fragment.ProductImagePagerFragment;
import com.babybox.util.DateTimeUtil;
import com.babybox.util.DefaultValues;
import com.babybox.util.ImageUtil;
import com.babybox.util.MessageUtil;
import com.babybox.util.SharingUtil;
import com.babybox.util.UrlUtil;
import com.babybox.util.ViewUtil;
import com.babybox.view.AdaptiveViewPager;
import com.babybox.viewmodel.CommentVM;
import com.babybox.viewmodel.ConversationVM;
import com.babybox.viewmodel.NewCommentVM;
import com.babybox.viewmodel.PostVM;
import com.babybox.viewmodel.PostVMLite;
import com.babybox.viewmodel.ResponseStatusVM;

import org.parceler.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ProductActivity extends TrackedFragmentActivity {

    private FrameLayout mainLayout;
    private ImageView backImage, whatsappAction, copyLinkAction;

    private AdaptiveViewPager imagePager;
    private ProductImagePagerAdapter imagePagerAdapter;
    private LinearLayout dotsLayout;
    private List<ImageView> dots = new ArrayList<>();

    private TextView titleText, descText, priceText;
    private Button chatButton, buyButton;
    private LinearLayout likeLayout;
    private ImageView likeImage;
    private TextView likeText;

    private ImageView sellerImage;
    private TextView sellerNameText;

    private RelativeLayout moreCommentsLayout;
    private ImageView moreCommentsImage;

    private TextView commentText, catNameText, timeText, numViewsText, numCommentsText, deleteText;
    private EditText commentEditText;
    private Button sendButton;

    private ListView commentList;

    private PopupWindow commentPopup;
    private Button followButton;

    private PostVM post;
    private long postId;
    private long ownerId;
    private boolean isFollowing;

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

        imagePager = (AdaptiveViewPager) findViewById(R.id.imagePager);
        dotsLayout = (LinearLayout) findViewById(R.id.dotsLayout);

        titleText = (TextView) findViewById(R.id.titleText);
        descText = (TextView) findViewById(R.id.descText);
        priceText = (TextView) findViewById(R.id.priceText);

        chatButton = (Button) findViewById(R.id.chatButton);
        buyButton = (Button) findViewById(R.id.buyButton);
        likeLayout = (LinearLayout) findViewById(R.id.likeLayout);
        likeImage = (ImageView) findViewById(R.id.likeImage);
        likeText = (TextView) findViewById(R.id.likeText);

        followButton = (Button) findViewById(R.id.followButton);

        sellerImage = (ImageView) findViewById(R.id.sellerImage);
        sellerNameText = (TextView) findViewById(R.id.sellerNameText);

        commentText = (TextView) findViewById(R.id.commentText);
        sendButton = (Button) findViewById(R.id.sendButton);
        catNameText = (TextView) findViewById(R.id.catNameText);
        timeText = (TextView) findViewById(R.id.timeText);
        numViewsText = (TextView) findViewById(R.id.numViewsText);
        numCommentsText = (TextView) findViewById(R.id.numCommentsText);
        deleteText = (TextView) findViewById(R.id.deleteText);

        moreCommentsLayout = (RelativeLayout) findViewById(R.id.moreCommentsLayout);
        moreCommentsImage = (ImageView) findViewById(R.id.moreCommentsImage);

        commentList = (ListView) findViewById(R.id.commentList);

        commentText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initCommentPopup();
            }
        });

        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFollowing) {
                    unFollow(ownerId);
                } else {
                    follow(ownerId);
                }
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
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

        postId = getIntent().getLongExtra(ViewUtil.BUNDLE_KEY_ID, 0L);

        getProduct(postId);
        getComments(postId);
    }

    private void setPost(PostVM post) {
        this.post = post;
    }

    private void getProduct(final Long postId) {
        ViewUtil.showSpinner(this);

        AppController.getApiService().getPost(postId, new Callback<PostVM>() {
            @Override
            public void success(final PostVM post, Response response) {
                setPost(post);

                ownerId = post.getOwnerId();

                if (UserInfoCache.getUser().getId() == ownerId)
                    followButton.setVisibility(View.GONE);

                isFollowing = post.isFollowingOwner();

                if (isFollowing) {
                    ViewUtil.selectFollowButtonStyle(followButton);
                } else {
                    ViewUtil.unselectFollowButtonStyle(followButton);
                }

                // Image slider
                if (post.images != null) {
                    imagePagerAdapter = new ProductImagePagerAdapter(getSupportFragmentManager(), post.images);
                    imagePager.setAdapter(imagePagerAdapter);
                    imagePager.setCurrentItem(0);
                    ViewUtil.addDots(ProductActivity.this, imagePagerAdapter.getCount(), dotsLayout, dots, imagePager);
                }

                // details
                ViewUtil.setHtmlText(post.getTitle(), titleText, ProductActivity.this, true);
                ViewUtil.setHtmlText(post.getBody(), descText, ProductActivity.this, true, true);
                catNameText.setText(post.getCategoryName());
                priceText.setText(ViewUtil.priceFormat(post.getPrice()));
                timeText.setText(DateTimeUtil.getTimeAgo(post.getCreatedDate()));
                numViewsText.setText(post.getNumViews() + "");
                numCommentsText.setText(post.getNumComments() + " " + getString(R.string.comments));

                catNameText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewUtil.startCategoryActivity(ProductActivity.this, post.getCategoryId(), "FromProductActivity");
                    }
                });

                // delete
                if (post.isOwner() || (AppController.isUserAdmin())) {
                    if (post.isOwner()) {
                        deleteText.setTextColor(getResources().getColor(R.color.gray));
                    } else if (AppController.isUserAdmin()) {
                        deleteText.setTextColor(getResources().getColor(R.color.admin_green));
                    }
                    deleteText.setVisibility(View.VISIBLE);

                    deleteText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProductActivity.this);
                            alertDialogBuilder.setMessage(getString(R.string.post_delete_confirm));
                            alertDialogBuilder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deletePost(post.getId());
                                }
                            });
                            alertDialogBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        }
                    });
                } else {
                    deleteText.setVisibility(View.GONE);
                }

                // like
                if (post.isLiked()) {
                    ViewUtil.selectLikeButtonStyle(likeImage, likeText, post.getNumLikes());
                } else {
                    ViewUtil.unselectLikeButtonStyle(likeImage, likeText, post.getNumLikes());
                }

                likeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (post.isLiked) {
                            unlike(post);
                        } else {
                            like(post);
                        }
                    }
                });

                // chat

                if (post.getOwnerId().equals(UserInfoCache.getUser().getId())) {
                    chatButton.setVisibility(View.GONE);
                    buyButton.setVisibility(View.GONE);
                } else {
                    chatButton.setVisibility(View.VISIBLE);
                    buyButton.setVisibility(View.VISIBLE);

                    chatButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openConversation(post.id, ProductActivity.this);
                        }
                    });
                    buyButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openConversation(post.id, ProductActivity.this);
                        }
                    });
                }

                // seller

                ImageUtil.displayThumbnailProfileImage(post.getOwnerId(), sellerImage);
                sellerNameText.setText(post.getOwnerName());

                sellerImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewUtil.startUserProfileActivity(ProductActivity.this, post.getOwnerId());
                    }
                });

                sellerNameText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewUtil.startUserProfileActivity(ProductActivity.this, post.getOwnerId());
                    }
                });

                // actionbar

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

                Log.e(ProductActivity.class.getSimpleName(), "getPost: failure", error);
            }
        });
    }

    private void openConversation(final Long postId, final Activity activity) {
        ConversationCache.open(postId, new Callback<ConversationVM>() {
            @Override
            public void success(ConversationVM conversationVM, Response response1) {
                if (conversationVM != null) {
                    ViewUtil.startMessageListActivity(activity, conversationVM.getId());
                } else {
                    Toast.makeText(activity, activity.getString(R.string.pm_start_failed), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(activity, activity.getString(R.string.pm_start_failed), Toast.LENGTH_SHORT).show();
                Log.e(MessageUtil.class.getSimpleName(), "openConversation: failure", error);
            }
        });
    }

    private void getComments(final Long postId) {
        ViewUtil.showSpinner(this);

        commentList.setVisibility(View.GONE);
        moreCommentsImage.setVisibility(View.GONE);

        Long offset = 0L;     // mock testing
        AppController.getApiService().getComments(offset, postId, new Callback<List<CommentVM>>() {
            @Override
            public void success(List<CommentVM> comments, Response response) {
                if (comments != null && comments.size() > 0) {
                    commentList.setVisibility(View.VISIBLE);
                    CommentListAdapter adapter = new CommentListAdapter(
                            ProductActivity.this,
                            ViewUtil.getLastComments(comments, DefaultValues.MAX_COMMENTS_PREVIEW));
                    commentList.setAdapter(adapter);
                    ViewUtil.setHeightBasedOnChildren(ProductActivity.this, commentList);

                    if (comments.size() > DefaultValues.MAX_COMMENTS_PREVIEW) {
                        moreCommentsImage.setVisibility(View.VISIBLE);
                        moreCommentsLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ViewUtil.startProductCommentsActivity(ProductActivity.this, postId);
                            }
                        });
                    }
                }

                ViewUtil.stopSpinner(ProductActivity.this);
            }

            @Override
            public void failure(RetrofitError error) {
                ViewUtil.stopSpinner(ProductActivity.this);
                Log.e(ProductActivity.class.getSimpleName(), "getComments: failure", error);
            }
        });
    }

    private void setIntentResult(ItemChangedState itemChangedState, PostVMLite post) {
        Intent i = new Intent();
        i.putExtra(ViewUtil.INTENT_VALUE_ITEM_CHANGED_STATE, itemChangedState.name());
        i.putExtra(ViewUtil.INTENT_VALUE_OBJECT, post);
        setResult(RESULT_OK, i);
    }

    private void like(final PostVM post) {
        AppController.getApiService().likePost(post.id, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                post.isLiked = true;
                post.numLikes++;
                ViewUtil.selectLikeButtonStyle(likeImage, likeText, post.getNumLikes());

                // pass back to feed view to handle
                setIntentResult(ItemChangedState.ITEM_UPDATED, post);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(ProductActivity.class.getSimpleName(), "like: failure", error);
            }
        });
    }

    private void unlike(final PostVM post) {
        AppController.getApiService().unlikePost(post.id, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                post.isLiked = false;
                post.numLikes--;
                ViewUtil.unselectLikeButtonStyle(likeImage, likeText, post.getNumLikes());

                // pass back to feed view to handle
                setIntentResult(ItemChangedState.ITEM_UPDATED, post);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(ProductActivity.class.getSimpleName(), "unlike: failure", error);
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

                TextView commentSendButton = (TextView) layout.findViewById(R.id.commentSendButton);
                commentSendButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doComment();
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

        Log.d(this.getClass().getSimpleName(), "doComment: postId=" + postId + " comment=" + comment.substring(0, Math.min(5, comment.length())));
        AppController.getApiService().newComment(new NewCommentVM(postId, comment), new Callback<ResponseStatusVM>() {
            @Override
            public void success(ResponseStatusVM responseStatus, Response response) {
                numCommentsText.setText((post.getNumComments()+1) + " " + getString(R.string.comments));
                getComments(postId);  // reload page
                Toast.makeText(ProductActivity.this, ProductActivity.this.getString(R.string.comment_success), Toast.LENGTH_LONG).show();
                reset();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(ProductActivity.this.getClass().getSimpleName(), "doComment.api.newComment: failed with error", error);
                Toast.makeText(ProductActivity.this, ProductActivity.this.getString(R.string.comment_failed), Toast.LENGTH_SHORT).show();
                reset();
            }
        });
    }

    private void deletePost(Long id) {
        AppController.getApiService().deletePost(id, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Toast.makeText(ProductActivity.this, getString(R.string.post_delete_success), Toast.LENGTH_SHORT).show();

                // pass back to feed view to handle
                setIntentResult(ItemChangedState.ITEM_REMOVED, null);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(ProductActivity.this, getString(R.string.post_delete_failed), Toast.LENGTH_SHORT).show();
                error.printStackTrace();
                Log.e(CommentListAdapter.class.getSimpleName(), "deletePost: failure", error);
            }
        });
    }

    private void reset() {
        if (commentPopup != null) {
            commentPopup.dismiss();
            commentPopup = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        reset();
    }

    public void follow(Long id){
        AppController.getApiService().followUser(id,new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                ViewUtil.selectFollowButtonStyle(followButton);
                isFollowing = true;
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }
    public void unFollow(Long id){
        AppController.getApiService().unfollowUser(id,new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                ViewUtil.unselectFollowButtonStyle(followButton);
                isFollowing = false;
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }
}

class ProductImagePagerAdapter extends FragmentStatePagerAdapter {

    private Long[] images;

    public ProductImagePagerAdapter(FragmentManager fm, Long[] images) {
        super(fm);
        this.images = images;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }

    @Override
    public int getCount() {
        return images == null? 0 : images.length;
    }

    @Override
    public Fragment getItem(int position) {
        Log.d(this.getClass().getSimpleName(), "getItem: item - " + position);
        switch (position) {
            default: {
                ProductImagePagerFragment fragment = new ProductImagePagerFragment();
                fragment.setImageId(images[position]);
                return fragment;
            }
        }
    }

    /**
     * HACK... returns POSITION_NONE will refresh pager more frequent than needed... but works in this case
     * http://stackoverflow.com/questions/12510404/reorder-pages-in-fragmentstatepageradapter-using-getitempositionobject-object
     *
     * @param object
     * @return
     */
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
