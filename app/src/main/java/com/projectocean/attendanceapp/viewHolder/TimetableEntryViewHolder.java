package com.projectocean.attendanceapp.viewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.projectocean.attendanceapp.R;

public class TimetableEntryViewHolder extends RecyclerView.ViewHolder {

    public  View mView;
    public ImageView delete,edit;

    public TimetableEntryViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
        delete = itemView.findViewById(R.id.delete);
        edit = itemView.findViewById(R.id.edit);
    }

    public void setDetails(String studentClass,String division,String subject){

        TextView mClass = mView.findViewById(R.id.class_name);
        TextView mDivision = mView.findViewById(R.id.division_name);
        TextView mSubject = mView.findViewById(R.id.subject_name);

        mClass.setText(studentClass);
        mDivision.setText(division);
        mSubject.setText(subject);

    }
}
