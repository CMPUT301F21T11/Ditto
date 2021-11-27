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

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.os.SystemClock;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test class for MainActivityTest for the Bottom Tab switching. Espresso test framework is used
 * TODO: check if the current tab we clicked on takes us to the right activity
 */
@RunWith(AndroidJUnit4ClassRunner.class)
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);


    /**
     * test to check if the tabs change
     */
    @Test
    public void changTab() {
        // check if the tabs change
        onView(withId(R.id.tabs)).check(matches(isDisplayed()));
        Espresso.onView(ViewMatchers.withText("PROFILE")).perform(ViewActions.click()); // click on PROFILE TAB
        Espresso.onView(ViewMatchers.withText("DUE")).perform(ViewActions.click()); // click on PROFILE TAB
        Espresso.onView(ViewMatchers.withText("HABITS")).perform(ViewActions.click()); // click on PROFILE TAB
        Espresso.onView(ViewMatchers.withText("HOME")).perform(ViewActions.click()); // click on PROFILE TAB
        SystemClock.sleep(5000);
        onView(withId(R.id.add_habit_event)).perform(click());
        SystemClock.sleep(5000);
    }

    @Test
    public void addHabitEventButton() {
        // check if the plus button for adding a habit event works
        Espresso.onView(ViewMatchers.withId(R.id.add_habit_event)).perform(ViewActions.click());
        SystemClock.sleep(5000);

    }

    @After
    public void tearDown() throws Exception {

    }

}