package com.rev.app.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "designations")
public class Designation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "desig_name", nullable = false, length = 120)
    private String legacyName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id")
    private Department department;

    public Designation() {
    }

    public Designation(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.legacyName = name;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    @PrePersist
    @PreUpdate
    private void syncColumnValues() {
        if (name == null || name.trim().isEmpty()) {
            name = (legacyName == null) ? null : legacyName.trim();
        }
        if (name != null) {
            name = name.trim();
        }
        legacyName = name;
    }

    @PostLoad
    private void syncAfterLoad() {
        if ((name == null || name.trim().isEmpty()) && legacyName != null && !legacyName.trim().isEmpty()) {
            name = legacyName.trim();
        }
        if ((legacyName == null || legacyName.trim().isEmpty()) && name != null && !name.trim().isEmpty()) {
            legacyName = name.trim();
        }
    }
}
