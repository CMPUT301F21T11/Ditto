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
/*
Role: To display the listview of Habits for a user in the "My Habits" tab
Allow a user to add a habit, swipe left to delete a habit
Goals:
    -Implement occurrence tracking
    -Visually make it better
 */

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.team11.ditto.habit.AddHabitFragment;
import com.team11.ditto.habit.Habit;
import com.team11.ditto.habit.HabitRecyclerAdapter;
import com.team11.ditto.habit.ViewHabitActivity;
import com.team11.ditto.interfaces.Days;
import com.team11.ditto.interfaces.EventFirebase;
import com.team11.ditto.interfaces.HabitFirebase;
import com.team11.ditto.interfaces.SwitchTabs;
import com.team11.ditto.login.ActiveUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**To display the listview of Habits for a user in the "My Habits" tab
 *Allow a user to add a habit, swipe left to delete a habit
 * TODO:
 *     -A user can add a habit to the database, but cannot delete a habit from the db yet
 *     -Add the days of week to db
 *     -Allow user to edit an existing habit
 *     -Visually make it better
 *     -Get the happy faces for the level of completion for each habit
 *     -WHEN YOU DELETE A HABIT, ALSO DELETE THE HABIT EVENT ITS ASSOCIATED WITH
 * @author Kelly Shih, Aidan Horemans
 */

public class MyHabitActivity extends AppCompatActivity implements
        AddHabitFragment.OnFragmentInteractionListener, SwitchTabs,
        HabitRecyclerAdapter.HabitClickListener, HabitFirebase, EventFirebase, Days {

    public static String SELECTED_HABIT = "HABIT";
    private TabLayout tabLayout;


    //Declare variables for the list of habits
    private RecyclerView habitListView;

    private HabitRecyclerAdapter habitRecyclerAdapter;
    private ArrayList<Habit> habitDataList;

    private FirebaseFirestore db;

    private ActiveUser currentUser;

    /**
     * Create the Activity instance for the "My Habits" screen, control flow of actions
     * @param savedInstanceState saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(0,0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_habit);
        tabLayout = findViewById(R.id.tabs);
        db = FirebaseFirestore.getInstance();

        setTitle("My Habits");

        currentUser = new ActiveUser();

        habitDataList = new ArrayList<>();
        habitListView = findViewById(R.id.list);
        tabLayout = findViewById(R.id.tabs);

        habitRecyclerAdapter = new HabitRecyclerAdapter(habitDataList, this, this);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        habitListView.setLayoutManager(manager);
        habitListView.setAdapter(habitRecyclerAdapter);

        adjustScore(db, currentUser);

        // Load habits
        queryHabits(db);

        currentTab(tabLayout, MY_HABITS_TAB);
        switchTabs(this, tabLayout, MY_HABITS_TAB);

        //add habit button
        final FloatingActionButton addHabitButton = findViewById(R.id.add_habit);
        addHabitButton.setOnClickListener(new View.OnClickListener() {
            /**
             * call the add habit fragment
             * @param view selected view
             */
            @Override
            public void onClick(View view) {
                new AddHabitFragment().show(getSupportFragmentManager(), "ADD_HABIT");
            }
        });

        //Notifies if cloud data changes (from Firebase Interface)
        autoHabitListener(db, habitRecyclerAdapter);

        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(habitListView);
    }

    public void queryHabits(FirebaseFirestore db) {
        db.collection(HABIT_KEY)
                .whereEqualTo(USER_ID, currentUser.getUID())
                .orderBy("position")
                .addSnapshotListener((value, error) -> {
                    habitDataList.clear();
                    if (value != null) {
                        for (QueryDocumentSnapshot document : value) {
                            String id = document.getId();
                            String title = (String) document.getData().get(TITLE);
                            String reason = (String) document.getData().get(REASON);
                            String streaks =  (String) Objects.requireNonNull(document.getData().get("streaks"));
                            int s = Integer.parseInt(streaks);
                            ArrayList<String> days = new ArrayList<>();
                            handleDays(days, document.getData());
                            boolean isPublic;
                            if (document.getData().get("is_public") == null) {
                                isPublic = true;
                            } else {
                                isPublic = (boolean) document.getData().get("is_public");
                            }
                            boolean isDoneToday = (boolean) document.getData().get("habitDoneToday");

                            Habit habit = new Habit(id, title, reason, days, isPublic, s, isDoneToday);
                            habitDataList.add(habit);
                        }

                    }
                    habitRecyclerAdapter.notifyDataSetChanged();

                });
    }


        /**
         * Adding a habit to the database and listview as the response to the user clicking the "Add" button from the fragment
         *
         * @param newHabit the Habit to be added
         */
    @Override
    public void onOkPressed(Habit newHabit) {
        //when the user clicks the add button, we want to add to the db and display the new entry
        if (newHabit.getTitle().length() > 0) {
            pushHabitData(db, newHabit);
            habitDataList.add(newHabit);

            habitRecyclerAdapter.notifyDataSetChanged();
        }
    }

    /**
     * To transfer the control to the Main activity/ homepage when the back button is pressed
     */
    public void onBackPressed() {
        Intent intent = new Intent(MyHabitActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN
            | ItemTouchHelper.START | ItemTouchHelper.END, ItemTouchHelper.LEFT) {
        /**
         * To delete an item from the listview and database when a Habit is swiped to the left
         * @param recyclerView .
         * @param viewHolder .
         * @param target .
         * @return .
         */
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

            //get position of long clicked item
            int fromPos = viewHolder.getAbsoluteAdapterPosition();
            //get position of target position
            int toPos = target.getAbsoluteAdapterPosition();
            ArrayList<Habit> updateHabits = new ArrayList<Habit>();
            ArrayList<Habit> decrementHabits = new ArrayList<Habit>();

            Collections.swap(habitDataList, fromPos, toPos);
            recyclerView.getAdapter().notifyItemMoved(fromPos, toPos);

            //reorder inside firebase by switching the order field
            Habit movedObject = habitDataList.get(toPos);
            Habit to = habitDataList.get(toPos);

            int total = habitRecyclerAdapter.getItemCount();


            int start = toPos+1;
            if (total==start) {
                //empty arraylist
            }
            else {
                //iterate through the habits and add them to the arraylist
                for (int i=start; i<total; i++) {
                    updateHabits.add(habitDataList.get(i));
                }
            }

            int t = toPos;
            //get an arraylist of habits after the moved object
            int s = fromPos;
            if (t==s) {
                //empty arraylist
            }
            else {
                //iterate through the habits and add them to the arraylist
                for (int i=s; i<t; i++) {
                    decrementHabits.add(habitDataList.get(i));
                }
            }

            Log.d(TAG, "FROM POS " + fromPos+" TITLE "+to.getTitle());
            Log.d(TAG, "TO POS " + toPos);
            Log.d(TAG, "ARRAY TO UPDATE " + updateHabits);

            reOrderPosition(db, movedObject, fromPos, toPos, updateHabits, decrementHabits);



            return false;
        }

        /**
         * When an item is swiped left, delete from database and recyclerview
         * @param viewHolder .
         * @param direction .
         */
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            Habit oldEntry = (Habit) habitDataList.get(viewHolder.getAbsoluteAdapterPosition());


            ArrayList<Habit> dHabits = new ArrayList<Habit>();

            //get an arraylist of habits after the moved object
            int s = viewHolder.getAbsoluteAdapterPosition()+1;
            int t = habitRecyclerAdapter.getItemCount();
            if (t==s) {
                //empty arraylist
            }
            else {
                //iterate through the habits and add them to the arraylist
                for (int i=s; i<t; i++) {
                    dHabits.add(habitDataList.get(i));
                }
            }

            habitDataList.remove(viewHolder.getAbsoluteAdapterPosition());
            habitRecyclerAdapter.notifyDataSetChanged();
            Log.d("NAUR", String.valueOf(dHabits));
            Collections.reverse(dHabits);
            deleteDataMyHabit(db, oldEntry, s, dHabits);

        }

        /**
         * To set the background color and background icon for a swipe to delete item in the list.
         * @param c .
         * @param recyclerView .
         * @param viewHolder .
         * @param dX .
         * @param dY .
         * @param actionState .
         * @param isCurrentlyActive .
         */
        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            //Get recyclerView item from viewholder
            View itemView = viewHolder.itemView;
            ColorDrawable background = new ColorDrawable();
            background.setColor(Color.rgb(0xC9, 0xC9,
                    0xCE));
            background.setBounds((int) (itemView.getRight() + dX), itemView.getTop(), itemView.getRight(), itemView.getBottom());
            background.draw(c);

            Drawable deleteIcon = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_round_delete_24);
            int itemTop = itemView.getTop() + ((itemView.getBottom() - itemView.getTop()) - deleteIcon.getIntrinsicHeight()) / 2;
            int itemMargin = ((itemView.getBottom() - itemView.getTop()) - deleteIcon.getIntrinsicHeight()) / 2;
            int itemLeft = itemView.getRight() - itemMargin - deleteIcon.getIntrinsicWidth();
            int itemRight = itemView.getRight() - itemMargin;
            int itemBottom = itemTop + deleteIcon.getIntrinsicHeight();
            deleteIcon.setBounds(itemLeft, itemTop, itemRight, itemBottom);
            deleteIcon.draw(c);

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    /**
     * Opens ViewHabitActivity to view and potentially update the clicked object
     *
     * @param position in list
     */
    @Override
    public void onHabitClick(int position) {
        Intent intent = new Intent(this, ViewHabitActivity.class);
        intent.putExtra(SELECTED_HABIT, habitDataList.get(position));
        startActivity(intent);
    }

    /**
     * add the days to the dates
     * @param dates
     * @param objectMap
     */
    public void handleDays(ArrayList<String> dates, Map<String, Object> objectMap){

        for (int i = 0; i < NUM_DAYS; i++) {
            if (objectMap.get(WEEKDAYS[i]) != null && (boolean) objectMap.get(WEEKDAYS[i])) {
                dates.add(WEEKDAYS[i]);
            }
        }

    }

    @Override
    public void onPause(){
        overridePendingTransition(0,0);
        super.onPause();
    }

}