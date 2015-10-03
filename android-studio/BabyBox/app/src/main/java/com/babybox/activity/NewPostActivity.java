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
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.babybox.R;
import com.babybox.adapter.EmoticonListAdapter;
import com.babybox.adapter.PopupCategoryListAdapter;
import com.babybox.app.AppController;
import com.babybox.app.CategoryCache;
import com.babybox.app.EmoticonCache;
import com.babybox.app.TrackedFragmentActivity;
import com.babybox.util.DefaultValues;
import com.babybox.util.ImageMapping;
import com.babybox.util.ImageUtil;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.CategoryVM;
import com.babybox.viewmodel.EmoticonVM;
import com.babybox.viewmodel.NewPostVM;
import com.babybox.viewmodel.ResponseStatusVM;

import org.parceler.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.MultipartTypedOutput;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

public class NewPostActivity extends TrackedFragmentActivity {

    protected RelativeLayout catLayout;
    protected LinearLayout selectCatLayout;
    protected TextView selectCatText;
    protected ImageView selectCatIcon;
    protected TextView catName;
    protected ImageView catIcon;
    protected ImageView backImage, browseImage, emoImage;
    protected TextView titleEdit, descEdit, priceEdit, postAction, editTextInFocus;

    protected String selectedImagePath = null;
    protected Uri selectedImageUri = null;

    protected List<File> photos = new ArrayList<>();
    protected List<ImageView> postImages = new ArrayList<>();

    protected List<EmoticonVM> emoticonVMList = new ArrayList<>();
    protected EmoticonListAdapter emoticonListAdapter;

    protected Long catId;
    protected PopupWindow categoryPopup, emoPopup;
    protected PopupCategoryListAdapter adapter;

    protected boolean postSuccess = false;
    protected int imageUploadSuccessCount = 0;

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
        catLayout = (RelativeLayout) findViewById(R.id.catLayout);
        selectCatLayout = (LinearLayout) findViewById(R.id.selectCatLayout);
        selectCatText = (TextView) findViewById(R.id.selectCatText);
        selectCatIcon = (ImageView) findViewById(R.id.selectCatIcon);
        catIcon = (ImageView) findViewById(R.id.catIcon);
        catName = (TextView) findViewById(R.id.catName);
        browseImage = (ImageView) findViewById(R.id.browseImage);
        emoImage = (ImageView) findViewById(R.id.emoImage);
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

        Long id = getIntent().getLongExtra(ViewUtil.BUNDLE_KEY_ID, -1L);
        if (id == 0L) {
            catId = null;
            catLayout.setVisibility(View.VISIBLE);
        } else {
            catId = id;
            catLayout.setVisibility(View.GONE);
        }
        Log.d(this.getClass().getSimpleName(), "onCreate: catId=" + catId);

        updateSelectCatLayout();
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
                ImageUtil.openPhotoPicker(NewPostActivity.this);
            }
        });

        if (postImages.size() == 0) {
            postImages.add((ImageView) findViewById(R.id.postImage1));
            postImages.add((ImageView) findViewById(R.id.postImage2));
            postImages.add((ImageView) findViewById(R.id.postImage3));

            for (ImageView postImage : postImages) {
                postImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removePostImage();
                    }
                });
            }
        }

        emoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initEmoticonPopup();
            }
        });

        postAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPost();
            }
        });
    }

    protected void updateSelectCatLayout() {
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ViewUtil.SELECT_IMAGE_REQUEST_CODE && resultCode == RESULT_OK &&
                data != null && photos.size() < DefaultValues.MAX_POST_IMAGES) {

            selectedImageUri = data.getData();
            selectedImagePath = ImageUtil.getRealPathFromUri(this, selectedImageUri);

            String path = selectedImageUri.getPath();
            Log.d(this.getClass().getSimpleName(), "onActivityResult: selectedImageUri=" + path + " selectedImagePath=" + selectedImagePath);

            Bitmap bitmap = ImageUtil.resizeAsPreviewThumbnail(selectedImagePath);
            if (bitmap != null) {
                setPostImage(bitmap);
            } else {
                Toast.makeText(NewPostActivity.this, NewPostActivity.this.getString(R.string.photo_size_too_big), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(NewPostActivity.this, NewPostActivity.this.getString(R.string.photo_not_found), Toast.LENGTH_SHORT).show();
        }

        // pop back soft keyboard
        ViewUtil.popupInputMethodWindow(this);
    }

    protected void setPostImage(Bitmap bp){
        ImageView postImage = postImages.get(photos.size());
        postImage.setImageDrawable(new BitmapDrawable(this.getResources(), bp));
        postImage.setVisibility(View.VISIBLE);
        File photo = new File(ImageUtil.getRealPathFromUri(this, selectedImageUri));
        photos.add(photo);
    }

    protected void removePostImage(){
        if (photos.size() > 0) {
            int toRemove = photos.size()-1;
            postImages.get(toRemove).setImageDrawable(null);
            photos.remove(toRemove);
        }
    }

    protected void doPost() {
        String title = titleEdit.getText().toString().trim();
        String body = descEdit.getText().toString().trim();
        String priceValue = priceEdit.getText().toString().trim();

        if (StringUtils.isEmpty(title)) {
            Toast.makeText(NewPostActivity.this, NewPostActivity.this.getString(R.string.invalid_post_title_empty), Toast.LENGTH_SHORT).show();
            return;
        }

        if (StringUtils.isEmpty(body)) {
            Toast.makeText(NewPostActivity.this, NewPostActivity.this.getString(R.string.invalid_post_desc_empty), Toast.LENGTH_SHORT).show();
            return;
        }

        if (StringUtils.isEmpty(priceValue)) {
            Toast.makeText(NewPostActivity.this, NewPostActivity.this.getString(R.string.invalid_post_price_empty), Toast.LENGTH_SHORT).show();
            return;
        }

        Long price = 0L;
        try {
            price = Long.valueOf(priceValue);
        } catch (NumberFormatException e) {
            Toast.makeText(NewPostActivity.this, NewPostActivity.this.getString(R.string.invalid_post_price_not_number), Toast.LENGTH_SHORT).show();
            return;
        }

        if (catId == null) {
            initCategoryPopup();
            return;
        }

        ViewUtil.showSpinner(this);

        List<TypedFile> typedFiles = new ArrayList<>();

        int i = 0;
        MultipartTypedOutput multipartTypedOutput = new MultipartTypedOutput();
        for (File photo : photos) {
            TypedFile typedFile = new TypedFile("application/octet-stream", photo);
            multipartTypedOutput.addPart("post-image"+i,typedFile);
            i++;
        }

        multipartTypedOutput.addPart("title", new TypedString(title));
        multipartTypedOutput.addPart("catId", new TypedString(catId+""));
        multipartTypedOutput.addPart("body", new TypedString(body));
        multipartTypedOutput.addPart("price", new TypedString(priceValue));

        Log.d(this.getClass().getSimpleName(), "doPost: catId=" + catId + " title=" + title + "images=" + typedFiles.size());
        NewPostVM newPost = new NewPostVM(catId, title, body, price);
        AppController.getApiService().newPost(multipartTypedOutput, newPost, new Callback<ResponseStatusVM>() {
            @Override
            public void success(ResponseStatusVM responseStatus, Response response) {
                postSuccess = true;
                complete();

                /*if (withPhotos) {
                    uploadPhotos(responseStatus.getObjId());
                } else {
                    complete();
                }*/
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
                ViewUtil.stopSpinner(NewPostActivity.this);
                Toast.makeText(NewPostActivity.this, NewPostActivity.this.getString(R.string.new_post_failed), Toast.LENGTH_SHORT).show();
                Log.e(NewPostActivity.class.getSimpleName(), "doPost: failure", error);
            }
        });
    }

    protected void uploadPhotos(Long postId) {
        for (File photo : photos) {
            photo = ImageUtil.resizeAsJPG(photo);   // IMPORTANT: resize before upload
            TypedFile typedFile = new TypedFile("application/octet-stream", photo);
            AppController.getMockApi().uploadPostPhoto(postId, typedFile, new Callback<Response>() {
                @Override
                public void success(Response array, retrofit.client.Response response) {
                    imageUploadSuccessCount++;
                    if (imageUploadSuccessCount >= photos.size()) {
                        imageUploadSuccessCount = 0;
                        complete();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    ViewUtil.stopSpinner(NewPostActivity.this);
                    Log.e(NewPostActivity.class.getSimpleName(), "uploadPhotos: failure", error);
                }
            });
        }
    }

    protected void complete() {
        ViewUtil.stopSpinner(this);
        onBackPressed();
        finish();
        Toast.makeText(NewPostActivity.this, NewPostActivity.this.getString(R.string.new_post_success), Toast.LENGTH_LONG).show();
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

                    catName.setText(category.getName());
                    int resId = ImageMapping.map(category.getIcon());
                    if (resId != -1) {
                        catIcon.setImageDrawable(getResources().getDrawable(resId));
                    } else {
                        Log.d(this.getClass().getSimpleName(), "initCategoryPopup: load category icon from background - " + category.getIcon());
                        ImageUtil.displayImage(category.getIcon(), catIcon);
                    }

                    updateSelectCatLayout();
                    categoryPopup.dismiss();
                    categoryPopup = null;
                    Log.d(this.getClass().getSimpleName(), "initCategoryPopup: listView.onItemClick: category="+category.getId()+"|"+category.getName());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void initEmoticonPopup() {
        try {
            //We need to get the instance of the LayoutInflater, use the context of this activity
            LayoutInflater inflater = (LayoutInflater) NewPostActivity.this
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
                    ImageMapping.insertEmoticon(emoticonVMList.get(i), editTextInFocus);
                    emoPopup.dismiss();
                    emoPopup = null;
                    ViewUtil.popupInputMethodWindow(NewPostActivity.this);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        String title = titleEdit.getText().toString().trim();
        String desc = descEdit.getText().toString().trim();
        String price = priceEdit.getText().toString().trim();

        if (postSuccess ||
                (StringUtils.isEmpty(title) && StringUtils.isEmpty(desc) && StringUtils.isEmpty(price))) {
            super.onBackPressed();
            if (categoryPopup != null) {
                categoryPopup.dismiss();
                categoryPopup = null;
            }
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(NewPostActivity.this);
        builder.setMessage(getString(R.string.cancel_new_post))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        NewPostActivity.super.onBackPressed();
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
