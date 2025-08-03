package com.railse.hiring.workforcemgmt.service;



import com.railse.hiring.workforcemgmt.dto.*;
import com.railse.hiring.workforcemgmt.model.Priority;


import java.util.List;


public interface TaskManagementService {
    List<TaskManagementDto> createTasks(TaskCreateRequest request);
    List<TaskManagementDto> updateTasks(UpdateTaskRequest request);
    String assignByReference(AssignByReferenceRequest request);
    List<TaskManagementDto> fetchTasksByDate(TaskFetchByDateRequest request);
    TaskManagementDto findTaskById(Long id);
    List<TaskManagementDto> fetchByPriority(Priority priority);
    TaskManagementDto updatePriority(Long id, Priority priority);
    TaskManagementDto addComment(Long id, AddCommentRequest request);

}
