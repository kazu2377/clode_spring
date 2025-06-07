package com.example.progressapp.controller;

import com.example.progressapp.entity.Task;
import com.example.progressapp.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class TaskController {
    
    @Autowired
    private TaskService taskService;
    
    @GetMapping("/")
    public String index(Model model) {
        List<Task> tasks = taskService.getActiveTasks();
        model.addAttribute("tasks", tasks);
        model.addAttribute("newTask", new Task());
        
        long todoCount = taskService.getTaskCountByStatus(Task.TaskStatus.TODO);
        long inProgressCount = taskService.getTaskCountByStatus(Task.TaskStatus.IN_PROGRESS);
        long completedCount = taskService.getTaskCountByStatus(Task.TaskStatus.COMPLETED);
        
        model.addAttribute("todoCount", todoCount);
        model.addAttribute("inProgressCount", inProgressCount);
        model.addAttribute("completedCount", completedCount);
        
        return "index";
    }
    
    @PostMapping("/tasks")
    public String createTask(@ModelAttribute Task task) {
        taskService.saveTask(task);
        return "redirect:/";
    }
    
    @GetMapping("/tasks/{id}/edit")
    public String editTask(@PathVariable Long id, Model model) {
        Task task = taskService.getTaskById(id).orElse(null);
        if (task != null) {
            model.addAttribute("task", task);
            return "edit";
        }
        return "redirect:/";
    }
    
    @PostMapping("/tasks/{id}/update")
    public String updateTask(@PathVariable Long id, @ModelAttribute Task task) {
        taskService.updateTask(id, task);
        return "redirect:/";
    }
    
    @PostMapping("/tasks/{id}/delete")
    public String deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return "redirect:/";
    }
    
    @PostMapping("/tasks/{id}/progress")
    public String updateProgress(@PathVariable Long id, @RequestParam Integer progress) {
        taskService.updateTaskProgress(id, progress);
        return "redirect:/";
    }
    
    @PostMapping("/tasks/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam Task.TaskStatus status) {
        taskService.updateTaskStatus(id, status);
        return "redirect:/";
    }
    
    @GetMapping("/search")
    public String searchTasks(@RequestParam String keyword, Model model) {
        List<Task> tasks = taskService.searchTasks(keyword);
        model.addAttribute("tasks", tasks);
        model.addAttribute("keyword", keyword);
        model.addAttribute("newTask", new Task());
        return "search";
    }
    
    @GetMapping("/gantt")
    public String ganttChart(Model model) {
        List<Task> tasks = taskService.getTasksForGanttChart();
        model.addAttribute("tasks", tasks);
        return "gantt";
    }
}