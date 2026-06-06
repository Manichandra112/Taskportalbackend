package com.example.demo.dto;

import com.example.demo.enums.TaskPriority;
import com.example.demo.enums.TaskStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    private TaskPriority priority;

    @FutureOrPresent(message = "Due date must be in the present or future")
    private LocalDate dueDate;

    @Size(max = 50, message = "Estimated time must not exceed 50 characters")
    private String estimatedTime;

    private TaskStatus status;
}
