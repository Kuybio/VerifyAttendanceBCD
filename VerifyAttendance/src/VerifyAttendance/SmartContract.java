package VerifyAttendance;

public class SmartContract {
	
    public void notifyStudentForAbsence(AttendanceRecord record, Blockchain blockchain) {
        // Get all blocks pertaining to this student and course
        String studentID = record.getStudentID();
        String courseID = record.getCourseID();
        int absenceCount = 0;

        for (Block block : blockchain.getChain()) {
            AttendanceRecord blockRecord = block.getAttendanceRecord();
            if (blockRecord != null && 
                studentID.equals(blockRecord.getStudentID()) && 
                courseID.equals(blockRecord.getCourseID()) && 
                "Absent".equals(blockRecord.getStatus())) {
                absenceCount++;
            }
        }
        if (absenceCount > 3) {
            // Code to notify the student (e.g., sending an email, an SMS, or an in-app notification)
            System.out.println("Notification: Student " + studentID + " has been absent " + absenceCount + " times in course " + courseID);
        }
    }
}
