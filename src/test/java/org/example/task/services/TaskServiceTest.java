package org.example.task.services;

import org.example.task.domain.NotFoundException;
import org.example.task.domain.Task;
import org.example.task.domain.TaskStatus;
import org.example.task.repository.InMemoryTaskRepository;
import org.example.task.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TaskServiceTest {
    private TaskService service;
    private TaskRepository repo;

    @BeforeEach
    void setup() {
        repo = new InMemoryTaskRepository();
        service = new TaskService(repo);
    }

    @Test
    void create_and_get_task_success() {
        Task created = service.create("Title", "Desc", TaskStatus.PENDING, LocalDate.now().plusDays(2));
        assertNotNull(created.getId());
        Task fetched = service.get(created.getId());
        assertEquals("Title", fetched.getTitle());
        assertEquals(TaskStatus.PENDING, fetched.getStatus());
    }

    @Test
    void create_task_missing_title() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
            service.create(null, "desc", TaskStatus.PENDING, LocalDate.now().plusDays(1)));
        assertTrue(ex.getMessage().contains("title"));
    }

    @Test
    void create_task_due_date_in_past() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
            service.create("T", "desc", TaskStatus.PENDING, LocalDate.now().minusDays(1)));
        assertTrue(ex.getMessage().contains("future"));
    }

    @Test
    void get_task_not_found() {
        assertThrows(NotFoundException.class, () -> service.get("notfound"));
    }

    @Test
    void update_task_success() {
        Task created = service.create("T", "D", TaskStatus.PENDING, LocalDate.now().plusDays(2));
        Task updated = service.update(created.getId(), "T2", "D2", TaskStatus.DONE, LocalDate.now().plusDays(3));
        assertEquals("T2", updated.getTitle());
        assertEquals("D2", updated.getDescription());
        assertEquals(TaskStatus.DONE, updated.getStatus());
    }

    @Test
    void update_task_not_found() {
        assertThrows(NotFoundException.class, () ->
            service.update("notfound", "T", null, null, LocalDate.now().plusDays(1)));
    }

    @Test
    void delete_task_success() {
        Task created = service.create("T", "D", TaskStatus.PENDING, LocalDate.now().plusDays(2));
        service.delete(created.getId());
        assertThrows(NotFoundException.class, () -> service.get(created.getId()));
    }

    @Test
    void delete_task_not_found() {
        assertThrows(NotFoundException.class, () -> service.delete("notfound"));
    }

    @Test
    void getTasks_paging_and_status_filter() {
        service.create("T1", "D1", TaskStatus.PENDING, LocalDate.now().plusDays(2));
        service.create("T2", "D2", TaskStatus.DONE, LocalDate.now().plusDays(3));
        service.create("T3", "D3", TaskStatus.PENDING, LocalDate.now().plusDays(4));
        Page<Task> page = service.getTasks(TaskStatus.PENDING, 0, 10);
        assertEquals(2, page.getTotalElements());
        assertTrue(page.getContent().stream().allMatch(t -> t.getStatus() == TaskStatus.PENDING));
    }
}

