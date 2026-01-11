package org.example.task.api;

import org.example.task.services.TaskService;
import org.example.task.domain.Task;
import org.example.task.domain.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/tasks")
@Validated
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateTaskRequest req) {
        Task task = taskService.create(req.title, req.description, req.status, req.dueDate);
        return ResponseEntity.status(HttpStatus.CREATED).body(TaskResponse.from(task));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable String id) {
        Task task = taskService.get(id);
        return ResponseEntity.ok(TaskResponse.from(task));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody UpdateTaskRequest req) {
        Task updated = taskService.update(id, req.title, req.description, req.status, req.dueDate);
        return ResponseEntity.ok(TaskResponse.from(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks(
            @RequestParam TaskStatus status,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "50") @Min(1) int size
    ) {
        Page<Task> tasks = taskService.getTasks(status, page, size);
        return ResponseEntity.ok(tasks.stream().map(TaskResponse::from).toList());
    }

    public static class CreateTaskRequest {
        @NotBlank public String title;
        public String description;
        public TaskStatus status;
        @NotNull public LocalDate dueDate;
    }

    public static class UpdateTaskRequest {
        public String title;
        public String description;
        public TaskStatus status;
        public LocalDate dueDate;
    }

    public record TaskResponse(String id, String title, String description, TaskStatus status, LocalDate dueDate) {
        public static TaskResponse from(Task t) {
            return new TaskResponse(t.getId(), t.getTitle(), t.getDescription(), t.getStatus(), t.getDueDate());
        }
    }
}
