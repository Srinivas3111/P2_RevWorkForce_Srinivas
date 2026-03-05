package com.rev.app.mapper;

import com.rev.app.dto.EmployeeNotificationDTO;
import com.rev.app.entity.EmployeeNotification;
import org.springframework.stereotype.Component;

@Component
public class EmployeeNotificationMapper {

    public EmployeeNotificationDTO toDTO(EmployeeNotification notification) {
        if (notification == null) {
            return null;
        }
        EmployeeNotificationDTO dto = new EmployeeNotificationDTO();
        dto.setId(notification.getId());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setCreatedOn(notification.getCreatedOn());
        dto.setRead(notification.isRead());
        return dto;
    }

    public EmployeeNotification toEntity(EmployeeNotificationDTO dto) {
        if (dto == null) {
            return null;
        }
        EmployeeNotification notification = new EmployeeNotification();
        notification.setId(dto.getId());
        notification.setTitle(dto.getTitle());
        notification.setMessage(dto.getMessage());
        notification.setCreatedOn(dto.getCreatedOn());
        notification.setRead(dto.isRead());
        return notification;
    }
}
