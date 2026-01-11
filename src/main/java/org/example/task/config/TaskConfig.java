package org.example.task.config;

import org.example.task.services.TaskService;
import org.example.task.repository.InMemoryTaskRepository;
import org.example.task.repository.TaskRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TaskConfig {
    @Bean
    public TaskRepository taskRepository() { return new InMemoryTaskRepository(); }

    @Bean
    public TaskService taskService(TaskRepository repository) { return new TaskService(repository); }
}

