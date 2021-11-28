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
package com.team11.ditto.interfaces;

import android.content.Context;
import android.content.Intent;

import com.google.android.material.tabs.TabLayout;
import com.team11.ditto.DueTodayActivity;
import com.team11.ditto.MainActivity;
import com.team11.ditto.MyHabitActivity;
import com.team11.ditto.UserProfileActivity;

/**
 * Interface to allow swapping between activities through bottom tabs
 * @author Courtenay Laing-Kobe, Vivek Malhotra
 */
public interface SwitchTabs {

    //Macros
    int HOME_TAB = 0;
    int MY_HABITS_TAB = 1;
    int DUE_TODAY_TAB = 2;
    int PROFILE_TAB = 3;

    /**
     *Change activities based on tab selected
     * @param context activity context
     * @param tabLayout tabs
     * @param currentTabPos current tab position
     */
    default void switchTabs(Context context, TabLayout tabLayout, int currentTabPos){

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tabSelect) {
                // position 0 is for home
                if ((tabSelect.getPosition() == HOME_TAB) && (currentTabPos != HOME_TAB)){
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |  Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    context.startActivity(intent);
                }

                // position 1 is for MyHabits
                else if ((tabSelect.getPosition() == MY_HABITS_TAB) && (currentTabPos != MY_HABITS_TAB)){
                    Intent intent = new Intent(context, MyHabitActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |  Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    context.startActivity(intent);
                }

                // position 2 is for Due Today
                else if ((tabSelect.getPosition() == DUE_TODAY_TAB) && (currentTabPos != DUE_TODAY_TAB)){
                    Intent intent = new Intent(context, DueTodayActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |  Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    context.startActivity(intent);
                }

                // position 3 is for Profile
                else if ((tabSelect.getPosition() == PROFILE_TAB) && (currentTabPos != PROFILE_TAB)){
                    Intent intent = new Intent(context, UserProfileActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |  Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    context.startActivity(intent);
                }

            }

            /**
             * Do nothing if tab unselected
             * @param tab tab unselected
             */
            @Override
            public void onTabUnselected(TabLayout.Tab tab){
            }

            /**
             * Do nothing is tab reselected
             * @param tab tab reselected
             */
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

    }

    /**
     * Selects the current tab
     * @param tabLayout tabs
     * @param currentTabPos position of current tab
     */
    default void currentTab(TabLayout tabLayout, int currentTabPos){
        TabLayout.Tab tab = tabLayout.getTabAt(currentTabPos);
        if (tab != null) {
            tab.select();
        }

    }
}