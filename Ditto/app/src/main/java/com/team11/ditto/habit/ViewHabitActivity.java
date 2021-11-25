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

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.team11.ditto.R;
import com.team11.ditto.interfaces.HabitFirebase;

import java.util.ArrayList;

/**
 * Role: An Activity to view the contents of a chosen Habit.
 * Allow user to edit Habit and return updated data back to this activity
 * TODO: Get updated photos and locations updating in the database
 * @author Kelly Shih, Aidan Horemans
 */
public class ViewHabitActivity extends AppCompatActivity
        implements EditHabitFragment.OnFragmentInteractionListener, HabitFirebase {

    TextView habitTitle; TextView habitReason; TextView habitDays;
    ArrayList<String> dates;
    Habit selectedHabit;
    Bundle habitBundle;
    final String TAG = "view";
    private FirebaseFirestore database;

    /**
     * Create the dialog with the fields for reason, dates and go to OnOkPressed method when user clicks "Add"
     * TODO: get fields for photos and location
     * @param savedInstanceState current app state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_habit);
        habitReason = findViewById(R.id.habit_reason);
        habitDays = findViewById(R.id.habit_days);
        database = FirebaseFirestore.getInstance();

        //Getting passed habit
        selectedHabit = (Habit) getIntent().getSerializableExtra("HABIT");

        //Setting title as habit title
        setTitle(selectedHabit.getTitle());

        //Setting habit_reason textview as habit reason
        habitReason.setText(selectedHabit.getReason());

        dates = selectedHabit.getDates();

        //Displaying dates in TextView
        habitDays.setText(listDays());
        habitTitle = findViewById(R.id.habit_tracking);
    }

    /**
     * Inflate the menu for the options menu
     * @param menu options menu
     * @return true when menu displayed, false otherwise
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.view_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Listener for the edit button
     * @param item selected item
     * @return true if displayed, false otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id == R.id.edit_habit){
            EditHabitFragment dialogFragment = new EditHabitFragment();

            //Creating bundle with selectedHabit
            habitBundle = new Bundle();
            habitBundle.putSerializable("HABIT", selectedHabit);

            //Passing bundle to EditHabitFragment
            dialogFragment.setArguments(habitBundle);

            //Opening EditHabitFragment with the selectedHabit bundled
            dialogFragment.show(getSupportFragmentManager(), "EDIT_HABIT");
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Updating a habit to the database and listview as the response to the user clicking the "Add" button from the fragment
     * @param habit habit to be updated
     */
    @Override
    public void onOkPressed(Habit habit) {

        //UPDATE THE OLD HABIT WITH THE NEW DATA

        //when the user clicks the add button, we want to add to the db and display the new entry
        Log.d(TAG, "dates -> "+ dates);

        pushEditData(database, habit);

        //Updating old text with new habit stuff
        habitReason.setText(habit.getReason());
        habitDays.setText(listDays());
    }

    private String listDays(){
        String listDays = "";
        if(dates != null){
            if(dates.size() > 0){
                for(int i = 0; i < dates.size(); i++){
                    listDays += dates.get(i) + " ";
                }
            }
        }
        return listDays;
    }

}