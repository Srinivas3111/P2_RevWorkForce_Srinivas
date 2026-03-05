package com.rev.app.service;

import com.rev.app.dto.DesignationDTO;

import java.util.List;

public interface DesignationService {
    List<DesignationDTO> getAllDesignations();

    List<DesignationDTO> getDesignationsByDepartment(String departmentName);

    long getEmployeeCountByDesignation(String designationName);

    DesignationDTO createDesignation(String designationName, Long departmentId) throws Exception;

    DesignationDTO updateDesignation(Long designationId, String newDesignationName, Long departmentId) throws Exception;

    void deleteDesignation(Long designationId) throws Exception;
}
