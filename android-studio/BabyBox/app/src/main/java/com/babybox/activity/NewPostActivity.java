package com.babybox.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.babybox.R;
import com.babybox.adapter.PopupCategoryListAdapter;
import com.babybox.app.AppController;
import com.babybox.app.CategoryCache;
import com.babybox.app.CountryCache;
import com.babybox.app.NotificationCounter;
import com.babybox.app.TrackedFragmentActivity;
import com.babybox.app.UserInfoCache;
import com.babybox.util.DefaultValues;
import com.babybox.util.ImageUtil;
import com.babybox.util.SelectedImage;
import com.babybox.util.SharedPreferencesUtil;
import com.babybox.util.SharingUtil;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.CategoryVM;
import com.babybox.viewmodel.CountryVM;
import com.babybox.viewmodel.NewPostVM;
import com.babybox.viewmodel.PostVM;
import com.babybox.viewmodel.ResponseStatusVM;

import org.parceler.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class NewPostActivity extends TrackedFragmentActivity{
    private static final String TAG = NewPostActivity.class.getName();

    protected LinearLayout imagesLayout, selectCatLayout, sellerLayout, sharingLayout;
    protected RelativeLayout catLayout;
    protected TextView selectCatText;
    protected ImageView selectCatIcon;
    protected TextView catName;
    protected ImageView catIcon;
    protected ImageView backImage;
    protected TextView titleEdit, descEdit, priceEdit, originalPriceEdit, postAction, editTextInFocus;
    protected Spinner conditionTypeSpinner, countrySpinner;
    protected CheckBox freeDeliveryCheckBox;
    protected TextView autoCompleteText;

    protected List<ImageView> postImages = new ArrayList<>();

    protected int selectedPostImageIndex = -1;
    protected Uri selectedImageUri = null;
    protected List<SelectedImage> selectedImages = new ArrayList<>();

    protected ViewUtil.PostConditionType conditionType;

    protected CountryVM country;

    protected Long catId;
    protected PopupWindow categoryPopup;
    protected PopupCategoryListAdapter adapter;

    protected ToggleButton fbSharingButton;

    protected boolean postSuccess = false;

    protected String getActionTypeText() {
        return getString(R.string.new_post_action);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.new_post_activity);

        fbSharingButton =(ToggleButton)findViewById(R.id.facebookshare);

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
        conditionTypeSpinner = (Spinner) findViewById(R.id.conditionTypeSpinner);
        sellerLayout = (LinearLayout) findViewById(R.id.sellerLayout);
        originalPriceEdit = (TextView) findViewById(R.id.originalPriceEdit);
        freeDeliveryCheckBox = (CheckBox) findViewById(R.id.freeDeliveryCheckBox);
        countrySpinner = (Spinner) findViewById(R.id.countrySpinner);
        sharingLayout = (LinearLayout) findViewById(R.id.sharingLayout);
        editTextInFocus = titleEdit;

        SharedPreferencesUtil.getInstance().saveUserLocation("");

        autoCompleteText = (TextView) findViewById(R.id.autoCompleteText);
        autoCompleteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NewPostActivity.this,LocationActivity.class));
            }
        });

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

        // select image

        if (postImages.size() == 0) {
            ImageView postImage1 = (ImageView) findViewById(R.id.postImage1);
            postImage1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = 0;
                    SelectedImage image = getSelectedPostImage(index);
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
                    SelectedImage image = getSelectedPostImage(index);
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
                    SelectedImage image = getSelectedPostImage(index);
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
                    SelectedImage image = getSelectedPostImage(index);
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

        // condition spinner

        initConditionTypeSpinner();

        conditionTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (conditionTypeSpinner.getSelectedItem() != null) {
                    String value = conditionTypeSpinner.getSelectedItem().toString();
                    conditionType = ViewUtil.parsePostConditionTypeFromValue(value);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // seller layout

        sellerLayout.setVisibility(View.GONE);
        if (AppController.isUserAdmin() ||
                UserInfoCache.getUser().isPromotedSeller() ||
                UserInfoCache.getUser().isVerifiedSeller()) {
            sellerLayout.setVisibility(View.VISIBLE);
        }

        freeDeliveryCheckBox.setChecked(false);

        initCountrySpinner();

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (countrySpinner.getSelectedItem() != null) {
                    String value = countrySpinner.getSelectedItem().toString();
                    country = CountryCache.getCountryWithName(value);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // sharing

        /*
        Log.d(TAG, "isSharingFacebookWall="+SharedPreferencesUtil.getInstance().isSharingFacebookWall());
        if (UserInfoCache.getUser().isFbLogin()) {
            sharingLayout.setVisibility(View.VISIBLE);
            fbSharingButton.setChecked(SharedPreferencesUtil.getInstance().isSharingFacebookWall());
            fbSharingButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // no opt
                }
            });
        } else {
            sharingLayout.setVisibility(View.GONE);
            fbSharingButton.setChecked(false);
        }
        */

        // post

        postAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPost();
            }
        });

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if(!SharedPreferencesUtil.getInstance().getUserLocation().equals("")) {
            autoCompleteText.setText(SharedPreferencesUtil.getInstance().getUserLocation());
        }
    }

    protected void setConditionTypeSpinner(ViewUtil.PostConditionType conditionType) {
        String value = ViewUtil.getPostConditionTypeValue(conditionType);
        int pos = ((ArrayAdapter)conditionTypeSpinner.getAdapter()).getPosition(value);
        conditionTypeSpinner.setSelection(pos);
    }

    protected void initConditionTypeSpinner() {
        List<String> conditionTypes = new ArrayList<>();
        conditionTypes.add(getString(R.string.spinner_select));
        conditionTypes.addAll(ViewUtil.getPostConditionTypeValues());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item_right,
                conditionTypes);
        conditionTypeSpinner.setAdapter(adapter);
    }

    protected void setCountrySpinner(String code) {
        CountryVM country = CountryCache.getCountryWithCode(code);
        if (country != null) {
            int pos = ((ArrayAdapter)countrySpinner.getAdapter()).getPosition(country.name);
            countrySpinner.setSelection(pos);
        }
    }

    protected void initCountrySpinner() {
        List<String> countries = new ArrayList<>();
        countries.add(getString(R.string.spinner_select));
        for (CountryVM country : CountryCache.getCountries()) {
            countries.add(country.name);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item_right,
                countries);
        countrySpinner.setAdapter(adapter);
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
        selectedImages.add(new SelectedImage(index, ImageUtil.getRealPathFromUri(this, selectedImageUri)));
    }

    protected void selectPostImage(int index, String imagePath) {
        if (!StringUtils.isEmpty(imagePath)) {
            selectedImages.add(new SelectedImage(index, imagePath));

            ImageView imageView = postImages.get(index);
            Bitmap bp = ImageUtil.resizeAsPreviewThumbnail(imagePath);
            imageView.setImageBitmap(bp);
            //imageView.setImageURI(Uri.parse(imagePath);

            Log.d(this.getClass().getSimpleName(), "selectPostImage: index="+index+" imagePath="+imagePath);
        }
    }

    protected void removeSelectedPostImage(int index){
        SelectedImage toRemove = getSelectedPostImage(index);
        selectedImages.remove(toRemove);
        postImages.get(index).setImageDrawable(getResources().getDrawable(R.drawable.img_camera));
    }

    protected SelectedImage getSelectedPostImage(int index) {
        for (SelectedImage image : selectedImages) {
            if (image.index.equals(index)) {
                return image;
            }
        }
        return null;
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

        Long price = -1L;
        try {
            price = Long.valueOf(priceValue);
            if (price < 0) {
                Toast.makeText(this, getString(R.string.invalid_post_price_negative), Toast.LENGTH_SHORT).show();
                return null;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, getString(R.string.invalid_post_price_not_number), Toast.LENGTH_SHORT).show();
            return null;
        }

        if (conditionType == null) {
            Toast.makeText(this, getString(R.string.invalid_post_condition_empty), Toast.LENGTH_SHORT).show();
            return null;
        }

        if (catId == null) {
            initCategoryPopup();
            return null;
        }

        Long originalPrice = -1L;
        Boolean freeDelivery = false;
        String countryCode = "";
        if (AppController.isUserAdmin() ||
                UserInfoCache.getUser().isPromotedSeller() ||
                UserInfoCache.getUser().isVerifiedSeller()) {
            try {
                String originalPriceValue = originalPriceEdit.getText().toString().trim();
                originalPrice = Long.valueOf(originalPriceValue);
                if (price < 0) {
                    Toast.makeText(this, getString(R.string.invalid_post_price_negative), Toast.LENGTH_SHORT).show();
                    return null;
                } else if (originalPrice <= price) {
                    Toast.makeText(this, getString(R.string.invalid_post_original_price_less_than_price), Toast.LENGTH_SHORT).show();
                    return null;
                }
            } catch (NumberFormatException e) {
            }

            freeDelivery = freeDeliveryCheckBox.isChecked();
            if (freeDelivery == null) {
                freeDelivery = false;
            }

            if (country != null) {
                countryCode = country.code;
            }
        }

        NewPostVM newPost = new NewPostVM(
                catId, title, body, price, conditionType, selectedImages,
                originalPrice, freeDelivery, countryCode);
        return newPost;
    }

    protected void doPost() {
        if (selectedImages.size() == 0) {
            Toast.makeText(this, getString(R.string.invalid_post_no_photo), Toast.LENGTH_SHORT).show();
            return;
        }

        final NewPostVM newPost = getNewPost();
        if (newPost == null) {
            return;
        }

        ViewUtil.showSpinner(this);

        postAction.setEnabled(false);
        AppController.getApiService().newPost(newPost, new Callback<ResponseStatusVM>() {
            @Override
            public void success(ResponseStatusVM responseStatus, Response response) {
                /*
                if (UserInfoCache.getUser().isFbLogin()) {
                    boolean fbShare = fbSharingButton.isChecked();
                    if (fbShare) {
                        PostVM post = new PostVM();
                        post.id = responseStatus.objId;
                        post.price = newPost.getPrice();
                        post.body = newPost.getBody();
                        post.title = newPost.getTitle();
                        SharingUtil.shareToFacebook(post, NewPostActivity.this);
                    }
                    SharedPreferencesUtil.getInstance().setSharingFacebookWall(fbShare);
                }
                */

                UserInfoCache.incrementNumProducts();
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
        selectedImages.clear();

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
        NotificationCounter.refresh();
        finish();
    }

    protected void initCategoryLayout(CategoryVM category) {
        catId = category.id;
        catName.setText(category.getName());
        ImageUtil.displayImage(category.getIcon(), catIcon);
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
        if (resultCode == RESULT_OK) {
            if (selectedImages.size() >= DefaultValues.MAX_POST_IMAGES) {
                Toast.makeText(NewPostActivity.this,
                        String.format(NewPostActivity.this.getString(R.string.new_post_max_images), DefaultValues.MAX_POST_IMAGES), Toast.LENGTH_SHORT).show();
                return;
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

				Log.d(this.getClass().getSimpleName(), "onActivityResult: imagePath edit set=" + croppedImagePath);

				if(data.getData() != null) {
					try {
						Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
						selectedImageUri = data.getData();
						selectPostImage(bitmap, selectedPostImageIndex);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}else{
					selectPostImage(selectedPostImageIndex, croppedImagePath);
				}

            }

            // pop back soft keyboard
            ViewUtil.popupInputMethodWindow(this);
        
		}
    }

    @Override
    public void onBackPressed() {
        String title = titleEdit.getText().toString().trim();
        String desc = descEdit.getText().toString().trim();
        String price = priceEdit.getText().toString().trim();

        if (postSuccess ||
                (selectedImages.size() == 0 && StringUtils.isEmpty(title) && StringUtils.isEmpty(desc) && StringUtils.isEmpty(price))) {
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
