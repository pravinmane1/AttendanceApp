package com.projectocean.attendanceapp.dataClass;

import java.util.ArrayList;

public class StudentClass  implements Comparable<StudentClass> {
    String id;
    Integer priority;
    ArrayList<Division> divisionList;

    public ArrayList<Division> getDivisionList() {
        return divisionList;
    }

    public void setDivisionList(ArrayList<Division> divisionList) {
        this.divisionList = divisionList;
    }

    public StudentClass(){
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    @Override
    public int compareTo(StudentClass o) {
        return this.priority.compareTo(o.getPriority());
    }
}
