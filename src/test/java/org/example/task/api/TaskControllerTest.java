package org.example.task.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.task.domain.NotFoundException;
import org.example.task.domain.Task;
import org.example.task.domain.TaskStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private org.example.task.services.TaskService taskService;

    @Test
    void createTask_success() throws Exception {
        TaskController.CreateTaskRequest req = new TaskController.CreateTaskRequest();
        req.title = "Test";
        req.dueDate = LocalDate.now().plusDays(1);
        var task = new Task("1", req.title, null, TaskStatus.PENDING, req.dueDate);
        when(taskService.create(any(), any(), any(), any())).thenReturn(task);
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"));
    }

    @Test
    void createTask_missingTitle() throws Exception {
        TaskController.CreateTaskRequest req = new TaskController.CreateTaskRequest();
        req.dueDate = LocalDate.now().plusDays(1);
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTask_success() throws Exception {
        var task = new Task("1", "T", null, TaskStatus.PENDING, LocalDate.now().plusDays(1));
        when(taskService.get("1")).thenReturn(task);
        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"));
    }

    @Test
    void getTask_notFound() throws Exception {
        when(taskService.get("404")).thenThrow(new NotFoundException("Task not found"));
        mockMvc.perform(get("/tasks/404"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTask_success() throws Exception {
        TaskController.UpdateTaskRequest req = new TaskController.UpdateTaskRequest();
        req.title = "Updated";
        var updated = new Task("1", req.title, null, TaskStatus.PENDING, LocalDate.now().plusDays(2));
        when(taskService.update(eq("1"), any(), any(), any(), any())).thenReturn(updated);
        mockMvc.perform(put("/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated"));
    }

    @Test
    void updateTask_notFound() throws Exception {
        TaskController.UpdateTaskRequest req = new TaskController.UpdateTaskRequest();
        req.title = "Updated";
        when(taskService.update(eq("404"), any(), any(), any(), any())).thenThrow(new org.example.task.domain.NotFoundException("Task not found"));
        mockMvc.perform(put("/tasks/404")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTask_success() throws Exception {
        doNothing().when(taskService).delete("1");
        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTask_notFound() throws Exception {
        doThrow(new NotFoundException("Task not found")).when(taskService).delete("404");
        mockMvc.perform(delete("/tasks/404"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllTasks_success() throws Exception {
        var task = new Task("1", "T", null, TaskStatus.PENDING, LocalDate.now().plusDays(1));
        Page<Task> page = new PageImpl<>(List.of(task), PageRequest.of(0, 50), 1);
        when(taskService.getTasks(any(), anyInt(), anyInt())).thenReturn(page);
        mockMvc.perform(get("/tasks?status=PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"));
    }
}

