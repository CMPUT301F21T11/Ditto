package com.team11.ditto.interfaces;

import android.util.Log;

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
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nullable;

public interface FollowFirebase extends Firebase{

    String USER_KEY = "User";
    String FOLLOWING_KEY = "Following";

    String USERNAME = "username";
    String NAME = "name";
    String EMAIL = "email";
    String PASSWORD = "password";

    String FOLLOWED_BY = "followedBy";
    String FOLLOWED = "followed";
    String SENT = "sent_requests";
    String RECEIVED = "follow_requests";

    ArrayList<User> usersFirebase = new ArrayList<>();

    /**
     * Logs the user data
     * @param queryDocumentSnapshots a passed query snapshot from which to log the data of
     */
    default void logUserData(@Nullable QuerySnapshot queryDocumentSnapshots) {
        if (queryDocumentSnapshots != null) {
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                Log.d(TAG, String.valueOf(doc.getData().get(USERNAME)));
                String uUsername = (String) doc.getData().get(USERNAME);
                String uPassword = (String) doc.getData().get(PASSWORD);
                usersFirebase.add(new User(uUsername, uPassword));
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

        db.collection(FOLLOWING_KEY)
                .whereEqualTo(FOLLOWED_BY,currentUser.getEmail())
                .get().addOnCompleteListener( task -> {
            if(task.isSuccessful()){
                for (QueryDocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())){
                    if(! followedByActiveUser.contains(snapshot.get(FOLLOWED).toString())){
                        Log.d("User following ", snapshot.get(FOLLOWED).toString());
                        followedByActiveUser.add(snapshot.get(FOLLOWED).toString());
                    }

                }
                Log.d(FOLLOWED,followedByActiveUser.toString());
                Log.d("Size followed ", String.valueOf(followedByActiveUser.size()));
            }
        });
    }


    /**
     * This method retrieves all User objects who sent follow request to active user in real time
     * @param db Firebase cloud
     * @param currentUser active user
     * @param receivedRequestEmails list of emails received follow requests from
     * @param userDataList list of User object
     * @param userAdapter Custom adapter to show user object
     */
    default void getReceivedRequestUsers(FirebaseFirestore db, ActiveUser currentUser, ArrayList<String> receivedRequestEmails, ArrayList<User> userDataList, FollowRequestList userAdapter){
        DocumentReference documentReference = db.collection(USER_KEY).document(currentUser.getUID());
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

                        Log.d(ORDER, listReceived.get(i));
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
                db.collection(USER_KEY).whereEqualTo(EMAIL, receivedRequestEmails.get(i))
                        .get()
                        .addOnCompleteListener(task2 -> {
                            if (task2.isSuccessful()) {
                                //for (int k = 0; k < 1; k++) {
                                try {
                                    if ((Objects.requireNonNull(task2.getResult()).getDocuments().get(0).getString(EMAIL)).equals(receivedEmail)) {
                                        String name = task2.getResult()
                                                .getDocuments().get(0).getString(NAME);
                                        userDataList.add(new User(name, receivedEmail));

                                    }
                                    userAdapter.notifyDataSetChanged();
                                }
                                catch (Exception noRequests){
                                    Log.d("No received requests", currentUser.getEmail());
                                }
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
        DocumentReference documentReference = db.collection(USER_KEY).document(currentUser.getUID());
        documentReference.get().addOnCompleteListener( task -> {
            if(task.isSuccessful()){
                DocumentSnapshot documentSnapshot = task.getResult();
                List<String> listSent = (List<String>) documentSnapshot.get(SENT);
                if(listSent != null){
                    for (String str : listSent){
                        if(! sentRequestEmails.contains(str)){
                            sentRequestEmails.add(str);
                            Log.d("ADDED TO SENT", str);
                        }
                    }
                }
            }
            for (int i = 0; i < sentRequestEmails.size(); i++) {
                String sentEmail = sentRequestEmails.get(i);
                Log.d("Looping over", String.valueOf(sentRequestEmails.size()));
                db.collection(USER_KEY).whereEqualTo(EMAIL, sentRequestEmails.get(i))
                        .get()
                        .addOnCompleteListener(task2 -> {
                            if (task2.isSuccessful()) {
                                for (int k = 0; k < 1; k++) {
                                    try {
                                        if ((Objects.requireNonNull(task2.getResult())
                                                .getDocuments().get(k).getString(EMAIL))
                                                .equals(sentEmail)) {
                                            String name = task2.getResult()
                                                    .getDocuments().get(k).getString(NAME);
                                            userDataList.add(new User(name, sentEmail));
                                            Log.d("Sent request", sentEmail);
                                            userAdapter.notifyDataSetChanged();
                                        }
                                    }
                                    catch (Exception noRequests){
                                        Log.d("No sent requests: ", currentUser.getEmail());
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
    default void retrieveSentRequest(FirebaseFirestore db , ActiveUser currentUser, Set<String> sentRequest){

        DocumentReference documentReference = db.collection(USER_KEY).document(currentUser.getUID());
        documentReference.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DocumentSnapshot documentSnapshot = task.getResult();
                List<String> listSent = (List<String>) documentSnapshot.get(SENT);
                if(listSent != null){
                    if(listSent.size()>0){
                        sentRequest.addAll(listSent);
                    }
                }
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
        db.collection(USER_KEY)
                .whereEqualTo(EMAIL,activeUserEmail)
                .get().addOnCompleteListener( Task -> {
            if (Task.isSuccessful()){
                for (QueryDocumentSnapshot snapshot : Objects.requireNonNull(Task.getResult())){

                    String id = snapshot.getId();
                    db.collection(USER_KEY)
                            .document(id)
                            .update(SENT, FieldValue.arrayRemove(undesiredUserEmail));
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
        db.collection(USER_KEY)
                .whereEqualTo(EMAIL,activeUserEmail)
                .get().addOnCompleteListener( Task -> {
            if (Task.isSuccessful()){
                for (QueryDocumentSnapshot snapshot : Objects.requireNonNull(Task.getResult())){

                    String id = snapshot.getId();
                    db.collection(USER_KEY)
                            .document(id)
                            .update(SENT, FieldValue.arrayUnion(desiredUserEmail));
                }
            }
        } );
    }


    /**
     * remove a follow request received from activeUser that is received from undesiredUser
     * @param db firebase cloud
     * @param undesiredUserEmail email id of undesired user
     * @param activeUserEmail email id of active user
     *
     */
    default void cancel_follow_request(FirebaseFirestore db, String undesiredUserEmail, String activeUserEmail ){
        db.collection(USER_KEY)
                .whereEqualTo(EMAIL,undesiredUserEmail)
                .get().addOnCompleteListener( Task -> {
            if (Task.isSuccessful()){
                for (QueryDocumentSnapshot snapshot : Objects.requireNonNull(Task.getResult())){

                    String id = snapshot.getId();
                    db.collection(USER_KEY)
                            .document(id)
                            .update(RECEIVED, FieldValue.arrayRemove(activeUserEmail));

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
        db.collection(USER_KEY)
                .whereEqualTo(EMAIL,desiredUserEmail)
                .get().addOnCompleteListener( Task -> {
            if (Task.isSuccessful()){
                for (QueryDocumentSnapshot snapshot : Objects.requireNonNull(Task.getResult())){

                    String id = snapshot.getId();
                    db.collection(USER_KEY)
                            .document(id)
                            .update(RECEIVED, FieldValue.arrayUnion(activeUserEmail));
                }
            }
        } );
    }

    /**
     * This method will remove a follower a active user's follower list
     * @param db firebase cloud
     * @param removeFollowerEmail email of user that active user wants to remove
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
                                    .addOnSuccessListener(unused -> Log.d("Remove Follower ", "DocumentSnapshot successfully deleted!"))
                                    .addOnFailureListener(e -> Log.w("Remove Follower ", "Error deleting document",e));
                        }
                    }
                });

    }


    /**
     * This method will remove a user active user follows from following list
     * @param db firebase cloud
     * @param removeFollowingEmail email of user that active user wants to remove
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
                                    .addOnSuccessListener(unused -> Log.d("Remove Follower ", "DocumentSnapshot successfully deleted!"))
                                    .addOnFailureListener(e -> Log.w("Remove Follower ", "Error deleting document",e));
                        }
                    }
                });

    }

    /**
     * This method will show all public habits of the users whom active user is following
     * @param db database
     * @param followedByMeEmail email of follow-ee
     * @param habitData habit
     * @param friendHabitAdapter adapter for listview
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

                                                        try {
                                                            String streak = documentSnapshot.get("streaks").toString();
                                                            int position = ((Long) documentSnapshot.get("position")).intValue();
                                                            int score = Integer.parseInt(streak);
                                                            Log.d("Title ", title);
                                                            Log.d("Reason ", reason);
                                                            habitData.add(new Habit(title,reason,score,position ));
                                                        }
                                                        catch (Exception e){
                                                            Log.w("null streak", e);
                                                        }

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
