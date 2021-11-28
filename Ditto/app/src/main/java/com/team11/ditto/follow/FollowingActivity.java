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
package com.team11.ditto.follow;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.team11.ditto.R;
import com.team11.ditto.UserProfileActivity;
import com.team11.ditto.interfaces.Firebase;
import com.team11.ditto.interfaces.FollowFirebase;
import com.team11.ditto.interfaces.SwitchTabs;
import com.team11.ditto.login.ActiveUser;
import com.team11.ditto.profile_details.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

/**
 * Activity to display a list of Users that follow the ActiveUser
 * @author Vivek Malhotra
 */
public class FollowingActivity extends AppCompatActivity implements SwitchTabs, Firebase, FollowFirebase {

    //Declarations
    private TabLayout tabLayout;
    private ListView followingListView;
    private static ArrayAdapter<User> userAdapter;
    private ArrayList<User> userDataList;
    private static ArrayList<String> followedByActiveUser = new ArrayList<>();
    private ActiveUser currentUser;
    private FirebaseFirestore db;

    /**
     * Instructions for Activity creation
     * Simple list view with tabs
     *
     * @param savedInstanceState current app state
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //set layouts
        super.onCreate(savedInstanceState);
        setContentView(R.layout.following_list);
        followingListView = findViewById(R.id.following_list_custom);
        tabLayout = findViewById(R.id.tabs);

        currentUser = new ActiveUser();
        db = FirebaseFirestore.getInstance();
        //Initialize values
        userDataList = new ArrayList<>();
        userAdapter = new CustomListFollowerFollowing(FollowingActivity.this, userDataList);


        followingListView.setAdapter(userAdapter);



        userAdapter.notifyDataSetChanged();

        //Enable tab switching
        currentTab(tabLayout, PROFILE_TAB);
        switchTabs(this, tabLayout, PROFILE_TAB);
        // Get the current user's followers

        getFollowedByActiveUser(db,currentUser,followedByActiveUser);

        getFollowerList();
        //View User profile if user in list is clicked
        onProfileClick();



    }



    /**
     * Define behavior if back button pressed
     * - goes back to ActiveUser profile
     */
    public void onBackPressed() {
        Intent intent = new Intent(FollowingActivity.this, UserProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |  Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    /**
     * View a User in the list's profile if they are clicked
     */
    public void onProfileClick() {
        followingListView.setOnItemClickListener((adapterView, view, i, l) -> {
            User followedByMe = (User) followingListView.getAdapter().getItem(i);
            String followedByMeEmail = followedByMe.getPassword();
            String followedByMeName = followedByMe.getUsername();
            Intent intent = new Intent(FollowingActivity.this, FriendHabitActivity.class);
            Bundle b = new Bundle();
            b.putStringArray("following", new String[]{followedByMeName, followedByMeEmail});
            intent.putExtras(b);
            Log.d("Opening profile of : ",followedByMeEmail);
            startActivity(intent);
        });

    }

    /**
     * This method shows all users followed by active user on screen
     */
    public void showData(){

        for (int i =0; i< followedByActiveUser.size(); i++){

            int finalI = i;
            db.collection("User")
                    .whereEqualTo("email",followedByActiveUser.get(i).toString() )
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for(QueryDocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())){

                                    userDataList.add( new User(snapshot.get("name").toString(), followedByActiveUser.get(finalI))  );
                                    Log.d("Followed", followedByActiveUser.get(finalI));
                                    Log.d("Iteration no. ", String.valueOf(finalI));
                                    Collections.sort(userDataList, new Comparator<User>() {
                                        @Override
                                        public int compare(User user, User t1) {
                                            return user.getUsername().compareTo(t1.getUsername()) ;
                                        }
                                    });
                                }
                            userAdapter.notifyDataSetChanged();
                            }
                        }
                    });
        }
    }

    /**
     * This method gets the list of all users followed by active user
     */
    // Do not add to firebase, Firebase is delaying return of data by few hundred ms
    // This is causing data to not show onCreation of activity
    // So just calling the showData() once the data has been returned successfully
    public void getFollowerList(){
        db.collection("Following")
                .whereEqualTo("followedBy",currentUser.getEmail())
                .get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for(QueryDocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())){
                    if(! followedByActiveUser.contains(snapshot.get("followed")) && (!snapshot.get("followed").toString().equals(currentUser.getEmail()))){
                        followedByActiveUser.add(snapshot.get("followedBy").toString());
                    }
                }
                showData();

            }

        });
    }

    @Override
    public void onPause(){
        overridePendingTransition(0,0);
        super.onPause();
    }

    /**
     * This method will remove a user active user follows from following list
     * @param view view selected
     */
    public void onRemovePress(View view){
        String cUserEmail = currentUser.getEmail();
        int position = followingListView.getPositionForView((View) view.getParent());
        //View v = followingListView.getChildAt(position);

        User removeFollower = (User) followingListView.getAdapter().getItem(position);
        String removeFollowerEmail = removeFollower.getPassword();
        removeFollowingFromList(db,removeFollowerEmail,cUserEmail);
        followedByActiveUser.clear();

        userDataList.remove(position);
        userAdapter.notifyDataSetChanged();
    }


}





