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
package com.team11.ditto;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.team11.ditto.follow.FollowRequestActivity;
import com.team11.ditto.follow.FollowerActivity;
import com.team11.ditto.follow.FollowingActivity;
import com.team11.ditto.follow.SentRequestActivity;
import com.team11.ditto.interfaces.Firebase;
import com.team11.ditto.interfaces.SwitchTabs;
import com.team11.ditto.login.ActiveUser;
import com.team11.ditto.profile_details.SearchUserActivity;

import java.util.ArrayList;

public class UserProfileActivity extends AppCompatActivity implements SwitchTabs, Firebase {

    private ImageView imageView;
    private TextView followers;
    private TextView no_followers;
    private TextView following;
    private TextView no_following;
    private TextView username_text;
    private TextView username;
    private Button search;
    private Button fr_pending;
    private Button logout;
    private static final String TAG = "UserProfileActivity";
    private Button frSent;


    private ActiveUser currentUser;

    FirebaseFirestore db; //when they add button we need to dump into db
    private TabLayout tabLayout;
    //public static Bundle habitBundle = new Bundle();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(0,0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);

        db = FirebaseFirestore.getInstance();
        currentUser = new ActiveUser();

        tabLayout = findViewById(R.id.tabs);
        imageView =findViewById(R.id.imageView2);
        followers = findViewById(R.id.followers);
        following = findViewById(R.id.following);
        no_following = findViewById(R.id.no_following_1);
        no_followers = findViewById(R.id.no_followers);
        username_text = findViewById(R.id.textView_user);
        search = findViewById(R.id.search_users);
        username = findViewById(R.id.username_editText);
        fr_pending = findViewById(R.id.pending_fr);
        logout = findViewById(R.id.logout_button);
        frSent = findViewById(R.id.follow_request_sent);

        currentTab(tabLayout, PROFILE_TAB);
        switchTabs(this, tabLayout, PROFILE_TAB);


        // Get the current user's followers
        db.collection("Following")
            .whereEqualTo("followed", currentUser.getEmail())
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        no_followers.setText((String.valueOf(task.getResult().getDocuments().size())));
                    } else {
                        Log.w(TAG, "UserProfileActivity - could not fetch followers");
                    }
                }
            });

        // Get the accounts the current user is following
        db.collection("Following")
            .whereEqualTo("followedBy", currentUser.getEmail())
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        no_following.setText((String.valueOf(task.getResult().getDocuments().size())));
                    } else {
                        Log.w(TAG, "UserProfileActivity - could not fetch following");
                    }
                }
            });

        username.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        onFollowingtap();
        onFollowNumberTap();
        onSearchTap();
        onFollowRequestTab();
        onFollowertap();
        onFollowNumberTap();
        onSentRequestTap();
        onFollowerNumberTap();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ActiveUser().logout();
                Intent intent = new Intent(UserProfileActivity.this, WelcomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |  Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |  Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void onFollowingtap(){
        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfileActivity.this, FollowingActivity.class);
                startActivity(intent);
            }
        });

    }

    public void onFollowNumberTap(){
        no_following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfileActivity.this,FollowingActivity.class);
                startActivity(intent);
            }
        });;
    }

    public void onSearchTap(){
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfileActivity.this, SearchUserActivity.class);
                startActivity(intent);
            }
        });
    }

    public void onFollowRequestTab(){
        fr_pending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfileActivity.this, FollowRequestActivity.class);
                startActivity(intent);
            }
        });
    }

    public void onFollowertap() {
        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfileActivity.this, FollowerActivity.class);
                startActivity(intent);
            }
        });
    }

    public void onFollowerNumberTap(){
        no_followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfileActivity.this, FollowerActivity.class);
                startActivity(intent);
            }
        });
    }

    public void onSentRequestTap(){
        frSent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfileActivity.this, SentRequestActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onPause(){
        overridePendingTransition(0,0);
        super.onPause();
    }

}
