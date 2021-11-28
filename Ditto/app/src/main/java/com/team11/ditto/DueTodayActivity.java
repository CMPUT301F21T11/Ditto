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
package com.team11.ditto;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.team11.ditto.habit.CustomListDue;
import com.team11.ditto.habit.Habit;
import com.team11.ditto.interfaces.Days;
import com.team11.ditto.interfaces.Firebase;
import com.team11.ditto.interfaces.HabitFirebase;
import com.team11.ditto.interfaces.SwitchTabs;
import com.team11.ditto.login.ActiveUser;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

/**
 * Activity to display a list of the ActiveUser's Habits that are scheduled to be done today
 * @author Aidan Horemans, Kelly Shih, Vivek Malhotra, Matthew Asgari
 */
public class DueTodayActivity extends AppCompatActivity implements SwitchTabs, Firebase, Days, HabitFirebase {
    FirebaseFirestore db;
    private TabLayout tabLayout;
    private ListView list;
    private ArrayAdapter<Habit> dueTodayAdapter ;
    private ArrayList<Habit> habits; //list of habits due today
    private ActiveUser currentUser;

    /**
     *Directions for creating this Activity
     * Simple listview, bottom tabs
     * @param savedInstanceState current app state
     */

    @Override
    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void onCreate(Bundle savedInstanceState) {
        //Set layouts
        overridePendingTransition(0,0);
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        setContentView(R.layout.activity_due_today);
        tabLayout = findViewById(R.id.tabs);
        list = findViewById(R.id.due_today_custom_list);

        setTitle(buildDateString());

        habits = new ArrayList<>();
        dueTodayAdapter = new CustomListDue(DueTodayActivity.this, habits);

        list.setAdapter(dueTodayAdapter);

        // Load habits
        currentUser = new ActiveUser();
        adjustScore(db,currentUser);
        db.collection("Habit")
                .whereEqualTo("uid",currentUser.getUID())
                .get()
                .addOnCompleteListener( task -> {
                    if(task.isSuccessful()){

                        for(QueryDocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())){
                            String habitID = (String) snapshot.getId();
                            ArrayList<String> days = new ArrayList<>();
                            updateDaysFromData(days, snapshot.getData());
                            String dayItIs = toTitleCase(LocalDate.now().getDayOfWeek().toString());
                            if (days.contains(dayItIs)) {
                                String title = (String) snapshot.getData().get("title");
                                String reason = (String) snapshot.getData().get("reason");
                                boolean isPublic = (boolean) snapshot.getData().get("is_public");
                                Habit habit = new Habit(title, reason, days, isPublic);
                                habit.setHabitID(habitID);
                                habits.add(habit);

                            }// Add to the habit list

                        }

                    }

                    dueTodayAdapter.notifyDataSetChanged();  // Refresh the adapter
                });

        currentTab(tabLayout, DUE_TODAY_TAB);
        switchTabs(this, tabLayout, DUE_TODAY_TAB);


    }

    /**
     * Define behaviour when back button pressed:
     * -go back to home page
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |  Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    /**
     * returns the current date
     * @return date a String format of the current date
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private String buildDateString(){

        Calendar cal = Calendar.getInstance();
        String date = LocalDate.now().getDayOfWeek().toString();
        date = date + ", ";
        String month = LocalDate.now().getMonth().toString();
        date = date + month;
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        String dayOfMonthstr = String.valueOf(dayOfMonth);
        date = date + " " + dayOfMonthstr;
        return date;
    }

    /**
     * From:
     * https://stackoverflow.com/questions/2375649/converting-to-upper-and-lower-case-in-java
     * Converts the given string to title case, where the first
     * letter is capitalized and the rest of the string is in
     * lower case.
     *
     * @param s a string with unknown capitalization
     * @return a title-case version of the string
     * @author: Ellen Spertus
     */
    public static String toTitleCase(String s)
    {
        if (s.isEmpty())
        {
            return s;
        }
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    @Override
    public void onPause(){
        overridePendingTransition(0,0);
        super.onPause();
    }


}

