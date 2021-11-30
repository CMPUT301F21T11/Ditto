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
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.core.app.ActivityScenario;

import org.junit.Test;

public class SearchForUsersTest {

    @Test
    public void SearchTest(){
        // Search for user and see if it was found
        ActivityScenario<UserProfileActivity> activityScenario = ActivityScenario.launch(UserProfileActivity.class);
        onView(withId(R.id.search_users)).perform(click());
        onView(withId(R.id.search_user)).perform(click());//click on search icon
        onView(withId(R.id.search_user)).perform(typeText("Reham"));
        onView(withId(R.id.search_user_name)).check(matches(withText("Reham")));
    }
}
