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
import java.util.HashMap;
import java.util.Map;

/**
 * Activity to display a list of Users who are requesting to follow the ActiveUser,
 * which the User can accept or deny
 * TODO make able to accept/deny requests
 * @author Vivek Malhotra
 */
public class FollowRequestActivity extends AppCompatActivity
        implements SwitchTabs, Firebase, FollowFirebase {

    //Declarations
    private TabLayout tabLayout;
    private ListView friendList;
    private FollowRequestList userAdapter;
    ArrayList<User> userDataList;
    FirebaseFirestore db;
    private ActiveUser currentUser;
    private final ArrayList<String> receivedRequestEmails = new ArrayList<>();

    /**
     * Instructions of what to do for Activity creation
     * Simple listview with tabs
     *
     * @param savedInstanceState current app state
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //Set layouts
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_request);
        friendList = findViewById(R.id.following_request_custom);
        tabLayout = findViewById(R.id.tabs);
        db = FirebaseFirestore.getInstance();   // get db instance
        currentUser = new ActiveUser();
        //Initialize values
        userDataList = new ArrayList<>();
        userAdapter = new FollowRequestList(FollowRequestActivity.this, userDataList);
        friendList.setAdapter(userAdapter);

        //Enable tab switching
        currentTab(tabLayout, PROFILE_TAB);
        switchTabs(this, tabLayout, PROFILE_TAB);
        getReceivedRequestUsers(db,currentUser,receivedRequestEmails,userDataList,userAdapter);
    }

    /**
     * Define what to do if back pressed;
     * -goes back to ActiveUser profile
     */
    public void onBackPressed() {
        Intent intent = new Intent(FollowRequestActivity.this, UserProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    /**
     * This method will accept a following request when accept icon is pressed
     * @param view view selected
     */
    public void onAcceptPress(View view){

        String currentUID = currentUser.getID();
        int position = friendList.getPositionForView((View) view.getParent());

        User acceptRequest = (User) friendList.getAdapter().getItem(position);
        String acceptRequestID = acceptRequest.getID();
        Map<String, Object> docData = new HashMap<>();
        docData.put(FOLLOWED,currentUID);
        docData.put(FOLLOWED_BY, acceptRequestID);

        db.collection(FOLLOWING_KEY)
                .add(docData);

        userDataList.remove(position);
        cancelFollowRequest(db,currentUID,acceptRequestID);
        removeFromSentRequest(db,currentUID,acceptRequestID);
        userAdapter.notifyDataSetChanged();
    }

    /**
     * This method will reject a following request when reject icon is pressed
     * @param view view for item
     */
    public void onRejectPress(View view){
        String currentUID = currentUser.getID();
        int position = friendList.getPositionForView((View) view.getParent());

        User acceptRequest = (User) friendList.getAdapter().getItem(position);
        String acceptRequestID = acceptRequest.getID();
        Map<String, Object> docData = new HashMap<>();
        docData.put("followed",currentUID);
        docData.put("followedBy", acceptRequestID);

        userDataList.remove(position);
        cancelFollowRequest(db,currentUID,acceptRequestID);
        removeFromSentRequest(db,currentUID,acceptRequestID);
        userAdapter.notifyDataSetChanged();
    }


    @Override
    public void onPause(){
        overridePendingTransition(0,0);
        super.onPause();
    }
}
