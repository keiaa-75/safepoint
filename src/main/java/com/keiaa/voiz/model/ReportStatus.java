package com.keiaa.voiz.model;

public enum ReportStatus {
    PENDING_REVIEW("Pending Review"),
    UNDER_INVESTIGATION("Under Investigation"),
    ACTION_TAKEN("Action Taken"),
    RESOLVED("Resolved");

    private final String displayName;

    ReportStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}