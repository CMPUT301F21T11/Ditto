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
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;

import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

import android.os.SystemClock;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;

import org.junit.After;
import org.junit.Test;

public class DeleteHabitTest {
    @Test
    public void delete() {
        // Add a habit then delete it and check if it still exists
        ActivityScenario<MyHabitActivity> activityScenario = ActivityScenario.launch(MyHabitActivity.class);
        onView(withId(R.id.add_habit)).perform(click());//open the add habit fragment
        ViewActions.closeSoftKeyboard();
        // Add the details of the habit
        onView(withId(R.id.title_editText)).perform(typeText("Habit to delete"));
        onView(withId(R.id.reason_editText)).perform(typeText("To test deleting"));
        ViewActions.closeSoftKeyboard();
        onView(withId(R.id.privacySwitch)).check(matches(isChecked()));
        onView(withText("ADD")).perform(click()); // Click add
        SystemClock.sleep(3000);
        // check if it is displayed
        onView(withText("Habit to delete")).check(matches(isDisplayed()));
        // delete the habit
        onView(withText("Habit to delete")).perform(swipeLeft());
        // check if it's still there
        onView(withText("Habit to delete")).check(doesNotExist());
    }
}
