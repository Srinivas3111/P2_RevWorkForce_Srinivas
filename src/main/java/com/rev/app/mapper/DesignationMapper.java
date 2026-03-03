package com.rev.app.mapper;

import com.rev.app.dto.DesignationDTO;
import com.rev.app.entity.Designation;
import org.springframework.stereotype.Component;

@Component
public class DesignationMapper {

    public DesignationDTO toDTO(Designation designation) {
        if (designation == null)
            return null;
        DesignationDTO dto = new DesignationDTO();
        dto.setId(designation.getId());
        dto.setName(designation.getName());
<<<<<<< HEAD
        if (designation.getDepartment() != null) {
            dto.setDepartmentId(designation.getDepartment().getId());
            dto.setDepartmentName(designation.getDepartment().getName());
        }
=======
>>>>>>> b09ad693854b4496e321429ab9250ea0c6c408cf
        return dto;
    }

    public Designation toEntity(DesignationDTO dto) {
        if (dto == null)
            return null;
        Designation designation = new Designation();
        designation.setName(dto.getName());
        return designation;
    }
}
