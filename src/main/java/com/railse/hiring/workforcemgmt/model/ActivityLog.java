package com.railse.hiring.workforcemgmt.model;

import lombok.Data;

@Data
public class ActivityLog {
    private String message;
    private Long timestamp;

    public ActivityLog(String message, long timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }
}
