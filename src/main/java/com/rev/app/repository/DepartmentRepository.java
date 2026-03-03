package com.rev.app.repository;

import com.rev.app.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    @Query("""
            select d
            from Department d
            where lower(trim(coalesce(d.name, d.legacyName))) = lower(trim(:name))
            """)
    Optional<Department> findByNameIgnoreCase(@Param("name") String name);

    @Query("""
            select d
            from Department d
            order by lower(trim(coalesce(d.name, d.legacyName))) asc
            """)
    List<Department> findAllByOrderByNameAsc();
}
