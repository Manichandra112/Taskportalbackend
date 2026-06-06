package com.example.demo.controller;

import com.example.demo.dto.TaskRequest;
import com.example.demo.dto.TaskResponse;
import com.example.demo.entity.User;
import com.example.demo.enums.TaskStatus;
import com.example.demo.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) TaskStatus status) {

        List<TaskResponse> tasks;
        if (status != null) {
            tasks = taskService.getTasksByStatus(user, status);
        } else {
            tasks = taskService.getAllTasks(user);
        }
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(taskService.getTaskById(id, user));
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody TaskRequest request,
            @AuthenticationPrincipal User user) {
        TaskResponse task = taskService.createTask(request, user);
        return new ResponseEntity<>(task, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(taskService.updateTask(id, request, user));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponse> updateTaskStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> statusMap,
            @AuthenticationPrincipal User user) {
        TaskStatus status = TaskStatus.valueOf(statusMap.get("status"));
        return ResponseEntity.ok(taskService.updateTaskStatus(id, status, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteTask(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        taskService.deleteTask(id, user);
        return ResponseEntity.ok(Map.of("message", "Task deleted successfully"));
    }
}
