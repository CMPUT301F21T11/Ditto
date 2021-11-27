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
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;

import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;

import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import com.team11.ditto.login.SignUpActivity;
import org.junit.Rule;
import org.junit.Test;

public class SignUpTest {
    public static String name;
    public static String email;
    public static String password;
    public static String confirmPassword;

    @Rule
    public ActivityScenarioRule<SignUpActivity> activityRule = new ActivityScenarioRule<>(SignUpActivity.class);

    @Test
    public void emptyName(){
        //test weather an error is display when the name field left empty
        name = "";
        email = "testsuccess@gmail.com";
        password = "1234567890";
        confirmPassword = "1234567890";
        onView(withId(R.id.signup_name_field)).perform(typeText(name), closeSoftKeyboard());
        onView(withId(R.id.signup_email_field)).perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.signup_password_field)).perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.signup_password_confirm_field)).perform(typeText(confirmPassword), closeSoftKeyboard());
        onView(withId(R.id.register_button)).perform(click());
        onView(withId(R.id.signup_name_field)).check(matches(hasErrorText("Name is required.")));
    }
    @Test
    public void emptyEmail() {
        //test weather an error is display when the email field left empty
        name = "Reham";
        email = "";
        password = "1234567890";
        confirmPassword = "1234567890";
        onView(withId(R.id.signup_name_field)).perform(typeText(name), closeSoftKeyboard());
        onView(withId(R.id.signup_email_field)).perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.signup_password_field)).perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.signup_password_confirm_field)).perform(typeText(confirmPassword), closeSoftKeyboard());
        onView(withId(R.id.register_button)).perform(click());
        onView(withId(R.id.signup_email_field)).check(matches(hasErrorText("email is required.")));
    }

    @Test
    public void emptyPassowrd() {
        //test weather an error is display when the password field left empty
        name = "Reham";
        email = "testsuccess@gmail.com";
        password = "";
        confirmPassword = "1234567890";
        onView(withId(R.id.signup_name_field)).perform(typeText(name), closeSoftKeyboard());
        onView(withId(R.id.signup_email_field)).perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.signup_password_field)).perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.signup_password_confirm_field)).perform(typeText(confirmPassword), closeSoftKeyboard());
        onView(withId(R.id.register_button)).perform(click());
        onView(withId(R.id.signup_password_field)).check(matches(hasErrorText("Password is required.")));
    }

    @Test
    public void emptyConfirmPassowrd() {
        //test weather an error is display when the confirm password field left empty
        name = "Reham";
        email = "testsuccess@gmail.com";
        password = "1234567890";
        confirmPassword = "";
        onView(withId(R.id.signup_name_field)).perform(typeText(name), closeSoftKeyboard());
        onView(withId(R.id.signup_email_field)).perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.signup_password_field)).perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.signup_password_confirm_field)).perform(typeText(confirmPassword), closeSoftKeyboard());
        onView(withId(R.id.register_button)).perform(click());
        onView(withId(R.id.signup_password_confirm_field)).check(matches(hasErrorText("Confirm Password is required.")));
    }

    @Test
    public void shortPassowrd() {
        //test weather an error is display when the password is les than 8 characters
        name = "Reham";
        email = "testsuccess@gmail.com";
        password = "1234567";
        confirmPassword = "1234567";
        onView(withId(R.id.signup_name_field)).perform(typeText(name), closeSoftKeyboard());
        onView(withId(R.id.signup_email_field)).perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.signup_password_field)).perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.signup_password_confirm_field)).perform(typeText(confirmPassword), closeSoftKeyboard());
        onView(withId(R.id.register_button)).perform(click());
        onView(withId(R.id.signup_password_field)).check(matches(hasErrorText("Password must be at least 8 characters.")));
    }

    @Test
    public void missMatchPassowrds() {
        //test weather an error is display when the password is les than 8 characters
        name = "Reham";
        email = "testsuccess@gmail.com";
        password = "12345678";
        confirmPassword = "1234567";
        onView(withId(R.id.signup_name_field)).perform(typeText(name), closeSoftKeyboard());
        onView(withId(R.id.signup_email_field)).perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.signup_password_field)).perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.signup_password_confirm_field)).perform(typeText(confirmPassword), closeSoftKeyboard());
        onView(withId(R.id.register_button)).perform(click());
        onView(withId(R.id.signup_password_confirm_field)).check(matches(hasErrorText("Password does not match.")));
    }
    @Test
    public void successSignUp(){
        //test if the user able to register with correct information
        name = "Reham";
        email = "testsuccess@gmail.com";
        password = "1234567890";
        confirmPassword = "1234567890";
        onView(withId(R.id.signup_name_field)).perform(typeText(name), closeSoftKeyboard());
        onView(withId(R.id.signup_email_field)).perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.signup_password_field)).perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.signup_password_confirm_field)).perform(typeText(confirmPassword), closeSoftKeyboard());
        onView(withId(R.id.register_button)).perform(click());
        Espresso.onView(withId(R.id.list_habit_event));
    }

}
