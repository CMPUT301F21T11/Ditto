package com.team11.ditto.interfaces;

import android.util.Log;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.team11.ditto.MyHabitActivity;
import com.team11.ditto.habit.Habit;
import com.team11.ditto.habit.HabitRecyclerAdapter;
import com.team11.ditto.habit_event.HabitEvent;
import com.team11.ditto.login.ActiveUser;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public interface HabitFirebase extends EventFirebase, Days{

    ArrayList<Habit> habitsFirebase = new ArrayList<>();
    HashMap<String, Object> habitData = new HashMap<>();


    String HABIT_KEY = "Habit";
    String TITLE = "title";
    String REASON = "reason";
    String IS_PUBLIC = "is_public";

    default void logHabitData(@Nullable QuerySnapshot queryDocumentSnapshots){
        if (queryDocumentSnapshots != null) {
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                Log.d(TAG, String.valueOf(doc.getData().get(TITLE)));
                String hTitle = (String) doc.getData().get(TITLE);
                String hReason = (String) doc.getData().get(REASON);
                ArrayList<String> hDate = new ArrayList<>();
                updateDaysFromData(hDate, doc.getData());

                boolean isPublic;
                if (doc.getData().get(IS_PUBLIC) != null) {
                    isPublic = (Boolean) doc.getData().get(IS_PUBLIC);
                } else {
                    isPublic = false;
                }

                Habit newHabit = new Habit(hTitle, hReason, hDate, isPublic);
                newHabit.setHabitID(doc.getId());

                newHabit.setDate(hDate);
                habitsFirebase.add(newHabit);
            }
        }
    }

    /**
     * initializing query for RecyclerAdapter
     * @param database firebase cloud
     * @param adapter adapter between datalist and database
     */
    default void autoHabitListener(FirebaseFirestore database, HabitRecyclerAdapter adapter){
        Query query = database.collection(HABIT_KEY).orderBy(ORDER, Query.Direction.DESCENDING);
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
                habitsFirebase.clear();
                logHabitData(queryDocumentSnapshots);
                adapter.notifyDataSetChanged();
                // Notifying the adapter to render any new data fetched from the cloud

            }
        });
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

                    // Query all associated habit events
                    db.collection(HABIT_EVENT_KEY)
                            .whereEqualTo("habitID", oldEntry.getHabitID())
                            .get()
                            .addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    // Add each habit event to a list
                                    ArrayList<String> habitEventIds = new ArrayList<>();
                                    for (QueryDocumentSnapshot snapshot : task1.getResult()) {
                                        habitEventIds.add(snapshot.getId());
                                    }
                                    deleteHabitEvents(db, habitEventIds);  // Delete the habit events
                                }
                            });

                    deleteHabit(db, oldEntry);
                }
            }
        });

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
        habitData.put("title", habit.getTitle());
        habitData.put("reason", habit.getReason());

        for (int i = 0; i<7; i++){
            habitData.put(WEEKDAYS[i], habit.getDates().contains(WEEKDAYS[i]));
        }
        habitData.put("is_public", habit.isPublic());
        //this field is used to add the current timestamp of the item, to be used to order the items
        habitData.put("order", currentTime);

        //pushToDB(database, HABIT_KEY, habitID, habitData);

        //get the number of documents in collection
        ActiveUser currentUser = new ActiveUser();

        database.collection(HABIT_KEY)
                .whereEqualTo("uid", currentUser.getUID())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int count = 0;
                            for (DocumentSnapshot document : task.getResult()) {
                                count++;
                            }
                            habit.setPosition(count);
                            habitData.put("position", count);
                            pushToDB(database,HABIT_KEY,habitID, habitData);
                            Log.d(TAG, "SET POSITION " + habit.getPosition());
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }




    default void pushHabitData(FirebaseFirestore database, Habit newHabit){
        habitData.clear();
        habitData.put("uid", new ActiveUser().getUID());
        pushEditData(database, newHabit);
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
     * Get connection to the Habit collection to fetch the Habits for the spinner
     * @param database firebase cloud
     * @param habits list of habit titles
     * @param habitsIDs list of habit ids
     * @param spinner spinner to display data
     * @param fragmentActivity fragment associated with spinner
     */
    default void getDocumentsHabit(FirebaseFirestore database, List<String> habits, List<String> habitsIDs, Spinner spinner, FragmentActivity fragmentActivity) {

        ActiveUser currentUser = new ActiveUser();
        database.collection(HABIT_KEY)
                .whereEqualTo("uid", currentUser.getUID())
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



}
