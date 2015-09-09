package com.babybox.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.babybox.R;
import com.babybox.activity.UserProfileActivity;
import com.babybox.app.AppController;
import com.babybox.mock.CommunityPostCommentVM;
import com.babybox.util.DateTimeUtil;
import com.babybox.util.ImageUtil;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.CommentVM;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CommentListAdapter extends BaseAdapter {
    private ImageView userImage;
    private TextView userNameText, commentText, timeText, deleteText;

    private Activity activity;
    private LayoutInflater inflater;

    private List<CommentVM> comments;

    public CommentListAdapter(Activity activity, List<CommentVM> comments) {
        this.activity = activity;
        this.comments = comments;
    }

    @Override
    public int getCount() {
        if (comments == null)
            return 0;
        return comments.size();
    }

    @Override
    public CommentVM getItem(int location) {
        if (comments == null || location > comments.size()-1)
            return null;
        return comments.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public synchronized View getView(int position, View convertView, final ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.comment_list_item, null);

        userImage = (ImageView) convertView.findViewById(R.id.userImage);
        userNameText = (TextView) convertView.findViewById(R.id.userNameText);
        timeText = (TextView) convertView.findViewById(R.id.timeText);
        commentText = (TextView) convertView.findViewById(R.id.commentText);
        deleteText = (TextView) convertView.findViewById(R.id.deleteText);

        final CommentVM item = comments.get(position);

        // admin fields
        LinearLayout adminLayout = (LinearLayout) convertView.findViewById(R.id.adminLayout);
        if (AppController.isUserAdmin()) {
            ImageView androidIcon = (ImageView) convertView.findViewById(R.id.androidIcon);
            ImageView iosIcon = (ImageView) convertView.findViewById(R.id.iosIcon);
            ImageView mobileIcon = (ImageView) convertView.findViewById(R.id.mobileIcon);
            androidIcon.setVisibility(item.isAndroid()? View.VISIBLE : View.GONE);
            iosIcon.setVisibility(item.isIOS()? View.VISIBLE : View.GONE);
            mobileIcon.setVisibility(item.isMobile()? View.VISIBLE : View.GONE);
            adminLayout.setVisibility(View.VISIBLE);
        } else {
            adminLayout.setVisibility(View.GONE);
        }

        // delete
        if (item.isOwner() || (AppController.isUserAdmin())) {
            if (item.isOwner()) {
                deleteText.setTextColor(this.activity.getResources().getColor(R.color.gray));
            } else if (AppController.isUserAdmin()) {
                deleteText.setTextColor(this.activity.getResources().getColor(R.color.admin_green));
            }
            deleteText.setVisibility(View.VISIBLE);

            final int pos = position;
            deleteText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(inflater.getContext());
                    alertDialogBuilder.setMessage(CommentListAdapter.this.activity.getString(R.string.post_delete_confirm));
                    alertDialogBuilder.setPositiveButton(CommentListAdapter.this.activity.getString(R.string.confirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteComment(item.getId(), pos);
                        }
                    });
                    alertDialogBuilder.setNegativeButton(CommentListAdapter.this.activity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
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

        ViewUtil.setHtmlText(item.getDesc(), commentText, activity, true, true);

        userNameText.setText(item.getPostedBy());
        userNameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, UserProfileActivity.class);
                i.putExtra(ViewUtil.BUNDLE_KEY_ID, item.getOwnerId());
                activity.startActivity(i);
            }
        });

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, UserProfileActivity.class);
                i.putExtra(ViewUtil.BUNDLE_KEY_ID, item.getOwnerId());
                activity.startActivity(i);
            }
        });

        timeText.setText(DateTimeUtil.getTimeAgo(item.getCreatedDate()));

        // profile pic
        ImageUtil.displayThumbnailProfileImage(item.getOwnerId(), userImage);

        return convertView;
    }

    private void loadImages(final CommunityPostCommentVM item, final LinearLayout layout) {
        layout.removeAllViewsInLayout();

        Log.d(this.getClass().getSimpleName(), "loadImages: "+item.getImgs().length+" images");
        for (Long imageId : item.getImgs()) {
            final ImageView postImage = new ImageView(this.activity);
            postImage.setAdjustViewBounds(true);
            postImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
            postImage.setPadding(0, 0, 0, ViewUtil.getRealDimension(10));
            layout.addView(postImage);

            /*
            postImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ViewUtil.fullscreenImagePopup(DetailListAdapter.this, source);
                }
            });
            */

            // obsolete
            /*
            String source = activity.getResources().getString(R.string.base_url) + "/image/get-original-post-image-by-id/" + imageId;
            Log.d(this.getClass().getSimpleName(), "loadImages: source - "+source);
            new LoadPostImage().execute(source, postImage);
            */

            ImageUtil.displayOriginalPostImage(imageId, postImage);
            /*
            ImageUtil.displayOriginalPostImage(imageId, postImage, new RequestListener<String, GlideBitmapDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideBitmapDrawable> target, boolean isFirstResource) {
                    postImage.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideBitmapDrawable resource, String model, Target<GlideBitmapDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    Bitmap loadedImage = resource.getBitmap();
                    if (loadedImage != null) {
                        Log.d(DetailListAdapter.class.getSimpleName(), "onLoadingComplete: loaded bitmap - " + loadedImage.getWidth() + "|" + loadedImage.getHeight());

                        int width = loadedImage.getWidth();
                        int height = loadedImage.getHeight();

                        // always stretch to screen width
                        int displayWidth = ViewUtil.getDisplayDimensions(DetailListAdapter.this.activity).width();
                        float scaleAspect = (float)displayWidth / (float)width;
                        width = displayWidth;
                        height = (int)(height * scaleAspect);

                        Log.d(DetailListAdapter.class.getSimpleName(), "onLoadingComplete: after resize - " + width + "|" + height + " with scaleAspect=" + scaleAspect);

                        Drawable d = new BitmapDrawable(
                                DetailListAdapter.this.activity.getResources(),
                                Bitmap.createScaledBitmap(loadedImage, width, height, false));
                        postImage.setImageDrawable(d);
                        postImage.setVisibility(View.VISIBLE);
                    }
                    return true;
                }
            });
            */
        }
        item.imageLoaded = true;
    }

    private void likeComment(Long id) {
        AppController.getApi().setLikeComment(id, AppController.getInstance().getSessionId(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {

            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(CommentListAdapter.class.getSimpleName(), "likeComment: failure", error);
            }
        });
    }

    private void unLikeComment(Long id) {
        AppController.getApi().setUnLikeComment(id, AppController.getInstance().getSessionId(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {

            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(CommentListAdapter.class.getSimpleName(), "unLikeComment: failure", error);
            }
        });
    }

    private void likePost(Long id) {
        AppController.getApi().setLikePost(id, AppController.getInstance().getSessionId(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {

            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(CommentListAdapter.class.getSimpleName(), "likePost: failure", error);
            }
        });
    }

    private void unLikePost(Long id) {
        AppController.getApi().setUnLikePost(id, AppController.getInstance().getSessionId(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {

            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(CommentListAdapter.class.getSimpleName(), "unLikePost: failure", error);
            }
        });
    }

    private void deletePost(Long id) {
        AppController.getApi().deletePost(id, AppController.getInstance().getSessionId(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Toast.makeText(inflater.getContext(), CommentListAdapter.this.activity.getString(R.string.post_delete_success), Toast.LENGTH_SHORT).show();
                CommentListAdapter.this.activity.finish();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(inflater.getContext(), CommentListAdapter.this.activity.getString(R.string.post_delete_failed), Toast.LENGTH_SHORT).show();
                Log.e(CommentListAdapter.class.getSimpleName(), "deletePost: failure", error);
            }
        });
    }

    private void deleteComment(Long id, final int position) {
        AppController.getApi().deleteComment(id, AppController.getInstance().getSessionId(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Toast.makeText(inflater.getContext(), CommentListAdapter.this.activity.getString(R.string.comment_delete_success), Toast.LENGTH_SHORT).show();
                CommentListAdapter.this.comments.remove(position);
                CommentListAdapter.this.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(inflater.getContext(), CommentListAdapter.this.activity.getString(R.string.comment_delete_success), Toast.LENGTH_SHORT).show();
                error.printStackTrace();
                Log.e(CommentListAdapter.class.getSimpleName(), "deleteComment: failure", error);
            }
        });
    }
}