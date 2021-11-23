/** Copyright [2021] [Reham Albakouni, Matt Asgari Motlagh, Aidan Horemans, Courtenay Laing-Kobe, Vivek Malhotra, Kelly Shih]

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

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.team11.ditto.R;
import com.team11.ditto.habit.EditHabitFragment;
import com.team11.ditto.habit.Habit;
import com.team11.ditto.habit_event.HabitEvent;
import com.team11.ditto.interfaces.HabitFirebase;

/**
 * Activity to view a Habit Event
 * @author Kelly Shih, Aidan Horemans
 */
public class ViewEventActivity extends AppCompatActivity implements EditEventFragment.OnFragmentInteractionListener, HabitFirebase {

    //Declarations
    HabitEvent habitEvent;
    TextView habitTitle;
    TextView habitComment;
    String title;
    String comment;
    Bundle eventBundle;
    private FirebaseFirestore database;


    /**
     * Instructions for creating the Activity
     * -display title & comment
     * @param savedInstanceState current app state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);
        habitTitle = findViewById(R.id.habit_title);
        habitComment = findViewById(R.id.habit_comment);

        //get the passed habit event
        habitEvent = (HabitEvent) getIntent().getSerializableExtra("EXTRA_HABIT_EVENT");

        //set title
        title = habitEvent.getHabitTitle();
        habitTitle.setText(title);

        //set comment
        comment = habitEvent.getComment();
        habitComment.setText(comment);

    }

    /**
     * Inflate the menu for the options menu
     * @param menu options menu
     * @return true when menu displayed, false otherwise
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.view_menu, menu);
        getSupportActionBar().setTitle("My Event");
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
            EditEventFragment dialogFragment = new EditEventFragment();

            //Creating bundle with selectedHabit
            eventBundle = new Bundle();
            eventBundle.putSerializable("EVENT", habitEvent);

            //Passing bundle to EditHabitFragment
            dialogFragment.setArguments(eventBundle);

            //Opening EditHabitFragment with the selectedHabit bundled
            dialogFragment.show(getSupportFragmentManager(), "EDIT_EVENT");
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onOkPressed(HabitEvent event) {
        //Update old habit event data with new habit event data

        pushEditEvent(database, event);

        //Updating old text with new habit stuff
        habitComment.setText(event.getComment());

    }
}