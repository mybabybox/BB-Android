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
import com.babybox.util.SelectedPostImage;
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
    protected RelativeLayout catLayout;
    protected TextView selectCatText;
    protected ImageView selectCatIcon;
    protected TextView catName;
    protected ImageView catIcon;
    protected ImageView backImage;
    protected TextView titleEdit, descEdit, priceEdit, postAction, editTextInFocus;

    protected List<ImageView> postImages = new ArrayList<>();

    protected int selectedPostImageIndex = -1;
    protected Uri selectedImageUri = null;
    protected List<SelectedPostImage> selectedPostImages = new ArrayList<>();

    protected Long catId;
    protected PopupWindow categoryPopup;
    protected PopupCategoryListAdapter adapter;

    protected boolean postSuccess = false;

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
        catLayout = (RelativeLayout) findViewById(R.id.catLayout);
        selectCatLayout = (LinearLayout) findViewById(R.id.selectCatLayout);
        selectCatText = (TextView) findViewById(R.id.selectCatText);
        selectCatIcon = (ImageView) findViewById(R.id.selectCatIcon);
        catIcon = (ImageView) findViewById(R.id.catIcon);
        catName = (TextView) findViewById(R.id.catName);
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

        // select image
        if (postImages.size() == 0) {
            ImageView postImage1 = (ImageView) findViewById(R.id.postImage1);
            postImage1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = 0;
                    SelectedPostImage image = getSelectedPostImage(index);
                    if (image == null) {
                        // select
                        selectedPostImageIndex = index;
                        ImageUtil.openPhotoPicker(NewPostActivity.this);
                    } else {
                        // remove
                        removeSelectedPostImage(index);
                    }
                }
            });
            postImages.add(postImage1);

            ImageView postImage2 = (ImageView) findViewById(R.id.postImage2);
            postImage2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = 1;
                    SelectedPostImage image = getSelectedPostImage(index);
                    if (image == null) {
                        // select
                        selectedPostImageIndex = index;
                        ImageUtil.openPhotoPicker(NewPostActivity.this);
                    } else {
                        // remove
                        removeSelectedPostImage(index);
                    }
                }
            });
            postImages.add(postImage2);

            ImageView postImage3 = (ImageView) findViewById(R.id.postImage3);
            postImage3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = 2;
                    SelectedPostImage image = getSelectedPostImage(index);
                    if (image == null) {
                        // select
                        selectedPostImageIndex = index;
                        ImageUtil.openPhotoPicker(NewPostActivity.this);
                    } else {
                        // remove
                        removeSelectedPostImage(index);
                    }
                }
            });
            postImages.add(postImage3);

            ImageView postImage4 = (ImageView) findViewById(R.id.postImage4);
            postImage4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = 3;
                    SelectedPostImage image = getSelectedPostImage(index);
                    if (image == null) {
                        // select
                        selectedPostImageIndex = index;
                        ImageUtil.openPhotoPicker(NewPostActivity.this);
                    } else {
                        // remove
                        removeSelectedPostImage(index);
                    }
                }
            });
            postImages.add(postImage4);
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

    protected void selectPostImage(Bitmap bp, int index) {
        ImageView postImage = postImages.get(index);
        postImage.setImageDrawable(new BitmapDrawable(this.getResources(), bp));
        postImage.setVisibility(View.VISIBLE);
        selectedPostImages.add(new SelectedPostImage(index, ImageUtil.getRealPathFromUri(this, selectedImageUri)));
    }

    protected void selectPostImage(int index, String croppedImagePath) {
        Log.d(this.getClass().getSimpleName(), "uri=" + getIntent().getData());
        Log.d(this.getClass().getSimpleName(), "size=" + getIntent().getIntExtra(ViewUtil.BUNDLE_KEY_INDEX, 0));
        Log.d(this.getClass().getSimpleName(), "index=" + index);
        Log.d(this.getClass().getSimpleName(), "selectedPostImages.size=" + selectedPostImages.size());

        String imagePath = croppedImagePath;
        if (!StringUtils.isEmpty(imagePath)) {
            selectedPostImages.add(new SelectedPostImage(index, imagePath));

            ImageView imageView = postImages.get(index);
            Bitmap bp = ImageUtil.resizeAsPreviewThumbnail(imagePath);
            imageView.setImageBitmap(bp);
            //imageView.setImageURI(Uri.parse(imagePath);

            Log.d(this.getClass().getSimpleName(), "selectPostImage: croppedImagePath=" + imagePath);
        }
    }

    protected void removeSelectedPostImage(int index){
        SelectedPostImage toRemove = getSelectedPostImage(index);
        selectedPostImages.remove(toRemove);
        postImages.get(index).setImageDrawable(getResources().getDrawable(R.drawable.img_camera));
    }

    protected void selectImageActivity(Uri imageUri) {
        Intent intent = new Intent(this, SelectImageActivity.class);
        intent.putExtra(ViewUtil.BUNDLE_KEY_IMAGE_SOURCE, 2);
        intent.putExtra(ViewUtil.BUNDLE_KEY_ID, getIntent().getLongExtra(ViewUtil.BUNDLE_KEY_ID, -1L));
        intent.setData(imageUri);
        startActivityForResult(intent, ViewUtil.CROP_IMAGE_REQUEST_CODE);
        overridePendingTransition(0, 0);
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

        NewPostVM newPost = new NewPostVM(catId, title, body, price, selectedPostImages);
        return newPost;
    }

    protected void doPost() {
        if (selectedPostImages.size() == 0) {
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

    protected SelectedPostImage getSelectedPostImage(int index) {
        for (SelectedPostImage image : selectedPostImages) {
            if (image.index.equals(index)) {
                return image;
            }
        }
        return null;
    }

    protected void reset() {
        postAction.setEnabled(true);
        selectedPostImages.clear();

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
                    Log.d(this.getClass().getSimpleName(), "initCategoryPopup: listView.onItemClick: category=" + category.getId() + "|" + category.getName());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (selectedPostImages.size() >= DefaultValues.MAX_POST_IMAGES) {
            Toast.makeText(NewPostActivity.this,
                    String.format(NewPostActivity.this.getString(R.string.new_post_max_images), DefaultValues.MAX_POST_IMAGES), Toast.LENGTH_SHORT).show();
            return;
        }

        if (resultCode == RESULT_OK) {
            if ( (requestCode == ViewUtil.SELECT_GALLERY_IMAGE_REQUEST_CODE  && data != null) ||
                    requestCode == ViewUtil.SELECT_CAMERA_IMAGE_REQUEST_CODE )  {

                String imagePath = "";
                if (requestCode == ViewUtil.SELECT_GALLERY_IMAGE_REQUEST_CODE  && data != null) {
                    selectedImageUri = data.getData();
                    imagePath = ImageUtil.getRealPathFromUri(this, selectedImageUri);
                } else if (requestCode == ViewUtil.SELECT_CAMERA_IMAGE_REQUEST_CODE) {
                    File picture = new File(Environment.getExternalStorageDirectory(), ImageUtil.CAMERA_IMAGE_TEMP_PATH);
                    selectedImageUri = Uri.fromFile(picture);
                    imagePath = picture.getPath();
                }

                Log.d(this.getClass().getSimpleName(), "onActivityResult: imagePath=" + imagePath);

                Bitmap bitmap = ImageUtil.resizeToUpload(imagePath);
                if (bitmap != null) {
                    selectImageActivity(selectedImageUri);
                } else {
                    Toast.makeText(NewPostActivity.this, NewPostActivity.this.getString(R.string.photo_size_too_big), Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == ViewUtil.CROP_IMAGE_REQUEST_CODE) {
                String croppedImagePath = data.getStringExtra(ViewUtil.INTENT_RESULT_OBJECT);
                selectPostImage(selectedPostImageIndex, croppedImagePath);
            }
        } else if (resultCode == RESULT_CANCELED) {
            // no-op
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
                (selectedPostImages.size() == 0 && StringUtils.isEmpty(title) && StringUtils.isEmpty(desc) && StringUtils.isEmpty(price))) {
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
