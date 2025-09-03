package com.example.rescueappforresponder.models;

public class Incident {
    private String reportId;
    private String emergencyType;
    private String emergencySeverity;
    private String locationText;
    private String respondingTeam;
    private String status;
    private String notes;

    public Incident() {} // Needed for Firestore

    public String getReportId() { return reportId; }
    public String getType() { return emergencyType; }
    public String getSeverity() { return emergencySeverity; }
    public String getLocation() { return locationText; }
    public String getRespondingTeam() { return respondingTeam; }
    public String getStatus() { return status; }
    public String getNotes() { return notes; }
}
