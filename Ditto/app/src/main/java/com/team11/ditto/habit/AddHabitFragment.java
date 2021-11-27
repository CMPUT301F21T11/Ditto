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
package com.team11.ditto.habit;

/*
Role: Initialize a Dialog for the user to input a title, reason, dates for a new Habit.
Send input back to MyHabitActivity and Firestore Database collection "Habit"
TODO Needs work on the visual aspect (to be done in xml)
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.team11.ditto.R;
import com.team11.ditto.interfaces.Days;

import java.util.ArrayList;
import java.util.Collections;

/**
 * This is a class that initializes a Dialog for the user to input a title, reason, dates for a new Habit.
 * Sends input back to MyHabitActivity and Firestore Database collection "Habit"
 * TODO: Needs work on the visual aspect (to be done in xml)
 * @author Kelly Shih, Aidan Horemans
 */
public class AddHabitFragment extends DialogFragment implements Days {
    //Declare views & interaction listener
    private EditText hTitle;
    private EditText hReason;
    private ArrayList<String> dates;
    private OnFragmentInteractionListener listener;
    private SwitchMaterial privacySwitch;
    private ArrayList<CheckBox> checkBoxes;

    //Declare interface
    public interface OnFragmentInteractionListener {
        void onOkPressed(Habit newHabit);
    }

    /**
     * Tells Android what to do when the Fragment is attached to the Activity
     * @param context the Activity context
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (OnFragmentInteractionListener) context;
    }

    /**
     * Create the dialog with the fields for title, reason, dates, and go to OnOkPressed method when user clicks "Add"
     * @param savedInstanceState current App state
     * @return Dialog for user creation of a new habit
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        View view = LayoutInflater.from(getContext()).inflate(R.layout.add_habit_fragment,null);
        hTitle = view.findViewById(R.id.title_editText);
        hReason = view.findViewById(R.id.reason_editText);
        dates = new ArrayList<>();
        privacySwitch = view.findViewById(R.id.privacySwitch);
        privacySwitch.setChecked(true);
        checkBoxes = setCheckBoxLayouts(view);

        //hDate = view.findViewById(R.id.date_editText);

        //Builds the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Add Habit")
                .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    /**
                     * On clicking the "add" button, create a new Habit object with the new data inputted by the user
                     * @param dialogInterface Android default
                     * @param i Android default
                     */
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String title = hTitle.getText().toString();
                        String reason = hReason.getText().toString();
                        updateDayList(dates, checkBoxes);
                        Collections.sort(dates);
                        boolean isPublic = privacySwitch.isChecked();
                        listener.onOkPressed(new Habit(title, reason, dates, isPublic));
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
    }

}
