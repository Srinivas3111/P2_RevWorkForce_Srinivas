package com.rev.app.mapper;

import com.rev.app.dto.EmployeeLeaveHistoryDTO;
import com.rev.app.entity.EmployeeLeaveHistory;
import org.springframework.stereotype.Component;

@Component
public class EmployeeLeaveHistoryMapper {

    public EmployeeLeaveHistoryDTO toDTO(EmployeeLeaveHistory history) {
        if (history == null) {
            return null;
        }
        EmployeeLeaveHistoryDTO dto = new EmployeeLeaveHistoryDTO();
        dto.setId(history.getId());
        if (history.getEmployee() != null) {
            dto.setEmployeeId(history.getEmployee().getId());
            dto.setEmployeeName(history.getEmployee().getName());
        }
        if (history.getLeaveType() != null) {
            dto.setLeaveTypeId(history.getLeaveType().getId());
            dto.setLeaveTypeName(history.getLeaveType().getName());
        }
        dto.setYear(history.getYear());
        dto.setStartDate(history.getStartDate());
        dto.setEndDate(history.getEndDate());
        dto.setLeaveDays(history.getLeaveDays());
        dto.setStatus(history.getStatus());
        dto.setReason(history.getReason());
        dto.setAppliedOn(history.getAppliedOn());
        dto.setManagerComment(history.getManagerComment());
        return dto;
    }
}
