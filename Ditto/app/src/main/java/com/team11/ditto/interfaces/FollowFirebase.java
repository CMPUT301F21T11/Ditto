package com.team11.ditto.interfaces;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.team11.ditto.follow.CustomListSentRequest;
import com.team11.ditto.follow.FollowRequestList;
import com.team11.ditto.follow.FriendHabitList;
import com.team11.ditto.habit.Habit;
import com.team11.ditto.login.ActiveUser;
import com.team11.ditto.profile_details.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nullable;

public interface FollowFirebase extends Firebase{

    String USER_KEY = "User";
    ArrayList<User> usersFirebase = new ArrayList<>();
    HashMap<String, Object> userData = new HashMap<>();


    default void logUserData(@Nullable QuerySnapshot queryDocumentSnapshots) {
        if (queryDocumentSnapshots != null) {
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                Log.d(TAG, String.valueOf(doc.getData().get("username")));
                String uUsername = (String) doc.getData().get("username");
                String uPassword = (String) doc.getData().get("password");
                int uAge = Integer.parseInt((String) doc.getData().get("age"));
                usersFirebase.add(new User(uUsername, uPassword, uAge));
            }
        }
    }

    /**
     * This method gets the email ids of all users followed by active user and store them in array
     * @param db Firebase cloud
     * @param currentUser Active User
     * @param followedByActiveUser Arraylist<String>
     */
    default void getFollowedByActiveUser(FirebaseFirestore db, ActiveUser currentUser, ArrayList<String> followedByActiveUser){

        db.collection("Following")
                .whereEqualTo("followedBy",currentUser.getEmail())
                .get().addOnCompleteListener( task -> {
            if(task.isSuccessful()){
                for (QueryDocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())){

                    if(! followedByActiveUser.contains(snapshot.get("followed").toString())){
                        followedByActiveUser.add(snapshot.get("followed").toString());
                    }

                }
                Log.d("followed ",followedByActiveUser.toString());
                Log.d("Size followed ", String.valueOf(followedByActiveUser.size()));
            }
        });
    }


    /**
     * This method retreives all User objects who sent follow request to active user in real time
     * @param db Firebase cloud
     * @param currentUser active user
     * @param receivedRequestEmails list of emails received follow requests from
     * @param userDataList list of User object
     * @param userAdapter Custom adapter to show user object
     */
    default void getReceivedRequestUsers(FirebaseFirestore db, ActiveUser currentUser, ArrayList<String> receivedRequestEmails, ArrayList<User> userDataList, FollowRequestList userAdapter){
        DocumentReference documentReference = db.collection("User").document(currentUser.getUID());
        documentReference.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.w("DB ERROR", "Listen failed", error);
                return;
            }
            List<String> re = new ArrayList<>();

            if (value != null && value.exists()) {
                List<String> listReceived = (List<String>) value.get("follow_requests");
                for (int i = 0; i < listReceived.size(); i++) {
                    if (!re.contains(listReceived.get(i))) {
                        re.add(listReceived.get(i));
                    }
                    if(! receivedRequestEmails.contains(listReceived.get(i))){
                        receivedRequestEmails.add(receivedRequestEmails.size(), listReceived.get(i));

                        Log.d("Order", listReceived.get(i));
                    }
                }
            }


            for(int k =0; k< receivedRequestEmails.size(); k++){
                if(! re.contains(receivedRequestEmails.get(k))){
                    receivedRequestEmails.remove(k);
                }
            }

            userDataList.clear();

            for (int i = 0; i < receivedRequestEmails.size(); i++) {

                String receivedEmail = receivedRequestEmails.get(i);
                db.collection("User").whereEqualTo("email", receivedRequestEmails.get(i))
                        .get()
                        .addOnCompleteListener(task2 -> {
                            if (task2.isSuccessful()) {
                                for (int k = 0; k < 1; k++) {
                                    if ((Objects.requireNonNull(task2.getResult()).getDocuments().get(k).getString("email")).equals(receivedEmail)) {
                                        String name = task2.getResult().getDocuments().get(k).getString("name");
                                        userDataList.add(new User(name, receivedEmail));

                                    }
                                }
                                userAdapter.notifyDataSetChanged();
                            }
                        });
            }

        });
    }


    /**
     * This method retrieves all User objects to whom follow requests are sent by active user
     * @param db Firebase cloud
     * @param currentUser active user
     * @param sentRequestEmails list of emails sent follow requests to
     * @param userDataList list of User object
     * @param userAdapter  Custom adapter to show user objects
     */
    default void getSentRequestUsers(FirebaseFirestore db, ActiveUser currentUser, ArrayList<String> sentRequestEmails, ArrayList<User> userDataList, CustomListSentRequest userAdapter){
        DocumentReference documentReference = db.collection("User").document(currentUser.getUID());
        documentReference.get().addOnCompleteListener( task -> {
            if(task.isSuccessful()){
                DocumentSnapshot documentSnapshot = task.getResult();
                List<String> listSent = (List<String>) documentSnapshot.get("sent_requests");
                if(listSent != null){
                    for (String str : listSent){
                        if(! sentRequestEmails.contains(str)){
                            sentRequestEmails.add(str);
                            Log.d("ADDED TO SENT", str);
                        }
                    }
                }
            }
            for(int i = 0;  i< sentRequestEmails.size();i++){
                String sentEmail = sentRequestEmails.get(i);
                Log.d("Looping over", String.valueOf(sentRequestEmails.size()));
                db.collection("User").whereEqualTo("email",sentRequestEmails.get(i))
                        .get()
                        .addOnCompleteListener(task2 -> {
                            if(task2.isSuccessful()){
                                for (int k =0; k < 1;k++){
                                    if((Objects.requireNonNull(task2.getResult()).getDocuments().get(k).getString("email")).equals(sentEmail)){
                                        String name = task2.getResult().getDocuments().get(k).getString("name");
                                        userDataList.add(new User(name, sentEmail));
                                        Log.d("Sent request", sentEmail);
                                        userAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        });
            }

        });


    }


    /**
     * This method retrieves all the sent requests
     * @param currentUser ActiveUser
     * @param db Firebase cloud
     * @param sentRequest Set
     */
    default void retreiveSentRequest(FirebaseFirestore db ,ActiveUser currentUser, Set<String> sentRequest){

        DocumentReference documentReference = db.collection("User").document(currentUser.getUID());
        documentReference.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DocumentSnapshot documentSnapshot = task.getResult();
                List<String> listSent = (List<String>) documentSnapshot.get("sent_requests");
                if(listSent != null){
                    if(listSent.size()>0){
                        sentRequest.addAll(listSent);
                    }
                }

                Log.d("THIS IS THE DATA ",sentRequest.toString() );
            }

        });
    }


    /**
     *
     * @param db Firebase cloud
     * @param undesiredUserEmail email id of user Active user doesn't want to follow
     * @param activeUserEmail email id of active user
     */
    default void removeFromSentRequest(FirebaseFirestore db, String undesiredUserEmail, String activeUserEmail){
        db.collection("User")
                .whereEqualTo("email",activeUserEmail)
                .get().addOnCompleteListener( Task -> {
            if (Task.isSuccessful()){
                for (QueryDocumentSnapshot snapshot : Objects.requireNonNull(Task.getResult())){

                    String id = snapshot.getId();
                    db.collection(("User"))
                            .document(id)
                            .update("sent_requests", FieldValue.arrayRemove(undesiredUserEmail));
                }
            }
        } );
    }



    /**
     *
     * @param db Firebase cloud
     * @param desiredUserEmail email id of desired user
     * @param activeUserEmail email id of active user
     */
    default void addToSentRequest(FirebaseFirestore db, String desiredUserEmail, String activeUserEmail){
        db.collection("User")
                .whereEqualTo("email",activeUserEmail)
                .get().addOnCompleteListener( Task -> {
            if (Task.isSuccessful()){
                for (QueryDocumentSnapshot snapshot : Objects.requireNonNull(Task.getResult())){

                    String id = snapshot.getId();
                    db.collection(("User"))
                            .document(id)
                            .update("sent_requests", FieldValue.arrayUnion(desiredUserEmail));
                }
            }
        } );
    }


    /**
     * Cancel follow request from active user to undesired user
     * @param db firebase cloud
     * @param undesiredUserEmail email id of undesired user
     * @param activeUserEmail email id of active user
     *
     */
    default void cancel_follow_request(FirebaseFirestore db, String undesiredUserEmail, String activeUserEmail ){
        db.collection("User")
                .whereEqualTo("email",undesiredUserEmail)
                .get().addOnCompleteListener( Task -> {
            if (Task.isSuccessful()){
                for (QueryDocumentSnapshot snapshot : Objects.requireNonNull(Task.getResult())){

                    String id = snapshot.getId();
                    db.collection(("User"))
                            .document(id)
                            .update("follow_requests", FieldValue.arrayRemove(activeUserEmail));
                }



            }
        } );
    }


    /**
     * Send follow requests from active user to the desired user
     * @param db firebase cloud
     * @param desiredUserEmail email Id of user Active user want to follow
     * @param activeUserEmail email id of active user
     */
    default void send_follow_request(FirebaseFirestore db, String desiredUserEmail, String activeUserEmail ){
        db.collection("User")
                .whereEqualTo("email",desiredUserEmail)
                .get().addOnCompleteListener( Task -> {
            if (Task.isSuccessful()){
                for (QueryDocumentSnapshot snapshot : Objects.requireNonNull(Task.getResult())){

                    String id = snapshot.getId();
                    db.collection(("User"))
                            .document(id)
                            .update("follow_requests", FieldValue.arrayUnion(activeUserEmail));
                }



            }
        } );
    }

    /**
     * This method will remove a follower a active user's follower list
     * @param db firebase cloud
     * @param removeFollowerEmail email of user that active user wants to removw
     * @param activeUserEmail   email of active user
     */
    default void removeFollowerFromList(FirebaseFirestore db, String removeFollowerEmail, String activeUserEmail){
        db.collection("Following").whereEqualTo("followed", activeUserEmail)
                .whereEqualTo("followedBy", removeFollowerEmail)
                .get()
                .addOnCompleteListener( task -> {
                    if(task.isSuccessful()){
                        for (QueryDocumentSnapshot snapshot: Objects.requireNonNull(task.getResult())){
                            String id = snapshot.getId();
                            Log.d("ID TO DELETE ", id);
                            db.collection("Following")
                                    .document(id)
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d("Remove Follower ", "DocumentSnapshot successfully deleted!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("Remove Follower ", "Error deleting document",e);
                                        }
                                    });
                        }
                    }
                });

    }


    /**
     * This method will remove a user active user follows from following list
     * @param db firebase cloud
     * @param removeFollowingEmail email of user that active user wants to removw
     * @param activeUserEmail   email of active user
     */
    default void removeFollowingFromList(FirebaseFirestore db, String removeFollowingEmail, String activeUserEmail){
        db.collection("Following").whereEqualTo("followed", removeFollowingEmail)
                .whereEqualTo("followedBy", activeUserEmail)
                .get()
                .addOnCompleteListener( task -> {
                    if(task.isSuccessful()){
                        for (QueryDocumentSnapshot snapshot: Objects.requireNonNull(task.getResult())){
                            String id = snapshot.getId();
                            Log.d("ID TO DELETE ", id);
                            db.collection("Following")
                                    .document(id)
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d("Remove Follower ", "DocumentSnapshot successfully deleted!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("Remove Follower ", "Error deleting document",e);
                                        }
                                    });
                        }
                    }
                });

    }

    /**
     * This method will show all public habits of the users whom active user is following
     * @param db
     * @param followedByMeEmail
     * @param habitData
     * @param friendHabitAdapter
     */
    default void showFriendHabits(FirebaseFirestore db, String followedByMeEmail, ArrayList<Habit> habitData, FriendHabitList friendHabitAdapter ){
        db.collection("User")
                .whereEqualTo("email",followedByMeEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        for (QueryDocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())){
                            String uid = snapshot.getId();
                            Log.d("Getting p habits of ", uid);
                            db.collection("Habit")
                                    .whereEqualTo("uid",uid)
                                    .whereEqualTo("is_public", true)
                                    .get()
                                    .addOnCompleteListener(task2 ->{
                                        if(task2.isSuccessful()){
                                            for (QueryDocumentSnapshot snapshot1 : Objects.requireNonNull(task2.getResult())){
                                                String id = snapshot1.getId();
                                                Log.d("Opening document ", id);
                                                DocumentReference documentReference = db.collection("Habit").document(id);
                                                documentReference.get().addOnCompleteListener( task3 ->{
                                                    if(task3.isSuccessful()){
                                                        DocumentSnapshot documentSnapshot = task3.getResult();
                                                        String title = documentSnapshot.get("title").toString();
                                                        String reason = documentSnapshot.get("reason").toString();
                                                        Log.d("Title ", title);
                                                        Log.d("Reason ", reason);
                                                        habitData.add(new Habit(title,reason));
                                                        friendHabitAdapter.notifyDataSetChanged();
                                                    }
                                                });


                                            }
                                        }
                                    });

                        }
                    }
                });
    }


}
