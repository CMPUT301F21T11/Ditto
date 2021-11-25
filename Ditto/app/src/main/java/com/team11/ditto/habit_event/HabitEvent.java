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
package com.team11.ditto.habit_event;

import java.io.Serializable;

/**
 * Role: Habit Event Object that stores the data for a habit event's
 * habitID
 * comment
 * photograph
 * location
 *
 * @author Kelly Shih, Aidan Horemans
 */

public class HabitEvent implements Serializable {
    private String habitId;
    private final String habitTitle;
    private String comment;
    private String photo;
    private String location;

    /**
     * Constructor
     * @param habitId Id of the Habit whose HabitEvent this is
     * @param comment optional comment (maybe empty string)
     * @param photo optional photo (may be empty string)
     * @param location optional location (may be empty string)
     * @param habitTitle Title of the Habit whose HabitEvent this is
     */
    public HabitEvent(String habitId, String comment, String photo, String location, String habitTitle) {
        this.habitId = habitId;
        this.comment = comment;
        this.photo = photo;
        this.location = location;
        this.habitTitle = habitTitle;
    }

    /**
     * Getter for parent Habit ID
     * @return parent Habit's ID
     */
    public String getHabitId() {
        return habitId;
    }

    /**
     * Setter for parent Habit ID
     * @param habitId ID for the parent Habit
     */
    public void setHabitId(String habitId) {
        this.habitId = habitId;
    }

    /**
     * Getter for the comment
     * @return this' comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * Setter for comment
     * @param comment new comment string
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Getter for event photo
     * @return string event photo
     */
    public String getPhoto() {
        return photo;
    }

    /**
     * Setter for event photo
     * @param photo string new photo
     */
    public void setPhoto(String photo) {
        this.photo = photo;
    }

    /**
     * Getter for location
     * @return this location string
     */
    public String getLocation() {
        return location;
    }

    /**
     * Setter for location
     * @param location new location string
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Getter for parent Habit title
     * @return parent Habit's title
     */
    public String getHabitTitle() {
        return habitTitle;
    }
}
