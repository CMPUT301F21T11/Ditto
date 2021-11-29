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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
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
import java.util.Calendar;
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
    private ArrayList<Pair<String,String>> userDataList;
    private ArrayList<String> emailList;
    private ArrayList<HabitEvent> hEvents;
    private int shortAnimationDuration;
    private Boolean mine = true;
    private Boolean others = true;

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
        emailList = new ArrayList<>();
        userDataList = new ArrayList<>();
        hEvents = new ArrayList<>();
        shortAnimationDuration = getResources().getInteger(android.R.integer.config_mediumAnimTime);
        habitEventList = findViewById(R.id.list_habit_event);
        habitEventRecyclerAdapter = new HabitEventRecyclerAdapter(this, hEvents, this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        habitEventList.setLayoutManager(manager);
        habitEventList.setAdapter(habitEventRecyclerAdapter);

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
     * Starts building the even list by finding who we follow
     * and whose events we want to see
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void generateFollowEventList(){
        //Start with empty lists
        userDataList.clear();
        hEvents.clear();

        //If we want to see our data, add it to the list
        Pair<String,String> currentUserData = new Pair<>(currentUser.getName(), currentUser.getUID());
        if (!userDataList.contains(currentUserData) && mine){
            userDataList.add(currentUserData);}

        //If we want to see others' data, continue to query their information
        if(others) {
            db.collection(FOLLOWING_KEY)
                    .whereEqualTo(FOLLOWED_BY, currentUser.getEmail())
                    .get()
                    //If query successful, and email not already in list, add email of followed user
                    .addOnCompleteListener(task -> task.addOnSuccessListener(success -> {
                        for (DocumentSnapshot snapshot : Objects.requireNonNull(success.getDocuments())) {
                            String email = snapshot.get(FOLLOWED).toString();
                            if (!emailList.contains(email)){
                            emailList.add(email);}
                        }
                        //Populate user data list for emails listed
                        getNameID();
                    }));
        }

        //If not, and we want to see our data only
        //Query the userdata list with just our data in it
        else if (mine){
            queryEvents();
        }

    }

    /**
     *Populate the activity user data list with the data of the users the current user follows
     *
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getNameID() {
        try {
            Log.d("Starting", "ID Queries");
            db.collection(USER_KEY)
              .whereIn(EMAIL, emailList)//check for where the User Document's email = query email
              .get()
              .addOnCompleteListener(Task -> {
                  if (Task.isSuccessful()) {
                      for (QueryDocumentSnapshot snapshot : Objects.requireNonNull(Task.getResult())) {
                          String id = snapshot.getId();
                          String name = snapshot.getData().get(NAME).toString();
                          Pair<String, String> userData = new Pair<>(name, id);
                          //Only add if user data exists and not already in the list
                          if ((userData.first != null && userData.second != null) && (!userDataList.contains(userData))) {
                              userDataList.add(userData);
                              Log.d("Added", name + "'s data " + id);
                          }
                          else {
                              Log.d("ID query", "unsuccessful");
                          }
                      }
                      //Query when user data is all added
                      queryEvents();
                  }
              });
        }
        //In case emails empty, etc
        catch (Exception e){
            Log.d("Exception", e.toString());
        }

    }

    /**
     * Get all the user following
     * Then query their info and the user's
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void queryEvents() {
        for (int i = 0; i < userDataList.size(); i++) {
            final int fi = i;
            Log.d("Starting", "Event Query for " + userDataList.get(i).first);
            db.collection(HABIT_EVENT_KEY)
                    .whereEqualTo(USER_ID, userDataList.get(i).second)
                    .get()
                    .addOnCompleteListener(getEventFromDB -> {
                        // Parse the event data for each event associated with selected user
                        for (DocumentSnapshot eventDoc : getEventFromDB.getResult()) {
                            String eventID = eventDoc.getId();
                            String eHabitId = (String) eventDoc.getData().get(HABIT_ID);

                            //Check if public before loading all event data
                            db.collection(HABIT_KEY).document(eHabitId).get().addOnCompleteListener(getHabitForEvent -> {
                                if (getHabitForEvent.isSuccessful()) {
                                    Object publicValue = getHabitForEvent.getResult().get(IS_PUBLIC);
                                    if ((publicValue != null && (boolean) publicValue)
                                            || (((String) eventDoc.getData().get(USER_ID)).equals(currentUser.getUID()))) {
                                        String eHabitTitle = (String) eventDoc.getData().get(HABIT_TITLE);
                                        String eComment = (String) eventDoc.getData().get(COMMENT);
                                        String ePhoto = (String) eventDoc.getData().get(PHOTO);
                                        String name = userDataList.get(fi).first;
                                        String userID = userDataList.get(fi).second;
                                        @Nullable List<Double> eLocation = null;
                                        if (eventDoc.getData().get(LOCATION) != "") {
                                            eLocation = (List<Double>) eventDoc.getData().get("location");
                                        }
                                        List<Double> locFinal = eLocation;
                                        String date = (String) eventDoc.getData().get(DATE);
                                        if (date != null) {
                                            try {
                                                Date eDate = DATE_FORMAT.parse(date);
                                                HabitEvent event = new HabitEvent(eventID, eHabitId, eComment, ePhoto,
                                                        locFinal, eHabitTitle, userID, name, eDate);
                                                //Only add if an event with that id isnt already in the list
                                                if (!hEvents.contains(event)){
                                                    hEvents.add(event);
                                                    habitEventRecyclerAdapter.notifyItemInserted(hEvents.indexOf(event));
                                                    Log.d("Added", event.toString());
                                                }
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                    //Sort as things are loaded, since otherwise tries to sort before events loaded
                                    sortFeed();
                                    habitEventRecyclerAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    });
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void sortFeed(){
        Log.d("Sorting", "chosen events");
        hEvents.sort(new Comparator<HabitEvent>() {
            @Override
            public int compare(HabitEvent habitEvent, HabitEvent t1) {
                return habitEvent.getDate().compareTo(t1.getDate());
            }
        }.reversed());
    }


    /**
     * Inflate the menu for the options menu
     * @param menu options menu
     * @return true when menu displayed, false otherwise
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.view_feed, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Listener for the edit button
     * @param item selected item
     * @return true if displayed, false otherwise
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.myEvents){
            mine = true;
            others = false;
            String myEventsTitle = "My Events";
            setTitle(myEventsTitle);
        }
        if (item.getItemId() == R.id.theirEvents){
            others = true;
            mine = false;
            String theirEvents = "Followed Users";
            setTitle(theirEvents);
        }
        if (item.getItemId() == R.id.myEvents){
            mine = true;
            others = true;
            setTitle("My Feed");
        }
        hEvents.clear();
        generateFollowEventList();
        return super.onOptionsItemSelected(item);
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

        habitEventList.setVisibility(View.INVISIBLE);
        adjustScore(db, currentUser); //Called here in case app is open during change of days
        //handle setting the habitDoneToday field for the Habit
        isHabitDoneToday(db, todayIs(), newHabitEvent);

        //Adds the item to the database and then immediately retrieves it from the list
        newHabitEvent.setName(currentUser.getName());
        hEvents.add(newHabitEvent);
        sortFeed();
        habitEventRecyclerAdapter.notifyDataSetChanged();
        pushHabitEventData(db, newHabitEvent, false);

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
        if(hEvents.get(position).getUid()
                .equals(FirebaseAuth.getInstance().getUid())){
            Intent intent = new Intent(this, ViewEventActivity.class);
            intent.putExtra(EXTRA_HABIT_EVENT, hEvents.get(position));
            startActivity(intent);
        }
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
     * Handle transitions between activities
     */
    @Override
    public void onPause(){
        overridePendingTransition(0,0);
        super.onPause();
    }
}