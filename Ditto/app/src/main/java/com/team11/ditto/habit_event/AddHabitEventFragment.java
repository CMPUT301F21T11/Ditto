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
package com.team11.ditto.habit_event;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.team11.ditto.LocationPicker;
import com.team11.ditto.R;
import com.team11.ditto.UserProfileActivity;
import com.team11.ditto.interfaces.FirebaseMedia;
import com.team11.ditto.interfaces.FirebaseMediaUploadCallback;
import com.team11.ditto.interfaces.HabitFirebase;
import com.team11.ditto.interfaces.MapHandler;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**Initialize a Dialog for the user to choose an EXISTING Habit from the database and add comment, dates for a new Habit Event.
 * Send input back to MainActivity and Firestore Database collection "HabitEvent", as well as update "Habit" collection
 * @author Kelly Shih, Aidan Horemans, Matt Asgari
 */
public class AddHabitEventFragment extends DialogFragment implements HabitFirebase, MapHandler, FirebaseMedia {
    //Declare necessary values
    private EditText hComment;
    private Button acc_photo;
    private Button locationButton;
    private ImageButton deletePhotoButton;
    private ImageButton deleteLocationButton;
    private OnFragmentInteractionListener listener;
    private FirebaseFirestore db;
    final String TAG = "dbs";
    private @Nullable ArrayList<Double> location = null;
    private String currentPhotoURL = "";

    private static final int MEDIA_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;

    //Declare interface
    public interface OnFragmentInteractionListener {
        void onOkPressed(HabitEvent newHabitEvent);
    }

    /**
     * Tells Android what to do when the Fragment attaches to the Activity
     * @param context: the context of the Activity
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (AddHabitEventFragment.OnFragmentInteractionListener) context;
    }

    /**
     * Create the dialog with the fields for habit (spinner), reason, dates, photos, location and go to OnOkPressed method when user clicks "Add"
     * @param savedInstanceState current state of the app
     * @return Dialog Fragment for user inputs
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        View view = LayoutInflater.from(getContext()).inflate(R.layout.add_event_fragment, null);
        hComment = view.findViewById(R.id.comment_editText);
        acc_photo = view.findViewById(R.id.add_photo);
        locationButton = view.findViewById(R.id.event_add_location_button);
        deletePhotoButton = view.findViewById(R.id.add_event_delete_photo);
        deleteLocationButton = view.findViewById(R.id.add_event_delete_location);
        db = FirebaseFirestore.getInstance();
        Spinner spinner = view.findViewById(R.id.event_spinner);
        final List<String> habits = new ArrayList<>();
        final List<String> habitIDs = new ArrayList<>();


        //get the documents from Habit
        getDocumentsHabit(db, habits, habitIDs, spinner, getActivity());

        final String[] hHabit = new String[1];
        final String[] IDhabit = new String[1];
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * to retrieve the habit and habit ID from the selected spinner choice
             * @param parent the adapter for the spinner
             * @param view the view selected
             * @param position the position of the view in the list
             * @param l view Id
             */
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                hHabit[0] = habits.get(position);
                IDhabit[0] = habitIDs.get(position);
                Log.d(TAG, "habit Id => "+IDhabit[0]);

            }

            /**
             * Do nothing if nothing selected
             * @param adapterView the listview adapter
             */
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        //Get camera permission for photo
        acc_photo.setOnClickListener(view1 -> {
            displayMediaOptions();
        });

        // Listen for when the location button is pressed
        locationButton.setOnClickListener(view1 -> {
            LocationPicker.callback = this;  // Really bad implementation - should be fixed
            Intent intent = new Intent(getActivity(), LocationPicker.class);
            startActivity(intent);
        });

        // Listen for when the delete photo button is pressed
        deletePhotoButton.setOnClickListener(view1 -> {
            currentPhotoURL = "";
            acc_photo.setText(R.string.add_photo);
        });

        // Listen for when the delete location button is pressed
        deleteLocationButton.setOnClickListener(view1 -> {
            location = null;
            locationButton.setText(R.string.add_location);
        });

        //Builds the Dialog for the user to add a new habit event
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Add Habit Event")
                .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    /**
                     * Create a new Habit Event object when the user clicks the add button with inputted data
                     * @param dialogInterface Android default input
                     * @param i Android default input
                     */
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String comment = hComment.getText().toString();
                        String photoURL = currentPhotoURL;

                        listener.onOkPressed(new HabitEvent(IDhabit[0], comment, photoURL, location, hHabit[0]));

                    }
                })
                .setNegativeButton("Cancel", null)
                .create();

    }

    /**
     * Handle changing a location
     * @param location
     */
    @Override
    public void handleLocationChange(@Nullable LatLng location) {
        if (location == null) {
            this.location = null;
            this.locationButton.setText("Add location");
        } else {
            this.location = new ArrayList<>();
            this.location.add(location.latitude);
            this.location.add(location.longitude);
            this.locationButton.setText("Update location");
        }
    }

    private void displayMediaOptions() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity())
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
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // Display camera
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        } else {
            // Display permission request
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        }
    }

    private void loadPhotos() {
        // Check if app has permission
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // Show media library
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, MEDIA_REQUEST_CODE);
        } else {
            // Display permission request
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, MEDIA_REQUEST_CODE);
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
                    Snackbar.make(getView(), "Ditto does not have camera access", Snackbar.LENGTH_SHORT).setBackgroundTint(getResources().getColor(R.color.error)).show();
                }
                break;

            case MEDIA_REQUEST_CODE:
                if (grantResults.length >= 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted
                    loadPhotos();
                } else {
                    // Display error
                    Snackbar.make(getView(), "Ditto does not have photo access", Snackbar.LENGTH_SHORT).setBackgroundTint(getResources().getColor(R.color.error)).show();
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
                ImageDecoder.Source source = ImageDecoder.createSource(this.getActivity().getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                // support older versions of Android by using getBitmap
                image = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    @Override
     public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            uploadEventPhoto(photo, new FirebaseMediaUploadCallback() {
                @Override
                public void imageURIChanged(Uri uri) {
                    currentPhotoURL = uri.toString();
                    acc_photo.setText("Update photo");
                }
            });
        } else if (requestCode == MEDIA_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            Uri photoUri = data.getData();
            Bitmap photo = loadFromUri(photoUri);
            uploadEventPhoto(photo, new FirebaseMediaUploadCallback() {
                @Override
                public void imageURIChanged(Uri uri) {
                    currentPhotoURL = uri.toString();
                    acc_photo.setText("Update photo");
                }
            });
        }
    }
}
