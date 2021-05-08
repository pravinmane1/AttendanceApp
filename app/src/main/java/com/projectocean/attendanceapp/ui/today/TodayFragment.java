package com.projectocean.attendanceapp.ui.today;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.projectocean.attendanceapp.dataClass.Attendance;
import com.projectocean.attendanceapp.viewHolder.AttendanceViewHolder;
import com.projectocean.attendanceapp.LoginActivity;
import com.projectocean.attendanceapp.R;
import com.projectocean.attendanceapp.SelectPresentStudentActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TodayFragment extends Fragment {

    FirebaseAuth mAuth;
    FirebaseDatabase db;
    String uid;
    RecyclerView itemList;
    private RecyclerView studentList;
    private String dateStr;
    TextView date;
    RelativeLayout emptyl;
    ImageView logout;
    private GoogleSignInClient mGoogleSignInClient;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_today, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        uid = mAuth.getUid();

        final GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        itemList = root.findViewById(R.id.recycler_view);
        itemList.setHasFixedSize(false);
        LinearLayoutManager linearLayout = new LinearLayoutManager(getContext());
        itemList.setLayoutManager(linearLayout);

        date = root.findViewById(R.id.date);
        emptyl = root.findViewById(R.id.emptyl);
        logout = root.findViewById(R.id.logout);


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                mGoogleSignInClient.signOut();

                // mGoogleSignInClient.signOut();
                Toast.makeText(getActivity(), "Successfully Signed Out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
            }
        });




        Calendar calendar = Calendar.getInstance();


        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

        Date currentTime = calendar.getTime();

        String dateStr = dateFormat.format(currentTime);

        long longDate = Long.parseLong(dateStr);

        Long dayNumber = longDate%100;
        longDate = longDate/100;

        Long month = longDate%100;
        longDate = longDate/100;

        Long year = longDate;

        String dayOfWeek="";




        int day = calendar.get(Calendar.DAY_OF_WEEK);

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

        date.setText(format);


        return root;
    }

    public void firebaseSearch(final String day){


        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

        Date currentTime = Calendar.getInstance().getTime();

        dateStr = dateFormat.format(currentTime);


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

                if (model.getPresentPercent()==null){
                    viewHolder.enterAttendance.setText("Enter Attendance");
                }
                else{
                    viewHolder.enterAttendance.setText("Edit Attendance");

                }


                viewHolder.enterAttendance.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent i = new Intent(getActivity(), SelectPresentStudentActivity.class);

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