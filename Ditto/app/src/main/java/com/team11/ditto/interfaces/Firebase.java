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
package com.team11.ditto.interfaces;


import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

/**
 * Role: implement default methods that interact with firebase
 * Make code more readable in classes that use firebase
 * @author Courtenay Laing-Kobe, Kelly Shih, Aidan Horemans
 */
public interface Firebase {

    String TAG = "add";
    String USER_ID = "uid";
    String ORDER = "order";

    /**
     * Set or update the data directly to the collection
     * @param database firestore cloud
     * @param key which collection to access
     */
    default void pushToDB(FirebaseFirestore database, String key, String docID, HashMap<String, Object> data){
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

















}




