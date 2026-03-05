package com.rev.app.service;

import com.rev.app.dto.PerformanceReviewDTO;

import java.util.List;

public interface PerformanceReviewService {
    PerformanceReviewDTO saveEmployeeReviewDraft(Long employeeId,
                                                 String reviewPeriod,
                                                 String selfAssessment,
                                                 String achievements,
                                                 String challenges,
                                                 Integer selfRating) throws Exception;

    PerformanceReviewDTO submitEmployeeReview(Long employeeId,
                                              String reviewPeriod,
                                              String selfAssessment,
                                              String achievements,
                                              String challenges,
                                              Integer selfRating) throws Exception;

    PerformanceReviewDTO getEmployeeReviewForPeriod(Long employeeId, String reviewPeriod);

    List<PerformanceReviewDTO> getEmployeePerformanceReviews(Long employeeId);

    PerformanceReviewDTO getReviewForEmployee(Long employeeId, Long reviewId) throws Exception;

    List<PerformanceReviewDTO> getManagerTeamPerformanceReviews(Long managerId);

    PerformanceReviewDTO getReviewForManager(Long managerId, Long reviewId) throws Exception;

    PerformanceReviewDTO submitManagerFeedback(Long managerId,
                                               Long reviewId,
                                               String managerFeedback,
                                               Integer managerRating) throws Exception;
}
