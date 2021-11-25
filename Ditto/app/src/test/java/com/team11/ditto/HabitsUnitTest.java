package com.team11.ditto;

import static org.junit.Assert.*;

import android.util.Log;

import com.team11.ditto.habit.Habit;

import org.junit.Test;

import java.util.ArrayList;

public class HabitsUnitTest {

    //Constructor 1
    @Test
    public void makeHabit(){
        ArrayList<String> dates = new ArrayList<>();
        dates.add("Monday"); dates.add("Thursday");
        Habit habit = new Habit("Go Running", "To get healthy", dates, true);

        assertEquals(habit.getHabitID(), "");
        assertEquals(habit.getTitle(), "Go Running");
        assertEquals(habit.getReason(), "To get healthy");
        assertEquals(habit.getDates(), dates);
        //get streak here when thats done
        assertTrue(habit.isPublic());

    }

    //Constructor 2
    @Test
    public void makeHabitWithID(){

    }

}
