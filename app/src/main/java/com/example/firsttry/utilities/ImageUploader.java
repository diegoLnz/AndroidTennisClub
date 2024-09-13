package com.example.firsttry.utilities;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.firsttry.enums.UploadRequestType;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.UploadTask;

public class ImageUploader
{
    private static final int PICK_IMAGE_REQUEST = 1;
    private final Activity activity;
    private Uri imageUri;
    private final UploadRequestType requestType;

    public ImageUploader(Activity activity, UploadRequestType requestType) {
        this.activity = activity;
        this.requestType = requestType;
    }

    public UploadTask uploadImageToFirebase(Uri imageUri, String imageName)
    {
        if (imageUri == null) {
            Toast.makeText(activity, "No file selected", Toast.LENGTH_SHORT).show();
            return null;
        }
        return StorageHandler.uploadImage(
                imageName,
                imageUri,
                requestType);
    }

    public static String getFileExtension(Uri uri, Fragment fragment) {
        ContentResolver cR = fragment.requireActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public static Task<Uri> getDownloadUrl(String relativePath)
    {
        return StorageHandler.getDownloadUrl(relativePath);
    }

    private Boolean ok(int requestCode,
                       int resultCode,
                       Intent data)
    {
        return requestCode == PICK_IMAGE_REQUEST
                && resultCode == Activity.RESULT_OK
                && data != null
                && data.getData() != null;
    }
}
