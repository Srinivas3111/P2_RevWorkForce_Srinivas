package com.rev.app.config;

import com.rev.app.entity.Employee;
import com.rev.app.entity.PerformanceGoal;
import com.rev.app.repository.EmployeeRepository;
import com.rev.app.repository.PerformanceGoalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Year;
import java.util.Optional;

@Component
@Order(7)
public class PerformanceGoalDataInitializer implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(PerformanceGoalDataInitializer.class);

    @Autowired
    private PerformanceGoalRepository performanceGoalRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public void run(String... args) {
        try {
            int currentYear = Year.now().getValue();

            seedGoal(
                    4L,
                    "Complete cloud certification",
                    "Complete Oracle cloud certification and apply learnings in project delivery.",
                    LocalDate.of(currentYear, 9, 30),
                    65,
                    "IN_PROGRESS",
                    "Completed foundational modules and scheduled the exam.",
                    "Good progress. Keep a consistent weekly preparation plan.");

            seedGoal(
                    6L,
                    "Deliver Project Phoenix milestone",
                    "Deliver API milestone before quarter end with quality checks.",
                    LocalDate.of(currentYear, 6, 30),
                    15,
                    "NOT_STARTED",
                    "Architecture review completed and implementation plan prepared.",
                    null);

            seedGoal(
                    5L,
                    "Improve Java performance tuning",
                    "Improve profiling skills and optimize SQL-heavy workflows.",
                    LocalDate.of(currentYear, 10, 31),
                    45,
                    "IN_PROGRESS",
                    "Completed two internal workshops and hands-on exercises.",
                    "Focus more on benchmark-driven improvements.");
        } catch (Exception e) {
            LOGGER.error("PERFORMANCE GOAL SEED ERROR: {}", e.getMessage(), e);
        }
    }

    private void seedGoal(Long employeeId,
            String goalTitle,
            String goalDescription,
            LocalDate targetDate,
            Integer completionPercentage,
            String goalStatus,
            String progressNote,
            String managerComment) {
        Optional<Employee> employeeOpt = employeeRepository.findById(employeeId);
        if (employeeOpt.isEmpty()) {
            return;
        }

        Employee employee = employeeOpt.get();
        if (employee.getManager() == null || employee.getManager().getId() == null) {
            return;
        }

        Optional<PerformanceGoal> existing = performanceGoalRepository
                .findByEmployee_IdAndGoalTitleIgnoreCase(employee.getId(), goalTitle);
        if (existing.isPresent()) {
            return;
        }

        PerformanceGoal goal = new PerformanceGoal();
        goal.setEmployee(employee);
        goal.setManager(employee.getManager());
        goal.setGoalTitle(goalTitle);
        goal.setGoalDescription(goalDescription);
        goal.setTargetDate(targetDate);
        goal.setCompletionPercentage(completionPercentage);
        goal.setGoalStatus(goalStatus);
        goal.setProgressNote(progressNote);
        goal.setManagerComment(managerComment);
        if (managerComment != null && !managerComment.isBlank()) {
            goal.setManagerCommentedOn(LocalDate.now());
        }
        performanceGoalRepository.save(goal);
    }
}
