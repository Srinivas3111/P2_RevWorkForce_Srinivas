package com.rev.app.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "leave_types", uniqueConstraints = {
        @UniqueConstraint(columnNames = "leave_name")
})
public class LeaveType {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "leave_type_seq")
    @SequenceGenerator(name = "leave_type_seq", sequenceName = "leave_type_seq", allocationSize = 1)
    private Long id;

    @Column(name = "leave_name", nullable = false, unique = true, length = 80)
    private String name;

    @Column(length = 200)
    private String description;

    @Column(nullable = false)
    private boolean active = true;

    public LeaveType() {
    }

    public LeaveType(String name, String description, boolean active) {
        this.name = name;
        this.description = description;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
