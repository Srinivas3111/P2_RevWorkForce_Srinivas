package com.rev.app.mapper;

import com.rev.app.dto.LeaveTypeDTO;
import com.rev.app.entity.LeaveType;
import org.springframework.stereotype.Component;

@Component
public class LeaveTypeMapper {

    public LeaveTypeDTO toDTO(LeaveType leaveType) {
        if (leaveType == null)
            return null;
        LeaveTypeDTO dto = new LeaveTypeDTO();
        dto.setId(leaveType.getId());
        dto.setName(leaveType.getName());
        dto.setDescription(leaveType.getDescription());
        dto.setActive(leaveType.isActive());
        return dto;
    }

    public LeaveType toEntity(LeaveTypeDTO dto) {
        if (dto == null)
            return null;
        LeaveType leaveType = new LeaveType();
        leaveType.setId(dto.getId());
        leaveType.setName(dto.getName());
        leaveType.setDescription(dto.getDescription());
        leaveType.setActive(dto.isActive());
        return leaveType;
    }
}
