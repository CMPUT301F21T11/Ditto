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

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.team11.ditto.R;
import com.team11.ditto.interfaces.FirebaseMedia;

import java.util.ArrayList;

/**
 * Custom RecyclerViewAdapter for HabitEvents item
 * @author Kelly Shih, Aidan Horemans
 */
public class HabitEventRecyclerAdapter extends RecyclerView.Adapter<HabitEventRecyclerAdapter.ViewHolderEvent> implements FirebaseMedia {
    //Declarations
    private ArrayList<HabitEvent> eventArrayList;
    private Context context;
    private EventClickListener eventClickListener;
    private FirebaseFirestore db;

    /**
     * Constructor
     * @param context activity context
     * @param eventArrayList list of HabitEvents
     * @param eventClickListener listener for interaction
     */
    public HabitEventRecyclerAdapter(Context context, ArrayList<HabitEvent> eventArrayList, EventClickListener eventClickListener){
        this.eventArrayList = eventArrayList;
        this.eventClickListener = eventClickListener;
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Initialize the viewholder for the position within the recyclerview
     * @param parent Android default
     * @param viewType Android default
     * @return view holder
     */
    @NonNull
    @Override
    public ViewHolderEvent onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_my_habit_event, parent, false);
        return new ViewHolderEvent(view, eventClickListener);
    }

    /**
     * Get list size
     * @return number of items
     */
    @Override
    public int getItemCount(){
        return eventArrayList.size();
    }

    /**
     * Connect holders to habit attributes for display
     * @param holder view holder
     * @param position list position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolderEvent holder, int position){
        HabitEvent habitEvent = eventArrayList.get(position);

        //Add separator if comment is not empty

        holder.habitEventTitle.setText(habitEvent.getHabitTitle());
        holder.habitUsername.setText((habitEvent.getName()));

        if(habitEvent.getComment().equals("")){
            holder.habitSeparator.setText("");
        } else {
            holder.habitSeparator.setText(" - ");
        }

        holder.habitEventComment.setText(habitEvent.getComment());
        setProfilePhoto(habitEvent.getUid(), holder.profilePhoto);
    }

    /**
     * Changes inner list
     */
    public void setList(ArrayList<HabitEvent> events){
        eventArrayList.clear();
        eventArrayList = new ArrayList<>(events);
    }

    /**
     * Viewholder class to handle RecyclerView
     */
    public static class ViewHolderEvent extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView habitEventTitle;
        private TextView habitEventComment;
        private TextView habitUsername;
        private TextView habitSeparator;
        private ImageView profilePhoto;
        EventClickListener eventClickListener;

        /**
         * Pair the holder with the item view
         * @param itemView view for item
         * @param eventClickListener listener for interaction with item view
         */
        public ViewHolderEvent(@NonNull View itemView, EventClickListener eventClickListener){
            super(itemView);
            habitUsername = itemView.findViewById(R.id.firstLine);
            habitEventTitle = itemView.findViewById(R.id.habit_name);
            habitEventComment = itemView.findViewById(R.id.habit_com);
            habitSeparator = itemView.findViewById(R.id.separator);
            profilePhoto = itemView.findViewById(R.id.event_cell_profile_photo);
            this.eventClickListener = eventClickListener;

            itemView.setOnClickListener(this);
        }

        /**
         * capture title, and comment of clicked view
         * @param view view clicked
         */
        @Override
        public void onClick(View view){
            eventClickListener.onEventClick(getBindingAdapterPosition());
            habitUsername = itemView.findViewById(R.id.firstLine);
            habitEventTitle = itemView.findViewById(R.id.habit_name);
            habitEventComment = itemView.findViewById(R.id.habit_com);

        }
    }

    /**
     * Listener interface
     */
    public interface EventClickListener{
        void onEventClick(int position);
    }

}
