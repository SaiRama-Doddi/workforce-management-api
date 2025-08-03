package com.railse.hiring.workforcemgmt.model;

import lombok.Data;

@Data
public class Comment {
    private String message;
    private Long timestamp;
    private String user;
}

