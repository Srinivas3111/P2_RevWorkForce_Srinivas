package com.rev.app.mapper;

import com.rev.app.dto.PerformanceReviewDTO;
import com.rev.app.entity.PerformanceReview;
import org.springframework.stereotype.Component;

@Component
public class PerformanceReviewMapper {

    public PerformanceReviewDTO toDTO(PerformanceReview entity) {
        if (entity == null) {
            return null;
        }

        PerformanceReviewDTO dto = new PerformanceReviewDTO();
        dto.setId(entity.getId());
        dto.setReviewPeriod(entity.getReviewPeriod());
        dto.setSelfAssessment(entity.getSelfAssessment());
        dto.setAchievements(entity.getAchievements());
        dto.setChallenges(entity.getChallenges());
        dto.setSelfRating(entity.getSelfRating());
        dto.setStatus(entity.getStatus());
        dto.setSubmittedOn(entity.getSubmittedOn());
        dto.setManagerFeedback(entity.getManagerFeedback());
        dto.setManagerRating(entity.getManagerRating());
        dto.setReviewedOn(entity.getReviewedOn());

        if (entity.getEmployee() != null) {
            dto.setEmployeeId(entity.getEmployee().getId());
<<<<<<< HEAD
            dto.setEmployeeName(entity.getEmployee().getName());
=======
            dto.setEmployeeName(entity.getEmployee().getFirstName() + " " + entity.getEmployee().getLastName());
>>>>>>> b09ad693854b4496e321429ab9250ea0c6c408cf
        }

        if (entity.getManager() != null) {
            dto.setManagerId(entity.getManager().getId());
<<<<<<< HEAD
            dto.setManagerName(entity.getManager().getName());
=======
            dto.setManagerName(entity.getManager().getFirstName() + " " + entity.getManager().getLastName());
>>>>>>> b09ad693854b4496e321429ab9250ea0c6c408cf
        }

        return dto;
    }

    public PerformanceReview toEntity(PerformanceReviewDTO dto) {
        if (dto == null) {
            return null;
        }

        PerformanceReview entity = new PerformanceReview();
        entity.setId(dto.getId());
        entity.setReviewPeriod(dto.getReviewPeriod());
        entity.setSelfAssessment(dto.getSelfAssessment());
        entity.setAchievements(dto.getAchievements());
        entity.setChallenges(dto.getChallenges());
        entity.setSelfRating(dto.getSelfRating());
        entity.setStatus(dto.getStatus());
        entity.setSubmittedOn(dto.getSubmittedOn());
        entity.setManagerFeedback(dto.getManagerFeedback());
        entity.setManagerRating(dto.getManagerRating());
        entity.setReviewedOn(dto.getReviewedOn());

        return entity;
    }
}
