package VerifyAttendance;

public class Student {
    private String id;
    private String name;
    private String emergencyContact;

    // Constructor
    public Student(String id, String name, String emergencyContact) {
        this.id = id;
        this.name = name;
        this.emergencyContact = emergencyContact;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }
}
