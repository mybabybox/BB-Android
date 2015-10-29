package com.babybox.activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.babybox.R;
import com.babybox.adapter.PopupCategoryListAdapter;
import com.babybox.app.AppController;
import com.babybox.app.CategoryCache;
import com.babybox.app.TrackedFragmentActivity;
import com.babybox.util.DefaultValues;
import com.babybox.util.ImageMapping;
import com.babybox.util.ImageUtil;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.CategoryVM;
import com.babybox.viewmodel.NewPostVM;
import com.babybox.viewmodel.ResponseStatusVM;

import org.parceler.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class NewPostActivity extends TrackedFragmentActivity {

    protected LinearLayout imagesLayout, selectCatLayout;
    protected RelativeLayout browseLayout, catLayout;
    protected TextView selectCatText;
    protected ImageView selectCatIcon;
    protected TextView catName;
    protected ImageView catIcon;
    protected ImageView backImage, browseImage;
    protected TextView titleEdit, descEdit, priceEdit, postAction, editTextInFocus;

    protected String selectedImagePath = null;
    protected Uri selectedImageUri = null;

    protected List<File> postImageFiles = new ArrayList<>();
    protected List<ImageView> postImages = new ArrayList<>();

    protected Long catId;
    protected PopupWindow categoryPopup;
    protected PopupCategoryListAdapter adapter;

    protected boolean postSuccess = false;

    class PostImage {
        int index;
        File file;
        String path;
        Uri pathUri;
    }

    protected String getActionTypeText() {
        return getString(R.string.new_post_action);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.new_post_activity);

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(getLayoutInflater().inflate(R.layout.new_post_actionbar, null),
                new ActionBar.LayoutParams(
                        ActionBar.LayoutParams.MATCH_PARENT,
                        ActionBar.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER
                )
        );
        getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg_pink));

        backImage = (ImageView) findViewById(R.id.backImage);
        postAction = (TextView) findViewById(R.id.postAction);
        imagesLayout = (LinearLayout) findViewById(R.id.imagesLayout);
        browseLayout = (RelativeLayout) findViewById(R.id.browseLayout);
        catLayout = (RelativeLayout) findViewById(R.id.catLayout);
        selectCatLayout = (LinearLayout) findViewById(R.id.selectCatLayout);
        selectCatText = (TextView) findViewById(R.id.selectCatText);
        selectCatIcon = (ImageView) findViewById(R.id.selectCatIcon);
        catIcon = (ImageView) findViewById(R.id.catIcon);
        catName = (TextView) findViewById(R.id.catName);
        browseImage = (ImageView) findViewById(R.id.browseImage);
        titleEdit = (TextView) findViewById(R.id.titleEdit);
        descEdit = (TextView) findViewById(R.id.descEdit);
        priceEdit = (TextView) findViewById(R.id.priceEdit);
        editTextInFocus = titleEdit;

        titleEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    editTextInFocus = titleEdit;
                }
            }
        });

        descEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    editTextInFocus = descEdit;
                }
            }
        });

        Long id = getIntent().getLongExtra(ViewUtil.BUNDLE_KEY_CATEGORY_ID, 0L);
        if (id == null || id == 0L) {
            catId = null;
        } else {
            catId = id;
            initCategoryLayout(CategoryCache.getCategory(id));
        }
        Log.d(this.getClass().getSimpleName(), "onCreate: catId=" + catId);

        updateSelectCategoryLayout();
        selectCatLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initCategoryPopup();
            }
        });

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        browseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (postImageFiles.size() >= DefaultValues.MAX_POST_IMAGES) {
                    Toast.makeText(NewPostActivity.this,
                            String.format(NewPostActivity.this.getString(R.string.new_post_max_images), DefaultValues.MAX_POST_IMAGES), Toast.LENGTH_SHORT).show();
                    return;
                }
                ImageUtil.openPhotoPicker(NewPostActivity.this);
            }
        });

        if (postImages.size() == 0) {
            postImages.add((ImageView) findViewById(R.id.postImage1));
            postImages.add((ImageView) findViewById(R.id.postImage2));
            postImages.add((ImageView) findViewById(R.id.postImage3));
            postImages.add((ImageView) findViewById(R.id.postImage4));

            for (ImageView postImage : postImages) {
                postImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removePostImage();
                    }
                });
            }
        }

        if (ImageUtil.cropUri != null) {
            setPostImage();
        }

        postAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPost();
            }
        });
    }

    protected void updateSelectCategoryLayout() {
        if (catId == null) {
            selectCatText.setVisibility(View.VISIBLE);
            selectCatIcon.setVisibility(View.VISIBLE);
            catIcon.setVisibility(View.GONE);
            catName.setVisibility(View.GONE);
        } else {
            selectCatText.setVisibility(View.GONE);
            selectCatIcon.setVisibility(View.GONE);
            catIcon.setVisibility(View.VISIBLE);
            catName.setVisibility(View.VISIBLE);
        }
    }

    protected void setPostImage(Bitmap bp){
        ImageView postImage = postImages.get(postImageFiles.size());
        postImage.setImageDrawable(new BitmapDrawable(this.getResources(), bp));
        postImage.setVisibility(View.VISIBLE);
        File photo = new File(ImageUtil.getRealPathFromUri(this, selectedImageUri));
        postImageFiles.add(photo);
    }

    protected void setPostImage(){
        Log.d(this.getClass().getSimpleName(), "uri="+getIntent().getData());
        Log.d(this.getClass().getSimpleName(), "size=" + getIntent().getIntExtra(ViewUtil.BUNDLE_KEY_INDEX, 0));
        Log.d(this.getClass().getSimpleName(), "postImageFiles=" + postImageFiles.size());
        Log.d(this.getClass().getSimpleName(), "imagePath.size=" + ImageUtil.imagePaths.size());




        // File photo = new File(ImageUtil.getRealPathFromUri(this, selectedImageUri));
        if (ImageUtil.imagePaths.size() != 0) {
            for (int i = 0; i < ImageUtil.imagePaths.size(); i++) {
                String imagePath = ImageUtil.imagePaths.get(i);
                File photo = new File(imagePath);
                postImageFiles.add(photo);

                ImageView imageView = postImages.get(i);
                Bitmap bp = ImageUtil.resizeAsPreviewThumbnail(imagePath);
                imageView.setImageBitmap(bp);

                //Uri imagePathUri = Uri.parse(imagePath);
                //imageView.setImageURI(imagePathUri);

                Log.d(this.getClass().getSimpleName(), "imagePath=" + imagePath);
            }
        }
    }

    protected void removePostImage(){
        if (postImageFiles.size() > 0) {
            int toRemove = postImageFiles.size()-1;
            postImages.get(toRemove).setImageDrawable(getResources().getDrawable(R.drawable.img_camera));
            postImageFiles.remove(toRemove);
        }
        if(ImageUtil.imagePaths.size() > 0){
            int toRemove = ImageUtil.imagePaths.size() - 1;
            ImageUtil.imagePaths.remove(toRemove);
        }
    }

    protected void displayPhotoActivity() {
        Intent intent = new Intent(this, SelectImageActivity.class);
        intent.putExtra(ViewUtil.BUNDLE_KEY_IMAGE_SOURCE, 2);
        intent.putExtra(ViewUtil.BUNDLE_KEY_ID, getIntent().getLongExtra(ViewUtil.BUNDLE_KEY_ID, -1L));
        intent.putExtra(ViewUtil.BUNDLE_KEY_INDEX, postImageFiles.size());
        intent.setData(selectedImageUri);
        startActivityForResult(intent, ViewUtil.CROP_IMAGE_REQUEST_CODE);
        overridePendingTransition(0, 0);
        //finish();
    }

    protected NewPostVM getNewPost() {
        String title = titleEdit.getText().toString().trim();
        String body = descEdit.getText().toString().trim();
        String priceValue = priceEdit.getText().toString().trim();

        if (StringUtils.isEmpty(title)) {
            Toast.makeText(this, getString(R.string.invalid_post_title_empty), Toast.LENGTH_SHORT).show();
            return null;
        }

        if (StringUtils.isEmpty(body)) {
            Toast.makeText(this, getString(R.string.invalid_post_desc_empty), Toast.LENGTH_SHORT).show();
            return null;
        }

        if (StringUtils.isEmpty(priceValue)) {
            Toast.makeText(this, getString(R.string.invalid_post_price_empty), Toast.LENGTH_SHORT).show();
            return null;
        }

        Long price = 0L;
        try {
            price = Long.valueOf(priceValue);
        } catch (NumberFormatException e) {
            Toast.makeText(this, getString(R.string.invalid_post_price_not_number), Toast.LENGTH_SHORT).show();
            return null;
        }

        if (catId == null) {
            initCategoryPopup();
            return null;
        }

        NewPostVM newPost = new NewPostVM(catId, title, body, price, postImageFiles);
        return newPost;
    }

    protected void doPost() {
        if (postImageFiles.size() == 0) {
            Toast.makeText(this, getString(R.string.invalid_post_no_photo), Toast.LENGTH_SHORT).show();
            return;
        }

        NewPostVM newPost = getNewPost();
        if (newPost == null) {
            return;
        }

        ViewUtil.showSpinner(this);

        postAction.setEnabled(false);
        AppController.getApiService().newPost(newPost, new Callback<ResponseStatusVM>() {
            @Override
            public void success(ResponseStatusVM responseStatus, Response response) {
                complete();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(NewPostActivity.this, String.format(NewPostActivity.this.getString(R.string.post_failed), getActionTypeText()), Toast.LENGTH_SHORT).show();
                Log.e(NewPostActivity.class.getSimpleName(), "doPost: failure", error);
            }
        });
    }

    protected void reset() {
        postAction.setEnabled(true);
        postImageFiles.clear();
        ImageUtil.imagePaths.clear();

        if (categoryPopup != null) {
            categoryPopup.dismiss();
            categoryPopup = null;
        }

        ViewUtil.stopSpinner(NewPostActivity.this);
    }

    protected void complete() {
        reset();

        postSuccess = true;
        Toast.makeText(NewPostActivity.this, String.format(getString(R.string.post_success), getActionTypeText()), Toast.LENGTH_LONG).show();
        ViewUtil.setActivityResult(this, true);
        finish();
    }

    protected void initCategoryLayout(CategoryVM category) {
        catId = category.id;
        catName.setText(category.getName());
        int resId = ImageMapping.map(category.getIcon());
        if (resId != -1) {
            catIcon.setImageDrawable(getResources().getDrawable(resId));
        } else {
            Log.d(this.getClass().getSimpleName(), "initCategoryPopup: load category icon from background - " + category.getIcon());
            ImageUtil.displayImage(category.getIcon(), catIcon);
        }
        updateSelectCategoryLayout();
    }

    protected void initCategoryPopup() {
        try {
            LayoutInflater inflater = (LayoutInflater) NewPostActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View layout = inflater.inflate(R.layout.category_popup_window,
                    (ViewGroup) findViewById(R.id.popupElement));

            if (categoryPopup == null) {
                categoryPopup = new PopupWindow(
                        layout,
                        ViewUtil.getRealDimension(DefaultValues.CATEGORY_PICKER_POPUP_WIDTH),
                        ViewUtil.getRealDimension(DefaultValues.CATEGORY_PICKER_POPUP_HEIGHT),
                        true);
            }

            categoryPopup.setBackgroundDrawable(new BitmapDrawable(getResources(), ""));
            categoryPopup.setOutsideTouchable(false);
            categoryPopup.setFocusable(true);
            categoryPopup.showAtLocation(layout, Gravity.CENTER, 0, 0);

            ListView listView = (ListView) layout.findViewById(R.id.categoryList);
            adapter = new PopupCategoryListAdapter(this, CategoryCache.getCategories());
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    CategoryVM category = adapter.getItem(position);
                    catId = category.getId();
                    initCategoryLayout(category);

                    categoryPopup.dismiss();
                    categoryPopup = null;
                    Log.d(this.getClass().getSimpleName(), "initCategoryPopup: listView.onItemClick: category="+category.getId()+"|"+category.getName());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (postImageFiles.size() >= DefaultValues.MAX_POST_IMAGES) {
            Toast.makeText(NewPostActivity.this,
                    String.format(NewPostActivity.this.getString(R.string.new_post_max_images), DefaultValues.MAX_POST_IMAGES), Toast.LENGTH_SHORT).show();
            return;
        }

        if (resultCode == RESULT_CANCELED) {
            postImageFiles.clear();
            setPostImage();
        }

        if (resultCode == RESULT_OK) {
            if ( (requestCode == ViewUtil.SELECT_GALLERY_IMAGE_REQUEST_CODE  && data != null) ||
                    requestCode == ViewUtil.SELECT_CAMERA_IMAGE_REQUEST_CODE )  {

                String path = "";
                if (requestCode == ViewUtil.SELECT_GALLERY_IMAGE_REQUEST_CODE  && data != null) {
                    selectedImageUri = data.getData();
                    selectedImagePath = ImageUtil.getRealPathFromUri(this, selectedImageUri);
                    path = selectedImageUri.getPath();
                } else if (requestCode == ViewUtil.SELECT_CAMERA_IMAGE_REQUEST_CODE) {
                    File picture = new File(Environment.getExternalStorageDirectory(), ImageUtil.CAMERA_IMAGE_TEMP_PATH);
                    selectedImageUri = Uri.fromFile(picture);
                    selectedImagePath = picture.getPath();
                    path = picture.getPath();
                }

                Log.d(this.getClass().getSimpleName(), "onActivityResult: selectedImageUri=" + path + " selectedImagePath=" + selectedImagePath);

                Bitmap bitmap = ImageUtil.resizeToUpload(selectedImagePath);
                if (bitmap != null) {
                    //setPostImage(bitmap);
                    displayPhotoActivity();
                } else {
                    Toast.makeText(NewPostActivity.this, NewPostActivity.this.getString(R.string.photo_size_too_big), Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == ViewUtil.CROP_IMAGE_REQUEST_CODE) {
                setPostImage();
            }
        }

        // pop back soft keyboard
        ViewUtil.popupInputMethodWindow(this);
    }

    @Override
    public void onBackPressed() {
        String title = titleEdit.getText().toString().trim();
        String desc = descEdit.getText().toString().trim();
        String price = priceEdit.getText().toString().trim();

        if (postSuccess ||
                (postImageFiles.size() == 0 && StringUtils.isEmpty(title) && StringUtils.isEmpty(desc) && StringUtils.isEmpty(price))) {
            super.onBackPressed();
            reset();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(NewPostActivity.this);
        builder.setMessage(String.format(getString(R.string.cancel_post), getActionTypeText()))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        NewPostActivity.super.onBackPressed();
                        reset();
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
