package com.rev.app.controller;

import com.rev.app.dto.EmployeeDTO;
import com.rev.app.entity.Employee;
import com.rev.app.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;

@Controller
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private LeaveManagementService leaveManagementService;

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private CompanyHolidayService companyHolidayService;

    @Autowired
    private PerformanceReviewService performanceReviewService;

    @Autowired
    private PerformanceGoalService performanceGoalService;

    @Autowired
    private EmployeeNotificationService notificationService;

    @Autowired
    private SystemActivityLogService systemActivityLogService;

    private EmployeeDTO getLoggedInUser(HttpSession session) {
        Object user = session.getAttribute("loggedInUser");
        if (user instanceof EmployeeDTO) {
            return (EmployeeDTO) user;
        }
        return null;
    }

    private boolean isEmployee(HttpSession session) {
        EmployeeDTO user = getLoggedInUser(session);
        return user != null && user.isActive();
    }

    @GetMapping("/dashboard")
    public String employeeDashboard(HttpSession session, Model model) {
        if (!isEmployee(session))
            return "redirect:/";
        EmployeeDTO user = getLoggedInUser(session);
        int currentYear = Year.now().getValue();

        model.addAttribute("user", user);
        var notifs = notificationService.getUnreadNotifications(user.getId());
        model.addAttribute("notifications", notifs);
        model.addAttribute("unreadNotificationCount", notifs.size());
        model.addAttribute("announcements", announcementService.getActiveAnnouncements());

        // Dynamic Leave Balance for Dashboard
        var balances = leaveManagementService.getEmployeeLeaveBalanceByYear(user.getId(), currentYear);
        int usedLeaves = balances.stream().mapToInt(b -> b.getUsedDays() != null ? b.getUsedDays() : 0).sum();
        int remainingLeaves = balances.stream().mapToInt(b -> b.getBalanceDays() != null ? b.getBalanceDays() : 0)
                .sum();
        long activeGoalsCount = performanceGoalService.getEmployeeGoalsByYear(user.getId(), currentYear)
                .stream()
                .filter(g -> g.getGoalStatus() == null || !"COMPLETED".equalsIgnoreCase(g.getGoalStatus()))
                .count();

        model.addAttribute("remainingLeaves", remainingLeaves);
        model.addAttribute("usedLeaves", usedLeaves);
        model.addAttribute("activeGoalsCount", activeGoalsCount);

        return "employee_dashboard";
    }

    @GetMapping("/profile")
    public String employeeProfile(HttpSession session, Model model) {
        if (!isEmployee(session))
            return "redirect:/";

        EmployeeDTO sessionUser = getLoggedInUser(session);
        // Explicitly fetch fresh employee details including manager relationship
        EmployeeDTO user = employeeService.getEmployeeDTOById(sessionUser.getId());

        if (user != null) {
            model.addAttribute("user", user);
            model.addAttribute("employee", user);

            // Resolve and fetch manager details if a manager is assigned
            if (user.getManagerId() != null) {
                EmployeeDTO manager = employeeService.getEmployeeDTOById(user.getManagerId());
                model.addAttribute("manager", manager);
            }
        }

        return "employee_profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam String phoneNumber,
            @RequestParam(required = false) String emergencyContactNumber,
            @RequestParam(required = false) String address,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (!isEmployee(session))
            return "redirect:/";
        try {
            employeeService.updateOwnContactDetails(getLoggedInUser(session).getId(),
                    phoneNumber, emergencyContactNumber, address);
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/employee/profile";
    }

    @GetMapping("/directory")
    public String employeeDirectory(@RequestParam(required = false) String q, HttpSession session, Model model) {
        if (!isEmployee(session))
            return "redirect:/";
        model.addAttribute("user", getLoggedInUser(session));
        List<EmployeeDTO> employees = employeeService.searchDirectoryEmployees(q);
        model.addAttribute("employees", employees);
        model.addAttribute("searchQuery", q);
        return "employee_directory";
    }

    @GetMapping("/leave/apply")
    public String showApplyLeave(HttpSession session, Model model) {
        if (!isEmployee(session))
            return "redirect:/";
        EmployeeDTO user = getLoggedInUser(session);
        model.addAttribute("user", user);
        model.addAttribute("leaveTypes", leaveManagementService.getActiveLeaveTypes());
        model.addAttribute("leaveBalances",
                leaveManagementService.getEmployeeLeaveBalanceByYear(user.getId(), Year.now().getValue()));
        model.addAttribute("leaveApplications",
                leaveManagementService.getEmployeeLeaveApplications(user.getId()));
        model.addAttribute("today", LocalDate.now());
        return "employee_leave_apply";
    }

    @PostMapping("/leave/apply")
    public String applyLeave(@RequestParam Long leaveTypeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam String reason,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (!isEmployee(session))
            return "redirect:/";
        try {
            EmployeeDTO user = getLoggedInUser(session);
            var submittedLeave = leaveManagementService.submitLeaveRequest(user.getId(), leaveTypeId, startDate, endDate,
                    reason);
            String dateRange = startDate + (startDate.equals(endDate) ? "" : " to " + endDate);
            String leaveTypeName = submittedLeave.getLeaveTypeName() != null ? submittedLeave.getLeaveTypeName()
                    : ("Leave Type ID " + leaveTypeId);
            String details = "Leave request ID " + submittedLeave.getId()
                    + " submitted for " + leaveTypeName + " (" + dateRange + ").";
            recordEmployeeActivity(
                    user,
                    "Leave Management",
                    "Employee Applied Leave",
                    details,
                    submittedLeave.getEmployeeId(),
                    submittedLeave.getEmployeeName());
            redirectAttributes.addFlashAttribute("success", "Leave application submitted!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/employee/leave/apply";
    }

    @PostMapping("/leave/{leaveId}/cancel")
    public String cancelLeave(@PathVariable Long leaveId, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isEmployee(session))
            return "redirect:/";
        try {
            leaveManagementService.cancelEmployeeLeaveRequest(getLoggedInUser(session).getId(), leaveId);
            redirectAttributes.addFlashAttribute("success", "Leave request cancelled!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/employee/leave/apply";
    }

    @GetMapping("/leave/balance")
    public String showLeaveBalance(@RequestParam(required = false) Integer year, HttpSession session, Model model) {
        if (!isEmployee(session))
            return "redirect:/";
        EmployeeDTO user = getLoggedInUser(session);
        int selectedYear = (year == null) ? Year.now().getValue() : year;

        var balances = leaveManagementService.getEmployeeLeaveBalanceByYear(user.getId(), selectedYear);
        int totalLeaves = balances.stream().mapToInt(b -> b.getQuotaDays() != null ? b.getQuotaDays() : 0).sum();
        int usedLeaves = balances.stream().mapToInt(b -> b.getUsedDays() != null ? b.getUsedDays() : 0).sum();
        int remainingLeaves = balances.stream().mapToInt(b -> b.getBalanceDays() != null ? b.getBalanceDays() : 0)
                .sum();

        model.addAttribute("user", user);
        model.addAttribute("selectedYear", selectedYear);
        model.addAttribute("totalLeaves", totalLeaves);
        model.addAttribute("usedLeaves", usedLeaves);
        model.addAttribute("remainingLeaves", remainingLeaves);
        model.addAttribute("leaveBalances", balances);

        return "employee_leave_balance";
    }

    @GetMapping("/holiday-calendar")
    public String showHolidayCalendar(@RequestParam(required = false) Integer year, HttpSession session, Model model) {
        if (!isEmployee(session))
            return "redirect:/";
        int selectedYear = (year == null) ? Year.now().getValue() : year;

        model.addAttribute("user", getLoggedInUser(session));
        model.addAttribute("selectedYear", selectedYear);
        model.addAttribute("holidays", companyHolidayService.getHolidaysByYear(selectedYear));
        return "employee_holiday_calendar";
    }

    @GetMapping("/announcements")
    public String showAnnouncements(HttpSession session, Model model) {
        if (!isEmployee(session))
            return "redirect:/";

        var announcements = announcementService.getActiveAnnouncements();
        model.addAttribute("user", getLoggedInUser(session));
        model.addAttribute("announcements", announcements);
        model.addAttribute("totalAnnouncements", announcements.size());
        return "employee_announcements";
    }

    @GetMapping("/performance-reviews")
    public String employeeReviews(HttpSession session, Model model) {
        if (!isEmployee(session))
            return "redirect:/";
        EmployeeDTO user = getLoggedInUser(session);
        var reviews = performanceReviewService.getEmployeePerformanceReviews(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("reviews", reviews);
        model.addAttribute("performanceReviews", reviews);
        model.addAttribute("totalReviews", reviews.size());
        model.addAttribute("draftCount", reviews.stream().filter(r -> "DRAFT".equalsIgnoreCase(r.getStatus())).count());
        model.addAttribute("submittedCount",
                reviews.stream().filter(r -> "SUBMITTED".equalsIgnoreCase(r.getStatus())).count());
        model.addAttribute("reviewedCount",
                reviews.stream().filter(r -> "REVIEWED".equalsIgnoreCase(r.getStatus())).count());

        return "employee_performance_reviews";
    }

    @GetMapping("/performance-review/submit")
    public String submitReviewForm(@RequestParam(required = false) String reviewPeriod, HttpSession session, Model model) {
        if (!isEmployee(session))
            return "redirect:/";

        EmployeeDTO user = getLoggedInUser(session);
        String selectedReviewPeriod = normalizeReviewPeriod(reviewPeriod);
        var existingReview = performanceReviewService.getEmployeeReviewForPeriod(user.getId(), selectedReviewPeriod);
        var reviewHistory = performanceReviewService.getEmployeePerformanceReviews(user.getId());
        boolean reviewLocked = existingReview != null
                && existingReview.getStatus() != null
                && ("SUBMITTED".equalsIgnoreCase(existingReview.getStatus())
                        || "REVIEWED".equalsIgnoreCase(existingReview.getStatus()));

        model.addAttribute("user", user);
        model.addAttribute("selectedReviewPeriod", selectedReviewPeriod);
        model.addAttribute("existingReview", existingReview);
        model.addAttribute("reviewHistory", reviewHistory);
        model.addAttribute("reviewLocked", reviewLocked);
        model.addAttribute("unreadCount", notificationService.getUnreadNotificationCount(user.getId()));

        return "employee_performance_review_submit";
    }

    @PostMapping("/performance-review/submit")
    public String submitReview(@RequestParam String reviewPeriod,
            @RequestParam String selfAssessment,
            @RequestParam String achievements,
            @RequestParam String challenges,
            @RequestParam Integer selfRating,
            @RequestParam(defaultValue = "draft") String action,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (!isEmployee(session))
            return "redirect:/";

        String selectedReviewPeriod = normalizeReviewPeriod(reviewPeriod);

        try {
            if ("submit".equalsIgnoreCase(action)) {
                // Ensure a draft exists for first-time submissions, then submit to manager.
                performanceReviewService.saveEmployeeReviewDraft(getLoggedInUser(session).getId(), selectedReviewPeriod,
                        selfAssessment, achievements, challenges, selfRating);
                performanceReviewService.submitEmployeeReview(getLoggedInUser(session).getId(), selectedReviewPeriod,
                        selfAssessment, achievements, challenges, selfRating);
                redirectAttributes.addFlashAttribute("success", "Performance review submitted to manager.");
            } else {
                performanceReviewService.saveEmployeeReviewDraft(getLoggedInUser(session).getId(), selectedReviewPeriod,
                        selfAssessment, achievements, challenges, selfRating);
                redirectAttributes.addFlashAttribute("success", "Performance review draft saved.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        String encodedPeriod = URLEncoder.encode(selectedReviewPeriod, StandardCharsets.UTF_8);
        return "redirect:/employee/performance-review/submit?reviewPeriod=" + encodedPeriod;
    }

    @GetMapping("/performance-reviews/{id}")
    public String reviewDetailForEmployee(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes ra) {
        if (!isEmployee(session))
            return "redirect:/";

        EmployeeDTO user = getLoggedInUser(session);
        try {
            var review = performanceReviewService.getReviewForEmployee(user.getId(), id);
            boolean feedbackVisible = review != null
                    && review.getStatus() != null
                    && "REVIEWED".equalsIgnoreCase(review.getStatus());

            model.addAttribute("user", user);
            model.addAttribute("review", review);
            model.addAttribute("feedbackVisible", feedbackVisible);
            return "employee_performance_review_detail";
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/employee/performance-reviews";
        }
    }

    @GetMapping("/goals")
    public String employeeGoals(@RequestParam(required = false) Integer year, HttpSession session, Model model) {
        if (!isEmployee(session))
            return "redirect:/";
        EmployeeDTO user = getLoggedInUser(session);
        int selectedYear = (year == null) ? Year.now().getValue() : year;

        var goals = performanceGoalService.getEmployeeGoalsByYear(user.getId(), selectedYear);
        model.addAttribute("user", user);
        model.addAttribute("goals", goals);
        model.addAttribute("selectedYear", selectedYear);
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("totalGoals", goals.size());
        return "employee_goals";
    }

    @PostMapping("/goals")
    public String createGoal(@RequestParam String goalDescription,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate deadline,
            @RequestParam String priority,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (!isEmployee(session))
            return "redirect:/";
        try {
            performanceGoalService.createEmployeeGoal(getLoggedInUser(session).getId(), goalDescription, deadline,
                    priority);
            redirectAttributes.addFlashAttribute("success", "Goal created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/employee/goals";
    }

    @PostMapping("/goals/{goalId}/progress")
    public String updateGoalProgress(@PathVariable Long goalId,
            @RequestParam Integer completionPercentage,
            @RequestParam String goalStatus,
            @RequestParam(required = false) Integer year,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (!isEmployee(session))
            return "redirect:/";
        try {
            performanceGoalService.updateEmployeeGoalProgress(getLoggedInUser(session).getId(), goalId,
                    completionPercentage, goalStatus);
            redirectAttributes.addFlashAttribute("success", "Goal progress updated!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/employee/goals" + (year != null ? "?year=" + year : "");
    }

    @PostMapping("/notifications/{id}/read")
    public String markNotificationRead(@PathVariable Long id, HttpSession session) {
        if (!isEmployee(session))
            return "redirect:/";
        EmployeeDTO user = getLoggedInUser(session);
        try {
            notificationService.markNotificationAsRead(user.getId(), id);
        } catch (Exception e) {
            // ignore if notification not found
        }
        return "redirect:/employee/dashboard";
    }

    @PostMapping("/notifications/read-all")
    public String markAllNotificationsRead(HttpSession session) {
        if (!isEmployee(session))
            return "redirect:/";
        notificationService.markAllNotificationsAsRead(getLoggedInUser(session).getId());
        return "redirect:/employee/dashboard";
    }

    private void recordEmployeeActivity(EmployeeDTO actor,
            String moduleName,
            String actionName,
            String details,
            Long targetEmployeeId,
            String targetEmployeeName) {
        try {
            systemActivityLogService.logActivity(
                    actor,
                    moduleName,
                    actionName,
                    details,
                    targetEmployeeId,
                    targetEmployeeName);
        } catch (Exception ignored) {
            // Activity log failures should not break primary user flow.
        }
    }

    private String normalizeReviewPeriod(String reviewPeriod) {
        if (reviewPeriod != null && !reviewPeriod.trim().isEmpty()) {
            return reviewPeriod.trim();
        }
        int quarter = ((LocalDate.now().getMonthValue() - 1) / 3) + 1;
        return "Q" + quarter + " " + Year.now().getValue();
    }
}
