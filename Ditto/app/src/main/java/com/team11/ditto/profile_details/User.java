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
package com.team11.ditto.profile_details;

import android.graphics.drawable.Drawable;

public class User {

    //User attributes
    private String id;
    private String username;
    private Drawable profilePhoto;
    private String email;


    /**
     * Constructor for a User object
     * @param username user chosen self-identifying string
     * @param email user email address
     *
     * Other attributes initialized to defaults can be added to or changed later
     */
    public User(String username, String email){
        this.username = username;
        this.profilePhoto = Drawable.createFromPath("ic_action_profile.png");
        this.email = email;
    }

    /**
     * Constructor for a User object
     * @param username user chosen self-identifying string
     * @param email user email address
     *
     * Other attributes initialized to defaults can be added to or changed later
     */
    public User(String username, String email, String id){
        this.username = username;
        this.profilePhoto = Drawable.createFromPath("ic_action_profile.png");
        this.email = email;
        this.id = id;
    }

    /**
     * Empty constructor to allow for ActiveUser flexibility
     */
    protected User(){
    }

    /**
     * Getter for username
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Getter for password
     * @return password
     */
    public String getEmail() {
        return email;
    }

    /**
     * Getter for photo
     * @return photo
     */
    public Drawable getProfilePhoto() {
        return profilePhoto;
    }

    /**
     * Setter for photo
     * @param profilePhotoPath string; path to new photo
     */
    public void setProfilePhoto(String profilePhotoPath) {
        this.profilePhoto = Drawable.createFromPath(profilePhotoPath);
    }

    /**
     * Getter for id
     * @return id existing id
     */
    public String getID(){
        return this.id;
    }



}

