package com.rev.app.rest;

import com.rev.app.dto.ApiResponse;
import com.rev.app.dto.EmployeeLeaveBalanceSummary;
import com.rev.app.dto.EmployeeLeaveHistoryDTO;
import com.rev.app.dto.LeaveTypeDTO;
import com.rev.app.service.LeaveManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;

@RestController
@RequestMapping("/api/leaves")
public class RestLeaveController {

    @Autowired
    private LeaveManagementService leaveManagementService;

    @GetMapping("/types/active")
    public ResponseEntity<ApiResponse<List<LeaveTypeDTO>>> getActiveLeaveTypes() {
        List<LeaveTypeDTO> types = leaveManagementService.getActiveLeaveTypes();
        return ResponseEntity.ok(ApiResponse.success("Successfully retrieved active leave types", types));
    }

    @GetMapping("/balance/{employeeId}")
    public ResponseEntity<ApiResponse<List<EmployeeLeaveBalanceSummary>>> getLeaveBalance(@PathVariable Long employeeId,
            @RequestParam(required = false) Integer year) {
        int selectedYear = (year == null) ? Year.now().getValue() : year;
        List<EmployeeLeaveBalanceSummary> balances = leaveManagementService.getEmployeeLeaveBalanceByYear(employeeId,
                selectedYear);
        return ResponseEntity.ok(ApiResponse.success("Successfully retrieved leave balances", balances));
    }

    @GetMapping("/history/{employeeId}")
    public ResponseEntity<ApiResponse<List<EmployeeLeaveHistoryDTO>>> getLeaveHistory(@PathVariable Long employeeId) {
        List<EmployeeLeaveHistoryDTO> history = leaveManagementService.getEmployeeLeaveApplications(employeeId);
        return ResponseEntity.ok(ApiResponse.success("Successfully retrieved leave history", history));
    }

    @PostMapping("/apply")
    public ResponseEntity<ApiResponse<EmployeeLeaveHistoryDTO>> applyLeave(
            @RequestParam Long employeeId,
            @RequestParam Long leaveTypeId,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam String reason) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            EmployeeLeaveHistoryDTO application = leaveManagementService.submitLeaveRequest(employeeId, leaveTypeId,
                    start, end, reason);
            return ResponseEntity.ok(ApiResponse.success("Successfully applied for leave", application));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(ApiResponse.error(e.getMessage()));
        }
    }
}
