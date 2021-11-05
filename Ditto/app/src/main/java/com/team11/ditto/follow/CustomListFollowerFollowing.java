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
import com.team11.ditto.profile_details.User;

import java.util.ArrayList;

/**
 * Custom ArrayAdapter for User Class
 * @author Vivek Malhotra, Courtenay Laing-Kobe
 */
public class CustomListFollowerFollowing extends ArrayAdapter<User> {

    //Declare values
    private ArrayList<User> users;
    private Context context;

    /**
     * Constructor for CustomUserList
     * @param context application context
     * @param users ArrayList of User objects to display
     */
    public CustomListFollowerFollowing(Context context, ArrayList<User> users) {
        super(context,0,users);
        this.users = users;
        this.context = context;
    }

    /**
     * Creates a view for an item in the list
     * @param position Android default
     * @param convertView Android default
     * @param parent Android default
     * @return Follower/Following style view
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @Nullable ViewGroup parent) {
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.follower_content, parent,false);
        }

        User user = users.get(position);

        TextView username = view.findViewById(R.id.follower_name);
        ImageView userPhoto = view.findViewById(R.id.follower_photo);

        username.setText(user.getUsername());
        userPhoto.setImageResource(R.drawable.background);

        return view;
    }
}