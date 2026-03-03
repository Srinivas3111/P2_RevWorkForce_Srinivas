package com.rev.app.mapper;

import com.rev.app.dto.EmployeeLeaveQuotaDTO;
import com.rev.app.entity.EmployeeLeaveQuota;
import org.springframework.stereotype.Component;

@Component
public class EmployeeLeaveQuotaMapper {

    public EmployeeLeaveQuotaDTO toDTO(EmployeeLeaveQuota quota) {
        if (quota == null) {
            return null;
        }
        EmployeeLeaveQuotaDTO dto = new EmployeeLeaveQuotaDTO();
        dto.setId(quota.getId());
        if (quota.getEmployee() != null) {
            dto.setEmployeeId(quota.getEmployee().getId());
<<<<<<< HEAD
            dto.setEmployeeName(quota.getEmployee().getName());
=======
            dto.setEmployeeName(quota.getEmployee().getFirstName() + " " + quota.getEmployee().getLastName());
>>>>>>> b09ad693854b4496e321429ab9250ea0c6c408cf
        }
        if (quota.getLeaveType() != null) {
            dto.setLeaveTypeId(quota.getLeaveType().getId());
            dto.setLeaveTypeName(quota.getLeaveType().getName());
        }
        dto.setYear(quota.getYear());
        dto.setQuotaDays(quota.getQuotaDays());
        return dto;
    }
}
