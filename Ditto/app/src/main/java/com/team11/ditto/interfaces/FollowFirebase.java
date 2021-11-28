package com.team11.ditto.interfaces;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
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

public interface FollowFirebase extends Firebase{

    String USER_KEY = "User";
    String FOLLOWING_KEY = "Following";

    String USERNAME = "name";
    String EMAIL = "email";

    String FOLLOWED_BY = "followedBy";
    String FOLLOWED = "followed";
    String SENT = "sent_requests";
    String RECEIVED = "follow_requests";


    /**
     * This method gets the ids of all users followed by active user and store them in array
     * @param db Firebase cloud
     * @param currentUser Active User
     * @param followedByActiveUser Arraylist<String>
     */
    default void getFollowedByActiveUser(FirebaseFirestore db, ActiveUser currentUser, ArrayList<String> followedByActiveUser){

        db.collection(FOLLOWING_KEY)
                .whereEqualTo(FOLLOWED_BY, currentUser.getUID())
                .get().addOnCompleteListener( task -> {
            if(task.isSuccessful()){
                for (QueryDocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())){
                    if(! followedByActiveUser.contains(Objects.requireNonNull(snapshot.get(FOLLOWED)).toString())){
                        Log.d("User following ", snapshot.get(FOLLOWED).toString());
                        followedByActiveUser.add(Objects.requireNonNull(snapshot.get(FOLLOWED)).toString());
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
     * @param receivedRequestIDs list of User IDs received follow requests from
     * @param userDataList list of User object
     * @param userAdapter Custom adapter to show user object
     */
    default void getReceivedRequestUsers(FirebaseFirestore db, ActiveUser currentUser, ArrayList<String> receivedRequestIDs, ArrayList<User> userDataList, FollowRequestList userAdapter){
        DocumentReference documentReference = db.collection(USER_KEY).document(currentUser.getUID());
        documentReference.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.w("DB ERROR", "Listen failed", error);
                return;
            }
            List<String> re = new ArrayList<>();

            if (value != null && value.exists()) {
                List<String> listReceived = (List<String>) value.get(RECEIVED);
                if (listReceived != null && !listReceived.isEmpty()){
                    for (int i = 0; i < listReceived.size(); i++) {
                        if (!re.contains(listReceived.get(i))) {
                            re.add(listReceived.get(i));
                        }
                        if(! receivedRequestIDs.contains(listReceived.get(i))){
                            receivedRequestIDs.add(receivedRequestIDs.size(), listReceived.get(i));

                            Log.d(ORDER, listReceived.get(i));
                        }
                    }
                }
            }


            for(int k =0; k< receivedRequestIDs.size(); k++){
                if(! re.contains(receivedRequestIDs.get(k))){
                    receivedRequestIDs.remove(k);
                }
            }

            userDataList.clear();

            for (int i = 0; i < receivedRequestIDs.size(); i++) {

                String receivedID = receivedRequestIDs.get(i);
                db.collection(USER_KEY).whereEqualTo(USER_ID, receivedRequestIDs.get(i))
                        .get()
                        .addOnCompleteListener(task2 -> {
                            if (task2.isSuccessful() && !receivedRequestIDs.isEmpty()) {
                                if (Objects.requireNonNull(task2.getResult())
                                        .getDocuments().get(0).getString(USER_ID).equals(receivedID)) {
                                    String name = task2.getResult()
                                            .getDocuments().get(0).getString(USERNAME);
                                    userDataList.add(new User(name, receivedID));
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
     * @param sentRequestIDs list of emails sent follow requests to
     * @param userDataList list of User object
     * @param userAdapter  Custom adapter to show user objects
     */
    default void getSentRequestUsers(FirebaseFirestore db, ActiveUser currentUser, ArrayList<String> sentRequestIDs, ArrayList<User> userDataList, CustomListSentRequest userAdapter){
        DocumentReference documentReference = db.collection(USER_KEY).document(currentUser.getUID());
        documentReference.get().addOnCompleteListener( task -> {
            if(task.isSuccessful()){
                DocumentSnapshot documentSnapshot = task.getResult();
                assert documentSnapshot != null;
                List<String> listSent = (List<String>) documentSnapshot.get(SENT);
                if(listSent != null && !listSent.isEmpty()){
                    for (String str : listSent){
                        if(! sentRequestIDs.contains(str)){
                            sentRequestIDs.add(str);
                            Log.d("ADDED TO SENT", str);
                        }
                    }
                }
            }
            for(int i = 0;  i < sentRequestIDs.size(); i++){
                String sentID = sentRequestIDs.get(i);
                Log.d("Looping over", String.valueOf(sentRequestIDs.size()));
                db.collection(USER_KEY).whereEqualTo(USER_ID,sentRequestIDs.get(i))
                        .get()
                        .addOnCompleteListener(task2 -> {
                            if(task2.isSuccessful()){
                                if(Objects.requireNonNull(task2.getResult())
                                        .getDocuments().get(0).getString(USER_ID).equals(sentID)){
                                    String name = task2.getResult()
                                            .getDocuments().get(0).getString(USERNAME);
                                    String email = task2.getResult().getDocuments().get(0).getString(EMAIL);
                                    userDataList.add(new User(name, email, sentID));
                                    Log.d("Sent request", sentID);
                                    userAdapter.notifyDataSetChanged();
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
                assert documentSnapshot != null;
                List<String> listSent = (List<String>) documentSnapshot.get(SENT);
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
     * @param undesiredUserID email id of user Active user doesn't want to follow
     * @param activeUserEmail email id of active user
     */
    default void removeFromSentRequest(FirebaseFirestore db, String undesiredUserID, String activeUserEmail){
        db.collection(USER_KEY)
                .whereEqualTo(USER_ID,activeUserEmail)
                .get().addOnCompleteListener( Task -> {
            if (Task.isSuccessful()){
                for (QueryDocumentSnapshot snapshot : Objects.requireNonNull(Task.getResult())){

                    String id = snapshot.getId();
                    db.collection((USER_KEY))
                            .document(id)
                            .update(SENT, FieldValue.arrayRemove(undesiredUserID));
                }
            }
        } );
    }



    /**
     *
     * @param db Firebase cloud
     * @param desiredUserID email id of desired user
     * @param activeUserID email id of active user
     */
    default void addToSentRequest(FirebaseFirestore db, String desiredUserID, String activeUserID){
        db.collection(USER_KEY)
                .whereEqualTo(USER_ID,activeUserID)
                .get().addOnCompleteListener( Task -> {
            if (Task.isSuccessful()){
                for (QueryDocumentSnapshot snapshot : Objects.requireNonNull(Task.getResult())){

                    String id = snapshot.getId();
                    db.collection((USER_KEY))
                            .document(id)
                            .update(SENT, FieldValue.arrayUnion(desiredUserID));
                }
            }
        } );
    }


    /**
     * Cancel follow request from active user to undesired user
     * @param db firebase cloud
     * @param undesiredUserID email id of undesired user
     * @param activeUserID email id of active user
     *
     */
    default void cancelFollowRequest(FirebaseFirestore db, String undesiredUserID, String activeUserID ){
        db.collection(USERNAME)
                .whereEqualTo(USER_ID,undesiredUserID)
                .get().addOnCompleteListener( Task -> {
            if (Task.isSuccessful()){
                for (QueryDocumentSnapshot snapshot : Objects.requireNonNull(Task.getResult())){

                    String id = snapshot.getId();
                    db.collection((USER_KEY))
                            .document(id)
                            .update(RECEIVED, FieldValue.arrayRemove(activeUserID));
                }

            }
        } );
    }


    /**
     * Send follow requests from active user to the desired user
     * @param db firebase cloud
     * @param desiredUserID email Id of user Active user want to follow
     * @param activeUserID email id of active user
     */
    default void send_follow_request(FirebaseFirestore db, String desiredUserID, String activeUserID ){
        db.collection(USER_KEY)
                .whereEqualTo(USER_ID,desiredUserID)
                .get().addOnCompleteListener( Task -> {
            if (Task.isSuccessful()){
                for (QueryDocumentSnapshot snapshot : Objects.requireNonNull(Task.getResult())){

                    String id = snapshot.getId();
                    db.collection((USER_KEY))
                            .document(id)
                            .update(RECEIVED, FieldValue.arrayUnion(activeUserID));
                }
            }
        } );
    }

    /**
     * This method will remove a follower a active user's follower list
     * @param db firebase cloud
     * @param removeFollowerID email of user that active user wants to remove
     * @param activeUserID   email of active user
     */
    default void removeFollowerFromList(FirebaseFirestore db, String removeFollowerID, String activeUserID){
        db.collection(FOLLOWING_KEY).whereEqualTo(FOLLOWED, activeUserID)
                .whereEqualTo(FOLLOWED_BY, removeFollowerID)
                .get()
                .addOnCompleteListener( task -> {
                    if(task.isSuccessful()){
                        for (QueryDocumentSnapshot snapshot: Objects.requireNonNull(task.getResult())){
                            String id = snapshot.getId();
                            Log.d("ID TO DELETE ", id);
                            db.collection(FOLLOWING_KEY)
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
     * @param removeFollowingID email of user that active user wants to remove
     * @param activeUserID   email of active user
     */
    default void removeFollowingFromList(FirebaseFirestore db, String removeFollowingID, String activeUserID){
        db.collection(FOLLOWING_KEY).whereEqualTo(FOLLOWED, removeFollowingID)
                .whereEqualTo(FOLLOWED_BY, activeUserID)
                .get()
                .addOnCompleteListener( task -> {
                    if(task.isSuccessful()){
                        for (QueryDocumentSnapshot snapshot: Objects.requireNonNull(task.getResult())){
                            String id = snapshot.getId();
                            Log.d("ID TO DELETE ", id);
                            db.collection(FOLLOWING_KEY)
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
     * @param followedByMeID email of follow-ee
     * @param habitData habit
     * @param friendHabitAdapter adapter for listview
     */
    default void showFriendHabits(FirebaseFirestore db, String followedByMeID, ArrayList<Habit> habitData, FriendHabitList friendHabitAdapter ){
        db.collection(USER_KEY)
                .whereEqualTo(USER_ID,followedByMeID)
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
                                                        assert documentSnapshot != null;
                                                        String title = Objects.requireNonNull(documentSnapshot.get("title")).toString();
                                                        String reason = Objects.requireNonNull(documentSnapshot.get("reason")).toString();
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
