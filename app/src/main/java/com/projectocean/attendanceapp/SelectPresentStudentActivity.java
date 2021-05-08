package com.projectocean.attendanceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.projectocean.attendanceapp.dataClass.Student;
import com.projectocean.attendanceapp.viewHolder.StudentViewHolder;

public class SelectPresentStudentActivity extends AppCompatActivity {

    RecyclerView studentList;
    FirebaseAuth mAuth;
    FirebaseDatabase db;
    String uid;
    String dateStr,lectureId;
    Integer strength;
    Button markAsPresent;
    private DatabaseReference studentsRef;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_present_student);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        uid = mAuth.getUid();

        markAsPresent = findViewById(R.id.mark_as_present);
        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        dateStr = getIntent().getStringExtra("date");
        lectureId = getIntent().getStringExtra("lectureId");
        strength = getIntent().getIntExtra("strength",0);

        studentList = findViewById(R.id.recycler_view);

        studentList.setHasFixedSize(false);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),5);
        studentList.setLayoutManager(gridLayoutManager);

        studentsRef = db.getReference("attendance").child(uid).child(dateStr).child(lectureId).child("presentStudents");

        firebaseSearchForStudents(strength,studentsRef);


        markAsPresent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                studentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        int totalStudents = (int)dataSnapshot.getChildrenCount();
                        int totalPresent = 0;

                        for (DataSnapshot ds:dataSnapshot.getChildren()){

                            if (ds.child("present").getValue(Boolean.class)){
                                totalPresent = totalPresent+1;
                            }
                        }


                        float percent = (totalPresent/ (float)totalStudents);
                        percent = percent*100;




                        Log.d("presentdebug","totalpresent "+totalPresent+" totalstu "+totalStudents);



                        db.getReference("attendance").child(uid).child(dateStr).child(lectureId).child("presentPercent")
                                .setValue(percent).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                finish();
                            }
                        });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

    }

    public void firebaseSearchForStudents(final Integer strength, final DatabaseReference q) {

        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    for (int i =1;i<=strength;i++){
                        Student student = new Student();
                        student.setPresent(false);
                        student.setRollNo(i);
                        q.child(String.valueOf(i)).setValue(student);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Log.d("recycle debug", "firebasesearch");

        FirebaseRecyclerAdapter<Student, StudentViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Student, StudentViewHolder>(
                Student.class, R.layout.student_layout, StudentViewHolder.class, q
        ) {

            @Override
            protected void populateViewHolder(final StudentViewHolder viewHolder, final Student model, final int position) {

                viewHolder.setDetails(model.getRollNo(),model.getPresent(),getApplicationContext());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (!model.getPresent()){

                            model.setPresent(true);
                            q.child(String.valueOf(model.getRollNo())).child("present").setValue(true);
                            viewHolder.mView.setBackground(getResources().getDrawable(R.drawable.circle_checked));
                        }
                        else if (model.getPresent()){
                            model.setPresent(false);
                            q.child(String.valueOf(model.getRollNo())).child("present").setValue(false);
                            viewHolder.mView.setBackground(getResources().getDrawable(R.drawable.circle));
                        }


                    }
                });
            }
        };


        studentList.setAdapter(firebaseRecyclerAdapter);
    }
}