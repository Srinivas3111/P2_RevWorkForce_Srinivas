package com.rev.app.service;

import com.rev.app.dto.EmployeeNotificationDTO;
import com.rev.app.entity.Employee;

import java.util.List;

public interface EmployeeNotificationService {
    List<EmployeeNotificationDTO> getRecentNotificationsForEmployee(Long employeeId);

    List<EmployeeNotificationDTO> getAllNotifications(Long employeeId);

    List<EmployeeNotificationDTO> getUnreadNotifications(Long employeeId);

    long getUnreadNotificationCount(Long employeeId);

    void createNotification(Employee employee, String title, String message);

    void createNotificationForRole(String role, String title, String message);

    void createNotifications(List<Employee> employees, String title, String message);

    void createNotificationForRoles(List<String> roles, String title, String message);

    void markNotificationAsRead(Long employeeId, Long notificationId) throws Exception;

    int markAllNotificationsAsRead(Long employeeId);
}
