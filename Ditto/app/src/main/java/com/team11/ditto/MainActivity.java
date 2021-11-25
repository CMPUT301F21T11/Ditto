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
package com.team11.ditto;
/*
Role: Class for Habit Event Activity, be able to see you feed and add a habit event
Goals:
    there is repetition between MyHabitActivity and the Homepage when creating fragments and listviews
    solve by making a more object oriented design
*/

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.team11.ditto.habit.Habit;
import com.team11.ditto.habit_event.AddHabitEventFragment;
import com.team11.ditto.habit_event.HabitEvent;
import com.team11.ditto.habit_event.HabitEventRecyclerAdapter;
import com.team11.ditto.habit_event.ViewEventActivity;
import com.team11.ditto.interfaces.Firebase;
import com.team11.ditto.interfaces.HabitFirebase;
import com.team11.ditto.interfaces.SwitchTabs;
import com.team11.ditto.login.ActiveUser;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Role: Class for Habit Event Activity, be able to see you feed and add a habit event
 * TODO:
 *     there is repetition between MyHabitActivity and the Homepage when creating fragments and listviews
 *     solve by making a more object oriented design
 * @author: Kelly Shih, Aidan Horemans, Vivek Malhotra, Matthew Asgari
 */

public class MainActivity extends AppCompatActivity implements SwitchTabs,
        AddHabitEventFragment.OnFragmentInteractionListener, HabitFirebase,
        HabitEventRecyclerAdapter.EventClickListener {
    private static final String TAG = "tab switch";
    private TabLayout tabLayout;
    public static String EXTRA_HABIT_EVENT = "EXTRA_HABIT_EVENT";
    private ArrayList<HabitEvent> habitEventsData;

    private RecyclerView habitEventList;
    private HabitEventRecyclerAdapter habitEventRecyclerAdapter;

    private FirebaseFirestore db;
    HashMap<String, Object> data = new HashMap<>();

    private ActiveUser activeUser;

    /**
     * Create the Activity instance for the "Homepage" screen, control flow of actions
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        habitEventRecyclerAdapter = new HabitEventRecyclerAdapter(this, habitEventsData, this);

        // Load the Habit Event data (This will be converted to use the Firebase interface in the future)
        db = FirebaseFirestore.getInstance();
        db.collection(HABIT_EVENT_KEY)
                .whereEqualTo("uid", FirebaseAuth.getInstance().getUid())  // Query only current user events for now
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        habitEventsData.clear();
                        for (QueryDocumentSnapshot doc: value) {
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
                    }
                });

        LinearLayoutManager manager = new LinearLayoutManager(this);
        habitEventList.setLayoutManager(manager);
        habitEventList.setAdapter(habitEventRecyclerAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(habitEventList.getContext(), manager.getOrientation());

        habitEventList.addItemDecoration(dividerItemDecoration);

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
     * @param newHabitEvent
     */
    @Override
    public void onOkPressed(HabitEvent newHabitEvent) {
        //Adds the item to the database and then immediately retrieves it from the list

        //if today is the same day as one of the dates they picked,
        //AND in this selected day if there are no other habit events with the same habit
        //THEN set habitDoneToday to true

        int today = todayIs(); //today's day of the week

        //get days ...
        final Integer[] daysOfWeek = new Integer[7];

        DocumentReference document = db.collection(HABIT_KEY).document(newHabitEvent.getHabitId());
        document.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        //retrieve the order value
                        int numDays = 7;
                        for (int i=0;i<numDays;i++) {
                            Boolean isDay; //is the day "true" in for this Habit
                            String day = daysForHabit(i);
                            isDay = documentSnapshot.getBoolean(day);
                            if (isDay==true) { daysOfWeek[i] = i+1; }
                            else { daysOfWeek[i] = 0; }
                        }

                        //if today is in the set of days chosen, update habitDoneToday to true
                        for (int i=0; i<daysOfWeek.length;i++) {
                            if (today == daysOfWeek[i]) {
                                //then we set ishabitDone to true
                                document.update("habitDoneToday", true)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                                Log.d(TAG, "DocumentSnapshot successfully updated!");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error updating document", e);
                                            }
                                        });



                            }
                        }


                    }
                    else {
                        Log.d(TAG, "document does not exist!!");
                    }

                }
                else {
                    Log.d(TAG, task.getException().toString());
                }


            }
        });



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
        habitEventsData.get(position);
        Intent intent = new Intent(this, ViewEventActivity.class);
        intent.putExtra(EXTRA_HABIT_EVENT, habitEventsData.get(position));
        startActivity(intent);
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

    private String daysForHabit(int i) {
        String day = null;
        switch (i) {
            case 0:
                day = "Sunday";
                break;
            case 1:
                day = "Monday";
                break;
            case 2:
                day = "Tuesday";
                break;
            case 3:
                day = "Wednesday";
                break;
            case 4:
                day = "Thursday";
                break;
            case 5:
                day = "Friday";
                break;
            case 6:
                day ="Saturday";
                break;
        }
        return day;
    }
}