package com.rev.app.repository;

import com.rev.app.entity.Department;
import com.rev.app.entity.Designation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DesignationRepositoryTest {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DesignationRepository designationRepository;

    @Test
    void findByDepartmentNameIgnoreCaseOrderByNameAsc_filtersAndSortsByDepartment() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);

        Department engineering = new Department();
        engineering.setName("repo-eng-" + suffix);
        Department hr = new Department();
        hr.setName("repo-hr-" + suffix);
        departmentRepository.saveAll(List.of(engineering, hr));
        departmentRepository.flush();

        Designation d1 = new Designation("repo-b-" + suffix);
        d1.setDepartment(engineering);
        Designation d2 = new Designation("repo-a-" + suffix);
        d2.setDepartment(engineering);
        Designation d3 = new Designation("repo-c-" + suffix);
        d3.setDepartment(hr);
        designationRepository.saveAll(List.of(d1, d2, d3));
        designationRepository.flush();

        List<Designation> result =
                designationRepository.findByDepartmentNameIgnoreCaseOrderByNameAsc("  repo-eng-" + suffix + " ");
        List<String> names = result.stream().map(Designation::getName).toList();

        assertEquals(List.of("repo-a-" + suffix, "repo-b-" + suffix), names);
    }

    @Test
    void findByNameIgnoreCaseAndDepartmentId_matchesScopedDesignation() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);

        Department engineering = new Department();
        engineering.setName("repo-eng-" + suffix);
        departmentRepository.saveAndFlush(engineering);

        Designation designation = new Designation("repo-dev-" + suffix);
        designation.setDepartment(engineering);
        designationRepository.saveAndFlush(designation);

        Optional<Designation> found =
                designationRepository.findByNameIgnoreCaseAndDepartmentId("REPO-DEV-" + suffix, engineering.getId());

        assertTrue(found.isPresent());
        assertEquals("repo-dev-" + suffix, found.get().getName());
    }
}
