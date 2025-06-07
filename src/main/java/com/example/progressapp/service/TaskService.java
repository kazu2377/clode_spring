package com.example.progressapp.service;

import com.example.progressapp.entity.Task;
import com.example.progressapp.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    
    @Autowired
    private TaskRepository taskRepository;
    
    public List<Task> getAllTasks() {
        return taskRepository.findByOrderByPriorityDescCreatedAtDesc();
    }
    
    public List<Task> getActiveTasks() {
        return taskRepository.findActiveTasks();
    }
    
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }
    
    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }
    
    public Task createTask(String title, String description, Integer priority) {
        Task task = new Task(title, description);
        if (priority != null) {
            task.setPriority(priority);
        }
        return taskRepository.save(task);
    }
    
    public Task updateTask(Long id, Task updatedTask) {
        Optional<Task> existingTask = taskRepository.findById(id);
        if (existingTask.isPresent()) {
            Task task = existingTask.get();
            task.setTitle(updatedTask.getTitle());
            task.setDescription(updatedTask.getDescription());
            task.setStatus(updatedTask.getStatus());
            task.setPriority(updatedTask.getPriority());
            task.setProgress(updatedTask.getProgress());
            task.setDueDate(updatedTask.getDueDate());
            task.setStartDate(updatedTask.getStartDate());
            task.setEndDate(updatedTask.getEndDate());
            task.setEstimatedHours(updatedTask.getEstimatedHours());
            task.setActualHours(updatedTask.getActualHours());
            task.setActualStartTime(updatedTask.getActualStartTime());
            task.setActualEndTime(updatedTask.getActualEndTime());
            return taskRepository.save(task);
        }
        return null;
    }
    
    public boolean deleteTask(Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public Task updateTaskProgress(Long id, Integer progress) {
        Optional<Task> existingTask = taskRepository.findById(id);
        if (existingTask.isPresent()) {
            Task task = existingTask.get();
            task.setProgress(progress);
            if (progress >= 100) {
                task.setStatus(Task.TaskStatus.COMPLETED);
            } else if (progress > 0 && task.getStatus() == Task.TaskStatus.TODO) {
                task.setStatus(Task.TaskStatus.IN_PROGRESS);
            }
            return taskRepository.save(task);
        }
        return null;
    }
    
    public Task updateTaskStatus(Long id, Task.TaskStatus status) {
        Optional<Task> existingTask = taskRepository.findById(id);
        if (existingTask.isPresent()) {
            Task task = existingTask.get();
            task.setStatus(status);
            if (status == Task.TaskStatus.COMPLETED && task.getProgress() < 100) {
                task.setProgress(100);
            }
            return taskRepository.save(task);
        }
        return null;
    }
    
    public List<Task> searchTasks(String keyword) {
        return taskRepository.findByTitleOrDescriptionContaining(keyword);
    }
    
    public List<Task> getTasksByStatus(Task.TaskStatus status) {
        return taskRepository.findByStatus(status);
    }
    
    public long getTaskCountByStatus(Task.TaskStatus status) {
        return taskRepository.countByStatus(status);
    }
    
    public List<Task> getTasksForGanttChart() {
        return taskRepository.findActiveTasks();
    }
}