package com.example.progressapp.repository;

import com.example.progressapp.entity.MonthlyBudget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Repository
public interface MonthlyBudgetRepository extends JpaRepository<MonthlyBudget, Long> {
    
    Optional<MonthlyBudget> findByYearMonth(YearMonth yearMonth);
    
    List<MonthlyBudget> findByYearMonthBetweenOrderByYearMonth(YearMonth start, YearMonth end);
    
    @Query("SELECT mb FROM MonthlyBudget mb ORDER BY mb.yearMonth DESC")
    List<MonthlyBudget> findAllOrderByYearMonthDesc();
    
    @Query("SELECT mb FROM MonthlyBudget mb WHERE mb.yearMonth >= ?1 ORDER BY mb.yearMonth")
    List<MonthlyBudget> findFromYearMonth(YearMonth yearMonth);
}