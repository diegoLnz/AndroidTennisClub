package com.example.firsttry.utilities;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.firsttry.enums.UploadRequestType;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class StorageHandler
{
    private static FirebaseStorage storage = FirebaseStorage.getInstance();
    private static StorageReference mStorageRef;

    private static void initReference(String path)
    {
        mStorageRef = storage.getReference(path);
    }

    public static UploadTask uploadImage(
            String imageName,
            Uri imagePath,
            UploadRequestType requestType)
    {
        initReference(requestType.toString().toLowerCase());
        StorageReference fileReference = mStorageRef.child(imageName);
        return fileReference.putFile(imagePath);
    }

    public static Task<Uri> getDownloadUrl(String relativePath)
    {
        initReference(relativePath);
        return mStorageRef.getDownloadUrl();
    }
}
