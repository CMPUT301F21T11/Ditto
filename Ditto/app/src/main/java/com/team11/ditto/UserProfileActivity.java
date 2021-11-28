/* Copyright [2021] [Reham Albakouni, Matt Asgari Motlagh, Aidan Horemans, Courtenay Laing-Kobe, Vivek Malhotra, Kelly Shih]

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package com.team11.ditto;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.team11.ditto.follow.FollowRequestActivity;
import com.team11.ditto.follow.FollowerActivity;
import com.team11.ditto.follow.FollowingActivity;
import com.team11.ditto.follow.SentRequestActivity;
import com.team11.ditto.interfaces.Firebase;
import com.team11.ditto.interfaces.FirebaseMedia;
import com.team11.ditto.interfaces.SwitchTabs;
import com.team11.ditto.login.ActiveUser;
import com.team11.ditto.profile_details.SearchUserActivity;

import java.io.FileNotFoundException;
import java.io.IOException;

public class UserProfileActivity extends AppCompatActivity implements SwitchTabs, Firebase, FirebaseMedia {

    private ImageView profilePhoto;
    private TextView followers;
    private TextView no_followers;
    private TextView following;
    private TextView no_following;
    private TextView username;
    private Button search;
    private Button fr_pending;
    private Button logout;
    private static final String TAG = "UserProfileActivity";
    private Button frSent;


    private ActiveUser currentUser;

    private static final int MEDIA_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;

    FirebaseFirestore db; //when they add button we need to dump into db
    private TabLayout tabLayout;
    //public static Bundle habitBundle = new Bundle();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(0,0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);

        db = FirebaseFirestore.getInstance();
        currentUser = new ActiveUser();

        tabLayout = findViewById(R.id.tabs);
        profilePhoto =findViewById(R.id.profilePhoto);
        followers = findViewById(R.id.followers);
        following = findViewById(R.id.following);
        no_following = findViewById(R.id.no_following_1);
        no_followers = findViewById(R.id.no_followers);
        search = findViewById(R.id.search_users);
        username = findViewById(R.id.username_editText);
        fr_pending = findViewById(R.id.pending_fr);
        logout = findViewById(R.id.logout_button);
        frSent = findViewById(R.id.follow_request_sent);

        currentTab(tabLayout, PROFILE_TAB);
        switchTabs(this, tabLayout, PROFILE_TAB);


        // Get the current user's followers
        db.collection("Following")
            .whereEqualTo("followed", currentUser.getEmail())
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    no_followers.setText((String.valueOf(task.getResult().getDocuments().size())));
                } else {
                    Log.w(TAG, "UserProfileActivity - could not fetch followers");
                }
            });

        // Get the accounts the current user is following
        db.collection("Following")
            .whereEqualTo("followedBy", currentUser.getEmail())
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    no_following.setText((String.valueOf(task.getResult().getDocuments().size())));
                } else {
                    Log.w(TAG, "UserProfileActivity - could not fetch following");
                }
            });
        username.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        setProfilePhoto(FirebaseAuth.getInstance().getUid(), profilePhoto);

        onFollowingTap();
        onFollowNumberTap();
        onSearchTap();
        onFollowRequestTab();
        onFollowerTap();
        onFollowNumberTap();
        onSentRequestTap();
        onFollowerNumberTap();
        onProfilePhotoTap();

        logout.setOnClickListener(view -> {
            new ActiveUser().logout();
            Intent intent = new Intent(UserProfileActivity.this, WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |  Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |  Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void onFollowingTap(){
        following.setOnClickListener(view -> {
            Intent intent = new Intent(UserProfileActivity.this, FollowingActivity.class);
            startActivity(intent);
        });

    }

    public void onFollowNumberTap(){
        no_following.setOnClickListener(view -> {
            Intent intent = new Intent(UserProfileActivity.this,FollowingActivity.class);
            startActivity(intent);
        });
    }

    public void onSearchTap(){
        search.setOnClickListener(view -> {
            Intent intent = new Intent(UserProfileActivity.this, SearchUserActivity.class);
            startActivity(intent);
        });
    }

    public void onFollowRequestTab(){
        fr_pending.setOnClickListener(view -> {
            Intent intent = new Intent(UserProfileActivity.this, FollowRequestActivity.class);
            startActivity(intent);
        });
    }

    public void onFollowerTap() {
        followers.setOnClickListener(view -> {
            Intent intent = new Intent(UserProfileActivity.this, FollowerActivity.class);
            startActivity(intent);
        });
    }

    public void onFollowerNumberTap(){
        no_followers.setOnClickListener(view -> {
            Intent intent = new Intent(UserProfileActivity.this, FollowerActivity.class);
            startActivity(intent);
        });
    }

    public void onSentRequestTap(){
        frSent.setOnClickListener(view -> {
            Intent intent = new Intent(UserProfileActivity.this, SentRequestActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onPause(){
        overridePendingTransition(0,0);
        super.onPause();
    }

    public void onProfilePhotoTap() {
        profilePhoto.setOnClickListener(view -> {
            displayMediaOptions();
        });
    }

    private void displayMediaOptions() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(UserProfileActivity.this)
                .setTitle("Load photo from camera or media")
                .setMessage("Please select where you will load the photo from")
                .setNeutralButton("Cancel", null)
                .setNegativeButton("Media", (dialogInterface, i) -> {
                    loadPhotos();
                })
                .setPositiveButton("Camera", (dialogInterface, i) -> {
                    loadCamera();
                });
        builder.show();
    }

    private void loadCamera() {
        // Check if app has permission
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // Display camera
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        } else {
            // Display permission request
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        }
    }

    private void loadPhotos() {
        // Check if app has permission
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // Show media library
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, MEDIA_REQUEST_CODE);
        } else {
            // Display permission request
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, MEDIA_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Determine which request code was granted or denied
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length >= 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted
                    loadCamera();
                } else {
                    // Display error
                    Snackbar.make(findViewById(R.id.user_profile_constraint_layout), "Ditto does not have camera access", Snackbar.LENGTH_SHORT).setBackgroundTint(getResources().getColor(R.color.error)).show();
                }
                break;

            case MEDIA_REQUEST_CODE:
                if (grantResults.length >= 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted
                    loadPhotos();
                } else {
                    // Display error
                    Snackbar.make(findViewById(R.id.user_profile_constraint_layout), "Ditto does not have photo access", Snackbar.LENGTH_SHORT).setBackgroundTint(getResources().getColor(R.color.error)).show();
                }
                break;
        }
    }

    private Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;
        try {
            // check version of Android on device
            if(Build.VERSION.SDK_INT > 27){
                // on newer versions of Android, use the new decodeBitmap method
                ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                // support older versions of Android by using getBitmap
                image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            profilePhoto.setImageBitmap(photo);
            uploadProfilePhoto(FirebaseAuth.getInstance().getUid(), photo);
        } else if (requestCode == MEDIA_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            Uri photoUri = data.getData();
            Bitmap photo = loadFromUri(photoUri);
            profilePhoto.setImageBitmap(photo);
            uploadProfilePhoto(FirebaseAuth.getInstance().getUid(), photo);
        }
    }

}
