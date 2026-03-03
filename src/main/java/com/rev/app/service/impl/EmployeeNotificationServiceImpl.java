package com.rev.app.service.impl;

import com.rev.app.service.*;

import com.rev.app.dto.EmployeeNotificationDTO;
import com.rev.app.entity.Employee;
import com.rev.app.entity.EmployeeNotification;
import com.rev.app.mapper.EmployeeNotificationMapper;
import com.rev.app.repository.EmployeeNotificationRepository;
import com.rev.app.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

@Service
public class EmployeeNotificationServiceImpl implements EmployeeNotificationService {

    @Autowired
    private EmployeeNotificationRepository employeeNotificationRepository;

    @Autowired
    private EmployeeNotificationMapper employeeNotificationMapper;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public List<EmployeeNotificationDTO> getRecentNotificationsForEmployee(Long employeeId) {
        if (employeeId == null) {
            return List.of();
        }
        return employeeNotificationRepository.findTop10ByEmployee_IdOrderByCreatedOnDesc(employeeId)
                .stream()
                .map(employeeNotificationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeNotificationDTO> getAllNotifications(Long employeeId) {
        if (employeeId == null) {
            return List.of();
        }
        return employeeNotificationRepository.findByEmployee_IdOrderByCreatedOnDesc(employeeId)
                .stream()
                .map(employeeNotificationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeNotificationDTO> getUnreadNotifications(Long employeeId) {
        if (employeeId == null) {
            return List.of();
        }
        return employeeNotificationRepository.findByEmployee_IdAndReadFalseOrderByCreatedOnDesc(employeeId)
                .stream()
                .map(employeeNotificationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public long getUnreadNotificationCount(Long employeeId) {
        if (employeeId == null) {
            return 0L;
        }
        return employeeNotificationRepository.countByEmployee_IdAndReadFalse(employeeId);
    }

    @Override
    public void createNotification(Employee employee, String title, String message) {
        if (employee == null || employee.getId() == null) {
            return;
        }

        EmployeeNotification notification = new EmployeeNotification();
        notification.setEmployee(employee);
        notification.setTitle(normalizeRequired(title, 150, "Notification"));
        notification.setMessage(normalizeRequired(message, 1000, "You have a new update."));
        notification.setRead(false);
        employeeNotificationRepository.save(notification);
    }

    @Override
    public void createNotificationForRole(String role, String title, String message) {
        if (role == null || role.trim().isEmpty()) {
            return;
        }
        List<Employee> recipients = employeeRepository.findByRoleIgnoreCase(role.trim());
        createNotifications(recipients, title, message);
    }

    @Override
    public void createNotifications(List<Employee> employees, String title, String message) {
        if (employees == null || employees.isEmpty()) {
            return;
        }

        // Avoid duplicate notifications for the same employee ID when recipients overlap.
        Map<Long, Employee> uniqueRecipients = new LinkedHashMap<>();
        for (Employee employee : employees) {
            if (employee == null || employee.getId() == null || !employee.isActive()) {
                continue;
            }
            uniqueRecipients.put(employee.getId(), employee);
        }

        for (Employee recipient : uniqueRecipients.values()) {
            createNotification(recipient, title, message);
        }
    }

    @Override
    public void createNotificationForRoles(List<String> roles, String title, String message) {
        if (roles == null || roles.isEmpty()) {
            return;
        }

        LinkedHashSet<String> uniqueRoles = roles.stream()
                .filter(r -> r != null && !r.trim().isEmpty())
                .map(String::trim)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        for (String role : uniqueRoles) {
            createNotificationForRole(role, title, message);
        }
    }

    @Override
    public void markNotificationAsRead(Long employeeId, Long notificationId) throws Exception {
        if (employeeId == null) {
            throw new Exception("Employee identity is required.");
        }
        if (notificationId == null) {
            throw new Exception("Notification ID is required.");
        }

        EmployeeNotification notification = employeeNotificationRepository
                .findByIdAndEmployee_Id(notificationId, employeeId)
                .orElseThrow(() -> new Exception("Notification not found for this employee."));

        if (!notification.isRead()) {
            notification.setRead(true);
            employeeNotificationRepository.save(notification);
        }
    }

    @Override
    public int markAllNotificationsAsRead(Long employeeId) {
        if (employeeId == null) {
            return 0;
        }

        List<EmployeeNotification> unreadNotifications = employeeNotificationRepository
                .findByEmployee_IdAndReadFalseOrderByCreatedOnDesc(employeeId);
        for (EmployeeNotification notification : unreadNotifications) {
            notification.setRead(true);
        }
        if (!unreadNotifications.isEmpty()) {
            employeeNotificationRepository.saveAll(unreadNotifications);
        }
        return unreadNotifications.size();
    }

    private String normalizeRequired(String value, int maxLength, String defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        String clean = value.trim();
        if (clean.length() <= maxLength) {
            return clean;
        }
        return clean.substring(0, maxLength);
    }
}



