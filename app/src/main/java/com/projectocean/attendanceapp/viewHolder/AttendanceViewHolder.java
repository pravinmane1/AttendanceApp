package com.projectocean.attendanceapp.viewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.projectocean.attendanceapp.R;

import java.text.DecimalFormat;

public class AttendanceViewHolder extends RecyclerView.ViewHolder {

    private View mView;
    public Button enterAttendance;

    public AttendanceViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
        enterAttendance = itemView.findViewById(R.id.enter_attendance);
    }

    public void setDetails(String studentClass,String division,String subject, Float present_percent){
        TextView mStudentClass = mView.findViewById(R.id.student_class);
        TextView mDivision = mView.findViewById(R.id.division);
        TextView mSubject = mView.findViewById(R.id.subject);
        TextView mpresentPercent = mView.findViewById(R.id.persent_percent);

        String present_percent_string="";
        if (present_percent!=null){
            DecimalFormat numberFormat = new DecimalFormat("#.00");

            present_percent_string = numberFormat.format(present_percent);
            mpresentPercent.setText(present_percent_string+"%");

        }
        else{
            mpresentPercent.setText("");
        }



        mStudentClass.setText(studentClass);
        mDivision.setText(division);
        mSubject.setText(subject);
    }
}