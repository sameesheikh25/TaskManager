package org.example.task.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
public class Task {
    private String id;
    private String title;
    private String description;
    private TaskStatus status;
    private LocalDate dueDate;

    public Task(String id, String title, String description, TaskStatus status, LocalDate dueDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status == null ? TaskStatus.PENDING : status;
        this.dueDate = dueDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

