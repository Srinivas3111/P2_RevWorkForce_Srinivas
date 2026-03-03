package com.rev.app.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "departments")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 120)
    private String name;

    @Column(name = "dept_name", nullable = false, length = 120)
    private String legacyName;

    public Department() {
    }

    public Department(String name) {
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
