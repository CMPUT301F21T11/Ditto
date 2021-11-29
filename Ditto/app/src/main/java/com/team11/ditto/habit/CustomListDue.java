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
package com.team11.ditto.habit;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.team11.ditto.R;

import java.util.ArrayList;

/**
 * Custom list to show Habits that are due on the current day of the week
 * @author Vivek Malhotra
 */
public class CustomListDue extends ArrayAdapter<Habit> {

    private final ArrayList<Habit> habits;
    private final Context context;
    FirebaseFirestore database;


    /**
     * Constructor for the an item in the due today listview
     * @param context
     * @param habits
     */
    public CustomListDue(Context context, ArrayList<Habit> habits) {
        super(context,0, habits);
        this.habits = habits;
        this.context = context;
    }

    /**
     * set the fields of the habit item in the listview
     * @param position position of habit in the listview
     * @param convertView
     * @param parent
     * @return
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @Nullable ViewGroup parent) {
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.duetoday_content, parent,false);
        }

        Habit habit = habits.get(position);

        TextView habitName = view.findViewById(R.id.due_habit_name);
        TextView habitDescription = view.findViewById(R.id.due_habit_description);
        ImageView progress = view.findViewById(R.id.my_progress);

        habitName.setText(habit.getTitle());
        habitDescription.setText(habit.getReason());
        setIcon(habit.getStreak(), progress);

        return view;
    }

    /**
     * set the streak icon for the habit
     * @param streaks the streak value for the habit
     * @param icon the indicator icon for progress
     */
    private void setIcon(int streaks, ImageView icon) {
        int lB = -3;
        int uB = 5;
        if (streaks < lB) {
            icon.setImageResource(R.drawable.sad);

        }
        else if (streaks >= lB && streaks < uB) {
            icon.setImageResource(R.drawable.neutral);


        }
        else if (streaks >= uB) {
            icon.setImageResource(R.drawable.happiness);
            //icon.setColorFilter(Color.rgb(50,205,50));


        }
    }
}