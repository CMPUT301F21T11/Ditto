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
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.team11.ditto.habit.Habit;
import com.team11.ditto.habit.HabitRecyclerAdapter;
import com.team11.ditto.interfaces.Days;
import com.team11.ditto.interfaces.HabitFirebase;
import com.team11.ditto.interfaces.SwitchTabs;
import com.team11.ditto.login.ActiveUser;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

/**
 * Activity to display a list of the ActiveUser's Habits that are scheduled to be done today
 * @author Aidan Horemans, Kelly Shih, Vivek Malhotra, Matthew Asgari
 */
public class DueTodayActivity extends AppCompatActivity implements SwitchTabs, HabitFirebase, Days,
        HabitRecyclerAdapter.HabitClickListener {
    FirebaseFirestore db;
    private TabLayout tabLayout;
    private RecyclerView list;
    private HabitRecyclerAdapter dueTodayAdapter ;
    private ArrayList<Habit> habits;
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
        LinearLayoutManager manager = new LinearLayoutManager(this);
        list.setLayoutManager(manager);
        dueTodayAdapter = new HabitRecyclerAdapter(habits, this, this);
        list.setAdapter(dueTodayAdapter);

        // Load habits
        currentUser = new ActiveUser();
        db.collection("Habit")
                .whereEqualTo("uid", currentUser.getUID())
                .addSnapshotListener((value, error) -> {
                    habits.clear();
                    if (value != null) {
                        for (QueryDocumentSnapshot document: value) {
                            // For each document parse the data and create a habit object
                            String dayItIs = toTitleCase(LocalDate.now().getDayOfWeek().toString());
                            if (document.getData().get(dayItIs) != null
                                    && (boolean) document.getData().get(dayItIs)
                                    && document.getData().get("habitDoneToday") != null
                                    && ! (boolean) document.getData().get("habitDoneToday")) {
                                String title = (String) document.getData().get("title");
                                String reason = (String) document.getData().get("reason");
                                boolean isPublic = (boolean) document.getData().get("is_public");
                                ArrayList<String> days = new ArrayList<>();
                                updateDaysFromData(days, document.getData());
                                Habit habit = new Habit(title, reason, days, isPublic);
                                habits.add(habit);
                            }// Add to the habit list
                        }
                    }
                    dueTodayAdapter.notifyDataSetChanged();  // Refresh the adapter
                });

        currentTab(tabLayout, DUE_TODAY_TAB);
        switchTabs(this, tabLayout, DUE_TODAY_TAB);

        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(list);
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

    @Override
    public void onHabitClick(int position) {

    }

    ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN
            | ItemTouchHelper.START | ItemTouchHelper.END, ItemTouchHelper.LEFT) {
        /**
         * When an item is swiped right, complete
         *
         * @param viewHolder .
         * @param direction  .
         */
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            Habit selected = (Habit) habits.get(viewHolder.getAbsoluteAdapterPosition());
            habits.remove(viewHolder.getAbsoluteAdapterPosition());
            dueTodayAdapter.notifyDataSetChanged();

            selected.completeHabit(true);

        }

        /**
         * To set the background color and background icon for a swipe to delete item in the list.
         *
         * @param c                 .
         * @param recyclerView      .
         * @param viewHolder        .
         * @param dX                .
         * @param dY                .
         * @param actionState       .
         * @param isCurrentlyActive .
         */
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            //Get recyclerView item from viewholder
            View itemView = viewHolder.itemView;
            ColorDrawable background = new ColorDrawable();
            background.setColor(getColor(R.color.selection));
            background.setBounds((int) (itemView.getRight() + dX), itemView.getTop(), itemView.getRight(), itemView.getBottom());
            background.draw(c);

            Drawable deleteIcon = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_round_check_24);
            int itemTop = itemView.getTop() + ((itemView.getBottom() - itemView.getTop()) - deleteIcon.getIntrinsicHeight()) / 2;
            int itemMargin = ((itemView.getBottom() - itemView.getTop()) - deleteIcon.getIntrinsicHeight()) / 2;
            int itemLeft = itemView.getRight() - itemMargin - deleteIcon.getIntrinsicWidth();
            int itemRight = itemView.getRight() - itemMargin;
            int itemBottom = itemTop + deleteIcon.getIntrinsicHeight();
            deleteIcon.setBounds(itemLeft, itemTop, itemRight, itemBottom);
            deleteIcon.draw(c);

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        /**
         *  @param recyclerView Android default
         * @param viewHolder Android default
         * @param target Android default
         * @return
         */
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target){
            //get position of long clicked item
            int fromPos = viewHolder.getAbsoluteAdapterPosition();
            //get position of target position
            int toPos = target.getAbsoluteAdapterPosition();
            ArrayList<Habit> updateHabits = new ArrayList<>();
            ArrayList<Habit> decrementHabits = new ArrayList<>();

            Collections.swap(habits, fromPos, toPos);
            list.getAdapter().notifyItemMoved(fromPos, toPos);

            //reorder inside firebase by switching the order field
            Habit movedObject = habits.get(toPos);
            Habit to = habits.get(toPos);

            int total = dueTodayAdapter.getItemCount();


            int start = toPos+1;
            if (total==start) {
                //empty arraylist
            }
            else {
                //iterate through the habits and add them to the arraylist
                for (int i=start; i<total; i++) {
                    updateHabits.add(habits.get(i));
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
                    decrementHabits.add(habits.get(i));
                }
            }

            Log.d(TAG, "FROM POS " + fromPos+" TITLE "+to.getTitle());
            Log.d(TAG, "TO POS " + toPos);
            Log.d(TAG, "ARRAY TO UPDATE " + updateHabits);

            reOrderPosition(db, movedObject, fromPos, toPos, updateHabits, decrementHabits);

            return false;
        }
    };


}

