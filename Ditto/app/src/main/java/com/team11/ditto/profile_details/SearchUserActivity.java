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
package com.team11.ditto.profile_details;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.team11.ditto.R;
import com.team11.ditto.UserProfileActivity;
import com.team11.ditto.interfaces.FollowFirebase;
import com.team11.ditto.interfaces.SwitchTabs;
import com.team11.ditto.login.ActiveUser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * Activity where the ActiveUser can search for other Users in the db to request to follow
 * @author Vivek Malhotra
 */
public class SearchUserActivity extends AppCompatActivity implements SwitchTabs, FollowFirebase {

    //Declarations
    private TabLayout tabLayout;
    private ListView user_listView;
    private static ArrayAdapter<User> searchAdapter;

    private ArrayList<User> userDataList;
    private SearchView searchView;
    private ArrayList<String> followedByActiveUser = new ArrayList<>();
    private ActiveUser currentUser;
    FirebaseFirestore db;

    private Set<String> sentRequest = new HashSet<>();

    /**
     * Instructions on creating the Activity
     * -search bar at top
     * -list of search results below
     * -tabs at bottom
     * -refresh search results if search bar text changes
     * @param savedInstanceState current state
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_users);
        user_listView = findViewById(R.id.search_list_custom);
        searchView = findViewById(R.id.search_user);
        tabLayout = findViewById(R.id.tabs);


        currentUser = new ActiveUser();         // get active  user
        db = FirebaseFirestore.getInstance();   // get db instance



        userDataList = new ArrayList<>();
        searchAdapter = new SearchList(SearchUserActivity.this,userDataList);
        user_listView.setAdapter(searchAdapter);

        currentTab(tabLayout, PROFILE_TAB);
        switchTabs(this, tabLayout, PROFILE_TAB);

        user_listView.setAdapter(searchAdapter);

        retrieveSentRequest(db,currentUser,sentRequest);
        getFollowedByActiveUser(db, currentUser, followedByActiveUser);

        //sendFollowRequest();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                String cUserEmail = currentUser.getEmail();
                String s_lower = s.toLowerCase(Locale.getDefault());
                userDataList.clear();

                // this is where I am searching in the database
                db.collection("User")
                        .whereGreaterThanOrEqualTo("name",s.toUpperCase())
                        .get()
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful()){

                                for (int i = 0; i < Objects.requireNonNull(task.getResult()).size(); i++){
                                    if(Objects.requireNonNull(task.getResult().getDocuments().get(i).getString("name")).toLowerCase().startsWith(s_lower)){
                                        String username = task.getResult().getDocuments().get(i).getString("name");
                                        String email = task.getResult().getDocuments().get(i).getString("email");

                                        Log.d("TAG",email);


                                        // Only add if there is something in search view
                                        // do not add the active user to search list
                                        // do not show if follow request already sent
                                        if( (! s.equals("")) &(! Objects.requireNonNull(email).equals(cUserEmail) ) &(!sentRequest.contains(email)) &(! followedByActiveUser.contains(email))){
                                            userDataList.add(new User(username, email));
                                            searchAdapter.notifyDataSetChanged();
                                        }
                                        // loop through and find duplicates
                                        // if duplicate found, delete it
                                        // In this case, I am using email as password (email is unique)
                                        // user is not setup as originally planned in db
                                        for (int i2 = 0; i2 < userDataList.size(); i2++){
                                            for (int j = i2+1; j < userDataList.size(); j++){
                                                if(userDataList.get(i2).getPassword().equals(userDataList.get(j).getPassword())){
                                                    userDataList.remove(j);
                                                    j--;
                                                }
                                            }
                                        }

                                        searchAdapter.notifyDataSetChanged();
                                    }

                                }

                            }
                        });

                return false;
            }
        });

    }




    /**
     * Define behaviour when back button pressed:
     * -go back to ActiveUser profile
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |  Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    /**
     * This method assists in sending and cancelling follow request
     * @param view view of the current search content
     */
    public void sendFollowRequest(View view) {

        String cUserEmail = currentUser.getEmail();

        int position  = user_listView.getPositionForView((View) view.getParent());
        View v = user_listView.getChildAt(position);
        ImageView sr = (ImageView) v.findViewById(R.id.send_request);

        String currentImage = (String) sr.getTag();

        // user that I want to follow
        User wantToFollow = (User) user_listView.getAdapter().getItem(position);

        // using email stored in password while fetching from db, since we don't want to know their actual password
        String wantToFollowEmail = wantToFollow.getPassword();



        // if request is not sent, then send it
        if (currentImage.equals("send")){
            sr.setImageResource(R.drawable.cancel_request);
            sr.setTag("cancel");
            send_follow_request(db,wantToFollowEmail,cUserEmail);
            addToSentRequest(db,wantToFollowEmail,cUserEmail);
            sentRequest.add(wantToFollowEmail);

        }

        // if request is already sent, then cancel request
        else{
            sr.setImageResource(R.drawable.ic_round_send_24);
            sr.setTag("send");

            cancel_follow_request(db,wantToFollowEmail,cUserEmail);
            removeFromSentRequest(db,wantToFollowEmail,cUserEmail);
            sentRequest.remove(wantToFollowEmail);
        }
        searchAdapter.notifyDataSetChanged();
    }


    @Override
    public void onPause(){
        overridePendingTransition(0,0);
        super.onPause();
    }

}
