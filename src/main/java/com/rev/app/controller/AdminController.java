package com.rev.app.controller;

import com.rev.app.dto.AnnouncementDTO;
import com.rev.app.dto.DepartmentDTO;
import com.rev.app.dto.DesignationDTO;
import com.rev.app.dto.EmployeeDTO;
import com.rev.app.dto.DepartmentLeaveReportSummary;
import com.rev.app.dto.EmployeeLeaveReportSummary;
import com.rev.app.dto.EmployeeLeaveBalanceSummary;
import com.rev.app.dto.EmployeeLeaveHistoryDTO;
import com.rev.app.dto.EmployeeLeaveQuotaDTO;
import com.rev.app.dto.EmployeeQuotaSummary;
import com.rev.app.dto.LeaveTypeDTO;
import com.rev.app.dto.CompanyHolidayDTO;
import com.rev.app.entity.CompanyHoliday;
import com.rev.app.entity.EmployeeLeaveQuota;
import com.rev.app.entity.EmployeeLeaveHistory;
import com.rev.app.entity.Employee;
import com.rev.app.entity.SystemActivityLog;
import com.rev.app.service.AnnouncementService;
import com.rev.app.service.CompanyHolidayService;
import com.rev.app.service.DepartmentService;
import com.rev.app.service.DesignationService;
import com.rev.app.service.EmployeeService;
import com.rev.app.service.LeaveManagementService;
import com.rev.app.service.SystemActivityLogService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.Year;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private LeaveManagementService leaveManagementService;

    @Autowired
    private CompanyHolidayService companyHolidayService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private DesignationService designationService;

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private SystemActivityLogService systemActivityLogService;

    private boolean isAdmin(HttpSession session, Model model) {
        Object sessionUser = session.getAttribute("loggedInUser");
        if (!(sessionUser instanceof EmployeeDTO)) {
            return false;
        }

        EmployeeDTO dtoUser = (EmployeeDTO) sessionUser;
        if (dtoUser.getId() == null) {
            return false;
        }

        Employee freshUser = employeeService.getEmployeeById(dtoUser.getId());
        if (freshUser != null && freshUser.isActive() && "ADMIN".equalsIgnoreCase(freshUser.getRole())) {
            if (model != null) {
                model.addAttribute("user", employeeService.getEmployeeDTOById(dtoUser.getId()));
            }
            return true;
        }
        if (freshUser == null || !freshUser.isActive()) {
            session.invalidate();
        }
        return false;
    }

    @GetMapping("/dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        if (!isAdmin(session, model))
            return "redirect:/";

        List<EmployeeDTO> employees = employeeService.getAllEmployees();
        int totalDepartments = departmentService.getAllDepartments().size();
        long activeCount = employees.stream().filter(EmployeeDTO::isActive).count();
        long managerCount = employees.stream()
                .filter(emp -> "MANAGER".equalsIgnoreCase(emp.getRole()))
                .count();

        model.addAttribute("totalEmployees", employees.size());
        model.addAttribute("totalDepartments", totalDepartments);
        model.addAttribute("activeEmployees", activeCount);
        model.addAttribute("inactiveEmployees", employees.size() - activeCount);
        model.addAttribute("managerCount", managerCount);
        model.addAttribute("announcements", announcementService.getActiveAnnouncements());
        return "admin_dashboard";
    }

    @GetMapping("/employees")
    public String listEmployees(HttpSession session, Model model) {
        if (!isAdmin(session, model))
            return "redirect:/";

        model.addAttribute("employees", employeeService.getAllEmployees());
        return "employee_list";
    }

    @GetMapping("/add-employee")
    public String showAddEmployeeForm(HttpSession session, Model model) {
        if (!isAdmin(session, model))
            return "redirect:/";

        EmployeeDTO newEmp = new EmployeeDTO();
        newEmp.setId(employeeService.generateUniqueEmployeeId());
        model.addAttribute("employee", newEmp);
        model.addAttribute("managers", employeeService.getManagers());
        model.addAttribute("designations", designationService.getAllDesignations());
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "add_employee";
    }

    @GetMapping("/api/designations/by-department")
    @ResponseBody
    public List<DesignationDTO> getDesignationsByDept(@RequestParam String departmentName) {
        return designationService.getDesignationsByDepartment(departmentName);
    }

    @PostMapping("/add-employee")
    public String addEmployee(@ModelAttribute("employee") EmployeeDTO employeeDTO,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (!isAdmin(session, model))
            return "redirect:/";

        try {
            employeeService.saveEmployee(employeeDTO);
            recordAdminActivity(session, "Employee Management", "Employee Created",
                    "Created employee ID " + employeeDTO.getId());
            redirectAttributes.addFlashAttribute("success", "Employee added successfully!");
            return "redirect:/admin/employees";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("employee", employeeDTO);
            model.addAttribute("managers", employeeService.getManagers());
            model.addAttribute("designations", designationService.getAllDesignations());
            model.addAttribute("departments", departmentService.getAllDepartments());
            return "add_employee";
        }
    }

    @GetMapping("/edit-employee/{id}")
    public String showEditEmployeeForm(@PathVariable Long id, HttpSession session, Model model) {
        if (!isAdmin(session, model))
            return "redirect:/";

        EmployeeDTO employee = employeeService.getEmployeeDTOById(id);
        if (employee == null) {
            return "redirect:/admin/employees";
        }

        model.addAttribute("employee", employee);
        model.addAttribute("managers", employeeService.getManagers());
        model.addAttribute("designations", designationService.getAllDesignations());
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "edit_employee";
    }

    @PostMapping("/edit-employee")
    public String editEmployee(@ModelAttribute EmployeeDTO employeeDTO,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (!isAdmin(session, model))
            return "redirect:/";

        try {
            employeeService.saveEmployee(employeeDTO);
            recordAdminActivity(session, "Employee Management", "Employee Updated",
                    "Updated employee ID " + employeeDTO.getId());
            redirectAttributes.addFlashAttribute("success", "Employee updated successfully!");
            return "redirect:/admin/employees";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("employee", employeeDTO);
            model.addAttribute("managers", employeeService.getManagers());
            model.addAttribute("designations", designationService.getAllDesignations());
            model.addAttribute("departments", departmentService.getAllDepartments());
            return "edit_employee";
        }
    }

    @PostMapping("/deactivate-employee/{id}")
    public String deactivateEmployee(@PathVariable Long id, HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (!isAdmin(session, null))
            return "redirect:/";

        try {
            EmployeeDTO currentUser = getLoggedInUser(session);
            if (currentUser != null && currentUser.getId() != null && currentUser.getId().equals(id)) {
                throw new Exception("You cannot deactivate your own account.");
            }

            employeeService.deactivateEmployee(id);
            recordAdminActivity(session, "Employee Management", "Employee Deactivated",
                    "Deactivated employee ID " + id + ".");
            redirectAttributes.addFlashAttribute("success", "Employee account deactivated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/employees";
    }

    @PostMapping("/reactivate-employee/{id}")
    public String reactivateEmployee(@PathVariable Long id, HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (!isAdmin(session, null))
            return "redirect:/";

        try {
            employeeService.reactivateEmployee(id);
            recordAdminActivity(session, "Employee Management", "Employee Reactivated",
                    "Reactivated employee ID " + id + ".");
            redirectAttributes.addFlashAttribute("success", "Employee account reactivated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/employees";
    }

    @GetMapping("/view-employee/{id}")
    public String showEmployeeProfile(@PathVariable Long id, HttpSession session, Model model) {
        if (!isAdmin(session, model))
            return "redirect:/";

        try {
            EmployeeDTO employee = employeeService.getEmployeeDTOById(id);
            model.addAttribute("employee", employee);
            return "view_employee";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/admin/employees";
        }
    }

    @GetMapping("/team-structure")
    public String showTeamStructure(HttpSession session, Model model) {
        if (!isAdmin(session, model))
            return "redirect:/";
        model.addAttribute("managers", employeeService.getManagers());
        model.addAttribute("managerLeaves", leaveManagementService.getManagerLeaveRequests());
        return "admin_team_structure";
    }

    @GetMapping("/leave-management")
    public String showLeaveManagement(@RequestParam(required = false) Integer year,
            HttpSession session,
            Model model) {
        if (!isAdmin(session, model))
            return "redirect:/";

        int selectedYear = (year == null) ? Year.now().getValue() : year;

        List<EmployeeDTO> employees = employeeService.getActiveEmployees();
        List<LeaveTypeDTO> leaveTypes = leaveManagementService.getAllLeaveTypes();
        List<LeaveTypeDTO> activeLeaveTypes = leaveManagementService.getActiveLeaveTypes();
        List<EmployeeLeaveQuotaDTO> leaveQuotas = leaveManagementService.getQuotasByYear(selectedYear);
        List<EmployeeQuotaSummary> quotaSummary = leaveManagementService.getEmployeeQuotaSummaryByYear(selectedYear);
        List<EmployeeLeaveBalanceSummary> leaveBalances = leaveManagementService
                .getEmployeeLeaveBalanceByYear(selectedYear);
        List<EmployeeLeaveHistoryDTO> leaveHistory = leaveManagementService.getLeaveHistoryByYear(selectedYear);

        model.addAttribute("selectedYear", selectedYear);
        model.addAttribute("employees", employees);
        model.addAttribute("leaveTypes", leaveTypes);
        model.addAttribute("activeLeaveTypes", activeLeaveTypes);
        model.addAttribute("leaveQuotas", leaveQuotas);
        model.addAttribute("quotaSummary", quotaSummary);
        model.addAttribute("leaveBalances", leaveBalances);
        model.addAttribute("leaveHistory", leaveHistory);
        return "leave_management";
    }

    @GetMapping("/leave-reports")
    public String showLeaveReports(@RequestParam(required = false) Integer year,
            HttpSession session,
            Model model) {
        if (!isAdmin(session, model))
            return "redirect:/";

        int selectedYear = (year == null) ? Year.now().getValue() : year;
        List<DepartmentLeaveReportSummary> departmentReports = leaveManagementService
                .getDepartmentWiseLeaveReport(selectedYear);
        List<EmployeeLeaveReportSummary> employeeReports = leaveManagementService
                .getEmployeeWiseLeaveReport(selectedYear);

        model.addAttribute("selectedYear", selectedYear);
        model.addAttribute("departmentReports", departmentReports);
        model.addAttribute("employeeReports", employeeReports);
        return "leave_reports";
    }

    @GetMapping("/holiday-calendar")
    public String showHolidayCalendar(@RequestParam(required = false) Integer year,
            HttpSession session,
            Model model) {
        if (!isAdmin(session, model))
            return "redirect:/";

        int selectedYear = (year == null) ? Year.now().getValue() : year;
        List<CompanyHolidayDTO> holidays = companyHolidayService.getHolidaysByYear(selectedYear);

        model.addAttribute("selectedYear", selectedYear);
        model.addAttribute("holidays", holidays);
        return "holiday_calendar";
    }

    @GetMapping("/departments")
    public String showDepartmentManagement(HttpSession session, Model model) {
        if (!isAdmin(session, model))
            return "redirect:/";

        List<DepartmentDTO> departments = departmentService.getAllDepartments();
        Map<Long, Long> departmentEmployeeCounts = new LinkedHashMap<>();
        for (DepartmentDTO department : departments) {
            departmentEmployeeCounts.put(
                    department.getId(),
                    departmentService.getEmployeeCountByDepartment(department.getName()));
        }

        model.addAttribute("departments", departments);
        model.addAttribute("departmentEmployeeCounts", departmentEmployeeCounts);
        model.addAttribute("totalDepartments", departments.size());
        return "departments";
    }

    @GetMapping("/designations")
    public String showDesignationManagement(HttpSession session, Model model) {
        if (!isAdmin(session, model))
            return "redirect:/";

        List<DesignationDTO> designations = designationService.getAllDesignations();
        Map<Long, Long> designationEmployeeCounts = new LinkedHashMap<>();
        for (DesignationDTO designation : designations) {
            designationEmployeeCounts.put(
                    designation.getId(),
                    designationService.getEmployeeCountByDesignation(designation.getName()));
        }

        model.addAttribute("designations", designations);
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("designationEmployeeCounts", designationEmployeeCounts);
        model.addAttribute("totalDesignations", designations.size());
        return "designations";
    }

    @GetMapping("/announcements")
    public String showAnnouncementManagement(HttpSession session, Model model) {
        if (!isAdmin(session, model))
            return "redirect:/";

        List<AnnouncementDTO> announcements = announcementService.getAllAnnouncements();
        model.addAttribute("announcements", announcements);
        model.addAttribute("totalAnnouncements", announcements.size());
        return "announcements";
    }

    @GetMapping("/system-activity-log")
    public String showSystemActivityLog(@RequestParam(required = false) String action,
            @RequestParam(required = false, name = "performedBy") String performedBy,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String employee,
            @RequestParam(required = false, defaultValue = "timestamp") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir,
            HttpSession session,
            Model model) {
        if (!isAdmin(session, model))
            return "redirect:/";

        List<SystemActivityLog> activityLogs = systemActivityLogService.searchLogs(
                action,
                performedBy,
                role,
                employee,
                sortBy,
                sortDir,
                500);
        model.addAttribute("activityLogs", activityLogs);
        model.addAttribute("filterAction", action);
        model.addAttribute("filterPerformedBy", performedBy);
        model.addAttribute("filterRole", role);
        model.addAttribute("filterEmployee", employee);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        return "system_activity_log";
    }

    @PostMapping("/departments")
    public String createDepartment(@RequestParam String name,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (!isAdmin(session, model))
            return "redirect:/";

        try {
            departmentService.createDepartment(name);
            recordAdminActivity(
                    session,
                    "Department Management",
                    "Department Created",
                    "Created department: " + ((name == null) ? "" : name.trim()) + ".");
            redirectAttributes.addFlashAttribute("success", "Department created successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/departments";
    }

    @PostMapping("/departments/{id}/update")
    public String updateDepartment(@PathVariable Long id,
            @RequestParam String name,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (!isAdmin(session, model))
            return "redirect:/";

        try {
            departmentService.updateDepartment(id, name);
            recordAdminActivity(
                    session,
                    "Department Management",
                    "Department Updated",
                    "Updated department ID " + id + " to: " + ((name == null) ? "" : name.trim()) + ".");
            redirectAttributes.addFlashAttribute("success", "Department updated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/departments";
    }

    @PostMapping("/departments/{id}/delete")
    public String deleteDepartment(@PathVariable Long id,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (!isAdmin(session, model))
            return "redirect:/";

        try {
            departmentService.deleteDepartment(id);
            recordAdminActivity(
                    session,
                    "Department Management",
                    "Department Deleted",
                    "Deleted department ID " + id + ".");
            redirectAttributes.addFlashAttribute("success", "Department deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/departments";
    }

    @PostMapping("/designations")
    public String createDesignation(@RequestParam String name,
            @RequestParam Long departmentId,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (!isAdmin(session, model))
            return "redirect:/";

        try {
            designationService.createDesignation(name, departmentId);
            recordAdminActivity(
                    session,
                    "Designation Management",
                    "Designation Created",
                    "Created designation: " + ((name == null) ? "" : name.trim()) + " in department " + departmentId
                            + ".");
            redirectAttributes.addFlashAttribute("success", "Designation created successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/designations";
    }

    @PostMapping("/designations/{id}/update")
    public String updateDesignation(@PathVariable Long id,
            @RequestParam String name,
            @RequestParam Long departmentId,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (!isAdmin(session, model))
            return "redirect:/";

        try {
            designationService.updateDesignation(id, name, departmentId);
            recordAdminActivity(
                    session,
                    "Designation Management",
                    "Designation Updated",
                    "Updated designation ID " + id + " to: " + ((name == null) ? "" : name.trim()) + " in department "
                            + departmentId + ".");
            redirectAttributes.addFlashAttribute("success", "Designation updated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/designations";
    }

    @PostMapping("/designations/{id}/delete")
    public String deleteDesignation(@PathVariable Long id,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (!isAdmin(session, model))
            return "redirect:/";

        try {
            designationService.deleteDesignation(id);
            recordAdminActivity(
                    session,
                    "Designation Management",
                    "Designation Deleted",
                    "Deleted designation ID " + id + ".");
            redirectAttributes.addFlashAttribute("success", "Designation deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/designations";
    }

    @PostMapping("/announcements")
    public String createAnnouncement(@RequestParam String title,
            @RequestParam String message,
            @RequestParam(defaultValue = "true") boolean active,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (!isAdmin(session, model))
            return "redirect:/";

        try {
            announcementService.createAnnouncement(title, message, active);
            recordAdminActivity(
                    session,
                    "Announcement Management",
                    "Announcement Created",
                    "Created announcement: " + ((title == null) ? "" : title.trim()) + ".");
            redirectAttributes.addFlashAttribute("success", "Announcement created successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/announcements";
    }

    @PostMapping("/announcements/{id}/update")
    public String updateAnnouncement(@PathVariable Long id,
            @RequestParam String title,
            @RequestParam String message,
            @RequestParam boolean active,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (!isAdmin(session, model))
            return "redirect:/";

        try {
            announcementService.updateAnnouncement(id, title, message, active);
            recordAdminActivity(
                    session,
                    "Announcement Management",
                    "Announcement Updated",
                    "Updated announcement ID " + id + " (" + ((title == null) ? "" : title.trim()) + ").");
            redirectAttributes.addFlashAttribute("success", "Announcement updated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/announcements";
    }

    @PostMapping("/announcements/{id}/delete")
    public String deleteAnnouncement(@PathVariable Long id,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (!isAdmin(session, model))
            return "redirect:/";

        try {
            announcementService.deleteAnnouncement(id);
            recordAdminActivity(
                    session,
                    "Announcement Management",
                    "Announcement Deleted",
                    "Deleted announcement ID " + id + ".");
            redirectAttributes.addFlashAttribute("success", "Announcement deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/announcements";
    }

    @PostMapping("/holidays")
    public String addHoliday(@RequestParam String name,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate holidayDate,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Integer year,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (!isAdmin(session, model))
            return "redirect:/";

        try {
            companyHolidayService.addHoliday(name, holidayDate, description);
            recordAdminActivity(
                    session,
                    "Holiday Calendar",
                    "Holiday Created",
                    "Added holiday: " + ((name == null) ? "" : name.trim()) + " on " + holidayDate + ".");
            redirectAttributes.addFlashAttribute("success", "Holiday added successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        int selectedYear = (year == null) ? ((holidayDate == null) ? Year.now().getValue() : holidayDate.getYear())
                : year;
        return "redirect:/admin/holiday-calendar?year=" + selectedYear;
    }

    @PostMapping("/holidays/{id}/update")
    public String updateHoliday(@PathVariable Long id,
            @RequestParam String name,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate holidayDate,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Integer year,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (!isAdmin(session, model))
            return "redirect:/";

        try {
            companyHolidayService.updateHoliday(id, name, holidayDate, description);
            recordAdminActivity(
                    session,
                    "Holiday Calendar",
                    "Holiday Updated",
                    "Updated holiday ID " + id + " to " + ((name == null) ? "" : name.trim()) + " on " + holidayDate
                            + ".");
            redirectAttributes.addFlashAttribute("success", "Holiday updated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        int selectedYear = (year == null) ? ((holidayDate == null) ? Year.now().getValue() : holidayDate.getYear())
                : year;
        return "redirect:/admin/holiday-calendar?year=" + selectedYear;
    }

    @PostMapping("/holidays/{id}/delete")
    public String deleteHoliday(@PathVariable Long id,
            @RequestParam(required = false) Integer year,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (!isAdmin(session, model))
            return "redirect:/";

        try {
            companyHolidayService.deleteHoliday(id);
            recordAdminActivity(
                    session,
                    "Holiday Calendar",
                    "Holiday Deleted",
                    "Deleted holiday ID " + id + ".");
            redirectAttributes.addFlashAttribute("success", "Holiday removed successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        int selectedYear = (year == null) ? Year.now().getValue() : year;
        return "redirect:/admin/holiday-calendar?year=" + selectedYear;
    }

    @PostMapping("/leave-types")
    public String createLeaveType(@RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Integer year,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (!isAdmin(session, model))
            return "redirect:/";

        try {
            leaveManagementService.createLeaveType(name, description);
            recordAdminActivity(
                    session,
                    "Leave Management",
                    "Leave Type Created",
                    "Created leave type: " + ((name == null) ? "" : name.trim()) + ".");
            redirectAttributes.addFlashAttribute("success", "Leave type created successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/leave-management?year=" + ((year == null) ? Year.now().getValue() : year);
    }

    @PostMapping("/leave-types/{id}/status")
    public String updateLeaveTypeStatus(@PathVariable Long id,
            @RequestParam boolean active,
            @RequestParam(required = false) Integer year,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (!isAdmin(session, model))
            return "redirect:/";

        try {
            leaveManagementService.updateLeaveTypeStatus(id, active);
            recordAdminActivity(
                    session,
                    "Leave Management",
                    active ? "Leave Type Activated" : "Leave Type Deactivated",
                    (active ? "Activated" : "Deactivated") + " leave type ID " + id + ".");
            redirectAttributes.addFlashAttribute("success",
                    active ? "Leave type activated successfully." : "Leave type deactivated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/leave-management?year=" + ((year == null) ? Year.now().getValue() : year);
    }

    @PostMapping("/leave-quotas")
    public String assignLeaveQuota(@RequestParam Long employeeId,
            @RequestParam Long leaveTypeId,
            @RequestParam Integer year,
            @RequestParam Integer quotaDays,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (!isAdmin(session, model))
            return "redirect:/";

        try {
            leaveManagementService.assignOrUpdateQuota(employeeId, leaveTypeId, year, quotaDays);
            recordAdminActivity(
                    session,
                    "Leave Management",
                    "Leave Quota Assigned/Updated",
                    "Employee ID " + employeeId + ", leave type ID " + leaveTypeId + ", year " + year + ", quota "
                            + quotaDays + " days.");
            redirectAttributes.addFlashAttribute("success", "Leave quota assigned/updated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/leave-management?year=" + ((year == null) ? Year.now().getValue() : year);
    }

    @PostMapping("/leave/{leaveId}/approve")
    public String adminApproveLeave(@PathVariable Long leaveId,
            @RequestParam(required = false) String adminComment,
            @RequestParam(required = false) Integer year,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (!isAdmin(session, null))
            return "redirect:/";
        try {
            leaveManagementService.adminApproveLeaveRequest(leaveId, adminComment);
            redirectAttributes.addFlashAttribute("success", "Leave request approved successfully.");
            recordAdminActivity(session, "Leave Management", "Approve Leave", "Approved leave ID: " + leaveId);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/admin/leave-management?year=" + ((year == null) ? Year.now().getValue() : year);
    }

    @PostMapping("/leave/{leaveId}/reject")
    public String adminRejectLeave(@PathVariable Long leaveId,
            @RequestParam(required = false) String adminComment,
            @RequestParam(required = false) Integer year,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (!isAdmin(session, null))
            return "redirect:/";
        try {
            leaveManagementService.adminRejectLeaveRequest(leaveId, adminComment);
            redirectAttributes.addFlashAttribute("error", "Leave request rejected.");
            recordAdminActivity(session, "Leave Management", "Reject Leave", "Rejected leave ID: " + leaveId);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/admin/leave-management?year=" + ((year == null) ? Year.now().getValue() : year);
    }

    private EmployeeDTO getLoggedInUser(HttpSession session) {
        Object user = session.getAttribute("loggedInUser");
        if (user instanceof EmployeeDTO) {
            return (EmployeeDTO) user;
        }
        return null;
    }

    private void recordAdminActivity(HttpSession session, String moduleName, String actionName, String details) {
        try {
            systemActivityLogService.logActivity(getLoggedInUser(session), moduleName, actionName, details);
        } catch (Exception ignored) {
            // Activity logs should not break the primary admin flow.
        }
    }
}
