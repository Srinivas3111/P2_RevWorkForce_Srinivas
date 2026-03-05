package com.rev.app.repository;

import com.rev.app.entity.Designation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DesignationRepository extends JpaRepository<Designation, Long> {
    @Query("""
            select d
            from Designation d
            where lower(trim(coalesce(d.name, d.legacyName))) = lower(trim(:name))
            """)
    Optional<Designation> findByNameIgnoreCase(@Param("name") String name);

    @Query("""
            select d
            from Designation d
            left join d.department dep
            where lower(trim(coalesce(dep.name, dep.legacyName))) = lower(trim(:departmentName))
            order by lower(trim(coalesce(d.name, d.legacyName))) asc
            """)
    List<Designation> findByDepartmentNameIgnoreCaseOrderByNameAsc(@Param("departmentName") String departmentName);

    @Query("""
            select d
            from Designation d
            order by lower(trim(coalesce(d.name, d.legacyName))) asc
            """)
    List<Designation> findAllByOrderByNameAsc();

    @Query("""
            select d
            from Designation d
            where lower(trim(coalesce(d.name, d.legacyName))) = lower(trim(:name))
              and d.department.id = :departmentId
            """)
    Optional<Designation> findByNameIgnoreCaseAndDepartmentId(@Param("name") String name,
            @Param("departmentId") Long departmentId);
}
