package VerifyAttendance;

import java.util.Date;
import java.util.Objects;

public class AttendanceRecord {
    private String studentID;
    private Date date;
    private String status;  // Present, Absent, Tardy
    private String courseID;
    private String instructorID;
    private long timestamp;  // Epoch time in milliseconds
    private byte[] digitalSignature;

    public AttendanceRecord(String studentID, Date date, String status,
                            String courseID, String instructorID, long timestamp,
                            byte[] digitalSignature) {
        this.studentID = studentID;
        this.date = date;
        this.status = status;
        this.courseID = courseID;
        this.instructorID = instructorID;
        this.timestamp = timestamp;
        this.digitalSignature = digitalSignature;
    }

    // Getters and Setters
    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCourseID() {
        return courseID;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public String getInstructorID() {
        return instructorID;
    }

    public void setInstructorID(String instructorID) {
        this.instructorID = instructorID;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public byte[] getDigitalSignature() {
        return digitalSignature;
    }

    public void setDigitalSignature(byte[] digitalSignature) {
        this.digitalSignature = digitalSignature;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttendanceRecord that = (AttendanceRecord) o;
        return timestamp == that.timestamp &&
                studentID.equals(that.studentID) &&
                date.equals(that.date) &&
                status.equals(that.status) &&
                courseID.equals(that.courseID) &&
                instructorID.equals(that.instructorID) &&
                digitalSignature.equals(that.digitalSignature);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentID, date, status, courseID, instructorID, timestamp, digitalSignature);
    }
}