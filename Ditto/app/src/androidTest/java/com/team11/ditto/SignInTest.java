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

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import com.team11.ditto.login.SignInActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)

public class SignInTest {
    public static String email = "albakoum@ualberta.ca";
    public static String wrongEmail = "albakoum";
    public static String password = "1234567890";
    public static String wrongPassword = "1234567";
    @Rule
    public ActivityScenarioRule<SignInActivity> activityRule = new ActivityScenarioRule<>(SignInActivity.class);

    @Test
    public void successLogin(){
        //test if the user able to login with correct information
        onView(withId(R.id.login_email_field)).perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.login_password_field)).perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        Espresso.onView(withId(R.id.list_habit_event));
    }

    @Test
    public void wrongPasssword(){
        //test if the user able to login with a wrong password
        onView(withId(R.id.login_email_field)).perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.login_password_field)).perform(typeText(wrongPassword), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        Espresso.onView(withId(R.id.login_button));
    }

    @Test
    public void wrongEmail(){
        //test if the user able to login with a wrong email
        onView(withId(R.id.login_email_field)).perform(typeText(wrongEmail), closeSoftKeyboard());
        onView(withId(R.id.login_password_field)).perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());
        Espresso.onView(withId(R.id.login_button));
    }

}
