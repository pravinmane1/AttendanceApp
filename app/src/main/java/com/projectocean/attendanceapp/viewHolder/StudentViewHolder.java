package com.projectocean.attendanceapp.viewHolder;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.projectocean.attendanceapp.R;

public class StudentViewHolder extends RecyclerView.ViewHolder {
    public View mView;
    public RelativeLayout relativeLayout;
    public StudentViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
        relativeLayout = mView.findViewById(R.id.relativelayout);

    }

    public void setDetails(int rollNo,Boolean present, Context context) {

        TextView mRollNo = mView.findViewById(R.id.roll_number);

        mRollNo.setText(String.valueOf(rollNo));

        if (!present){
            mView.setBackground(context.getResources().getDrawable(R.drawable.circle));
        }
        else{
            mView.setBackground(context.getResources().getDrawable(R.drawable.circle_checked));
        }
    }

}
