package com.team11.ditto.interfaces;

import android.net.Uri;

import com.google.android.gms.tasks.Task;

public interface FirebaseMediaUploadCallback {
    // Define callbacks
    void imageURIChanged(Uri uri);
}
