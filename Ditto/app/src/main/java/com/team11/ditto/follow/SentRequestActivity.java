/** Copyright [2021] [Reham Albakouni, Matt Asgari Motlagh, Aidan Horemans, Courtenay Laing-Kobe, Vivek Malhotra, Kelly Shih]

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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.team11.ditto.R;
import com.team11.ditto.UserProfileActivity;
import com.team11.ditto.interfaces.Firebase;
import com.team11.ditto.interfaces.FollowFirebase;
import com.team11.ditto.interfaces.SwitchTabs;
import com.team11.ditto.login.ActiveUser;
import com.team11.ditto.profile_details.User;

import java.util.ArrayList;

/**
 * Activity to display a list of Users that follow the ActiveUser
 * @author Vivek Malhotra
 */
public class SentRequestActivity extends AppCompatActivity
        implements SwitchTabs, Firebase, FollowFirebase {

    //Declarations
    private TabLayout tabLayout;
    private ListView sentRequestListView;
    private ArrayAdapter<User> userAdapter;
    private ArrayList<User> userDataList;
    FirebaseFirestore db;
    private ActiveUser currentUser;
    private ArrayList<String> sentRequestEmails = new ArrayList<>();

    /**
     * Instructions for Activity creation
     * Simple list view with tabs
     * @param savedInstanceState current app state
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //set layouts
        super.onCreate(savedInstanceState);
        setContentView(R.layout.following_list);
        sentRequestListView = findViewById(R.id.following_list_custom);
        tabLayout = findViewById(R.id.tabs);

        //Initialize values
        userDataList = new ArrayList<>();
        userAdapter = new CustomListSentRequest(SentRequestActivity.this,userDataList);
        sentRequestListView.setAdapter(userAdapter);
        currentUser = new ActiveUser();
        db = FirebaseFirestore.getInstance();   // get db instance

        //Enable tab switching
        currentTab(tabLayout, PROFILE_TAB);
        switchTabs(this, tabLayout, PROFILE_TAB);

        getSentRequestUsers(db,currentUser,sentRequestEmails,userDataList, (CustomListSentRequest) userAdapter);


    }

    /**
     * Define behavior if back button pressed
     * - goes back to ActiveUser profile
     */
    public void onBackPressed() {
        Intent intent = new Intent(SentRequestActivity.this, UserProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |  Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    public void removeFromFollowRequestSent(View view){
        String cUserEmail = currentUser.getEmail();

        int position  = sentRequestListView.getPositionForView((View) view.getParent());
        View v = sentRequestListView.getChildAt(position);
        ImageView sr = (ImageView) v.findViewById(R.id.cancel_sent_request);



        // user that I want to follow
        User wantToRemove = (User) sentRequestListView.getAdapter().getItem(position);

        // using email stored in password while fetching from db, since we don't want to know their actual password
        String wantToRemoveEmail = wantToRemove.getPassword();

        cancel_follow_request(db,wantToRemoveEmail,cUserEmail);
        removeFromSentRequest(db,wantToRemoveEmail,cUserEmail);
        userDataList.remove(wantToRemove);
        userAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause(){
        overridePendingTransition(0,0);
        super.onPause();
    }

}