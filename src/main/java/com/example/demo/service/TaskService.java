package com.example.demo.service;

import com.example.demo.dto.TaskRequest;
import com.example.demo.dto.TaskResponse;
import com.example.demo.entity.Task;
import com.example.demo.entity.User;
import com.example.demo.enums.TaskStatus;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * Get all tasks for the authenticated user
     */
    public List<TaskResponse> getAllTasks(User user) {
        return taskRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get tasks filtered by status
     */
    public List<TaskResponse> getTasksByStatus(User user, TaskStatus status) {
        return taskRepository.findByUserAndStatusOrderByCreatedAtDesc(user, status)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get a single task by ID (scoped to user)
     */
    public TaskResponse getTaskById(Long id, User user) {
        Task task = taskRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        return mapToResponse(task);
    }

    /**
     * Create a new task
     */
    public TaskResponse createTask(TaskRequest request, User user) {
        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .dueDate(request.getDueDate())
                .estimatedTime(request.getEstimatedTime())
                .status(request.getStatus() != null ? request.getStatus() : TaskStatus.TODO)
                .user(user)
                .build();

        Task savedTask = taskRepository.save(task);
        return mapToResponse(savedTask);
    }

    /**
     * Update an existing task
     */
    public TaskResponse updateTask(Long id, TaskRequest request, User user) {
        Task task = taskRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());
        task.setEstimatedTime(request.getEstimatedTime());
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }

        Task updatedTask = taskRepository.save(task);
        return mapToResponse(updatedTask);
    }

    /**
     * Update only the status of a task
     */
    public TaskResponse updateTaskStatus(Long id, TaskStatus status, User user) {
        Task task = taskRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        task.setStatus(status);
        Task updatedTask = taskRepository.save(task);
        return mapToResponse(updatedTask);
    }

    /**
     * Delete a task
     */
    public void deleteTask(Long id, User user) {
        Task task = taskRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        taskRepository.delete(task);
    }

    /**
     * Map Task entity to TaskResponse DTO
     */
    private TaskResponse mapToResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .estimatedTime(task.getEstimatedTime())
                .status(task.getStatus())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}
