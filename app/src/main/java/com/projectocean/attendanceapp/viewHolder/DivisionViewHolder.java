package com.projectocean.attendanceapp.viewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.projectocean.attendanceapp.R;

public class DivisionViewHolder extends RecyclerView.ViewHolder {

    private View mView;
    public ImageView delete;

    public DivisionViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
        delete = itemView.findViewById(R.id.delete);
    }

    public void setDetails(String name,Integer strength){
        TextView mName = mView.findViewById(R.id.name);
        TextView mStrength = mView.findViewById(R.id.strength);

        mName.setText(name);
        mStrength.setText("Students "+strength.toString());

    }
}
