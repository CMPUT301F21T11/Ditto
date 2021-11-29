package com.team11.ditto;

import org.junit.Test;

import static org.junit.Assert.*;

import com.team11.ditto.habit_event.HabitEvent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class HabitEventUnitTests {
    @Test
    public void listSort(){
        ArrayList<HabitEvent> events = new ArrayList<>();
        ArrayList<Double> loc = new ArrayList<Double>();
        
        events.add(
          new HabitEvent("habitID", "comment", "photo", (List<Double>) loc, "uid", "name", new Date())
        );
    }
}