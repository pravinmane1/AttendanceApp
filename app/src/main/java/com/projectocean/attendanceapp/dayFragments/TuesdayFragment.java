package com.projectocean.attendanceapp.dayFragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.projectocean.attendanceapp.R;
import com.projectocean.attendanceapp.dataClass.TimetableEntry;
import com.projectocean.attendanceapp.viewHolder.TimetableEntryViewHolder;
import com.projectocean.attendanceapp.dataClass.Division;
import com.projectocean.attendanceapp.dataClass.StudentClass;
import com.projectocean.attendanceapp.dataClass.Subject;

import java.util.ArrayList;
import java.util.Collections;

public class TuesdayFragment extends Fragment {

    FirebaseAuth mAuth;
    FirebaseDatabase db;
    String uid;
    RecyclerView itemList;

    private Spinner yearSpinner, divisionSpinner,subjectSpinner;
    ArrayAdapter<String> divisionAdapter, yearAdapter,subjectAdapter;
    ArrayList<String> divisions,subjects;
    ArrayList<String> years;
    ArrayList<StudentClass> yearList;
    ArrayList<Subject>subjectList;

    ProgressDialog pd;
    DatabaseReference  yearsRef;
    DatabaseReference  subjectRef;
    String oldYearName="",oldDivisionName="";
    String yearName = "", divisionName = "";

    String subjectCode = "";
    String subjectName="";
    private String oldSubjectCode="";

    private StudentClass selectedData;
    private boolean found = false;
    private String mode="";

    private DatabaseReference mondayRef;
    private ValueEventListener valueEventListener;



    public TuesdayFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.fragment_monday, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        uid = mAuth.getUid();


        itemList = root.findViewById(R.id.recycler_view);

        itemList.setHasFixedSize(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        itemList.setLayoutManager(linearLayoutManager);


        mondayRef = db.getReference("timetable").child(uid).child("tuesday");

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                RelativeLayout emptyl = root.findViewById(R.id.emptyl);

                if (dataSnapshot.exists()){
                    emptyl.setVisibility(View.GONE);
                    itemList.setVisibility(View.VISIBLE);
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


        mondayRef.addValueEventListener(valueEventListener);

        firebaseSearch(mondayRef);


        return root;

    }

    public void firebaseSearch(Query q){

        FirebaseRecyclerAdapter<TimetableEntry, TimetableEntryViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<TimetableEntry, TimetableEntryViewHolder>(
                TimetableEntry.class, R.layout.timetable_entry_row, TimetableEntryViewHolder.class, q
        ) {

            @Override
            protected void populateViewHolder(final TimetableEntryViewHolder viewHolder, final TimetableEntry model, final int position) {

                viewHolder.setDetails(model.getClassId(),model.getDivisionId(),model.getSubject());

                viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        db.getReference("timetable").child(uid).child("tuesday").child(model.getId()).removeValue();
                    }
                });

                viewHolder.edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mode = "edit";

                        oldSubjectCode = subjectCode = "";
                        subjectName = "";
                        oldDivisionName = divisionName = "";
                        oldYearName = yearName = "";

                        final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                        LayoutInflater inflater = getActivity().getLayoutInflater();

                        View customDialog = inflater.inflate(R.layout.add_timetable_dialog,null);
                        alert.setView(customDialog);
                        final EditText name = (EditText)customDialog.findViewById(R.id.division_name);
                        final EditText strength = customDialog.findViewById(R.id.strength);
                        alert.setCancelable(true);



                        yearSpinner = customDialog.findViewById(R.id.course_spinner);
                        divisionSpinner = customDialog.findViewById(R.id.branch_spinner);
                        subjectSpinner = customDialog.findViewById(R.id.subject);




                        yearsRef = db.getReference("classData").child(uid);

                        years = new ArrayList<>();
                        divisions = new ArrayList<>();
                        yearList = new ArrayList<>();

                        subjects = new ArrayList<>();
                        subjectList = new ArrayList<>();

                        yearAdapter = new ArrayAdapter(getContext(), R.layout.spinner_item2, years);
                        divisionAdapter = new ArrayAdapter(getContext(), R.layout.spinner_item2, divisions);
                        subjectAdapter = new ArrayAdapter(getContext(), R.layout.spinner_item2, subjects);

                        yearSpinner.setAdapter(yearAdapter);
                        divisionSpinner.setAdapter(divisionAdapter);
                        subjectSpinner.setAdapter(subjectAdapter);


                        yearsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Log.d("debugcrash", "inside addlisterner");

                                years.clear();
                                divisions.clear();
                                yearAdapter.clear();
                                divisionAdapter.clear();
                                years.add("SELECT YEAR");
                                divisions.add("SELECT DIVISION");
                                StudentClass currentYear;

                                Log.d("debugcrash", "data is " + dataSnapshot);

                                for (DataSnapshot courseSnapshot : dataSnapshot.getChildren()) {

                                    //fetch all courses , add it to currentYear which will at the end be added to yearList
                                    // and added to courses which is attached to adapter
                                    Log.d("debugcrash", "the course is " + courseSnapshot);
                                    currentYear = courseSnapshot.getValue(StudentClass.class);

                                    ArrayList<Division> divisionList = new ArrayList<>();

                                    //if branches exists add it to branchlist and add to currentYear
                                    if (courseSnapshot.child("divisions").exists()) {

                                        for (DataSnapshot branchSnapshot : courseSnapshot.child("divisions").getChildren()) {
                                            divisionList.add(branchSnapshot.getValue(Division.class));
                                        }

                                        currentYear.setDivisionList(divisionList);
                                    }


                                    yearList.add(currentYear);

                                }

                                Collections.sort(yearList);

                                for (StudentClass c : yearList) {
                                    years.add(c.getId());
                                }
                                //courses are applied
                                yearAdapter.notifyDataSetChanged();

                                if (mode.equals("edit")){


                                    for (int i=0;i<yearList.size();i++){

                                        if (yearList.get(i).getId().equals(oldYearName)){
                                            yearSpinner.setSelection(i+1);
                                            break;
                                        }
                                    }
                                }

//                                pd.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(getContext(), "courseRef: " + databaseError.toException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        });


                        setListeners();

                        //alert.setIcon(R.drawable.plus_green);

                        alert.setPositiveButton("DISMISS", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                            }
                        });

                        alert.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, int which) {

                                if (yearName.equals("")){
                                    Toast.makeText(getActivity(),"Please enter year",Toast.LENGTH_SHORT).show();
                                }
                                else if (divisionName.equals("")){
                                    Toast.makeText(getActivity(),"Please enter division ",Toast.LENGTH_SHORT).show();

                                }
                                else if (subjectCode.equals("")){
                                    Toast.makeText(getActivity(),"Please enter subject",Toast.LENGTH_SHORT).show();

                                }
                                else{

//                                    pd.show();



                                    String day = "tuesday";

                                    String key =model.getId();

                                    TimetableEntry timetableEntry = new TimetableEntry();
                                    timetableEntry.setClassId(yearName);
                                    timetableEntry.setDivisionId(divisionName);
                                    timetableEntry.setId(key);
                                    timetableEntry.setSubject(subjectName);
                                    timetableEntry.setSubjectId(subjectCode);

                                    db.getReference("timetable").child(uid).child(day).child(key).setValue(timetableEntry).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            oldSubjectCode = subjectCode = "";
                                            oldDivisionName = divisionName = "";
                                            oldYearName = yearName = "";
                                            subjectName = "";

                                            //   pd.dismiss();
                                            dialog.dismiss();

                                        }
                                    });
                                }

                            }
                        });


                        AlertDialog dialog = alert.create();
                        dialog.show();

                    }
                });

            }
        };


        itemList.setAdapter(firebaseRecyclerAdapter);
    }

    private void setListeners() {
        //after selection of course set years and branch if exists
        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                divisions.clear();
                divisionAdapter.clear();
                divisionName="";
                divisions.add("SELECT DIVISION");
                divisionAdapter.notifyDataSetChanged();

                subjects.clear();
                Log.d("debugerror2",subjects.size()+"");

                subjectAdapter.clear();
                subjectCode="";
                subjectName="";
                subjects.add("SELECT SUBJECT");
                subjectAdapter.notifyDataSetChanged();


                //indicates selected course is found in courselist
                found = false;
                //get selected course for searching of year and branch
                String yearSelected = yearSpinner.getSelectedItem().toString();

                //match selected course name to the course in courselist
                for (StudentClass currentYear : yearList) {

                    if (yearSelected.equals(currentYear.getId())) {

                        found = true;
                        //selectedData can be further used to retain selected course
                        selectedData = currentYear;
                        yearName = currentYear.getId();


                        //if branches exits the make branchSpinner visible,and add branches from branchlist which is present in currentYear
                        //to the branches which is attached to spinner
                        if (currentYear.getDivisionList() != null) {
                            for (Division currentDivision : currentYear.getDivisionList()) {
                                divisions.add(currentDivision.getId());
                            }
                            divisionAdapter.notifyDataSetChanged();


                            if (oldYearName.equals(yearName)){

                                for (int i=0;i<currentYear.getDivisionList().size();i++){

                                    if (currentYear.getDivisionList().get(i).getId().equals(oldDivisionName)){
                                        divisionSpinner.setSelection(i+1);
                                        divisionName = currentYear.getDivisionList().get(i).getId();
                                        break;
                                    }
                                }
                            }
                            else {
                                divisionSpinner.setSelection(0);
                                divisionName= "";
                            }

                        }
                        break;
                    }
                }

                if (found){

                    subjectList.clear();

                    subjectRef = db.getReference("classData/"+uid+"/"+yearName+"/subjects");

                    subjectRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.d("debugcrash2", dataSnapshot.toString());


                            Subject currentSubject;


                            for (DataSnapshot subjectSnapshot : dataSnapshot.getChildren()) {

                                //fetch all courses , add it to currentYear which will at the end be added to yearList
                                // and added to courses which is attached to adapter
                                Log.d("debugcrash2", "the course is " + subjectSnapshot);

                                currentSubject = subjectSnapshot.getValue(Subject.class);
                                subjectList.add(currentSubject);

                            }

                            //  Collections.sort(subjectList);

                            for (Subject c : subjectList) {
                                Log.d("debugerror2","found year "+yearName+" subject size "+subjects.size()+" added subject: "+c.getId());

                                subjects.add(c.getName());
                            }
                            //courses are applied
                            subjectAdapter.notifyDataSetChanged();

                            if (mode.equals("edit")){


                                for (int i=0;i<subjectList.size();i++){

                                    if (subjectList.get(i).getId().equals(oldSubjectCode)){
                                        subjectSpinner.setSelection(i+1);
                                        break;
                                    }
                                }
                            }
                            Log.d("oldsubdebug",oldSubjectCode);

//                            pd.dismiss();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getContext(), "courseRef: " + databaseError.toException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //this listener is to obtain branch code for selected branch and generate finalCode
        divisionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                boolean flag = false;

                String divisionSelected = divisionSpinner.getSelectedItem().toString();
                if (!divisionSelected.equals("SELECT DIVISION")) {

                    for (Division currentDivision : selectedData.getDivisionList()) {

                        if (currentDivision.getId().equals(divisionSelected)) {
                            flag = true;
                            divisionName = currentDivision.getId();
                            break;
                        }
                    }
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                //indicates selected course is found in courselist
                //get selected course for searching of year and branch
                String subjectSelected = subjectSpinner.getSelectedItem().toString();

                //match selected course name to the course in courselist
                for (Subject currentSubject : subjectList) {

                    if (subjectSelected.equals(currentSubject.getName())) {
                        //selectedData can be further used to retain selected course
                        subjectCode = currentSubject.getId();
                        subjectName = currentSubject.getName();
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mondayRef!=null && valueEventListener!=null)
            mondayRef.removeEventListener(valueEventListener);
    }

}