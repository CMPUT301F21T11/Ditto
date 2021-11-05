package com.team11.ditto.follow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.team11.ditto.R;
import com.team11.ditto.habit.Habit;

import java.util.ArrayList;

/**
 * Custom list adapter to display the habits of a User that is followed by the ActiveUser
 * @author Vivek Malhotra, Courtenay Laing-Kobe
 */
public class FriendHabitList extends ArrayAdapter<Habit> {

    //Declarations
    private ArrayList<Habit> habits;
    private final Context context;

    /**
     * Constructor for the list
     * @param context activity context
     * @param habits habits to display
     */
    public FriendHabitList(Context context, ArrayList<Habit> habits) {
        super(context,0,habits);
        this.habits = habits;
        this.context = context;
    }

    /**
     * Create the views for each list item
     * @param position Android Default
     * @param convertView Android Default
     * @param parent Android Default
     * @return View for a list item
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @Nullable ViewGroup parent) {
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.friend_profile_content, parent,false);
        }

        Habit habit = habits.get(position);

        TextView habitName = view.findViewById(R.id.friend_habit_name);
        TextView habitDescription = view.findViewById(R.id.friend_habit_description);
        ImageView progress = view.findViewById(R.id.friend_progress);

        habitName.setText(habit.getTitle());
        habitDescription.setText("This is a sample description of a habit");

        return view;
    }
}