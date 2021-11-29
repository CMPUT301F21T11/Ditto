package com.team11.ditto.interfaces;

import android.os.Build;
import android.util.Log;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import com.team11.ditto.habit.CustomListDue;
import com.team11.ditto.habit.Habit;
import com.team11.ditto.habit.HabitRecyclerAdapter;
import com.team11.ditto.habit_event.HabitEvent;
import com.team11.ditto.login.ActiveUser;

import org.w3c.dom.Document;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

/**
 * Role: implement default methods that deal with Habits in firebase
 * @author Courtenay Laing-Kobe, Kelly Shih, Aidan Horemans
 */
public interface HabitFirebase extends EventFirebase, Days{

    ArrayList<Habit> habitsFirebase = new ArrayList<>();
    HashMap<String, Object> habitData = new HashMap<>();


    String HABIT_KEY = "Habit";
    String TITLE = "title";
    String REASON = "reason";
    String IS_PUBLIC = "is_public";

    /**
     * Retrieve the habits from firestore, called when making activity changes
     * @param queryDocumentSnapshots the event data
     */
    default void logHabitData(@Nullable QuerySnapshot queryDocumentSnapshots){
        if (queryDocumentSnapshots != null) {
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
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
     * @param adapter adapter between dataset and database
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
     * @param s position of deletion
     * @param dHabits habits after the one deleted (to have position decremented)
     */
    default void deleteDataMyHabit(FirebaseFirestore db, Habit oldEntry, int s, ArrayList<Habit> dHabits) {
        //ALSO REMOVE THE ASSOCIATED HABIT EVENTS
        db.collection(HABIT_KEY).document(oldEntry.getHabitID()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {

                    // Query all associated habit events
                    db.collection(HABIT_EVENT_KEY)
                            .whereEqualTo(HABIT_ID, oldEntry.getHabitID())
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

        //decrement all the habit positions after the one that was deleted
        decPosition(db, s,dHabits);

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
        habitData.put(TITLE, habit.getTitle());
        habitData.put(REASON, habit.getReason());

        for (int i = 0; i<7; i++){
            habitData.put(WEEKDAYS[i], habit.getDates().contains(WEEKDAYS[i]));
        }
        habitData.put(IS_PUBLIC, habit.isPublic());
        habitData.put("habitDoneToday", habit.isHabitDoneToday());
        habitData.put("streaks", Integer.toString(habit.getStreak()));

        //this field is used to add the current timestamp of the item, to be used to order the items
        //habitData.put("order", currentTime);

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
                                habit.setHabitID(document.getId());

                            }
                            habit.setPosition(count);
                            habitData.put("position", count);
                            habitData.put("Start_Date",currentTime);
                            habitData.put("Last_Adjusted", currentTime);
                            habitData.put("Last_Created", currentTime);
                            pushToDB(database,HABIT_KEY,habitID, habitData);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }


    /**
     * Handle adding a new habit to firebase
     * @param database firestore cloud
     * @param newHabit the new habit to be added to firestore
     */
    default void pushHabitData(FirebaseFirestore database, Habit newHabit){
        habitData.clear();
        habitData.put(USER_ID, new ActiveUser().getUID());
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
        String habitTitle = snapshot.get(TITLE).toString();
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
                .whereEqualTo(USER_ID, currentUser.getUID())
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
     * Handle the manual reordering of Habits in firestore
     * @param database firebase cloud
     * @param from the habit object that is moved
     * @param fromPos the original position of the habit object
     * @param toPos the new position of the habit object
     * @param habitsUpdate habits after the new position the habit is placed in
     * @param habitsDecrement habits after the original position of the habit, up to the new position of the habit
     */
    default void reOrderPosition(FirebaseFirestore database, Habit from, int fromPos, int toPos, ArrayList<Habit> habitsUpdate, ArrayList<Habit> habitsDecrement) {
        //get the number of documents in collection
        from.setPosition(toPos);
        DocumentReference movedRef = database.collection(HABIT_KEY).document(from.getHabitID());

        //set position of from habit to toPos
        setNewPosition(movedRef, toPos);

        //decrement all habit positions after the "fromPos"
        decPosition(database, fromPos, habitsDecrement);

        //increment all habit positions after the "toPos"
        incPosition(database, toPos, habitsUpdate);

    }

    /**
     * increment all habit positions after the "toPos" in firebase
     * @param database
     * @param toPos
     * @param habitsUpdate
     */
    default void incPosition(FirebaseFirestore database, int toPos, ArrayList<Habit> habitsUpdate) {
        int counter = toPos+1;
        for (int i=0; i<habitsUpdate.size();i++) {
            DocumentReference docRef = database.collection(HABIT_KEY).document(habitsUpdate.get(i).getHabitID());

            //set position of from habit to toPos
            docRef
                    .update("position", counter)
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
            counter++;

        }

    }

    /**
     * decrement all habit positions after the "fromPos" in firebase
     * @param database firestore cloud
     * @param fromPos the initial position of habit that is moved
     * @param habitsDecrement array of habits after the habit being moved
     */
    default void decPosition(FirebaseFirestore database, int fromPos, ArrayList<Habit> habitsDecrement) {
        int c = fromPos;
        for (int i=0; i<habitsDecrement.size();i++) {
            DocumentReference docRef = database.collection(HABIT_KEY).document(habitsDecrement.get(i).getHabitID());

            //set position of from habit to toPos
            docRef.update("position", c)
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
            c--;

        }
    }


    /**
     * The the position of the habit moved to the new position in firebase
     * @param movedRef Document reference to that habit moved
     * @param toPos the position the habit ends up in
     */
    default void setNewPosition(DocumentReference movedRef, int toPos){
        movedRef
                .update("position", toPos)
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

    /** Handle updating habitDoneToday field for a habit when an event is added
     * if today is the same day as one of the dates they picked,
     * AND in this selected day if there are no other habit events with the same habit
     * THEN set habitDoneToday to true
     * @param db firebase cloud
     * @param today integer representation the current day
     * @param newHabitEvent the new habit event added
     */
    default void isHabitDoneToday(FirebaseFirestore db, int today, HabitEvent newHabitEvent) {
        //get days ...
        final Integer[] daysOfWeek = new Integer[7];
        DocumentReference document = db.collection(HABIT_KEY).document(newHabitEvent.getHabitId()); //get the associated habit
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
                            if (isDay) {
                                daysOfWeek[i] = i+1;
                            } else {
                                daysOfWeek[i] = 0;
                            }
                        }
                        //if today is in the set of days chosen, update habitDoneToday to true
                        boolean doneToday = (Boolean) documentSnapshot.get("habitDoneToday");
                        int StreakScore = Integer.parseInt(documentSnapshot.get("streaks").toString());
                        if(!doneToday){
                            setHabitDoneToday(document, daysOfWeek, today, StreakScore);
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
    }

    /**
     * Handle setting the habitDoneToday boolean field in firestore after an event has been posted.
     * @param document firebase cloud
     * @param daysOfWeek a list of the days of the week that the Habit is to be done
     * @param today the current day as an int (1-7)
     */
    default void setHabitDoneToday(DocumentReference document, Integer[] daysOfWeek, int today, int StreakScore) {
        document.get();
        int newStreak = (int) (StreakScore + 3);
        newStreak = Math.min(8,newStreak);
        String newStreakStr = String.valueOf(newStreak);

        for (int i=0; i<daysOfWeek.length;i++) {
            if (today == daysOfWeek[i]) {
                //then we set ishabitDone to true
                document.update("habitDoneToday", true,
                                    "streaks",newStreakStr)
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

    /**
     * Retrieve the day of the week
     * @param i index of day in firestore
     * @return String form of the day of week
     */
    default String daysForHabit(int i) {
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

    /**
     * This method will update the streak score of a habit in db, accounts for days since the last opening of the app,
     * as well as the last created habit event
     * @param db FirebaseFirestore
     * @param currentUser ActiveUser
     */
    default void adjustScore(FirebaseFirestore db, ActiveUser currentUser){
        Date present_date =  Calendar.getInstance().getTime();
        //Date today = present_date.getTime();
        db.collection("Habit")
                .whereEqualTo("uid",currentUser.getUID())
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        for (QueryDocumentSnapshot snapshot: Objects.requireNonNull(task.getResult())){
                            if(snapshot.get("Last_Adjusted") != null){
                                Date LastAdjusted =  snapshot.getTimestamp("Last_Adjusted").toDate();

                                boolean Monday = snapshot.getBoolean("Monday");
                                boolean Tuesday = snapshot.getBoolean("Tuesday");
                                boolean Wednesday = snapshot.getBoolean("Wednesday");
                                boolean Thursday = snapshot.getBoolean("Thursday");
                                boolean Friday = snapshot.getBoolean("Friday");
                                boolean Saturday = snapshot.getBoolean("Saturday");
                                boolean Sunday = snapshot.getBoolean("Sunday");

                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    Date LastCreated =  snapshot.getTimestamp("Last_Created").toDate();

                                    LocalDate dateCreated = LastCreated.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                                    LocalDate dateAdjusted = LastAdjusted.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                                    LocalDate currentDate = LocalDate.now();

                                    int sum = 0;
                                    LocalDate dateLoop;

                                    //if the dates are equal, it means a habit event was created on
                                    //the datecreated day, but nothing afterwards
                                    //if they arent equal, then no habit events have been created since
                                    //dateAdjusted
                                    if(dateCreated.isEqual(dateAdjusted)){
                                        //If they created a habit event last login, ignore that day
                                        dateLoop = dateCreated.plusDays(1);
                                    } else {
                                        //If they DIDNT create a habit event last login, include that day
                                        dateLoop = dateAdjusted;
                                    }

                                    //Loop through and add 1 to sum for every missed day
                                    for (; dateLoop.isBefore(LocalDate.now()); dateLoop = dateLoop.plusDays(1)) {
                                        if ((Sunday) && dateLoop.getDayOfWeek().toString().equals("SUNDAY")) {
                                            sum = sum + 1;
                                        }
                                        if ((Monday) && dateLoop.getDayOfWeek().toString().equals("MONDAY")) {
                                            sum = sum + 1;
                                        }
                                        if ((Tuesday) && dateLoop.getDayOfWeek().toString().equals("TUESDAY")) {
                                            sum = sum + 1;
                                        }
                                        if ((Wednesday) && dateLoop.getDayOfWeek().toString().equals("WEDNESDAY")) {
                                            sum = sum + 1;
                                        }
                                        if ((Thursday) && dateLoop.getDayOfWeek().toString().equals("THURSDAY")) {
                                            sum = sum + 1;
                                        }
                                        if ((Friday) && dateLoop.getDayOfWeek().toString().equals("FRIDAY")) {
                                            sum = sum + 1;
                                        }
                                        if ((Saturday) && dateLoop.getDayOfWeek().toString().equals("SATURDAY")) {
                                            sum = sum + 1;
                                        }
                                    }

                                    //If there were days missed, then create a public event about it
                                    if(sum > 0){
                                        String habitID = snapshot.getId();
                                        String comment = "Missed " + sum + " days";
                                        String photo = "";
                                        List<Double> location = new ArrayList<>();
                                        String habitTitle = snapshot.get("title").toString();

                                        HabitEvent newHabitEvent = new HabitEvent(habitID, comment, photo, location, habitTitle);

                                        //Make habit event now
                                        pushHabitEventData(db, newHabitEvent, true);
                                    }

                                    //If it is a new day, we reset the habitDoneToday boolean
                                    //to allow new habit event credit
                                    if(!dateAdjusted.equals(currentDate)){
                                        db.collection("Habit")
                                                .document(snapshot.getId())
                                                .update("habitDoneToday", false);
                                    }

                                    int StreakScore = Integer.parseInt(snapshot.get("streaks").toString());

                                    int streak = StreakScore - sum;

                                    //User cannot go below a score of -5
                                    streak = Math.max(-5, streak);
                                    String streakString = String.valueOf(streak);

                                    // adjust score for all missed days
                                    db.collection("Habit")
                                            .document(snapshot.getId())
                                            .update("streaks", streakString,
                                                    "Last_Adjusted", present_date
                                            );
                                }

                            }
                        }
                    }
                });
    }

}
