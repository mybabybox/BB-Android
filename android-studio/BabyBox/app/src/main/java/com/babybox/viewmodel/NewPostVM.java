package com.babybox.viewmodel;

import com.babybox.app.AppController;
import com.babybox.util.ImageUtil;

import java.io.File;
import java.util.List;

import retrofit.mime.MultipartTypedOutput;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

public class NewPostVM {
    public Long catId;
    public String title, body;
    public double price;
    public Boolean withPhotos;
    public List<File> images;
    public String deviceType;

    public NewPostVM(Long catId, String title, String body, double price, List<File> images) {
        this.catId = catId;
        this.title = title;
        this.body = body;
        this.price = price;
        this.withPhotos = (images != null && images.size() > 0);
        this.images = images;
        this.deviceType = AppController.DeviceType.ANDROID.name();
    }

    public MultipartTypedOutput toMultipart() {
        MultipartTypedOutput multipartTypedOutput = new MultipartTypedOutput();
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
