package com.team11.ditto.interfaces;

import android.os.Build;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
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
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public interface EventFirebase extends Firebase, Days{

    String HABIT_EVENT_KEY = "HabitEvent";

    String HABIT_ID = "habitID";
    String HABIT_TITLE = "habitTitle";

    String EVENT_ID = "habitEventId ";
    String COMMENT = "comment";
    String PHOTO = "photo";
    String LOCATION = "location";
    String DATE = "date";
    ArrayList<HabitEvent> hEventsFirebase = new ArrayList<>();
    HashMap<String, Object> eventData = new HashMap<>();

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
        eventData.clear();
        String habitID = event.getHabitId();
        String comment = event.getComment();
        String photo = event.getPhoto();
        String habitTitle = event.getHabitTitle();
        List<Double> location = event.getLocation();
        String date = DATE_FORMAT.format(event.getDate());

        eventData.put(USER_ID, FirebaseAuth.getInstance().getUid());
        eventData.put(HABIT_ID, habitID);
        eventData.put(COMMENT, comment);
        eventData.put(PHOTO, photo);
        eventData.put(LOCATION, location);
        eventData.put(HABIT_TITLE, habitTitle);
        eventData.put(DATE, date);
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

    /**
     * Handle resetting the habitDueToday boolean and updating the streak value
     * according to the number of days they've missed their habit
     * @param db firebase cloud
     */
    default void resetDueToday(FirebaseFirestore db){
        ActiveUser currentUser = new ActiveUser();
        db.collection("Habit")
                .whereEqualTo(USER_ID, currentUser.getUID())
                .orderBy("position")
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        for (QueryDocumentSnapshot document : value) {
                            //Check if we need to decrement since last time habit was updated
                            Boolean doneToday = (Boolean) document.getData().get("habitDoneToday");
                            Date currentDate = Calendar.getInstance().getTime();
                            Calendar cDate = Calendar.getInstance();
                            cDate.setTime(currentDate);

                            Date lastDone = document.getDate("Last_Adjusted");
                            Calendar lDone = Calendar.getInstance();
                            lDone.setTime(lastDone);

                            String habitID = document.getId();


                            //If the current day is not the same anymore, then reset boolean
                            boolean sameDay = cDate.get(Calendar.DAY_OF_YEAR) == lDone.get(Calendar.DAY_OF_YEAR) &&
                                    cDate.get(Calendar.YEAR) == lDone.get(Calendar.YEAR);


                            if (!sameDay) {
                                //how many days they missed inbetween currentday and last decremented
                                DocumentReference movedRef = db.collection("Habit").document(habitID);
                                //set habitDoneToday to false
                                movedRef
                                        .update("habitDoneToday", false)
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
                });

    }



}
