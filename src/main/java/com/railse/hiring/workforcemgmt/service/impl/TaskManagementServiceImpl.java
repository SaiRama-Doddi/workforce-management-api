package com.railse.hiring.workforcemgmt.service.impl;



import com.railse.hiring.workforcemgmt.common.exception.ResourceNotFoundException;
import com.railse.hiring.workforcemgmt.dto.*;
import com.railse.hiring.workforcemgmt.mapper.ITaskManagementMapper;
import com.railse.hiring.workforcemgmt.model.*;

import com.railse.hiring.workforcemgmt.repository.TaskRepository;
import com.railse.hiring.workforcemgmt.service.TaskManagementService;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class TaskManagementServiceImpl implements TaskManagementService {


    private final TaskRepository taskRepository;
    private final ITaskManagementMapper taskMapper;


    public TaskManagementServiceImpl(TaskRepository taskRepository, ITaskManagementMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }


    @Override
    public TaskManagementDto findTaskById(Long id) {
        TaskManagement task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        return taskMapper.modelToDto(task);
    }

    @Override
    public List<TaskManagementDto> fetchByPriority(Priority priority) {
        List<TaskManagement> allTasks = taskRepository.findAll();
        return taskMapper.modelListToDtoList(
                allTasks.stream()
                        .filter(t -> t.getPriority() == priority)
                        .collect(Collectors.toList())
        );

    }

    @Override
    public TaskManagementDto updatePriority(Long id, Priority priority) {
        TaskManagement task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        task.setPriority(priority);
        task.getHistory().add(new ActivityLog("priority changed to:"+priority,System.currentTimeMillis()));

        return taskMapper.modelToDto(taskRepository.save(task));

    }


    @Override
    public List<TaskManagementDto> createTasks(TaskCreateRequest createRequest) {
        List<TaskManagement> createdTasks = new ArrayList<>();
        for (TaskCreateRequest.RequestItem item : createRequest.getRequests()) {
            TaskManagement newTask = new TaskManagement();
            newTask.setReferenceId(item.getReferenceId());
            newTask.setReferenceType(item.getReferenceType());
            newTask.setTask(item.getTask());
            newTask.setAssigneeId(item.getAssigneeId());
            newTask.setPriority(item.getPriority());
            newTask.setTaskDeadlineTime(item.getTaskDeadlineTime());
            newTask.setStatus(TaskStatus.ASSIGNED);
            newTask.setDescription("New task created.");
            newTask.getHistory().add(new ActivityLog("Task created with status assigned ",System.currentTimeMillis()));
            createdTasks.add(taskRepository.save(newTask));
        }

        return taskMapper.modelListToDtoList(createdTasks);
    }


    @Override
    public List<TaskManagementDto> updateTasks(UpdateTaskRequest updateRequest) {
        List<TaskManagement> updatedTasks = new ArrayList<>();
        for (UpdateTaskRequest.RequestItem item : updateRequest.getRequests()) {
            TaskManagement task = taskRepository.findById(item.getTaskId())
                    .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + item.getTaskId()));


            if (item.getTaskStatus() != null) {
                task.setStatus(item.getTaskStatus());
            }
            if (item.getDescription() != null) {
                task.setDescription(item.getDescription());
            }
            task.getHistory().add(new ActivityLog("Task updated", System.currentTimeMillis()));

            updatedTasks.add(taskRepository.save(task));
        }
        return taskMapper.modelListToDtoList(updatedTasks);
    }


    @Override
    public String assignByReference(AssignByReferenceRequest request) {
        List<Task> applicableTasks = Task.getTasksByReferenceType(request.getReferenceType());
        List<TaskManagement> existingTasks = taskRepository.findByReferenceIdAndReferenceType(
                request.getReferenceId(), request.getReferenceType());

        for (Task taskType : applicableTasks) {
            List<TaskManagement> tasksOfType = existingTasks.stream()
                    .filter(t -> t.getTask() == taskType && t.getStatus() != TaskStatus.COMPLETED)
                    .collect(Collectors.toList());

            if (!tasksOfType.isEmpty()) {
                boolean reassigned = false;
                for (TaskManagement taskToUpdate : tasksOfType) {
                    if (!taskToUpdate.getAssigneeId().equals(request.getAssigneeId())) {
                        taskToUpdate.setStatus(TaskStatus.CANCELLED);
                        taskToUpdate.getHistory().add(
                                new ActivityLog("Task cancelled due to reassignment", System.currentTimeMillis())
                        );
                        taskRepository.save(taskToUpdate);
                        reassigned = true;
                    } else {
                        return "Task already assigned to this assignee.";
                    }
                }

                // Only create a new task if we cancelled the old one
                if (reassigned) {
                    TaskManagement newTask = new TaskManagement();
                    newTask.setReferenceId(request.getReferenceId());
                    newTask.setReferenceType(request.getReferenceType());
                    newTask.setTask(taskType);
                    newTask.setAssigneeId(request.getAssigneeId());
                    newTask.setStatus(TaskStatus.ASSIGNED);
                    newTask.getHistory().add(
                            new ActivityLog("Task reassigned and assigned to new assignee", System.currentTimeMillis())
                    );
                    taskRepository.save(newTask);
                }

            } else {
                // No existing task → create a fresh one
                TaskManagement newTask = new TaskManagement();
                newTask.setReferenceId(request.getReferenceId());
                newTask.setReferenceType(request.getReferenceType());
                newTask.setTask(taskType);
                newTask.setAssigneeId(request.getAssigneeId());
                newTask.setStatus(TaskStatus.ASSIGNED);
                newTask.getHistory().add(
                        new ActivityLog("Task created and assigned", System.currentTimeMillis())
                );
                taskRepository.save(newTask);
            }
        }

        return "Tasks assigned successfully for reference " + request.getReferenceId();
    }


    @Override
    public List<TaskManagementDto> fetchTasksByDate(TaskFetchByDateRequest request) {
        List<TaskManagement> tasks = taskRepository.findByAssigneeIdIn(request.getAssigneeIds());

   long start=request.getStartDate();
   long end=request.getEndDate();
        List<TaskManagement> filteredTasks = tasks.stream()
                .filter(task -> task.getStatus()!=TaskStatus.CANCELLED &&
                        (
                                (task.getTaskDeadlineTime() >= start && task.getTaskDeadlineTime()<=end)
                                ||
                                        (task.getTaskDeadlineTime()< start && task.getStatus()!=TaskStatus.COMPLETED)
                                )
                )
                .collect(Collectors.toList());


        return taskMapper.modelListToDtoList(filteredTasks);
    }


    @Override
    public TaskManagementDto addComment(Long id, AddCommentRequest request) {
        TaskManagement task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        Comment comment = new Comment();
        comment.setMessage(request.getMessage());
        comment.setUser(request.getUser());
        comment.setTimestamp(System.currentTimeMillis());
        task.getComments().add(comment);

        task.getHistory().add(new ActivityLog("Comment added by " + request.getUser(), System.currentTimeMillis()));
        return taskMapper.modelToDto(taskRepository.save(task));
    }


}




