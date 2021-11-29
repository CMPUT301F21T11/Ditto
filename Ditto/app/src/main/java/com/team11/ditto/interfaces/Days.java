package com.team11.ditto.interfaces;

import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.NonNull;

import com.team11.ditto.R;

import java.util.ArrayList;
import java.util.Map;

//Specifically for dealing with Dates
public interface Days {
    //String keys for the respective dates
    String MON = "Monday";
    String TUES = "Tuesday";
    String WED = "Wednesday";
    String THURS = "Thursday";
    String FRI = "Friday";
    String SAT = "Saturday";
    String SUN = "Sunday";

    String[] WEEKDAYS = {MON, TUES, WED, THURS, FRI, SAT, SUN};

    int NUM_DAYS = 7;

    /**
     * Gets the checked dates from the checkboxes and puts them into the dates list
     * @param dates ArrayList of string date values that are being set
     * @param checkboxes checkboxes that determine day values
     */
    default void updateDayList(ArrayList<String> dates, ArrayList<CheckBox> checkboxes) {
        dates.clear();
        for (int i = 0; i < NUM_DAYS; i++) {
            if (checkboxes.get(i).isChecked()) {
                dates.add(WEEKDAYS[i]);
            }
        }
    }

    /**
     * Checks the respective checkboxes for the dates string passed through
     * @param dates pre-selected string dates in ArrayList
     * @param checkBoxes checkboxes to be checked or unchecked depending on dates
     */
    default void initializeCheckBoxes(ArrayList<String> dates, ArrayList<CheckBox> checkBoxes){

        for (int i = 0; i < NUM_DAYS; i++){
            checkBoxes.get(i).setChecked(false);
            if(dates.contains(WEEKDAYS[i])){
                checkBoxes.get(i).setChecked(true);
            }
        }

    }

    /**
     * Initializes the date checkboxes for a respective view
     * @param view current view being worked in
     * @return ArrayList of checkboxes with their set view ids
     */
    default ArrayList<CheckBox> setCheckBoxLayouts(@NonNull View view){

        ArrayList<CheckBox> checkBoxes = new ArrayList<>();
        checkBoxes.add(view.findViewById(R.id.monday_select));
        checkBoxes.add(view.findViewById(R.id.tuesday_select));
        checkBoxes.add(view.findViewById(R.id.wednesday_select));
        checkBoxes.add(view.findViewById(R.id.thursday_select));
        checkBoxes.add(view.findViewById(R.id.friday_select));
        checkBoxes.add(view.findViewById(R.id.saturday_select));
        checkBoxes.add(view.findViewById(R.id.sunday_select));

        return checkBoxes;
    }

    /**
     * Update a map to be passed into the database using the arraylist of date strings
     * @param dates dates to be passed through map
     * @param data map to be passed through to the database
     */
    default void updateDaysFromData(ArrayList<String> dates, Map<String, Object> data){

        dates.clear();
        for (int i = 0; i<7; i++){
            Object current = data.get(WEEKDAYS[i]);
            if ( current != null && (boolean) current){
                dates.add(WEEKDAYS[i]);
            }
        }
    }
}
