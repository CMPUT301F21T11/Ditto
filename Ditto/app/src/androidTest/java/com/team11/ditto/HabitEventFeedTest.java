/** Copyright [2021] [Reham Albakouni, Matt Asgari Motlagh, Aidan Horemans, Courtenay Laing-Kobe, Vivek Malhotra, Kelly Shih]

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
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
//import static org.hamcrest.Matchers.not;

import android.os.SystemClock;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test class for MainActivity for the Feed actions. Espresso test framework is used
 TODO: FutureTests:
 @Before
 public void clearHabitEvents()
 public void testAddPhoto()
 public void testAddGeolocation()
 public void testCantEditTitle() //make sure we cant edit a habit event title
 public void testEditHabitEvent() //make sure we can edit details like reason, photo, location, and dates for a habit event
 * @author Kelly Shih, Aidan Horemans
 */

@RunWith(AndroidJUnit4.class)
public class HabitEventFeedTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    /**
     * test the add habit event fragment for choosing habit and commenting
     * TODO: create tests for adding photos and location
     */

    /**
     * Assumes that a Habit named Running exists
     * then adds a habit event for Running
     */
//    @Test
//    public void testAddHabitEventAll() {
//        String habit = "Running";
//        String comment = "I am sweaty";
//
//        //open the add habit fragment
//        onView(withId(R.id.add_habit_event)).perform(click());
//
//        //click choose a habit, create comment, add
//        onView(withId(R.id.event_spinner)).perform(click());
//
//        SystemClock.sleep(3000);
//
//        onView(withText(habit)).inRoot(isPlatformPopup()).perform(click());
//
//        SystemClock.sleep(3000);
//
//        //check if what you chose is correct
//        onView(withId(R.id.event_spinner))
//                .check(matches(withSpinnerText(containsString("Running"))));
//
//        onView(withId(R.id.comment_editText)).perform(typeText(comment));
//
//        //click the add button
//        onView(withText("ADD")).perform(click());
//
//        onView(withId(R.id.list_habit_event)).check(matches(hasDescendant(withText(habit))));
//    }


    /**
     * Assumes that a Habit named Running exists
     * adds the habit then tests the VIEW functionality
//     */
//    @Test
//    public void testViewEvent() {
//        String habit = "Running";
//        String comment = "I am drenched in sweat";
//
//        //open the add habit fragment
//        onView(withId(R.id.add_habit_event)).perform(click());
//
//        //click choose a habit, create comment, add
//        onView(withId(R.id.event_spinner)).perform(click());
//
//        SystemClock.sleep(3000);
//
//        onView(withText(habit)).inRoot(isPlatformPopup()).perform(click());
//
//        SystemClock.sleep(3000);
//
//        //check if what you chose is correct
//        onView(withId(R.id.event_spinner))
//                .check(matches(withSpinnerText(containsString("Running"))));
//
//        onView(withId(R.id.comment_editText)).perform(typeText(comment));
//
//        //click the add button
//        onView(withText("ADD")).perform(click());
//
//        onView(withId(R.id.list_habit_event)).check(matches(hasDescendant(withText(habit))));
//
//        //Assuming no other habit events
//        onView(withId(R.id.list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
//
//        onView(withId(R.id.tracking)).check(matches(isDisplayed()));
//
//
//
//    }


}
