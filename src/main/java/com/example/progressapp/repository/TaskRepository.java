package com.example.progressapp.repository;

import com.example.progressapp.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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
    
    @Query("SELECT SUM(t.actualHours) FROM Task t WHERE t.createdAt >= :startDate AND t.createdAt < :endDate")
    Double sumActualHoursBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT SUM(t.estimatedHours) FROM Task t WHERE t.createdAt >= :startDate AND t.createdAt < :endDate")
    Double sumEstimatedHoursBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT t FROM Task t WHERE t.createdAt >= :startDate AND t.createdAt < :endDate ORDER BY t.priority DESC, t.createdAt DESC")
    List<Task> findTasksBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT t FROM Task t WHERE t.estimatedHours IS NOT NULL AND t.actualHours IS NOT NULL ORDER BY t.updatedAt DESC")
    List<Task> findTasksWithTimeTracking();
    
    @Query("SELECT t FROM Task t WHERE t.endDate IS NOT NULL AND t.endDate < :currentDate AND t.status != 'COMPLETED'")
    List<Task> findOverdueTasks(@Param("currentDate") LocalDateTime currentDate);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.createdAt >= :startDate AND t.createdAt < :endDate")
    Long countTasksBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.createdAt >= :startDate AND t.createdAt < :endDate AND t.status = 'COMPLETED'")
    Long countCompletedTasksBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}