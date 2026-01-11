package org.example.task.services;

import org.example.task.domain.Task;
import org.example.task.domain.TaskStatus;
import org.example.task.repository.TaskRepository;
import org.example.task.domain.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.*;

public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task create(String title, String description, TaskStatus status, LocalDate dueDate) {
        validateTitle(title);
        validateDueDate(dueDate);
        String id = UUID.randomUUID().toString();
        Task task = new Task(id, title, description, status == null ? TaskStatus.PENDING : status, dueDate);
        taskRepository.save(task);
        System.out.println(task);
        return task;
    }

    public Task get(String id) {
        return taskRepository.findById(id).orElseThrow(() -> new NotFoundException("Task not found"));
    }

    public Task update(String id, String title, String description, TaskStatus status, LocalDate dueDate) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new NotFoundException("Task not found"));
        if (title != null) { validateTitle(title); task.setTitle(title); }
        if (dueDate != null) { validateDueDate(dueDate); task.setDueDate(dueDate); }
        if (description != null) task.setDescription(description);
        if (status != null) task.setStatus(status);
        taskRepository.save(task);
        return task;
    }

    public void delete(String id) {
        if (taskRepository.findById(id).isEmpty()) throw new NotFoundException("Task not found");
        taskRepository.deleteById(id);
    }

    public Page<Task> getTasks(TaskStatus taskStatus, int page, int size) {
        // Use repository sorting by dueDate
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("dueDate"));
        Page<Task> taskPage = taskRepository.findAll(pageRequest);
        if (taskStatus != null) {
            // Filter by status, but keep paging info
            List<Task> filtered = taskPage.stream()
                .filter(task -> task.getStatus() == taskStatus)
                .toList();
            return new org.springframework.data.domain.PageImpl<>(filtered, pageRequest, filtered.size());
        }
        return taskPage;
    }

    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title is required");
        }
    }

    private void validateDueDate(LocalDate dueDate) {
        if (dueDate == null) {
            throw new IllegalArgumentException("due_date is required");
        }
        if (!dueDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("due_date must be in the future");
        }
    }
}
