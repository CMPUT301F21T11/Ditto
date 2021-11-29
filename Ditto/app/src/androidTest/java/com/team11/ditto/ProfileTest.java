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

import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.os.SystemClock;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;

import org.junit.Test;

public class ProfileTest {
    @Test
    public void ifSearch4UserDisplayed(){
        // Check if the field "Search for users" is displayed
        ActivityScenario<UserProfileActivity> activityScenario = ActivityScenario.launch(UserProfileActivity.class);
        onView(withId(R.id.search_users)).check(matches(isDisplayed()));
    }

    @Test
    public void ifFollowSentDisplayed(){
        // Check if the field "Follow request sent" is displayed
        ActivityScenario<UserProfileActivity> activityScenario = ActivityScenario.launch(UserProfileActivity.class);
        onView(withId(R.id.follow_request_sent)).check(matches(isDisplayed()));
    }

    @Test
    public void ifFollowReceivedDisplayed(){
        // Check if the field "Follow request received" is displayed
        ActivityScenario<UserProfileActivity> activityScenario = ActivityScenario.launch(UserProfileActivity.class);
        onView(withId(R.id.pending_fr)).check(matches(isDisplayed()));
    }

    @Test
    public void ifLogoutBtnDisplayed(){
        // Check if the field "Logout button" is displayed
        ActivityScenario<UserProfileActivity> activityScenario = ActivityScenario.launch(UserProfileActivity.class);
        onView(withId(R.id.logout_button)).check(matches(isDisplayed()));
    }
    @Test
    public void ifProfilePhotoDisplayed(){
        // Check if the profile picture is displayed
        ActivityScenario<UserProfileActivity> activityScenario = ActivityScenario.launch(UserProfileActivity.class);
        onView(withId(R.id.profilePhoto)).check(matches(isDisplayed()));
    }
    @Test
    public void ifFollowersFieldDisplayed(){
        // Check if the followers field is displayed
        ActivityScenario<UserProfileActivity> activityScenario = ActivityScenario.launch(UserProfileActivity.class);
        onView(withId(R.id.followers)).check(matches(isDisplayed()));
    }

    @Test
    public void ifFollowingFieldDisplayed(){
        // Check if the following field is displayed
        ActivityScenario<UserProfileActivity> activityScenario = ActivityScenario.launch(UserProfileActivity.class);
        onView(withId(R.id.following)).check(matches(isDisplayed()));
    }
    @Test
    public void ifFollowingNumberDisplayed(){
        // Check if the number of following is displayed
        ActivityScenario<UserProfileActivity> activityScenario = ActivityScenario.launch(UserProfileActivity.class);
        onView(withId(R.id.no_following_1)).check(matches(isDisplayed()));
    }

    @Test
    public void ifFollowersNumberDisplayed(){
        // Check if the number of followers is displayed
        ActivityScenario<UserProfileActivity> activityScenario = ActivityScenario.launch(UserProfileActivity.class);
        onView(withId(R.id.no_followers)).check(matches(isDisplayed()));
    }

    @Test
    public void ifUsernameDisplayed(){
        // Check if username is displayed
        ActivityScenario<UserProfileActivity> activityScenario = ActivityScenario.launch(UserProfileActivity.class);
        onView(withId(R.id.textView_user)).check(matches(isDisplayed()));
    }

    @Test
    public void ifNameDisplayed(){
        // Check if Name is displayed
        ActivityScenario<UserProfileActivity> activityScenario = ActivityScenario.launch(UserProfileActivity.class);
        onView(withId(R.id.username_editText)).check(matches(isDisplayed()));
    }





}
