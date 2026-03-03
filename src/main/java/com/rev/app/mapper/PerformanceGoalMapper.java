package com.rev.app.mapper;

import com.rev.app.dto.PerformanceGoalDTO;
import com.rev.app.entity.PerformanceGoal;
import org.springframework.stereotype.Component;

@Component
public class PerformanceGoalMapper {

    public PerformanceGoalDTO toDTO(PerformanceGoal goal) {
        if (goal == null)
            return null;
        PerformanceGoalDTO dto = new PerformanceGoalDTO();
        dto.setId(goal.getId());
        if (goal.getEmployee() != null) {
            dto.setEmployeeId(goal.getEmployee().getId());
            dto.setEmployeeName(goal.getEmployee().getFirstName() + " " + goal.getEmployee());
        }
        if (goal.getManager() != null) {
            dto.setManagerId(goal.getManager().getId());
        }
        dto.setGoalTitle(goal.getGoalTitle());
        dto.setGoalDescription(goal.getGoalDescription());
        dto.setTargetDate(goal.getTargetDate());
        dto.setGoalPriority(goal.getGoalPriority());
        dto.setGoalStatus(goal.getGoalStatus());
        dto.setCompletionPercentage(goal.getCompletionPercentage());
        dto.setProgressNote(goal.getProgressNote());
        dto.setManagerComment(goal.getManagerComment());
        dto.setFinalRating(goal.getFinalRating());
        dto.setManagerCommentedOn(goal.getManagerCommentedOn());
        dto.setCreatedOn(goal.getCreatedOn());
        return dto;
    }

    public PerformanceGoal toEntity(PerformanceGoalDTO dto) {
        if (dto == null)
            return null;
        PerformanceGoal goal = new PerformanceGoal();
        goal.setId(dto.getId());
        goal.setGoalTitle(dto.getGoalTitle());
        goal.setGoalDescription(dto.getGoalDescription());
        goal.setTargetDate(dto.getTargetDate());
        goal.setGoalPriority(dto.getGoalPriority());
        goal.setGoalStatus(dto.getGoalStatus());
        goal.setCompletionPercentage(dto.getCompletionPercentage());
        goal.setProgressNote(dto.getProgressNote());
        goal.setManagerComment(dto.getManagerComment());
        goal.setFinalRating(dto.getFinalRating());
        goal.setManagerCommentedOn(dto.getManagerCommentedOn());
        goal.setCreatedOn(dto.getCreatedOn());
        return goal;
    }
}
