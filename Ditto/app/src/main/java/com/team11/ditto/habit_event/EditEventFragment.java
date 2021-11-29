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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.team11.ditto.LocationPicker;
import com.team11.ditto.R;
import com.team11.ditto.habit.EditHabitFragment;
import com.team11.ditto.habit.Habit;
import com.team11.ditto.interfaces.Days;
import com.team11.ditto.interfaces.FirebaseMedia;
import com.team11.ditto.interfaces.FirebaseMediaUploadCallback;
import com.team11.ditto.interfaces.MapHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Role: Handle events to edit a habit event comment, photo, location, dates
 * @author Kelly Shih, Aidan Horemans
 */
public class EditEventFragment extends DialogFragment implements Days, MapHandler, FirebaseMedia {
    private EditText Comment;
    private HabitEvent selectedEvent;
    private Button updateLocationButton;
    private Button updatePhotoButton;
    private ImageButton deleteLocationButton;
    private ImageButton deletePhotoButton;
    private EditEventFragment.OnFragmentInteractionListener listener;

    private @Nullable List<Double> location = null;
    private String currentPhotoURL = "";

    private static final int MEDIA_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;

    public interface OnFragmentInteractionListener {
        void onOkPressed(HabitEvent habit);
    }

    /**
     * Instructions for what to do when Fragment attaches
     * -set listener for interaction
     * @param context activity context
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (EditEventFragment.OnFragmentInteractionListener) context;
    }


    /**
     * Create the dialog with the edit fields for reason, dates, and go to OnOkPressed method when user clicks "Add"
     * @param savedInstanceState app state
     * @return Dialog
     */

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_edit_event,null);
        //Declarations
        TextView hTitle = view.findViewById(R.id.title_textView);
        Comment = view.findViewById(R.id.comment_editText);
        updateLocationButton = view.findViewById(R.id.edit_loc_btn);
        updatePhotoButton = view.findViewById(R.id.edit_photo_btn);
        deleteLocationButton = view.findViewById(R.id.edit_event_delete_location_button);
        deletePhotoButton = view.findViewById(R.id.edit_event_delete_photo_button);

        //Get and handle Habit from bundle if there is one
        Bundle bundle = getArguments();
        if (bundle != null) {
            selectedEvent = (HabitEvent) bundle.getSerializable("EVENT");
            hTitle.setText(selectedEvent.getHabitTitle());
            Comment.setText(selectedEvent.getComment());
            location = selectedEvent.getLocation();
            currentPhotoURL = selectedEvent.getPhoto();
        }

        if (location != null && location.size() == 2) {
            updateLocationButton.setText(R.string.update_location);
        }

        if (!currentPhotoURL.equals("")) {
            updatePhotoButton.setText(R.string.update_photo);
        }

        //Get camera permission for photo
        updatePhotoButton.setOnClickListener(view1 -> {
            displayMediaOptions();
        });

        // Listen for when the location button is pressed
        updateLocationButton.setOnClickListener(view1 -> {
            LocationPicker.callback = this;  // Really bad implementation - should be fixed
            Intent intent = new Intent(getActivity(), LocationPicker.class);
            startActivity(intent);
        });

        // Listen for when the delete photo button is pressed
        deletePhotoButton.setOnClickListener(view1 -> {
            currentPhotoURL = "";
            updatePhotoButton.setText(R.string.add_photo);
        });

        // Listen for when the delete location button is pressed
        deleteLocationButton.setOnClickListener(view1 -> {
            location = null;
            updateLocationButton.setText(R.string.add_location);
        });



        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Edit Habit Event")
                .setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                    /**
                     * On clicking the "add" button, edit the pre-existing Habit object with the new data inputted by the user
                     * @param dialogInterface Android default
                     * @param i Android default
                     */

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String updatedComment = Comment.getText().toString();

                        selectedEvent.setComment(updatedComment);
                        selectedEvent.setPhoto(currentPhotoURL);
                        selectedEvent.setLocation(location);

                        listener.onOkPressed(selectedEvent);
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
            this.updateLocationButton.setText(R.string.add_location);
        } else {
            this.location = new ArrayList<>();
            this.location.add(location.latitude);
            this.location.add(location.longitude);
            this.updateLocationButton.setText(R.string.update_location);
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
                    updatePhotoButton.setText(R.string.update_photo);
                }
            });
        } else if (requestCode == MEDIA_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            Uri photoUri = data.getData();
            Bitmap photo = loadFromUri(photoUri);
            uploadEventPhoto(photo, new FirebaseMediaUploadCallback() {
                @Override
                public void imageURIChanged(Uri uri) {
                    currentPhotoURL = uri.toString();
                    updatePhotoButton.setText(R.string.update_photo);
                }
            });
        }
    }


}
