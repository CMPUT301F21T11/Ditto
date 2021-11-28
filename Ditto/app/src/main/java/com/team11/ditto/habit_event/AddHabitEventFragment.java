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
/*
Role: Initialize a Dialog for the user to choose an EXISTING Habit from the database, comment, photo, location for a new Habit Event.
Send input back to MainActivity and Firestore Database collection "HabitEvent", as well as update "Habit" collection
Goals:
    -To create restraints on user i.e. warn user to pick a Habit before pressing Add
    -To not make the first choice "Go eat ramen" for the choices of habits
    -Get Camera and photo library permission
    -Get location option
    -Make it better visually (xml)
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.team11.ditto.R;
import com.team11.ditto.interfaces.HabitFirebase;

import java.util.ArrayList;
import java.util.List;

/**Initialize a Dialog for the user to choose an EXISTING Habit from the database and add comment, dates for a new Habit Event.
 * Send input back to MainActivity and Firestore Database collection "HabitEvent", as well as update "Habit" collection
 * TODO: allow user to add photo and location
 * @author Kelly Shih, Aidan Horemans
 */
public class AddHabitEventFragment extends DialogFragment implements HabitFirebase {
    //Declare necessary values
    private EditText hComment;
    private Button acc_photo;
    private Button locationButton;
    private OnFragmentInteractionListener listener;
    private FirebaseFirestore db;
    final String TAG = "dbs";

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
     * Create the dialog with the fields for habit (spinner), reason, dates and go to OnOkPressed method when user clicks "Add"
     * TODO: photo and location addition
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

        //TODO Get camera permission for photo
        acc_photo.setOnClickListener(view1 -> {
        });

        // Listen for when the location button is pressed
        locationButton.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), LocationPicker.class);
            startActivity(intent);
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

                        //TODO If user doesn't choose a Habit...
                        /*
                        if (hHabit.equals("Habit")) {
                            hHabit = "";
                        }
                         */

                        //set photo and location
                        String photo = "";
                        String location = "";

                        listener.onOkPressed(new HabitEvent(IDhabit[0], comment, photo, location, hHabit[0]));

                    }
                })
                .setNegativeButton("Cancel", null)
                .create();

    }
}
