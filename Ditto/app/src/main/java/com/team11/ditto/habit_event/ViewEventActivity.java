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
package com.team11.ditto.habit_event;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.team11.ditto.R;
import com.team11.ditto.habit_event.HabitEvent;

/**
 * Activity to view a Habit Event
 * @author Kelly Shih, Aidan Horemans
 */
public class ViewEventActivity extends AppCompatActivity {

    //Declarations
    HabitEvent habitEvent;
    TextView habitTitle;
    TextView habitComment;
    String title;
    String comment;

    /**
     * Instructions for creating the Activity
     * -display title & comment
     * @param savedInstanceState current app state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);
        habitTitle = findViewById(R.id.habit_title);
        habitComment = findViewById(R.id.habit_comment);

        //get the passed habit event
        habitEvent = (HabitEvent) getIntent().getSerializableExtra("EXTRA_HABIT_EVENT");

        //set title
        title = habitEvent.getHabitTitle();
        habitTitle.setText(title);

        //set comment
        comment = habitEvent.getComment();
        habitComment.setText(comment);

    }
}