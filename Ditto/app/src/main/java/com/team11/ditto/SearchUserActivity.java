package com.team11.ditto;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Locale;

public class SearchUserActivity extends AppCompatActivity implements SwitchTabs{

    private TabLayout tabLayout;
    private ListView user_listView;
    private static ArrayAdapter searchAdapter;
    private ArrayList<User> userDataList;
    private SearchView searchView;
    private ArrayList<String> usernames;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_users);
        user_listView = (ListView) findViewById(R.id.search_list_custom);
        searchView = (SearchView) findViewById(R.id.search_user);
        tabLayout =(TabLayout) findViewById(R.id.tabs);

        usernames = new ArrayList<>();
        // Just to check if view works as intended
        User Aidan = new User("Aidan","1346789",25);
        usernames.add("Aidan");
        User Courtenay = new User("Courtenay","123456",25);
        usernames.add("Courtenay");
        User Kelly = new User("Kelly","123456",25);
        usernames.add("Kelly");
        User Matt = new User("Matt","1213456",25);
        usernames.add("Matt");
        User Reham = new User("Reham","123456",25);
        usernames.add("Reham");
        User Vivek = new User("Vivek","123456",25);
        usernames.add("Vivek");

        userDataList = new ArrayList<>();
        searchAdapter = new SearchList(SearchUserActivity.this,userDataList);
        user_listView.setAdapter(searchAdapter);
        userDataList.add(Aidan);

        userDataList.add(Courtenay);
        userDataList.add(Kelly);
        userDataList.add(Matt);
        userDataList.add(Reham);
        userDataList.add(Vivek);
        currentTab(tabLayout, PROFILE_TAB);
        switchTabs(this, tabLayout, PROFILE_TAB);

        user_listView.setAdapter(searchAdapter);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                String s_lower = s.toLowerCase(Locale.getDefault());
                userDataList.clear();
                for (int i =0; i < usernames.size();i++){
                    if(usernames.get(i).toLowerCase(Locale.getDefault()).contains(s_lower)){
                        userDataList.add(new User(usernames.get(i),"123456",25));
                    }
                }
                searchAdapter.notifyDataSetChanged();
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(),UserProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |  Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}