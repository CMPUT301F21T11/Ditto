package com.team11.ditto.interfaces;

import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.fragment.app.FragmentActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.team11.ditto.habit_event.HabitEventRecyclerAdapter;
import com.team11.ditto.login.ActiveUser;
import com.team11.ditto.profile_details.User;
import com.team11.ditto.habit.Habit;
import com.team11.ditto.habit.HabitRecyclerAdapter;
import com.team11.ditto.habit_event.HabitEvent;
import com.team11.ditto.habit_event.HabitEventRecyclerAdapter;
import com.team11.ditto.profile_details.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

/**
 * Role: implement default methods that interact with firebase
 * Make code more readable in classes that use firebase
 * @author Courtenay Laing-Kobe, Kelly Shih, Aidan Horemans
 */
public interface Firebase {

    String HABIT_KEY = "Habit";
    String USER_KEY = "User";
    String HABIT_EVENT_KEY = "HabitEvent";
    String TAG = "add";
    HashMap<String, Object> data = new HashMap<>();
    ArrayList<Habit> habitsFirebase = new ArrayList<>();
    ArrayList<User> usersFirebase = new ArrayList<>();
    ArrayList<HabitEvent> hEventsFirebase = new ArrayList<>();


    /**
     * initializing query for HabitRecyclerAdapter
     * @param database firebase cloud
     * @param adapter adapter between datalist and database
     * @param key which collection to access
     */
    default void autoSnapshotListener(FirebaseFirestore database, HabitRecyclerAdapter adapter, String key){
        Query query = database.collection(key).orderBy("order", Query.Direction.DESCENDING);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            /**Maintain listview after each activity switch, login, logout
             *
             * @param queryDocumentSnapshots
             *          event data
             * @param error
             *          error data
             */
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
                    FirebaseFirestoreException error) {

                // Clear the old list
                keyList(key).clear();
                logData(queryDocumentSnapshots, key);
                adapter.notifyDataSetChanged();
                // Notifying the adapter to render any new data fetched from the cloud
            }
        });
    }

    /**
     * initializing query for HabitEventRecyclerAdapter
     * @param database firebase cloud
     * @param adapter adapter b/t cloud and list
     * @param key which collection to access
     */
    default void autoSnapshotListener(FirebaseFirestore database, HabitEventRecyclerAdapter adapter, String key){
        Query query = database.collection(key).orderBy("order", Query.Direction.DESCENDING);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            /**Maintain listview after each activity switch, login, logout
             *
             * @param queryDocumentSnapshots
             *          event data
             * @param error
             *          error data
             */
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
                    FirebaseFirestoreException error) {

                // Clear the old list
                keyList(key).clear();
                logData(queryDocumentSnapshots, key);
                adapter.notifyDataSetChanged();
                // Notifying the adapter to render any new data fetched from the cloud
            }
        });
    }

    /**
     * handle data collection for the Habit, HabitEvent, and User cases
     * @param queryDocumentSnapshots event data
     * @param key which collection to access
     */
    default void logData(@Nullable QuerySnapshot queryDocumentSnapshots, String key){
        if (queryDocumentSnapshots != null) {
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                switch (key) {
                    case HABIT_KEY:
                        Log.d(TAG, String.valueOf(doc.getData().get("title")));
                        String hTitle = (String) doc.getData().get("title");
                        String hReason = (String) doc.getData().get("reason");
                        ArrayList<Long> temp = (ArrayList<Long>) doc.getData().get("days_of_week");
                        ArrayList<Integer> hDate = new ArrayList<>();

                        Habit newHabit = new Habit(hTitle, hReason, hDate);
                        newHabit.setHabitID(doc.getId());

                        habitsFirebase.add(newHabit);

                        //TEMP FIX DO NOT LEAVE IN FINAL BUILD
                        //MAKES SURE ALL VALUES ARE INTS (problem with long being added to firebase)
                        if (temp != null && temp.size() > 0) {
                            for (int i = 0; i < temp.size(); i++) {
                                hDate.add(i, Integer.parseInt(String.valueOf(temp.get(i))));
                            }
                        }
                        break;

                    case USER_KEY:
                        Log.d(TAG, String.valueOf(doc.getData().get("username")));
                        String uUsername = (String) doc.getData().get("username");
                        String uPassword = (String) doc.getData().get("password");
                        int uAge = Integer.parseInt((String) doc.getData().get("age"));
                        usersFirebase.add(new User(uUsername, uPassword, uAge));
                        break;

                    case HABIT_EVENT_KEY:
                        Log.d(TAG, String.valueOf(doc.getData().get("habitID")));
                        String eHabitId = (String) doc.getData().get("habitID");
                        String eHabitTitle = (String) doc.getData().get("habitTitle");
                        String eComment = (String) doc.getData().get("comment");
                        String ePhoto = (String) doc.getData().get("photo");
                        String eLocation = (String) doc.getData().get("location");
                        hEventsFirebase.add(new HabitEvent(eHabitId, eComment, ePhoto, eLocation, eHabitTitle));
                        break;

                    default:
                        throw new RuntimeException("logData: Improper key used");
                }
            }
        }
    }

    /**
     * push the Habit document data to the Habit class
     * @param database firebase cloud
     * @param newHabit Habit to be added
     */
    default void pushHabitData(FirebaseFirestore database, Habit newHabit){
        final String title = newHabit.getTitle();
        final String reason = newHabit.getReason();
        final ArrayList<Integer> dates = newHabit.getDate();
        Date currentTime = Calendar.getInstance().getTime();

        data.clear();
        data.put("uid", new ActiveUser().getUID());
        data.put("title", title);
        data.put("reason", reason);
        data.put("days_of_week", dates);
        //this field is used to add the current timestamp of the item, to be used to order the items
        data.put("order", currentTime);

        pushToDB(database, HABIT_KEY, "");
    }

    /**
     * Set or update the data directly to the collection
     * @param database firestore cloud
     * @param key which collection to access
     */
    default void pushToDB(FirebaseFirestore database, String key, String docID){
        DocumentReference documentReference;
        if (docID.equals("")) { //set new
            documentReference = database.collection(key).document();
            documentReference
                    .set(data)
                    .addOnSuccessListener(aVoid -> {
                        //method which gets executed when the task is successful
                        Log.d(TAG, "Data has been added successfully!");
                    })
                    .addOnFailureListener(e -> {
                        //method that gets executed if there's a problem
                        Log.d(TAG, "Data could not be added!" + e.toString());
                    });
        }

        else { //update
            documentReference = database.collection(key).document(docID);
            documentReference
                    .update(data)
                    .addOnSuccessListener(aVoid -> {
                        //method which gets executed when the task is successful
                        Log.d(TAG, "Data has been added successfully!");
                    })
                    .addOnFailureListener(e -> {
                        //method that gets executed if there's a problem
                        Log.d(TAG, "Data could not be added!" + e.toString());
                    });
        }


    }


    /**
     * fetch the arraylist of Habit, User, HabitEvent objects. Used to store objects that were already stored in firestore
     * @param key which behaviour to access
     * @return ArrayList<Habit/User/HabitEvent>
     */
    default ArrayList<?> keyList(String key){
        switch (key){
            case HABIT_KEY:
                return habitsFirebase;
            case USER_KEY:
                return usersFirebase;
            case HABIT_EVENT_KEY:
                return hEventsFirebase;
            default:
                throw new RuntimeException("keyList: Invalid key passed");
        }
    }

    /**
     * push the HabitEvent document data to the HabitEvent collection
     * @param database firestore cloud
     * @param newHabitEvent HabitEvent to be added
     */
    default void pushHabitEventData(FirebaseFirestore database, HabitEvent newHabitEvent){
        String habitID = newHabitEvent.getHabitId();
        String comment = newHabitEvent.getComment();
        String photo = newHabitEvent.getPhoto();
        String location = newHabitEvent.getLocation();
        String habitTitle = newHabitEvent.getHabitTitle();
        final DocumentReference documentReference = database.collection(HABIT_EVENT_KEY).document();
        //get unique timestamp for ordering our list
        Date currentTime = Calendar.getInstance().getTime();
        data.put("habitID", habitID);
        data.put("comment", comment);
        data.put("photo", photo);
        data.put("location", location);
        data.put("habitTitle", habitTitle);
        //this field is used to add the current timestamp of the item, to be used to order the items
        data.put("order", currentTime);

        pushToDB(database, HABIT_EVENT_KEY, "");
    }

    /**
     * push the User document data to the User collection
     * @param database firebase cloud
     * @param newUser user to be added
     */
    default void pushUserData(FirebaseFirestore database, User newUser) {
    //    data.put("username", newUser.getUsername());
     //   data.put("password", newUser.getPassword());
      //  data.put("age", newUser.getAge());

      //  pushToDB(database, USER_KEY, "");
    }

    /**
     * Get connection to the Habit collection to fetch the Habits for the spinner
     * @param database firebase cloud
     * @param habits list of habit titles
     * @param habitsIDs list of habit ids
     * @param spinner spinner to display data
     * @param fragmentActivity fragment associated with spinner
     */
    default void getDocumentsHabit(FirebaseFirestore database, List<String> habits, List<String> habitsIDs, Spinner spinner, FragmentActivity fragmentActivity) {
        database.collection(HABIT_KEY)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                            addHabitData(snapshot, habits, habitsIDs);
                        }
                        spinnerData(spinner, habits, fragmentActivity);
                    }
                    else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });

    }

    /**
     * fetch the Habit parameter information, add to the habits and habitID arrays
     * @param snapshot event data
     * @param habits habit titles list
     * @param habitIDs habit ids list
     */
    default void addHabitData(QueryDocumentSnapshot snapshot, List<String> habits, List<String> habitIDs) {
        Log.d(TAG, snapshot.getId() + "=>" + snapshot.getData());
        String habitTitle = snapshot.get("title").toString();
        String habitID = snapshot.getId();
        habits.add(habitTitle);
        habitIDs.add(habitID);
    }

    /**
     * initialize the spinner with the options from the database
     * @param spinner spinner to populate
     * @param habits list of habit titles
     * @param fragmentActivity fragment for spinner
     */
    default void spinnerData(Spinner spinner, List<String> habits, FragmentActivity fragmentActivity) {
        //initialize the spinner with the options from the database
        ArrayAdapter<String> habitAdapter = new ArrayAdapter<>(fragmentActivity, android.R.layout.simple_spinner_item, habits);
        habitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(habitAdapter);
    }


    /**
     * populate the data map with the updated Habit data
     * @param database firebase cloud
     * @param habit habit to change
     */
    default void pushEditData(FirebaseFirestore database, Habit habit) {
        //get unique timestamp for ordering our list
        final String habitID = habit.getHabitID();
        Date currentTime = Calendar.getInstance().getTime();
        data.put("title", habit.getTitle());
        data.put("reason", habit.getReason());
        data.put("days_of_week", habit.getDate());
        //this field is used to add the current timestamp of the item, to be used to order the items
        data.put("order", currentTime);

        pushToDB(database, HABIT_KEY, habitID);
    }

    /**
     * delete the habit and ensure the associated habit events also get deleted
     * @param db firebase cloud
     * @param oldEntry Habit already on cloud to remove
     */
    default void deleteDataMyHabit(FirebaseFirestore db, Habit oldEntry) {
        //ALSO REMOVE THE ASSOCIATED HABIT EVENTS
        db.collection(HABIT_KEY).document(oldEntry.getHabitID()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    ArrayList<String> habitEventIds = (ArrayList<String>) document.get("habitEvents");
                    if (habitEventIds != null) {
                        deleteHabitEvents(db, habitEventIds);
                    }

                    deleteHabit(db, oldEntry);
                }
            }
        });


    }

    /**
     * If the array is not null, go to this function to delete the habit event
     * @param db firebase cloud
     * @param habitEventIds list of HabitEvent ids to delete
     */
    default void deleteHabitEvents(FirebaseFirestore db, ArrayList<String> habitEventIds) {
        for (int i = 0; i < habitEventIds.size(); i++) {
            //delete the associated habit event in the database
            Log.d(TAG, "habiteventid " + habitEventIds.get(i));
            db.collection(HABIT_EVENT_KEY).document(habitEventIds.get(i))
                    .delete();
        }
    }

    /**
     * delete the habit event oldEntry from firestore
     * @param db firebase cloud
     * @param oldEntry habit to delete
     */
    default void deleteHabit(FirebaseFirestore db, Habit oldEntry){
        //remove from database
        db.collection(HABIT_KEY).document(oldEntry.getHabitID())
                .delete()
                .addOnSuccessListener(unused -> Log.d(TAG, "DocumentSnapshot successfully deleted!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error deleting document", e));
    }
}