package com.team11.ditto.interfaces;

import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.fragment.app.FragmentActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.team11.ditto.habit.Habit;
import com.team11.ditto.habit_event.HabitEvent;
import com.team11.ditto.habit_event.HabitEventRecyclerAdapter;
import com.team11.ditto.login.ActiveUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

public interface EventFirebase extends Firebase{

    String HABIT_EVENT_KEY = "HabitEvent";

    String HABIT_ID = "habitID";
    String HABIT_TITLE = "habitTitle";

    String EVENT_ID = "habitEventId ";
    String COMMENT = "comment";
    String PHOTO = "photo";
    String LOCATION = "location";
    ArrayList<HabitEvent> hEventsFirebase = new ArrayList<>();
    HashMap<String, Object> eventData = new HashMap<>();


    default void logEventData(@Nullable QuerySnapshot queryDocumentSnapshots) {
        if (queryDocumentSnapshots != null) {
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                Log.d(TAG, String.valueOf(doc.getData().get(HABIT_ID)));
                String eHabitId = (String) doc.getData().get(HABIT_ID);
                String eHabitTitle = (String) doc.getData().get(HABIT_TITLE);
                String eComment = (String) doc.getData().get(COMMENT);
                String ePhoto = (String) doc.getData().get(PHOTO);
                String eLocation = (String) doc.getData().get(LOCATION);
                HabitEvent hEvent = new HabitEvent(eHabitId, eComment, ePhoto, eLocation, eHabitTitle);
                hEvent.setEventID(doc.getId());
                Log.d(TAG, "EVENT ID IS" + hEvent.getEventID());
                hEventsFirebase.add(hEvent);
            }
        }
    }

    /**
     * initializing query for RecyclerAdapter
     * @param database firebase cloud
     * @param adapter adapter between datalist and database
     */
    default void autoHabitEventListener(FirebaseFirestore database, HabitEventRecyclerAdapter adapter){
        Query query = database.collection(HABIT_EVENT_KEY).orderBy(ORDER, Query.Direction.DESCENDING);
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
                hEventsFirebase.clear();
                logEventData(queryDocumentSnapshots);
                adapter.notifyDataSetChanged();
                // Notifying the adapter to render any new data fetched from the cloud

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
            Log.d(TAG, EVENT_ID + habitEventIds.get(i));
            db.collection(HABIT_EVENT_KEY).document(habitEventIds.get(i))
                    .delete();
        }
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
     * push the HabitEvent document data to the HabitEvent collection
     * @param database firestore cloud
     * @param event HabitEvent to be added
     */
    default void pushHabitEventData(FirebaseFirestore database, HabitEvent event){
        getEventData(event); //Puts the data from event into eventData
        pushToDB(database, HABIT_EVENT_KEY, "", eventData);
    }

    /**
     * push the Habit document data to the Habit class
     * @param database firebase cloud
     * @param event Habit to be added
     */
    default void pushEditEvent(FirebaseFirestore database, HabitEvent event) {
        getEventData(event); //Puts the data from event into eventData
        pushToDB(database, HABIT_EVENT_KEY, event.getEventID(), eventData);
    }

    /**
     * Helper function to put the proper data from HabitEvent into eventData
     * @param event
     */
    default void getEventData(HabitEvent event){
        String habitID = event.getHabitId();
        String comment = event.getComment();
        String photo = event.getPhoto();
        String location = event.getLocation();
        String habitTitle = event.getHabitTitle();
        //get unique timestamp for ordering our list
        Date currentTime = Calendar.getInstance().getTime();
        eventData.put(USER_ID, FirebaseAuth.getInstance().getUid());
        eventData.put(HABIT_ID, habitID);
        eventData.put(COMMENT, comment);
        eventData.put(PHOTO, photo);
        eventData.put(LOCATION, location);
        eventData.put(HABIT_TITLE, habitTitle);
        eventData.put(ORDER, currentTime);
        //this field is used to add the current timestamp of the item, to be used to order the items
    }



    /**
     * Handle the deletion process for a habit event
     * @param db firebase cloud
     * @param oldEntry habit to delete
     */
    default void deleteDataMyEvent(FirebaseFirestore db, HabitEvent oldEntry) {
        db.collection(HABIT_EVENT_KEY).document(oldEntry.getEventID()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {

                    deleteHabit(db, oldEntry);
                }
            }
        });

    }

    /**
     * delete the habit event oldEntry from firestore
     * @param db firebase cloud
     * @param oldEntry habit to delete
     */
    default void deleteHabit(FirebaseFirestore db, HabitEvent oldEntry){
        //remove from database
        db.collection(HABIT_EVENT_KEY).document(oldEntry.getEventID())
                .delete()
                .addOnSuccessListener(unused -> Log.d(TAG, "DocumentSnapshot successfully deleted!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error deleting document", e));
    }

}
