package com.team11.ditto;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;

import com.team11.ditto.profile_details.User;

import org.junit.Test;

/**
 * Tests for the User Class functionalities
 * @author Courtenay Laing-Kobe
 */
public class UserUnitTests {

    @Test
    /**
     * Creates a sample User for testing
     * @return sample User
     */
    public User mockUser(){
        User user = new User("universalwonder", "notAPassword", 26);
        return user;
    }
    @Test
    /**
     * Tests User constructor
     */
    public void testUserInitialization(){
        User courtenay = mockUser();
        assertEquals(courtenay.getUsername(), "universalwonder");
        assertEquals(courtenay.getPassword(), "notAPassword");
        assertNull(courtenay.getID());
        assertNotNull(courtenay.getFollowMe());
        assertNotNull(courtenay.getIFollow());
        assertNotNull(courtenay.getHabits());
        assertNotNull(courtenay.getProfilePhoto());
    }

    @Test
    /**
     * Test changing User attributes
     */
    public void testUserSetters(){


    }



}
