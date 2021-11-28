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

import androidx.test.ext.junit.rules.ActivityScenarioRule;


import androidx.test.espresso.Espresso;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import org.junit.Rule;
import org.junit.Test;

public class WelcomeActivityTest {

    @Rule
    public ActivityScenarioRule<WelcomeActivity> activityRule = new ActivityScenarioRule<>(WelcomeActivity.class);

    @Test
    public void getStarted() {
        //test if get started button starts sign up activity
        onView(withId(R.id.sign_up_button)).perform(click());
        Espresso.onView(withId(R.id.signup_name_field));
    }

    @Test
    public void login() {
        //test if log in button starts sign in activity
        onView(withId(R.id.sign_in_button)).perform(click());
        Espresso.onView(withId(R.id.login_email_field));
    }



}
