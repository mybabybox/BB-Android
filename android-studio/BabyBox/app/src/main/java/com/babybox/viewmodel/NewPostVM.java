package com.babybox.viewmodel;

import com.babybox.app.AppController;
import com.babybox.util.SelectedImage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit.mime.MultipartTypedOutput;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

public class NewPostVM {
    public Long postId;
    public Long catId;
    public String title, body;
    public double price;
    public Boolean withPhotos;
    public List<File> images;
    public String deviceType;

    public NewPostVM(Long catId, String title, String body, double price, List<SelectedImage> selectedPostImages) {
        this(-1L, catId, title, body, price, selectedPostImages);
    }

    public NewPostVM(Long postId, Long catId, String title, String body, double price, List<SelectedImage> selectedImages) {
        this.postId = postId;
        this.catId = catId;
        this.title = title;
        this.body = body;
        this.price = price;
        this.withPhotos = (selectedImages != null && selectedImages.size() > 0);
        this.images = new ArrayList<>();
        for (SelectedImage selectedImage : selectedImages) {
            this.images.add(selectedImage.getFile());
        }
        this.deviceType = AppController.DeviceType.ANDROID.name();
    }

    public MultipartTypedOutput toMultipart() {
        MultipartTypedOutput multipartTypedOutput = new MultipartTypedOutput();
        multipartTypedOutput.addPart("postId", new TypedString(postId+""));
        multipartTypedOutput.addPart("catId", new TypedString(catId+""));
        multipartTypedOutput.addPart("title", new TypedString(title));
        multipartTypedOutput.addPart("body", new TypedString(body));
        multipartTypedOutput.addPart("price", new TypedString(price+""));
        multipartTypedOutput.addPart("withPhotos", new TypedString(withPhotos.toString()));
        multipartTypedOutput.addPart("deviceType", new TypedString(Boolean.TRUE.toString()));

        int i = 0;
        for (File image : images) {
            //image = ImageUtil.resizeAsJPG(image);
            TypedFile typedFile = new TypedFile("application/octet-stream", image);
            multipartTypedOutput.addPart("image"+i, typedFile);
            i++;
        }
        return multipartTypedOutput;
    }
}
