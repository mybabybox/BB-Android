package com.babybox.viewmodel;

import com.babybox.app.AppController;
import com.babybox.util.SelectedImage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit.mime.MultipartTypedOutput;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

public class NewMessageVM {
    public Long conversationId;
    public String body;
    public Boolean withPhotos;
    public List<File> images;
    public String deviceType;

    public NewMessageVM(Long conversationId, String body, List<SelectedImage> selectedImages) {
        this.conversationId = conversationId;
        this.body = body;
        this.withPhotos = (selectedImages != null && selectedImages.size() > 0);
        this.images = new ArrayList<>();
        for (SelectedImage selectedImage : selectedImages) {
            this.images.add(selectedImage.getFile());
        }
        this.deviceType = AppController.DeviceType.ANDROID.name();
    }

    public MultipartTypedOutput toMultipart() {
        MultipartTypedOutput multipartTypedOutput = new MultipartTypedOutput();
        multipartTypedOutput.addPart("conversationId", new TypedString(conversationId+""));
        multipartTypedOutput.addPart("body", new TypedString(body));
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
