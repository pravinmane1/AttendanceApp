package com.projectocean.attendanceapp;

import com.google.firebase.database.FirebaseDatabase;

public class AttendanceApp extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        /* Enable disk persistence  */
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

}