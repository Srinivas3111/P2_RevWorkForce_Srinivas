package com.rev.app.repository;

import com.rev.app.entity.Department;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DepartmentRepositoryTest {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Test
    void findByNameIgnoreCase_matchesIgnoringCaseAndSpaces() {
        String departmentName = "repo-dept-" + UUID.randomUUID().toString().substring(0, 8);
        Department department = new Department();
        department.setName(departmentName);
        departmentRepository.saveAndFlush(department);

        Optional<Department> found =
                departmentRepository.findByNameIgnoreCase("  " + departmentName.toUpperCase() + " ");

        assertTrue(found.isPresent());
        assertEquals(departmentName, found.get().getName());
    }

    @Test
    void findAllByOrderByNameAsc_returnsCaseInsensitiveSortedDepartments() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        String nameA = "repo-aa-" + suffix;
        String nameB = "repo-mm-" + suffix;
        String nameC = "repo-zz-" + suffix;

        Department d1 = new Department();
        d1.setName(nameC);
        Department d2 = new Department();
        d2.setName(nameB);
        Department d3 = new Department();
        d3.setName(nameA);
        departmentRepository.saveAll(List.of(d1, d2, d3));
        departmentRepository.flush();

        List<Department> result = departmentRepository.findAllByOrderByNameAsc();
        List<String> names = result.stream().map(Department::getName).toList();

        int indexA = names.indexOf(nameA);
        int indexB = names.indexOf(nameB);
        int indexC = names.indexOf(nameC);
        assertTrue(indexA >= 0);
        assertTrue(indexB >= 0);
        assertTrue(indexC >= 0);
        assertTrue(indexA < indexB);
        assertTrue(indexB < indexC);
    }
}
