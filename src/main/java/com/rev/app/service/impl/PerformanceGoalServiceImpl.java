package com.rev.app.service.impl;

import com.rev.app.service.*;

import com.rev.app.dto.PerformanceGoalDTO;
import com.rev.app.entity.Employee;
import com.rev.app.entity.PerformanceGoal;
import com.rev.app.mapper.PerformanceGoalMapper;
import com.rev.app.repository.PerformanceGoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PerformanceGoalServiceImpl implements PerformanceGoalService {

    private static final Set<String> ALLOWED_PRIORITIES = Set.of("HIGH", "MEDIUM", "LOW");
    private static final Set<String> ALLOWED_GOAL_STATUSES = Set.of("NOT_STARTED", "IN_PROGRESS", "COMPLETED");

    @Autowired
    private PerformanceGoalRepository performanceGoalRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private PerformanceGoalMapper performanceGoalMapper;

    @Autowired
    private EmployeeNotificationService notificationService;

    @Override
    public List<PerformanceGoalDTO> getManagerTeamGoals(Long managerId) {
        if (managerId == null) {
            return List.of();
        }
        return performanceGoalRepository.findByManager_IdOrderByTargetDateAscIdAsc(managerId)
                .stream()
                .map(performanceGoalMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PerformanceGoalDTO> getEmployeeGoalsByYear(Long employeeId, int year) {
        if (employeeId == null)
            return List.of();
        return performanceGoalRepository.findByEmployee_IdOrderByCreatedOnDescIdDesc(employeeId)
                .stream()
                .filter(g -> g.getCreatedOn() != null && g.getCreatedOn().getYear() == year)
                .map(performanceGoalMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PerformanceGoalDTO getGoalForManager(Long managerId, Long goalId) throws Exception {
        PerformanceGoal goal = performanceGoalRepository.findByIdAndManager_Id(goalId, managerId)
                .orElseThrow(() -> new Exception("Goal not found for your team."));
        return performanceGoalMapper.toDTO(goal);
    }

    @Override
    public PerformanceGoalDTO saveManagerComment(Long managerId, Long goalId, String managerComment,
            Integer finalRating) throws Exception {
        PerformanceGoal goal = performanceGoalRepository.findByIdAndManager_Id(goalId, managerId)
                .orElseThrow(() -> new Exception("Goal not found for your team."));

        String cleanComment = (managerComment == null) ? "" : managerComment.trim();
        if (cleanComment.isEmpty()) {
            throw new Exception("Manager comment is required.");
        }
        goal.setManagerComment(cleanComment);
        if (finalRating != null && finalRating >= 1 && finalRating <= 5) {
            goal.setFinalRating(finalRating);
        }
        goal.setManagerCommentedOn(LocalDate.now());
        PerformanceGoal saved = performanceGoalRepository.save(goal);

        // Notify employee
        notificationService.createNotification(
                goal.getEmployee(),
                "Manager Feedback on Goal",
                "Action: Goal Commented | Employee: " + goal.getEmployee().getName() + " | Goal: "
                        + goal.getGoalTitle());

        return performanceGoalMapper.toDTO(saved);
    }

    @Override
    public PerformanceGoalDTO createEmployeeGoal(Long employeeId,
            String goalDescription,
            LocalDate deadline,
            String priority) throws Exception {
        Employee employee = employeeService.getEmployeeById(employeeId);
        if (employee == null || !employee.isActive()) {
            throw new Exception("Employee account is not valid.");
        }

        String cleanDescription = normalizeRequired(goalDescription, 2000, "Goal description is required.");
        String cleanPriority = normalizeRequired(priority, 10, "Priority is required.").toUpperCase();

        PerformanceGoal goal = new PerformanceGoal();
        goal.setEmployee(employee);
        goal.setManager(employee.getManager());
        goal.setGoalDescription(cleanDescription);
        goal.setGoalTitle(
                cleanDescription.length() > 50 ? cleanDescription.substring(0, 47) + "..." : cleanDescription);
        goal.setTargetDate(deadline);
        goal.setGoalPriority(cleanPriority);
        goal.setGoalStatus("NOT_STARTED");
        goal.setCompletionPercentage(0);
        goal.setCreatedOn(LocalDate.now());

        PerformanceGoal saved = performanceGoalRepository.save(goal);

        // Notify manager
        if (employee.getManager() != null) {
            notificationService.createNotification(
                    employee.getManager(),
                    "New Performance Goal Created",
                    "Action: Goal Submitted | Employee: " + employee.getName()
                            + " | Goal: " + goal.getGoalTitle());
        }

        return performanceGoalMapper.toDTO(saved);
    }

    @Override
    public PerformanceGoalDTO updateEmployeeGoalProgress(Long employeeId,
            Long goalId,
            Integer completionPercentage,
            String goalStatus) throws Exception {
        PerformanceGoal goal = performanceGoalRepository.findByIdAndEmployee_Id(goalId, employeeId)
                .orElseThrow(() -> new Exception("Goal not found."));

        goal.setCompletionPercentage(completionPercentage);
        if (completionPercentage == 100) {
            goal.setGoalStatus("COMPLETED");
        } else if (goalStatus != null) {
            goal.setGoalStatus(goalStatus);
        }

        PerformanceGoal saved = performanceGoalRepository.save(goal);

        if (saved.getManager() != null) {
            notificationService.createNotification(
                    saved.getManager(),
                    "Performance Goal Updated",
                    "Action: Goal Updated | Employee: " + saved.getEmployee().getName() + " | Goal: "
                            + saved.getGoalTitle() + " | Progress: "
                            + (saved.getCompletionPercentage() == null ? 0 : saved.getCompletionPercentage()) + "%");
        }

        return performanceGoalMapper.toDTO(saved);
    }

    private String normalizeRequired(String value, int maxLength, String errorMessage) throws Exception {
        if (value == null || value.trim().isEmpty()) {
            throw new Exception(errorMessage);
        }
        String clean = value.trim();
        return clean.length() <= maxLength ? clean : clean.substring(0, maxLength);
    }
}
