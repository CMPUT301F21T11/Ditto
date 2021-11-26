package com.team11.ditto.habit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
        ArrayList<String> habitIDs = intent.getStringArrayListExtra("HABITS_DUE");
        Log.d("BRUH15", String.valueOf(habitIDs));

        //get habits due today
        database = FirebaseFirestore.getInstance();
        Log.d("HELLO", "DOES THIS WORK");



        for (int i = 0; i < habitIDs.size(); i++) {
            //retrieve the habitDoneToday value from firebase
            DocumentReference docRef = database.collection("Habit").document(habitIDs.get(i));
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists()) {
                            //retrieve the habitDoneToday value
                            Boolean habitDoneToday = documentSnapshot.getBoolean("habitDoneToday");
                            int streaks = Integer.valueOf(documentSnapshot.getString("streaks"));
                            Log.d("YK", "HABITDONETODAY  " + habitDoneToday);

                            //if habitDoneToday is true -> increment streak
                            //if habitDone is false -> decrement streak
                            setStreaks(docRef, streaks, habitDoneToday);

                        } else {
                            Log.d("YK", "document does not exist!!");
                        }

                    } else {
                        Log.d("YK", task.getException().toString());
                    }


                }
            });

        }

    }


    private void setStreaks(DocumentReference document, int streaks, boolean habitDoneToday) {
        String newStreaks;

        if (habitDoneToday) {
            //increment the streaks value
            newStreaks = String.valueOf(streaks +1);
        }
        else {
            //decrement the streaks value
            newStreaks=String.valueOf(streaks -1);
        }

        document.update("streaks", newStreaks)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.w("YUH", "Streaks have been successfully updated");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("YUH", "Error updating document", e);
                    }
                });
    }


}
