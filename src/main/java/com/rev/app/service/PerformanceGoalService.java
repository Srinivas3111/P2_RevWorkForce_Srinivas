package com.rev.app.service;

import com.rev.app.dto.PerformanceGoalDTO;

import java.time.LocalDate;
import java.util.List;

public interface PerformanceGoalService {
    List<PerformanceGoalDTO> getManagerTeamGoals(Long managerId);

    List<PerformanceGoalDTO> getEmployeeGoalsByYear(Long employeeId, int year);

    PerformanceGoalDTO getGoalForManager(Long managerId, Long goalId) throws Exception;

    PerformanceGoalDTO saveManagerComment(Long managerId, Long goalId, String managerComment, Integer finalRating) throws Exception;

    PerformanceGoalDTO createEmployeeGoal(Long employeeId,
                                          String goalDescription,
                                          LocalDate deadline,
                                          String priority) throws Exception;

    PerformanceGoalDTO updateEmployeeGoalProgress(Long employeeId,
                                                  Long goalId,
                                                  Integer completionPercentage,
                                                  String goalStatus) throws Exception;
}
