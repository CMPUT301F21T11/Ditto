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
*/

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;


/**
 * Role: Class for Habit Event Activity, be able to see you feed and add a habit event
 * @author: Kelly Shih, Aidan Horemans, Vivek Malhotra, Matthew Asgari
 */

public class MainActivity extends AppCompatActivity implements SwitchTabs,
        AddHabitEventFragment.OnFragmentInteractionListener, HabitFirebase,
        HabitEventRecyclerAdapter.EventClickListener, FollowFirebase {
//MACROS
    public static String EXTRA_HABIT_EVENT = "EXTRA_HABIT_EVENT";

//ACTIVITY WIDE VARIABLES
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final ActiveUser currentUser = new ActiveUser();
    private final ArrayList<String> emailList = new ArrayList<>();
    private final ArrayList<Pair<String,String>> userDataList = new ArrayList<>();
    private int shortAnimationDuration;
    private static int numProcessed = 0;
    Boolean updated;

//LAYOUTS & HELPERS
    private TabLayout tabLayout;
    private ProgressBar progressBar;
    private RecyclerView habitEventList;
    private HabitEventRecyclerAdapter habitEventRecyclerAdapter;

    /**
     * Create the Activity instance for the "Home Page" screen, default screen when back pressed
     * Shows the Feed of a the logged in User, including events posted by the people they follow
     * and themselves
     * @param savedInstanceState saved state to start from
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        overridePendingTransition(0,0);
        super.onCreate(savedInstanceState);

    // If device has userID, go to app - else, go to login
        if (new ActiveUser().getUID().equals(ActiveUser.NONE)) {
            Intent intent = new Intent(this, WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |  Intent.FLAG_ACTIVITY_CLEAR_TASK);
            this.startActivity(intent);
        }

    //Set layouts
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progress_bar);
        tabLayout = findViewById(R.id.tabs);
        setTitle("My Feed");
        final FloatingActionButton addHabitEventButton = findViewById(R.id.add_habit_event);
        addHabitEventButton.setOnClickListener(view -> new AddHabitEventFragment()
                .show(getSupportFragmentManager(), "ADD_HABIT_EVENT"));

    //Initialize non-final variables
        shortAnimationDuration = getResources().getInteger(android.R.integer.config_mediumAnimTime);
        habitEventList = findViewById(R.id.list_habit_event);
        habitEventRecyclerAdapter = new HabitEventRecyclerAdapter(this, hEventsFirebase, this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        habitEventList.setLayoutManager(manager);
        habitEventList.setAdapter(habitEventRecyclerAdapter);
        updated = false;

    //Show loading page
        habitEventList.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

    //Enable tab navigation
        currentTab(tabLayout, HOME_TAB);
        switchTabs(this, tabLayout, HOME_TAB);

    //Load the Habit Event data
        generateFollowEventList();

    //Update the habitDoneToday boolean in the database
        resetDueToday(db);
        adjustScore(db, currentUser);

    //Show feed
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
     * updates the habitDoneToday value for the Habit
     * @param newHabitEvent
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onOkPressed(HabitEvent newHabitEvent) {

        //if today is the same day as one of the dates they picked,
        //AND in this selected day if there are no other habit events with the same habit
        //THEN set habitDoneToday to true
        habitEventList.setVisibility(View.INVISIBLE);

        //handle setting the habitDoneToday field for the Habit
        isHabitDoneToday(db, todayIs(), newHabitEvent);

        //Adds the item to the database and then immediately retrieves it from the list
        clearUserEvents(currentUser.getUID());
        pushHabitEventData(db, newHabitEvent);
        sortFeed();
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
        if(hEventsFirebase.get(position).getUid()
                .equals(FirebaseAuth.getInstance().getUid())){
            Intent intent = new Intent(this, ViewEventActivity.class);
            intent.putExtra(EXTRA_HABIT_EVENT, hEventsFirebase.get(position));
            startActivity(intent);
        }
    }

    /**
     * Get all the user following
     * Then query their info and the user's
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void queryEventsForID(){
        for (int i=0; i < userDataList.size(); i++) {
            final int fi = i;
            Log.d("Starting", "Event Query for " + userDataList.get(i).first);
            db.collection(HABIT_EVENT_KEY)
                    .whereEqualTo("uid", userDataList.get(i).second)
                    .orderBy(DATE)//userevents
                    .addSnapshotListener((value, error) -> {
                        for (QueryDocumentSnapshot doc : value) {
                            // Parse the event data for each document
                            String eventID = doc.getId();
                            String eHabitId = (String) doc.getData().get("habitID");
                            db.collection(HABIT_KEY).document(eHabitId).get().addOnCompleteListener(task -> {
                                Object publicValue = task.getResult().get(IS_PUBLIC);
                                //clearUserEvents(userData.second);

                                if ( (publicValue != null && (boolean) publicValue)
                                        || ( ((String) doc.getData().get(USER_ID)).equals(currentUser.getUID())) ) {
                                    String eHabitTitle = (String) doc.getData().get("habitTitle");
                                    String eComment = (String) doc.getData().get("comment");
                                    String ePhoto = (String) doc.getData().get("photo");
                                    String date = null;
                                    Date eDate = new Date();
                                    if (date != null) {
                                        try {
                                            eDate = DATE_FORMAT.parse(date);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    String name = userDataList.get(fi).first;
                                    String userID = userDataList.get(fi).second;
                                    @Nullable List<Double> eLocation = null;
                                    if (doc.getData().get(LOCATION) != "") {
                                        eLocation = (List<Double>) doc.getData().get("location");
                                    }
                                    List<Double> locFinal = eLocation;
                                    HabitEvent event = new HabitEvent(eventID, eHabitId, eComment, ePhoto,
                                            locFinal, eHabitTitle, userID, name, eDate);
                                    if (!hEventsFirebase.contains(event)) {
                                        hEventsFirebase.add(event);
                                    }
                                    habitEventRecyclerAdapter.notifyDataSetChanged();

                                }

                            });
                        }
                        sortFeed();
                        habitEventRecyclerAdapter.notifyDataSetChanged();

                    });
        }
    }



    @Override
    public void onPause(){
        overridePendingTransition(0,0);
        super.onPause();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void generateFollowEventList(){
        hEventsFirebase.clear();
        Log.d("Current user", currentUser.getName()+" "+currentUser.getUID());
        userDataList.add(new Pair<>(currentUser.getName(), currentUser.getUID()));
        db.collection("Following")
                .whereEqualTo("followedBy", currentUser.getEmail())
                .get()
                .addOnCompleteListener(task -> task.addOnSuccessListener(success -> {
                    for (DocumentSnapshot snapshot : Objects.requireNonNull(success.getDocuments())) {
                        String email = snapshot.get(FOLLOWED).toString();
                        emailList.add(email);
                        getNameID(email);
                    }
                }));

    }

    /**
     *
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getNameID(String email) {
        Log.d("Starting", "ID Query");

        db.collection(USER_KEY)
          .whereEqualTo(EMAIL, email)
          .get()
          .addOnCompleteListener(Task -> {
              if (Task.isSuccessful()) {
                  for (QueryDocumentSnapshot snapshot : Objects.requireNonNull(Task.getResult())) {
                        String id = snapshot.getData().get(USER_ID).toString();
                        String name = snapshot.getData().get(NAME).toString();
                        Pair<String, String> userData = new Pair<>(name, id);
                        Log.d("Added", name+"'s id "+id+" and email "+email);
                        if (userData.first != null && userData.second != null) {
                            userDataList.add(userData);
                        }
                  }
                  queryEventsForID();
              } else {
                    Log.d("ID query", "unsuccessful");
              }
          });

    }

    private void clearUserEvents(String userId){
        for (int i = 0; i < hEventsFirebase.size(); i++){
            HabitEvent selectedEvent = hEventsFirebase.get(i);
            if ( selectedEvent.getUid().equals(userId) ){
                hEventsFirebase.remove(selectedEvent);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void sortFeed(){
        Log.d("Sorting", "all events");
        hEventsFirebase.sort(new Comparator<HabitEvent>() {
            @Override
            public int compare(HabitEvent habitEvent, HabitEvent t1) {
                return habitEvent.getDate().compareTo(t1.getDate());
            }
        });
    }

}