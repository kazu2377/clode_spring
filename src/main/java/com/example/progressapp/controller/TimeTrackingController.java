package com.example.progressapp.controller;

import com.example.progressapp.dto.MonthlyTimeStats;
import com.example.progressapp.entity.MonthlyBudget;
import com.example.progressapp.entity.Task;
import com.example.progressapp.service.TimeTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@Controller
@RequestMapping("/time")
public class TimeTrackingController {
    
    @Autowired
    private TimeTrackingService timeTrackingService;
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        MonthlyTimeStats currentMonth = timeTrackingService.getCurrentMonthStats();
        List<MonthlyTimeStats> sixMonthsStats = timeTrackingService.getLastSixMonthsStats();
        List<Task> timeVarianceTasks = timeTrackingService.getTasksWithTimeVariance();
        List<Task> topTimeTasks = timeTrackingService.getTopTimeConsumingTasks(10);
        List<Task> overdueTasks = timeTrackingService.getOverdueTasks();
        Double averageEfficiency = timeTrackingService.calculateAverageEfficiency();
        
        model.addAttribute("currentMonthStats", currentMonth);
        model.addAttribute("sixMonthsStats", sixMonthsStats);
        model.addAttribute("timeVarianceTasks", timeVarianceTasks);
        model.addAttribute("topTimeTasks", topTimeTasks);
        model.addAttribute("overdueTasks", overdueTasks);
        model.addAttribute("averageEfficiency", averageEfficiency);
        
        return "time/dashboard";
    }
    
    @GetMapping("/monthly")
    public String monthlyManagement(Model model, @RequestParam(required = false) String yearMonth) {
        YearMonth targetMonth = yearMonth != null ? YearMonth.parse(yearMonth) : YearMonth.now();
        MonthlyTimeStats stats = timeTrackingService.getMonthlyStats(targetMonth);
        Double remainingHours = timeTrackingService.getTotalRemainingHours(targetMonth);
        
        model.addAttribute("targetMonth", targetMonth);
        model.addAttribute("monthlyStats", stats);
        model.addAttribute("remainingHours", remainingHours);
        model.addAttribute("newBudget", new MonthlyBudget());
        
        return "time/monthly";
    }
    
    @PostMapping("/budget")
    public String createBudget(@RequestParam String yearMonth,
                              @RequestParam Double budgetHours,
                              @RequestParam Double dailyHours,
                              @RequestParam Integer workingDays) {
        YearMonth ym = YearMonth.parse(yearMonth);
        timeTrackingService.createOrUpdateMonthlyBudget(ym, budgetHours, dailyHours, workingDays);
        return "redirect:/time/monthly?yearMonth=" + yearMonth;
    }
    
    @GetMapping("/report")
    public String efficiencyReport(Model model) {
        List<MonthlyTimeStats> sixMonthsStats = timeTrackingService.getLastSixMonthsStats();
        List<Task> allTasksWithTime = timeTrackingService.getTasksWithTimeVariance();
        Double averageEfficiency = timeTrackingService.calculateAverageEfficiency();
        
        model.addAttribute("monthlyStats", sixMonthsStats);
        model.addAttribute("tasksWithVariance", allTasksWithTime);
        model.addAttribute("averageEfficiency", averageEfficiency);
        
        return "time/report";
    }
    
    @PostMapping("/start/{taskId}")
    @ResponseBody
    public ResponseEntity<String> startTask(@PathVariable Long taskId) {
        Task task = timeTrackingService.startTask(taskId);
        if (task != null) {
            return ResponseEntity.ok("タスクを開始しました");
        }
        return ResponseEntity.badRequest().body("タスクの開始に失敗しました");
    }
    
    @PostMapping("/stop/{taskId}")
    @ResponseBody
    public ResponseEntity<String> stopTask(@PathVariable Long taskId) {
        Task task = timeTrackingService.stopTask(taskId);
        if (task != null) {
            return ResponseEntity.ok("タスクを停止しました");
        }
        return ResponseEntity.badRequest().body("タスクの停止に失敗しました");
    }
    
    @GetMapping("/api/monthly/{yearMonth}")
    @ResponseBody
    public ResponseEntity<MonthlyTimeStats> getMonthlyStats(@PathVariable String yearMonth) {
        try {
            YearMonth ym = YearMonth.parse(yearMonth);
            MonthlyTimeStats stats = timeTrackingService.getMonthlyStats(ym);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}