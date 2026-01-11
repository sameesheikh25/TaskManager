package org.example.task.repository;

import org.example.task.domain.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryTaskRepository implements TaskRepository {
    private final Map<String, Task> store = new ConcurrentHashMap<>();

    @Override
    public Iterable<Task> findAll(Sort sort) {
        List<Task> all = new ArrayList<>(store.values());
        Comparator<Task> comparator = null;
        for (Sort.Order order : sort) {
            Comparator<Task> c = switch (order.getProperty()) {
                case "dueDate" -> Comparator.comparing(Task::getDueDate);
                case "title" -> Comparator.comparing(Task::getTitle, Comparator.nullsLast(String::compareTo));
                case "status" -> Comparator.comparing(Task::getStatus);
                default -> null;
            };
            if (c != null) {
                if (order.isDescending()) c = c.reversed();
                comparator = comparator == null ? c : comparator.thenComparing(c);
            }
        }
        if (comparator != null) all.sort(comparator);
        return all;
    }

    @Override
    public Page<Task> findAll(Pageable pageable) {
        List<Task> sorted = (List<Task>) findAll(pageable.getSort());
        int from = Math.min(pageable.getPageNumber() * pageable.getPageSize(), sorted.size());
        int to = Math.min(from + pageable.getPageSize(), sorted.size());
        List<Task> content = sorted.subList(from, to);
        return new PageImpl<>(content, pageable, sorted.size());
    }

    @Override
    public void save(Task entity) {
        store.put(entity.getId(), entity);
    }

//    @Override
//    public <S extends Task> Iterable<S> saveAll(Iterable<S> entities) {
//        for (S t : entities) store.put(t.getId(), t);
//        return entities;
//    }

    @Override
    public Optional<Task> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

//    @Override
//    public boolean existsById(String id) {
//        return store.containsKey(id);
//    }
//
//    @Override
//    public Iterable<Task> findAll() {
//        return new ArrayList<>(store.values());
//    }
//
//    @Override
//    public Iterable<Task> findAllById(Iterable<String> ids) {
//        List<Task> list = new ArrayList<>();
//        for (String id : ids) {
//            Task t = store.get(id);
//            if (t != null) list.add(t);
//        }
//        return list;
//    }

//    @Override
//    public long count() {
//        return store.size();
//    }

    @Override
    public void deleteById(String id) {
        store.remove(id);
    }

//    @Override
//    public void delete(Task entity) {
//        if (entity != null) store.remove(entity.getId());
//    }

//    @Override
//    public void deleteAllById(Iterable<? extends String> ids) {
//        for (String id : ids) store.remove(id);
//    }

//    @Override
//    public void deleteAll(Iterable<? extends Task> entities) {
//        for (Task t : entities) store.remove(t.getId());
//    }

//    @Override
//    public void deleteAll() {
//        store.clear();
//    }
}
