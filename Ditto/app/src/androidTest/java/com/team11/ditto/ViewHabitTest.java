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
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.anything;

import static java.util.EnumSet.allOf;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.team11.ditto.habit.EditHabitFragment;
import com.team11.ditto.habit.ViewHabitActivity;

import org.junit.Rule;
import org.junit.Test;


public class ViewHabitTest {
//    @Rule
//    public ActivityScenarioRule<ViewHabitActivity> activityRule = new ActivityScenarioRule<>(ViewHabitActivity.class);


    @Test
    public void editHabitButton(){
        // Test if the edit button displays the edit habit fragment
        ActivityScenario<MyHabitActivity> activityScenario = ActivityScenario.launch(MyHabitActivity.class);
        onView(withId(R.id.list)).perform(RecyclerViewActions.actionOnItemAtPosition(0,click()));
        onView(withId(R.id.edit_habit)).perform(click());
        onView(withId(R.id.editHabitFragment)).check(matches(isDisplayed()));
    }

    @Test
    public void ifHabitTrackingDisplayed(){
        // Check if the field "How well am I tracking?" is displayed
        ActivityScenario<MyHabitActivity> activityScenario = ActivityScenario.launch(MyHabitActivity.class);
        onView(withId(R.id.list)).perform(RecyclerViewActions.actionOnItemAtPosition(0,click()));
        Espresso.onView(withText("How well am I tracking?"));
    }

    @Test
    public void ifDaysDisplayed(){
        // Check if the days af the habit are displayed
        ActivityScenario<MyHabitActivity> activityScenario = ActivityScenario.launch(MyHabitActivity.class);
        onView(withId(R.id.list)).perform(RecyclerViewActions.actionOnItemAtPosition(0,click()));
        Espresso.onView(withText("Days"));
    }

    @Test
    public void ifReasonDisplayed(){
        // Check if the reason af the habit is displayed
        ActivityScenario<MyHabitActivity> activityScenario = ActivityScenario.launch(MyHabitActivity.class);
        onView(withId(R.id.list)).perform(RecyclerViewActions.actionOnItemAtPosition(0,click()));
        Espresso.onView(withText("Reason"));
    }

}
