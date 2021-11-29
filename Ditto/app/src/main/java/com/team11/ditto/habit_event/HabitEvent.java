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

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

/**
 * Role: Habit Event Object that stores the data for a habit event's
 * habitID
 * comment
 * photograph
 * location
 * eventID
 * uid
 * name
 * @author Kelly Shih, Aidan Horemans
 */

public class HabitEvent implements Serializable, Comparable {
    private String eventID;
    private String habitId;
    private final String habitTitle;
    private String comment;
    private String photo;
    private String uid;
    private String name;
    private List<Double> location;
    private Date date;

    /**
     * Constructor for pre-existing event
     * @param habitId Id of the Habit whose HabitEvent this is
     * @param comment optional comment (may be empty string)
     * @param photo optional photo (may be empty string)
     * @param location optional location given in a list of doubles
     * @param habitTitle Title of the Habit whose HabitEvent this is
     * @param uid user id of the user this event is attached to
     * @param name the name of the user this event is attached to
     */
    public HabitEvent(String eventID, String habitId, String comment, String photo,
                      @Nullable List<Double> location, String habitTitle, String uid, String name, Date date) {

        this.eventID = eventID;
        this.habitId = habitId;
        this.comment = comment;
        this.photo = photo;
        this.location = location;
        this.habitTitle = habitTitle;
        this.uid = uid;
        this.name = name;
        this.date = date;
    }

    /**
     * Constructor for new event
     * @param habitId Id of the Habit whose HabitEvent this is
     * @param comment optional comment (may be empty string)
     * @param photo optional photo (may be empty string)
     * @param location optional location (may be empty string)
     * @param habitTitle Title of the Habit whose HabitEvent this is
     */
    public HabitEvent(String habitId, String comment, String photo, @Nullable List<Double> location, String habitTitle) {
        this.habitId = habitId;
        this.comment = comment;
        this.photo = photo;
        this.location = location;
        this.habitTitle = habitTitle;
        setDate();
    }

    /**
     * Getter for the habit's user's name
     * @return a name as a string
     */
    public String getName(){
        return name;
    }

    /**
     * Setter for the habit's user's name
     * @param name a name as a string
     */
    public void setName(String name){
        this.name = name;
    }

    /**
     * Getter for the habit's user's id
     * @return user's id as a string
     */
    public String getUid(){
        return uid;
    }

    /**
     * Getter for the Event's ID
     * @return  Event's ID
     */
    public String getEventID() {
        return eventID;
    }

    /**
     * Setter for the Event's ID
     * @param eventID the ID
     */
    public void setEventID(String eventID) {
        this.eventID = eventID;
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
    public List<Double> getLocation() {
        return location;
    }

    /**
     * Setter for location
     * @param location new location string
     */
    public void setLocation(List<Double> location) {
        this.location = location;
    }

    /**
     * Getter for parent Habit title
     * @return parent Habit's title
     */
    public String getHabitTitle() {
        return habitTitle;
    }


    /**
     *Sets the timestamp for the HabitEvent post
     */
    private void setDate(){
        this.date = new Date();
    }

    /**
     *Sets the timestamp for the HabitEvent post
     */
    public Date getDate(){
        return this.date;
    }

    /**
     *
     * @param o Other event object
     * @return comparison of their timestamps
     */
    @Override
    public int compareTo(Object o) {
        return (date).compareTo(((HabitEvent) o).date);
    }
}
