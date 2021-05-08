package com.projectocean.attendanceapp.viewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.projectocean.attendanceapp.R;

public class StudentClassViewHolder extends RecyclerView.ViewHolder {

    private View mView;

    public StudentClassViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
    }

    public void setDetails(String name){
        TextView mName = mView.findViewById(R.id.name);

        mName.setText(name);
    }
}
