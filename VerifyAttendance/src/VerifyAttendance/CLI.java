package VerifyAttendance;

import java.util.Scanner;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.regex.Matcher;
import java.util.ArrayList;

public class CLI {

    private Blockchain blockchain;
    private SmartContract smartContract;
    private Registration registration;

    public CLI() {
        this.blockchain = new Blockchain();
        this.smartContract = new SmartContract();
        this.registration = new Registration();
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        String command;

        while (true) {
            displayMenu();
            System.out.print("> ");
            command = scanner.nextLine().trim();

            switch (mapCommand(command)) {
                case "add-attendance":
                    addAttendanceRecord();
                    break;
                case "add-class":
                    addNewClass(scanner);
                    break;
                case "add-student":
                    addNewStudent(scanner);
                    break;
                case "enroll-student":
                    enrollStudent(scanner);
                    break;
                case "view":
                    viewSubMenu(scanner);
                    break;
                case "validate-single":
                    validateSingleBlock(scanner);
                    break;
                case "validate-system":
                    System.out.println("Is the blockchain system valid? " + blockchain.validateChain());
                    break;
                case "smart-contract":
                    // Existing logic to choose which record to evaluate
                    break;
                case "stats":
                    displayStats();
                    break;
                case "help":
                    displayHelp();
                    break;
                case "exit":
                    if (confirmExitApplication(scanner)) {
                        scanner.close();
                        return;
                    }
                    break;
                default:
                    System.out.println("Invalid or missing command. Try again.");
                    break;
            }
        }
    }

    private void displayMenu() {
        System.out.println("#######################################################");
        System.out.println("#                Attendance Verification              #");
        System.out.println("#######################################################");
        System.out.println("# 1: Add Attendance  - Add a new attendance record    #");
        System.out.println("# 2: Add Class       - Add a new class                #");
        System.out.println("# 3: Add Student     - Add a new student              #");
        System.out.println("# 4: Enroll Student  - Enroll student in a class      #");
        System.out.println("# 5: View            - View the blockchain list       #");
        System.out.println("# 6: Validate Single - Validate a single block        #");
        System.out.println("# 7: Validate System - Validate the blockchain system #");
        System.out.println("# 8: Smart Contract  - Run smart contract             #");
        System.out.println("# 9: Stats           - Show the blockchain statistic  #");
        System.out.println("# 10: Help           - Display the help menu          #");
        System.out.println("# 11: Exit           - Exit the application           #");
        System.out.println("#######################################################");
    }

    private String mapCommand(String input) {
        switch (input.toLowerCase()) {
            case "1":
            case "add-attendance":
                return "add-attendance";
            case "2":
            case "add-class":
                return "add-class";
            case "3":
            case "add-student":
                return "add-student";
            case "4":
            case "enroll-student":
                return "enroll-student";
            case "5":
            case "view":
                return "view";
            case "6":
            case "validate single":
                return "validate-single";
            case "7":
            case "validate system":
                return "validate-system";
            case "8":
            case "smart contract":
                return "smart-contract";
            case "9":
            case "stats":
                return "stats";
            case "10":
            case "help":
                return "help";
            case "11":
            case "exit":
                return "exit";
            default:
                return "invalid";
        }
    }
    
    public void addAttendanceRecord() {
        Scanner scanner = new Scanner(System.in);
        List<Class> classObjects = registration.getAllClasses();
        String classID = null;

        // Step 1: Choose a class with validation
        for (int attempts = 0; attempts < 3; attempts++) {
            try {
                System.out.println("Available Classes:");
                for (int i = 0; i < classObjects.size(); i++) {
                    System.out.println((i + 1) + ". " + classObjects.get(i).getName());
                }
                System.out.print("Select a class (by index): ");
                int classIndex = Integer.parseInt(scanner.nextLine().trim()) - 1;
                classID = classObjects.get(classIndex).getId();
                break;
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                System.out.println("Invalid input. Please try again.");
            }
        }

        if (classID == null) {
            System.out.println("Failed to select a class after 3 attempts. Returning to main menu.");
            return;
        }

        // Step 2: List students
        List<String> enrolledStudents = registration.getEnrolledStudents(classID);
        if (enrolledStudents.isEmpty()) {
            System.out.println("No students are enrolled in this class.");
            return;
        }

        List<AttendanceRecord> attendanceRecords = new ArrayList<>();
        System.out.println("Mark attendance for the following students (type 'done' to finish):");

        // Step 3: Mark attendance
        for (int i = 0; i < enrolledStudents.size(); i++) {
            System.out.print((i + 1) + ". " + enrolledStudents.get(i) + " (Present/Absent): ");
            String attendance = scanner.nextLine().trim().toLowerCase();

            if ("done".equals(attendance)) {
                break;
            }

            if (!"present".equals(attendance) && !"absent".equals(attendance)) {
                System.out.println("Invalid input. Skipping this student.");
                continue;
            }

            Date currentDate = new Date();  // Capturing the current date and time
            String status = "present".equals(attendance) ? "Present" : "Absent";
            
            AttendanceRecord record = new AttendanceRecord(
                    enrolledStudents.get(i),  // Student ID
                    currentDate,              // Current date
                    status,                   // Status (Present or Absent)
                    classID,                  // Class ID
                    "instructorID_here",      // Instructor ID
                    currentDate.getTime(),    // Current time in milliseconds
                    null                      // Digital Signature (can be set later)
            );
            attendanceRecords.add(record);
        }


        // Step 4: Confirm
        System.out.println("Confirm the attendance records? (Yes/No)");
        String confirm = scanner.nextLine().trim().toLowerCase();
        if (!"yes".equals(confirm)) {
            System.out.println("Attendance not recorded.");
            return;
        }

        // Step 5: Upload to blockchain
        for (AttendanceRecord record : attendanceRecords) {
            blockchain.addBlock(new Block(record));
        }
        System.out.println("Attendance records added to the blockchain.");
    }


    private void addNewClass(Scanner scanner) {
        String className = getInput(scanner, "Enter the new class name (5-100 characters):", "[A-Za-z\\s]{5,100}", 3);
        if (className == null) return;

        String classID = getInput(scanner, "Enter the new class ID (Format: ABCD-####):", "[A-Z]{4}-\\d{4}", 3);
        if (classID == null) return;

        if (className == null || classID == null) {
            System.out.println("Failed at input validation in CLI.");
            return;
        }
        String lecturerIDPattern = "LEC\\d{6}";  // adjust this regex based on your requirements
        String lecturerID = getInput(scanner, "Enter the new lecturer ID (Format: LEC######):", lecturerIDPattern, 3);
        if (lecturerID == null) {
            return;
        }

        // Add the new class through the Registration class
        registration.addClass(className, classID, lecturerID);
        System.out.println("New class added.");
        }

    private void addNewStudent(Scanner scanner) {
    	List<Student> studentObjects = registration.getAllStudents();  // <-- Change here
        List<String> students = studentObjects.stream().map(Student::getId).collect(Collectors.toList()); // <-- Change here
        String fullName = getInput(scanner, "Enter Student's Full Name (5-100 characters):", "[A-Za-z ]{5,100}", 3);
        if (fullName == null) return;

        String emergencyContact = getInput(scanner, "Enter Emergency Contact (10-11 digits):", "\\d{10,11}", 3);
        if (emergencyContact == null) return;

        // Add the new student through the Registration class
        registration.registerStudent(fullName, emergencyContact);

        System.out.println("New student added.");
    }

    private void enrollStudent(Scanner scanner) {
        List<Student> studentObjects = registration.getAllStudents();
        List<String> students = studentObjects.stream().map(Student::getId).collect(Collectors.toList());

        List<Class> classObjects = registration.getAllClasses();
        List<String> classes = classObjects.stream().map(Class::getId).collect(Collectors.toList()); // Use getId() instead of getName()

        // Selecting a student
        String studentID = selectFromList(scanner, "Select a student (by index):", students);
        if (studentID == null) return;

        // Selecting a class
        String classID = selectFromList(scanner, "Select a class (by index):", classes);
        if (classID == null) return;


        // Enroll the student in the class through the Registration class
        registration.enrollStudentInClass(studentID, classID);

        System.out.println("Student enrolled in class.");
    }

    
    private String selectFromList(Scanner scanner, String prompt, List<String> items) {
        for (int i = 0; i < items.size(); i++) {
            System.out.println((i + 1) + ". " + items.get(i));
        }
        System.out.println(prompt);
        int index = Integer.parseInt(scanner.nextLine().trim()) - 1;
        if (index >= 0 && index < items.size()) {
            return items.get(index);
        }
        return null;
    }
    
    private String getInput(Scanner scanner, String prompt, String pattern, int maxAttempts) {
        int attempts = 0;
        while (attempts < maxAttempts) {
            System.out.println(prompt);
            String input = scanner.nextLine().trim();

            if (isValidInput(input, pattern)) {  // Use the helper method here
                return input;
            }

            System.out.println("Invalid input. Please try again.");
            attempts++;
        }

        System.out.println("Maximum attempts reached. Returning to main menu.");
        return null;
    }

    private void displayHelp() {
        System.out.println("add - Add a new attendance record.");
        System.out.println("validate - Validate the entire blockchain.");
        System.out.println("notify - Notify based on attendance record.");
        System.out.println("view - View the entire blockchain.");
        System.out.println("help - Display this help menu.");
        System.out.println("stats - Display blockchain statistics.");
        System.out.println("validate-single - Validate a single block in the blockchain.");
        System.out.println("exit - Exit the application.");
    }

    private void displayStats() {
        System.out.println("Total Blocks: " + blockchain.getChain().size());
    }

    private void validateSingleBlock(Scanner scanner) {
        int attempts = 0;
        while (attempts < 3) {
            System.out.println("Enter the index of the block to validate:");
            try {
                int index = Integer.parseInt(scanner.nextLine().trim());
                if (index >= 0 && index < blockchain.getChain().size()) {
                    Block block = blockchain.getChain().get(index);
                    System.out.println("Is block valid? " + blockchain.validateSingleBlock(index));
                    if (confirmExitToMenu(scanner)) {
                        return;
                    }
                } else {
                    System.out.println("Invalid index. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
            attempts++;
        }
        if (confirmExitToMenu(scanner)) {
            System.out.println("Maximum attempts reached.");
        }
    }

    private boolean confirmExitToMenu(Scanner scanner) {
        System.out.println("You will be returned to the main menu. Continue? (y/ok to confirm, any other key to cancel)");
        String confirmation = scanner.nextLine().trim().toLowerCase();
        return confirmation.equals("y") || confirmation.equals("ok");
    }
    
    private boolean confirmExitApplication(Scanner scanner) {
        System.out.println("Are you sure you want to exit? (y/n)");
        String confirmation = scanner.nextLine().trim().toLowerCase();
        if (confirmation.equals("y")) {
            return true;
        } else if (confirmation.equals("n")) {
            return false;
        } else {
            System.out.println("No valid input detected. Returning to main menu.");
            return false;
        }
    }
    
    private void viewBlockchain() {
        Scanner scanner = new Scanner(System.in);
        String command;

        while (true) {
            displayViewMenu();
            System.out.print("> ");
            command = scanner.nextLine().trim();

            switch (mapViewCommand(command)) {
                case "student-details":
                    viewStudentDetails();
                    break;
                case "class-details":
                    viewClassDetails();
                    break;
                case "attendance-percentage":
                    viewAttendancePercentage();
                    break;
                case "below-70":
                    viewBelowSeventyAttendance();
                    break;
                case "block-count":
                    viewBlockCount();
                    break;
                case "":
                    viewClassAttendanceSummary();
                    break;
                case "exit":
                    return;
                default:
                    System.out.println("Invalid or missing command. Try again.");
                    break;
            }
        }
    }

    private void displayViewMenu() {
        System.out.println("########################################################");
        System.out.println("#                      View Menu                        #");
        System.out.println("########################################################");
        System.out.println("# 1: Student Details - View details of all students    #");
        System.out.println("# 2: Class Details  - View details of all classes      #");
        System.out.println("# 3: Attendance %   - View attendance percentage       #");
        System.out.println("# 4: Below 70%      - View students below 70%          #");
        System.out.println("# 5: Block Count    - View number of blocks            #");
        System.out.println("# 6: Class Summary - View the attendance summary #");
        System.out.println("# 7: Exit           - Return to the main menu          #");
        System.out.println("########################################################");
    }
    
    private void viewSubMenu(Scanner scanner) {
        while (true) {
            System.out.println("#######################################################");
            System.out.println("#                     View Menu                       #");
            System.out.println("#######################################################");
            System.out.println("# 1: Student Details                                  #");
            System.out.println("# 2: Class Details                                    #");
            System.out.println("# 3: Attendance percentage of the students            #");
            System.out.println("# 4: Students with below 70% attendance               #");
            System.out.println("# 5: How many blocks in the blockchain?               #");
            System.out.println("# 6: Class Attendance Summary                         #");
            System.out.println("# 7: Back to Main Menu                                #");
            System.out.println("#######################################################");
            System.out.print("> ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    // Your logic for showing Student Details
                    break;
                case "2":
                    // Your logic for showing Class Details
                    break;
                case "3":
                    // Your logic for showing Attendance percentage
                    break;
                case "4":
                    // Your logic for showing Students with below 70% attendance
                    break;
                case "5":
                    // Your logic for showing the number of blocks
                    break;
                case "6":
                    viewClassAttendanceSummary();
                    break;
                case "7":
                    return; // Back to the main menu
                default:
                    System.out.println("Invalid choice. Try again.");
                    break;
            }
        }
    }

    private String mapViewCommand(String input) {
        switch (input.toLowerCase()) {
            case "1":
                return "student-details";
            case "2":
                return "class-details";
            case "3":
                return "attendance-percentage";
            case "4":
                return "below-70";
            case "5":
                return "block-count";
            case "6":
                return "class-summary";
            case "7":
                return "exit";
            default:
                return "invalid";
        }
    }

    private void viewStudentDetails() {
        System.out.println("##################### Student Details #####################");
        List<Student> students = registration.getAllStudents();
        for (Student student : students) {
            System.out.println("ID: " + student.getId());
            System.out.println("Name: " + student.getName());
            System.out.println("Emergency Contact: " + student.getEmergencyContact());

            // Show the classes the student is enrolled in
            List<String> enrolledClasses = registration.getEnrolledStudents(student.getId());
            System.out.println("Enrolled Classes: " + String.join(", ", enrolledClasses));

            System.out.println("----------------------------------------------------------");
        }
    }

    private void viewClassDetails() {
        System.out.println("##################### Class Details #####################");
        List<Class> classes = registration.getAllClasses();
        for (Class cls : classes) {
            System.out.println("ID: " + cls.getId());
            System.out.println("Name: " + cls.getName());
            System.out.println("Lecturer: " + cls.getLecturer());

            // Show the students enrolled in the class
            List<String> enrolledStudents = registration.getEnrolledStudents(cls.getId());
            System.out.println("Enrolled Students: " + String.join(", ", enrolledStudents));

            System.out.println("-------------------------------------------------------");
        }
    }

    private void viewAttendancePercentage() {
        System.out.println("##################### Attendance Percentage #####################");
        // Logic to fetch all classes
        List<Class> classes = registration.getAllClasses();

        for (Class cls : classes) {
            System.out.println("Class ID: " + cls.getId());
            System.out.println("Class Name: " + cls.getName());

            // Logic to fetch all students in the class
            List<String> studentIDs = registration.getEnrolledStudents(cls.getId());

            for (String studentID : studentIDs) {
                // Logic to calculate attendance for each student in the class
                // For example, if you have a method to get attendance count:
                // int attendanceCount = getAttendanceCount(studentID, cls.getId());
                int attendanceCount = 0; // Placeholder
                System.out.println("Student ID: " + studentID + ", Attendance: " + attendanceCount + "%");
            }

            System.out.println("-------------------------------------------------------");
        }
    }

    private void viewBelowSeventyAttendance() {
        System.out.println("##################### Below 70% Attendance #####################");
        // Logic to fetch all classes
        List<Class> classes = registration.getAllClasses();

        for (Class cls : classes) {
            System.out.println("Class ID: " + cls.getId());
            System.out.println("Class Name: " + cls.getName());

            // Logic to fetch all students in the class
            List<String> studentIDs = registration.getEnrolledStudents(cls.getId());

            for (String studentID : studentIDs) {
                // Logic to calculate attendance for each student in the class
                // int attendanceCount = getAttendanceCount(studentID, cls.getId());
                int attendanceCount = 0; // Placeholder

                if (attendanceCount < 70) {
                    System.out.println("Student ID: " + studentID + ", Attendance: " + attendanceCount + "%");
                }
            }

            System.out.println("-------------------------------------------------------");
        }
    }

    private void viewBlockCount() {
        System.out.println("#################### Block Count ####################");
        int blockCount = blockchain.getChain().size(); // Assuming getChain() returns the list of all blocks
        System.out.println("Total Blocks: " + blockCount);
    }


    private void viewClassAttendanceSummary() {
        // ASCII art can go here
        System.out.println("###################################################");
        System.out.println("#             Class Attendance Summary            #");
        System.out.println("###################################################");

        // Logic to display summary. This is a placeholder; you'll need to replace it with your own logic.
        // Fetch all classes and their enrolled students
        List<Class> classObjects = registration.getAllClasses();
        List<String> classes = classObjects.stream().map(Class::getName).collect(Collectors.toList());

        for (String classID : classes) {
            List<String> enrolledStudents = registration.getEnrolledStudents(classID);
            int totalStudents = enrolledStudents.size();
            
            // Logic to find out how many students have attended the class
            // This is a placeholder; you'll need to replace it with your own logic
            int studentsAttended = 0;  // Replace this with actual logic

            double attendancePercentage = ((double) studentsAttended / totalStudents) * 100;

            System.out.println("Class ID: " + classID);
            System.out.println("Total Students: " + totalStudents);
            System.out.println("Students Attended: " + studentsAttended);
            System.out.printf("Attendance Percentage: %.2f%%\n", attendancePercentage);
            System.out.println("---------------------------------------------------");
        }
    }

    private void displayList(String title, List<String> items) {
        System.out.println("##################### " + title + " #####################");
        for (int i = 0; i < items.size(); i++) {
            System.out.println((i + 1) + ". " + items.get(i));
        }
        System.out.println("-------------------------------------------------------");
    }

    private boolean isValidInput(String input, String pattern) {
        return Pattern.matches(pattern, input);
    }

}
