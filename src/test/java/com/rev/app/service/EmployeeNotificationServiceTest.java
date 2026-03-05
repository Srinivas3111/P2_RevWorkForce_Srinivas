package com.rev.app.service;

import com.rev.app.service.impl.EmployeeNotificationServiceImpl;

import com.rev.app.entity.Employee;
import com.rev.app.entity.EmployeeNotification;
import com.rev.app.repository.EmployeeNotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeNotificationServiceTest {

    @Mock
    private EmployeeNotificationRepository employeeNotificationRepository;

    @InjectMocks
    private EmployeeNotificationServiceImpl employeeNotificationService;

    @Test
    void markNotificationAsRead_updatesUnreadNotification() throws Exception {
        EmployeeNotification notification = new EmployeeNotification();
        notification.setRead(false);

        when(employeeNotificationRepository.findByIdAndEmployee_Id(21L, 4L))
                .thenReturn(Optional.of(notification));
        when(employeeNotificationRepository.save(any(EmployeeNotification.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        employeeNotificationService.markNotificationAsRead(4L, 21L);

        assertTrue(notification.isRead());
        verify(employeeNotificationRepository).save(notification);
    }

    @Test
    void markNotificationAsRead_throwsWhenNotificationNotFoundForEmployee() {
        when(employeeNotificationRepository.findByIdAndEmployee_Id(99L, 4L))
                .thenReturn(Optional.empty());

        Exception ex = assertThrows(Exception.class,
                () -> employeeNotificationService.markNotificationAsRead(4L, 99L));

        assertTrue(ex.getMessage().contains("Notification not found"));
    }

    @Test
    void markAllNotificationsAsRead_marksAllUnreadForEmployee() {
        EmployeeNotification n1 = new EmployeeNotification();
        n1.setRead(false);
        EmployeeNotification n2 = new EmployeeNotification();
        n2.setRead(false);

        when(employeeNotificationRepository.findByEmployee_IdAndReadFalseOrderByCreatedOnDesc(4L))
                .thenReturn(List.of(n1, n2));
        when(employeeNotificationRepository.saveAll(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        int count = employeeNotificationService.markAllNotificationsAsRead(4L);

        assertEquals(2, count);
        assertTrue(n1.isRead());
        assertTrue(n2.isRead());
        verify(employeeNotificationRepository).saveAll(List.of(n1, n2));
    }

    @Test
    void createNotification_setsUnreadAndSaves() {
        Employee employee = new Employee();
        employee.setId(4L);

        employeeNotificationService.createNotification(employee, "Leave Update", "Approved");

        verify(employeeNotificationRepository).save(any(EmployeeNotification.class));
    }
}


