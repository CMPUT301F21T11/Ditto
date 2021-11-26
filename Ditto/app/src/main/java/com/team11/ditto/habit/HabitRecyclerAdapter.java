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
package com.team11.ditto.habit;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.team11.ditto.R;

import java.util.ArrayList;

/**
 *  Custom RecyclerView item for the Habit Activity
 *  TODO: match the UI more accurately
 * @author Kelly Shih
 */
public class HabitRecyclerAdapter extends RecyclerView.Adapter<HabitRecyclerAdapter.RecyclerViewHolder> {
    //Declarations
    private final ArrayList<Habit> courseDataArrayList;
    private final Context context;
    private final HabitClickListener habitClickListener;
    FirebaseFirestore database;


    /**
     * Constructor
     * @param recyclerDataArrayList list of Habits
     * @param context activity context
     */
    public HabitRecyclerAdapter(ArrayList<Habit> recyclerDataArrayList, Context context, HabitClickListener habitClickListener) {
        this.courseDataArrayList = recyclerDataArrayList;
        this.context = context;
        this.habitClickListener = habitClickListener;
    }

    /**
     * To inflate the layout for the view
     * @param parent Android default
     * @param viewType Android default
     * @return holder for list item
     */
    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate Layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_my_habit, parent, false);
        return new RecyclerViewHolder(view, habitClickListener);
    }

    /**
     * To set the data to textview from the Habit class
     * @param holder Android default
     * @param position Android default
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        Habit habit = courseDataArrayList.get(position);
        holder.habitTitle.setText(habit.getTitle());
        holder.habitReason.setText(habit.getReason());

        //set the streak icon
        //get the streak value from database
        database = FirebaseFirestore.getInstance();
        Log.d("HELLO", habit.getHabitID());
        DocumentReference docRef = database.collection("Habit").document(habit.getHabitID());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        //retrieve the habitDoneToday value
                        int streaks = Integer.valueOf(documentSnapshot.getString("streaks"));

                        //if streaks is less than -3 -> sad face
                        //if streaks between -3 and 5 -> neutral
                        //if streaks greater than 5 -> happy face
                        setIcon(streaks, holder);





                    } else {
                        Log.d("YK", "document does not exist!!");
                    }

                } else {
                    Log.d("YK", task.getException().toString());
                }


            }
        });

    }

    private void setIcon(int streaks, RecyclerViewHolder holder) {
        int lB = -3;
        int uB = 5;
        if (streaks < lB) {
            holder.icon.setImageResource(R.drawable.sad);
            holder.icon.setColorFilter(Color.RED);



        }
        else if (streaks >= lB && streaks <= uB) {
            holder.icon.setImageResource(R.drawable.neutral);
            holder.icon.setColorFilter(Color.rgb(255,191,0));

        }
        else if (streaks > uB) {
            holder.icon.setImageResource(R.drawable.happiness);
            holder.icon.setColorFilter(Color.rgb(50,205,50));



        }


    }


    /**
     * Returns the size of the recyclerview
     * @return number of items (int)
     */
    @Override
    public int getItemCount() {
        return courseDataArrayList.size();
    }

    public Habit getItemAt(int position){
        return courseDataArrayList.get(position);
    }

    /**
     * Viewholder class to handle RecyclerView
     */
    public static class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // creating a variable for our text view.
        private TextView habitTitle;
        private TextView habitReason;
        private ImageView icon;
        HabitClickListener habitClickListener;

        /**
         * Pair the holder with the item view
         * @param itemView the view for the item
         * @param habitClickListener a listener for interaction with the item view
         */
        public RecyclerViewHolder(@NonNull View itemView, HabitClickListener habitClickListener) {
            super(itemView);
            // initializing our text views.
            habitTitle = itemView.findViewById(R.id.firstLine);
            habitReason = itemView.findViewById(R.id.secondLine);
            icon = itemView.findViewById(R.id.tracking_icon);
            this.habitClickListener = habitClickListener;

            itemView.setOnClickListener(this);
        }

        /**
         * Define what to do when clicked
         * -capture title/reason of clicked view
         * @param view view clicked
         */
        @Override
        public void onClick(View view) {
            habitClickListener.onHabitClick(getBindingAdapterPosition());
            habitTitle = itemView.findViewById(R.id.firstLine);
            habitReason = itemView.findViewById(R.id.secondLine);
        }


    }

    /**
     * Listener interface
     */
    public interface HabitClickListener {
        void onHabitClick(int position);
    }

}
