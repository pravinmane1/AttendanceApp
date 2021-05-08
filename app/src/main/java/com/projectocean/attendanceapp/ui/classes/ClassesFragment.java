package com.projectocean.attendanceapp.ui.classes;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.projectocean.attendanceapp.dataClass.Division;
import com.projectocean.attendanceapp.viewHolder.DivisionViewHolder;
import com.projectocean.attendanceapp.R;
import com.projectocean.attendanceapp.dataClass.StudentClass;
import com.projectocean.attendanceapp.viewHolder.StudentClassViewHolder;
import com.projectocean.attendanceapp.dataClass.Subject;
import com.projectocean.attendanceapp.viewHolder.SubjectViewHolder;

public class ClassesFragment extends Fragment {
    FirebaseAuth mAuth;
    FirebaseDatabase db;
    String uid;
    RecyclerView itemList;
    FloatingActionButton floatingActionButton;
    String classNameStr;
    String SubjectNameStr;
    private RecyclerView subjectList, divisionlist;
    RelativeLayout emptyl;
    ValueEventListener valueEventListener;
    DatabaseReference databaseReference;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_classes, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        uid = mAuth.getUid();
        floatingActionButton = root.findViewById(R.id.fab);

        emptyl = root.findViewById(R.id.emptyl);


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                        getActivity(), R.style.BottomSheetDialogTheme
                );

                final View bottomSheetView = LayoutInflater.from(getContext())
                        .inflate(
                                R.layout.add_class_dialog,
                                (ScrollView) root.findViewById(R.id.bottom_sheet_container)
                        );

                final TextView TVclassName = bottomSheetView.findViewById(R.id.class_name);
                final TextView TVaddSubject = bottomSheetView.findViewById(R.id.add_subject);
                final TextView TVaddDivision = bottomSheetView.findViewById(R.id.add_division);
                subjectList = bottomSheetView.findViewById(R.id.subjectlist);
                divisionlist = bottomSheetView.findViewById(R.id.divisionlist);

                subjectList = bottomSheetView.findViewById(R.id.subjectlist);

                subjectList.setHasFixedSize(false);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                subjectList.setLayoutManager(linearLayoutManager);

                divisionlist.setHasFixedSize(false);
                LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getContext());

                divisionlist.setLayoutManager(linearLayoutManager2);

                TVaddSubject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                        LayoutInflater inflater = getActivity().getLayoutInflater();

                        View customDialog = inflater.inflate(R.layout.add_subject_dialog, null);
                        alert.setView(customDialog);
                        final EditText name = (EditText) customDialog.findViewById(R.id.subject_name);
                        alert.setCancelable(true);

                        //alert.setIcon(R.drawable.plus_green);

                        alert.setPositiveButton("DISMISS", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                            }
                        });

                        alert.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                String key = db.getReference("classData")
                                        .child(uid).child(classNameStr).child("subjects").push().getKey();

                                Subject subject = new Subject();
                                subject.setId(key);
                                subject.setName(name.getText().toString());

                                db.getReference("classData")
                                        .child(uid).child(classNameStr).child("subjects").child(key).setValue(subject);
                                dialog.dismiss();

                            }
                        });


                        AlertDialog dialog = alert.create();
                        dialog.show();


                    }
                });

                TVaddDivision.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                        LayoutInflater inflater = getActivity().getLayoutInflater();

                        View customDialog = inflater.inflate(R.layout.add_division_dialog, null);
                        alert.setView(customDialog);
                        final EditText name = (EditText) customDialog.findViewById(R.id.division_name);
                        final EditText strength = customDialog.findViewById(R.id.strength);
                        alert.setCancelable(true);

                        //alert.setIcon(R.drawable.plus_green);

                        alert.setPositiveButton("DISMISS", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                            }
                        });

                        alert.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                if (!name.getText().toString().equals("") && !strength.getText().toString().equals("")) {
                                    Division division = new Division();
                                    division.setId(name.getText().toString());
                                    division.setStrength(Integer.parseInt(strength.getText().toString()));

                                    db.getReference("classData")
                                            .child(uid)
                                            .child(classNameStr)
                                            .child("divisions").child(name.getText().toString()).setValue(division);
                                    dialog.dismiss();

                                }

                            }
                        });


                        AlertDialog dialog = alert.create();
                        dialog.show();

                    }
                });


                final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = getActivity().getLayoutInflater();

                View customDialog = inflater.inflate(R.layout.add_class_name_dialog, null);
                alert.setView(customDialog);
                final EditText className = (EditText) customDialog.findViewById(R.id.class_name);
                alert.setCancelable(false);

                //alert.setIcon(R.drawable.plus_green);

                alert.setPositiveButton("DISMISS", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });

                alert.setNegativeButton("CONTINUE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        classNameStr = className.getText().toString();

                        if (!classNameStr.equals("")) {

                            TVclassName.setText(classNameStr);

                            db.getReference("classData").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    dataSnapshot.getChildrenCount();

                                    StudentClass studentClass = new StudentClass();
                                    studentClass.setId(classNameStr);
                                    studentClass.setPriority((int) dataSnapshot.getChildrenCount() + 1);

                                    db.getReference("classData")
                                            .child(uid).child(studentClass.getId())
                                            .setValue(studentClass);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                            bottomSheetDialog.setContentView(bottomSheetView);
                            bottomSheetDialog.setCancelable(true);
                            bottomSheetDialog.show();


                            DatabaseReference subjectsRef = db.getReference("classData").child(uid).child(classNameStr).child("subjects");
                            DatabaseReference divisionsRef = db.getReference("classData").child(uid).child(classNameStr).child("divisions");


                            firebaseSearchSubjects(subjectsRef);
                            firebaseSearchDivisions(divisionsRef);
                        }

                    }
                });


                AlertDialog dialog = alert.create();
                dialog.show();

            }
        });

        itemList = root.findViewById(R.id.recycler_view);

        itemList.setHasFixedSize(false);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        itemList.setLayoutManager(gridLayoutManager);


        DatabaseReference classesRef = db.getReference("classData").child(uid);


        firebaseSearch(classesRef, root);

        return root;
    }

    public void firebaseSearch(Query q, final View root) {

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    itemList.setVisibility(View.VISIBLE);
                    emptyl.setVisibility(View.GONE);

                }
                else{

                    emptyl.setVisibility(View.VISIBLE);

                    itemList.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        databaseReference = db.getReference("classData").child(uid);
        databaseReference.addValueEventListener(valueEventListener);



        Log.d("recycle debug", "firebasesearch");

        FirebaseRecyclerAdapter<StudentClass, StudentClassViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<StudentClass, StudentClassViewHolder>(
                StudentClass.class, R.layout.class_row, StudentClassViewHolder.class, q
        ) {

            @Override
            protected void populateViewHolder(final StudentClassViewHolder viewHolder, final StudentClass model, final int position) {

                viewHolder.setDetails(model.getId());

                //remove button listener
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        classNameStr = model.getId();

                        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                                getActivity(), R.style.BottomSheetDialogTheme
                        );

                        final View bottomSheetView = LayoutInflater.from(getContext())
                                .inflate(
                                        R.layout.add_class_dialog,
                                        (ScrollView) root.findViewById(R.id.bottom_sheet_container)
                                );

                        final TextView TVclassName = bottomSheetView.findViewById(R.id.class_name);
                        final TextView TVaddSubject = bottomSheetView.findViewById(R.id.add_subject);
                        final TextView TVaddDivision = bottomSheetView.findViewById(R.id.add_division);
                        subjectList = bottomSheetView.findViewById(R.id.subjectlist);
                        divisionlist = bottomSheetView.findViewById(R.id.divisionlist);
                        Button delete = bottomSheetView.findViewById(R.id.delete);

                        delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                db.getReference("classData").child(uid).child(model.getId()).removeValue();
                                bottomSheetDialog.dismiss();
                            }
                        });

                        TVclassName.setText(model.getId());

                        subjectList = bottomSheetView.findViewById(R.id.subjectlist);

                        subjectList.setHasFixedSize(false);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                        subjectList.setLayoutManager(linearLayoutManager);

                        divisionlist.setHasFixedSize(false);
                        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getContext());

                        divisionlist.setLayoutManager(linearLayoutManager2);

                        TVaddSubject.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                                LayoutInflater inflater = getActivity().getLayoutInflater();

                                View customDialog = inflater.inflate(R.layout.add_subject_dialog, null);
                                alert.setView(customDialog);
                                final EditText name = (EditText) customDialog.findViewById(R.id.subject_name);
                                alert.setCancelable(true);

                                //alert.setIcon(R.drawable.plus_green);

                                alert.setPositiveButton("DISMISS", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();

                                    }
                                });

                                alert.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {


                                        String key = db.getReference("classData")
                                                .child(uid).child(classNameStr).child("subjects").push().getKey();

                                        Subject subject = new Subject();
                                        subject.setId(key);
                                        subject.setName(name.getText().toString());

                                        db.getReference("classData")
                                                .child(uid).child(classNameStr).child("subjects").child(key).setValue(subject);
                                        dialog.dismiss();

                                    }
                                });


                                AlertDialog dialog = alert.create();
                                dialog.show();


                            }
                        });

                        TVaddDivision.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                                LayoutInflater inflater = getActivity().getLayoutInflater();

                                View customDialog = inflater.inflate(R.layout.add_division_dialog, null);
                                alert.setView(customDialog);
                                final EditText name = (EditText) customDialog.findViewById(R.id.division_name);
                                final EditText strength = customDialog.findViewById(R.id.strength);
                                alert.setCancelable(true);

                                //alert.setIcon(R.drawable.plus_green);

                                alert.setPositiveButton("DISMISS", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();

                                    }
                                });

                                alert.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {


                                        if (!name.getText().toString().equals("") && !strength.getText().toString().equals("")) {
                                            Division division = new Division();
                                            division.setId(name.getText().toString());
                                            division.setStrength(Integer.parseInt(strength.getText().toString()));

                                            db.getReference("classData")
                                                    .child(uid)
                                                    .child(classNameStr)
                                                    .child("divisions").child(name.getText().toString()).setValue(division);
                                            dialog.dismiss();
                                        }

                                    }
                                });


                                AlertDialog dialog = alert.create();
                                dialog.show();

                            }
                        });

                        bottomSheetDialog.setContentView(bottomSheetView);
                        bottomSheetDialog.setCancelable(true);
                        bottomSheetDialog.show();


                        DatabaseReference subjectsRef = db.getReference("classData").child(uid).child(classNameStr).child("subjects");
                        DatabaseReference divisionsRef = db.getReference("classData").child(uid).child(classNameStr).child("divisions");


                        firebaseSearchSubjects(subjectsRef);
                        firebaseSearchDivisions(divisionsRef);
                    }
                });
            }
        };


        itemList.setAdapter(firebaseRecyclerAdapter);
    }

    public void firebaseSearchSubjects(Query q) {

        Log.d("recycle debug", "firebasesearch");

        FirebaseRecyclerAdapter<Subject, SubjectViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Subject, SubjectViewHolder>(
                Subject.class, R.layout.subject_row, SubjectViewHolder.class, q
        ) {

            @Override
            protected void populateViewHolder(final SubjectViewHolder viewHolder, final Subject model, final int position) {

                viewHolder.setDetails(model.getName());

                viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        db.getReference("classData").child(uid).child(classNameStr).child("subjects").child(model.getId()).removeValue();
                    }
                });
            }
        };


        subjectList.setAdapter(firebaseRecyclerAdapter);
    }

    public void firebaseSearchDivisions(Query q) {

        Log.d("recycle debug", "firebasesearch");

        FirebaseRecyclerAdapter<Division, DivisionViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Division, DivisionViewHolder>(
                Division.class, R.layout.division_row, DivisionViewHolder.class, q
        ) {

            @Override
            protected void populateViewHolder(final DivisionViewHolder viewHolder, final Division model, final int position) {

                viewHolder.setDetails(model.getId(), model.getStrength());

                //remove button listener
                viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        db.getReference("classData").child(uid).child(classNameStr).child("divisions").child(model.getId()).removeValue();

                    }
                });
            }
        };


        divisionlist.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (databaseReference!=null && valueEventListener!=null)
        databaseReference.removeEventListener(valueEventListener);
    }
}
