package com.rev.app.service;

import com.rev.app.dto.EmployeeDTO;
import com.rev.app.entity.SystemActivityLog;

import java.util.List;

public interface SystemActivityLogService {
    List<SystemActivityLog> getRecentLogs(int maxCount);

    void logActivity(EmployeeDTO actor, String moduleName, String actionName, String details);

    void logActivity(EmployeeDTO actor,
                     String moduleName,
                     String actionName,
                     String details,
                     Long targetEmployeeId,
                     String targetEmployeeName);

    List<SystemActivityLog> searchLogs(String action,
                                       String performedBy,
                                       String role,
                                       String employee,
                                       String sortBy,
                                       String sortDir,
                                       int maxCount);
}
