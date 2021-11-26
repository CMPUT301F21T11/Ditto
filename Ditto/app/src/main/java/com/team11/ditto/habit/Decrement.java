package com.team11.ditto.habit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.team11.ditto.interfaces.Days;
import com.team11.ditto.login.ActiveUser;

import java.util.ArrayList;


public class Decrement extends BroadcastReceiver implements Days {
    private ActiveUser currentUser;
    FirebaseFirestore database;
    String today;


    @Override
    public void onReceive(Context context, Intent intent) {
        //Bundle args = intent.getBundleExtra("HABITS_DUE");
        //ArrayList<Habit> habits = (ArrayList<Habit>) args.getSerializable("ARRAYLIST");
        ArrayList<String> habitIDs = intent.getStringArrayListExtra("HABITS_DUE");
        Log.d("BRUH15", Integer.toString(habitIDs.size()));

        //get habits due today
        //if habitDoneToday is true -> increment streak
        //if habitDone is false -> decrement streak
        //database = FirebaseFirestore.getInstance();
        Log.d("HELLO", "DOES THIS WORK");

/*
        // Load habits
        currentUser = new ActiveUser();
        database.collection("Habit")
                .whereEqualTo("uid", currentUser.getUID())
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        for (QueryDocumentSnapshot document: value) {
                            // For each document parse the data and create a habit object
                            ArrayList<String> days = new ArrayList<>();
                            updateDaysFromData(days, document.getData());
                            if (days.contains(today)) {
                                String title = (String) document.getData().get("title");
                                String reason = (String) document.getData().get("reason");
                                boolean isPublic = (boolean) document.getData().get("is_public");
                                Habit habit = new Habit(title, reason, days, isPublic);
                                habits_list.add(habit);
                                Log.d("TAG", "THE HABITS ARE " +habits_list);

                            }// Add to the habit list
                            Log.d("TAG", "THE HABITS ARE " +habits_list);

                        }
                    }
                    Log.d("TAG", "THE HABITS ARE " +habits_list);

                });

 */



/*
        for (int i = 0; i < habits.size(); i++) {
            //retrieve the habitDoneToday value from firebase
            DocumentReference docRef =  database.collection("Habit").document(habits.get(i).getHabitID());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists()) {
                            //retrieve the habitDoneToday value
                            Boolean habitDoneToday = documentSnapshot.getBoolean("habitDoneToday");
                            Log.d("YK", "HABITDONETODAY  "+habitDoneToday);



                        }
                        else {
                            Log.d("YK", "document does not exist!!");
                        }

                    }
                    else {
                        Log.d("YK", task.getException().toString());
                    }


                }
            });

        }

 */

    }
}
