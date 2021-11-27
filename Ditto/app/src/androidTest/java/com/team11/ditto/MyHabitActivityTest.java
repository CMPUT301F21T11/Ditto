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
import static androidx.test.InstrumentationRegistry.getTargetContext;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.Matchers.allOf;

import android.content.ComponentName;

import androidx.test.espresso.Espresso;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.team11.ditto.habit.ViewHabitActivity;
import com.team11.ditto.login.SignUpActivity;

import org.junit.Rule;
import org.junit.Test;

public class MyHabitActivityTest {
    @Rule
    public ActivityScenarioRule<MyHabitActivity> activityRule = new ActivityScenarioRule<>(MyHabitActivity.class);

    @Test
    public void addHabitButton(){
        Espresso.onView(ViewMatchers.withId(R.id.add_habit)).perform(click());
        onView(withId(R.id.addHabitFragement)).check(matches(isDisplayed()));

    }

//    @Test
//    public void viewHabit(){
//        onData(anything()).inAdapterView(allOf(withId(R.id.list), isDisplayed())).atPosition(0).perform(click());
//        onView(withId(R.id.list)).check(matches(hasDescendant(withText("How well am I tracking?"))));
//    }


}
