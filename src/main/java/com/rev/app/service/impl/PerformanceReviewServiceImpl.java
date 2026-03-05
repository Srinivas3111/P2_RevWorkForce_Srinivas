package com.rev.app.service.impl;

import com.rev.app.service.*;

import com.rev.app.dto.PerformanceReviewDTO;
import com.rev.app.entity.Employee;
import com.rev.app.entity.PerformanceReview;
import com.rev.app.mapper.PerformanceReviewMapper;
import com.rev.app.repository.PerformanceReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PerformanceReviewServiceImpl implements PerformanceReviewService {
    private static final int MIN_REVIEW_TEXT_LENGTH = 20;

    @Autowired
    private PerformanceReviewRepository performanceReviewRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private PerformanceReviewMapper performanceReviewMapper;

    @Autowired
    private EmployeeNotificationService notificationService;

    @Override
    public PerformanceReviewDTO saveEmployeeReviewDraft(Long employeeId,
            String reviewPeriod,
            String selfAssessment,
            String achievements,
            String challenges,
            Integer selfRating) throws Exception {
        Employee employee = validateEmployeeForReview(employeeId);

        String cleanReviewPeriod = normalizeRequired(reviewPeriod, 40, "Review period is required.");
        String cleanSelfAssessment = normalizeRequiredWithMin(selfAssessment, 3000, "Accomplishments are required.");
        String cleanDeliverables = normalizeRequiredWithMin(achievements, 2000, "Key deliverables are required.");
        String cleanImprovementAreas = normalizeRequiredWithMin(challenges, 2000, "Areas of improvement are required.");
        Integer cleanSelfRating = normalizeSelfRating(selfRating);

        Optional<PerformanceReview> existingOpt = performanceReviewRepository
                .findByEmployee_IdAndReviewPeriodIgnoreCase(employeeId, cleanReviewPeriod);

        PerformanceReview review = existingOpt.orElseGet(PerformanceReview::new);
        if (review.getId() != null) {
            String currentStatus = normalizeStatus(review.getStatus());
            if ("SUBMITTED".equals(currentStatus) || "REVIEWED".equals(currentStatus)) {
                throw new Exception("This review period is already submitted and cannot be edited by employee.");
            }
        } else {
            review.setEmployee(employee);
            review.setManager(employee.getManager());
        }

        review.setReviewPeriod(cleanReviewPeriod);
        review.setSelfAssessment(cleanSelfAssessment);
        review.setAchievements(cleanDeliverables);
        review.setChallenges(cleanImprovementAreas);
        review.setSelfRating(cleanSelfRating);
        review.setStatus("DRAFT");
        review.setSubmittedOn(LocalDate.now());
        review.setManagerFeedback(null);
        review.setManagerRating(null);
        review.setReviewedOn(null);

        PerformanceReview saved = performanceReviewRepository.save(review);
        return performanceReviewMapper.toDTO(saved);
    }

    @Override
    public PerformanceReviewDTO submitEmployeeReview(Long employeeId,
            String reviewPeriod,
            String selfAssessment,
            String achievements,
            String challenges,
            Integer selfRating) throws Exception {
        Employee employee = validateEmployeeForReview(employeeId);

        String cleanReviewPeriod = normalizeRequired(reviewPeriod, 40, "Review period is required.");
        String cleanSelfAssessment = normalizeRequiredWithMin(selfAssessment, 3000, "Accomplishments are required.");
        String cleanDeliverables = normalizeRequiredWithMin(achievements, 2000, "Key deliverables are required.");
        String cleanImprovementAreas = normalizeRequiredWithMin(challenges, 2000, "Areas of improvement are required.");
        Integer cleanSelfRating = normalizeSelfRating(selfRating);

        PerformanceReview review = performanceReviewRepository
                .findByEmployee_IdAndReviewPeriodIgnoreCase(employeeId, cleanReviewPeriod)
                .orElseThrow(() -> new Exception("Draft review not found for this review period."));

        String currentStatus = normalizeStatus(review.getStatus());
        if ("SUBMITTED".equals(currentStatus) || "REVIEWED".equals(currentStatus)) {
            throw new Exception("This review has already been submitted and cannot be submitted again.");
        }
        if (!"DRAFT".equals(currentStatus)) {
            throw new Exception("Review must be in DRAFT status before submission.");
        }

        review.setEmployee(employee);
        review.setManager(employee.getManager());
        review.setReviewPeriod(cleanReviewPeriod);
        review.setSelfAssessment(cleanSelfAssessment);
        review.setAchievements(cleanDeliverables);
        review.setChallenges(cleanImprovementAreas);
        review.setSelfRating(cleanSelfRating);
        review.setStatus("SUBMITTED");
        review.setSubmittedOn(LocalDate.now());
        review.setManagerFeedback(null);
        review.setManagerRating(null);
        review.setReviewedOn(null);

        PerformanceReview saved = performanceReviewRepository.save(review);

        // Notify manager
        if (employee.getManager() != null) {
            notificationService.createNotification(
                    employee.getManager(),
                    "Performance Review Submitted",
                    "Action: Performance Review Submitted | Employee: " + employee.getName() + " | Review Period: "
                            + review.getReviewPeriod());
        }

        return performanceReviewMapper.toDTO(saved);
    }

    @Override
    public PerformanceReviewDTO getEmployeeReviewForPeriod(Long employeeId, String reviewPeriod) {
        PerformanceReview review = performanceReviewRepository
                .findByEmployee_IdAndReviewPeriodIgnoreCase(employeeId, reviewPeriod)
                .orElse(null);
        return performanceReviewMapper.toDTO(review);
    }

    @Override
    public List<PerformanceReviewDTO> getEmployeePerformanceReviews(Long employeeId) {
        if (employeeId == null) {
            return List.of();
        }
        return performanceReviewRepository.findByEmployee_IdOrderBySubmittedOnDesc(employeeId)
                .stream()
                .map(performanceReviewMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PerformanceReviewDTO getReviewForEmployee(Long employeeId, Long reviewId) throws Exception {
        if (employeeId == null) {
            throw new Exception("Employee identity is required.");
        }
        if (reviewId == null) {
            throw new Exception("Review ID is required.");
        }
        PerformanceReview review = performanceReviewRepository.findById(reviewId)
                .orElseThrow(() -> new Exception("Performance review not found."));
        if (!review.getEmployee().getId().equals(employeeId)) {
            throw new Exception("Access Denied: You cannot view this review.");
        }
        return performanceReviewMapper.toDTO(review);
    }

    @Override
    public List<PerformanceReviewDTO> getManagerTeamPerformanceReviews(Long managerId) {
        if (managerId == null) {
            return List.of();
        }
        return performanceReviewRepository.findByManager_IdAndStatusNotIgnoreCaseOrderBySubmittedOnDesc(managerId,
                "DRAFT")
                .stream()
                .map(performanceReviewMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PerformanceReviewDTO getReviewForManager(Long managerId, Long reviewId) throws Exception {
        if (managerId == null) {
            throw new Exception("Manager identity is required.");
        }
        if (reviewId == null) {
            throw new Exception("Review ID is required.");
        }
        PerformanceReview review = performanceReviewRepository.findById(reviewId)
                .orElseThrow(() -> new Exception("Performance review not found."));
        if (!review.getManager().getId().equals(managerId)) {
            throw new Exception("Access Denied: You cannot view this review.");
        }
        if ("DRAFT".equals(normalizeStatus(review.getStatus()))) {
            throw new Exception("This review is still draft and not yet submitted to manager.");
        }
        return performanceReviewMapper.toDTO(review);
    }

    @Override
    public PerformanceReviewDTO submitManagerFeedback(Long managerId,
            Long reviewId,
            String managerFeedback,
            Integer managerRating) throws Exception {
        PerformanceReview review = performanceReviewRepository.findById(reviewId)
                .orElseThrow(() -> new Exception("Performance review not found."));

        if (!review.getManager().getId().equals(managerId)) {
            throw new Exception("Access Denied: You cannot view this review.");
        }
        if ("DRAFT".equals(normalizeStatus(review.getStatus()))) {
            throw new Exception("This review is still draft and not yet submitted to manager.");
        }

        String currentStatus = review.getStatus() == null ? "" : review.getStatus().trim();

        if ("REVIEWED".equalsIgnoreCase(currentStatus)) {
            throw new Exception("This review has already been submitted and is locked.");
        }
        if (!currentStatus.isEmpty() && !"SUBMITTED".equalsIgnoreCase(currentStatus)) {
            throw new Exception("Only submitted reviews can be finalized by manager.");
        }

        if (managerRating == null) {
            throw new Exception("Manager rating is required.");
        }
        if (managerRating < 1 || managerRating > 5) {
            throw new Exception("Manager rating must be between 1 and 5.");
        }

        String cleanFeedback = (managerFeedback == null) ? "" : managerFeedback.trim();
        if (cleanFeedback.isEmpty()) {
            throw new Exception("Detailed feedback is required.");
        }
        if (cleanFeedback.length() > 4000) {
            cleanFeedback = cleanFeedback.substring(0, 4000);
        }

        review.setManagerRating(managerRating);
        review.setManagerFeedback(cleanFeedback);
        review.setStatus("REVIEWED");
        if (review.getReviewedOn() == null) {
            review.setReviewedOn(LocalDate.now());
        }
        PerformanceReview saved = performanceReviewRepository.save(review);

        // Notify employee
        notificationService.createNotification(
                review.getEmployee(),
                "Performance Review Finalized",
                "Action: Performance Review Feedback Added | Employee: " + review.getEmployee().getName()
                        + " | Review Period: " + review.getReviewPeriod()
                        + " | Status: Reviewed");

        return performanceReviewMapper.toDTO(saved);
    }

    private Employee validateEmployeeForReview(Long employeeId) throws Exception {
        if (employeeId == null) {
            throw new Exception("Employee identity is required.");
        }

        Employee employee = employeeService.getEmployeeById(employeeId);
        if (employee == null || !employee.isActive()) {
            throw new Exception("Employee account is not valid.");
        }
        if (employee.getManager() == null || employee.getManager().getId() == null) {
            throw new Exception("Reporting manager is required before creating review.");
        }
        return employee;
    }

    private String normalizeRequired(String value, int maxLength, String errorMessage) throws Exception {
        if (value == null || value.trim().isEmpty()) {
            throw new Exception(errorMessage);
        }
        String clean = value.trim();
        if (clean.length() <= maxLength) {
            return clean;
        }
        return clean.substring(0, maxLength);
    }

    private String normalizeRequiredWithMin(String value, int maxLength, String requiredMessage) throws Exception {
        String clean = normalizeRequired(value, maxLength, requiredMessage);
        if (clean.length() < MIN_REVIEW_TEXT_LENGTH) {
            throw new Exception("Each review text field must be at least " + MIN_REVIEW_TEXT_LENGTH + " characters.");
        }
        return clean;
    }

    private Integer normalizeSelfRating(Integer selfRating) throws Exception {
        if (selfRating == null) {
            throw new Exception("Self assessment rating is required.");
        }
        if (selfRating < 1 || selfRating > 5) {
            throw new Exception("Self assessment rating must be between 1 and 5.");
        }
        return selfRating;
    }

    private String normalizeStatus(String status) {
        if (status == null) {
            return "";
        }
        return status.trim().toUpperCase();
    }
}
