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
/*
Role: Class for Habit Event Activity, be able to see you feed and add a habit event
Goals:
    there is repetition between MyHabitActivity and the Homepage when creating fragments and listviews
    solve by making a more object oriented design
*/

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.team11.ditto.habit_event.AddHabitEventFragment;
import com.team11.ditto.habit_event.HabitEvent;
import com.team11.ditto.habit_event.HabitEventRecyclerAdapter;
import com.team11.ditto.habit_event.ViewEventActivity;
import com.team11.ditto.interfaces.FollowFirebase;
import com.team11.ditto.interfaces.HabitFirebase;
import com.team11.ditto.interfaces.SwitchTabs;
import com.team11.ditto.login.ActiveUser;
import com.team11.ditto.profile_details.User;

import java.util.ArrayList;
import java.util.Calendar;


/**
 * Role: Class for Habit Event Activity, be able to see you feed and add a habit event
 * TODO:
 *     there is repetition between MyHabitActivity and the Homepage when creating fragments and listviews
 *     solve by making a more object oriented design
 * @author: Kelly Shih, Aidan Horemans, Vivek Malhotra, Matthew Asgari
 */

public class MainActivity extends AppCompatActivity implements SwitchTabs,
        AddHabitEventFragment.OnFragmentInteractionListener, HabitFirebase, FollowFirebase,
        HabitEventRecyclerAdapter.EventClickListener {

    private TabLayout tabLayout;
    public static String EXTRA_HABIT_EVENT = "EXTRA_HABIT_EVENT";
    private ArrayList<HabitEvent> habitEventsData;
    private ArrayList<String> idList;

    private RecyclerView habitEventList;
    private HabitEventRecyclerAdapter habitEventRecyclerAdapter;

    private FirebaseFirestore db;

    /**
     * Create the Activity instance for the "Homepage" screen, control flow of actions
     * @param savedInstanceState saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(0,0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // If device has userID, go to app - else, go to login
        if (new ActiveUser().getUID().equals("")) {
            Intent intent = new Intent(this, WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |  Intent.FLAG_ACTIVITY_CLEAR_TASK);
            this.startActivity(intent);
        }

        tabLayout = findViewById(R.id.tabs);

        setTitle("My Feed");

        habitEventList = findViewById(R.id.list_habit_event);
        habitEventsData = new ArrayList<>();
        idList = new ArrayList<>();
        idList.add(new ActiveUser().getUID());
        getFollowedByActiveUser(db, new ActiveUser(), idList);

        habitEventRecyclerAdapter = new HabitEventRecyclerAdapter(this, habitEventsData, this);

        // Load the Habit Event data (This will be converted to use the Firebase interface in the future)
        db = FirebaseFirestore.getInstance();
        for (int i = 0; i < idList.size(); i++){
            try {
                db.collection(HABIT_EVENT_KEY)
                        .whereEqualTo(USER_ID, idList.get(i))
                        .addSnapshotListener((value, error) -> {
                            habitEventsData.clear();
                            for (QueryDocumentSnapshot doc : value) {
                                // Parse the event data for each document
                                String eventID = (String) doc.getId();
                                String eHabitId = (String) doc.getData().get("habitID");
                                String eHabitTitle = (String) doc.getData().get("habitTitle");
                                String eComment = (String) doc.getData().get("comment");
                                String ePhoto = (String) doc.getData().get("photo");
                                String eLocation = (String) doc.getData().get("location");
                                String uid = (String) doc.getData().get("uid");
                                habitEventsData.add(new HabitEvent(eventID, eHabitId, eComment, ePhoto, eLocation, eHabitTitle, uid));  // Add the event to the event list
                            }
                            habitEventRecyclerAdapter.notifyDataSetChanged();  // Refresh the recycler
                        });
            }
            catch (NullPointerException skip){
                continue;
            }
        }

        LinearLayoutManager manager = new LinearLayoutManager(this);
        habitEventList.setLayoutManager(manager);
        habitEventList.setAdapter(habitEventRecyclerAdapter);

        currentTab(tabLayout, HOME_TAB);
        switchTabs(this, tabLayout, HOME_TAB);
        db = FirebaseFirestore.getInstance();

        //Get a top level reference to the collection

        //Notifies if cloud data changes (from Firebase Interface)
        autoHabitEventListener(db, habitEventRecyclerAdapter);

        final FloatingActionButton addHabitEventButton = findViewById(R.id.add_habit_event);

        addHabitEventButton.setOnClickListener(view -> new AddHabitEventFragment()
                .show(getSupportFragmentManager(), "ADD_HABIT_EVENT"));
    }



    /**
     * Adds a habitevent to firestore "HabitEvent" and adds the habitevent ID to the list of habitEvents for the habit in "Habit"
     * Adds the habitevent to the listview
     * @param newHabitEvent habit event to add
     */
    @Override
    public void onOkPressed(HabitEvent newHabitEvent) {
        //Adds the item to the database and then immediately retrieves it from the list

        //if today is the same day as one of the dates they picked,
        //AND in this selected day if there are no other habit events with the same habit
        //THEN set habitDoneToday to true

        isHabitDoneToday(db, todayIs(), newHabitEvent);
        pushHabitEventData(db, newHabitEvent);
        habitEventRecyclerAdapter.notifyDataSetChanged();



    }

    /**
     * Handles the view Habit event activity
     * starts a new activity to view the clicked habit event
     * @param position of the clicked habit event
     */
    @Override
    public void onEventClick(int position) {
        Intent intent = new Intent(this, ViewEventActivity.class);
        intent.putExtra(EXTRA_HABIT_EVENT, habitEventsData.get(position));
        startActivity(intent);
    }

    @Override
    public void onPause(){
        overridePendingTransition(0,0);
        super.onPause();
    }

    private int todayIs() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        switch (day) {
            case Calendar.SUNDAY:
                day = 1;
                break;
            case Calendar.MONDAY:
                day = 2;
                break;
            case Calendar.TUESDAY:
                day = 3;
                break;
            case Calendar.WEDNESDAY:
                day = 4;
                break;
            case Calendar.THURSDAY:
                day = 5;
                break;
            case Calendar.FRIDAY:
                day = 6;
                break;
            case Calendar.SATURDAY:
                day = 7;
                break;
        }
        return day;
    }

}