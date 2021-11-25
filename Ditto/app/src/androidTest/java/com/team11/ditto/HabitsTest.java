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
/*
 Test class for MyHabitActivity. All the UI tests are written here. Espresso test framework is
 used
 TODO: Future tests:
  public void testEditHabitTitle() //tests the edit function which will be implemented
  public void testViewPersists() //tests that the view updates the updated data
  check for repeated Habit titles!!!
  public void habitEventDeleted() //when you delete a habit activity, it should also delete the associated habit events

  @author Kelly Shih, Aidan Horemans
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import android.app.Activity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import com.robotium.solo.Solo;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.team11.ditto.habit.AddHabitFragment;
import com.team11.ditto.habit.Habit;
import com.team11.ditto.habit.HabitRecyclerAdapter;
import com.team11.ditto.habit_event.HabitEventRecyclerAdapter;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicReference;

public class HabitsTest {

    private Solo solo;

    @Rule
    public ActivityTestRule<MyHabitActivity> rule = new ActivityTestRule<>(MyHabitActivity.class, true, true);

    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @Test
    public void start() throws Exception{
        Activity activity = rule.getActivity();
    }

    /**
     * Add to all fields of the add fragment
//     */
    @Test
    public void testAddHabit() {
        String title = "Running";
        String reason = "Get healthy";

        solo.assertCurrentActivity("Wrong Activity", MyHabitActivity.class);

        MyHabitActivity activity = (MyHabitActivity) solo.getCurrentActivity();

        solo.clickOnView(activity.findViewById(R.id.add_habit));

        solo.enterText((EditText) solo.getView(R.id.title_editText), title);
        solo.enterText((EditText) solo.getView(R.id.reason_editText), reason);

        CheckBox monday = (CheckBox) solo.getView(R.id.monday_select);
        CheckBox friday = (CheckBox) solo.getView(R.id.friday_select);

        solo.clickOnView(monday);
        solo.clickOnView(friday);

        solo.clickOnText("ADD");

        RecyclerView recyclerView = activity.findViewById(R.id.list);
        HabitRecyclerAdapter habitRecyclerAdapter = (HabitRecyclerAdapter) recyclerView.getAdapter();

        Habit habit = (Habit) habitRecyclerAdapter.getItemAt(habitRecyclerAdapter.getItemCount() - 1);

        assertEquals("Running", habit.getTitle());
        assertEquals("Get healthy", habit.getReason());


    }


    //
    private void deleteHabit(String text) {
        int x1, x2, y;
        int[] location = new int[2];

        View row = solo.getText(text);
        row.getLocationInWindow(location);

        x1 = location[0] + 10;
        y = location[1];

        x2 = location[0];

        solo.drag(x1, x2, y, y, 10);
    }


    /**
     * Test the swipe action to delete
     */
    @Test
    public void testDeleteHabit() {
        //first add a habit then delete
        String title = "Read a book";
        String reason = "Become literate";

        solo.assertCurrentActivity("Wrong Activity", MyHabitActivity.class);

        MyHabitActivity activity = (MyHabitActivity) solo.getCurrentActivity();

        solo.clickOnView(activity.findViewById(R.id.add_habit));

        solo.enterText((EditText) solo.getView(R.id.title_editText), title);
        solo.enterText((EditText) solo.getView(R.id.reason_editText), reason);

        CheckBox tuesday = (CheckBox) solo.getView(R.id.tuesday_select);

        solo.clickOnView(tuesday);

        solo.clickOnText("ADD");

        deleteHabit(title);



        //open the add habit fragment

    }

    /**
     * just add the title test
     */
    @Test
    public void testAddTitle() {
        /*String title = "Eating";

        //open the add habit fragment
        onView(withId(R.id.add_habit)).perform(click());

        //type in title and reason
        onView(withId(R.id.title_editText)).perform(typeText(title));

        //click add
        onView(withText("ADD")).perform(click());

        //Check eating exists
        onView(withId(R.id.list)).check(matches(hasDescendant(withText("Eating"))));*/

    }

    /**
     * TODO: Testing unable to add without a habit title
     */
    @Test
    public void testNoTitleAdd() {
        String reason = "To get abs";

        //open the add habit fragment
        /*onView(withId(R.id.add_habit)).perform(click());

        //type in title and reason
        onView(withId(R.id.reason_editText)).perform(typeText(reason));
        onView(withId(R.id.tuesday_select)).perform(click());
        onView(withId(R.id.saturday_select)).perform(click());

        //click add
        onView(withText("ADD")).perform(click());

        //Check the item didnt get added
        onView(withId(R.id.list)).check(matches(not(hasDescendant(withText("To get abs")))));*/

    }

    /**
     * test the view habit action -> click on a habit and go to a new activity
     */
//    @Test
//    public void testViewHabit() {
//        //first add a habit then view it
//        String title = "Massage";
//        String reason = "Stress relief";
//
//        //open the add habit fragment
//        onView(withId(R.id.add_habit)).perform(click());
//
//        //type in title and reason
//        onView(withId(R.id.title_editText)).perform(typeText(title));
//        onView(withId(R.id.reason_editText)).perform(typeText(reason));
//        onView(withId(R.id.tuesday_select)).perform(click());
//        onView(withId(R.id.saturday_select)).perform(click());
//
//        //click add
//        onView(withText("ADD")).perform(click());
//
//        //click and view activity for habit
//        onView(withId(R.id.list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
//
//        //Checking that the activity switched
//        onView(withId(R.id.tracking)).check(matches(isDisplayed()));
//        onView(withId(R.id.habit_reason)).check(matches(withText("Stress relief")));
//    }

    /**
     * test if editing the details of a habit crashes
     */
//    @Test
//    public void testEditHabitDetails() {
//        //make sure there is a habit in the listview to start
//        //first add a habit then view it
//        String title = "Eat cake";
//        String reason = "Stress relief";
//
//        //open the add habit fragment
//        onView(withId(R.id.add_habit)).perform(click());
//
//        //type in title and reason
//        onView(withId(R.id.title_editText)).perform(typeText(title));
//        onView(withId(R.id.reason_editText)).perform(typeText(reason));
//        onView(withId(R.id.tuesday_select)).perform(click());
//        onView(withId(R.id.saturday_select)).perform(click());
//
//        //click add
//        onView(withText("ADD")).perform(click());
//
//        onView(withId(R.id.list)).check(matches(hasDescendant(withText("Eat cake"))));
//
//        //click on a habit
//        onView(withId(R.id.list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
//
//
//        //click on the edit button
//        onView(withId(R.id.edit_habit)).perform(click());
//
//        //edit the reason and dates
//        String newreason = "Birthday";
//        onView(withId(R.id.reason_editText)).perform(replaceText("")); //delete old text
//        onView(withId(R.id.reason_editText)).perform(typeText(newreason));
//
//        //click on one of the same date and unclick one, add new cases
//        //tuesday should still be clicked
//        onView(withId(R.id.saturday_select)).perform(click()); //basically unclick
//        onView(withId(R.id.wednesday_select)).perform(click()); //add a new one
//
//        onView(withText("UPDATE")).perform(click());
//
//        onView(withId(R.id.habit_reason)).check(matches(withText(reason)));
//
//
//
//    }


    /*
    Future tests:
     * public void testEditHabitTitle() //tests the edit function which will be implemented
    * public void testViewPersists() //tests that the view updates the updated data
    * check for repeated Habit titles!!!
    * public void habitEventDeleted() //when you delete a habit activity, it should also delete the associated habit events
     */



}
