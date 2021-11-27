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
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.team11.ditto.MainActivity;
import com.team11.ditto.R;
import com.team11.ditto.WelcomeActivity;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {
    private EditText nameField;
    private EditText emailField;
    private EditText passwordField;
    private EditText confirmPasswordField;
    private TextView registerButton;
    final String TAG = "Sample";

    Context context = this;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    /**
     * Instructions for creating the Activity
     * -Image background
     * -fields for new User input data
     * -button to return to login screen
     * -button to submit User data
     * @param savedInstanceState current state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Set layouts
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        nameField = findViewById(R.id.signup_name_field);
        emailField = findViewById(R.id.signup_email_field);
        passwordField = findViewById(R.id.signup_password_field);
        registerButton = findViewById(R.id.register_button);
        confirmPasswordField = findViewById(R.id.signup_password_confirm_field);
        db = FirebaseFirestore.getInstance();

        registerButton.setOnClickListener(view -> {
            String name = nameField.getText().toString().trim();
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();
            String passwordConfirm = confirmPasswordField.getText().toString().trim();

            // Validate input
            if (TextUtils.isEmpty(name)) {
                nameField.setError("Name is required.");
            } else if (TextUtils.isEmpty(email)) {
                emailField.setError("Email is required.");
            } else if (TextUtils.isEmpty(password)) {
                passwordField.setError("Password is required.");
            } else if (TextUtils.isEmpty(passwordConfirm)) {
                confirmPasswordField.setError("Confirm Password is required.");
            } else if (password.length() < 8) {
                passwordField.setError("Password must be at least 8 characters.");
            } else if (!password.equals(passwordConfirm)) {
                confirmPasswordField.setError("Password does not match.");
            } else {
                HashMap<String, String> data = new HashMap<>();
                //input is valid -> create the user
                data.put("name", name);
                data.put("email", email);

                registerUser(email, password, data);
            }
        });
    }


    // Attempt to register a user on Firebase
    private void registerUser(String email, String password, HashMap<String, String> userData) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignUpActivity.this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();

                        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                .setDisplayName(userData.get("name"))
                                .build();
                        user.updateProfile(profileChangeRequest);
                        userData.put("uid", user.getUid());
                        storeUserData(userData);
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(SignUpActivity.this, task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Push new User data to database
     * @param userData the data to store under the uid
     */
    // Store user data on Firestore
    private void storeUserData(HashMap<String, String> userData) {
        CollectionReference collectionReference = db.collection("User");
        DocumentReference documentReference = collectionReference.document(userData.get("uid"));

        documentReference
                .set(userData)
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "User data added successfully!");

                    Intent intent = new Intent(context, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |  Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    // These are a method which gets executed if there’s any problem
                    Log.w(TAG, "Data could not be added!" + e.toString());
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