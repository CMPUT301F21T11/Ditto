package com.team11.ditto.habit_event;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.team11.ditto.R;
import com.team11.ditto.habit.EditHabitFragment;
import com.team11.ditto.habit.Habit;
import com.team11.ditto.interfaces.Days;

import java.util.ArrayList;
import java.util.List;

/**
 * Role: Handle events to edit a habit event comment, photo, location, dates
 * @author Kelly Shih, Aidan Horemans
 */
public class EditEventFragment extends DialogFragment implements Days {
    private EditText Comment;
    private HabitEvent selectedEvent;
    private Button updateLocationButton;
    private Button updatePhotoButton;
    private ImageButton deleteLocationButton;
    private ImageButton deletePhotoButton;
    private EditEventFragment.OnFragmentInteractionListener listener;

    private @Nullable List<Double> location = null;
    private String currentPhotoURL = "";

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


}
