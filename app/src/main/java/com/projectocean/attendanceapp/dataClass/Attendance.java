package com.projectocean.attendanceapp.dataClass;

public class Attendance {
    String classId,divisionId,id,subject,subjectId,date;
    Integer strength;
    Float presentPercent;

    public Attendance() {
    }

    public Integer getStrength() {
        return strength;
    }

    public void setStrength(Integer strength) {
        this.strength = strength;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }


    public Float getPresentPercent() {
        return presentPercent;
    }

    public void setPresentPercent(Float presentPercent) {
        this.presentPercent = presentPercent;
    }

    public Attendance(String classId, String divisionId, String id, String subject, String subjectId, String date) {
        this.classId = classId;
        this.divisionId = divisionId;
        this.id = id;
        this.subject = subject;
        this.subjectId = subjectId;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDivisionId() {
        return divisionId;
    }

    public void setDivisionId(String divisionId) {
        this.divisionId = divisionId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }
}