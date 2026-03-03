package com.rev.app.controller;

import com.rev.app.dto.*;
import com.rev.app.entity.Employee;
import com.rev.app.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Year;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.DayOfWeek;
import java.time.DateTimeException;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/manager")
public class ManagerController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private LeaveManagementService leaveManagementService;

    @Autowired
    private PerformanceReviewService performanceReviewService;

    @Autowired
    private PerformanceGoalService performanceGoalService;

    @Autowired
    private EmployeeNotificationService notificationService;

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private SystemActivityLogService systemActivityLogService;

    private EmployeeDTO getLoggedInUser(HttpSession session) {
        Object user = session.getAttribute("loggedInUser");
        if (user instanceof EmployeeDTO) {
            return (EmployeeDTO) user;
        }
        return null;
    }

    private boolean isManager(HttpSession session) {
        EmployeeDTO user = getLoggedInUser(session);
        return user != null && user.isActive() && "MANAGER".equalsIgnoreCase(user.getRole());
    }

    @GetMapping("/dashboard")
    public String managerDashboard(HttpSession session, Model model) {
        if (!isManager(session))
            return "redirect:/";
        EmployeeDTO user = getLoggedInUser(session);

        List<EmployeeDTO> team = employeeService.getActiveDirectReportees(user.getId());
        model.addAttribute("user", user);
        model.addAttribute("teamSize", team.size());

        var pendingRequests = leaveManagementService.getPendingTeamLeaveApplications(user.getId());
        model.addAttribute("pendingTeamRequests", pendingRequests.size());

        var notifications = notificationService.getRecentNotificationsForEmployee(user.getId());
        model.addAttribute("managerNotifications", notifications);
        model.addAttribute("managerUnreadNotificationCount", notificationService.getUnreadNotificationCount(user.getId()));

        var teamGoals = performanceGoalService.getManagerTeamGoals(user.getId());
        model.addAttribute("teamGoalProgressRows", teamGoals);
        var underperforming = teamGoals.stream()
                .filter(g -> g.getCompletionPercentage() != null && g.getCompletionPercentage() < 40).count();
        var overdue = teamGoals.stream()
                .filter(g -> g.getTargetDate() != null && g.getTargetDate().isBefore(LocalDate.now())
                        && (g.getCompletionPercentage() == null || g.getCompletionPercentage() < 100))
                .count();
        var pendingComment = teamGoals.stream()
                .filter(g -> g.getCompletionPercentage() != null && g.getCompletionPercentage() == 100
                        && (g.getManagerComment() == null || g.getManagerComment().trim().isEmpty()))
                .count();

        model.addAttribute("underperformingGoalCount", underperforming);
        model.addAttribute("overdueGoalCount", overdue);
        model.addAttribute("goalsPendingComment", pendingComment);

        model.addAttribute("announcements", announcementService.getActiveAnnouncements());
        return "manager_dashboard";
    }

    @GetMapping("/profile")
    public String managerProfile(HttpSession session, Model model) {
        if (!isManager(session))
            return "redirect:/";
        EmployeeDTO user = getLoggedInUser(session);
        // Fetch fresh manager data to ensure all fields (address, etc.) are up to date
        EmployeeDTO freshUser = employeeService.getEmployeeDTOById(user.getId());
        // Fetch all direct reportees (active and inactive) for the team members section
        List<EmployeeDTO> teamMembers = employeeService.getAllDirectReportees(user.getId());
        model.addAttribute("user", freshUser);
        model.addAttribute("teamMembers", teamMembers);
        return "manager_profile";
    }

    @PostMapping("/update-profile")
    public String updateManagerProfile(@RequestParam String phoneNumber,
            @RequestParam String address,
            @RequestParam String emergencyContactNumber,
            HttpSession session, RedirectAttributes ra) {
        if (!isManager(session))
            return "redirect:/";
        try {
            EmployeeDTO user = getLoggedInUser(session);
            employeeService.updateOwnContactDetails(user.getId(), phoneNumber, emergencyContactNumber, address);

            // Refresh session user data
            session.setAttribute("loggedInUser", employeeService.getEmployeeDTOById(user.getId()));

            ra.addFlashAttribute("success", "Profile updated successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Failed to update profile: " + e.getMessage());
        }
        return "redirect:/manager/profile";
    }

    @GetMapping("/directory")
    public String managerDirectory(@RequestParam(required = false) String q, HttpSession session, Model model) {
        if (!isManager(session))
            return "redirect:/";
        EmployeeDTO user = getLoggedInUser(session);
        model.addAttribute("user", user);
        List<EmployeeDTO> employees = employeeService.searchManagerDirectory(user.getId(), q);
        model.addAttribute("employees", employees);
        model.addAttribute("searchQuery", q);
        return "manager_directory";
    }

    @GetMapping("/my-team")
    public String managerMyTeam(HttpSession session, Model model) {
        if (!isManager(session))
            return "redirect:/";
        EmployeeDTO user = getLoggedInUser(session);
        List<EmployeeDTO> team = employeeService.getActiveDirectReportees(user.getId());
        model.addAttribute("user", user);
        model.addAttribute("reportees", team);
        model.addAttribute("teamSize", team.size());
        return "manager_my_team";
    }

    @GetMapping("/my-team/{id}")
    public String teamMemberProfile(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes ra) {
        if (!isManager(session))
            return "redirect:/";
        try {
            EmployeeDTO manager = getLoggedInUser(session);
            EmployeeDTO member = employeeService.getEmployeeDTOById(id);
            if (member == null || member.getManagerId() == null || !member.getManagerId().equals(manager.getId())) {
                throw new Exception("Employee not found in your team.");
            }
            model.addAttribute("user", manager);
            model.addAttribute("teamMember", member);
            return "manager_team_member_profile";
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/manager/team-structure";
        }
    }

    @GetMapping("/team-leave")
    public String teamLeaveManagement(@RequestParam(required = false, defaultValue = "requests") String tab,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            HttpSession session, Model model) {
        if (!isManager(session))
            return "redirect:/";

        EmployeeDTO user = getLoggedInUser(session);
        model.addAttribute("user", user);
        model.addAttribute("activeTab", tab);
        model.addAttribute("currentUri", "/manager/team-leave");

        // --- Requests Data ---
        var pending = leaveManagementService.getPendingTeamLeaveApplications(user.getId());
        var totalRequests = leaveManagementService.getTeamLeaveApplications(user.getId());
        model.addAttribute("teamLeaveApplications", pending);
        model.addAttribute("pendingTeamRequests", pending.size());
        model.addAttribute("totalTeamRequests", totalRequests.size());

        // --- Balance Data ---
        int selectedYear = (year == null) ? Year.now().getValue() : year;
        model.addAttribute("selectedYear", selectedYear);
        var balances = leaveManagementService.getManagerTeamLeaveBalance(user.getId(), selectedYear);
        int totalAllowed = 0, totalUsed = 0, totalRemaining = 0;
        for (var emp : balances) {
            totalAllowed += emp.getTotalAllowedLeaves() != null ? emp.getTotalAllowedLeaves() : 0;
            totalUsed += emp.getUsedLeaves() != null ? emp.getUsedLeaves() : 0;
            totalRemaining += emp.getRemainingLeaves() != null ? emp.getRemainingLeaves() : 0;
        }
        model.addAttribute("teamLeaveBalances", balances);
        model.addAttribute("totalAllowedLeaves", totalAllowed);
        model.addAttribute("totalUsedLeaves", totalUsed);
        model.addAttribute("totalRemainingLeaves", totalRemaining);

        // --- Calendar Data (Approved team leaves only) ---
        YearMonth selectedYearMonth;
        try {
            int selectedCalendarYear = (year == null) ? Year.now().getValue() : year;
            int selectedCalendarMonth = (month == null) ? YearMonth.now().getMonthValue() : month;
            selectedYearMonth = YearMonth.of(selectedCalendarYear, selectedCalendarMonth);
        } catch (DateTimeException ex) {
            selectedYearMonth = YearMonth.now();
        }

        LocalDate monthStart = selectedYearMonth.atDay(1);
        LocalDate monthEnd = selectedYearMonth.atEndOfMonth();
        List<EmployeeLeaveHistoryDTO> approvedTeamLeaves = leaveManagementService
                .getApprovedTeamLeaveApplications(user.getId());

        Map<LocalDate, LinkedHashSet<String>> employeesOnLeaveByDate = new LinkedHashMap<>();
        Set<String> uniqueEmployeesOnLeave = new HashSet<>();
        int totalApprovedApplications = 0;

        for (EmployeeLeaveHistoryDTO leave : approvedTeamLeaves) {
            if (leave.getStartDate() == null || leave.getEndDate() == null) {
                continue;
            }

            LocalDate effectiveStart = leave.getStartDate().isAfter(monthStart) ? leave.getStartDate() : monthStart;
            LocalDate effectiveEnd = leave.getEndDate().isBefore(monthEnd) ? leave.getEndDate() : monthEnd;
            if (effectiveStart.isAfter(effectiveEnd)) {
                continue;
            }

            totalApprovedApplications += 1;
            String employeeName = (leave.getEmployeeName() == null || leave.getEmployeeName().trim().isEmpty())
                    ? "Employee"
                    : leave.getEmployeeName().trim();

            LocalDate dateCursor = effectiveStart;
            while (!dateCursor.isAfter(effectiveEnd)) {
                employeesOnLeaveByDate
                        .computeIfAbsent(dateCursor, d -> new LinkedHashSet<>())
                        .add(employeeName);
                uniqueEmployeesOnLeave.add(employeeName);
                dateCursor = dateCursor.plusDays(1);
            }
        }

        List<TeamLeaveCalendarRow> teamLeaveCalendarRows = new ArrayList<>();
        for (Map.Entry<LocalDate, LinkedHashSet<String>> entry : employeesOnLeaveByDate.entrySet()) {
            List<String> employeeNames = new ArrayList<>(entry.getValue());
            employeeNames.sort(String.CASE_INSENSITIVE_ORDER);
            teamLeaveCalendarRows.add(new TeamLeaveCalendarRow(entry.getKey(), String.join(", ", employeeNames)));
        }
        teamLeaveCalendarRows.sort(Comparator.comparing(TeamLeaveCalendarRow::getDate));

        LocalDate calendarStart = monthStart.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate calendarEnd = monthEnd.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        List<List<CalendarDayView>> calendarWeeks = new ArrayList<>();
        List<CalendarDayView> currentWeek = new ArrayList<>(7);

        LocalDate calendarDate = calendarStart;
        while (!calendarDate.isAfter(calendarEnd)) {
            List<String> namesForDate = new ArrayList<>(employeesOnLeaveByDate.getOrDefault(calendarDate, new LinkedHashSet<>()));
            namesForDate.sort(String.CASE_INSENSITIVE_ORDER);
            currentWeek.add(new CalendarDayView(
                    calendarDate.getDayOfMonth(),
                    selectedYearMonth.equals(YearMonth.from(calendarDate)),
                    namesForDate));

            if (currentWeek.size() == 7) {
                calendarWeeks.add(currentWeek);
                currentWeek = new ArrayList<>(7);
            }
            calendarDate = calendarDate.plusDays(1);
        }

        YearMonth previousMonth = selectedYearMonth.minusMonths(1);
        YearMonth nextMonth = selectedYearMonth.plusMonths(1);

        model.addAttribute("selectedMonthName", selectedYearMonth.getMonth().name());
        model.addAttribute("selectedYearCal", selectedYearMonth.getYear());
        model.addAttribute("prevYear", previousMonth.getYear());
        model.addAttribute("prevMonth", previousMonth.getMonthValue());
        model.addAttribute("nextYear", nextMonth.getYear());
        model.addAttribute("nextMonth", nextMonth.getMonthValue());
        model.addAttribute("daysWithLeaves", employeesOnLeaveByDate.size());
        model.addAttribute("employeesOnLeaveCount", uniqueEmployeesOnLeave.size());
        model.addAttribute("totalApprovedApplications", totalApprovedApplications);
        model.addAttribute("calendarWeeks", calendarWeeks);
        model.addAttribute("teamLeaveCalendarRows", teamLeaveCalendarRows);

        return "manager_team_leave";
    }

    @PostMapping("/team-requests/{id}/approve")
    public String approveLeave(@PathVariable Long id, @RequestParam(required = false) String managerComment,
            HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isManager(session))
            return "redirect:/";
        try {
            EmployeeDTO manager = getLoggedInUser(session);
            EmployeeLeaveHistoryDTO approvedLeave = leaveManagementService.approveTeamLeaveRequest(manager.getId(), id,
                    managerComment);
            String dateRange = approvedLeave.getStartDate()
                    + (approvedLeave.getStartDate().equals(approvedLeave.getEndDate()) ? ""
                            : " to " + approvedLeave.getEndDate());
            String details = "Approved leave request ID " + approvedLeave.getId()
                    + " for " + approvedLeave.getEmployeeName()
                    + " (" + approvedLeave.getLeaveTypeName() + ", " + dateRange + ").";
            recordManagerActivity(
                    manager,
                    "Leave Management",
                    "Manager Approved Leave",
                    details,
                    approvedLeave.getEmployeeId(),
                    approvedLeave.getEmployeeName());
            redirectAttributes.addFlashAttribute("success", "Leave request approved.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/manager/team-leave?tab=requests";
    }

    @PostMapping("/team-requests/{id}/reject")
    public String rejectLeave(@PathVariable Long id, @RequestParam(required = false) String managerComment,
            HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isManager(session))
            return "redirect:/";
        try {
            leaveManagementService.rejectTeamLeaveRequest(getLoggedInUser(session).getId(), id, managerComment);
            redirectAttributes.addFlashAttribute("success", "Leave request rejected.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/manager/team-leave?tab=requests";
    }

    @GetMapping("/performance-reviews")
    public String teamReviews(HttpSession session, Model model) {
        if (!isManager(session))
            return "redirect:/";
        EmployeeDTO user = getLoggedInUser(session);
        List<PerformanceReviewDTO> reviews = performanceReviewService.getManagerTeamPerformanceReviews(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("performanceReviews", reviews); // For detail-oriented access
        model.addAttribute("reviews", reviews); // Matches template naming
        model.addAttribute("totalReviews", reviews.size());
        model.addAttribute("submittedCount",
                reviews.stream().filter(r -> "SUBMITTED".equalsIgnoreCase(r.getStatus())).count());
        model.addAttribute("reviewedCount",
                reviews.stream().filter(r -> "REVIEWED".equalsIgnoreCase(r.getStatus())).count());
        model.addAttribute("currentUri", "/manager/performance-reviews");
        model.addAttribute("unreadCount", notificationService.getUnreadNotificationCount(user.getId()));

        return "manager_performance_reviews";
    }

    @GetMapping("/performance-reviews/{id}")
    public String reviewDetail(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes ra) {
        if (!isManager(session))
            return "redirect:/";
        try {
            EmployeeDTO user = getLoggedInUser(session);
            PerformanceReviewDTO review = performanceReviewService.getReviewForManager(user.getId(), id);
            boolean reviewLocked = review != null && "REVIEWED".equalsIgnoreCase(review.getStatus());
            String[] feedbackSections = splitManagerFeedbackSections(
                    review != null ? review.getManagerFeedback() : null);

            model.addAttribute("user", user);
            model.addAttribute("review", review);
            model.addAttribute("reviewLocked", reviewLocked);
            model.addAttribute("managerStrengths", feedbackSections[0]);
            model.addAttribute("managerImprovementAreas", feedbackSections[1]);
            model.addAttribute("managerSuggestions", feedbackSections[2]);
            model.addAttribute("currentUri", "/manager/performance-reviews");
            model.addAttribute("unreadCount", notificationService.getUnreadNotificationCount(user.getId()));
            return "manager_performance_review_detail";
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/manager/performance-reviews";
        }
    }

    @PostMapping("/performance-reviews/{id}/feedback")
    public String submitFeedback(@PathVariable Long id,
            @RequestParam(required = false) String managerFeedback,
            @RequestParam(required = false) String managerStrengths,
            @RequestParam(required = false) String managerImprovementAreas,
            @RequestParam(required = false) String managerSuggestions,
            @RequestParam Integer managerRating,
            HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isManager(session))
            return "redirect:/";
        try {
            String structuredFeedback = buildStructuredManagerFeedback(
                    managerStrengths,
                    managerImprovementAreas,
                    managerSuggestions,
                    managerFeedback);
            performanceReviewService.submitManagerFeedback(getLoggedInUser(session).getId(), id, structuredFeedback,
                    managerRating);
            redirectAttributes.addFlashAttribute("success", "Performance feedback submitted.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/manager/performance-reviews";
    }

    @GetMapping("/performance-goals")
    public String teamGoalsRedirect() {
        return "redirect:/manager/goals";
    }

    @GetMapping("/my-goals")
    public String myGoalsRedirect() {
        return "redirect:/manager/goals";
    }

    @GetMapping("/goals")
    public String combinedGoals(HttpSession session, Model model) {
        if (!isManager(session))
            return "redirect:/";
        EmployeeDTO user = getLoggedInUser(session);
        int currentYear = Year.now().getValue();

        // Team Goals Data
        List<PerformanceGoalDTO> teamGoals = performanceGoalService.getManagerTeamGoals(user.getId());
        double avgProgress = teamGoals.isEmpty() ? 0
                : teamGoals.stream()
                        .mapToInt(g -> g.getCompletionPercentage() != null ? g.getCompletionPercentage() : 0)
                        .average().orElse(0.0);

        // Personal Goals Data
        List<PerformanceGoalDTO> personalGoals = performanceGoalService.getEmployeeGoalsByYear(user.getId(),
                currentYear);

        model.addAttribute("user", user);

        // Team attributes
        model.addAttribute("performanceGoals", teamGoals);
        model.addAttribute("teamGoalsCount", teamGoals.size());
        model.addAttribute("averageProgress", Math.round(avgProgress));
        model.addAttribute("commentedGoals",
                teamGoals.stream().filter(g -> g.getManagerComment() != null && !g.getManagerComment().isBlank())
                        .count());
        model.addAttribute("pendingComments",
                teamGoals.stream().filter(g -> g.getManagerComment() == null || g.getManagerComment().isBlank())
                        .count());

        // Personal attributes
        model.addAttribute("personalGoals", personalGoals);
        model.addAttribute("totalPersonalGoals", personalGoals.size());

        return "manager_goals";
    }

    @GetMapping("/performance-goals/{id}")
    public String goalDetail(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes ra) {
        if (!isManager(session))
            return "redirect:/";
        try {
            model.addAttribute("user", getLoggedInUser(session));
            model.addAttribute("goal", performanceGoalService.getGoalForManager(getLoggedInUser(session).getId(), id));
            return "manager_performance_goal_detail";
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/manager/performance-goals";
        }
    }

    @PostMapping("/performance-goals/{id}/comment")
    public String submitGoalComment(@PathVariable Long id, @RequestParam String managerComment, @RequestParam(required = false) Integer finalRating,
            HttpSession session, RedirectAttributes ra) {
        if (!isManager(session))
            return "redirect:/";
        try {
            performanceGoalService.saveManagerComment(getLoggedInUser(session).getId(), id, managerComment, finalRating);
            ra.addFlashAttribute("success", "Manager comment saved.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/manager/performance-goals/" + id;
    }

    @PostMapping("/my-goals")
    public String submitMyGoal(@RequestParam String goalDescription,
            @RequestParam String deadline,
            @RequestParam String priority,
            HttpSession session, RedirectAttributes ra) {
        if (!isManager(session))
            return "redirect:/";
        try {
            performanceGoalService.createEmployeeGoal(getLoggedInUser(session).getId(), goalDescription,
                    LocalDate.parse(deadline), priority);
            ra.addFlashAttribute("success", "Personal goal added successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error creating goal: " + e.getMessage());
        }
        return "redirect:/manager/goals";
    }

    @PostMapping("/my-goals/{id}/update-progress")
    public String updateMyGoalProgress(@PathVariable Long id,
            @RequestParam Integer completionPercentage,
            @RequestParam String goalStatus,
            HttpSession session, RedirectAttributes ra) {
        if (!isManager(session))
            return "redirect:/";
        try {
            performanceGoalService.updateEmployeeGoalProgress(getLoggedInUser(session).getId(), id,
                    completionPercentage,
                    goalStatus);
            ra.addFlashAttribute("success", "Progress updated.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/manager/goals";
    }

    public static class CalendarDayView {
        private final int dayNumber;
        private final boolean currentMonth;
        private final List<String> employeeNames;

        public CalendarDayView(int dayNumber, boolean currentMonth, List<String> employeeNames) {
            this.dayNumber = dayNumber;
            this.currentMonth = currentMonth;
            this.employeeNames = employeeNames;
        }

        public int getDayNumber() {
            return dayNumber;
        }

        public boolean isCurrentMonth() {
            return currentMonth;
        }

        public List<String> getEmployeeNames() {
            return employeeNames;
        }
    }

    public static class TeamLeaveCalendarRow {
        private final LocalDate date;
        private final String employeesOnLeave;

        public TeamLeaveCalendarRow(LocalDate date, String employeesOnLeave) {
            this.date = date;
            this.employeesOnLeave = employeesOnLeave;
        }

        public LocalDate getDate() {
            return date;
        }

        public String getEmployeesOnLeave() {
            return employeesOnLeave;
        }
    }

    public static class TeamBalanceRow {
        private String employeeName;
        private int totalAllowedLeaves;
        private int usedLeaves;
        private int remainingLeaves;

        public String getEmployeeName() {
            return employeeName;
        }

        public void setEmployeeName(String employeeName) {
            this.employeeName = employeeName;
        }

        public int getTotalAllowedLeaves() {
            return totalAllowedLeaves;
        }

        public void setTotalAllowedLeaves(int totalAllowedLeaves) {
            this.totalAllowedLeaves = totalAllowedLeaves;
        }

        public int getUsedLeaves() {
            return usedLeaves;
        }

        public void setUsedLeaves(int usedLeaves) {
            this.usedLeaves = usedLeaves;
        }

        public int getRemainingLeaves() {
            return remainingLeaves;
        }

        public void setRemainingLeaves(int remainingLeaves) {
            this.remainingLeaves = remainingLeaves;
        }
    }

    private void recordManagerActivity(EmployeeDTO actor,
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
            // Activity log failures should not block manager workflows.
        }
    }

    private String buildStructuredManagerFeedback(
            String strengths,
            String improvementAreas,
            String suggestions,
            String fallbackFeedback) {
        String cleanStrengths = normalizeFeedbackPart(strengths);
        String cleanImprovementAreas = normalizeFeedbackPart(improvementAreas);
        String cleanSuggestions = normalizeFeedbackPart(suggestions);

        if (cleanStrengths.isEmpty() && cleanImprovementAreas.isEmpty() && cleanSuggestions.isEmpty()) {
            return normalizeFeedbackPart(fallbackFeedback);
        }

        StringBuilder builder = new StringBuilder();
        builder.append("Strengths:\n").append(cleanStrengths.isEmpty() ? "N/A" : cleanStrengths).append("\n\n");
        builder.append("Areas of Improvement:\n")
                .append(cleanImprovementAreas.isEmpty() ? "N/A" : cleanImprovementAreas)
                .append("\n\n");
        builder.append("Suggestions:\n").append(cleanSuggestions.isEmpty() ? "N/A" : cleanSuggestions);
        return builder.toString();
    }

    private String[] splitManagerFeedbackSections(String managerFeedback) {
        String[] sections = new String[] { "", "", "" };
        String feedback = normalizeFeedbackPart(managerFeedback);
        if (feedback.isEmpty()) {
            return sections;
        }

        Pattern pattern = Pattern.compile(
                "(?s)Strengths:\\s*(.*?)\\s*Areas of Improvement:\\s*(.*?)\\s*Suggestions:\\s*(.*)");
        Matcher matcher = pattern.matcher(feedback);
        if (matcher.matches()) {
            sections[0] = normalizeFeedbackPart(matcher.group(1));
            sections[1] = normalizeFeedbackPart(matcher.group(2));
            sections[2] = normalizeFeedbackPart(matcher.group(3));
            return sections;
        }

        sections[0] = feedback;
        return sections;
    }

    private String normalizeFeedbackPart(String value) {
        return value == null ? "" : value.trim();
    }
}
