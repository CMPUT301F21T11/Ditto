package com.team11.ditto.habit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.team11.ditto.R;
import com.team11.ditto.habit.EditHabitFragment;
import com.team11.ditto.habit.Habit;
import com.team11.ditto.interfaces.Firebase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Role: An Activity to view the contents of a chosen Habit.
 * Allow user to edit Habit and return updated data back to this activity
 * TODO: Get updated photos and locations updating in the database
 */
public class ViewHabitActivity extends AppCompatActivity implements EditHabitFragment.OnFragmentInteractionListener, Firebase {

    TextView habitTitle; TextView habitReason; TextView habitDays;
    ArrayList<Integer> dates; String title; String reason; String listDays;
    Habit selectedHabit;
    Bundle habitBundle;
    final String TAG = "view";
    private FirebaseFirestore database;
    HashMap<String, Object> data = new HashMap<>();


    /**
     * Create the dialog with the fields for reason, dates and go to OnOkPressed method when user clicks "Add"
     * TODO: get fields for photos and location
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_habit);
        habitReason = findViewById(R.id.habit_reason);
        habitDays = findViewById(R.id.habit_days);

        //Getting passed habit
        selectedHabit = (Habit) getIntent().getSerializableExtra("EXTRA_HABIT");

        //Setting title as habit title
        setTitle(selectedHabit.getTitle());

        //Setting habit_reason textview as habit reason
        habitReason.setText(selectedHabit.getReason());

        dates = selectedHabit.getDate();

        //Displaying dates in TextView
        if(dates != null){
            if(dates.size() > 0){
                listDays = "";
                for(int i = 0; i < dates.size(); i++){
                    listDays +=  intToDate(dates.get(i)) + " ";
                }
            }
        }

        habitDays.setText(listDays);

        habitTitle = findViewById(R.id.habit_tracking);
    }

    /**
     * Inflate the menu for the options menu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.view_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Listener for the edit button
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id == R.id.edit_habit){
            EditHabitFragment dialogFragment = new EditHabitFragment();

            //Creating bundle with selectedHabit
            habitBundle = new Bundle();
            habitBundle.putSerializable("HABIT", selectedHabit);

            //Passing bundle to EditHabitFragment
            dialogFragment.setArguments(habitBundle);

            //Opening EditHabitFragment with the selectedHabit bundled
            dialogFragment.show(getSupportFragmentManager(), "EDIT_HABIT");
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Updating a habit to the database and listview as the response to the user clicking the "Add" button from the fragment
     * @param habit
     */
    @Override
    public void onOkPressed(Habit habit) {

        //UPDATE THE OLD HABIT WITH THE NEW DATA

        //when the user clicks the add button, we want to add to the db and display the new entry
        final String title = habit.getTitle();
        final String reason = habit.getReason();
        final ArrayList<Integer> dates = habit.getDate();
        final String habitID = habit.getHabitID();
        Log.d(TAG, "dates -> "+ dates);

        database = FirebaseFirestore.getInstance();
        pushEditData(database, habit);

        //Updating old text with new habit stuff
        habitReason.setText(habit.getReason());

        if(dates != null){
            if(dates.size() > 0){
                listDays = "";
                for(int i = 0; i < dates.size(); i++){
                    listDays +=  intToDate(dates.get(i)) + " ";
                }
            }
        }

        habitDays.setText(listDays);
    }


    //Takes an integer and returns the respective day of the week,
    //returns null when given incorrect int
    private String intToDate(int date){
        if(date == 1){
            return "Monday";
        } else if (date == 2){
            return "Tuesday";
        } else if (date == 3){
            return "Wednesday";
        } else if (date == 4){
            return "Thursday";
        } else if (date == 5){
            return "Friday";
        } else if (date == 6){
            return "Saturday";
        } else if (date == 7){
            return "Sunday";
        }
        return null;
    }
}