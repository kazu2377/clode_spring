package com.example.progressapp.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.YearMonth;

@Entity
@Table(name = "monthly_budgets")
public class MonthlyBudget {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "year_month", nullable = false, unique = true)
    private YearMonth yearMonth;
    
    @Column(name = "budget_hours", nullable = false)
    private Double budgetHours;
    
    @Column(name = "daily_hours")
    private Double dailyHours;
    
    @Column(name = "working_days")
    private Integer workingDays;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    public MonthlyBudget() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public MonthlyBudget(YearMonth yearMonth, Double budgetHours, Double dailyHours, Integer workingDays) {
        this();
        this.yearMonth = yearMonth;
        this.budgetHours = budgetHours;
        this.dailyHours = dailyHours;
        this.workingDays = workingDays;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
    
    public Double getDailyHours() {
        return dailyHours;
    }
    
    public void setDailyHours(Double dailyHours) {
        this.dailyHours = dailyHours;
    }
    
    public Integer getWorkingDays() {
        return workingDays;
    }
    
    public void setWorkingDays(Integer workingDays) {
        this.workingDays = workingDays;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}