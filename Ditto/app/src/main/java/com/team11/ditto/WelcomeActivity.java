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
package com.team11.ditto;
/*
The Class for MyHabit Activity Screen
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.team11.ditto.login.SignInActivity;
import com.team11.ditto.login.SignUpActivity;

/**
 * Role: Class for Welcome Activity, onboard a user
 * @author: Matthew Asgari
 */
public class WelcomeActivity extends Activity {

    private Button getStartedButton;
    private Button loginButton;
    private final Activity activity = this;

    /**
     * Create the Activity instance for the "Welcome" screen, control flow of actions
     * @param savedInstanceState state bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Load buttons
        getStartedButton = findViewById(R.id.sign_up_button);
        loginButton = findViewById(R.id.sign_in_button);

        // Set on click listener for "Get Started!" button
        getStartedButton.setOnClickListener(view -> {
            Intent intent = new Intent(activity, SignUpActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |  Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // Set on click listener for "Sign In" button
        loginButton.setOnClickListener(view -> {
            Intent intent = new Intent(activity, SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |  Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}