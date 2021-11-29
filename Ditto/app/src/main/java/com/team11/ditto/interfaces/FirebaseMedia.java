package com.team11.ditto.interfaces;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

/**
 * Role: Update the respective media values from firebase
 * This includes setting, getting and updating the user profile picture
 * @author Matthew Asgari
 */
public interface FirebaseMedia {

    /**
     * Sets the user profile picture
     * @param uid user id
     * @param imageView imageview to set
     */
    default void setProfilePhoto(String uid, ImageView imageView) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Get the profile photo
        StorageReference pathReference = storageRef.child("profile_photos/"+uid+"/photo.jpg");
        pathReference.getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                imageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Photo does not exist, download default
                setDefaultProfilePhoto(imageView);
            }
        });

    }

    default void setImage(String url, ImageView imageView) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(url);

        // Get the profile photo
        storageRef.getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                imageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            }
        });
    }

    /**
     * Sets the user's profile photo to a default value when none is set
     * @param imageView imageview to set
     */
    default void setDefaultProfilePhoto(ImageView imageView) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Get the profile photo
        StorageReference pathReference = storageRef.child("profile_photos/default.jpg");
        pathReference.getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                imageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            }
        });
    }

    /**
     * Uploads a profile photo for a given uid
     * @param uid user id
     * @param imgBitmap bitmap value to store the passed image by the user
     */
    default void uploadProfilePhoto(String uid, Bitmap imgBitmap) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imgRef = storageRef.child("profile_photos/"+uid+"/photo.jpg");

        // Upload photo
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imgBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imgRef.putBytes(data);
    }

    // Uploads an image for a habit event and returns the url
    default void uploadEventPhoto(Bitmap imgBitmap, FirebaseMediaUploadCallback callback) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imgRef = storageRef.child("event_photos/"+ UUID.randomUUID().toString() +".jpg");

        // Upload photo
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imgBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        imgRef.putBytes(data).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        callback.imageURIChanged(task1.getResult());
                    }
                });
            }
        });
    }


}
