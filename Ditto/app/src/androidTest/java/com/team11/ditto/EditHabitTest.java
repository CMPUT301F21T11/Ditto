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
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import android.view.KeyEvent;
import android.widget.CheckBox;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;

import android.os.SystemClock;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.KeyEventAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;

import com.team11.ditto.habit.EditHabitFragment;
import com.team11.ditto.habit.ViewHabitActivity;

import org.junit.After;
import org.junit.Test;


public class EditHabitTest {
    @Test
    public void editPrivacy() {
        // Add a habit then edit its privacy
        ActivityScenario<MyHabitActivity> activityScenario = ActivityScenario.launch(MyHabitActivity.class);
        onView(withId(R.id.add_habit)).perform(click());//open the add habit fragment
        ViewActions.closeSoftKeyboard();
        // Add the details of the habit
        onView(withId(R.id.title_editText)).perform(typeText("Habit_to_edit"));
        onView(withId(R.id.reason_editText)).perform(typeText("To test editing"));
        ViewActions.closeSoftKeyboard();
        onView(withId(R.id.privacySwitch)).check(matches(isChecked()));
        onView(withText("ADD")).perform(click()); // Click add
        SystemClock.sleep(3000);
        // Switch the privacy to private
        onView(withText("Habit_to_edit")).perform(click());
        onView(withId(R.id.edit_habit)).perform(click());
        onView(withText("Public")).perform(click());
        onView(withText("UPDATE")).perform(click());
        // Check if it got changed
        onView(withId(R.id.edit_habit)).perform(click());
        onView(withText("Public")).check(matches(isNotChecked()));
        onView(withText("CANCEL")).perform(click());
        pressBack();
        // Delete the habit to clean up
        onView(withText("Habit_to_edit")).perform(swipeLeft());
    }

    @Test
    public void editReason() {
        // Add a habit then edit its reason
        ActivityScenario<MyHabitActivity> activityScenario = ActivityScenario.launch(MyHabitActivity.class);
        onView(withId(R.id.add_habit)).perform(click());//open the add habit fragment
        ViewActions.closeSoftKeyboard();
        // Add the details of the habit
        onView(withId(R.id.title_editText)).perform(typeText("Habit_to_edit"));
        onView(withId(R.id.reason_editText)).perform(typeText("To test editing"));
        ViewActions.closeSoftKeyboard();
        onView(withId(R.id.privacySwitch)).check(matches(isChecked()));
        onView(withText("ADD")).perform(click()); // Click add
        SystemClock.sleep(3000);
        // Change the reason
        onView(withText("Habit_to_edit")).perform(click());
        onView(withId(R.id.edit_habit)).perform(click());
        onView(withId(R.id.reason_editText)).perform(clearText(), typeText("Because I like testing"));
        onView(withText("UPDATE")).perform(click());
        // Check if it got changed
        onView(withId(R.id.edit_habit)).perform(click());
        onView(withText("Because I like testing")).check(matches(isDisplayed()));
        onView(withText("CANCEL")).perform(click());
        pressBack();
        // Delete the habit to clean up
        onView(withText("Habit_to_edit")).perform(swipeLeft());
    }

    @Test
    public void editDays() {
        // Add a habit with no selected days then change it
        ActivityScenario<MyHabitActivity> activityScenario = ActivityScenario.launch(MyHabitActivity.class);
        onView(withId(R.id.add_habit)).perform(click());//open the add habit fragment
        ViewActions.closeSoftKeyboard();
        // Add the details of the habit
        onView(withId(R.id.title_editText)).perform(typeText("Habit_to_edit"));
        onView(withId(R.id.reason_editText)).perform(typeText("To test editing"));
        ViewActions.closeSoftKeyboard();
        onView(withId(R.id.privacySwitch)).check(matches(isChecked()));
        // Check if monday was selected
        onView(withId(R.id.monday_select)).check(matches(isNotChecked()));
        onView(withText("ADD")).perform(click()); // Click add
        SystemClock.sleep(3000);
        // Edit the habit and select Monday
        onView(withText("Habit_to_edit")).perform(click());
        onView(withId(R.id.edit_habit)).perform(click());
        onView(withText("M")).perform(click());
        onView(withText("UPDATE")).perform(click());
        // Check if it got changed
        onView(withId(R.id.edit_habit)).perform(click());
        onView(withId(R.id.monday_select)).check(matches(isChecked()));
        onView(withText("CANCEL")).perform(click());
        pressBack();
        // Delete the habit to clean up
        onView(withText("Habit_to_edit")).perform(swipeLeft());
    }
    @After
    public void tearDown() throws Exception {

    }
    @Test
    public void editHabitButttons() {
        // Test that correct button texts are displayed
        ActivityScenario<MyHabitActivity> activityScenario = ActivityScenario.launch(MyHabitActivity.class);
        onView(withId(R.id.add_habit)).perform(click());//open the add habit fragment
        ViewActions.closeSoftKeyboard();
        // Add the details of the habit
        onView(withId(R.id.title_editText)).perform(typeText("Habit to test"));
        onView(withId(R.id.reason_editText)).perform(typeText("To test edit button"));
        onView(withText("ADD")).perform(click());
        onView(withText("Habit to test")).perform(click());
        onView(withId(R.id.edit_habit)).perform(click());
        onView(withText("UPDATE")).check(matches(isDisplayed())); // Check update button
        onView(withText("CANCEL")).check(matches(isDisplayed())); // Check cancel button
        pressBack();
        pressBack();
        onView(withText("Habit to test")).perform(swipeLeft());
    }

}
