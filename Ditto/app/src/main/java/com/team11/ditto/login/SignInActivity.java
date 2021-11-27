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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.team11.ditto.MainActivity;
import com.team11.ditto.R;
import com.team11.ditto.WelcomeActivity;

/**
 * Activity to login a User so they can access the application
 * @author Reham Albakouni
 */
public class SignInActivity extends AppCompatActivity {
    private EditText emailField;
    private EditText passwordField;
    private Button loginButton;
    final String TAG = "Sample";

    Context context = this;

    private FirebaseAuth mAuth;

    /**
     * Instructions for creating Activity
     * -Image background
     * -input fields to sign in the user
     * -button to transfer to sign up Activity
     * -button to submit login details
     *
     * @param savedInstanceState current app state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();

        emailField = findViewById(R.id.login_email_field);
        passwordField = findViewById(R.id.login_password_field);
        loginButton = findViewById(R.id.login_button);

        loginButton.setOnClickListener(view -> {
            String email = emailField.getText().toString();
            String password = passwordField.getText().toString();

            Log.d(TAG, "SIGNING IN");

            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignInActivity.this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "loginUserWithEmail:success");
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |  Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        Log.d(TAG, "loginUserWithEmail:failure");
                        Toast.makeText(getApplicationContext(),"Wrong username/password.",Toast.LENGTH_LONG).show();
                    }
                });

        });
    }

    /**
     * Define what to do if back pressed;
     * -goes back to Welcome screen
     */
    public void onBackPressed() {
        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |  Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
