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
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.os.SystemClock;


import org.junit.Rule;
import org.junit.Test;

public class DueTodayTest {
    @Test
    public void dueTodayDisplay() {
        // Add a habit due today and test if it's displayed in due today activity
        ActivityScenario<MyHabitActivity> activityScenario = ActivityScenario.launch(MyHabitActivity.class);
        onView(withId(R.id.add_habit)).perform(click()); //open the add habit fragment
        // Add the details of the habit
        onView(withId(R.id.title_editText)).perform(typeText("Testing my app"));
        onView(withId(R.id.reason_editText)).perform(typeText("To make it professional"));
        onView(withText("ADD")).perform(click()); // Click add
        SystemClock.sleep(3000);
        onView(withText("Testing my app")).perform(click());
        onView(withId(R.id.edit_habit)).perform(click());
        onView(withId(R.id.monday_box)).perform(click());
        onView(withId(R.id.tuesday_box)).perform(click());
        onView(withId(R.id.wedensday_box)).perform(click());
        onView(withId(R.id.thursday_box)).perform(click());
        onView(withId(R.id.friday_box)).perform(click());
        onView(withId(R.id.saturday_box)).perform(click());
        onView(withId(R.id.sunday_box)).perform(click());
        onView(withText("UPDATE")).perform(click());

        // Check if the habit was added to due today habits
        ActivityScenario<DueTodayActivity> activityScenario1 = ActivityScenario.launch(DueTodayActivity.class);
        onView(withText("Testing my app")).check(matches(isDisplayed()));
        // delete the habit
        ActivityScenario<MyHabitActivity> activityScenario2 = ActivityScenario.launch(MyHabitActivity.class);
        onView(withText("Testing my app")).perform(swipeLeft());

    }
}
