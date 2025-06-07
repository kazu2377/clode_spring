package com.example.progressapp.dto;

import java.time.YearMonth;

public class MonthlyTimeStats {
    private YearMonth yearMonth;
    private Double budgetHours;
    private Double actualHours;
    private Double remainingHours;
    private Double dailyAverage;
    private Double efficiency;
    private Integer taskCount;
    private Integer completedTasks;
    private Integer overdueTasks;
    
    public MonthlyTimeStats() {
    }
    
    public MonthlyTimeStats(YearMonth yearMonth, Double budgetHours, Double actualHours) {
        this.yearMonth = yearMonth;
        this.budgetHours = budgetHours;
        this.actualHours = actualHours;
        this.remainingHours = Math.max(0, budgetHours - actualHours);
        this.efficiency = actualHours > 0 ? (budgetHours / actualHours) * 100 : 0.0;
    }
    
    public YearMonth getYearMonth() {
        return yearMonth;
    }
    
    public void setYearMonth(YearMonth yearMonth) {
        this.yearMonth = yearMonth;
    }
    
    public Double getBudgetHours() {
        return budgetHours;
    }
    
    public void setBudgetHours(Double budgetHours) {
        this.budgetHours = budgetHours;
    }
    
    public Double getActualHours() {
        return actualHours;
    }
    
    public void setActualHours(Double actualHours) {
        this.actualHours = actualHours;
    }
    
    public Double getRemainingHours() {
        return remainingHours;
    }
    
    public void setRemainingHours(Double remainingHours) {
        this.remainingHours = remainingHours;
    }
    
    public Double getDailyAverage() {
        return dailyAverage;
    }
    
    public void setDailyAverage(Double dailyAverage) {
        this.dailyAverage = dailyAverage;
    }
    
    public Double getEfficiency() {
        return efficiency;
    }
    
    public void setEfficiency(Double efficiency) {
        this.efficiency = efficiency;
    }
    
    public Integer getTaskCount() {
        return taskCount;
    }
    
    public void setTaskCount(Integer taskCount) {
        this.taskCount = taskCount;
    }
    
    public Integer getCompletedTasks() {
        return completedTasks;
    }
    
    public void setCompletedTasks(Integer completedTasks) {
        this.completedTasks = completedTasks;
    }
    
    public Integer getOverdueTasks() {
        return overdueTasks;
    }
    
    public void setOverdueTasks(Integer overdueTasks) {
        this.overdueTasks = overdueTasks;
    }
    
    public Double getBudgetUtilization() {
        if (budgetHours != null && budgetHours > 0 && actualHours != null) {
            return (actualHours / budgetHours) * 100;
        }
        return 0.0;
    }
    
    public boolean isBudgetExceeded() {
        return actualHours != null && budgetHours != null && actualHours > budgetHours;
    }
}