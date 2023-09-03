package VerifyAttendance;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class Registration {
    
    // CSV file paths
    private String studentCsv = "student.csv";
    private String classesCsv = "classes.csv";
    private String enrollmentCsv = "enrollment.csv";
    
    public Registration() {
        // Ensure CSV files exist
        ensureCsvExists(studentCsv);
        ensureCsvExists(classesCsv);
        ensureCsvExists(enrollmentCsv);
    }
    
    public String generateNextStudentID() {
        List<Student> existingStudents = getAllStudents();
        int maxID = 0;
        for (Student student : existingStudents) {
            String id = student.getId();  // Use the correct getter
            if (id.matches("STU\\d{6}")) {  // Check if the ID conforms to the expected pattern
                String numPart = id.substring(3);  // Assumes the format "STU######"
                int num = Integer.parseInt(numPart);
                if (num > maxID) {
                    maxID = num;
                }
            }
        }
        return String.format("STU%06d", maxID + 1);
    }

    public void registerStudent(String fullName, String emergencyContact) {
        String nextStudentID = generateNextStudentID();  // Pass the prefix "STU" here

        // Input Validation
        if (!fullName.matches("[A-Za-z ]{5,100}") || 
            !emergencyContact.matches("\\d{10,11}")) {
            System.out.println("Invalid input. Please try again.");
            return;  // Return here to prevent adding
        }

        // Write to student.csv
        try (FileWriter writer = new FileWriter(studentCsv, true)) {  // Changed to use studentCsv
            writer.write(fullName + "," + nextStudentID + "," + emergencyContact + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void registerClass(String className, String classID, String lecturerID) {
        if (!className.matches("[A-Za-z\\s]{5,100}") || 
            !classID.matches("[A-Z]{4}-\\d{4}") || 
            !lecturerID.matches("LEC\\d{6}")) {
            System.out.println("Invalid input. Please try again.");
            return;
        }
        // Write to classes.csv
        appendToCsv(classesCsv, className + "," + classID + "," + lecturerID + "\n");
    }
    
    public void enrollStudentInClass(String studentID, String classID) {
    	System.out.println("Enrolling Student: " + studentID + " into Class: " + classID);
        if (!studentID.matches("STU\\d{6}") || 
            !classID.matches("[A-Z]{4}-\\d{4}")) {
            System.out.println("Invalid input. Please try again.");
            return;
        }
        // Write to enrollment.csv
        appendToCsv(enrollmentCsv, studentID + "," + classID + "\n");
    }
    
    public List<String> getEnrolledStudents(String classID) {
        List<String> studentIDs = new ArrayList<>();
        
        // Read the lines from enrollment.csv
        List<String> lines = readCsv(enrollmentCsv);
        
        for (String line : lines) {
            String[] tokens = line.split(",");
            if (tokens[1].equals(classID)) {
                studentIDs.add(tokens[0]);
            }
        }
        
        return studentIDs;
    }
    
    public List<Class> getAllClasses() {
        List<Class> classes = new ArrayList<>();
        List<String> lines = readCsv(classesCsv);
        for (String line : lines) {
            String[] tokens = line.split(",");
            classes.add(new Class(tokens[1], tokens[0], tokens[2]));
        }
        return classes;
    }

    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        List<String> lines = readCsv(studentCsv);
        for (String line : lines) {
            String[] tokens = line.split(",");
            students.add(new Student(tokens[1], tokens[0], tokens[2]));
        }
        return students;
    }
    
    public void addClass(String className, String classID, String lecturerID) {
        // Input Validation
        if (!className.matches("[A-Za-z\\s]{5,100}") || 
            !classID.matches("[A-Z]{4}-\\d{4}") || 
            !lecturerID.matches("LEC\\d{6}")) {
            System.out.println("Invalid input. Please try again.");
            return;  // Return here to prevent adding
        }
        
        // Write to classes.csv
        try (FileWriter writer = new FileWriter(classesCsv, true)) {
            writer.write(className + "," + classID + "," + lecturerID + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

    public void addStudent(String fullName, String emergencyContact) {
    	String studentID = generateNextStudentID();
    	// Input Validation
        if (!fullName.matches("[A-Za-z ]{5,100}") || 
            !studentID.matches("STU\\d{6}") || 
            !emergencyContact.matches("\\d{10,11}")) {
            System.out.println("Invalid input. Please try again.");
            return;
        }
        
        try (FileWriter writer = new FileWriter(studentCsv, true)) {
            writer.write(fullName + "," + studentID + "," + emergencyContact + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void enrollStudent(String studentID, String classID) {
        // Input Validation
        if (!studentID.matches("STU\\d{6}") || 
            !classID.matches("[A-Z]{4}-\\d{4}")) {
            System.out.println("Invalid input. Please try again.");
            return;
        }
        
        // Write to enrollment.csv
        try (FileWriter writer = new FileWriter(enrollmentCsv, true)) {
            writer.write(studentID + "," + classID + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private List<String> readCsv(String filePath) {
        List<String> lines = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(new File(filePath));
            while (scanner.hasNextLine()) {
                lines.add(scanner.nextLine());
            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    // Utility method to ensure that a CSV file exists
    private void ensureCsvExists(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
                FileWriter writer = new FileWriter(file);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void appendToCsv(String filePath, String data) {
        try {
            FileWriter writer = new FileWriter(new File(filePath), true); // true to append
            writer.write(data);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
