package com.rev.app.rest;

import com.rev.app.dto.ApiResponse;
import com.rev.app.dto.DesignationDTO;
import com.rev.app.service.DesignationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/designations")
public class RestDesignationController {

    @Autowired
    private DesignationService designationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<DesignationDTO>>> getAllDesignations() {
        List<DesignationDTO> designations = designationService.getAllDesignations();
        return ResponseEntity.ok(ApiResponse.success("Successfully retrieved all designations", designations));
    }

    @GetMapping("/department/{deptName}")
    public ResponseEntity<ApiResponse<List<DesignationDTO>>> getDesignationsByDepartment(
            @PathVariable String deptName) {
        List<DesignationDTO> designations = designationService.getDesignationsByDepartment(deptName);
        return ResponseEntity.ok(
                ApiResponse.success("Successfully retrieved designations for department: " + deptName, designations));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DesignationDTO>> createDesignation(@RequestParam String name,
            @RequestParam(required = false) Long departmentId) {
        try {
            DesignationDTO designation = designationService.createDesignation(name, departmentId);
            return ResponseEntity.ok(ApiResponse.success("Successfully created designation", designation));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDesignation(@PathVariable Long id) {
        try {
            designationService.deleteDesignation(id);
            return ResponseEntity.ok(ApiResponse.success("Successfully deleted designation", null));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(ApiResponse.error(e.getMessage()));
        }
    }
}
