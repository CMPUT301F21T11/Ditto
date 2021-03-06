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
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.team11.ditto.R;
import com.team11.ditto.habit.Habit;
import com.team11.ditto.interfaces.FollowFirebase;
import com.team11.ditto.interfaces.SwitchTabs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

// Need an active user for this page to work

/**
 * Activity to display a list of habits of a User the ActiveUser follows

 * @author Vivek Malhotra, Courtenay Laing-Kobe
 */
public class FriendHabitActivity extends AppCompatActivity implements SwitchTabs, FollowFirebase {

    //Declarations
    private TabLayout tabLayout;
    private ListView friendHabitList;
    private ArrayAdapter<Habit> friendHabitAdapter;
    private ArrayList<Habit> habitData;
    private Bundle b;
    private String[] followingDetails;
    private String followedByMeName;
    private String followedByMeEmail;
    private FirebaseFirestore db;

    /**
     * Instructions on creating the Activity
     * Simple listview with bottom tabs
     * @param savedInstanceState current app state
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //Set layouts
        super.onCreate(savedInstanceState);
        b = this.getIntent().getExtras();
        followingDetails = b.getStringArray("following");
        followedByMeName = followingDetails[0];
        followedByMeEmail = followingDetails[1];

        setContentView(R.layout.activity_friend_profile);
        friendHabitList = findViewById(R.id.friend_habits);
        tabLayout = findViewById(R.id.tabs);
        setTitle(followedByMeName);

        db = FirebaseFirestore.getInstance();
        //Initialize
        habitData = new ArrayList<>();
        friendHabitAdapter = new FriendHabitList(FriendHabitActivity.this, habitData);
        friendHabitList.setAdapter(friendHabitAdapter);
        Collections.sort(habitData, new Comparator<Habit>() {
            @Override
            public int compare(Habit habit, Habit t1) {
                return habit.getTitle().compareTo(t1.getTitle());
            }
        });

        friendHabitAdapter.notifyDataSetChanged();

        //Enable tab switching
        currentTab(tabLayout, PROFILE_TAB);
        showFriendHabits(db,followedByMeEmail,habitData, (FriendHabitList) friendHabitAdapter);
        switchTabs(this, tabLayout, PROFILE_TAB);
    }

    /**
     * Define behaviour on back button press:
     * -Go back to FollowingActivity
     */
    public void onBackPressed() {
            Intent intent = new Intent(FriendHabitActivity.this, FollowingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |  Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
    }



    @Override
    public void onPause(){
        //Generic onPause function
        overridePendingTransition(0,0);
        super.onPause();
    }
}
