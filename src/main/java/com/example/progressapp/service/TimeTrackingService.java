package com.example.progressapp.service;

import com.example.progressapp.dto.MonthlyTimeStats;
import com.example.progressapp.entity.MonthlyBudget;
import com.example.progressapp.entity.Task;
import com.example.progressapp.repository.MonthlyBudgetRepository;
import com.example.progressapp.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TimeTrackingService {
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private MonthlyBudgetRepository monthlyBudgetRepository;
    
    public MonthlyTimeStats getCurrentMonthStats() {
        YearMonth currentMonth = YearMonth.now();
        return getMonthlyStats(currentMonth);
    }
    
    public MonthlyTimeStats getMonthlyStats(YearMonth yearMonth) {
        LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = yearMonth.plusMonths(1).atDay(1).atStartOfDay();
        
        Optional<MonthlyBudget> budget = monthlyBudgetRepository.findByYearMonth(yearMonth);
        Double budgetHours = budget.map(MonthlyBudget::getBudgetHours).orElse(0.0);
        
        Double actualHours = taskRepository.sumActualHoursBetween(startOfMonth, endOfMonth);
        if (actualHours == null) actualHours = 0.0;
        
        Long taskCount = taskRepository.countTasksBetween(startOfMonth, endOfMonth);
        Long completedTasks = taskRepository.countCompletedTasksBetween(startOfMonth, endOfMonth);
        
        List<Task> overdueTasks = taskRepository.findOverdueTasks(LocalDateTime.now());
        long overdueCount = overdueTasks.stream()
                .filter(task -> {
                    LocalDateTime taskCreated = task.getCreatedAt();
                    return taskCreated.isAfter(startOfMonth) && taskCreated.isBefore(endOfMonth);
                })
                .count();
        
        MonthlyTimeStats stats = new MonthlyTimeStats(yearMonth, budgetHours, actualHours);
        stats.setTaskCount(taskCount.intValue());
        stats.setCompletedTasks(completedTasks.intValue());
        stats.setOverdueTasks((int) overdueCount);
        
        if (budget.isPresent() && budget.get().getWorkingDays() != null && budget.get().getWorkingDays() > 0) {
            stats.setDailyAverage(actualHours / budget.get().getWorkingDays());
        }
        
        return stats;
    }
    
    public List<MonthlyTimeStats> getLastSixMonthsStats() {
        List<MonthlyTimeStats> stats = new ArrayList<>();
        YearMonth currentMonth = YearMonth.now();
        
        for (int i = 5; i >= 0; i--) {
            YearMonth month = currentMonth.minusMonths(i);
            stats.add(getMonthlyStats(month));
        }
        
        return stats;
    }
    
    public List<Task> getTasksWithTimeVariance() {
        return taskRepository.findTasksWithTimeTracking()
                .stream()
                .filter(task -> Math.abs(task.getTimeVariance()) > 0.1)
                .toList();
    }
    
    public Double calculateAverageEfficiency() {
        List<Task> tasksWithTime = taskRepository.findTasksWithTimeTracking();
        if (tasksWithTime.isEmpty()) return 0.0;
        
        double totalEfficiency = tasksWithTime.stream()
                .mapToDouble(Task::getEfficiencyRatio)
                .average()
                .orElse(0.0);
        
        return totalEfficiency;
    }
    
    public MonthlyBudget createOrUpdateMonthlyBudget(YearMonth yearMonth, Double budgetHours, Double dailyHours, Integer workingDays) {
        Optional<MonthlyBudget> existing = monthlyBudgetRepository.findByYearMonth(yearMonth);
        
        MonthlyBudget budget;
        if (existing.isPresent()) {
            budget = existing.get();
            budget.setBudgetHours(budgetHours);
            budget.setDailyHours(dailyHours);
            budget.setWorkingDays(workingDays);
        } else {
            budget = new MonthlyBudget(yearMonth, budgetHours, dailyHours, workingDays);
        }
        
        return monthlyBudgetRepository.save(budget);
    }
    
    public Task startTask(Long taskId) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            task.setActualStartTime(LocalDateTime.now());
            if (task.getStatus() == Task.TaskStatus.TODO) {
                task.setStatus(Task.TaskStatus.IN_PROGRESS);
            }
            return taskRepository.save(task);
        }
        return null;
    }
    
    public Task stopTask(Long taskId) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            if (task.getActualStartTime() != null) {
                task.setActualEndTime(LocalDateTime.now());
                
                long minutes = ChronoUnit.MINUTES.between(task.getActualStartTime(), task.getActualEndTime());
                double hours = minutes / 60.0;
                
                Double currentActual = task.getActualHours() != null ? task.getActualHours() : 0.0;
                task.setActualHours(currentActual + hours);
                
                task.setActualStartTime(null);
                task.setActualEndTime(null);
            }
            return taskRepository.save(task);
        }
        return null;
    }
    
    public Double getTotalRemainingHours(YearMonth yearMonth) {
        LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = yearMonth.plusMonths(1).atDay(1).atStartOfDay();
        
        List<Task> tasks = taskRepository.findTasksBetween(startOfMonth, endOfMonth);
        return tasks.stream()
                .filter(task -> task.getEstimatedHours() != null)
                .mapToDouble(Task::getRemainingHours)
                .sum();
    }
    
    public List<Task> getTopTimeConsumingTasks(int limit) {
        return taskRepository.findTasksWithTimeTracking()
                .stream()
                .filter(task -> task.getActualHours() != null)
                .sorted((t1, t2) -> Double.compare(t2.getActualHours(), t1.getActualHours()))
                .limit(limit)
                .toList();
    }
    
    public List<Task> getOverdueTasks() {
        return taskRepository.findOverdueTasks(LocalDateTime.now());
    }
}