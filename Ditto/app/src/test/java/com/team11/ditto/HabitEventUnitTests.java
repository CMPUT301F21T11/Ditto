package com.team11.ditto;

import org.junit.Test;

import static org.junit.Assert.*;

import android.util.Log;

import com.team11.ditto.habit_event.HabitEvent;

import java.util.ArrayList;
import java.util.Comparator;
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
        Date date = new Date();
        HabitEvent first = new HabitEvent("zeventID","zhabitID", "zcomment",
                "zphoto", (List<Double>) loc, "ztitle", "zuid", "zname", date);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Date date2 = new Date();
        HabitEvent second = new HabitEvent("eventID2","habitID2", "comment2",
                "photo2", (List<Double>) loc, "title2", "uid2", "name2", date2);

        events.add(second);
        events.add(first);

        events.sort(new Comparator<HabitEvent>() {
            @Override
            public int compare(HabitEvent habitEvent, HabitEvent t1) {
                return habitEvent.getDate().compareTo(t1.getDate());
            }
        });

        ArrayList<HabitEvent> events2 = new ArrayList<>();
        events2.add(first);
        events2.add(second);

       assertTrue(events.equals(events2));
    }
}