package com.rev.app.service.impl;

import com.rev.app.dto.EmployeeDTO;
import com.rev.app.entity.SystemActivityLog;
import com.rev.app.repository.SystemActivityLogRepository;
import com.rev.app.service.SystemActivityLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

@Service
public class SystemActivityLogServiceImpl implements SystemActivityLogService {

    private static final Comparator<String> CASE_INSENSITIVE = Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER);
    private static final Comparator<LocalDateTime> DATE_TIME_COMPARATOR = Comparator
            .nullsLast(Comparator.naturalOrder());

    @Autowired
    private SystemActivityLogRepository systemActivityLogRepository;

    @Override
    public List<SystemActivityLog> getRecentLogs(int maxCount) {
        List<SystemActivityLog> allLogs = systemActivityLogRepository.findAllByOrderByCreatedOnDesc();
        if (maxCount <= 0 || allLogs.isEmpty()) {
            return List.of();
        }
        if (allLogs.size() <= maxCount) {
            return allLogs;
        }
        return new ArrayList<>(allLogs.subList(0, maxCount));
    }

    @Override
    public void logActivity(EmployeeDTO actor, String moduleName, String actionName, String details) {
        logActivity(actor, moduleName, actionName, details, null, null);
    }

    @Override
    public void logActivity(EmployeeDTO actor,
            String moduleName,
            String actionName,
            String details,
            Long targetEmployeeId,
            String targetEmployeeName) {
        SystemActivityLog log = new SystemActivityLog();
        log.setModuleName(normalizeRequired(moduleName, 80, "General"));
        log.setActionName(normalizeRequired(actionName, 120, "Action"));
        log.setDetails(normalizeOptional(details, 2000));
        log.setTargetEmployeeId(targetEmployeeId);
        log.setTargetEmployeeName(normalizeOptional(targetEmployeeName, 160));

        if (actor != null) {
            log.setActorEmployeeId(actor.getId());
            log.setActorEmail(normalizeOptional(actor.getEmail(), 160));
            log.setActorRole(normalizeOptional(actor.getRole(), 40));
            log.setActorName(actor.getName());
        } else {
            log.setActorName("System");
            log.setActorRole("SYSTEM");
        }

        systemActivityLogRepository.save(log);
    }

    @Override
    public List<SystemActivityLog> searchLogs(String action,
            String performedBy,
            String role,
            String employee,
            String sortBy,
            String sortDir,
            int maxCount) {
        if (maxCount == 0) {
            return List.of();
        }

        Stream<SystemActivityLog> stream = systemActivityLogRepository.findAll().stream();
        if (hasText(action)) {
            stream = stream.filter(log -> containsIgnoreCase(log.getActionName(), action));
        }
        if (hasText(performedBy)) {
            stream = stream.filter(log -> containsIgnoreCase(log.getActorName(), performedBy));
        }
        if (hasText(role)) {
            stream = stream.filter(log -> equalsIgnoreCase(log.getActorRole(), role));
        }
        if (hasText(employee)) {
            stream = stream.filter(log -> containsIgnoreCase(log.getTargetEmployeeName(), employee));
        }

        Comparator<SystemActivityLog> comparator = resolveComparator(sortBy);
        if (!"asc".equalsIgnoreCase(sortDir)) {
            comparator = comparator.reversed();
        }

        List<SystemActivityLog> filtered = stream.sorted(comparator).toList();
        if (maxCount > 0 && filtered.size() > maxCount) {
            return new ArrayList<>(filtered.subList(0, maxCount));
        }
        return filtered;
    }

    private Comparator<SystemActivityLog> resolveComparator(String sortBy) {
        String normalizedSortBy = hasText(sortBy) ? sortBy.trim().toLowerCase(Locale.ROOT) : "timestamp";
        return switch (normalizedSortBy) {
            case "action" -> Comparator.comparing(SystemActivityLog::getActionName, CASE_INSENSITIVE)
                    .thenComparing(SystemActivityLog::getCreatedOn, DATE_TIME_COMPARATOR);
            case "performed_by", "performedby" ->
                Comparator.comparing(SystemActivityLog::getActorName, CASE_INSENSITIVE)
                        .thenComparing(SystemActivityLog::getCreatedOn, DATE_TIME_COMPARATOR);
            case "role" -> Comparator.comparing(SystemActivityLog::getActorRole, CASE_INSENSITIVE)
                    .thenComparing(SystemActivityLog::getCreatedOn, DATE_TIME_COMPARATOR);
            case "employee" -> Comparator.comparing(SystemActivityLog::getTargetEmployeeName, CASE_INSENSITIVE)
                    .thenComparing(SystemActivityLog::getCreatedOn, DATE_TIME_COMPARATOR);
            case "module" -> Comparator.comparing(SystemActivityLog::getModuleName, CASE_INSENSITIVE)
                    .thenComparing(SystemActivityLog::getCreatedOn, DATE_TIME_COMPARATOR);
            case "timestamp", "created_on" ->
                Comparator.comparing(SystemActivityLog::getCreatedOn, DATE_TIME_COMPARATOR);
            default -> Comparator.comparing(SystemActivityLog::getCreatedOn, DATE_TIME_COMPARATOR);
        };
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private boolean containsIgnoreCase(String value, String query) {
        if (!hasText(query)) {
            return true;
        }
        if (!hasText(value)) {
            return false;
        }
        return value.toLowerCase(Locale.ROOT).contains(query.trim().toLowerCase(Locale.ROOT));
    }

    private boolean equalsIgnoreCase(String value, String query) {
        if (!hasText(query)) {
            return true;
        }
        if (!hasText(value)) {
            return false;
        }
        return value.trim().equalsIgnoreCase(query.trim());
    }

    private String normalizeRequired(String value, int maxLength, String defaultValue) {
        String clean = normalizeOptional(value, maxLength);
        if (clean == null || clean.isEmpty()) {
            return defaultValue;
        }
        return clean;
    }

    private String normalizeOptional(String value, int maxLength) {
        if (value == null) {
            return null;
        }
        String clean = value.trim();
        if (clean.length() <= maxLength) {
            return clean;
        }
        return clean.substring(0, maxLength);
    }
}
