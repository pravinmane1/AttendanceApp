package com.projectocean.attendanceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.projectocean.attendanceapp.dataClass.Attendance;
import com.projectocean.attendanceapp.viewHolder.AttendanceViewHolder;

import java.util.Calendar;

public class AttendanceDayActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseDatabase db;
    String uid;
    RecyclerView itemList;
    private String dateStr;
    int day;
    RelativeLayout emptyl;
    TextView dateView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_day);



        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        uid = mAuth.getUid();

        dateView = findViewById(R.id.date);

        emptyl = findViewById(R.id.emptyl);


        itemList = findViewById(R.id.recycler_view);
        itemList.setHasFixedSize(false);
        LinearLayoutManager linearLayout = new LinearLayoutManager(getApplicationContext());
        itemList.setLayoutManager(linearLayout);

        dateStr = getIntent().getStringExtra("date");
       day = getIntent().getIntExtra("day",2);

        long longDate = Long.parseLong(dateStr);

        Long dayNumber = longDate%100;
        longDate = longDate/100;

        Long month = longDate%100;
        longDate = longDate/100;

        Long year = longDate;

        String dayOfWeek="";

        switch (day) {
            case Calendar.SUNDAY:
                dayOfWeek = "Sunday";
                // firebaseSearch("wednesday");
                break;
            case Calendar.MONDAY:
                firebaseSearch("monday");
                dayOfWeek = "Monday";
                break;
            case Calendar.TUESDAY:
                firebaseSearch("tuesday");
                dayOfWeek = "Tuesday";
                break;
            case Calendar.WEDNESDAY:
                firebaseSearch("wednesday");
                dayOfWeek = "Wednesday";
                break;
            case Calendar.THURSDAY:
                firebaseSearch("thursday");
                dayOfWeek = "Thursday";
                break;
            case Calendar.FRIDAY:
                firebaseSearch("friday");
                dayOfWeek = "Friday";
                break;
            case Calendar.SATURDAY:
                firebaseSearch("saturday");
                dayOfWeek = "Saturday";
                break;
        }

        String format = dayOfWeek+", "+dayNumber+"-"+month+"-"+year;

        dateView.setText(format);
    }

    public void firebaseSearch(final String day){

        Query q = db.getReference("attendance").child(uid).child(dateStr);



        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot attendanceSnapshot) {


                db.getReference("timetable").child(uid).child(day).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot timetableSnapshot) {

                        if (timetableSnapshot.exists()){
                            itemList.setVisibility(View.VISIBLE);
                            emptyl.setVisibility(View.GONE);


                            for (DataSnapshot ds:timetableSnapshot.getChildren()){


                                Attendance attendance = ds.getValue(Attendance.class);
                                attendance.setDate(dateStr);

                                if (!attendanceSnapshot.child(attendance.getId()).exists()){
                                    db.getReference("attendance").child(uid).child(dateStr).child(attendance.getId()).setValue(attendance);
                                }
                            }
                        }
                        else{

                            emptyl.setVisibility(View.VISIBLE);

                            itemList.setVisibility(View.GONE);
                        }



                        for (DataSnapshot ds:attendanceSnapshot.getChildren()){


                            Attendance attendance = ds.getValue(Attendance.class);

                            if (!timetableSnapshot.child(attendance.getId()).exists()){
                                db.getReference("attendance").child(uid).child(dateStr).child(attendance.getId()).removeValue();
                            }



                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Log.d("recycle debug","firebasesearch");

        FirebaseRecyclerAdapter<Attendance, AttendanceViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Attendance, AttendanceViewHolder>(
                Attendance.class, R.layout.today_row, AttendanceViewHolder.class, q
        ) {

            @Override
            protected void populateViewHolder(final AttendanceViewHolder viewHolder, final Attendance model, final int position) {

                viewHolder.setDetails(model.getClassId(),model.getDivisionId(),model.getSubject(),model.getPresentPercent());

                viewHolder.enterAttendance.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent i = new Intent(getApplicationContext(), SelectPresentStudentActivity.class);

                        i.putExtra("date",dateStr);
                        i.putExtra("lectureId",model.getId());
                        i.putExtra("strength",model.getStrength());

                        startActivity(i);
                    }
                });
            }
        };


        itemList.setAdapter(firebaseRecyclerAdapter);
    }
}