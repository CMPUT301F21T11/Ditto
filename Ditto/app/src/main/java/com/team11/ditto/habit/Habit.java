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
package com.team11.ditto.habit;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Purpose: Habit class represents a habit object and holds data for
 * title
 * reason
 * dates
 * habitID
 * isPublic
 * position
 * habitDoneToday
 * Design Rationale: set getters and setters for the data that Habit holds
 * @author Kelly Shih, Aidan Horemans
 */

public class Habit implements Serializable {

    //Attributes
    private String habitID;
    private String title;
    private String reason;
    private ArrayList<String> dates;
    private boolean isPublic;
    private int streak;
    private int position;
    private boolean habitDoneToday;

    /**
     * Constructor for Habit object
     * @param title Habit title
     * @param reason Reason for habit
     * @param dates Days of the week for scheduling
     */
    public Habit(String title, String reason, ArrayList<String> dates, boolean isPublic) {
        this.title = title;
        this.reason = reason;
        this.setDate(dates);
        this.isPublic = isPublic;
        this.streak = 0; //Basic habit, not in db yet means it is BRAND NEW
        this.habitID = "";
    }

    /**
     * Constructor for Habit object
     * @param id Habit id
     * @param title Habit title
     * @param reason Reason for habit
     * @param dates Days of the week for scheduling
     */
    public Habit(String id, String title, String reason, ArrayList<String> dates, boolean isPublic, int streak, boolean habitDoneToday) {
        this.habitID = id;
        this.title = title;
        this.reason = reason;
        this.setDate(dates);
        this.isPublic = isPublic;
        this.streak = streak;
        this.habitDoneToday = habitDoneToday;
    }

    /**
     * Constructor for habit object
     * Its purpose is to show all habit objects of users that are followed by active user
     * @param title title of the habit
     * @param reason reason for habit
     */
    public Habit(String title, String reason, int streak, int position){
        this.title = title;
        this.reason = reason;
        this.streak = streak;
        this.position = position;
    }

    public int getStreak(){
        return this.streak;
    }

    public void setStreak(int streak){
      this.streak = streak;
    }


    /**
     * Getter for Habit title
     * @return Habit title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Setter for Habit Title
     * @param title new title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Getter for reason
     * @return Habit's reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * Setter for reason
     * @param reason updated reason
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * Getter for days scheduled
     * @return list of ints for the days of the week Habit is scheduled for (1-7)
     */
    public ArrayList<String> getDates() {
        if (this.dates == null){
            throw new RuntimeException("dates empty");
        }
        else{
            return dates;
        }
    }

    /**
     * Setter for Habit schedule days
     * @param dates list of ints for the days of the week for Habit's NEW schedule (1-7)
     */
    public void setDate(ArrayList<String> dates) {
        this.dates = new ArrayList<>(dates);
    }

    /**
     * Getter for ID
     * @return Habit id string
     */
    public String getHabitID() {
        return habitID;
    }

    /**
     * Setter for ID
     * @param habitID new ID String
     */
    public void setHabitID(String habitID) {
        this.habitID = habitID;
    }

    /**
     * Getter for whether Habit is public or not
     * @return isPublic boolean
     */
    public boolean isPublic(){ return this.isPublic; }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * Setter for whether Habit is public or not
     * switches isPublic bool to opposite
     */
    public void changePrivacy(){
        this.isPublic = !this.isPublic;
    }

    public void completeHabit(boolean complete){
        if (complete && this.streak > 0){
            this.streak ++;
        }
        else if (complete && this.streak <= 0){
            this.streak = 1;
        }
        else if (!complete && this.streak <= 0){
            this.streak--;
        }
        else if (!complete && this.streak > 0){
            this.streak = 0;
        }

    }

    /**
     * Getter for whether Habit is done today or not
     * @return
     */
    public boolean isHabitDoneToday() {
        return habitDoneToday;
    }

    /**
     * Setter for whether Habit is done today or not
     * @param habitDoneToday
     */
    public void setHabitDoneToday(boolean habitDoneToday) {
        this.habitDoneToday = habitDoneToday;
    }
}
