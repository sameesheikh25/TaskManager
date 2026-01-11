package org.example.task.repository;

import org.example.task.domain.Task;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface TaskRepository extends PagingAndSortingRepository<Task, String> {
    void save(Task entity);

    Optional<Task> findById(String id);

    void deleteById(String id);

}
