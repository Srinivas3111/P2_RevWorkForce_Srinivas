package com.rev.app.service;

import com.rev.app.service.impl.PerformanceReviewServiceImpl;

import com.rev.app.dto.PerformanceReviewDTO;
import com.rev.app.entity.Employee;
import com.rev.app.entity.PerformanceReview;
import com.rev.app.mapper.PerformanceReviewMapper;
import com.rev.app.repository.PerformanceReviewRepository;
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
class PerformanceReviewServiceTest {

        @Mock
        private PerformanceReviewRepository performanceReviewRepository;

        @Mock
        private EmployeeService employeeService;

        @Mock
        private PerformanceReviewMapper performanceReviewMapper;

        @Mock
        private EmployeeNotificationService notificationService;

        @InjectMocks
        private PerformanceReviewServiceImpl performanceReviewService;

        @Test
        void saveEmployeeReviewDraft_createsDraftWhenValid() throws Exception {
                Employee manager = new Employee();
                manager.setId(2L);
                manager.setActive(true);

                Employee employee = new Employee();
                employee.setId(4L);
                employee.setActive(true);
                employee.setManager(manager);

                when(employeeService.getEmployeeById(4L)).thenReturn(employee);
                when(performanceReviewRepository.findByEmployee_IdAndReviewPeriodIgnoreCase(4L, "Q1 2026"))
                                .thenReturn(Optional.empty());
                when(performanceReviewRepository.save(any(PerformanceReview.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));
                when(performanceReviewMapper.toDTO(any(PerformanceReview.class))).thenAnswer(invocation -> {
                        PerformanceReview pr = invocation.getArgument(0);
                        PerformanceReviewDTO dto = new PerformanceReviewDTO();
                        dto.setStatus(pr.getStatus());
                        dto.setReviewPeriod(pr.getReviewPeriod());
                        dto.setSelfRating(pr.getSelfRating());
                        return dto;
                });

                PerformanceReviewDTO saved = performanceReviewService.saveEmployeeReviewDraft(
                                4L,
                                "Q1 2026",
                                "Delivered important outcomes across all assigned projects. (Longer text to pass min length)",
                                "Completed sprint deliverables and resolved high-priority defects. (Longer text)",
                                "Need to improve planning accuracy and reduce rework effort. (Longer text)",
                                4);

                assertEquals("DRAFT", saved.getStatus());
                assertEquals("Q1 2026", saved.getReviewPeriod());
                assertEquals(4, saved.getSelfRating());
                verify(performanceReviewRepository).save(any(PerformanceReview.class));
        }

        @Test
        void saveEmployeeReviewDraft_throwsWhenReviewAlreadySubmitted() {
                Employee manager = new Employee();
                manager.setId(2L);

                Employee employee = new Employee();
                employee.setId(4L);
                employee.setActive(true);
                employee.setManager(manager);

                PerformanceReview existing = new PerformanceReview();
                existing.setId(99L);
                existing.setStatus("SUBMITTED");

                when(employeeService.getEmployeeById(4L)).thenReturn(employee);
                when(performanceReviewRepository.findByEmployee_IdAndReviewPeriodIgnoreCase(4L, "Q1 2026"))
                                .thenReturn(Optional.of(existing));

                Exception ex = assertThrows(Exception.class, () -> performanceReviewService.saveEmployeeReviewDraft(
                                4L,
                                "Q1 2026",
                                "Delivered important outcomes across all assigned projects. (Longer text)",
                                "Completed sprint deliverables and resolved high-priority defects. (Longer text)",
                                "Need to improve planning accuracy and reduce rework effort. (Longer text)",
                                4));

                assertTrue(ex.getMessage().contains("already submitted"));
        }

        @Test
        void submitEmployeeReview_throwsWhenDraftNotFound() {
                Employee manager = new Employee();
                manager.setId(2L);

                Employee employee = new Employee();
                employee.setId(4L);
                employee.setActive(true);
                employee.setManager(manager);

                when(employeeService.getEmployeeById(4L)).thenReturn(employee);
                when(performanceReviewRepository.findByEmployee_IdAndReviewPeriodIgnoreCase(4L, "Q1 2026"))
                                .thenReturn(Optional.empty());

                Exception ex = assertThrows(Exception.class, () -> performanceReviewService.submitEmployeeReview(
                                4L,
                                "Q1 2026",
                                "Delivered important outcomes across all assigned projects. (Longer text)",
                                "Completed sprint deliverables and resolved high-priority defects. (Longer text)",
                                "Need to improve planning accuracy and reduce rework effort. (Longer text)",
                                4));

                assertTrue(ex.getMessage().contains("Draft review not found"));
        }

        @Test
        void submitEmployeeReview_submitsDraftWhenValid() throws Exception {
                Employee manager = new Employee();
                manager.setId(2L);

                Employee employee = new Employee();
                employee.setId(4L);
                employee.setActive(true);
                employee.setManager(manager);

                PerformanceReview existingDraft = new PerformanceReview();
                existingDraft.setStatus("DRAFT");
                existingDraft.setEmployee(employee);
                existingDraft.setManager(manager);

                when(employeeService.getEmployeeById(4L)).thenReturn(employee);
                when(performanceReviewRepository.findByEmployee_IdAndReviewPeriodIgnoreCase(4L, "Q1 2026"))
                                .thenReturn(Optional.of(existingDraft));
                when(performanceReviewRepository.save(any(PerformanceReview.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));
                when(performanceReviewMapper.toDTO(any(PerformanceReview.class))).thenAnswer(invocation -> {
                        PerformanceReview pr = invocation.getArgument(0);
                        PerformanceReviewDTO dto = new PerformanceReviewDTO();
                        dto.setStatus(pr.getStatus());
                        dto.setSelfRating(pr.getSelfRating());
                        return dto;
                });

                PerformanceReviewDTO submitted = performanceReviewService.submitEmployeeReview(
                                4L,
                                "Q1 2026",
                                "Delivered important outcomes across all assigned projects. (Longer text)",
                                "Completed sprint deliverables and resolved high-priority defects. (Longer text)",
                                "Need to improve planning accuracy and reduce rework effort. (Longer text)",
                                5);

                assertEquals("SUBMITTED", submitted.getStatus());
                assertEquals(5, submitted.getSelfRating());
                verify(performanceReviewRepository).save(existingDraft);
        }

        @Test
        void submitEmployeeReview_throwsWhenAlreadySubmitted() {
                Employee manager = new Employee();
                manager.setId(2L);

                Employee employee = new Employee();
                employee.setId(4L);
                employee.setActive(true);
                employee.setManager(manager);

                PerformanceReview existing = new PerformanceReview();
                existing.setId(99L);
                existing.setStatus("SUBMITTED");

                when(employeeService.getEmployeeById(4L)).thenReturn(employee);
                when(performanceReviewRepository.findByEmployee_IdAndReviewPeriodIgnoreCase(4L, "Q1 2026"))
                                .thenReturn(Optional.of(existing));

                Exception ex = assertThrows(Exception.class, () -> performanceReviewService.submitEmployeeReview(
                                4L,
                                "Q1 2026",
                                "Delivered important outcomes across all assigned projects. (Longer text)",
                                "Completed sprint deliverables and resolved high-priority defects. (Longer text)",
                                "Need to improve planning accuracy and reduce rework effort. (Longer text)",
                                4));

                assertTrue(ex.getMessage().contains("already been submitted"));
        }

        @Test
        void getManagerTeamPerformanceReviews_filtersOutDraftReviews() {
                PerformanceReview draft = new PerformanceReview();
                draft.setStatus("DRAFT");

                PerformanceReview submitted = new PerformanceReview();
                submitted.setStatus("SUBMITTED");

                PerformanceReview reviewed = new PerformanceReview();
                reviewed.setStatus("REVIEWED");

                when(performanceReviewRepository.findByManager_IdAndStatusNotIgnoreCaseOrderBySubmittedOnDesc(2L,
                                "DRAFT"))
                                .thenReturn(List.of(submitted, reviewed));
                when(performanceReviewMapper.toDTO(submitted)).thenAnswer(inv -> {
                        PerformanceReviewDTO d = new PerformanceReviewDTO();
                        d.setStatus(submitted.getStatus());
                        return d;
                });
                when(performanceReviewMapper.toDTO(reviewed)).thenAnswer(inv -> {
                        PerformanceReviewDTO d = new PerformanceReviewDTO();
                        d.setStatus(reviewed.getStatus());
                        return d;
                });

                List<PerformanceReviewDTO> result = performanceReviewService.getManagerTeamPerformanceReviews(2L);

                assertEquals(2, result.size());
                assertEquals("SUBMITTED", result.get(0).getStatus());
                assertEquals("REVIEWED", result.get(1).getStatus());
        }

        @Test
        void getReviewForManager_throwsWhenReviewStillDraft() {
                Employee manager = new Employee();
                manager.setId(2L);

                PerformanceReview review = new PerformanceReview();
                review.setStatus("DRAFT");
                review.setManager(manager);

                when(performanceReviewRepository.findById(11L))
                                .thenReturn(Optional.of(review));

                Exception ex = assertThrows(Exception.class,
                                () -> performanceReviewService.getReviewForManager(2L, 11L));

                assertTrue(ex.getMessage().contains("still draft"));
        }

        @Test
        void getEmployeePerformanceReviews_returnsOwnReviewsOnly() {
                PerformanceReview r1 = new PerformanceReview();
                r1.setStatus("REVIEWED");

                PerformanceReview r2 = new PerformanceReview();
                r2.setStatus("SUBMITTED");

                when(performanceReviewRepository.findByEmployee_IdOrderBySubmittedOnDesc(4L))
                                .thenReturn(List.of(r1, r2));
                when(performanceReviewMapper.toDTO(r1)).thenAnswer(inv -> {
                        PerformanceReviewDTO d = new PerformanceReviewDTO();
                        d.setStatus(r1.getStatus());
                        return d;
                });
                when(performanceReviewMapper.toDTO(r2)).thenAnswer(inv -> {
                        PerformanceReviewDTO d = new PerformanceReviewDTO();
                        d.setStatus(r2.getStatus());
                        return d;
                });

                List<PerformanceReviewDTO> result = performanceReviewService.getEmployeePerformanceReviews(4L);

                assertEquals(2, result.size());
                assertEquals("REVIEWED", result.get(0).getStatus());
                assertEquals("SUBMITTED", result.get(1).getStatus());
        }
}


