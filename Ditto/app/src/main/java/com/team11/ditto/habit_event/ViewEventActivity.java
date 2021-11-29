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

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.team11.ditto.R;
import com.team11.ditto.interfaces.FirebaseMedia;
import com.team11.ditto.interfaces.HabitFirebase;

/**
 * Activity to view a Habit Event, delete and edit the habit event
 * @author Kelly Shih, Aidan Horemans, Matthew Asgari
 */
public class ViewEventActivity extends AppCompatActivity implements EditEventFragment.OnFragmentInteractionListener, HabitFirebase, OnMapReadyCallback, FirebaseMedia {

    //Declarations
    HabitEvent habitEvent;
    TextView habitTitle;
    TextView habitComment;
    ImageView eventImage;
    String title;
    String comment;
    Bundle eventBundle;
    GoogleMap map;
    private FirebaseFirestore database;


    /**
     * Instructions for creating the Activity
     * -display title & comment
     * @param savedInstanceState current app state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);
        habitTitle = findViewById(R.id.habit_title);
        habitComment = findViewById(R.id.habit_comment);
        eventImage = findViewById(R.id.event_image_view);

        //get the passed habit event
        habitEvent = (HabitEvent) getIntent().getSerializableExtra("EXTRA_HABIT_EVENT");

        database = FirebaseFirestore.getInstance();

        //set title
        title = habitEvent.getHabitTitle();
        habitTitle.setText(title);

        //set comment
        comment = habitEvent.getComment();
        habitComment.setText(comment);

        //set image
        if (!habitEvent.getPhoto().equals("")) {
            setImage(habitEvent.getPhoto(), eventImage);
        }

        //setup map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_event);
        mapFragment.getMapAsync(this);

        if (habitEvent.getLocation() == null || habitEvent.getLocation().size() != 2) {
            findViewById(R.id.map_event).setVisibility(View.GONE);
        }
    }

    /**
     * Inflate the menu for the options menu
     * @param menu options menu
     * @return true when menu displayed, false otherwise
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.view_event, menu);
        getSupportActionBar().setTitle("My Event");
        return super.onCreateOptionsMenu(menu);
    }


    /**
     * Listener for the edit button
     * @param item selected item
     * @return true if displayed, false otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id == R.id.edit_event){
            EditEventFragment dialogFragment = new EditEventFragment();

            //Creating bundle with selectedHabit
            eventBundle = new Bundle();
            eventBundle.putSerializable("EVENT", habitEvent);

            //Passing bundle to EditHabitFragment
            dialogFragment.setArguments(eventBundle);

            //Opening EditHabitFragment with the selectedHabit bundled
            dialogFragment.show(getSupportFragmentManager(), "EDIT_EVENT");
        }

        //When we delete the event, finish the activity and remove it from the database
        if (id == R.id.delete_event){

            deleteDataMyEvent(database, habitEvent);
            finish();

        }

        return super.onOptionsItemSelected(item);

    }


    /**
     * handle calling functions to update event data in firebase, and the textview
     * @param event
     */
    @Override
    public void onOkPressed(HabitEvent event) {
        //Update old habit event data with new habit event data
        pushEditEvent(database, event);

        //Updating old text with new habit stuff
        habitComment.setText(event.getComment());

        //update image
        if (!event.getPhoto().equals("")) {
            setImage(event.getPhoto(), eventImage);
        } else {
            eventImage.setImageBitmap(null);
        }

        //update map
        if (event.getLocation() == null || event.getLocation().size() != 2) {
            findViewById(R.id.map_event).setVisibility(View.GONE);
        } else {
            findViewById(R.id.map_event).setVisibility(View.VISIBLE);
            LatLng location = new LatLng(event.getLocation().get(0), event.getLocation().get(1));
            map.clear();
            map.addMarker(new MarkerOptions()
                    .position(location)
                    .title(habitEvent.getHabitTitle()));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
        }
    }

    /**
     * Called when the map is ready to be used
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (habitEvent.getLocation() != null && habitEvent.getLocation().size() == 2) {
            LatLng location = new LatLng(habitEvent.getLocation().get(0), habitEvent.getLocation().get(1));
            googleMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title(habitEvent.getHabitTitle()));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
        }
    }
}