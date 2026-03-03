package com.rev.app.config;

import com.rev.app.entity.Employee;
import com.rev.app.entity.PerformanceReview;
import com.rev.app.repository.EmployeeRepository;
import com.rev.app.repository.PerformanceReviewRepository;
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
@Order(6)
public class PerformanceReviewDataInitializer implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(PerformanceReviewDataInitializer.class);

    @Autowired
    private PerformanceReviewRepository performanceReviewRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public void run(String... args) {
        try {
            int currentYear = Year.now().getValue();
            String reviewPeriod = "Q1 " + currentYear;

            seedReview(
                    4L,
                    reviewPeriod,
                    "Completed all sprint goals and improved test coverage.",
                    "Implemented reusable modules and reduced production bugs.",
                    "Balancing feature delivery with documentation was difficult.",
                    4,
                    "SUBMITTED",
                    null,
                    null,
                    LocalDate.of(currentYear, 3, 15),
                    null);
            seedReview(
                    6L,
                    reviewPeriod,
                    "Delivered APIs on schedule and supported production fixes.",
                    "Improved API latency and supported critical hotfixes quickly.",
                    "Need to improve stakeholder update frequency.",
                    3,
                    "REVIEWED",
                    4,
                    "Consistent delivery and good ownership. Improve proactive communication and mentoring.",
                    LocalDate.of(currentYear, 3, 18),
                    LocalDate.of(currentYear, 3, 22));
        } catch (Exception e) {
            LOGGER.error("PERFORMANCE REVIEW SEED ERROR: {}", e.getMessage(), e);
        }
    }

    private void seedReview(Long employeeId,
            String reviewPeriod,
            String selfAssessment,
            String achievements,
            String challenges,
            Integer selfRating,
            String status,
            Integer managerRating,
            String managerFeedback,
            LocalDate submittedOn) {
        seedReview(employeeId, reviewPeriod, selfAssessment, achievements, challenges, selfRating, status, managerRating,
                managerFeedback,
                submittedOn, null);
    }

    private void seedReview(Long employeeId,
            String reviewPeriod,
            String selfAssessment,
            String achievements,
            String challenges,
            Integer selfRating,
            String status,
            Integer managerRating,
            String managerFeedback,
            LocalDate submittedOn,
            LocalDate reviewedOn) {
        Optional<Employee> employeeOpt = employeeRepository.findById(employeeId);
        if (employeeOpt.isEmpty()) {
            return;
        }

        Employee employee = employeeOpt.get();
        if (employee.getManager() == null || employee.getManager().getId() == null) {
            return;
        }

        Optional<PerformanceReview> existing = performanceReviewRepository
                .findByEmployee_IdAndReviewPeriodIgnoreCase(employee.getId(), reviewPeriod);
        if (existing.isPresent()) {
            return;
        }

        PerformanceReview review = new PerformanceReview();
        review.setEmployee(employee);
        review.setManager(employee.getManager());
        review.setReviewPeriod(reviewPeriod);
        review.setSelfAssessment(selfAssessment);
        review.setAchievements(achievements);
        review.setChallenges(challenges);
        review.setSelfRating(selfRating);
        review.setManagerRating(managerRating);
        review.setStatus(status);
        review.setManagerFeedback(managerFeedback);
        review.setSubmittedOn(submittedOn);
        review.setReviewedOn(reviewedOn);
        performanceReviewRepository.save(review);
    }
}
