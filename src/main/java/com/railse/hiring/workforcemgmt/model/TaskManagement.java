package com.railse.hiring.workforcemgmt.model;



import com.railse.hiring.workforcemgmt.common.model.enums.ReferenceType;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class TaskManagement {
    private Long id;
    private Long referenceId;
    private ReferenceType referenceType;
    private Task task;
    private String description;
    private TaskStatus status;
    private Long assigneeId; // Simplified from Entity for this assignment
    private Long taskDeadlineTime;
    private Priority priority;
    private List<Comment> comments = new ArrayList<>();
    private List<ActivityLog> history = new ArrayList<>();

}




