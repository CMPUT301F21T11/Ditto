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
*/

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
<<<<<<< HEAD

=======
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
>>>>>>> b97d383fe628a7f02a93a1b08ebcbe0eee52d8c5
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

<<<<<<< HEAD
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
=======
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
>>>>>>> b97d383fe628a7f02a93a1b08ebcbe0eee52d8c5
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.team11.ditto.habit.Habit;
import com.team11.ditto.habit_event.AddHabitEventFragment;
import com.team11.ditto.habit_event.HabitEvent;
import com.team11.ditto.habit_event.HabitEventRecyclerAdapter;
import com.team11.ditto.habit_event.ViewEventActivity;
import com.team11.ditto.interfaces.FollowFirebase;
import com.team11.ditto.interfaces.HabitFirebase;
import com.team11.ditto.interfaces.SwitchTabs;
import com.team11.ditto.login.ActiveUser;
import com.team11.ditto.profile_details.User;

<<<<<<< HEAD
import java.util.ArrayList;
import java.util.Calendar;
=======

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
>>>>>>> b97d383fe628a7f02a93a1b08ebcbe0eee52d8c5


/**
 * Role: Class for Habit Event Activity, be able to see you feed and add a habit event
 * @author: Kelly Shih, Aidan Horemans, Vivek Malhotra, Matthew Asgari
 */

public class MainActivity extends AppCompatActivity implements SwitchTabs,
<<<<<<< HEAD
        AddHabitEventFragment.OnFragmentInteractionListener, HabitFirebase, FollowFirebase,
        HabitEventRecyclerAdapter.EventClickListener {

=======
        AddHabitEventFragment.OnFragmentInteractionListener, HabitFirebase,
        HabitEventRecyclerAdapter.EventClickListener, FollowFirebase {

    private static final String TAG = "tab switch";
>>>>>>> b97d383fe628a7f02a93a1b08ebcbe0eee52d8c5
    private TabLayout tabLayout;
    public static String EXTRA_HABIT_EVENT = "EXTRA_HABIT_EVENT";
    private ArrayList<HabitEvent> habitEventsData;
    private ArrayList<String> idList;
    private ActiveUser currentUser;

    private ProgressBar progressBar;
    private int shortAnimationDuration;

    private RecyclerView habitEventList;
    private HabitEventRecyclerAdapter habitEventRecyclerAdapter;

    private FirebaseFirestore db;
    HashMap<String, Object> data = new HashMap<>();

    private ActiveUser activeUser;
    private ArrayList<Habit> habits; //list of habits due today
    private ActiveUser currentUser;


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

        progressBar = findViewById(R.id.progress_bar);
        tabLayout = findViewById(R.id.tabs);
<<<<<<< HEAD
=======
        habits = new ArrayList<>();

        currentUser = new ActiveUser();

>>>>>>> b97d383fe628a7f02a93a1b08ebcbe0eee52d8c5
        setTitle("My Feed");
        habitEventList = findViewById(R.id.list_habit_event);
        db = FirebaseFirestore.getInstance();

        currentUser = new ActiveUser();
        habitEventsData = new ArrayList<>();
        idList = new ArrayList<>();
        idList.add(currentUser.getUID());
        getFollowedByActiveUser(db, currentUser, idList);

        habitEventRecyclerAdapter = new HabitEventRecyclerAdapter(this, habitEventsData, this);

<<<<<<< HEAD

        // Load the Habit Event data (This will be converted to use the Firebase interface in the future)
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
=======
        habitEventList.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        shortAnimationDuration = getResources().getInteger(android.R.integer.config_mediumAnimTime);

        // Load the Habit Event data (This will be converted to use the Firebase interface in the future)
        queryList();

        resetDueToday(db); //reset the habitDoneToday boolean in the database

>>>>>>> b97d383fe628a7f02a93a1b08ebcbe0eee52d8c5

        LinearLayoutManager manager = new LinearLayoutManager(this);
        habitEventList.setLayoutManager(manager);
        habitEventList.setAdapter(habitEventRecyclerAdapter);

        currentTab(tabLayout, HOME_TAB);
        switchTabs(this, tabLayout, HOME_TAB);

        //Get a top level reference to the collection
        db = FirebaseFirestore.getInstance();

        //Notifies if cloud data changes (from Firebase Interface)
        autoHabitEventListener(db, habitEventRecyclerAdapter);

        final FloatingActionButton addHabitEventButton = findViewById(R.id.add_habit_event);

        addHabitEventButton.setOnClickListener(view -> new AddHabitEventFragment()
                .show(getSupportFragmentManager(), "ADD_HABIT_EVENT"));

        adjustScore(db, currentUser);

        fadeInView();

    }

    /**
     * Runs a loading animation for the habitEventList while the data is queried, and then fades
     * out after all info is properly queried
     */
    private void fadeInView(){
        habitEventList.setAlpha(1f);
        habitEventList.setVisibility(View.VISIBLE);

        habitEventList.animate()
                .alpha(1f)
                .setDuration(shortAnimationDuration)
                .setListener(null);

        progressBar.animate()
                .alpha(0f)
                .setDuration(shortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animator){
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }


    /**
     * Adds a habitevent to firestore "HabitEvent" and adds the habitevent ID to the list of habitEvents for the habit in "Habit"
     * Adds the habitevent to the listview
<<<<<<< HEAD
     * @param newHabitEvent habit event to add
=======
     * updates the habitDoneToday value for the Habit
     * @param newHabitEvent
>>>>>>> b97d383fe628a7f02a93a1b08ebcbe0eee52d8c5
     */
    @Override
    public void onOkPressed(HabitEvent newHabitEvent) {

        //if today is the same day as one of the dates they picked,
        //AND in this selected day if there are no other habit events with the same habit
        //THEN set habitDoneToday to true
        habitEventList.setVisibility(View.INVISIBLE);

        //handle setting the habitDoneToday field for the Habit
        isHabitDoneToday(db, todayIs(), newHabitEvent);

        //Adds the item to the database and then immediately retrieves it from the list
        pushHabitEventData(db, newHabitEvent);
        habitEventRecyclerAdapter.notifyDataSetChanged();

        fadeInView();

    }

    /**
     * Handles the view Habit event activity
     * starts a new activity to view the clicked habit event
     * @param position of the clicked habit event
     */
    @Override
    public void onEventClick(int position) {
        //If we are clicking on our own event
        if(habitEventsData.get(position).getUid().equals(FirebaseAuth.getInstance().getUid())){
            Intent intent = new Intent(this, ViewEventActivity.class);
            intent.putExtra(EXTRA_HABIT_EVENT, habitEventsData.get(position));
            startActivity(intent);
        }
    }

<<<<<<< HEAD
    @Override
    public void onPause(){
        overridePendingTransition(0,0);
        super.onPause();
    }

=======

    /**
     * Get all the user following
     * Then query their info and the user's
     */
    public void queryList(){
        db = FirebaseFirestore.getInstance();

        db.collection(HABIT_EVENT_KEY)
                .whereEqualTo("uid", FirebaseAuth.getInstance().getUid()) //userevents
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
                            DocumentReference userNameReference = db.collection("User").document(uid);

                            //Query for name then create the habit event
                            userNameReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot documentSnapshot = task.getResult();
                                        if (documentSnapshot.exists()) {
                                            //retrieve the order value
                                            String name = (String) documentSnapshot.get("name");
                                            habitEventsData.add(new HabitEvent(eventID, eHabitId, eComment, ePhoto, eLocation, eHabitTitle, uid, name));
                                            habitEventRecyclerAdapter.notifyDataSetChanged();
                                        }
                                        else {
                                            Log.d("retrieve", "document does not exist!!");
                                        }

                                    }
                                    else {
                                        Log.d("retrieve", task.getException().toString());
                                    }
                                }

                            });

                        }
                    }
                });
    }

    /**
     * return the current day
     * @return int representing the current day of week (1-7)
     */
>>>>>>> b97d383fe628a7f02a93a1b08ebcbe0eee52d8c5
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

<<<<<<< HEAD
=======
    @Override
    public void onPause(){
        overridePendingTransition(0,0);
        super.onPause();
    }
>>>>>>> b97d383fe628a7f02a93a1b08ebcbe0eee52d8c5
}