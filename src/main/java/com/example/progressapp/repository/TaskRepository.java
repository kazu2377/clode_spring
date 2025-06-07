package com.example.progressapp.repository;

import com.example.progressapp.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    List<Task> findByStatus(Task.TaskStatus status);
    
    List<Task> findByOrderByPriorityDescCreatedAtDesc();
    
    @Query("SELECT t FROM Task t WHERE t.status != 'CANCELLED' ORDER BY t.priority DESC, t.createdAt DESC")
    List<Task> findActiveTasks();
    
    @Query("SELECT t FROM Task t WHERE t.title LIKE %?1% OR t.description LIKE %?1%")
    List<Task> findByTitleOrDescriptionContaining(String keyword);
    
    long countByStatus(Task.TaskStatus status);
}