package VerifyAttendance;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class Block {
    private AttendanceRecord attendanceRecord;  // The attendance data
    private String hash;                        // The hash of this block
    private String previousHash;                // The hash of the previous block

    public Block(AttendanceRecord attendanceRecord, String previousHash) {
        this.attendanceRecord = attendanceRecord;
        this.previousHash = previousHash;
        this.hash = calculateHash();
    }

    // Calculates and returns the hash of the block
    public String calculateHash() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String input = previousHash +
                           attendanceRecord.getStudentID() +
                           attendanceRecord.getDate().toString() +
                           attendanceRecord.getStatus() +
                           attendanceRecord.getCourseID() +
                           attendanceRecord.getInstructorID() +
                           Long.toString(attendanceRecord.getTimestamp());
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer(); // This will contain hash as hexadecimal

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException | java.io.UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    // Getters and setters
    public AttendanceRecord getAttendanceRecord() {
        return attendanceRecord;
    }

    public void setAttendanceRecord(AttendanceRecord attendanceRecord) {
        this.attendanceRecord = attendanceRecord;
    }

    public String getHash() {
        return hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Block block = (Block) o;
        return Objects.equals(attendanceRecord, block.attendanceRecord) &&
                Objects.equals(hash, block.hash) &&
                Objects.equals(previousHash, block.previousHash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attendanceRecord, hash, previousHash);
    }
}