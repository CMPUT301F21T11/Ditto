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
package com.team11.ditto.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.team11.ditto.profile_details.User;

/**
 * Represents the User who is currently logged in
 * @author Courtenay Laing-Kobe, Matt Asgari Motlagh
 */
public class ActiveUser extends User {

    static ActiveUser activeUser;
    FirebaseUser fbUser;

    /**
     * Constructor for the ActiveUser
     * @param user : takes an argument of the user object related to the person logged in
     */
    private ActiveUser(User user){
        activeUser = (ActiveUser) user;
        fbUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public ActiveUser() {
        fbUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    /**
     * Gets the current ActiveUser
     * @return the current ActiveUser if there is one, otherwise null
     */
    public @Nullable ActiveUser getActiveUser(){
        if (activeUser == null) {
            return null;
        }
        else{
            return activeUser;
        }
    }

    /**
     * Changes the user object associated with the ActiveUser
     * @param user : change the ActiveUser to the new object 'user'
     * @throws RuntimeException when previous User is not logged out
     */
    public void setActiveUser(@NonNull User user) {
        if (getActiveUser() == null) {
            activeUser = new ActiveUser(user);
        }
        else{
            throw new RuntimeException("Active User Error");
        }
    }

    /**
     * Returns the UID of the signed in user on Firebase
     * @return String : String form of the current user UID
     */
    public String getUID() {
        if (fbUser != null) {
            return fbUser.getUid();
        } else {
            return "";
        }
    }

    /**
     * Sets the ActiveUser to null
     * Logs the user out of Firebase
     */
    public void logout(){
        FirebaseAuth.getInstance().signOut();
        activeUser = null;
    }

    /**
     * Get the email  of active user
     * @return String : String form of current user email
     */
    public String getEmail(){
        if(fbUser != null){
            return fbUser.getEmail();
        }
        else{
            return "";
        }
    }
}
