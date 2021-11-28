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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.team11.ditto.R;
import com.team11.ditto.interfaces.Days;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Role: Initialize a Dialog for the user to edit an EXISTING Habit Event
 * editable -> reason, dates
 *  Send input back to ViewHabitActivity and Firestore Database collection "Habit"
 *  TODO: get the photo and location to be editable
 *  @author Kelly Shih, Aidan Horemans
 */
public class EditHabitFragment extends DialogFragment implements Days {
    private EditText hReason;
    private ArrayList<String> dates;
    private Habit selectedHabit;
    private EditHabitFragment.OnFragmentInteractionListener listener;
    private SwitchMaterial privacySwitch;
    private ArrayList<CheckBox> checkBoxes;

    public interface OnFragmentInteractionListener {
        void onOkPressed(Habit habit);
    }

    /**
     * Instructions for what to do when Fragment attaches
     * -set listener for interaction
     * @param context activity context
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (EditHabitFragment.OnFragmentInteractionListener) context;
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
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_edit_habit,null);
        //Declarations
        TextView hTitle = view.findViewById(R.id.title_textView);
        hReason = view.findViewById(R.id.reason_editText);
        privacySwitch = view.findViewById(R.id.privacySwitchEdit);
        checkBoxes = setCheckBoxLayouts(view);

        //Get and handle Habit from bundle if there is one
        Bundle bundle = getArguments();
        if (bundle != null) {
            selectedHabit = (Habit) bundle.getSerializable("HABIT");
            hTitle.setText(selectedHabit.getTitle());
            hReason.setText(selectedHabit.getReason());
            dates = selectedHabit.getDates();
            for(String date : dates){
                Log.d("days", date);
            }
            privacySwitch.setChecked(selectedHabit.isPublic());

            //Setting the checkboxes depending on pre-selected days
            initializeCheckBoxes(dates, checkBoxes);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Edit Habit")
                .setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                    /**
                     * On clicking the "add" button, edit the pre-existing Habit object with the new data inputted by the user
                     * @param dialogInterface Android default
                     * @param i Android default
                     */
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (privacySwitch.isChecked() != selectedHabit.isPublic()){
                            selectedHabit.changePrivacy();
                        }
                        String reason = hReason.getText().toString();
                        updateDayList(dates, checkBoxes);

                        selectedHabit.setReason(reason);
                        selectedHabit.setDate(dates);

                        listener.onOkPressed(selectedHabit);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
    }

}
