package com.team11.ditto.habit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Decrement extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //get habits due today
        //if habitDoneToday is true -> increment streak
        //if habitDone is false -> decrement streak

    }
}
